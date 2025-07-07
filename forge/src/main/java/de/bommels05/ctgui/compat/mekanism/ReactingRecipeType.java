package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.ForgeLoaderUtils;
import de.bommels05.ctgui.api.*;
import de.bommels05.ctgui.api.option.DoubleRecipeOption;
import de.bommels05.ctgui.api.option.IntegerRecipeOption;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.PressurizedReactionRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.PressurizedReactionIRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class ReactingRecipeType extends SupportedRecipeType<PressurizedReactionIRecipe> {

    private final DoubleRecipeOption<PressurizedReactionIRecipe> energyRequired = new DoubleRecipeOption<>(Component.translatable("ctgui.editing.options.additional_required_energy"), 0);
    private final IntegerRecipeOption<PressurizedReactionIRecipe> duration = new IntegerRecipeOption<>(Component.translatable("ctgui.editing.options.duration"), 1);

    public ReactingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "pressurized_reaction_chamber"));

        addAreaScrollAmountEmptyRightClick(50, 24, 17, 17, (r, am) -> {
            PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = r.getOutput(null, null, null);
            return new PressurizedReactionIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getInputFluid(), r.getInputGas(), r.getEnergyRequired(), r.getDuration(), output.item(), output.gas());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInputSolid()));
        });
        addAreaScrollAmountEmptyRightClick(112, 24, 17, 17, (r, am) -> {
            PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = r.getOutput(null, null, null);
            return new PressurizedReactionIRecipe(r.getId(), r.getInputSolid(), r.getInputFluid(), r.getInputGas(), r.getEnergyRequired(), r.getDuration(), output.gas().isEmpty() ? convertToUnset(am.asStack()) : am.asStack(), output.gas());
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutput(null, null, null).item()));
        });
        addAreaScrollAmountEmptyRightClick(1, -1, 18, 60, (r, stack) -> {
            PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = r.getOutput(null, null, null);
            return new PressurizedReactionIRecipe(r.getId(), r.getInputSolid(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInputFluid()), r.getInputGas(), r.getEnergyRequired(), r.getDuration(), output.item(), output.gas());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInputFluid());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 1000)), ForgeLoaderUtils::fluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(24, -1, 18, 60, (r, stack) -> {
            PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = r.getOutput(null, null, null);
            return new PressurizedReactionIRecipe(r.getId(), r.getInputSolid(), r.getInputFluid(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInputGas()), r.getEnergyRequired(), r.getDuration(), output.item(), output.gas());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInputGas());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 100)), MekanismRecipeUtils::chemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(136, 29, 18, 30, (r, input) -> {
            PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = r.getOutput(null, null, null);
            GasStack stack = input.toStack();
            return new PressurizedReactionIRecipe(r.getId(), r.getInputSolid(), r.getInputFluid(), r.getInputGas(), r.getEnergyRequired(), r.getDuration(), stack.isEmpty() ? convertToUnset(output.item()) : convertUnset(output.item()), stack.getType() == output.gas().getType() ? stack : new GasStack(stack, output.gas().getAmount() == 0 ? 100 : output.gas().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null, null, null).gas());
        }, () -> new ChemicalAmountedIngredient<>(GasStack.EMPTY), MekanismRecipeUtils::chemicalAmountSetter);

        addOption(energyRequired, (r, energyRequired) -> {
            PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = r.getOutput(null, null, null);
            return new PressurizedReactionIRecipe(r.getId(), r.getInputSolid(), r.getInputFluid(), r.getInputGas(), FloatingLong.create(energyRequired), r.getDuration(), output.item(), output.gas());
        });
        addOption(duration, (r, duration) -> {
            PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = r.getOutput(null, null, null);
            return new PressurizedReactionIRecipe(r.getId(), r.getInputSolid(), r.getInputFluid(), r.getInputGas(), r.getEnergyRequired(), duration, output.item(), output.gas());
        });
    }

    @Override
    public PressurizedReactionIRecipe onInitialize(@Nullable PressurizedReactionIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            duration.set(20);
            return new PressurizedReactionIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.fluid().from(Fluids.WATER, 1000),
                    IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 100), FloatingLong.ZERO, 20, UNSET, GasStack.EMPTY);
        }
        energyRequired.set(recipe.getEnergyRequired().doubleValue());
        duration.set(recipe.getDuration());
        return recipe;
    }

    @Override
    public boolean isValid(PressurizedReactionIRecipe recipe) {
        return !recipe.getInputSolid().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getOutput(null, null, null).item(), UNSET);
    }

    @Override
    public Object getEmiRecipe(PressurizedReactionIRecipe recipe) throws UnsupportedViewerException {
         throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(PressurizedReactionIRecipe recipe, String id) {
        PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = recipe.getOutput(null, null, null);
        return "<recipetype:mekanism:reaction>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInputSolid())) + ", " + ForgeLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getInputFluid())) + ", " + MekanismRecipeUtils.getCTString(recipe.getInputGas()) + ", " + recipe.getDuration() + (!output.item().isEmpty() ? ", " + getCTString(output.item()) : "") + (!output.gas().isEmpty() ? ", " + MekanismRecipeUtils.getCTString(output.gas()) : "") + ", " + recipe.getEnergyRequired() + ");";
    }

    @Override
    public JsonObject getRecipeJson(PressurizedReactionIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.ITEM_INPUT, recipe.getInputSolid().serialize());
        json.add(JsonConstants.FLUID_INPUT, recipe.getInputFluid().serialize());
        json.add(JsonConstants.GAS_INPUT, recipe.getInputGas().serialize());

        json.addProperty(JsonConstants.DURATION, recipe.getDuration());
        json.addProperty(JsonConstants.ENERGY_REQUIRED, recipe.getEnergyRequired());

        PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = recipe.getOutput(null, null, null);
        if (!output.item().isEmpty()) {
            json.add(JsonConstants.ITEM_OUTPUT, SerializerHelper.serializeItemStack(output.item()));
        }
        if (!output.gas().isEmpty()) {
            json.add(JsonConstants.GAS_OUTPUT, SerializerHelper.serializeGasStack(output.gas()));
        }
        return json;
    }

    @Override
    public PressurizedReactionIRecipe getWithId(PressurizedReactionIRecipe r, ResourceLocation id) {
        PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = r.getOutput(null, null, null);
        return new PressurizedReactionIRecipe(id, r.getInputSolid(), r.getInputFluid(), r.getInputGas(), r.getEnergyRequired(), r.getDuration(), output.item(), output.gas());
    }

    @Override
    public ItemStack getMainOutput(PressurizedReactionIRecipe recipe) {
        PressurizedReactionRecipe.PressurizedReactionRecipeOutput output = recipe.getOutput(null, null, null);
        return output.item().isEmpty() ? new ItemStack(MekanismBlocks.PRESSURIZED_REACTION_CHAMBER) : convertUnset(output.item());
    }
}
