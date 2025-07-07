package de.bommels05.ctgui.compat.minecraft.custom;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CompostingRecipeSerializer implements RecipeSerializer<CompostingRecipe> {

    @Override
    public CompostingRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new CompostingRecipe(id, Ingredient.fromJson(json.get("ingredient")), json.get("chance").getAsFloat());
    }

    @Override
    public CompostingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, CompostingRecipe recipe) {
        throw new UnsupportedOperationException();
    }
}
