package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class FuelRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final int burnTime;

    public FuelRecipe(ResourceLocation id, Ingredient ingredient, int burnTime) {
        this.id = id;
        this.ingredient = ingredient;
        this.burnTime = burnTime;
    }

    @Override
    public boolean matches(Container Container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container Container, RegistryAccess access) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
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

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getBurnTime() {
        return burnTime;
    }
}
