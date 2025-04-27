package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class CompostingRecipe implements Recipe<RecipeInput> {

    private final Ingredient ingredient;
    private final float chance;

    public CompostingRecipe(Ingredient ingredient, float chance) {
        this.ingredient = ingredient;
        this.chance = chance;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ingredient.isEmpty() ? ItemStack.EMPTY : ingredient.getItems()[0];
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CraftTweakerGUI.getLoaderUtils().getCompostingRecipeSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CraftTweakerGUI.getLoaderUtils().getCompostingRecipeType();
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public float getChance() {
        return chance;
    }
}
