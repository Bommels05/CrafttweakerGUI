package de.bommels05.ctgui.compat.minecraft.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

public class TagRecipeSerializer implements RecipeSerializer<TagRecipe> {

    private final Codec<TagRecipe> codec;

    public TagRecipeSerializer() {
        this.codec = RecordCodecBuilder.create(
                recipe ->
                    recipe.group(
                            Codec.BOOL.fieldOf("item").forGetter(r -> r.item),
                            ResourceLocation.CODEC.fieldOf("id").forGetter(r -> r.id),
                            Codec.list(ItemStack.SINGLE_ITEM_CODEC).fieldOf("items").orElse(List.of()).forGetter(r -> r.items != null ? r.items : List.of()),
                            Codec.list(ResourceLocation.CODEC).fieldOf("itemTags").orElse(List.of()).forGetter(r -> r.itemTags != null ? r.itemTags.stream().map(TagKey::location).toList() : List.of()),
                            Codec.list(BuiltInRegistries.FLUID.byNameCodec()).fieldOf("fluids").orElse(List.of()).forGetter(r -> r.fluids != null ? r.fluids : List.of()),
                            Codec.list(ResourceLocation.CODEC).fieldOf("fluidTags").orElse(List.of()).forGetter(r -> r.fluidTags != null ? r.fluidTags.stream().map(TagKey::location).toList() : List.of())
                    ).apply(recipe, (item, id, items, itemTags, fluids, fluidTags) -> {
                        return item ? new TagRecipe(TagKey.create(Registries.ITEM, id), items, itemTags.stream().map(tag -> TagKey.create(Registries.ITEM, tag)).toList()) :
                                new TagRecipe(fluidTags.stream().map(tag -> TagKey.create(Registries.FLUID, tag)).toList(), fluids, TagKey.create(Registries.FLUID, id));
                    })
        );
    }

    @Override
    public Codec<TagRecipe> codec() {
        return codec;
    }

    @Override
    public TagRecipe fromNetwork(FriendlyByteBuf pBuffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, TagRecipe pRecipe) {
        throw new UnsupportedOperationException();
    }
}
