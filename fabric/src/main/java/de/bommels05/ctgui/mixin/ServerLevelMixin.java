package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "save", at = @At("RETURN"))
    protected void onSave(ProgressListener progressListener, boolean bl, boolean bl2, CallbackInfo ci) {
        if (!bl2) {
            if (Config.editMode) {
                ChangedRecipeManager.save();
            }
        }
    }

}
