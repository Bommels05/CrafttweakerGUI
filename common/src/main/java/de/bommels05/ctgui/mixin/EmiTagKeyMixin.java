package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import dev.emi.emi.EmiPort;
import dev.emi.emi.runtime.EmiTagKey;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EmiTagKey.class)
public abstract class EmiTagKeyMixin {

    @Shadow
    public abstract ResourceLocation id();

    @Inject(method = "getTagName", at = @At(value = "HEAD"), cancellable = true)
    private void disableTagTranslation(CallbackInfoReturnable<Component> cir) {
        if (Config.noTagTranslations && (Minecraft.getInstance().screen instanceof RecipeEditScreen<?> || Config.showTagsEverywhere)) {
            cir.setReturnValue(EmiPort.literal("#" + id()));
        }
    }

}
