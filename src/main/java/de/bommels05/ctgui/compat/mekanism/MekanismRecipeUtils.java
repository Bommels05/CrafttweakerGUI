package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.text.EnumColor;
import mekanism.common.recipe.ingredient.creator.ItemStackIngredientCreator;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismInfuseTypes;
import mekanism.common.registries.MekanismPigments;
import mekanism.common.registries.MekanismSlurries;
import mekanism.common.resource.PrimaryResource;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

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

    public static FluidStack of(FluidStackIngredient ingredient) {
        if (!ingredient.getRepresentations().isEmpty()) {
            return ingredient.getRepresentations().get(0);
        }
        return new FluidStack(Fluids.WATER, 1000);
    }

    public static int getAmount(FluidStackIngredient ingredient) {
        return of(ingredient).getAmount();
    }

    public static InfusionStack of(ChemicalStackIngredient.InfusionStackIngredient ingredient) {
        if (!ingredient.getRepresentations().isEmpty()) {
            return ingredient.getRepresentations().get(0);
        }
        return new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 1000);
    }

    public static GasStack of(ChemicalStackIngredient.GasStackIngredient ingredient) {
        if (!ingredient.getRepresentations().isEmpty()) {
            return ingredient.getRepresentations().get(0);
        }
        return new GasStack(MekanismGases.OXYGEN.get(), 100);
    }

    public static SlurryStack of(ChemicalStackIngredient.SlurryStackIngredient ingredient) {
        if (!ingredient.getRepresentations().isEmpty()) {
            return ingredient.getRepresentations().get(0);
        }
        return new SlurryStack(MekanismSlurries.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getDirtySlurry(), 1);
    }

    public static PigmentStack of(ChemicalStackIngredient.PigmentStackIngredient ingredient) {
        if (!ingredient.getRepresentations().isEmpty()) {
            return ingredient.getRepresentations().get(0);
        }
        return new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 100);
    }

    public static ChemicalStack<?> of(ChemicalStackIngredient<?, ?> ingredient) {
        if (!ingredient.getRepresentations().isEmpty()) {
            return ingredient.getRepresentations().get(0);
        }
        return new GasStack(MekanismGases.OXYGEN.get(), 100);
    }

    public static long getAmount(ChemicalStackIngredient<?, ?> ingredient) {
        return of(ingredient).getAmount();
    }

    public static String getCTString(BoxedChemicalStack stack) {
        return getCTString(stack.getChemicalStack());
    }

    public static String getCTString(ChemicalStackIngredient<?, ?> ingredient) {
        return getCTString(of(ingredient));
    }

    public static String getCTString(ChemicalStack<?> stack) {
        String s = "<" + ChemicalType.getTypeFor(stack.getType()).getSerializedName() + ":" + stack.getTypeRegistryName() + ">";
        if (stack.getAmount() > 1) {
            return s + " * " + stack.getAmount();
        }
        return s;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ChemicalStack<?>> T chemicalAmountSetter(T stack, boolean up, int smallChange, int normalChange, int largeChange) {
        T result = (T) stack.copy();
        int value = Screen.hasShiftDown() ? largeChange : (Screen.hasControlDown() ? smallChange : normalChange);
        result.setAmount(Math.max(1, (stack.getAmount() == 1 ? (value == 1 ? 1 : 0) : stack.getAmount()) + (up ? value : -value)));
        return result;
    }

    public static <T extends ChemicalStack<?>> T chemicalAmountSetter(T stack, boolean up, int normalChange, int largeChange) {
        return chemicalAmountSetter(stack, up, normalChange,normalChange, largeChange);
    }


    public static <T extends ChemicalStack<?>> T chemicalAmountSetter(T stack, boolean up) {
        return chemicalAmountSetter(stack, up, 1, 50, 1000);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ChemicalStack<?>> T limitedChemicalAmountSetter(T stack, boolean up) {
        T result = (T) stack.copy();
        result.setAmount(Math.max(1, stack.getAmount() + (up ? 1 : -1)));
        return result;
    }

}
