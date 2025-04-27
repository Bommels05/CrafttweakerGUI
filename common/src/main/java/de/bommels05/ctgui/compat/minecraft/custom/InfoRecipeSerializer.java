package de.bommels05.ctgui.compat.minecraft.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class InfoRecipeSerializer implements RecipeSerializer<InfoRecipe> {
    private final MapCodec<InfoRecipe> codec;

    public InfoRecipeSerializer() {
        codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(InfoRecipe::getIngredient),
                Codec.STRING.fieldOf("text").forGetter(InfoRecipe::getText)
        ).apply(instance, InfoRecipe::new));
    }

    @Override
    public MapCodec<InfoRecipe> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, InfoRecipe> streamCodec() {
        throw new UnsupportedOperationException();
    }
}
