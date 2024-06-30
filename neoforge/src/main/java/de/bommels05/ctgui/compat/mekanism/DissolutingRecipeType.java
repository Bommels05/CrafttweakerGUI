package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.basic.BasicChemicalDissolutionRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ChemicalDissolutionEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class DissolutingRecipeType extends SupportedRecipeType<BasicChemicalDissolutionRecipe> {

    @SuppressWarnings("unchecked")
    public <S extends ChemicalStack<T>, T extends Chemical<T>> DissolutingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "dissolution"));

        addAreaScrollAmountEmptyRightClick(25, 33, 17, 17, (r, am) -> {
            return new BasicChemicalDissolutionRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getGasInput(), r.getOutputRaw().getChemicalStack());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(4, 1, 18, 60, (r, stack) -> {
            return new BasicChemicalDissolutionRecipe(r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getGasInput()), r.getOutputRaw().getChemicalStack());
        }, r -> {
            return MekanismRecipeUtils.of(r.getGasInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), (stack, up) -> MekanismRecipeUtils.chemicalAmountSetter(stack, up, 1, 10));
        addAreaScrollAmountEmptyRightClick(128, 10, 18, 60, (r, input) -> {
            ChemicalStack<?> stack = input.toStack();
            ChemicalStack<?> stack2 = stack.copy();
            if (stack.getType() != r.getOutputRaw().getChemicalStack().getType()) {
                stack2.setAmount(r.getOutputRaw().getChemicalStack().getAmount());
            }
            return new BasicChemicalDissolutionRecipe(r.getItemInput(), r.getGasInput(), stack2);
        }, r -> {
            return new ChemicalAmountedIngredient<>((S) r.getOutputRaw().getChemicalStack());
        }, () -> new ChemicalAmountedIngredient<>((S) new GasStack(MekanismGases.OXYGEN.get(), 1000)), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public BasicChemicalDissolutionRecipe onInitialize(@Nullable BasicChemicalDissolutionRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicChemicalDissolutionRecipe(IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1),
                    new GasStack(MekanismGases.OXYGEN.get(), 1000));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicChemicalDissolutionRecipe recipe) {
        return !recipe.getItemInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicChemicalDissolutionRecipe recipe) throws UnsupportedViewerException {
        return new ChemicalDissolutionEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "dissolution")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicChemicalDissolutionRecipe recipe, String id) {
        return "<recipetype:mekanism:dissolution>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getGasInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicChemicalDissolutionRecipe recipe) {
        return new ItemStack(MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER);
    }
}
