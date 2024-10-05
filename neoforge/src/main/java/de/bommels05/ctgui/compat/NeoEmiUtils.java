package de.bommels05.ctgui.compat;

import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.compat.minecraft.BrewingRecipe;
import de.bommels05.ctgui.emi.FakeEmiStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiBrewingRecipe;
import net.minecraft.resources.ResourceLocation;

public class NeoEmiUtils {

    public static EmiBrewingRecipe getEmiRecipe(BrewingRecipe recipe, ResourceLocation id) throws UnsupportedViewerException {
        return new EmiBrewingRecipe(new FakeEmiStack(EmiIngredient.of(recipe.getInput())), EmiIngredient.of(recipe.getReagent()), EmiStack.of(recipe.getOutput()), id);
    }

}
