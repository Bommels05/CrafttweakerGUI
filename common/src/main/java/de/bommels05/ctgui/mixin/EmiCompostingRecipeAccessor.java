package de.bommels05.ctgui.mixin;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.recipe.EmiCompostingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EmiCompostingRecipe.class)
public interface EmiCompostingRecipeAccessor {

    @Accessor(value = "stack", remap = false)
    public EmiIngredient getStack();

    @Accessor(value = "chance", remap = false)
    public float getChance();

}
