package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.InfusionConversionIRecipe;
import mekanism.common.registries.MekanismInfuseTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InfuseTypeConvertingRecipeType extends SupportedRecipeType<InfusionConversionIRecipe> {

    public InfuseTypeConvertingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "infusion_conversion"));

        addAreaScrollAmountEmptyRightClick(6, 24, 17, 17, (r, am) -> {
            return new InfusionConversionIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getOutput(null));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(113, 1, 18, 60, (r, input) -> {
            InfusionStack stack = input.toStack();
            return new InfusionConversionIRecipe(r.getId(), r.getInput(), stack.getType() == r.getOutput(null).getType() ? stack : new InfusionStack(stack, r.getOutput(null).getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null));
        }, () -> new ChemicalAmountedIngredient<>(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 10)), (stack, up) ->
                MekanismRecipeUtils.chemicalAmountSetter(stack, up, 1, 10, 100));
    }

    @Override
    public InfusionConversionIRecipe onInitialize(@Nullable InfusionConversionIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new InfusionConversionIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 10));
        }
        return recipe;
    }

    @Override
    public boolean isValid(InfusionConversionIRecipe recipe) {
        return !recipe.getInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(InfusionConversionIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(InfusionConversionIRecipe recipe, String id) {
        return "<recipetype:mekanism:infusion_conversion>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutput(null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(InfusionConversionIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeInfusionStack(recipe.getOutput(null)));
        return json;
    }

    @Override
    public InfusionConversionIRecipe getWithId(InfusionConversionIRecipe r, ResourceLocation id) {
        return new InfusionConversionIRecipe(id, r.getInput(), r.getOutput(null));
    }

    @Override
    public ItemStack getMainOutput(InfusionConversionIRecipe recipe) {
        return convertUnset(MekanismRecipeUtils.of(recipe.getInput()).asStack());
    }
}