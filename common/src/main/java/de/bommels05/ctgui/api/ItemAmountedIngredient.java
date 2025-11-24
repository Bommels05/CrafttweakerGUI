package de.bommels05.ctgui.api;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemAmountedIngredient extends SpecialAmountedIngredient<ItemStack, Item> {

    protected ItemAmountedIngredient(ItemStack stack, TagKey<Item> tag, int amount) {
        super(stack == null || stack.getCount() == amount ? stack : stack.copyWithCount(amount), tag, amount);
    }

    public ItemAmountedIngredient(ItemStack stack, int amount) {
        this(stack, null, amount);
    }

    public ItemAmountedIngredient(ItemStack stack) {
        this(stack, stack.getCount());
    }

    public ItemAmountedIngredient(TagKey<Item> tag, int amount) {
        this(null, tag, amount);
    }

    @Override
    public ItemAmountedIngredient withAmount(int amount) {
        return new ItemAmountedIngredient(getStack(), getTag(), amount);
    }

    @Override
    public List<ItemStack> getStacks() {
        if (isTag()) {
            return getStacksInternal().stream().map(stack -> stack.copyWithCount(getAmount())).toList();
        }
        return getStacksInternal();
    }

    @Override
    public Item getStackAsType() {
        return getStack().getItem();
    }

    @Override
    public Registry<Item> getRegistry() {
        return BuiltInRegistries.ITEM;
    }

    @Override
    public boolean isStackEmpty() {
        return getStack().isEmpty();
    }
}
