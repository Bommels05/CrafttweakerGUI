package de.bommels05.ctgui.registry;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CraftTweakerGUI.MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TagRecipe>> TAG = RECIPE_SERIALIZERS.register("tag", TagRecipeSerializer::new);

}
