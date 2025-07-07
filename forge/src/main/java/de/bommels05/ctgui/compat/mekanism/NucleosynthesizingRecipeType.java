package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.IntegerRecipeOption;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.NucleosynthesizingIRecipe;
import mekanism.common.registries.MekanismGases;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NucleosynthesizingRecipeType extends SupportedRecipeType<NucleosynthesizingIRecipe> {

    private final IntegerRecipeOption<NucleosynthesizingIRecipe> duration = new IntegerRecipeOption<>(Component.translatable("ctgui.editing.options.duration"), 1);

    public NucleosynthesizingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "antiprotonic_nucleosynthesizer"));


        addAreaScrollAmountEmptyRightClick(20, 22, 17, 17, (r, am) -> {
            return new NucleosynthesizingIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getResultItem(regAccess()), r.getDuration());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(146, 22, 17, 17, (r, am) -> {
            return new NucleosynthesizingIRecipe(r.getId(), r.getItemInput(), r.getChemicalInput(), convertToUnset(am.asStack()), r.getDuration());
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getResultItem(regAccess())));
        });
        addAreaScrollAmountEmptyRightClick(-1, 0, 18, 60, (r, stack) -> {
            return new NucleosynthesizingIRecipe(r.getId(), r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getResultItem(regAccess()), r.getDuration());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.ANTIMATTER.get(), 2)), (stack, up) ->
                MekanismRecipeUtils.chemicalAmountSetter(stack, up, 1, 10));

        addOption(duration, (r, duration) -> {
            return new NucleosynthesizingIRecipe(r.getId(), r.getItemInput(), r.getChemicalInput(), r.getResultItem(regAccess()), duration);
        });
    }

    @Override
    public NucleosynthesizingIRecipe onInitialize(@Nullable NucleosynthesizingIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            duration.set(500);
            return new NucleosynthesizingIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.gas().from(MekanismGases.ANTIMATTER, 2), UNSET, 500);
        }
        duration.set(recipe.getDuration());
        return recipe;
    }

    @Override
    public boolean isValid(NucleosynthesizingIRecipe recipe) {
        return !recipe.getItemInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getResultItem(regAccess()), UNSET);
    }

    @Override
    public Object getEmiRecipe(NucleosynthesizingIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(NucleosynthesizingIRecipe recipe, String id) {
        return "<recipetype:mekanism:nucleosynthesizing>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + getCTString(recipe.getResultItem(regAccess())) + ", " + recipe.getDuration() + ");";
    }

    @Override
    public JsonObject getRecipeJson(NucleosynthesizingIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.ITEM_INPUT, recipe.getItemInput().serialize());
        json.add(JsonConstants.GAS_INPUT, recipe.getChemicalInput().serialize());
        json.addProperty(JsonConstants.DURATION, recipe.getDuration());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(recipe.getResultItem(regAccess())));
        return json;
    }

    @Override
    public NucleosynthesizingIRecipe getWithId(NucleosynthesizingIRecipe r, ResourceLocation id) {
        return new NucleosynthesizingIRecipe(id, r.getItemInput(), r.getChemicalInput(), r.getResultItem(regAccess()), r.getDuration());
    }
}
