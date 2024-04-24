package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.api.UnsupportedViewerException;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.recipe.EmiCookingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class SmeltingRecipeType extends CookingRecipeType<SmeltingRecipe> {

    public SmeltingRecipeType() {
        super(new ResourceLocation("minecraft:smelting"), SmeltingRecipe::new, 200, "furnace");
    }

    @Override
    public Object getEmiRecipe(SmeltingRecipe recipe) throws UnsupportedViewerException {
        return new EmiCookingRecipe(recipe, VanillaEmiRecipeCategories.SMELTING, 1, false);
    }
}
