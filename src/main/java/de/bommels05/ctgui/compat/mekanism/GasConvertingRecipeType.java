package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.basic.BasicGasConversionRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ItemStackToGasEmiRecipe;
import mekanism.common.registries.MekanismGases;
import mekanism.common.tile.machine.TileEntityChemicalOxidizer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class GasConvertingRecipeType extends SupportedRecipeType<BasicGasConversionRecipe> {

    public GasConvertingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "gas_conversion"));

        addAreaScrollAmountEmptyRightClick(6, 24, 17, 17, (r, am) -> {
            return new BasicGasConversionRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getOutputRaw());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(111, 1, 18, 60, (r, input) -> {
            GasStack stack = input.toStack();
            return new BasicGasConversionRecipe(r.getInput(), stack.getType() == r.getOutputRaw().getType() ? stack : new GasStack(stack, r.getOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 100)), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public BasicGasConversionRecipe onInitialize(@Nullable BasicGasConversionRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicGasConversionRecipe(IngredientCreatorAccess.item().from(UNSET), new GasStack(MekanismGases.OXYGEN.get(), 100));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicGasConversionRecipe recipe) {
        return !recipe.getInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicGasConversionRecipe recipe) throws UnsupportedViewerException {
        return new ItemStackToGasEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "gas_conversion")), new RecipeHolder<>(nullRl(), recipe), TileEntityChemicalOxidizer.BASE_TICKS_REQUIRED);
    }

    @Override
    public String getCraftTweakerString(BasicGasConversionRecipe recipe, String id) {
        return "<recipetype:mekanism:gas_conversion>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicGasConversionRecipe recipe) {
        return convertUnset(MekanismRecipeUtils.of(recipe.getInput()).asStack());
    }
}
