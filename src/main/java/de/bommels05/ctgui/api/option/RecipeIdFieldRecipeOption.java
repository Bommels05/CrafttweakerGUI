package de.bommels05.ctgui.api.option;

import de.bommels05.ctgui.screen.BetterCheckBox;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Used to replace the function of the recipe id field
 * So this can only be used by recipes that don't have a recipe id
 */
public class RecipeIdFieldRecipeOption<R extends Recipe<?>> implements RecipeOption<String, R> {

    private final Component tooltip;
    private BiFunction<R, String, R> listener;
    private Predicate<String> filter;
    private EditBox box;
    private RecipeEditScreen<?> screen;
    private String value;

    public RecipeIdFieldRecipeOption(Component tooltip, Predicate<String> filter) {
        this.tooltip = tooltip;
        this.filter = filter;
    }

    @Override
    public void addToScreen(RecipeEditScreen<?> screen, int x, int y) {
        this.screen = screen;
    }

    public void supplyEditBox(EditBox box, Consumer<String> recipeIdSetter) {
        this.box = box;
        box.setValue(value);
        box.setMaxLength(256);
        box.setTooltip(Tooltip.create(tooltip));
        box.setFilter(filter);
        box.setResponder(value -> {
            screen.handleRecipeOption(value, listener);
            recipeIdSetter.accept(value);
        });
    }

    @Override
    public void addListener(BiFunction<R, String, R> listener) {
        this.listener = listener;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public String get() {
        if (box == null) {
            return value;
        }
        return box.getValue();
    }

    @Override
    public void set(String value) {
        if (box == null) {
            this.value = value;
        } else {
            box.setValue(value);
        }
    }

    @Override
    public void reset() {
        box = null;
        value = "";
    }

}
