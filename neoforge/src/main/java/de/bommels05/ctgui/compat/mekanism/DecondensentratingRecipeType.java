package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.NeoLoaderUtils;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.basic.BasicRotaryRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.RotaryEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class DecondensentratingRecipeType extends SupportedRecipeType<BasicRotaryRecipe> {

    public DecondensentratingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "decondensentrating"));

        addAreaScrollAmountEmptyRightClick(22, 1, 18, 60, (r, input) -> {
            GasStack stack = input.toStack();
            return new BasicRotaryRecipe(r.getFluidInput(), stack.getType() == r.getGasOutputRaw().getType() ? stack : new GasStack(stack, r.getGasOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getGasOutputRaw());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(130, 1, 18, 60, (r, stack) -> {
            return new BasicRotaryRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getFluidInput()), r.getGasOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getFluidInput());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 1)), NeoLoaderUtils::limitedFluidAmountSetter);
    }

    @Override
    public BasicRotaryRecipe onInitialize(@Nullable BasicRotaryRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicRotaryRecipe(IngredientCreatorAccess.fluid().from(Fluids.WATER, 1), new GasStack(MekanismGases.OXYGEN.get(), 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicRotaryRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicRotaryRecipe recipe) throws UnsupportedViewerException {
        return new RotaryEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "decondensentrating")), nullRl(), new RecipeHolder<>(nullRl(), recipe), false);
    }

    @Override
    public String getCraftTweakerString(BasicRotaryRecipe recipe, String id) {

        return "<recipetype:mekanism:rotary>.addRecipe(\"" + id + "\", " + NeoLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getFluidInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getGasOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicRotaryRecipe recipe) {
        return new ItemStack(MekanismBlocks.ROTARY_CONDENSENTRATOR);
    }
}
