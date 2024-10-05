package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.IntegerRecipeOption;
import de.bommels05.ctgui.emi.EmiViewerUtils;
import de.bommels05.ctgui.mixin.EmiFuelRecipeMixin;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class FuelRecipeType extends SupportedRecipeType<FuelRecipe> {
    private final IntegerRecipeOption<FuelRecipe> burnTime = new IntegerRecipeOption<>(Component.translatable("ctgui.editing.options.burn_time"), 1);

    public FuelRecipeType() {
        super(new ResourceLocation(CraftTweakerGUI.isJeiActive() ? "minecraft:fuel" : "emi:fuel"));
        addAreaEmptyRightClick(18, 0, 17, 17, (r, am) -> {
            return new FuelRecipe(am.ensureAmount(1, 1).ingredient(), r.getBurnTime());
        }, r -> {
            return new AmountedIngredient(r.getIngredient(), 1);
        });
        addOption(burnTime, (r, value) -> {
            return new FuelRecipe(r.getIngredient(), value);
        });
    }

    @Override
    public FuelRecipe onInitialize(@Nullable FuelRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            burnTime.set(200);
            return new FuelRecipe(Ingredient.EMPTY, 200);
        }
        burnTime.set(recipe.getBurnTime());
        return null;
    }

    @Override
    public boolean isValid(FuelRecipe recipe) {
        return !recipe.getIngredient().isEmpty();
    }

    @Override
    public Object getEmiRecipe(FuelRecipe recipe) throws UnsupportedViewerException {
        //Cursed class loading issues without emi
        return EmiViewerUtils.getFuelRecipe(recipe, nullRl());
    }

    @Override
    public Function<EmiRecipe, FuelRecipe> getAlternativeEmiRecipeGetter() {
        return recipe -> recipe instanceof EmiFuelRecipeMixin r ? new FuelRecipe(EmiViewerUtils.getElseEmpty(r.getStack()), r.getTime()) : null;
    }

    @Override
    public String getCraftTweakerString(FuelRecipe recipe, String id) {
        return "(" + getCTString(recipe.getIngredient()) + " as IIngredient).burnTime = " + recipe.getBurnTime() + ";";
    }

    @Override
    public String getCraftTweakerRemoveString(FuelRecipe recipe, ResourceLocation id) {
        return "(" + getCTString(recipe.getIngredient()) + " as IIngredient).burnTime = 0;";
    }
}
