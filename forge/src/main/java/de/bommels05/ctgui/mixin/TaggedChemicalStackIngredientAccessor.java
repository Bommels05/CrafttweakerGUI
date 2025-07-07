package de.bommels05.ctgui.mixin;

import mekanism.common.recipe.ingredient.chemical.TaggedChemicalStackIngredient;
import net.minecraftforge.registries.tags.ITag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TaggedChemicalStackIngredient.class)
public interface TaggedChemicalStackIngredientAccessor {

    @Accessor(value = "tag", remap = false)
    public ITag<?> getTag();

    @Accessor(value = "amount", remap = false)
    public long getAmount();

}
