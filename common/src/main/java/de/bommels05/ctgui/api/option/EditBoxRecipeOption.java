package de.bommels05.ctgui.api.option;

import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public abstract class EditBoxRecipeOption<T extends Comparable<T>, R extends Recipe<?>> implements AdvancedRecipeOption<T, R> {
    private EditBox editBox;
    private BiFunction<R, T, R> listener;
    private final Component tooltip;
    private BiPredicate<R, T> advancedFilter;
    protected final T min;
    protected final T max;
    protected T value;

    public EditBoxRecipeOption(Component tooltip, T min, T max) {
        this.tooltip = tooltip;
        this.min = min;
        this.max = max;
    }

    @Override
    public void addToScreen(RecipeEditScreen<R> screen, int x, int y) {
        editBox = new EditBox(screen.getFont(), x, y, 100, 18, Component.empty());
        editBox.setMaxLength(256);
        editBox.setTooltip(Tooltip.create(tooltip));
        editBox.setValue(String.valueOf(value));
        editBox.setResponder(value -> {
            editBox.setTextColor(14737632);

            try {
                T t = parse(value);
                if (t.compareTo(min) < 0) {
                    editBox.setValue(String.valueOf(min));
                    return;
                }
                if (t.compareTo(max) > 0) {
                    editBox.setValue(String.valueOf(max));
                    return;
                }
                if (!screen.handleRecipeOption(parse(value), listener, advancedFilter)) {
                    editBox.setTextColor(0xFFC9071F);
                }
            } catch (NumberFormatException e) {
                editBox.setTextColor(0xFFC9071F);
            }
        });
        screen.addRenderableWidget(editBox);
    }

    @Override
    public void addListener(BiFunction<R, T, R> listener) {
        this.listener = listener;
        this.advancedFilter = (r, t) -> true;
    }

    @Override
    public void addAdvancedFilter(BiPredicate<R, T> advancedFilter) {
        this.advancedFilter = advancedFilter;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public T get() {
        if (editBox == null) {
            return value;
        }
        return parse(editBox.getValue());
    }

    @Override
    public void set(T value) {
        if (editBox == null) {
            this.value = value;
        } else {
            editBox.setValue(String.valueOf(value));
        }
    }

    @Override
    public void reset() {
        editBox = null;
        value = min;
    }

    protected abstract T parse(String input);
}
