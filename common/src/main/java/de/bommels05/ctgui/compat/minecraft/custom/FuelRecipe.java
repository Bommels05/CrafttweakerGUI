package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class FuelRecipe implements Recipe<RecipeInput> {

    private final Ingredient ingredient;
    private final int burnTime;

    public FuelRecipe(Ingredient ingredient, int burnTime) {
        this.ingredient = ingredient;
        this.burnTime = burnTime;
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
        return CraftTweakerGUI.getLoaderUtils().getFuelRecipeSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CraftTweakerGUI.getLoaderUtils().getFuelRecipeType();
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getBurnTime() {
        return burnTime;
    }
}
