package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.ingredient.creator.ItemStackIngredientCreator;

public class MekanismRecipeUtils {

    public static AmountedIngredient of(ItemStackIngredient ingredient) {
        if (ingredient instanceof ItemStackIngredientCreator.SingleItemStackIngredient i) {
            return new AmountedIngredient(i.getInputRaw(), i.getAmountRaw());
        }
        return AmountedIngredient.of(SupportedRecipeType.UNSET);
    }

    public static ItemStackIngredient of(AmountedIngredient ingredient) {
        return IngredientCreatorAccess.item().from(ingredient.ingredient(), ingredient.amount());
    }

}
