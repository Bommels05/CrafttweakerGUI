package de.bommels05.ctgui.api;

import com.google.common.base.Preconditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class FluidAmountedIngredient extends SpecialAmountedIngredient<FluidStack, Fluid> {

    protected FluidAmountedIngredient(FluidStack stack, TagKey<Fluid> tag, int amount) {
        super(stack, tag, amount);
    }

    public FluidAmountedIngredient(FluidStack stack, int amount) {
        super(stack, amount);
    }

    public FluidAmountedIngredient(FluidStack stack) {
        super(stack);
    }

    public FluidAmountedIngredient(TagKey<Fluid> tag, int amount) {
        super(tag, amount);
    }

    public boolean shouldChangeAmount(FluidAmountedIngredient other) {
        return (this.isStack() && other.isStack() && this.getStack().getFluid() == other.getStack().getFluid()) ||
                (this.isTag() && other.isTag() && this.getTag().equals(other.getTag()));
    }

    public int getRightAmount() {
        return shouldUseAmount() ? getAmount() : getStack().getAmount();
    }

    @Override
    public FluidAmountedIngredient withAmount(int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        return new FluidAmountedIngredient(getStack(), getTag(), amount);
    }

    @Override
    public List<FluidStack> getStacks() {
        if (shouldUseAmount()) {
            return getStacksInternal().stream().map(stack -> stack.copyWithAmount(getAmount())).toList();
        }
        return getStacksInternal();
    }

    @Override
    public Fluid getStackAsType() {
        return getStack().getFluid();
    }

    @Override
    public Registry<Fluid> getRegistry() {
        return BuiltInRegistries.FLUID;
    }

    @Override
    public boolean isStackEmpty() {
        return getStack().isEmpty();
    }
}
