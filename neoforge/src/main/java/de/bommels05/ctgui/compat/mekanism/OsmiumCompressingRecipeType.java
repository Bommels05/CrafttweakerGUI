package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.BooleanRecipeOption;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicCompressingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ItemStackChemicalToItemStackEmiRecipe;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class OsmiumCompressingRecipeType extends SupportedRecipeType<BasicCompressingRecipe> {

    private final BooleanRecipeOption<BasicCompressingRecipe> perTickUsage = new BooleanRecipeOption<>(Component.translatable("ctgui.editing.options.per_tick_usage"), Component.translatable("ctgui.editing.options.per_tick_usage_chemical"));

    public OsmiumCompressingRecipeType() {
        super(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "compressing"));

        addAreaScrollAmountEmptyRightClick(36, 1, 17, 17, (r, am) -> {
            return new BasicCompressingRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getOutputRaw(), r.perTickUsage());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(88, 19, 17, 17, (r, am) -> {
            return new BasicCompressingRecipe(r.getItemInput(), r.getChemicalInput(), convertToUnset(am.asStack()), r.perTickUsage());
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputRaw()));
        });
        addAreaScrollAmountEmptyRightClick(40, 20, 6, 12, (r, stack) -> {
            return new BasicCompressingRecipe(r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getOutputRaw(), r.perTickUsage());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OSMIUM.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addOption(perTickUsage, (r, value) -> {
            return new BasicCompressingRecipe(r.getItemInput(), r.getChemicalInput(), r.getOutputRaw(), value);
        });
    }

    @Override
    public BasicCompressingRecipe onInitialize(@Nullable BasicCompressingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicCompressingRecipe(IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.chemicalStack().from(MekanismChemicals.OSMIUM, 1), UNSET, true);
        }
        perTickUsage.set(recipe.perTickUsage());
        return recipe;
    }

    @Override
    public boolean isValid(BasicCompressingRecipe recipe) {
        return !recipe.getItemInput().test(UNSET) && !ItemStack.isSameItemSameComponents(recipe.getOutputRaw(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicCompressingRecipe recipe) throws UnsupportedViewerException {
        return new ItemStackChemicalToItemStackEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "compressing")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicCompressingRecipe recipe, String id) {
        return "<recipetype:mekanism:compressing>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + getCTString(recipe.getOutputRaw()) + ", " + recipe.perTickUsage() + ");";
    }
}
