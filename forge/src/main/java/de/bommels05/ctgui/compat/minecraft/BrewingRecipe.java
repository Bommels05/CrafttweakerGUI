package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.registry.RecipeSerializers;
import de.bommels05.ctgui.registry.RecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class BrewingRecipe extends net.minecraftforge.common.brewing.BrewingRecipe implements Recipe<Container> {
    private final ResourceLocation id;

    public BrewingRecipe(ResourceLocation id, Ingredient input, Ingredient reagent, ItemStack output) {
        super(input, reagent, output);
        this.id = id;
    }

    public Ingredient getReagent() {
        return getIngredient();
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

    @Override
    public ResourceLocation getId() {
        return id;
    }
}
