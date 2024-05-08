package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.recipes.basic.BasicPaintingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.text.EnumColor;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.PaintingEmiRecipe;
import mekanism.common.registries.MekanismPigments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class PaintingRecipeType extends SupportedRecipeType<BasicPaintingRecipe> {

    public PaintingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "painting"));

        addAreaScrollAmountEmptyRightClick(20, 22, 17, 17, (r, am) -> {
            return new BasicPaintingRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getOutputRaw());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(91, 22, 17, 17, (r, am) -> {
            return new BasicPaintingRecipe(r.getItemInput(), r.getChemicalInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputRaw()));
        });
        addAreaScrollAmountEmptyRightClick(0, 0, 18, 60, (r, stack) -> {
            return new BasicPaintingRecipe(r.getItemInput(), IngredientCreatorAccess.pigment().from(stack.getType() == MekanismRecipeUtils.of(r.getChemicalInput()).getType() ? stack : new PigmentStack(stack, MekanismRecipeUtils.getAmount(r.getChemicalInput()))), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 50), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public BasicPaintingRecipe onInitialize(@Nullable BasicPaintingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicPaintingRecipe(IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.pigment().from(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 50), UNSET);
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicPaintingRecipe recipe) {
        return !recipe.getItemInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getOutputRaw(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicPaintingRecipe recipe) throws UnsupportedViewerException {
        return new PaintingEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "painting")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicPaintingRecipe recipe, String id) {
        return "<recipetype:mekanism:painting>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + getCTString(recipe.getOutputRaw()) + ");";
    }
}
