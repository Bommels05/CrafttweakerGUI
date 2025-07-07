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
import mekanism.common.recipe.impl.PigmentExtractingIRecipe;
import mekanism.common.registries.MekanismPigments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PigmentExtractingRecipeType extends SupportedRecipeType<PigmentExtractingIRecipe> {

    public PigmentExtractingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "pigment_extractor"));

        addAreaScrollAmountEmptyRightClick(6, 24, 17, 17, (r, am) -> {
            return new PigmentExtractingIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getOutput(null));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(113, 1, 18, 60, (r, input) -> {
            PigmentStack stack = input.toStack();
            return new PigmentExtractingIRecipe(r.getId(), r.getInput(), stack.getType() == r.getOutput(null).getType() ? stack : new PigmentStack(stack.getType(), r.getOutput(null).getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null));
        }, () -> new ChemicalAmountedIngredient<>(new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED).getChemical(), 100)), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public PigmentExtractingIRecipe onInitialize(@Nullable PigmentExtractingIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new PigmentExtractingIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED).getChemical(), 100));
        }
        return recipe;
    }

    @Override
    public boolean isValid(PigmentExtractingIRecipe recipe) {
        return !recipe.getInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(PigmentExtractingIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(PigmentExtractingIRecipe recipe, String id) {
        return "<recipetype:mekanism:pigment_extracting>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutput(null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(PigmentExtractingIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializePigmentStack(recipe.getOutput(null)));
        return json;
    }

    @Override
    public PigmentExtractingIRecipe getWithId(PigmentExtractingIRecipe r, ResourceLocation id) {
        return new PigmentExtractingIRecipe(id, r.getInput(), r.getOutput(null));
    }

    @Override
    public ItemStack getMainOutput(PigmentExtractingIRecipe recipe) {
        return convertUnset(MekanismRecipeUtils.of(recipe.getInput()).asStack());
    }
}
