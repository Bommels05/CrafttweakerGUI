package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.NeoLoaderUtils;
import de.bommels05.ctgui.api.*;
import de.bommels05.ctgui.api.option.IntegerRecipeOption;
import de.bommels05.ctgui.api.option.LongRecipeOption;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicPressurizedReactionRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.PressurizedReactionEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class ReactingRecipeType extends SupportedRecipeType<BasicPressurizedReactionRecipe> {

    private final LongRecipeOption<BasicPressurizedReactionRecipe> energyRequired = new LongRecipeOption<>(Component.translatable("ctgui.editing.options.additional_required_energy"), 0);
    private final IntegerRecipeOption<BasicPressurizedReactionRecipe> duration = new IntegerRecipeOption<>(Component.translatable("ctgui.editing.options.duration"), 1);

    public ReactingRecipeType() {
        super(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "reaction"));

        addAreaScrollAmountEmptyRightClick(50, 24, 17, 17, (r, am) -> {
            return new BasicPressurizedReactionRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getInputFluid(), r.getInputChemical(), r.getEnergyRequired(), r.getDuration(), r.getOutputItem(), r.getOutputChemical());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInputSolid()));
        });
        addAreaScrollAmountEmptyRightClick(112, 24, 17, 17, (r, am) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), r.getInputFluid(), r.getInputChemical(), r.getEnergyRequired(), r.getDuration(), r.getOutputChemical().isEmpty() ? convertToUnset(am.asStack()) : am.asStack(), r.getOutputChemical());
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputItem()));
        });
        addAreaScrollAmountEmptyRightClick(1, -1, 18, 60, (r, stack) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInputFluid()), r.getInputChemical(), r.getEnergyRequired(), r.getDuration(), r.getOutputItem(), r.getOutputChemical());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInputFluid());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 1000)), NeoLoaderUtils::fluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(24, -1, 18, 60, (r, stack) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), r.getInputFluid(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInputChemical()), r.getEnergyRequired(), r.getDuration(), r.getOutputItem(), r.getOutputChemical());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInputChemical());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN.get(), 100)), MekanismRecipeUtils::chemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(136, 29, 18, 30, (r, input) -> {
            ChemicalStack stack = input.toStack();
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), r.getInputFluid(), r.getInputChemical(), r.getEnergyRequired(), r.getDuration(), stack.isEmpty() ? convertToUnset(r.getOutputItem()) : convertUnset(r.getOutputItem()), stack.getChemical() == r.getOutputChemical().getChemical() ? stack : stack.copyWithAmount(r.getOutputChemical().getAmount() == 0 ? 100 : r.getOutputChemical().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient(r.getOutputChemical());
        }, () -> new ChemicalAmountedIngredient(ChemicalStack.EMPTY), MekanismRecipeUtils::chemicalAmountSetter);

        addOption(energyRequired, (r, energyRequired) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), r.getInputFluid(), r.getInputChemical(), energyRequired, r.getDuration(), r.getOutputItem(), r.getOutputChemical());
        });
        addOption(duration, (r, duration) -> {
            return new BasicPressurizedReactionRecipe(r.getInputSolid(), r.getInputFluid(), r.getInputChemical(), r.getEnergyRequired(), duration, r.getOutputItem(), r.getOutputChemical());
        });
    }

    @Override
    public BasicPressurizedReactionRecipe onInitialize(@Nullable BasicPressurizedReactionRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            duration.set(20);
            return new BasicPressurizedReactionRecipe(IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.fluid().from(Fluids.WATER, 1000),
                    IngredientCreatorAccess.chemicalStack().from(MekanismChemicals.OXYGEN, 100), 0, 20, UNSET, ChemicalStack.EMPTY);
        }
        energyRequired.set(recipe.getEnergyRequired());
        duration.set(recipe.getDuration());
        return recipe;
    }

    @Override
    public boolean isValid(BasicPressurizedReactionRecipe recipe) {
        return !recipe.getInputSolid().test(UNSET) && !ItemStack.isSameItemSameComponents(recipe.getOutputItem(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicPressurizedReactionRecipe recipe) throws UnsupportedViewerException {
        return new PressurizedReactionEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "reaction")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicPressurizedReactionRecipe recipe, String id) {
        return "<recipetype:mekanism:reaction>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInputSolid())) + ", " + NeoLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getInputFluid())) + ", " + MekanismRecipeUtils.getCTString(recipe.getInputChemical()) + ", " + recipe.getDuration() + (!recipe.getOutputItem().isEmpty() ? ", " + getCTString(recipe.getOutputItem()) : "") + (!recipe.getOutputChemical().isEmpty() ? ", " + MekanismRecipeUtils.getCTString(recipe.getOutputChemical()) : "") + ", " + recipe.getEnergyRequired() + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicPressurizedReactionRecipe recipe) {
        return recipe.getOutputItem().isEmpty() ? new ItemStack(MekanismBlocks.PRESSURIZED_REACTION_CHAMBER) : convertUnset(recipe.getOutputItem());
    }
}
