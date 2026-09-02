package de.bommels05.ctgui.api.option;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

public class LongRecipeOption<R extends Recipe<?>> extends EditBoxRecipeOption<Long, R> {

    public LongRecipeOption(Component tooltip, long min, long max) {
        super(tooltip, min, max);
    }

    public LongRecipeOption(Component tooltip, long min) {
        this(tooltip, min, Long.MAX_VALUE);
    }

    public LongRecipeOption(Component tooltip) {
        this(tooltip, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Override
    protected Long parse(String input) {
        if (input.isEmpty()) {
            return min;
        } else {
            return Long.parseLong(input);
        }
    }
}
