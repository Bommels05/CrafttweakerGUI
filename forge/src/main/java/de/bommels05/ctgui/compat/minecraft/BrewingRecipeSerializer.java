package de.bommels05.ctgui.compat.minecraft;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

public class BrewingRecipeSerializer implements RecipeSerializer<BrewingRecipe> {

    @Override
    public BrewingRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new BrewingRecipe(id, Ingredient.fromJson(json.get("input")), Ingredient.fromJson(json.get("reagent")), ShapedRecipe.itemStackFromJson(json.get("output").getAsJsonObject()));
    }

    @Override
    public @Nullable BrewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, BrewingRecipe recipe) {
        throw new UnsupportedOperationException();
    }
}
