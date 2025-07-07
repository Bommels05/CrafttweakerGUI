package de.bommels05.ctgui.compat.minecraft.custom;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class InfoRecipeSerializer implements RecipeSerializer<InfoRecipe> {

    @Override
    public InfoRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new InfoRecipe(id, Ingredient.fromJson(json.get("ingredient")), json.get("text").getAsString());
    }

    @Override
    public InfoRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, InfoRecipe recipe) {
        throw new UnsupportedOperationException();
    }
}
