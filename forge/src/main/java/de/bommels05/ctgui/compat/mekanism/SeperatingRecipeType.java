package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.ForgeLoaderUtils;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.DoubleRecipeOption;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.ElectrolysisRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.ElectrolysisIRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class SeperatingRecipeType extends SupportedRecipeType<ElectrolysisIRecipe> {

    private final DoubleRecipeOption<ElectrolysisIRecipe> energyMultiplier = new DoubleRecipeOption<>(Component.translatable("ctgui.editing.options.energy_multiplier"), 1);

    public SeperatingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "electrolytic_separator"));

        addAreaScrollAmountEmptyRightClick(1, 1, 18, 60, (r, stack) -> {
            ElectrolysisRecipe.ElectrolysisRecipeOutput output = r.getOutput(null);
            return new ElectrolysisIRecipe(r.getId(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getEnergyMultiplier(), output.left(), output.right());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInput());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 10)), ForgeLoaderUtils::limitedFluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(54, 9, 18, 30, (r, input) -> {
            ElectrolysisRecipe.ElectrolysisRecipeOutput output = r.getOutput(null);
            GasStack stack = input.toStack();
            return new ElectrolysisIRecipe(r.getId(), r.getInput(), r.getEnergyMultiplier(), stack.getType() == output.left().getType() ? stack : new GasStack(stack, output.left().getAmount()), output.right());
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null).left());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 10)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(96, 9, 18, 30, (r, input) -> {
            ElectrolysisRecipe.ElectrolysisRecipeOutput output = r.getOutput(null);
            GasStack stack = input.toStack();
            return new ElectrolysisIRecipe(r.getId(), r.getInput(), r.getEnergyMultiplier(), output.left(), stack.getType() == output.right().getType() ? stack : new GasStack(stack, output.right().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null).right());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 10)), MekanismRecipeUtils::limitedChemicalAmountSetter);

        addOption(energyMultiplier, (r, energyMultiplier) -> {
            ElectrolysisRecipe.ElectrolysisRecipeOutput output = r.getOutput(null);
            return new ElectrolysisIRecipe(r.getId(), r.getInput(), FloatingLong.create(energyMultiplier), output.left(), output.right());
        });
    }

    @Override
    public ElectrolysisIRecipe onInitialize(@Nullable ElectrolysisIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new ElectrolysisIRecipe(nullRl(), IngredientCreatorAccess.fluid().from(Fluids.WATER, 10), FloatingLong.ONE,
                    new GasStack(MekanismGases.OXYGEN.get(), 10), new GasStack(MekanismGases.OXYGEN.get(), 10));
        }
        energyMultiplier.set(recipe.getEnergyMultiplier().doubleValue());
        return recipe;
    }

    @Override
    public boolean isValid(ElectrolysisIRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(ElectrolysisIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(ElectrolysisIRecipe recipe, String id) {
        ElectrolysisRecipe.ElectrolysisRecipeOutput output = recipe.getOutput(null);
        return "<recipetype:mekanism:separating>.addRecipe(\"" + id + "\", " + ForgeLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(output.left()) + ", " + MekanismRecipeUtils.getCTString(output.right()) + ", " + recipe.getEnergyMultiplier() + ");";
    }

    @Override
    public JsonObject getRecipeJson(ElectrolysisIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.addProperty(JsonConstants.ENERGY_MULTIPLIER, recipe.getEnergyMultiplier());

        ElectrolysisRecipe.ElectrolysisRecipeOutput output = recipe.getOutput(null);
        json.add(JsonConstants.LEFT_GAS_OUTPUT, SerializerHelper.serializeGasStack(output.left()));
        json.add(JsonConstants.RIGHT_GAS_OUTPUT, SerializerHelper.serializeGasStack(output.right()));
        return json;
    }

    @Override
    public ElectrolysisIRecipe getWithId(ElectrolysisIRecipe r, ResourceLocation id) {
        ElectrolysisRecipe.ElectrolysisRecipeOutput output = r.getOutput(null);
        return new ElectrolysisIRecipe(id, r.getInput(), r.getEnergyMultiplier(), output.left(), output.right());
    }

    @Override
    public ItemStack getMainOutput(ElectrolysisIRecipe recipe) {
        return new ItemStack(MekanismBlocks.ELECTROLYTIC_SEPARATOR);
    }
}
