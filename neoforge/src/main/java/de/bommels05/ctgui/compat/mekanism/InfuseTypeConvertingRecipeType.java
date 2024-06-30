package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.recipes.basic.BasicItemStackToInfuseTypeRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ItemStackToInfuseTypeEmiRecipe;
import mekanism.common.registries.MekanismInfuseTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class InfuseTypeConvertingRecipeType extends SupportedRecipeType<BasicItemStackToInfuseTypeRecipe> {

    public InfuseTypeConvertingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "infusion_conversion"));

        addAreaScrollAmountEmptyRightClick(6, 24, 17, 17, (r, am) -> {
            return new BasicItemStackToInfuseTypeRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getOutputRaw());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(113, 1, 18, 60, (r, input) -> {
            InfusionStack stack = input.toStack();
            return new BasicItemStackToInfuseTypeRecipe(r.getInput(), stack.getType() == r.getOutputRaw().getType() ? stack : new InfusionStack(stack, r.getOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient<>(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 10)), (stack, up) ->
                MekanismRecipeUtils.chemicalAmountSetter(stack, up, 1, 10, 100));
    }

    @Override
    public BasicItemStackToInfuseTypeRecipe onInitialize(@Nullable BasicItemStackToInfuseTypeRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicItemStackToInfuseTypeRecipe(IngredientCreatorAccess.item().from(UNSET), new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 10));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicItemStackToInfuseTypeRecipe recipe) {
        return !recipe.getInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicItemStackToInfuseTypeRecipe recipe) throws UnsupportedViewerException {
        return new ItemStackToInfuseTypeEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "infusion_conversion")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicItemStackToInfuseTypeRecipe recipe, String id) {
        return "<recipetype:mekanism:infusion_conversion>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicItemStackToInfuseTypeRecipe recipe) {
        return convertUnset(MekanismRecipeUtils.of(recipe.getInput()).asStack());
    }
}
