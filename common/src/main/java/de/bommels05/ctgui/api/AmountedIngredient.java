package de.bommels05.ctgui.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

/**
 * Represents an {@link Ingredient} with an amount
 * @param ingredient The ingredient
 * @param amount The amount of the ingredient
 */
public record AmountedIngredient(Ingredient ingredient, int amount) {

    /**
     * Returns a new AmountedIngredient with the given amount
     * @param amount The new amount
     * @return The AmountedIngredient with the given amount
     */
    public AmountedIngredient withAmount(int amount) {
        return new AmountedIngredient(ingredient, amount);
    }

    /**
     * Returns a new AmountedIngredient with the given ingredient
     * @param ingredient The new ingredient
     * @return The AmountedIngredient with the given ingredient
     */
    public AmountedIngredient withIngredient(Ingredient ingredient) {
        return new AmountedIngredient(ingredient, amount);
    }

    /**
     * Returns a new AmountedIngredient with the current amount plus the given amount
     * @param amount The amount to add
     * @return The AmountedIngredient with the new amount
     */
    public AmountedIngredient plusAmount(int amount) {
        return new AmountedIngredient(ingredient, this.amount + amount);
    }

    /**
     * Returns a new AmountedIngredient with the current amount minus the given amount
     * @param amount The amount to remove
     * @return The AmountedIngredient with the new amount
     */
    public AmountedIngredient minusAmount(int amount) {
        return new AmountedIngredient(ingredient, this.amount - amount);
    }

    /**
     * Returns a AmountedIngredient with the amount in the given range
     * @param min The minimum amount (inclusive)
     * @param max The maximum amount (inclusive)
     * @return This if already in rage or a new AmountedIngredient with the amount in the given range
     */
    public AmountedIngredient ensureAmount(int min, int max) {
        int amount = Math.min(max, Math.max(min, this.amount));
        if (amount != this.amount) {
            return new AmountedIngredient(Ingredient.of(Arrays.stream(ingredient.values).filter(value -> value instanceof Ingredient.ItemValue).
                    map(value -> ((Ingredient.ItemValue) value).item().copyWithCount(amount))), amount);
        }
        return this;
    }

    /**
     * Returns a {@link ItemStack} with the first item of the {@link Ingredient} and the amount of this AmountedIngredient
     * @return This AmountedIngredient as a {@link ItemStack}
     */
    public ItemStack asStack() {
        ItemStack stack = ingredient.getItems().length >= 1 ? ingredient.getItems()[0] : ItemStack.EMPTY;
        return stack.copyWithCount(Math.min(stack.getMaxStackSize(), amount));
    }

    /**
     * Returns if the {@link Ingredient} is a tag ingredient (If it has one {@link Ingredient.TagValue})
     * @return If the {@link Ingredient} is a tag ingredient
     */
    public boolean isTag() {
        return ingredient.values.length == 1 && ingredient.values[0] instanceof Ingredient.TagValue;
    }

    /**
     * Returns if the {@link Ingredient} is empty
     * @return If the {@link Ingredient} is empty
     */
    public boolean isEmpty() {
        return ingredient == Ingredient.EMPTY;
    }

    /**
     * Returns a new AmountedIngredient with the amount and an {@link Ingredient} of the given {@link ItemStack}
     * @param stack The {@link ItemStack} to create the AmountedIngredient from
     * @return A new AmountedIngredient of the given {@link ItemStack}
     */
    public static AmountedIngredient of(ItemStack stack) {
        return new AmountedIngredient(Ingredient.of(stack), stack.getCount());
    }

    /**
     * Returns a new AmountedIngredient with an empty {@link Ingredient} and an amount of 1
     * @return A new empty AmountedIngredient
     */
    public static AmountedIngredient empty() {
        return new AmountedIngredient(Ingredient.EMPTY, 1);
    }

}
