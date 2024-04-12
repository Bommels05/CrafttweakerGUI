package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.ChangedRecipeManager;
import dev.emi.emi.registry.EmiRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.emi.emi.registry.EmiRecipes$Worker")
public class EmiRecipesMixin {

    @Inject(method = "run", at = @At(value = "RETURN"))
    protected void onReloaded(CallbackInfo ci) {
        ChangedRecipeManager.reInjectAll();
    }

}
