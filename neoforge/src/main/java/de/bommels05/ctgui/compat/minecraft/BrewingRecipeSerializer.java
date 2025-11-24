package de.bommels05.ctgui.compat.minecraft;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.bommels05.ctgui.api.ExtraCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class BrewingRecipeSerializer implements RecipeSerializer<BrewingRecipe> {

    private final MapCodec<BrewingRecipe> codec;

    public BrewingRecipeSerializer() {
        this.codec = RecordCodecBuilder.mapCodec(recipe ->
            recipe.group(ExtraCodecs.NBT_INGREDIENT_CODEC.fieldOf("input").forGetter(r -> r.getInput()),
                    ExtraCodecs.NBT_INGREDIENT_CODEC.fieldOf("reagent").forGetter(r -> r.getIngredient()),
                    ItemStack.CODEC.fieldOf("output").forGetter(r -> r.getOutput())).apply(recipe, BrewingRecipe::new)
        );
    }

    @Override
    public MapCodec<BrewingRecipe> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> streamCodec() {
        throw new UnsupportedOperationException();
    }
}
