package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.ForgeLoaderUtils;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.RotaryIRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CondensentratingRecipeType extends SupportedRecipeType<RotaryIRecipe> {

    public CondensentratingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "condensentrating"));

        addAreaScrollAmountEmptyRightClick(22, 1, 18, 60, (r, stack) -> {
            return new RotaryIRecipe(r.getId(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getGasInput()), r.getFluidOutput(null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getGasInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(130, 1, 18, 60, (r, input) -> {
            FluidStack stack = input.toStack();
            return new RotaryIRecipe(r.getId(), r.getGasInput(), stack.getFluid() == r.getFluidOutput(null).getFluid() ? stack : new FluidStack(stack, r.getFluidOutput(null).getAmount()));
        }, r -> {
            return new FluidAmountedIngredient(r.getFluidOutput(null));
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 1)), ForgeLoaderUtils::limitedFluidAmountSetter);
    }

    @Override
    public RotaryIRecipe onInitialize(@Nullable RotaryIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new RotaryIRecipe(nullRl(), IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1), new FluidStack(Fluids.WATER, 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(RotaryIRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(RotaryIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(RotaryIRecipe recipe, String id) {
        return "<recipetype:mekanism:rotary>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getGasInput()) + ", " + ForgeLoaderUtils.getCTString(recipe.getFluidOutput(null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(RotaryIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.GAS_INPUT, recipe.getGasInput().serialize());
        json.add(JsonConstants.FLUID_OUTPUT, SerializerHelper.serializeFluidStack(recipe.getFluidOutput(null)));
        return json;
    }

    @Override
    public RotaryIRecipe getWithId(RotaryIRecipe r, ResourceLocation id) {
        return new RotaryIRecipe(id, r.getGasInput(), r.getFluidOutput(null));
    }

    @Override
    public ItemStack getMainOutput(RotaryIRecipe recipe) {
        return new ItemStack(MekanismBlocks.ROTARY_CONDENSENTRATOR);
    }
}
