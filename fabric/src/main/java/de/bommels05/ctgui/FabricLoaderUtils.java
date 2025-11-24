package de.bommels05.ctgui;

import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import de.bommels05.ctgui.compat.minecraft.custom.CompostingRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.FuelRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.InfoRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import dev.emi.emi.api.EmiInitRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.nio.file.Path;

public class FabricLoaderUtils implements LoaderUtils {

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public void setEditMode(boolean value) {
        FabricConfig.setEditMode(value);
    }

    @Override
    public void setListButton(boolean value) {
        FabricConfig.setListButton(value);
    }

    @Override
    public RecipeSerializer<TagRecipe<?>> getTagRecipeSerializer() {
        return ClientInit.tagRecipeSerializer;
    }

    @Override
    public RecipeType<TagRecipe<?>> getTagRecipeType() {
        return ClientInit.tagRecipeType;
    }

    @Override
    public RecipeSerializer<FuelRecipe> getFuelRecipeSerializer() {
        return ClientInit.fuelRecipeSerializer;
    }

    @Override
    public RecipeType<FuelRecipe> getFuelRecipeType() {
        return ClientInit.fuelRecipeType;
    }

    @Override
    public RecipeSerializer<CompostingRecipe> getCompostingRecipeSerializer() {
        return ClientInit.compostingRecipeSerializer;
    }

    @Override
    public RecipeType<CompostingRecipe> getCompostingRecipeType() {
        return ClientInit.compostingRecipeType;
    }

    @Override
    public RecipeSerializer<InfoRecipe> getInfoRecipeSerializer() {
        return ClientInit.infoRecipeSerializer;
    }

    @Override
    public RecipeType<InfoRecipe> getInfoRecipeType() {
        return ClientInit.infoRecipeType;
    }

    @Override
    public Object stackFromType(Object type) {
        return type;
    }

    @Override
    public Object emptyStackFromRegistry(Registry<?> registry) {
        throw new UnsupportedOperationException("Unsupported Ingredient Type: " + registry.key().location());
    }

    @Override
    public SpecialAmountedIngredient<?, ?> getIngredientFromStack(Object stack) {
        throw new UnsupportedOperationException("Unsupported Ingredient Type: " + stack.getClass());
    }

    @Override
    public SpecialAmountedIngredient<?, ?> getIngredientFromTag(TagKey<?> tag, int amount) {
        throw new UnsupportedOperationException("Unsupported Ingredient Type: " + tag.registry().location());
    }

    @Override
    public MinecraftServer getServer() {
        return Minecraft.getInstance().getSingleplayerServer();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public String getDefaultTag() {
        return "c:ingots/iron";
    }

    @Override
    public <S> Object getEmiIngredient(S stack) {
        throw new IllegalArgumentException("Unsupported ingredient");
    }

    @Override
    public Object getFromEmiStack(Object stack) {
        return null;
    }

    @Override
    public void emiInit(Object registry) {
        EmiInitRegistry reg = (EmiInitRegistry) registry;
    }

    @Override
    public ShapedRecipe tryGetFromMekanismRecipe(Recipe<?> recipe) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMekanismCraftTweakerString(ShapedRecipe recipe, String id) {
        throw new UnsupportedOperationException();
    }
}
