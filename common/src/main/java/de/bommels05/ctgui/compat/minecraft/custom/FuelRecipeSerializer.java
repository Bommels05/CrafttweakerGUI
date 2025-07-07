package de.bommels05.ctgui.compat.minecraft.custom;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FuelRecipeSerializer implements RecipeSerializer<FuelRecipe> {

    @Override
    public FuelRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new FuelRecipe(id, Ingredient.fromJson(json.get("ingredient")), json.get("burnTime").getAsInt());
    }

    @Override
    public FuelRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, FuelRecipe recipe) {
        throw new UnsupportedOperationException();
    }
}
