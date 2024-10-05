package de.bommels05.ctgui.compat.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.bommels05.ctgui.api.FlexibleNBTIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class BrewingRecipeSerializer implements RecipeSerializer<BrewingRecipe> {

    private final Codec<BrewingRecipe> codec;

    public BrewingRecipeSerializer() {
        this.codec = RecordCodecBuilder.create(recipe ->
            recipe.group(FlexibleNBTIngredient.CODEC.fieldOf("input").forGetter(r -> r.getInput()),
                    FlexibleNBTIngredient.CODEC.fieldOf("reagent").forGetter(r -> r.getIngredient()),
                    ItemStack.CODEC.fieldOf("output").forGetter(r -> r.getOutput())).apply(recipe, BrewingRecipe::new)
        );
    }

    @Override
    public Codec<BrewingRecipe> codec() {
        return codec;
    }

    @Override
    public BrewingRecipe fromNetwork(FriendlyByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, BrewingRecipe recipe) {
        throw new UnsupportedOperationException();
    }
}
