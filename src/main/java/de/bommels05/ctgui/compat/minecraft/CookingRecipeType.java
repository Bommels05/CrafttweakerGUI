package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.FloatRecipeOption;
import de.bommels05.ctgui.api.option.IntegerRecipeOption;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.recipe.EmiCookingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public abstract class CookingRecipeType<R extends AbstractCookingRecipe> extends SupportedRecipeType<R> {

    private final IntegerRecipeOption<R> cookingTime = new IntegerRecipeOption<>(Component.translatable("ctgui.editing.options.cooking_time"), 1);
    private final FloatRecipeOption<R> experience = new FloatRecipeOption<>(Component.translatable("ctgui.editing.options.experience"), 0, 1000);
    private final CookingRecipeType.Constructor<R> constructor;
    private final int defaultCookingTime;
    private final String craftTweakerPrefix;

    public CookingRecipeType(ResourceLocation id, Constructor<R> constructor, int defaultCookingTime, String craftTweakerPrefix) {
        super(id);
        this.constructor = constructor;
        this.defaultCookingTime = defaultCookingTime;
        this.craftTweakerPrefix = craftTweakerPrefix;

        addAreaEmptyRightClick(0, 4, 17, 17, (r, am) -> {
            return constructor.construct(r.getGroup(), r.category(), am.ingredient(), r.getResultItem(regAccess()), r.getExperience(), r.getCookingTime());
        }, r -> {
            return new AmountedIngredient(r.getIngredients().get(0), 1);
        });
        addAreaScrollAmountEmptyRightClick(56, 0, 25, 25, (r, am) -> {
            return constructor.construct(r.getGroup(), r.category(), r.getIngredients().get(0), am.asStack(), r.getExperience(), r.getCookingTime());
        }, r -> {
            return AmountedIngredient.of(r.getResultItem(regAccess()));
        });
        addOption(cookingTime, (r, cookingTime) -> {
            return constructor.construct(r.getGroup(), r.category(), r.getIngredients().get(0), r.getResultItem(regAccess()), r.getExperience(), cookingTime);
        });
        addOption(experience, (r, experience) -> {
            return constructor.construct(r.getGroup(), r.category(), r.getIngredients().get(0), r.getResultItem(regAccess()), experience, r.getCookingTime());
        });
    }

    @Override
    public R onInitialize(R recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe != null) {
            cookingTime.set(recipe.getCookingTime());
            experience.set(recipe.getExperience());
            return null;
        } else {
            cookingTime.set(defaultCookingTime);
            return constructor.construct("", CookingBookCategory.MISC, Ingredient.EMPTY, ItemStack.EMPTY, 0.0F, defaultCookingTime);
        }
    }

    @Override
    public boolean isValid(R recipe) {
        return !recipe.getIngredients().get(0).isEmpty() && !recipe.getResultItem(regAccess()).isEmpty();
    }

    @Override
    public String getCraftTweakerString(R recipe, String id) {
        return craftTweakerPrefix + ".addRecipe(\"" + id +"\", " + getCTString(recipe.getResultItem(regAccess())) + ", " + getCTString(recipe.getIngredients().get(0)) + ", " + recipe.getExperience() + ", " + recipe.getCookingTime() + ");";
    }

    @FunctionalInterface
    public static interface Constructor<R extends AbstractCookingRecipe> {
        public R construct(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime);
    }
}
