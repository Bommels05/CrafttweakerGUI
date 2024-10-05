package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.registry.RecipeSerializers;
import de.bommels05.ctgui.registry.RecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

public class BrewingRecipe extends net.neoforged.neoforge.common.brewing.BrewingRecipe implements Recipe<BrewingStandBlockEntity> {

    public BrewingRecipe(Ingredient input, Ingredient reagent, ItemStack output) {
        super(input, reagent, output);
    }

    public Ingredient getReagent() {
        return getIngredient();
    }

    @Override
    public boolean matches(BrewingStandBlockEntity arg, Level arg2) {
        return false;
    }

    @Override
    public ItemStack assemble(BrewingStandBlockEntity arg, RegistryAccess arg2) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess arg) {
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
