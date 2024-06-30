package de.bommels05.ctgui;

import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.nio.file.Path;

public interface LoaderUtils {

    public boolean isModLoaded(String id);

    public void setEditMode(boolean value);

    public void setListButton(boolean value);

    public RecipeSerializer<TagRecipe> getTagRecipeSerializer();

    public RecipeType<TagRecipe> getTagRecipeType();

    public <T> Object stackFromType(T type);

    public SpecialAmountedIngredient<?, ?> getRightImplementation(SpecialAmountedIngredient<?, ?> ingredient);

    public MinecraftServer getServer();

    public Path getConfigDir();

    public Path getGameDir();

    //Actually returns an EmiIngredient but can't be class loaded when it is not installed
    public <S> Object getEmiIngredient(S stack);

    //Same as above, actually needs an EmiIngredient
    public Object getFromEmiStack(Object stack);
    //Needs an EmiInitRegistry
    public void emiInit(Object registry);

    public ShapedRecipe tryGetFromMekanismRecipe(Recipe<?> recipe);

    public String getMekanismCraftTweakerString(ShapedRecipe recipe, String id);

}
