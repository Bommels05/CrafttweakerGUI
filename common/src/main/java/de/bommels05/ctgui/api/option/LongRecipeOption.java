package de.bommels05.ctgui.api.option;

import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;

public class LongRecipeOption<R extends Recipe<?>> implements RecipeOption<Long, R> {

    private EditBox editBox;
    private BiFunction<R, Long, R> listener;
    private final Component tooltip;
    private final long min;
    private final long max;
    private long value;

    public LongRecipeOption(Component tooltip, long min, long max) {
        this.tooltip = tooltip;
        this.min = min;
        this.max = max;
    }

    public LongRecipeOption(Component tooltip, long min) {
        this(tooltip, min, Long.MAX_VALUE);
    }

    public LongRecipeOption(Component tooltip) {
        this(tooltip, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Override
    public void addToScreen(RecipeEditScreen<?> screen, int x, int y) {
        editBox = new EditBox(screen.getFont(), x, y, 100, 18, Component.empty());
        editBox.setMaxLength(256);
        editBox.setTooltip(Tooltip.create(tooltip));
        editBox.setValue(String.valueOf(value));
        editBox.setResponder(value -> screen.handleRecipeOption(parse(value), listener));
        editBox.setFilter(value -> {
            try {
                long i = Long.parseLong(value);
                if (i < min) {
                    editBox.setValue(String.valueOf(min));
                    return false;
                }
                if (i > max) {
                    editBox.setValue(String.valueOf(max));
                    return false;
                }
                return true;
            } catch (NumberFormatException e) {
                return value.isEmpty();
            }
        });
        screen.addRenderableWidget(editBox);
    }

    @Override
    public void addListener(BiFunction<R, Long, R> listener) {
        this.listener = listener;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public Long get() {
        if (editBox == null) {
            return value;
        }
        return parse(editBox.getValue());
    }

    @Override
    public void set(Long value) {
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

    private long parse(String input) {
        if (input.isEmpty()) {
            return min;
        } else {
            return Integer.parseInt(input);
        }
    }
}
