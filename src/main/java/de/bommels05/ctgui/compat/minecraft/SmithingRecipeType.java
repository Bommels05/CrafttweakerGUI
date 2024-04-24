package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.block.SmithingTableBlock;
import org.checkerframework.checker.units.qual.A;

public class SmithingRecipeType extends SupportedRecipeType<SmithingRecipe> {

    public SmithingRecipeType() {
        super(new ResourceLocation("minecraft:smithing"));
        addAreaEmptyRightClick(0, 0, 17, 17, (r, am) -> {
            SmithingTransformRecipe recipe = ((SmithingTransformRecipe) r);
            return new SmithingTransformRecipe(am.ingredient(), recipe.base, recipe.addition, recipe.getResultItem(regAccess()));
        }, r -> {
            return new AmountedIngredient(((SmithingTransformRecipe) r).template, 1);
        });
        addAreaEmptyRightClick(18, 0, 17, 17, (r, am) -> {
            SmithingTransformRecipe recipe = ((SmithingTransformRecipe) r);
            return new SmithingTransformRecipe(recipe.template, am.ingredient(), recipe.addition, recipe.getResultItem(regAccess()));
        }, r -> {
            return new AmountedIngredient(((SmithingTransformRecipe) r).base, 1);
        });
        addAreaEmptyRightClick(36, 0, 17, 17, (r, am) -> {
            SmithingTransformRecipe recipe = ((SmithingTransformRecipe) r);
            return new SmithingTransformRecipe(recipe.template, recipe.base, am.ingredient(), recipe.getResultItem(regAccess()));
        }, r -> {
            return new AmountedIngredient(((SmithingTransformRecipe) r).addition, 1);
        });
        addAreaScrollAmountEmptyRightClick(94, 0, 17, 17, (r, am) -> {
            SmithingTransformRecipe recipe = ((SmithingTransformRecipe) r);
            return new SmithingTransformRecipe(recipe.template, recipe.base, recipe.addition, am.asStack());
        }, r -> {
            return AmountedIngredient.of(r.getResultItem(regAccess()));
        });
    }

    @Override
    public SmithingRecipe onInitialize(SmithingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new SmithingTransformRecipe(Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
        }
        if (!(recipe instanceof SmithingTransformRecipe)) {
            throw new UnsupportedRecipeException();
        }
        return null;
    }

    @Override
    public boolean isValid(SmithingRecipe r) {
        SmithingTransformRecipe recipe = ((SmithingTransformRecipe) r);
        return !recipe.template.isEmpty() && !recipe.base.isEmpty() && !recipe.addition.isEmpty() && !recipe.getResultItem(regAccess()).isEmpty();
    }

    @Override
    public Object getEmiRecipe(SmithingRecipe r) throws UnsupportedViewerException {
        SmithingTransformRecipe recipe = ((SmithingTransformRecipe) r);
        return new EmiSmithingRecipe(EmiIngredient.of(recipe.template), EmiIngredient.of(recipe.base), EmiIngredient.of(recipe.addition), EmiStack.of(recipe.getResultItem(regAccess())), EmiPort.getId(recipe));
    }

    @Override
    public String getCraftTweakerString(SmithingRecipe r, String id) {
        SmithingTransformRecipe recipe = ((SmithingTransformRecipe) r);
        return "smithing.addTransformRecipe(\"" + id + "\", " + getCTString(recipe.getResultItem(regAccess())) + ", " + getCTString(recipe.template) + ", " + getCTString(recipe.base) + ", " + getCTString(recipe.addition) + ");";
    }
}
