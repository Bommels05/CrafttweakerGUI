package de.bommels05.ctgui.compat.minecraft.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FuelRecipeSerializer implements RecipeSerializer<FuelRecipe> {

    private final MapCodec<FuelRecipe> codec;

    public FuelRecipeSerializer() {
        codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(FuelRecipe::getIngredient),
                Codec.INT.fieldOf("burnTime").forGetter(FuelRecipe::getBurnTime)
        ).apply(instance, FuelRecipe::new));
    }

    @Override
    public MapCodec<FuelRecipe> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FuelRecipe> streamCodec() {
        throw new UnsupportedOperationException();
    }
}
