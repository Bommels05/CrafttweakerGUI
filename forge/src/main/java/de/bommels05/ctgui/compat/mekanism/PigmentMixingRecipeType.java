package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.text.EnumColor;
import mekanism.common.recipe.impl.PigmentMixingIRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismPigments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PigmentMixingRecipeType extends SupportedRecipeType<PigmentMixingIRecipe> {

    public PigmentMixingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "pigment_mixer"));

        addAreaScrollAmountEmptyRightClick(22, 10, 18, 60, (r, stack) -> {
            return new PigmentMixingIRecipe(r.getId(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getLeftInput()), r.getRightInput(), r.getOutput(null, null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getLeftInput());
        }, () -> new ChemicalAmountedIngredient<>(new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED).getChemical(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(130, 10, 18, 60, (r, stack) -> {
            return new PigmentMixingIRecipe(r.getId(), r.getLeftInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getRightInput()), r.getOutput(null, null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getRightInput());
        }, () -> new ChemicalAmountedIngredient<>(new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED).getChemical(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(76, 1, 18, 60, (r, input) -> {
            PigmentStack stack = input.toStack();
            return new PigmentMixingIRecipe(r.getId(), r.getLeftInput(), r.getRightInput(), stack.getType() == r.getOutput(null, null).getType() ? stack : new PigmentStack(stack.getType(), r.getOutput(null, null).getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null, null));
        }, () -> new ChemicalAmountedIngredient<>(new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED).getChemical(), 2)), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public PigmentMixingIRecipe onInitialize(@Nullable PigmentMixingIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new PigmentMixingIRecipe(nullRl(), IngredientCreatorAccess.pigment().from(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 1),
                    IngredientCreatorAccess.pigment().from(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 1),
                    new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED).getChemical(), 2));
        }
        return recipe;
    }

    @Override
    public boolean isValid(PigmentMixingIRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(PigmentMixingIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(PigmentMixingIRecipe recipe, String id) {
        return "<recipetype:mekanism:pigment_mixing>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getLeftInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getRightInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutput(null, null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(PigmentMixingIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.LEFT_INPUT, recipe.getLeftInput().serialize());
        json.add(JsonConstants.RIGHT_INPUT, recipe.getRightInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializePigmentStack(recipe.getOutput(null, null)));
        return json;
    }

    @Override
    public PigmentMixingIRecipe getWithId(PigmentMixingIRecipe r, ResourceLocation id) {
        return new PigmentMixingIRecipe(id, r.getLeftInput(), r.getRightInput(), r.getOutput(null, null));
    }

    @Override
    public ItemStack getMainOutput(PigmentMixingIRecipe recipe) {
        return new ItemStack(MekanismBlocks.PIGMENT_MIXER);
    }
}
