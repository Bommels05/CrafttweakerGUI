package de.bommels05.ctgui;

import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import de.bommels05.ctgui.compat.minecraft.custom.CompostingRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.FuelRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.InfoRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.nio.file.Path;

public interface LoaderUtils {

    public boolean isModLoaded(String id);

    public void setEditMode(boolean value);

    public void setListButton(boolean value);

    public RecipeSerializer<TagRecipe<?>> getTagRecipeSerializer();

    public RecipeType<TagRecipe<?>> getTagRecipeType();

    public RecipeSerializer<FuelRecipe> getFuelRecipeSerializer();

    public RecipeType<FuelRecipe> getFuelRecipeType();

    public RecipeSerializer<CompostingRecipe> getCompostingRecipeSerializer();

    public RecipeType<CompostingRecipe> getCompostingRecipeType();

    public RecipeSerializer<InfoRecipe> getInfoRecipeSerializer();

    public RecipeType<InfoRecipe> getInfoRecipeType();

    public Object stackFromType(Object type);

    public Object emptyStackFromRegistry(Registry<?> registry);

    public SpecialAmountedIngredient<?, ?> getIngredientFromStack(Object stack);

    public SpecialAmountedIngredient<?, ?> getIngredientFromTag(TagKey<?> tag, int amount);

    public MinecraftServer getServer();

    public Path getConfigDir();

    public Path getGameDir();

    public String getDefaultTag();

    //Actually returns an EmiIngredient but can't be class loaded when it is not installed
    public <S> Object getEmiIngredient(S stack);

    //Same as above, actually needs an EmiIngredient
    public Object getFromEmiStack(Object stack);

    public ShapedRecipe tryGetFromMekanismRecipe(Recipe<?> recipe);

    public String getMekanismCraftTweakerString(ShapedRecipe recipe, String id);

}
