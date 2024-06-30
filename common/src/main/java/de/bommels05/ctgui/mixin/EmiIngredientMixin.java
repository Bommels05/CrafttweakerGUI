package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import de.bommels05.ctgui.emi.TagCollapsingBypassingList;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Mixin(EmiIngredient.class)
public interface EmiIngredientMixin {

    @Redirect(method = "of(Ljava/util/List;J)Ldev/emi/emi/api/stack/EmiIngredient;", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"), remap = false)
    private static int alwaysDisplayTag(List<?> instance) {
        if (instance instanceof TagCollapsingBypassingList && instance.size() == 1) {
            return 2;
        }
        return instance.size();
    }

    @Redirect(method = "of(Ljava/util/List;J)Ldev/emi/emi/api/stack/EmiIngredient;", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"), remap = false)
    private static List<?> alwaysDisplayTag(Stream<?> instance, List<?> ingredients) {
        if (ingredients instanceof TagCollapsingBypassingList) {
            //Pass on the tag indicator to mixins further down the line
            return new TagCollapsingBypassingList<>(instance.toList());
        }
        return instance.toList();
    }

    @Redirect(method = "of(Lnet/minecraft/world/item/crafting/Ingredient;J)Ldev/emi/emi/api/stack/EmiIngredient;", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"), remap = false)
    private static List<?> alwaysDisplayTag(Stream<?> instance, Ingredient ingredient) {
        if (Config.noTagCollapsing && (Minecraft.getInstance().screen instanceof RecipeEditScreen<?> || Config.showTagsEverywhere) &&
                Arrays.stream(ingredient.values).anyMatch(value -> value instanceof Ingredient.TagValue &&
                        //Empty Tags shouldn't display as a list ingredient
                        BuiltInRegistries.ITEM.getTagOrEmpty(((Ingredient.TagValue) value).tag()).iterator().hasNext())) {
            //Indicate that this is a tag to mixins further down the line
            return new TagCollapsingBypassingList<>(instance.toList());
        }
        return instance.toList();
    }

}
