package de.bommels05.ctgui.api;

import com.google.common.base.Preconditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemAmountedIngredient extends SpecialAmountedIngredient<ItemStack, Item> {

    protected ItemAmountedIngredient(ItemStack stack, TagKey<Item> tag, int amount) {
        super(stack, tag, amount);
    }

    public ItemAmountedIngredient(ItemStack stack, int amount) {
        super(stack, amount);
    }

    public ItemAmountedIngredient(ItemStack stack) {
        super(stack);
    }

    public ItemAmountedIngredient(TagKey<Item> tag, int amount) {
        super(tag, amount);
    }

    @Override
    public ItemAmountedIngredient withAmount(int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        return new ItemAmountedIngredient(getStack(), getTag(), amount);
    }

    @Override
    public List<ItemStack> getStacks() {
        if (shouldUseAmount()) {
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
