package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.text.EnumColor;
import mekanism.common.recipe.impl.PaintingIRecipe;
import mekanism.common.registries.MekanismPigments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PaintingRecipeType extends SupportedRecipeType<PaintingIRecipe> {

    public PaintingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "painting_machine"));

        addAreaScrollAmountEmptyRightClick(20, 22, 17, 17, (r, am) -> {
            return new PaintingIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getResultItem(regAccess()));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(91, 22, 17, 17, (r, am) -> {
            return new PaintingIRecipe(r.getId(), r.getItemInput(), r.getChemicalInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getResultItem(regAccess())));
        });
        addAreaScrollAmountEmptyRightClick(0, 0, 18, 60, (r, stack) -> {
            return new PaintingIRecipe(r.getId(), r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getResultItem(regAccess()));
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient<>(new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED).getChemical(), 50)), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public PaintingIRecipe onInitialize(@Nullable PaintingIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new PaintingIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.pigment().from(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 50), UNSET);
        }
        return recipe;
    }

    @Override
    public boolean isValid(PaintingIRecipe recipe) {
        return !recipe.getItemInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getResultItem(regAccess()), UNSET);
    }

    @Override
    public Object getEmiRecipe(PaintingIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(PaintingIRecipe recipe, String id) {
        return "<recipetype:mekanism:painting>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + getCTString(recipe.getResultItem(regAccess())) + ");";
    }

    @Override
    public JsonObject getRecipeJson(PaintingIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.ITEM_INPUT, recipe.getItemInput().serialize());
        json.add(JsonConstants.CHEMICAL_INPUT, recipe.getChemicalInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(recipe.getResultItem(regAccess())));
        return json;
    }

    @Override
    public PaintingIRecipe getWithId(PaintingIRecipe r, ResourceLocation id) {
        return new PaintingIRecipe(id, r.getItemInput(), r.getChemicalInput(), r.getResultItem(regAccess()));
    }
}
