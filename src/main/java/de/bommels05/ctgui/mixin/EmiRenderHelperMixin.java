package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.Config;
import dev.emi.emi.EmiRenderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EmiRenderHelper.class)
public class EmiRenderHelperMixin {

    @Redirect(method = "renderTag", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private static int alwaysRenderTag(List<?> instance) {
        //Forces EMI to render the tag indicator for tags with 1 item
        if (Config.noTagCollapsing && instance.size() == 1) {
            return 2;
        }
        return instance.size();
    }

}
