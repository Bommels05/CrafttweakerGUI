package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.chemical.TagChemicalIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;

public class MekanismRecipeUtils {

    public static AmountedIngredient of(ItemStackIngredient ingredient) {
        return new AmountedIngredient(ingredient.ingredient().ingredient(), ingredient.ingredient().count());
    }

    public static ItemStackIngredient of(AmountedIngredient ingredient) {
        return IngredientCreatorAccess.item().from(ingredient.ingredient(), ingredient.amount());
    }

    public static FluidAmountedIngredient of(FluidStackIngredient ingredient) {
        if (ingredient.ingredient().ingredient() instanceof TagFluidIngredient tagged) {
            return new FluidAmountedIngredient(tagged.tag(), ingredient.ingredient().amount());
        }
        if (!ingredient.getRepresentations().isEmpty()) {
            return new FluidAmountedIngredient(ingredient.getRepresentations().get(0), ingredient.ingredient().amount());
        }
        throw new IllegalStateException("Non tag empty fluid ingredient: " + ingredient);
    }

    public static int getAmount(FluidStackIngredient ingredient) {
        return of(ingredient).getRightAmount();
    }

    public static ChemicalAmountedIngredient of(ChemicalStackIngredient ingredient) {
        if (ingredient.ingredient() instanceof TagChemicalIngredient tagged) {
            return new ChemicalAmountedIngredient(tagged.tag(), (int) ingredient.amount());
        }
        if (!ingredient.getRepresentations().isEmpty()) {
            return new ChemicalAmountedIngredient(ingredient.getRepresentations().get(0), (int) ingredient.amount());
        }
        throw new IllegalStateException("Non tag empty chemical ingredient: " + ingredient);
    }

    public static ChemicalStackIngredient toIngredientChemical(ChemicalAmountedIngredient ingredient) {
        if (ingredient.isStack()) {
            ChemicalStack stack = ingredient.getStack();
            if (ingredient.shouldUseAmount()) {
                stack = ingredient.getStack().copyWithAmount(ingredient.getAmount());
            }
            return IngredientCreatorAccess.chemicalStack().from(stack);
        }
        return IngredientCreatorAccess.chemicalStack().from(ingredient.getTag(), ingredient.getAmount());
    }

    public static FluidStackIngredient toIngredientFluid(FluidAmountedIngredient ingredient) {
        if (ingredient.isStack()) {
            return IngredientCreatorAccess.fluid().from(ingredient.shouldUseAmount() ? ingredient.getStack().copyWithAmount(ingredient.getAmount()) : ingredient.getStack());
        }
        return IngredientCreatorAccess.fluid().from(ingredient.getTag(), ingredient.getAmount());
    }

    public static ChemicalStackIngredient toIngredientKeepAmount(ChemicalAmountedIngredient ingredient, ChemicalStackIngredient old) {
        if (ingredient.shouldChangeAmount(of(old))) {
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

    public static int getAmount(ChemicalStackIngredient ingredient) {
        return of(ingredient).getRightAmount();
    }

    public static String getCTString(ChemicalStackIngredient ingredient) {
        ChemicalAmountedIngredient chemicalIngredient = of(ingredient);
        if (chemicalIngredient.isStack()) {
            return getCTString(chemicalIngredient.getStack());
        } else {
            TagKey<?> tag = chemicalIngredient.getTag();
            return "mods.mekanism.api.ingredient.ChemicalStackIngredient.from(<tag:mekanism/chemical:" + tag.location() + "> * " + chemicalIngredient.getAmount() + ")";
        }
    }

    public static String getCTString(ChemicalStack stack) {
        String s = "<chemical:" + stack.getTypeRegistryName() + ">";
        if (stack.getAmount() > 1) {
            return s + " * " + stack.getAmount();
        }
        return s;
    }

    public static ChemicalAmountedIngredient chemicalAmountSetter(ChemicalAmountedIngredient stack, boolean up, int smallChange, int normalChange, int largeChange) {
        int value = Screen.hasShiftDown() ? largeChange : (Screen.hasControlDown() ? smallChange : normalChange);
        return stack.withAmount(Math.max(1, (stack.getRightAmount() == 1 ? (value == 1 ? 1 : 0) : stack.getRightAmount()) + (up ? value : -value)));
    }

    public static ChemicalAmountedIngredient chemicalAmountSetter(ChemicalAmountedIngredient stack, boolean up, int normalChange, int largeChange) {
        return chemicalAmountSetter(stack, up, normalChange,normalChange, largeChange);
    }


    public static ChemicalAmountedIngredient chemicalAmountSetter(ChemicalAmountedIngredient stack, boolean up) {
        return chemicalAmountSetter(stack, up, 1, 50, 1000);
    }

    public static ChemicalAmountedIngredient limitedChemicalAmountSetter(ChemicalAmountedIngredient stack, boolean up) {
        return stack.withAmount(Math.max(1, stack.getRightAmount() + (up ? 1 : -1)));
    }

    public static ChemicalStack from(Chemical chemical, long amount) {
        return new ChemicalStack(chemical, amount);
    }

}
