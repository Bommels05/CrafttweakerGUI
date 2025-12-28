package de.bommels05.ctgui.compat.minecraft.custom;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.bommels05.ctgui.api.ExtraCodecs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;
import java.util.stream.Stream;

public class TagRecipeSerializer implements RecipeSerializer<TagRecipe<?>> {

    private final MapCodec<TagRecipe<?>> codec;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public TagRecipeSerializer() {
        MapCodec<TagRecipe<?>> oldCodec = RecordCodecBuilder.mapCodec(recipe ->
                recipe.group(
                        ResourceLocation.CODEC.fieldOf("id").forGetter(r -> r.id),
                        Codec.list(ItemStack.SINGLE_ITEM_CODEC).fieldOf("items").orElse(List.of()).forGetter(r -> null),
                        Codec.list(ResourceLocation.CODEC).fieldOf("itemTags").orElse(List.of()).forGetter(r -> null)
                ).apply(recipe, (id, items, itemTags) -> {
                    return new TagRecipe<>(id, BuiltInRegistries.ITEM, itemTags.stream().map(r -> TagKey.create(Registries.ITEM, r)).toList(),
                            items.stream().map(ItemStack::getItem).toList());
                })
        );
        MapCodec<TagRecipe> newCodec = RecordCodecBuilder.mapCodec(recipe ->
                recipe.group(
                        ResourceLocation.CODEC.fieldOf("id").forGetter(r -> r.id),
                        ResourceLocation.CODEC.fieldOf("registry").forGetter(r -> r.registry.key().location()),
                        Codec.list(ExtraCodecs.TAG_KEY_CODEC).fieldOf("tags").orElse(List.of()).forGetter(r -> r.tags != null ? r.tags : List.of()),
                        Codec.list(ResourceLocation.CODEC).fieldOf("entries").forGetter(r -> r.entries.stream().map(r.registry::getKey).toList())
                ).apply(recipe, (id, registry, tags, entries) -> {
                    Registry<?> r = BuiltInRegistries.REGISTRY.get(registry);
                    return TagRecipe.cast(id, r, tags, entries.stream().map(r::get).toList());
                })
        );

        this.codec = new MapCodec<>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }

            @Override
            public <T> DataResult<TagRecipe<?>> decode(DynamicOps<T> ops, MapLike<T> input) {
                if (input.get("item") != null) {
                    return oldCodec.decode(ops, input);
                }
                return ((MapCodec<TagRecipe<?>>) (MapCodec<?>) newCodec).decode(ops, input);
            }

            @Override
            public <T> RecordBuilder<T> encode(TagRecipe<?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return newCodec.encode(input, ops, prefix);
            }
        };
    }

    @Override
    public MapCodec<TagRecipe<?>> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TagRecipe<?>> streamCodec() {
        throw new UnsupportedOperationException();
    }
}
