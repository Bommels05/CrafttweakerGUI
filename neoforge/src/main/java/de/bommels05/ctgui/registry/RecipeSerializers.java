package de.bommels05.ctgui.registry;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.compat.minecraft.BrewingRecipe;
import de.bommels05.ctgui.compat.minecraft.BrewingRecipeSerializer;
import de.bommels05.ctgui.compat.minecraft.custom.FuelRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.FuelRecipeSerializer;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CraftTweakerGUI.MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TagRecipe>> TAG = RECIPE_SERIALIZERS.register("tag", TagRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FuelRecipe>> FUEL = RECIPE_SERIALIZERS.register("fuel", FuelRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BrewingRecipe>> BREWING = RECIPE_SERIALIZERS.register("brewing", BrewingRecipeSerializer::new);

}
