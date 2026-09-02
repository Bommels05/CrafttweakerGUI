package de.bommels05.ctgui.api.option;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

public class DoubleRecipeOption<R extends Recipe<?>> extends EditBoxRecipeOption<Double, R> {

    public DoubleRecipeOption(Component tooltip, double min, double max) {
        super(tooltip, min, max);
    }

    public DoubleRecipeOption(Component tooltip, double min) {
        this(tooltip, min, Double.MAX_VALUE);
    }

    public DoubleRecipeOption(Component tooltip) {
        this(tooltip, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    @Override
    protected Double parse(String input) {
        if (input.isEmpty()) {
            return min;
        } else {
            return Double.parseDouble(input);
        }
    }
}
