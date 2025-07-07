package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.ChemicalInfuserIRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ChemicalInfusingRecipeType extends SupportedRecipeType<ChemicalInfuserIRecipe> {

    public ChemicalInfusingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "chemical_infuser"));

        addAreaScrollAmountEmptyRightClick(22, 10, 18, 60, (r, stack) -> {
            return new ChemicalInfuserIRecipe(r.getId(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getLeftInput()), r.getRightInput(), r.getOutput(null, null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getLeftInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(130, 10, 18, 60, (r, stack) -> {
            return new ChemicalInfuserIRecipe(r.getId(), r.getLeftInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getRightInput()), r.getOutput(null, null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getRightInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(76, 1, 18, 60, (r, input) -> {
            GasStack stack = input.toStack();
            return new ChemicalInfuserIRecipe(r.getId(), r.getLeftInput(), r.getRightInput(), stack.getType() == r.getOutput(null, null).getType() ? stack : new GasStack(stack, r.getOutput(null, null).getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null, null));
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 2)), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public ChemicalInfuserIRecipe onInitialize(@Nullable ChemicalInfuserIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new ChemicalInfuserIRecipe(nullRl(), IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1),
                    IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1), new GasStack(MekanismGases.OXYGEN.get(), 2));
        }
        return recipe;
    }

    @Override
    public boolean isValid(ChemicalInfuserIRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(ChemicalInfuserIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(ChemicalInfuserIRecipe recipe, String id) {
        return "<recipetype:mekanism:chemical_infusing>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getLeftInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getRightInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutput(null, null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(ChemicalInfuserIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.LEFT_INPUT, recipe.getLeftInput().serialize());
        json.add(JsonConstants.RIGHT_INPUT, recipe.getRightInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeGasStack(recipe.getOutput(null, null)));
        return json;
    }

    @Override
    public ChemicalInfuserIRecipe getWithId(ChemicalInfuserIRecipe r, ResourceLocation id) {
        return new ChemicalInfuserIRecipe(id, r.getLeftInput(), r.getRightInput(), r.getOutput(null, null));
    }

    @Override
    public ItemStack getMainOutput(ChemicalInfuserIRecipe recipe) {
        return new ItemStack(MekanismBlocks.CHEMICAL_INFUSER);
    }
}
