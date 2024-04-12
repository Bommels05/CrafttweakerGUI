package de.bommels05.ctgui.mixin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.io.Serializable;
import java.util.*;

@Mixin(ShapedRecipePattern.class)
public class ShapedRecipePatternMixin {

    @ModifyReturnValue(method = "lambda$static$2", at = @At(value = "RETURN"))
    private static DataResult<ShapedRecipePattern.Data> generatePattern(DataResult<ShapedRecipePattern.Data> original, ShapedRecipePattern pattern) {
        //Our recipes are always 3x3, so we don't have to handle other sizes
        if (original.error().isPresent() && pattern.width() == 3 && pattern.height() == 3) {
            List<String> patternString = new ArrayList<>();
            BiMap<Character, Ingredient> keys = HashBiMap.create();
            int i = 0;
            for (Ingredient ingredient : pattern.ingredients()) {
                Character key = getKey(ingredient, keys);
                if (key != ' ') {
                    keys.put(key, ingredient);
                }
                if (i % 3 == 0) {
                    patternString.add(String.valueOf(key));
                } else {
                    patternString.set(i / 3, patternString.get(i / 3) + key);
                }
                i++;
            }
            return DataResult.success(new ShapedRecipePattern.Data(keys, patternString));
        }
        return original;
    }

    private static Character getKey(Ingredient ingredient, BiMap<Character, Ingredient> keys) {
        if (keys.containsValue(ingredient)) {
            return keys.inverse().get(ingredient);
        }
        if (ingredient.isEmpty()) {
            return ' ';
        }
        Set<Character> blacklist = keys.keySet();
        if (ingredient.getItems().length > 0) {
            char c = BuiltInRegistries.ITEM.getKey(ingredient.getItems()[0].getItem()).getPath().charAt(0);
            if (!blacklist.contains(c)) {
                return c;
            }
        }
        while (blacklist.size() < 26) {
            char c = (char) new Random().nextInt('a', 'z' + 1);
            if (!blacklist.contains(c)) {
                return c;
            }
        }
        throw new IllegalStateException("Crafting recipe somehow has more than 26 different ingredients!?");
    }

}
