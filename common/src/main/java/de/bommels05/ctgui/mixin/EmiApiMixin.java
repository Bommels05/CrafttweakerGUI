package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.screen.RecipeEditScreen;
import dev.emi.emi.api.EmiApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EmiApi.class)
public class EmiApiMixin {

    @Shadow
    @Final
    private static Minecraft client;

    @Inject(method = "getHandledScreen", at = @At(value = "RETURN"), cancellable = true)
    private static void supportRecipeEditScreen(CallbackInfoReturnable<AbstractContainerScreen<?>> cir) {
        if (client.screen instanceof RecipeEditScreen<?>) {
            cir.setReturnValue(new InventoryScreen(client.player));
        }
    }

}
