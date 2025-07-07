package de.bommels05.ctgui.registry;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.compat.minecraft.BrewingRecipe;
import de.bommels05.ctgui.compat.minecraft.BrewingRecipeSerializer;
import de.bommels05.ctgui.compat.minecraft.custom.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CraftTweakerGUI.MOD_ID);
    public static final RegistryObject<RecipeSerializer<TagRecipe>> TAG = RECIPE_SERIALIZERS.register("tag", TagRecipeSerializer::new);
    public static final RegistryObject<RecipeSerializer<FuelRecipe>> FUEL = RECIPE_SERIALIZERS.register("fuel", FuelRecipeSerializer::new);
    public static final RegistryObject<RecipeSerializer<CompostingRecipe>> COMPOSTING = RECIPE_SERIALIZERS.register("composting", CompostingRecipeSerializer::new);
    public static final RegistryObject<RecipeSerializer<InfoRecipe>> INFO = RECIPE_SERIALIZERS.register("info", InfoRecipeSerializer::new);
    public static final RegistryObject<RecipeSerializer<BrewingRecipe>> BREWING = RECIPE_SERIALIZERS.register("brewing", BrewingRecipeSerializer::new);

}
