package de.bommels05.ctgui.registry;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, CraftTweakerGUI.MOD_ID);
    public static final DeferredHolder<RecipeType<?>, RecipeType<TagRecipe>> TAG = RECIPE_TYPES.register("tag", () -> RecipeType.simple(new ResourceLocation(CraftTweakerGUI.MOD_ID, "tag")));

}
