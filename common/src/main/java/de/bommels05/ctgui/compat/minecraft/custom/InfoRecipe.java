package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class InfoRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final String text;

    public InfoRecipe(ResourceLocation id, Ingredient ingredient, String text) {
        this.id = id;
        this.ingredient = ingredient;
        this.text = text;
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
        return CraftTweakerGUI.getLoaderUtils().getInfoRecipeSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CraftTweakerGUI.getLoaderUtils().getInfoRecipeType();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public String getText() {
        return text;
    }
}
