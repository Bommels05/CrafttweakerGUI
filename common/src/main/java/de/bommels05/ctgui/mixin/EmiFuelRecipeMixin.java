package de.bommels05.ctgui.mixin;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.recipe.EmiFuelRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EmiFuelRecipe.class)
public interface EmiFuelRecipeMixin {

    @Accessor(value = "stack")
    public EmiIngredient getStack();

    @Accessor(value = "time")
    public int getTime();

}
