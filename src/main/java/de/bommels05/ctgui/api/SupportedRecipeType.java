package de.bommels05.ctgui.api;

import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.util.ItemStackUtil;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import de.bommels05.ctgui.api.option.RecipeOption;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SupportedRecipeType<R extends Recipe<?>> {

    private final ResourceLocation id;
    private final List<Area<R>> areas = new ArrayList<>();
    private final List<RecipeOption<?, R>> options = new ArrayList<>();

    protected SupportedRecipeType(ResourceLocation id) {
        this.id = id;
    }

    public R onInitialize(R recipe) throws UnsupportedRecipeException {
        options.forEach(RecipeOption::reset);
        return null;
    }

    /**
     * Checks if this recipe can be saved and later exported to CraftTweaker or is still missing something
     * @param recipe The recipe
     * @return If this recipe is "finished" and ready to be saved and later exported
     */
    public abstract boolean isValid(R recipe);

    public abstract EmiRecipe getEmiRecipe(R recipe) throws UnsupportedViewerException;

    public abstract String getCraftTweakerString(R recipe, String id);


    public String getCraftTweakerRemoveString(R recipe, ResourceLocation id) {
        return "<recipetype:" + this.id + ">.removeByName(\"" + id + "\");\n";
    }

    /**
     * Not required, only used for auto generating recipe ids
     * Returns the recipe result item by default
     * @param recipe The recipe
     * @return The main output of the recipe or ItemStack.EMPTY
     */
    public ItemStack getMainOutput(R recipe) {
        return recipe.getResultItem(regAccess());
    }

    /**
     * Can be used when the emi recipes does not have a backing recipe or the backing recipe is of the wrong type
     * @return A function that manually gets the recipe from the emi recipe or null to use the backing recipe
     */
    public Function<EmiRecipe, R> getAlternativeEmiRecipeGetter() {
        return null;
    }

    protected String getCTString(List<Ingredient> ingredients) {
        StringBuilder string = new StringBuilder("[");
        int i = 1;
        for (Ingredient ingredient : ingredients) {
            string.append(getCTString(ingredient));
            if (i < ingredients.size()) {
                string.append(", ");
            }
            i++;
        }
        string.append("]");
        return string.toString();
    }

    protected String getCTString(ItemStack stack) {
        return ItemStackUtil.getCommandString(stack);
    }

    protected String getCTString(Ingredient ingredient) {
        return IIngredient.fromIngredient(ingredient).getCommandString();
    }

    public R onDragAndDrop(R recipe, int x, int y, AmountedIngredient ingredient) {
        for (Area<R> area : areas) {
            if (area.inside(x, y)) {
                return area.dragAndDropHandler.apply(recipe, ingredient);
            }
        }
        return null;
    }

    public R onClick(R recipe, int x, int y, boolean rightClick, RecipeEditScreen<R> screen) {
        for (Area<R> area : areas) {
            if (area.inside(x, y)) {
                R result = area.clickHandler.apply(recipe, rightClick);
                if (result == null) {
                    AmountedIngredient ingredient = area.stackSupplier.apply(recipe);
                    if (!ingredient.isEmpty()) {
                        screen.setDragged(ingredient);
                    }
                }
                return result;
            }
        }
        return null;
    }

    /*public R onMiddleClick(R recipe, int x, int y) {
        for (Area<R> area : areas) {
            if (area.inside(x, y)) {
                AmountedIngredient ingredient = area.stackSupplier.apply(recipe);

            }
        }
        return null;
    }*/

    public R onReleased(R recipe, int x, int y, boolean rightClick, RecipeEditScreen<R> screen) {
        if (!rightClick && screen.getDragged() != null) {
            R result = onDragAndDrop(recipe, x, y, screen.getDragged());
            screen.setDragged(null);
            return result;
        }
        return null;
    }

    public R onScroll(R recipe, int x, int y, boolean up) {
        for (Area<R> area : areas) {
            if (area.inside(x, y)) {
                return area.scrollHandler.apply(recipe, up);
            }
        }
        return null;
    }

    protected <T> void addOption(RecipeOption<T, R> option, BiFunction<R, T, R> handler) {
        option.addListener(handler);
        options.add(option);
    }

    protected void addArea(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler, Function<R, AmountedIngredient> stackSupplier) {
        addArea(x, y, width, height, dragAndDropHandler, stackSupplier, (r, rightClick) -> null);
    }

    protected void addArea(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler, Function<R, AmountedIngredient> stackSupplier,
                           BiFunction<R, Boolean, R> clickHandler) {
        addArea(x, y, width, height, dragAndDropHandler, stackSupplier, clickHandler, (r, up) -> null);
    }

    protected void addArea(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler, Function<R, AmountedIngredient> stackSupplier,
                           BiFunction<R, Boolean, R> clickHandler, BiFunction<R, Boolean, R> scrollHandler) {
        areas.add(new Area<>(x, y, width, height, dragAndDropHandler, stackSupplier, clickHandler, scrollHandler));
    }

    protected void addAreaEmptyRightClick(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler, Function<R, AmountedIngredient> stackSupplier) {
        addAreaEmptyRightClick(x, y, width, height, dragAndDropHandler, stackSupplier, (r, up) -> null);
    }

    protected void addAreaEmptyRightClick(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler, Function<R, AmountedIngredient> stackSupplier,
                                          BiFunction<R, Boolean, R> scrollHandler) {
        addArea(x, y, width, height, dragAndDropHandler, stackSupplier, (recipe, rightClick) -> rightClick ? dragAndDropHandler.apply(recipe, AmountedIngredient.empty()) : null, scrollHandler);
    }

    protected void addAreaScrollAmountEmptyRightClick(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler, Function<R, AmountedIngredient> stackSupplier) {
        addAreaEmptyRightClick(x, y, width, height, dragAndDropHandler, stackSupplier, (recipe, up) -> {
            AmountedIngredient ingredient = stackSupplier.apply(recipe);
            return dragAndDropHandler.apply(recipe, ingredient.withAmount(getScrollStackSize(ingredient.amount(), up)));
        });
    }

    protected int getScrollStackSize(int originalSize, boolean up) {
        return Math.min(Math.max(originalSize + ((up ? 1 : -1) * (Screen.hasShiftDown() ? (originalSize == 1 ? 15 : 16) : 1)), 1), 64);
    }

    protected void addArea(Area<R> area) {
        areas.add(area);
    }

    protected void clearAreas() {
        areas.clear();
    }

    protected RegistryAccess regAccess() {
        return Minecraft.getInstance().level.registryAccess();
    }

    protected void error(Component message) {
        new UnsupportedRecipeException(message).display();
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<RecipeOption<?, R>> getOptions() {
        return options;
    }

    public record Area<R extends Recipe<?>>(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler,
                                            Function<R, AmountedIngredient> stackSupplier,
                                            BiFunction<R, Boolean, R> clickHandler, BiFunction<R, Boolean, R> scrollHandler) {

        public boolean inside(int x, int y) {
            return (this.x <= x && (this.x + this.width) >= x) && (this.y <= y && (this.y + this.height) >= y);
        }

    }
}
