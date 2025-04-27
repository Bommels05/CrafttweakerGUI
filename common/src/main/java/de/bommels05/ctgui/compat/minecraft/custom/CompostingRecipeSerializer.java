package de.bommels05.ctgui.compat.minecraft.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CompostingRecipeSerializer implements RecipeSerializer<CompostingRecipe> {

    private final MapCodec<CompostingRecipe> codec;

    public CompostingRecipeSerializer() {
        codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(CompostingRecipe::getIngredient),
                Codec.FLOAT.fieldOf("chance").forGetter(CompostingRecipe::getChance)
        ).apply(instance, CompostingRecipe::new));
    }

    @Override
    public MapCodec<CompostingRecipe> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CompostingRecipe> streamCodec() {
        throw new UnsupportedOperationException();
    }
}
