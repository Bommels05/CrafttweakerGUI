package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.api.UnsupportedViewerException;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.recipe.EmiCookingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;

public class CampfireCookingRecipeType extends CookingRecipeType<CampfireCookingRecipe> {

    public CampfireCookingRecipeType() {
        super(new ResourceLocation("minecraft:campfire_cooking"), CampfireCookingRecipe::new, 100, "campfire");
    }

    @Override
    public Object getEmiRecipe(CampfireCookingRecipe recipe) throws UnsupportedViewerException {
        return new EmiCookingRecipe(recipe, VanillaEmiRecipeCategories.CAMPFIRE_COOKING, 1, true);
    }
}
