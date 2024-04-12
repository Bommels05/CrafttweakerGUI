package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.api.UnsupportedViewerException;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.recipe.EmiCookingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;

public class SmokingRecipeType extends CookingRecipeType<SmokingRecipe> {

    public SmokingRecipeType() {
        super(new ResourceLocation("minecraft:smoking"), SmokingRecipe::new, 100, "smoker");
    }

    @Override
    public EmiRecipe getEmiRecipe(SmokingRecipe recipe) throws UnsupportedViewerException {
        return new EmiCookingRecipe(recipe, VanillaEmiRecipeCategories.SMOKING, 2, false);
    }
}
