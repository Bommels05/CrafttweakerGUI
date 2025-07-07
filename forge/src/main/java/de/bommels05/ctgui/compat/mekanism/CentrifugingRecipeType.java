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
import mekanism.common.recipe.impl.CentrifugingIRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CentrifugingRecipeType extends SupportedRecipeType<CentrifugingIRecipe> {

    public CentrifugingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "isotopic_centrifuge"));

        addAreaScrollAmountEmptyRightClick(21, 0, 18, 60, (r, stack) -> {
            return new CentrifugingIRecipe(r.getId(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getOutput(null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(129, 0, 18, 60, (r, input) -> {
            GasStack stack = input.toStack();
            return new CentrifugingIRecipe(r.getId(), r.getInput(), stack.getType() == r.getOutput(null).getType() ? stack : new GasStack(stack, r.getOutput(null).getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null));
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public CentrifugingIRecipe onInitialize(@Nullable CentrifugingIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new CentrifugingIRecipe(nullRl(), IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1), new GasStack(MekanismGases.OXYGEN.get(), 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(CentrifugingIRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(CentrifugingIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(CentrifugingIRecipe recipe, String id) {
        return "<recipetype:mekanism:centrifuging>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutput(null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(CentrifugingIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeGasStack(recipe.getOutput(null)));
        return json;
    }

    @Override
    public ItemStack getMainOutput(CentrifugingIRecipe recipe) {
        return new ItemStack(MekanismBlocks.ISOTOPIC_CENTRIFUGE);
    }

    @Override
    public CentrifugingIRecipe getWithId(CentrifugingIRecipe r, ResourceLocation id) {
        return new CentrifugingIRecipe(id, r.getInput(), r.getOutput(null));
    }
}
