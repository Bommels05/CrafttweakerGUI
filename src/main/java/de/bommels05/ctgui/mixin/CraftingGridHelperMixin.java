package de.bommels05.ctgui.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import mezz.jei.library.gui.CraftingGridHelper;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingGridHelper.class)
public class CraftingGridHelperMixin {

    @ModifyReturnValue(method = "getShapelessSize", at = @At(value = "RETURN"))
    private static int showRealPosition(int original) {
        //Don't center Recipes in the editing menu
        if (Minecraft.getInstance().screen instanceof RecipeEditScreen<?>) {
            return 3;
        }
        return original;
    }

}
