package de.bommels05.ctgui;

import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import dev.emi.emi.api.EmiInitRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
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
    public RecipeSerializer<TagRecipe> getTagRecipeSerializer() {
        return ClientInit.tagRecipeSerializer;
    }

    @Override
    public RecipeType<TagRecipe> getTagRecipeType() {
        return ClientInit.tagRecipeType;
    }

    @Override
    public <T> Object stackFromType(T type) {
        return type;
    }

    @Override
    public SpecialAmountedIngredient<?, ?> getRightImplementation(SpecialAmountedIngredient<?, ?> ingredient) {
        return ingredient;
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
        return "c:iron_ingots";
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
