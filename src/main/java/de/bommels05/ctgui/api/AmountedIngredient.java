package de.bommels05.ctgui.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

public record AmountedIngredient(Ingredient ingredient, int amount) {

    public AmountedIngredient withAmount(int amount) {
        return new AmountedIngredient(ingredient, amount);
    }

    public AmountedIngredient withIngredient(Ingredient ingredient) {
        return new AmountedIngredient(ingredient, amount);
    }

    public AmountedIngredient plusAmount(int amount) {
        return new AmountedIngredient(ingredient, this.amount + amount);
    }

    public AmountedIngredient minusAmount(int amount) {
        return new AmountedIngredient(ingredient, this.amount - amount);
    }

    public AmountedIngredient ensureAmount(int min, int max) {
        int amount = Math.min(max, Math.max(min, this.amount));
        if (amount != this.amount) {
            return new AmountedIngredient(Ingredient.of(Arrays.stream(ingredient.getValues()).filter(value -> value instanceof Ingredient.ItemValue).
                    map(value -> ((Ingredient.ItemValue) value).item().copyWithCount(amount))), amount);
        }
        return this;
    }

    public ItemStack asStack() {
        ItemStack stack = ingredient.getItems().length >= 1 ? ingredient.getItems()[0] : ItemStack.EMPTY;
        return stack.copyWithCount(Math.min(stack.getMaxStackSize(), amount));
    }

    public boolean isTag() {
        return ingredient.getValues().length == 1 && ingredient.getValues()[0] instanceof Ingredient.TagValue;
    }

    public boolean isEmpty() {
        return ingredient == Ingredient.EMPTY;
    }

    public static AmountedIngredient of(ItemStack stack) {
        return new AmountedIngredient(Ingredient.of(stack), stack.getCount());
    }

    public static AmountedIngredient empty() {
        return new AmountedIngredient(Ingredient.EMPTY, 1);
    }

}
