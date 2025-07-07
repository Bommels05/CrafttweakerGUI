package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class CompostingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final float chance;

    public CompostingRecipe(ResourceLocation id, Ingredient ingredient, float chance) {
        this.id = id;
        this.ingredient = ingredient;
        this.chance = chance;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
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
        return CraftTweakerGUI.getLoaderUtils().getCompostingRecipeSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CraftTweakerGUI.getLoaderUtils().getCompostingRecipeType();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public float getChance() {
        return chance;
    }
}
