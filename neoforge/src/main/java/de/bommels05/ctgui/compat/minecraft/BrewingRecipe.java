package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.registry.RecipeSerializers;
import de.bommels05.ctgui.registry.RecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

public class BrewingRecipe extends net.neoforged.neoforge.common.brewing.BrewingRecipe implements Recipe<RecipeInput> {

    public BrewingRecipe(Ingredient input, Ingredient reagent, ItemStack output) {
        super(input, reagent, output);
    }

    public Ingredient getReagent() {
        return getIngredient();
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
        return getOutput();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.BREWING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeTypes.BREWING.get();
    }
}
