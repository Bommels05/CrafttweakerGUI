package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.recipe.EmiStonecuttingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import javax.swing.plaf.PanelUI;

public class StoneCuttingRecipeType extends SupportedRecipeType<StonecutterRecipe> {


    public StoneCuttingRecipeType() {
        super(new ResourceLocation("minecraft:stonecutting"));

        addAreaEmptyRightClick(0, 0, 17, 17, (r, am) -> {
            return new StonecutterRecipe(r.getGroup(),am.ingredient(), r.getResultItem(regAccess()));
        }, r -> {
            return new AmountedIngredient(r.getIngredients().get(0), 1);
        });
        addAreaScrollAmountEmptyRightClick(58, 0, 25, 25, (r, am) -> {
            return new StonecutterRecipe(r.getGroup(), r.getIngredients().get(0), am.asStack());
        }, r -> {
            return AmountedIngredient.of(r.getResultItem(regAccess()));
        });
    }

    @Override
    public StonecutterRecipe onInitialize(StonecutterRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new StonecutterRecipe("", Ingredient.EMPTY, ItemStack.EMPTY);
        }
        return null;
    }

    @Override
    public boolean isValid(StonecutterRecipe recipe) {
        return !recipe.getIngredients().get(0).isEmpty() && !recipe.getResultItem(regAccess()).isEmpty();
    }

    @Override
    public Object getEmiRecipe(StonecutterRecipe recipe) throws UnsupportedViewerException {
        return new EmiStonecuttingRecipe(recipe);
    }

    @Override
    public String getCraftTweakerString(StonecutterRecipe recipe, String id) {
        return "stoneCutter.addRecipe(\"" + id + "\", " + getCTString(recipe.getResultItem(regAccess())) +  ", "+ getCTString(recipe.getIngredients().get(0)) + ");";
    }
}
