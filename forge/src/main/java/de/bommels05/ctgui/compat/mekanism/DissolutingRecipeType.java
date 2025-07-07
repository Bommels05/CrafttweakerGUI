package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.ChemicalDissolutionIRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DissolutingRecipeType extends SupportedRecipeType<ChemicalDissolutionIRecipe> {

    @SuppressWarnings("unchecked")
    public <S extends ChemicalStack<T>, T extends Chemical<T>> DissolutingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "chemical_dissolution_chamber"));

        addAreaScrollAmountEmptyRightClick(25, 33, 17, 17, (r, am) -> {
            return new ChemicalDissolutionIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getGasInput(), r.getOutput(null, null).getChemicalStack());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(4, 1, 18, 60, (r, stack) -> {
            return new ChemicalDissolutionIRecipe(r.getId(), r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getGasInput()), r.getOutput(null, null).getChemicalStack());
        }, r -> {
            return MekanismRecipeUtils.of(r.getGasInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), (stack, up) -> MekanismRecipeUtils.chemicalAmountSetter(stack, up, 1, 10));
        addAreaScrollAmountEmptyRightClick(128, 10, 18, 60, (r, input) -> {
            ChemicalStack<?> stack = input.toStack();
            ChemicalStack<?> stack2 = stack.copy();
            if (stack.getType() != r.getOutput(null, null).getChemicalStack().getType()) {
                stack2.setAmount(r.getOutput(null, null).getChemicalStack().getAmount());
            }
            return new ChemicalDissolutionIRecipe(r.getId(), r.getItemInput(), r.getGasInput(), stack2);
        }, r -> {
            return (ChemicalAmountedIngredient<S, T>) new ChemicalAmountedIngredient<>(r.getOutput(null, null).getChemicalStack());
        }, () -> new ChemicalAmountedIngredient<>((S) new GasStack(MekanismGases.OXYGEN.get(), 1000)), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public ChemicalDissolutionIRecipe onInitialize(@Nullable ChemicalDissolutionIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new ChemicalDissolutionIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1),
                    new GasStack(MekanismGases.OXYGEN.get(), 1000));
        }
        return recipe;
    }

    @Override
    public boolean isValid(ChemicalDissolutionIRecipe recipe) {
        return !recipe.getItemInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(ChemicalDissolutionIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(ChemicalDissolutionIRecipe recipe, String id) {
        return "<recipetype:mekanism:dissolution>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getGasInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutput(null, null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(ChemicalDissolutionIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.ITEM_INPUT, recipe.getItemInput().serialize());
        json.add(JsonConstants.GAS_INPUT, recipe.getGasInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeBoxedChemicalStack(recipe.getOutput(null, null)));
        return json;
    }

    @Override
    public ChemicalDissolutionIRecipe getWithId(ChemicalDissolutionIRecipe r, ResourceLocation id) {
        return new ChemicalDissolutionIRecipe(id, r.getItemInput(), r.getGasInput(), r.getOutput(null, null).getChemicalStack());
    }

    @Override
    public ItemStack getMainOutput(ChemicalDissolutionIRecipe recipe) {
        return new ItemStack(MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER);
    }
}
