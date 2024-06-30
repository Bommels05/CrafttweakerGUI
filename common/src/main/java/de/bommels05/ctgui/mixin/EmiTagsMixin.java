package de.bommels05.ctgui.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import de.bommels05.ctgui.emi.TagCollapsingBypassingList;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiTags;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(EmiTags.class)
public class EmiTagsMixin {

    @Redirect(method = "tagIngredient", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private static int alwaysDisplayTag(List<?> instance) {
        if (Config.noTagCollapsing && (Minecraft.getInstance().screen instanceof RecipeEditScreen<?> || Config.showTagsEverywhere) && instance.size() == 1) {
            return 2;
        }
        return instance.size();
    }

    @Redirect(method = "getIngredient", at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I"), remap = false)
    private static int alwaysDisplayTag2(Map<?, ?> instance, Class<?> clazz, List<EmiStack> stacks) {
        if (stacks instanceof TagCollapsingBypassingList && instance.size() == 1) {
            return 2;
        }
        return instance.size();
    }

    @Redirect(method = "getIngredient", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"), remap = false)
    private static int alwaysDisplayTag3(List<?> instance, Class<?> clazz, List<EmiStack> stacks) {
        if (stacks instanceof TagCollapsingBypassingList && instance.size() == 1 && !(instance.get(0) instanceof TagKey)) {
            return 2;
        }
        return instance.size();
    }

    @ModifyReturnValue(method = "getRawValues", at = @At(value = "RETURN"))
    private static List<EmiStack> modifyRawValues(List<EmiStack> original) {
        //Indicate that this is a tag to mixins further down the line
        if (Config.noTagCollapsing && (Minecraft.getInstance().screen instanceof RecipeEditScreen<?> || Config.showTagsEverywhere)) {
            return new TagCollapsingBypassingList<>(original);
        }
        return original;
    }

    @Inject(method = "getTagName", at = @At(value = "HEAD"), cancellable = true)
    private static void disableTagTranslation(TagKey<?> key, CallbackInfoReturnable<Component> cir) {
        if (Config.noTagTranslations && (Minecraft.getInstance().screen instanceof RecipeEditScreen<?> || Config.showTagsEverywhere)) {
            cir.setReturnValue(EmiPort.literal("#" + key.location()));
        }
    }

}
