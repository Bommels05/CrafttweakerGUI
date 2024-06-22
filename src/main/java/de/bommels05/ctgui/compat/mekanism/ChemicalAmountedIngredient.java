package de.bommels05.ctgui.compat.mekanism;

import com.google.common.base.Preconditions;
import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.tags.TagKey;

public class ChemicalAmountedIngredient<S extends ChemicalStack<T>, T extends Chemical<T>> extends SpecialAmountedIngredient<S, T> {

    protected ChemicalAmountedIngredient(S stack, TagKey<T> tag, int amount) {
        super(stack, tag, amount);
    }

    public ChemicalAmountedIngredient(S stack, int amount) {
        super(stack, amount);
    }

    public ChemicalAmountedIngredient(S stack) {
        super(stack);
    }

    public ChemicalAmountedIngredient(TagKey<T> tag, int amount) {
        super(tag, amount);
    }

    public boolean shouldChangeAmount(ChemicalAmountedIngredient<S, T> other) {
        return (this.isStack() && other.isStack() && this.getStack().getType() == other.getStack().getType()) ||
                (this.isTag() && other.isTag() && this.getTag().equals(other.getTag()));
    }

    public int getRightAmount() {
        return shouldUseAmount() ? getAmount() : (int) getStack().getAmount();
    }

    @Override
    public ChemicalAmountedIngredient<S, T> withAmount(int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        return new ChemicalAmountedIngredient<>(getStack(), getTag(), amount);
    }

    @Override
    @SuppressWarnings("unchecked")
    public S toStack() {
        ChemicalStack<T> withAmount = super.toStack().copy();
        withAmount.setAmount(getRightAmount());
        return (S) withAmount;
    }

    @Override
    public boolean isStackEmpty() {
        return getStack().isEmpty();
    }
}
