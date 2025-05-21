package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.FloatRecipeOption;
import de.bommels05.ctgui.emi.EmiViewerUtils;
import de.bommels05.ctgui.mixin.EmiCompostingRecipeAccessor;
import de.bommels05.ctgui.mixin.EmiFuelRecipeAccessor;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.recipe.EmiCompostingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CompostingRecipeType extends SupportedRecipeType<CompostingRecipe> {
    private final FloatRecipeOption<CompostingRecipe> chance = new FloatRecipeOption<>(Component.translatable("ctgui.editing.options.composting_chance"), 0, 1);

    public CompostingRecipeType() {
        super(ResourceLocation.parse(CraftTweakerGUI.isJeiActive() ? "minecraft:composting" : "emi:composting"));
        addAreaEmptyRightClick(0, 0, 17, 17, (r, am) -> {
            return new CompostingRecipe(am.ensureAmount(1, 1).ingredient(), r.getChance());
        }, r -> {
            return new AmountedIngredient(r.getIngredient(), 1);
        });
        addOption(chance, (r, value) -> {
            return new CompostingRecipe(r.getIngredient(), value);
        });
    }

    @Override
    public CompostingRecipe onInitialize(@Nullable CompostingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            chance.set(0.3f);
            return new CompostingRecipe(Ingredient.EMPTY, 0.3f);
        }
        chance.set(recipe.getChance());
        return null;
    }

    @Override
    public boolean isValid(CompostingRecipe recipe) {
        return !recipe.getIngredient().isEmpty() && recipe.getChance() > 0;
    }

    @Override
    public Object getEmiRecipe(CompostingRecipe recipe) throws UnsupportedViewerException {
        return new EmiCompostingRecipe(EmiIngredient.of(recipe.getIngredient()), recipe.getChance(), nullRl());
    }

    @Override
    public Function<EmiRecipe, CompostingRecipe> getAlternativeEmiRecipeGetter() {
        return recipe -> recipe instanceof EmiCompostingRecipeAccessor r ? new CompostingRecipe(EmiViewerUtils.getElseEmpty(r.getStack()), r.getChance()) : null;
    }

    @Override
    public String getCraftTweakerString(CompostingRecipe recipe, String id) {
        return "composter.setValue(" + getCTString(recipe.getIngredient()) + ", " + recipe.getChance() +");";
    }

    @Override
    public String getCraftTweakerRemoveString(CompostingRecipe recipe, ResourceLocation id) {
        return "composter.setValue(" + getCTString(recipe.getIngredient()) + ", 0);";
    }

    @Override
    public boolean needsRecipeId() {
        return false;
    }
}
