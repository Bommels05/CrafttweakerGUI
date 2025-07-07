package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.MetallurgicInfuserIRecipe;
import mekanism.common.registries.MekanismInfuseTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InfusingRecipeType extends SupportedRecipeType<MetallurgicInfuserIRecipe> {

    public InfusingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "metallurgic_infuser"));

        addAreaScrollAmountEmptyRightClick(45, 26, 17, 17, (r, am) -> {
            return new MetallurgicInfuserIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getResultItem(regAccess()));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(103, 26, 17, 17, (r, am) -> {
            return new MetallurgicInfuserIRecipe(r.getId(), r.getItemInput(), r.getChemicalInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getResultItem(regAccess())));
        });
        addAreaScrollAmountEmptyRightClick(1, -2, 6, 54, (r, stack) -> {
            return new MetallurgicInfuserIRecipe(r.getId(), r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getResultItem(regAccess()));
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient<>(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 10)), (stack, up) ->
                MekanismRecipeUtils.chemicalAmountSetter(stack, up, 1, 10, 100));
    }

    @Override
    public MetallurgicInfuserIRecipe onInitialize(@Nullable MetallurgicInfuserIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new MetallurgicInfuserIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.infusion().from(MekanismInfuseTypes.REDSTONE, 10), UNSET);
        }
        return recipe;
    }

    @Override
    public boolean isValid(MetallurgicInfuserIRecipe recipe) {
        return !recipe.getItemInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getResultItem(regAccess()), UNSET);
    }

    @Override
    public Object getEmiRecipe(MetallurgicInfuserIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(MetallurgicInfuserIRecipe recipe, String id) {
        return "<recipetype:mekanism:metallurgic_infusing>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + getCTString(recipe.getResultItem(regAccess())) + ");";
    }

    public String getCraftTweakerRemoveString(MetallurgicInfuserIRecipe recipe, ResourceLocation id) {
        return "<recipetype:mekanism:metallurgic_infusing>.removeByName(\"" + id + "\");";
    }

    @Override
    public JsonObject getRecipeJson(MetallurgicInfuserIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.ITEM_INPUT, recipe.getItemInput().serialize());
        json.add(JsonConstants.CHEMICAL_INPUT, recipe.getChemicalInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(recipe.getResultItem(regAccess())));
        return json;
    }

    @Override
    public MetallurgicInfuserIRecipe getWithId(MetallurgicInfuserIRecipe r, ResourceLocation id) {
        return new MetallurgicInfuserIRecipe(id, r.getItemInput(), r.getChemicalInput(), r.getResultItem(regAccess()));
    }
}
