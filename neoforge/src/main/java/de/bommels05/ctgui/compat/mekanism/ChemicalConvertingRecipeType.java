package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicChemicalConversionRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ItemStackToChemicalEmiRecipe;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.tile.machine.TileEntityChemicalOxidizer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class ChemicalConvertingRecipeType extends SupportedRecipeType<BasicChemicalConversionRecipe> {

    public ChemicalConvertingRecipeType() {
        super(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "chemical_conversion"));

        addAreaScrollAmountEmptyRightClick(6, 24, 17, 17, (r, am) -> {
            return new BasicChemicalConversionRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getOutputRaw());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(111, 1, 18, 60, (r, input) -> {
            ChemicalStack stack = input.toStack();
            return new BasicChemicalConversionRecipe(r.getInput(), stack.getChemical() == r.getOutputRaw().getChemical() ? stack : stack.copyWithAmount(r.getOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN, 100)), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public BasicChemicalConversionRecipe onInitialize(@Nullable BasicChemicalConversionRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicChemicalConversionRecipe(IngredientCreatorAccess.item().from(UNSET), new ChemicalStack(MekanismChemicals.OXYGEN, 100));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicChemicalConversionRecipe recipe) {
        return !recipe.getInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicChemicalConversionRecipe recipe) throws UnsupportedViewerException {
        return new ItemStackToChemicalEmiRecipe<>((MekanismEmiRecipeCategory) getEmiCategory(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "gas_conversion")), new RecipeHolder<>(nullRl(), recipe), TileEntityChemicalOxidizer.BASE_TICKS_REQUIRED);
    }

    @Override
    public String getCraftTweakerString(BasicChemicalConversionRecipe recipe, String id) {
        return "<recipetype:mekanism:chemical_conversion>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicChemicalConversionRecipe recipe) {
        return convertUnset(MekanismRecipeUtils.of(recipe.getInput()).asStack());
    }
}
