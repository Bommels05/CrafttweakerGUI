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

public class DecondensentratingRecipeType extends SupportedRecipeType<RotaryIRecipe> {

    public DecondensentratingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "decondensentrating"));

        addAreaScrollAmountEmptyRightClick(22, 1, 18, 60, (r, input) -> {
            GasStack stack = input.toStack();
            return new RotaryIRecipe(r.getId(), r.getFluidInput(), stack.getType() == r.getGasOutput(null).getType() ? stack : new GasStack(stack, r.getGasOutput(null).getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getGasOutput(null));
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(130, 1, 18, 60, (r, stack) -> {
            return new RotaryIRecipe(r.getId(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getFluidInput()), r.getGasOutput(null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getFluidInput());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 1)), ForgeLoaderUtils::limitedFluidAmountSetter);
    }

    @Override
    public RotaryIRecipe onInitialize(@Nullable RotaryIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new RotaryIRecipe(nullRl(), IngredientCreatorAccess.fluid().from(Fluids.WATER, 1), new GasStack(MekanismGases.OXYGEN.get(), 1));
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
        return "<recipetype:mekanism:rotary>.addRecipe(\"" + id + "\", " + ForgeLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getFluidInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getGasOutput(null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(RotaryIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.FLUID_INPUT, recipe.getFluidInput().serialize());
        json.add(JsonConstants.GAS_OUTPUT, SerializerHelper.serializeGasStack(recipe.getGasOutput(null)));
        return json;
    }

    @Override
    public RotaryIRecipe getWithId(RotaryIRecipe r, ResourceLocation id) {
        return new RotaryIRecipe(id, r.getFluidInput(), r.getGasOutput(null));
    }

    @Override
    public ItemStack getMainOutput(RotaryIRecipe recipe) {
        return new ItemStack(MekanismBlocks.ROTARY_CONDENSENTRATOR);
    }
}
