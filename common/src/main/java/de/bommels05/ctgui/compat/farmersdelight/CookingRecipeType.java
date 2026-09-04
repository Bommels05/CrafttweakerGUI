package de.bommels05.ctgui.compat.farmersdelight;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.FloatRecipeOption;
import de.bommels05.ctgui.api.option.IntegerRecipeOption;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import vectorwing.farmersdelight.client.recipebook.CookingPotRecipeBookTab;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.integration.emi.recipe.CookingPotEmiRecipe;

import java.util.ArrayList;

public class CookingRecipeType extends SupportedRecipeType<CookingPotRecipe> {
    private final IntegerRecipeOption<CookingPotRecipe> cookingTime = new IntegerRecipeOption<>(Component.translatable("ctgui.editing.options.cooking_time"), 1);
    private final FloatRecipeOption<CookingPotRecipe> experience = new FloatRecipeOption<>(Component.translatable("ctgui.editing.options.experience"), 0);

    public CookingRecipeType() {
        super(CraftTweakerGUI.rl("farmersdelight:cooking"));

        for(int row = 0; row < 2; row++) {
            for(int column = 0; column < 3; column++) {
                int i = row * 3 + column;

                addAreaEmptyRightClick(column * 18, row * 18, 17, 17, (r, am) -> {
                    ArrayList<Ingredient> ingredients = new ArrayList<>(r.getIngredients());
                    if (am.isEmpty() && i < ingredients.size()) {
                        ingredients.remove(i);
                    } else {
                        if (i < ingredients.size()) {
                            ingredients.set(i, am.ingredient());
                        } else if (!am.isEmpty()) {
                            ingredients.add(am.ingredient());
                        }
                    }
                    return new CookingPotRecipe(r.getGroup(), r.getRecipeBookTab(), CraftTweakerGUI.copyWithSize(ingredients, Ingredient.EMPTY),
                            r.getResultItem(regAccess()), r.getContainerOverride(), r.getExperience(), r.getCookTime());
                }, r -> {
                    return new AmountedIngredient(r.getIngredients().size() > i ? r.getIngredients().get(i) : Ingredient.EMPTY, 1);
                });
            }
        }

        addAreaScrollAmountEmptyRightClick(94, 9, 17, 17, (r, am) -> {
            return new CookingPotRecipe(r.getGroup(), r.getRecipeBookTab(), r.getIngredients(),
                    am.asStack(), r.getContainerOverride(), r.getExperience(), r.getCookTime());
        }, r -> {
            return AmountedIngredient.of(r.getResultItem(regAccess()));
        });
        addAreaEmptyRightClick(62, 38, 17, 17, (r, am) -> {
            return new CookingPotRecipe(r.getGroup(), r.getRecipeBookTab(), r.getIngredients(),
                    r.getResultItem(regAccess()), am.ensureAmount(1, 1).asStack(), r.getExperience(), r.getCookTime());
        }, r -> {
            return AmountedIngredient.of(r.getOutputContainer());
        });

        addOption(cookingTime, (r, cookingTime) -> {
            return new CookingPotRecipe(r.getGroup(), r.getRecipeBookTab(), r.getIngredients(),
                    r.getResultItem(regAccess()), r.getContainerOverride(), r.getExperience(), cookingTime);
        });
        addOption(experience, (r, experience) -> {
            return new CookingPotRecipe(r.getGroup(), r.getRecipeBookTab(), r.getIngredients(),
                    r.getResultItem(regAccess()), r.getContainerOverride(), experience, r.getCookTime());
        });
    }

    @Override
    public CookingPotRecipe onInitialize(CookingPotRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            cookingTime.set(200);
            return new CookingPotRecipe("", CookingPotRecipeBookTab.MISC, NonNullList.create(), ItemStack.EMPTY, ItemStack.EMPTY, 0, 200);
        }
        cookingTime.set(recipe.getCookTime());
        experience.set(recipe.getExperience());
        return null;
    }

    @Override
    public boolean isValid(CookingPotRecipe recipe) {
        return !recipe.getIngredients().isEmpty() && !recipe.getResultItem(regAccess()).isEmpty();
    }

    @Override
    public Object getEmiRecipe(CookingPotRecipe recipe) throws UnsupportedViewerException {
        return new CookingPotEmiRecipe(nullRl(), recipe.getIngredients().stream().map(EmiIngredient::of).toList(), EmiStack.of(recipe.getResultItem(regAccess())), EmiStack.of(recipe.getOutputContainer()), recipe.getCookTime(), recipe.getExperience());
    }

    @Override
    public String getCraftTweakerString(CookingPotRecipe recipe, String id) {
        return getCTJsonString(recipe, id);
    }
}
