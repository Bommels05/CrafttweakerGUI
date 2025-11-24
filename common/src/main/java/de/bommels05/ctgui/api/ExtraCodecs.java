package de.bommels05.ctgui.api;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.EitherCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Has an Ingredient Codec with NBT support for item values
 */
public class ExtraCodecs {

    public static final Codec<TagKey<Item>> ITEM_TAG_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(tag -> tag)
    ).apply(instance, tag -> tag));

    public static final Codec<Ingredient> NBT_INGREDIENT_CODEC = new EitherCodec<>(ItemStack.CODEC, ITEM_TAG_CODEC).xmap(either -> {
        return either.map(Ingredient::of, Ingredient::of);
    }, ingredient -> {
        if (ingredient.isEmpty()) {
            return Either.left(ItemStack.EMPTY);
        } else {
            Ingredient.Value value = ingredient.values[0];
            if (value instanceof Ingredient.ItemValue item) {
                return Either.left(item.item());
            } else if (value instanceof Ingredient.TagValue tag) {
                return Either.right(tag.tag());
            }
            throw new IllegalArgumentException("Invalid ingredient value: " + value);
        }
    });

    public static final Codec<TagKey<?>> TAG_KEY_CODEC = RecordCodecBuilder.create(tagKey ->
        tagKey.group(
                ResourceLocation.CODEC.fieldOf("location").forGetter(TagKey::location),
                ResourceLocation.CODEC.fieldOf("registry").forGetter(t -> t.registry().location())
        ).apply(tagKey, (location, registry) ->
            TagKey.create(ResourceKey.createRegistryKey(registry), location)
        )
    );

}
