package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicActivatingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ChemicalToChemicalEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class NeutronActivatingRecipeType extends SupportedRecipeType<BasicActivatingRecipe> {

    public NeutronActivatingRecipeType() {
        super(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "activating"));

        addAreaScrollAmountEmptyRightClick(21, 0, 18, 60, (r, stack) -> {
            return new BasicActivatingRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN, 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(129, 0, 18, 60, (r, input) -> {
            ChemicalStack stack = input.toStack();
            return new BasicActivatingRecipe(r.getInput(), stack.getChemical() == r.getOutputRaw().getChemical() ? stack : stack.copyWithAmount(r.getOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN, 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public BasicActivatingRecipe onInitialize(@Nullable BasicActivatingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicActivatingRecipe(IngredientCreatorAccess.chemicalStack().fromHolder(MekanismChemicals.OXYGEN, 1),
                    new ChemicalStack(MekanismChemicals.OXYGEN, 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicActivatingRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicActivatingRecipe recipe) throws UnsupportedViewerException {
        return new ChemicalToChemicalEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "activating")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicActivatingRecipe recipe, String id) {
        return "<recipetype:mekanism:activating>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicActivatingRecipe recipe) {
        return new ItemStack(MekanismBlocks.SOLAR_NEUTRON_ACTIVATOR);
    }
}
