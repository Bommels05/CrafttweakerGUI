package de.bommels05.ctgui.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EmiCraftingRecipe.class)
public class EmiCraftingRecipeMixin {

    @ModifyReturnValue(method = "canFit", at = @At(value = "RETURN"), remap = false)
    private boolean showRealPosition(boolean original) {
        //Don't center Recipes in the editing menu
        if (Minecraft.getInstance().screen instanceof RecipeEditScreen<?>) {
            return false;
        }
        return original;
    }

}
