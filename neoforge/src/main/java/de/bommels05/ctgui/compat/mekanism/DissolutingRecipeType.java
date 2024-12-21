package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.BooleanRecipeOption;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicChemicalDissolutionRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ChemicalDissolutionEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class DissolutingRecipeType extends SupportedRecipeType<BasicChemicalDissolutionRecipe> {

    private final BooleanRecipeOption<BasicChemicalDissolutionRecipe> perTickUsage = new BooleanRecipeOption<>(Component.translatable("ctgui.editing.options.per_tick_usage"), Component.translatable("ctgui.editing.options.per_tick_usage_chemical"));

    public DissolutingRecipeType() {
        super(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "dissolution"));

        addAreaScrollAmountEmptyRightClick(25, 33, 17, 17, (r, am) -> {
            return new BasicChemicalDissolutionRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getOutputRaw(), r.perTickUsage());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(4, 1, 18, 60, (r, stack) -> {
            return new BasicChemicalDissolutionRecipe(r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getOutputRaw(), r.perTickUsage());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN.get(), 1)), (stack, up) -> MekanismRecipeUtils.chemicalAmountSetter(stack, up, 1, 10));
        addAreaScrollAmountEmptyRightClick(128, 10, 18, 60, (r, input) -> {
            ChemicalStack stack = input.toStack();
            return new BasicChemicalDissolutionRecipe(r.getItemInput(), r.getChemicalInput(), stack.getChemical() == r.getOutputRaw().getChemical() ? stack : stack.copyWithAmount(r.getOutputRaw().getAmount()), r.perTickUsage());
        }, r -> {
            return new ChemicalAmountedIngredient(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN.get(), 1000)), MekanismRecipeUtils::chemicalAmountSetter);
        addOption(perTickUsage, (r, value) -> {
            return new BasicChemicalDissolutionRecipe(r.getItemInput(), r.getChemicalInput(), r.getOutputRaw(), value);
        });
    }

    @Override
    public BasicChemicalDissolutionRecipe onInitialize(@Nullable BasicChemicalDissolutionRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            perTickUsage.set(true);
            return new BasicChemicalDissolutionRecipe(IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.chemicalStack().from(MekanismChemicals.OXYGEN, 1),
                    new ChemicalStack(MekanismChemicals.OXYGEN.get(), 1000), true);
        }
        perTickUsage.set(recipe.perTickUsage());
        return recipe;
    }

    @Override
    public boolean isValid(BasicChemicalDissolutionRecipe recipe) {
        return !recipe.getItemInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicChemicalDissolutionRecipe recipe) throws UnsupportedViewerException {
        return new ChemicalDissolutionEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "dissolution")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicChemicalDissolutionRecipe recipe, String id) {
        return "<recipetype:mekanism:dissolution>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ", " + recipe.perTickUsage() + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicChemicalDissolutionRecipe recipe) {
        return new ItemStack(MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER);
    }
}
