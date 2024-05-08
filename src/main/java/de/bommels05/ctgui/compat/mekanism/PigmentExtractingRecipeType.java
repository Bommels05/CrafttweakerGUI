package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.recipes.basic.BasicItemStackToPigmentRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.text.EnumColor;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ItemStackToPigmentEmiRecipe;
import mekanism.common.registries.MekanismPigments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class PigmentExtractingRecipeType extends SupportedRecipeType<BasicItemStackToPigmentRecipe> {

    public PigmentExtractingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "pigment_extracting"));

        addAreaScrollAmountEmptyRightClick(6, 24, 17, 17, (r, am) -> {
            return new BasicItemStackToPigmentRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getOutputRaw());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(113, 1, 18, 60, (r, stack) -> {
            return new BasicItemStackToPigmentRecipe(r.getInput(), stack.getType() == r.getOutputRaw().getType() ? stack : new PigmentStack(stack, r.getOutputRaw().getAmount()));
        }, BasicItemStackToPigmentRecipe::getOutputRaw, () -> new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 100), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public BasicItemStackToPigmentRecipe onInitialize(@Nullable BasicItemStackToPigmentRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicItemStackToPigmentRecipe(IngredientCreatorAccess.item().from(UNSET), new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 100));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicItemStackToPigmentRecipe recipe) {
        return !recipe.getInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicItemStackToPigmentRecipe recipe) throws UnsupportedViewerException {
        return new ItemStackToPigmentEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "pigment_extracting")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicItemStackToPigmentRecipe recipe, String id) {
        return "<recipetype:mekanism:pigment_extracting>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicItemStackToPigmentRecipe recipe) {
        return convertUnset(MekanismRecipeUtils.of(recipe.getInput()).asStack());
    }
}
