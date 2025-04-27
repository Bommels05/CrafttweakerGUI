package de.bommels05.ctgui.mixin;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.recipe.EmiFuelRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EmiFuelRecipe.class)
public interface EmiFuelRecipeAccessor {

    @Accessor(value = "stack", remap = false)
    public EmiIngredient getStack();

    @Accessor(value = "time", remap = false)
    public int getTime();

}
