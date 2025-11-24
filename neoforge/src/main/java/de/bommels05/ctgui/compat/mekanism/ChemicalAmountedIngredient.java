package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

import java.util.List;

public class ChemicalAmountedIngredient extends SpecialAmountedIngredient<ChemicalStack, Chemical> {

    protected ChemicalAmountedIngredient(ChemicalStack stack, TagKey<Chemical> tag, int amount) {
        super(stack == null || stack.getAmount() == amount ? stack : stack.copyWithAmount(amount), tag, amount);
    }

    public ChemicalAmountedIngredient(ChemicalStack stack, int amount) {
        this(stack, null, amount);
    }

    public ChemicalAmountedIngredient(ChemicalStack stack) {
        this(stack, (int) stack.getAmount());
    }

    public ChemicalAmountedIngredient(TagKey<Chemical> tag, int amount) {
        super(null, tag, amount);
    }

    public boolean shouldChangeAmount(ChemicalAmountedIngredient other) {
        return (this.isStack() && other.isStack() && this.getStack().getChemical() == other.getStack().getChemical()) ||
                (this.isTag() && other.isTag() && this.getTag().equals(other.getTag()));
    }

    @Override
    public ChemicalAmountedIngredient withAmount(int amount) {
        return new ChemicalAmountedIngredient(getStack(), getTag(), amount);
    }

    @Override
    public List<ChemicalStack> getStacks() {
        if (isTag()) {
            return getStacksInternal().stream().map(stack -> stack.copyWithAmount(getAmount())).toList();
        }
        return getStacksInternal();
    }

    @Override
    public Chemical getStackAsType() {
        return getStack().getChemical();
    }

    @Override
    public Registry<Chemical> getRegistry() {
        return MekanismAPI.CHEMICAL_REGISTRY;
    }

    @Override
    public boolean isStackEmpty() {
        return getStack().isEmpty();
    }
}
