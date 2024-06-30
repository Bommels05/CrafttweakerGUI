package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.screen.RecipeEditScreen;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.screen.EmiScreenBase;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EmiScreenBase.class)
public class EmiScreenBaseMixin {

    @Invoker(value = "<init>")
    protected static EmiScreenBase init(Screen screen, Bounds bounds) {
        return null;
    }

    @Inject(method = "of", at = @At(value = "HEAD"), cancellable = true)
    private static void supportRecipeEditScreen(Screen screen, CallbackInfoReturnable<EmiScreenBase> cir) {
        if (screen instanceof RecipeEditScreen<?> edit) {
            cir.setReturnValue(init(screen, new Bounds((edit.width - edit.imageWidth) / 2, (edit.height - edit.imageHeight) / 2, edit.imageWidth, edit.imageHeight)));
        }
    }

}
