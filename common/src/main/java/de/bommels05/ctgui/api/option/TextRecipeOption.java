package de.bommels05.ctgui.api.option;

import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class TextRecipeOption<R extends Recipe<?>> implements RecipeOption<String, R> {

    private EditBox editBox;
    private BiFunction<R, String, R> listener;
    private final Predicate<String> validator;
    private final Component tooltip;
    private String value;

    public TextRecipeOption(Component tooltip, Predicate<String> validator) {
        this.tooltip = tooltip;
        this.validator = validator;
    }

    public TextRecipeOption(Component tooltip) {
        this(tooltip, value -> true);
    }

    @Override
    public void addToScreen(RecipeEditScreen<?> screen, int x, int y) {
        editBox = new EditBox(screen.getFont(), x, y, 100, 18, Component.empty());
        editBox.setMaxLength(256);
        editBox.setTooltip(Tooltip.create(tooltip));
        editBox.setValue(value);
        editBox.setResponder(value -> screen.handleRecipeOption(value, listener));
        editBox.setFilter(validator);
        screen.addRenderableWidget(editBox);
    }

    @Override
    public void addListener(BiFunction<R, String, R> listener) {
        this.listener = listener;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public String get() {
        if (editBox == null) {
            return value;
        }
        return editBox.getValue();
    }

    @Override
    public void set(String value) {
        if (editBox == null) {
            this.value = value;
        } else {
            editBox.setValue(value);
        }
    }

    @Override
    public void reset() {
        editBox = null;
        value = "";
    }
}
