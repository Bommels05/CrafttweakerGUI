package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IChemicalStackIngredientCreator;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.ingredient.chemical.TaggedChemicalStackIngredient;
import mekanism.common.recipe.ingredient.creator.FluidStackIngredientCreator;
import mekanism.common.recipe.ingredient.creator.ItemStackIngredientCreator;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;

public class MekanismRecipeUtils {

    public static AmountedIngredient of(ItemStackIngredient ingredient) {
        if (ingredient instanceof ItemStackIngredientCreator.SingleItemStackIngredient stack) {
            return new AmountedIngredient(stack.getInputRaw(), stack.getAmountRaw());
        }
        return new AmountedIngredient(Ingredient.of(ingredient.getRepresentations().stream()), 1);
    }

    public static ItemStackIngredient of(AmountedIngredient ingredient) {
        return IngredientCreatorAccess.item().from(ingredient.ingredient(), ingredient.amount());
    }

    public static FluidAmountedIngredient of(FluidStackIngredient ingredient) {
        if (ingredient instanceof FluidStackIngredientCreator.TaggedFluidStackIngredient tagged) {
            return new FluidAmountedIngredient(tagged.getTag(), tagged.getRawAmount());
        }
        if (!ingredient.getRepresentations().isEmpty()) {
            return new FluidAmountedIngredient(ingredient.getRepresentations().get(0));
        }
        throw new IllegalStateException("Non tag empty fluid ingredient: " + ingredient);
    }

    public static int getAmount(FluidStackIngredient ingredient) {
        return of(ingredient).getRightAmount();
    }

    public static <S extends ChemicalStack<T>, T extends Chemical<T>> ChemicalAmountedIngredient<S, T> of(ChemicalStackIngredient<T, S> ingredient) {
        if (ingredient instanceof TaggedChemicalStackIngredient<T, S> tagged) {
            return new ChemicalAmountedIngredient<>(tagged.getTag(), (int) tagged.getRawAmount());
        }
        if (!ingredient.getRepresentations().isEmpty()) {
            return new ChemicalAmountedIngredient<>(ingredient.getRepresentations().get(0));
        }
        throw new IllegalStateException("Non tag empty chemical ingredient: " + ingredient);
    }

    public static ChemicalStackIngredient.GasStackIngredient toIngredientGas(ChemicalAmountedIngredient<GasStack, Gas> ingredient) {
        if (ingredient.isStack()) {
            return IngredientCreatorAccess.gas().from(ingredient.shouldUseAmount() ? new GasStack(ingredient.getStack(), ingredient.getAmount()) : ingredient.getStack());
        }
        return IngredientCreatorAccess.gas().from(ingredient.getTag(), ingredient.getAmount());
    }

    public static ChemicalStackIngredient.InfusionStackIngredient toIngredientInfusion(ChemicalAmountedIngredient<InfusionStack, InfuseType> ingredient) {
        if (ingredient.isStack()) {
            return IngredientCreatorAccess.infusion().from(ingredient.shouldUseAmount() ? new InfusionStack(ingredient.getStack(), ingredient.getAmount()) : ingredient.getStack());
        }
        return IngredientCreatorAccess.infusion().from(ingredient.getTag(), ingredient.getAmount());
    }

    public static ChemicalStackIngredient.SlurryStackIngredient toIngredientSlurry(ChemicalAmountedIngredient<SlurryStack, Slurry> ingredient) {
        if (ingredient.isStack()) {
            return IngredientCreatorAccess.slurry().from(ingredient.shouldUseAmount() ? new SlurryStack(ingredient.getStack(), ingredient.getAmount()) : ingredient.getStack());
        }
        return IngredientCreatorAccess.slurry().from(ingredient.getTag(), ingredient.getAmount());
    }

    public static ChemicalStackIngredient.PigmentStackIngredient toIngredientPigment(ChemicalAmountedIngredient<PigmentStack, Pigment> ingredient) {
        if (ingredient.isStack()) {
            return IngredientCreatorAccess.pigment().from(ingredient.shouldUseAmount() ? new PigmentStack(ingredient.getStack(), ingredient.getAmount()) : ingredient.getStack());
        }
        return IngredientCreatorAccess.pigment().from(ingredient.getTag(), ingredient.getAmount());
    }

    @SuppressWarnings("unchecked")
    public static <S extends ChemicalStack<T>, T extends Chemical<T>> ChemicalStackIngredient<T, S> toIngredientChemical(ChemicalAmountedIngredient<S, T> ingredient) {
        if (ingredient.isStack()) {
            S stack = ingredient.getStack();
            if (ingredient.shouldUseAmount()) {
                stack = (S) ingredient.getStack().copy();
                stack.setAmount(ingredient.getAmount());
            }
            return ((IChemicalStackIngredientCreator<T, S, ?>) IngredientCreatorAccess.getCreatorForType(ChemicalType.getTypeFor(ingredient.getStack().getType()))).from(stack);
        }
        return ((IChemicalStackIngredientCreator<T, S, ?>) IngredientCreatorAccess.getCreatorForType(Arrays.stream(ChemicalType.values()).filter(type -> type.getSerializedName().equals(ingredient.getTag().registry().location().getPath())).findFirst().orElseThrow())).from(ingredient.getTag(), ingredient.getAmount());
    }

    public static FluidStackIngredient toIngredientFluid(FluidAmountedIngredient ingredient) {
        if (ingredient.isStack()) {
            return IngredientCreatorAccess.fluid().from(ingredient.shouldUseAmount() ? new FluidStack(ingredient.getStack(), ingredient.getAmount()) : ingredient.getStack());
        }
        return IngredientCreatorAccess.fluid().from(ingredient.getTag(), ingredient.getAmount());
    }

    public static ChemicalStackIngredient.GasStackIngredient toIngredientKeepAmount(ChemicalAmountedIngredient<GasStack, Gas> ingredient, ChemicalStackIngredient.GasStackIngredient old) {
        if (ingredient.shouldChangeAmount(of(old))) {
            return toIngredientGas(ingredient);
        }
        return toIngredientGas(ingredient.withAmount(getAmount(old)));
    }

    public static ChemicalStackIngredient.InfusionStackIngredient toIngredientKeepAmount(ChemicalAmountedIngredient<InfusionStack, InfuseType> ingredient, ChemicalStackIngredient.InfusionStackIngredient old) {
        if (ingredient.shouldChangeAmount(of(old))) {
            return toIngredientInfusion(ingredient);
        }
        return toIngredientInfusion(ingredient.withAmount(getAmount(old)));
    }

    public static ChemicalStackIngredient.SlurryStackIngredient toIngredientKeepAmount(ChemicalAmountedIngredient<SlurryStack, Slurry> ingredient, ChemicalStackIngredient.SlurryStackIngredient old) {
        if (ingredient.shouldChangeAmount(of(old))) {
            return toIngredientSlurry(ingredient);
        }
        return toIngredientSlurry(ingredient.withAmount(getAmount(old)));
    }

    public static ChemicalStackIngredient.PigmentStackIngredient toIngredientKeepAmount(ChemicalAmountedIngredient<PigmentStack, Pigment> ingredient, ChemicalStackIngredient.PigmentStackIngredient old) {
        if (ingredient.shouldChangeAmount(of(old))) {
            return toIngredientPigment(ingredient);
        }
        return toIngredientPigment(ingredient.withAmount(getAmount(old)));
    }

    @SuppressWarnings("unchecked")
    public static <S extends ChemicalStack<T>, T extends Chemical<T>> ChemicalStackIngredient<T, S> toIngredientKeepAmount(ChemicalAmountedIngredient<S, T> ingredient, ChemicalStackIngredient<?, ?> old) {
        if (ingredient.shouldChangeAmount(of((ChemicalStackIngredient<T, S>) old))) {
            return toIngredientChemical(ingredient);
        }
        return toIngredientChemical(ingredient.withAmount(getAmount(old)));
    }

    public static FluidStackIngredient toIngredientKeepAmount(FluidAmountedIngredient ingredient, FluidStackIngredient old) {
        if (ingredient.shouldChangeAmount(of(old))) {
            return toIngredientFluid(ingredient);
        }
        return toIngredientFluid(ingredient.withAmount(getAmount(old)));
    }

    public static int getAmount(ChemicalStackIngredient<?, ?> ingredient) {
        return of(ingredient).getRightAmount();
    }

    public static String getCTString(BoxedChemicalStack stack) {
        return getCTString(stack.getChemicalStack());
    }

    public static String getCTString(ChemicalStackIngredient<?, ?> ingredient) {
        ChemicalAmountedIngredient<?, ?> chemicalIngredient = of(ingredient);
        if (chemicalIngredient.isStack()) {
            return getCTString(chemicalIngredient.getStack());
        } else {
            TagKey<?> tag = chemicalIngredient.getTag();
            return "<tag:" + tag.registry().location().toString().replace(":", "/") + ":" + tag.location() + "> * " + chemicalIngredient.getAmount();
        }
    }

    public static String getCTString(ChemicalStack<?> stack) {
        String s = "<" + ChemicalType.getTypeFor(stack.getType()).getSerializedName() + ":" + stack.getTypeRegistryName() + ">";
        if (stack.getAmount() > 1) {
            return s + " * " + stack.getAmount();
        }
        return s;
    }

    public static <S extends ChemicalStack<T>, T extends Chemical<T>> ChemicalAmountedIngredient<S, T> chemicalAmountSetter(ChemicalAmountedIngredient<S, T> stack, boolean up, int smallChange, int normalChange, int largeChange) {
        int value = Screen.hasShiftDown() ? largeChange : (Screen.hasControlDown() ? smallChange : normalChange);
        return stack.withAmount(Math.max(1, (stack.getRightAmount() == 1 ? (value == 1 ? 1 : 0) : stack.getRightAmount()) + (up ? value : -value)));
    }

    public static <S extends ChemicalStack<T>, T extends Chemical<T>> ChemicalAmountedIngredient<S, T> chemicalAmountSetter(ChemicalAmountedIngredient<S, T> stack, boolean up, int normalChange, int largeChange) {
        return chemicalAmountSetter(stack, up, normalChange,normalChange, largeChange);
    }


    public static <S extends ChemicalStack<T>, T extends Chemical<T>> ChemicalAmountedIngredient<S, T> chemicalAmountSetter(ChemicalAmountedIngredient<S, T> stack, boolean up) {
        return chemicalAmountSetter(stack, up, 1, 50, 1000);
    }

    public static <S extends ChemicalStack<T>, T extends Chemical<T>> ChemicalAmountedIngredient<S, T> limitedChemicalAmountSetter(ChemicalAmountedIngredient<S, T> stack, boolean up) {
        return stack.withAmount(Math.max(1, stack.getRightAmount() + (up ? 1 : -1)));
    }

    public static ChemicalStack<?> from(Chemical<?> chemical, long amount) {
        if (chemical instanceof Gas gas) {
            return new GasStack(gas, amount);
        }
        if (chemical instanceof InfuseType infuseType) {
            return new InfusionStack(infuseType, amount);
        }
        if (chemical instanceof Pigment pigment) {
            return new PigmentStack(pigment, amount);
        }
        if (chemical instanceof Slurry slurry) {
            return new SlurryStack(slurry, amount);
        }
        throw new IllegalArgumentException("Unsupported chemical type: " + chemical);
    }

}
