package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.recipe.EmiCookingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.BlastingRecipe;

public class BlastingRecipeType extends CookingRecipeType<BlastingRecipe> {

    public BlastingRecipeType() {
        super(CraftTweakerGUI.rl("minecraft:blasting"), BlastingRecipe::new, 100, "blastFurnace");
    }

    @Override
    public Object getEmiRecipe(BlastingRecipe recipe) throws UnsupportedViewerException {
        return new EmiCookingRecipe(recipe, VanillaEmiRecipeCategories.BLASTING, 2, false);
    }
}
