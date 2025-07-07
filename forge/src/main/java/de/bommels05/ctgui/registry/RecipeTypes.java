package de.bommels05.ctgui.registry;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.compat.minecraft.BrewingRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.CompostingRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.FuelRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.InfoRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, CraftTweakerGUI.MOD_ID);
    public static final RegistryObject<RecipeType<TagRecipe>> TAG = RECIPE_TYPES.register("tag", () -> RecipeType.simple(new ResourceLocation(CraftTweakerGUI.MOD_ID, "tag")));
    public static final RegistryObject<RecipeType<FuelRecipe>> FUEL = RECIPE_TYPES.register("fuel", () -> RecipeType.simple(new ResourceLocation(CraftTweakerGUI.MOD_ID, "fuel")));
    public static final RegistryObject<RecipeType<CompostingRecipe>> COMPOSTING = RECIPE_TYPES.register("composting", () -> RecipeType.simple(new ResourceLocation(CraftTweakerGUI.MOD_ID, "composting")));
    public static final RegistryObject<RecipeType<InfoRecipe>> INFO = RECIPE_TYPES.register("info", () -> RecipeType.simple(new ResourceLocation(CraftTweakerGUI.MOD_ID, "info")));
    public static final RegistryObject<RecipeType<BrewingRecipe>> BREWING = RECIPE_TYPES.register("brewing", () -> RecipeType.simple(new ResourceLocation(CraftTweakerGUI.MOD_ID, "brewing")));

}
