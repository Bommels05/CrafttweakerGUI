package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.DoubleRecipeOption;
import de.bommels05.ctgui.api.option.IntegerRecipeOption;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.basic.BasicPressurizedReactionRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.PressurizedReactionEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class ReactingRecipeType extends SupportedRecipeType<BasicPressurizedReactionRecipe> {

    private final DoubleRecipeOption<BasicPressurizedReactionRecipe> energyRequired = new DoubleRecipeOption<>(Component.translatable("ctgui.editing.options.additional_required_energy"), 0);
    private final IntegerRecipeOption<BasicPressurizedReactionRecipe> duration = new IntegerRecipeOption<>(Component.translatable("ctgui.editing.options.duration"), 1);

    public ReactingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "reaction"));

        addAreaScrollAmountEmptyRightClick(50, 24, 17, 17, (r, am) -> {
            return new BasicPressurizedReactionRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getInputFluid(), r.getInputGas(), r.getEnergyRequired(), r.getDuration(), r.getOutputItem(), r.getOutputGas());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInputSolid()));
        });
        addAreaScrollAmountEmptyRightClick(112, 24, 17, 17, (r, am) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), r.getInputFluid(), r.getInputGas(), r.getEnergyRequired(), r.getDuration(), r.getOutputGas().isEmpty() ? convertToUnset(am.asStack()) : am.asStack(), r.getOutputGas());
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputItem()));
        });
        addAreaScrollAmountEmptyRightClick(1, -1, 18, 60, (r, stack) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), IngredientCreatorAccess.fluid().from(stack.getFluid() == MekanismRecipeUtils.of(r.getInputFluid()).getFluid() ? stack : new FluidStack(stack, MekanismRecipeUtils.getAmount(r.getInputFluid()))), r.getInputGas(), r.getEnergyRequired(), r.getDuration(), r.getOutputItem(), r.getOutputGas());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInputFluid());
        }, () -> new FluidStack(Fluids.WATER, 1000), SupportedRecipeType::fluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(24, -1, 18, 60, (r, stack) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), r.getInputFluid(), IngredientCreatorAccess.gas().from(stack.getType() == MekanismRecipeUtils.of(r.getInputGas()).getType() ? stack : new GasStack(stack, MekanismRecipeUtils.getAmount(r.getInputGas()))), r.getEnergyRequired(), r.getDuration(), r.getOutputItem(), r.getOutputGas());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInputGas());
        }, () -> new GasStack(MekanismGases.OXYGEN.get(), 100), MekanismRecipeUtils::chemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(136, 29, 18, 30, (r, stack) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), r.getInputFluid(), r.getInputGas(), r.getEnergyRequired(), r.getDuration(), stack.isEmpty() ? convertToUnset(r.getOutputItem()) : convertUnset(r.getOutputItem()), stack.getType() == r.getOutputGas().getType() ? stack : new GasStack(stack, r.getOutputGas().getAmount() == 0 ? 100 : r.getOutputGas().getAmount()));
        }, BasicPressurizedReactionRecipe::getOutputGas, () -> GasStack.EMPTY, MekanismRecipeUtils::chemicalAmountSetter);

        addOption(energyRequired, (r, energyRequired) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), r.getInputFluid(), r.getInputGas(), FloatingLong.create(energyRequired), r.getDuration(), r.getOutputItem(), r.getOutputGas());
        });
        addOption(duration, (r, duration) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), r.getInputFluid(), r.getInputGas(), r.getEnergyRequired(), duration, r.getOutputItem(), r.getOutputGas());
        });
    }

    @Override
    public BasicPressurizedReactionRecipe onInitialize(@Nullable BasicPressurizedReactionRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            duration.set(20);
            return new BasicPressurizedReactionRecipe(IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.fluid().from(Fluids.WATER, 1000),
                    IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 100), FloatingLong.create(0), 20, UNSET, GasStack.EMPTY);
        }
        energyRequired.set(recipe.getEnergyRequired().doubleValue());
        duration.set(recipe.getDuration());
        return recipe;
    }

    @Override
    public boolean isValid(BasicPressurizedReactionRecipe recipe) {
        return !recipe.getInputSolid().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getOutputItem(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicPressurizedReactionRecipe recipe) throws UnsupportedViewerException {
        return new PressurizedReactionEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "reaction")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicPressurizedReactionRecipe recipe, String id) {
        return "<recipetype:mekanism:reaction>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInputSolid())) + ", " + getCTString(MekanismRecipeUtils.of(recipe.getInputFluid())) + ", " + MekanismRecipeUtils.getCTString(recipe.getInputGas()) + ", " + recipe.getDuration() + (!recipe.getOutputItem().isEmpty() ? ", " + getCTString(recipe.getOutputItem()) : "") + (!recipe.getOutputGas().isEmpty() ? ", " + MekanismRecipeUtils.getCTString(recipe.getOutputGas()) : "") + ", " + recipe.getEnergyRequired() + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicPressurizedReactionRecipe recipe) {
        return recipe.getOutputItem().isEmpty() ? new ItemStack(MekanismBlocks.PRESSURIZED_REACTION_CHAMBER) : convertUnset(recipe.getOutputItem());
    }
}
