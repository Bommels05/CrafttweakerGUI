package de.bommels05.ctgui.compat.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.BooleanRecipeOption;
import dev.emi.emi.recipe.EmiShapedRecipe;
import dev.emi.emi.recipe.EmiShapelessRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.Optional;

public class CraftingRecipeType extends SupportedRecipeType<CraftingRecipe> {

    private final BooleanRecipeOption<CraftingRecipe> shapeless = new BooleanRecipeOption<>(Component.translatable("ctgui.editing.options.shapeless"));

    public CraftingRecipeType() {
        super(new ResourceLocation("minecraft:crafting"));
        for (int row = 0; row < 3; row++) {
            for (int i = 0; i < 3; i++) {
                int index = i + row * 3;
                addAreaEmptyRightClick(i * 18, row * 18, 17, 17, (r, am) -> {
                    if (r instanceof ShapedRecipe recipe) {
                        return setIngredient(recipe, index, am.ensureAmount(1, 1).ingredient());
                    }
                    if (r instanceof ShapelessRecipe recipe) {
                        return setIngredient(recipe, index, am.ensureAmount(1, 1).ingredient());
                    }
                    return null;
                }, r -> {
                    if (r instanceof ShapedRecipe || r instanceof ShapelessRecipe) {
                        Ingredient ingredient = r.getIngredients().size() > index ? r.getIngredients().get(index) : Ingredient.EMPTY;
                        return new AmountedIngredient(ingredient, 1);
                    }
                    return AmountedIngredient.empty();
                });
            }
        }
        addAreaScrollAmountEmptyRightClick(92, 14, 25, 25, (r, am) -> {
            if (r instanceof ShapedRecipe recipe) {
                return setOutput(recipe, am.asStack());
            }
            if (r instanceof ShapelessRecipe recipe) {
                return setOutput(recipe, am.asStack());
            }
            return null;
        }, r -> {
            if (r instanceof ShapedRecipe || r instanceof ShapelessRecipe) {
                return AmountedIngredient.of(r.getResultItem(regAccess()));
            }
            return AmountedIngredient.empty();
        });
        addOption(shapeless, (r, value) -> {
            if (value) {
                if (r instanceof ShapedRecipe recipe) {
                    return new ShapelessRecipe(recipe.getGroup(), recipe.category(), recipe.getResultItem(regAccess()), NonNullList.of(null, recipe.getIngredients().stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new)));
                }
            } else {
                if (r instanceof ShapelessRecipe recipe) {
                    return new ShapedRecipe(recipe.getGroup(), recipe.category(), new ShapedRecipePattern(3, 3, expandIngredients(recipe.getIngredients(), 3, 3), Optional.empty()), recipe.getResultItem(regAccess()));
                }
            }
            return null;
        });
    }

    @Override
    public CraftingRecipe onInitialize(CraftingRecipe r) throws UnsupportedRecipeException {
        super.onInitialize(r);
        if (r == null) {
            return new ShapedRecipe("", CraftingBookCategory.MISC, new ShapedRecipePattern(3, 3, NonNullList.withSize(9, Ingredient.EMPTY), Optional.empty()), ItemStack.EMPTY);
        }
        if (r instanceof ShapedRecipe recipe) {
            return new ShapedRecipe(recipe.getGroup(), recipe.category(), new ShapedRecipePattern(3, 3, expandIngredients(recipe.getIngredients(), recipe.getWidth(), recipe.getHeight()), Optional.empty()), recipe.getResultItem(regAccess()));
        }
        if (r instanceof ShapelessRecipe) {
            shapeless.set(true);
            //Forces the anti centering mixins to apply in jei
            return r;
        }
        ShapedRecipe recipe = CraftTweakerGUI.getLoaderUtils().tryGetFromMekanismRecipe(r);
        if (recipe != null) {
            return onInitialize(recipe);
        }
        throw new UnsupportedRecipeException();
    }

    @Override
    public boolean isValid(CraftingRecipe recipe) {
        return !recipe.getResultItem(regAccess()).isEmpty() && recipe.getIngredients().stream().anyMatch(i -> !i.isEmpty());
    }

    @Override
    public Object getEmiRecipe(CraftingRecipe r) throws UnsupportedViewerException {
        if (r instanceof ShapedRecipe recipe) {
            return new EmiShapedRecipe(recipe);
        }
        if (r instanceof ShapelessRecipe recipe) {
            return new EmiShapelessRecipe(recipe);
        }
        throw new IllegalStateException("Unsupported recipe implementation was not caught by onInitialize");
    }

    @Override
    public String getCraftTweakerString(CraftingRecipe r, String id) {
        if (r instanceof ShapedRecipe recipe) {
            //Enable Mekanism support (Energy, Chemical and more transfer) for recipes with an mekanism output
            if (CraftTweakerGUI.getLoaderUtils().isModLoaded("mekanism") && BuiltInRegistries.ITEM.getKey(recipe.getResultItem(regAccess()).getItem()).getNamespace().equals("mekanism")) {
                return CraftTweakerGUI.getLoaderUtils().getMekanismCraftTweakerString(recipe, id);
            } else {
                return "craftingTable.addShaped(\"" + id + "\", " + getCTString(recipe.getResultItem(regAccess())) + ", " + getGrid(recipe) + ");";
            }
        }
        if (r instanceof ShapelessRecipe recipe) {
            return "craftingTable.addShapeless(\"" + id + "\", " + getCTString(recipe.getResultItem(regAccess())) + ", " + getCTString(recipe.getIngredients()) + ");";
        }
        throw new IllegalStateException("Unsupported recipe implementation was not caught by onInitialize");
    }

    private String getGrid(ShapedRecipe recipe) {
        int width = 0, height = 0, i = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (!ingredient.isEmpty()) {
                width = Math.max((i % 3) + 1, width);
                height = (i / 3) + 1;
            }
            i++;
        }
        StringBuilder grid = new StringBuilder("[\n    ");
        for (int y = 0; y < height; y++) {
            grid.append("[");
            for (int x = 0; x < width; x++) {
                grid.append(getCTString(recipe.getIngredients().get(x + y * 3)));
                if (x + 1 < width) {
                    grid.append(", ");
                }
            }
            grid.append("]" + (y + 1 < height ? ",\n    " : ""));
        }
        grid.append("]");
        return grid.toString();
    }

    private ShapedRecipe setIngredient(ShapedRecipe recipe, int index, Ingredient ingredient) {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
        int i = 0;
        for (Ingredient ingredient2 : recipe.getIngredients()) {
            ingredients.set(i, ingredient2);
            i++;
        }
        ingredients.set(index, ingredient);
        //Size is always 3x3 and shrunk down when converting to ZenScript
        return new ShapedRecipe(recipe.getGroup(), recipe.category(), new ShapedRecipePattern(3, 3, ingredients,
                Optional.empty()), recipe.getResultItem(regAccess()));
    }

    private ShapelessRecipe setIngredient(ShapelessRecipe recipe, int index, Ingredient ingredient) {
        ArrayList<Ingredient> ingredients = new ArrayList<>(recipe.getIngredients());
        if (ingredient.isEmpty() && index < ingredients.size()) {
            ingredients.remove(index);
        } else {
            if (index < ingredients.size()) {
                ingredients.set(index, ingredient);
            } else if (!ingredient.isEmpty()) {
                ingredients.add(ingredient);
            }
        }
        return new ShapelessRecipe(recipe.getGroup(), recipe.category(), recipe.getResultItem(regAccess()), NonNullList.of(null, ingredients.toArray(Ingredient[]::new)));
    }

    private ShapedRecipe setOutput(ShapedRecipe recipe, ItemStack stack) {
        return new ShapedRecipe(recipe.getGroup(), recipe.category(), new ShapedRecipePattern(recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), Optional.empty()), stack);
    }

    private ShapelessRecipe setOutput(ShapelessRecipe recipe, ItemStack stack) {
        return new ShapelessRecipe(recipe.getGroup(), recipe.category(), stack, recipe.getIngredients());
    }

    private NonNullList<Ingredient> expandIngredients(NonNullList<Ingredient> ingredients, int width, int height) {
        NonNullList<Ingredient> newIngredients = NonNullList.withSize(9, Ingredient.EMPTY);
        int oldIndex = 0;
        int newIndex = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (y < height && x < width) {
                    if (oldIndex >= ingredients.size()) {
                        return newIngredients;
                    }
                    newIngredients.set(newIndex, ingredients.get(oldIndex));
                    oldIndex++;
                }
                newIndex++;
            }
        }
        return newIngredients;
    }

}
