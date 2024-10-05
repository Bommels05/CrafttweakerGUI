package de.bommels05.ctgui.api;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

/**
 * Has an Ingredient Codec with NBT support for item values
 */
public class FlexibleNBTIngredient {

    public static final Codec<ItemStack> OPTIONAL_COUNT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(ItemStack::getItemHolder),
            Codec.INT.optionalFieldOf("count").forGetter(stack -> Optional.of(stack.getCount())),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(stack -> Optional.ofNullable(stack.getTag()))
    ).apply(instance, (item, count, tag) -> new ItemStack(item, count.orElse(1), tag)));

    public static final Codec<TagKey<Item>> ITEM_TAG_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(tag -> tag)
    ).apply(instance, tag -> tag));

    public static final Codec<Ingredient> CODEC = ExtraCodecs.either(OPTIONAL_COUNT_CODEC, ITEM_TAG_CODEC).xmap(either -> {
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

}
