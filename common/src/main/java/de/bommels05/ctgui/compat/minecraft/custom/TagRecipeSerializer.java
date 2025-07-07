package de.bommels05.ctgui.compat.minecraft.custom;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class TagRecipeSerializer implements RecipeSerializer<TagRecipe> {

    @Override
    public TagRecipe fromJson(ResourceLocation id, JsonObject json) {
        if (json.get("item").getAsInt() == 1) { //JSON to NBT messes the boolean up
            return new TagRecipe(TagKey.create(Registries.ITEM, new ResourceLocation(json.get("id").getAsString())),
                    json.getAsJsonArray("items").asList().stream().map(JsonElement::getAsJsonObject).map(ShapedRecipe::itemStackFromJson).toList(),
                    json.getAsJsonArray("itemTags").asList().stream().map(JsonElement::getAsString).map(ResourceLocation::new).map(tag -> TagKey.create(Registries.ITEM, tag)).toList());
        } else {
            return new TagRecipe(json.getAsJsonArray("fluidTags").asList().stream().map(JsonElement::getAsString).map(ResourceLocation::new).map(tag -> TagKey.create(Registries.FLUID, tag)).toList(),
                    json.getAsJsonArray("fluids").asList().stream().map(JsonElement::getAsString).map(ResourceLocation::new).map(BuiltInRegistries.FLUID::get).toList(),
                    TagKey.create(Registries.FLUID, new ResourceLocation(json.get("id").getAsString())));
        }
    }

    @Override
    public TagRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, TagRecipe recipe) {
        throw new UnsupportedOperationException();
    }
}
