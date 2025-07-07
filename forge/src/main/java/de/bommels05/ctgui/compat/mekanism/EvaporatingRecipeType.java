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
import mekanism.api.chemical.ChemicalType;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.FluidToFluidIRecipe;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class EvaporatingRecipeType extends SupportedRecipeType<FluidToFluidIRecipe> {

    public EvaporatingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "thermal_evaporation_controller"));

        addAreaScrollAmountEmptyRightClick(3, 1, 18, 60, (r, stack) -> {
            return new FluidToFluidIRecipe(r.getId(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getOutput(null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getInput());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 1)), ForgeLoaderUtils::limitedFluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(149, 1, 18, 60, (r, input) -> {
            FluidStack stack = input.toStack();
            return new FluidToFluidIRecipe(r.getId(), r.getInput(), stack.getFluid() == r.getOutput(null).getFluid() ? stack : new FluidStack(stack, r.getOutput(null).getAmount()));
        }, r -> {
            return new FluidAmountedIngredient(r.getOutput(null));
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 1)), ForgeLoaderUtils::limitedFluidAmountSetter);
    }

    @Override
    public FluidToFluidIRecipe onInitialize(@Nullable FluidToFluidIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new FluidToFluidIRecipe(nullRl(), IngredientCreatorAccess.fluid().from(Fluids.WATER, 1), new FluidStack(Fluids.WATER, 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(FluidToFluidIRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(FluidToFluidIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(FluidToFluidIRecipe recipe, String id) {
        return "<recipetype:mekanism:evaporating>.addRecipe(\"" + id + "\", " + ForgeLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + ForgeLoaderUtils.getCTString(recipe.getOutput(null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(FluidToFluidIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeFluidStack(recipe.getOutput(null)));
        return json;
    }

    @Override
    public FluidToFluidIRecipe getWithId(FluidToFluidIRecipe r, ResourceLocation id) {
        return new FluidToFluidIRecipe(id, r.getInput(), r.getOutput(null));
    }

    @Override
    public ItemStack getMainOutput(FluidToFluidIRecipe recipe) {
        return new ItemStack(MekanismBlocks.THERMAL_EVAPORATION_CONTROLLER);
    }
}
