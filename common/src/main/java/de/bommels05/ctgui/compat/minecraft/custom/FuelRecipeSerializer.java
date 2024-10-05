package de.bommels05.ctgui.compat.minecraft.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FuelRecipeSerializer implements RecipeSerializer<FuelRecipe> {

    private final Codec<FuelRecipe> codec;

    public FuelRecipeSerializer() {
        codec = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(FuelRecipe::getIngredient),
                Codec.INT.fieldOf("burnTime").forGetter(FuelRecipe::getBurnTime)
        ).apply(instance, FuelRecipe::new));
    }

    @Override
    public Codec<FuelRecipe> codec() {
        return codec;
    }

    @Override
    public FuelRecipe fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, FuelRecipe recipe) {
        throw new UnsupportedOperationException();
    }
}
