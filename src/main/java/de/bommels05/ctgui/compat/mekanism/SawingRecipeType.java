package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.DoubleRecipeOption;
import mekanism.api.MekanismAPI;
import mekanism.api.recipes.basic.BasicCrushingRecipe;
import mekanism.api.recipes.basic.BasicSawmillRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ItemStackToItemStackEmiRecipe;
import mekanism.client.recipe_viewer.emi.recipe.SawmillEmiRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class SawingRecipeType extends SupportedRecipeType<BasicSawmillRecipe> {

    private final DoubleRecipeOption<BasicSawmillRecipe> chance = new DoubleRecipeOption<>(Component.translatable("ctgui.editing.options.secondary_output_chance"), 0, 1);

    public SawingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "sawing"));

        addAreaScrollAmountEmptyRightClick(27, 0, 17, 17, (r, am) -> {
            return new BasicSawmillRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getMainOutputRaw().orElse(ItemStack.EMPTY),
                    r.getSecondaryOutputRaw().orElse(ItemStack.EMPTY), verifyChance(chance.get(), r));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(87, 18, 17, 17, (r, am) -> {
            ItemStack output = (r.getSecondaryOutputRaw().isEmpty() || convertUnset(r.getSecondaryOutputRaw().get()).isEmpty()) && am.isEmpty() ? UNSET : am.asStack();
            ItemStack secondaryOutput = convertUnset(r.getSecondaryOutputRaw().orElse(ItemStack.EMPTY));
            return new BasicSawmillRecipe(r.getInput(), output,
                    secondaryOutput, verifyChance(chance.get(), secondaryOutput, output));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getMainOutputRaw().orElse(ItemStack.EMPTY)));
        });
        addAreaScrollAmountEmptyRightClick(103, 18, 17, 17, (r, am) -> {
            ItemStack output = (r.getMainOutputRaw().isEmpty() || convertUnset(r.getMainOutputRaw().get()).isEmpty()) && am.isEmpty() ? UNSET : am.asStack();
            ItemStack mainOutput = convertUnset(r.getMainOutputRaw().orElse(ItemStack.EMPTY));
            return new BasicSawmillRecipe(r.getInput(), mainOutput,
                   output, verifyChance(chance.get(), output, mainOutput));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getSecondaryOutputRaw().orElse(ItemStack.EMPTY)));
        });
        addOption(chance, (r, chance) -> {
            return new BasicSawmillRecipe(r.getInput(), r.getMainOutputRaw().orElse(ItemStack.EMPTY), r.getSecondaryOutputRaw().orElse(ItemStack.EMPTY), verifyChance(chance, r));
        });
    }

    @Override
    public BasicSawmillRecipe onInitialize(@Nullable BasicSawmillRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicSawmillRecipe(IngredientCreatorAccess.item().from(UNSET), UNSET, ItemStack.EMPTY, 0);
        }
        chance.set(recipe.getSecondaryChance());
        return recipe;
    }

    @Override
    public boolean isValid(BasicSawmillRecipe recipe) {
        return !recipe.getInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getMainOutputRaw().isPresent() ? recipe.getMainOutputRaw().get() : recipe.getSecondaryOutputRaw().get(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicSawmillRecipe recipe) throws UnsupportedViewerException {
        return new SawmillEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "sawing")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicSawmillRecipe recipe, String id) {
        return "<recipetype:mekanism:sawing>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + getCTString(recipe.getMainOutputRaw().orElse(ItemStack.EMPTY)) + ", " + getCTString(recipe.getSecondaryOutputRaw().orElse(ItemStack.EMPTY)) + ", " + recipe.getSecondaryChance() + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicSawmillRecipe recipe) {
        return recipe.getMainOutputRaw().or(recipe::getSecondaryOutputRaw).orElse(ItemStack.EMPTY);
    }

    private double verifyChance(double chance, BasicSawmillRecipe recipe) {
        return verifyChance(chance, recipe.getSecondaryOutputRaw().orElse(ItemStack.EMPTY), recipe.getMainOutputRaw().orElse(ItemStack.EMPTY));
    }

    private double verifyChance(double chance, ItemStack secondaryOutput, ItemStack mainOutput) {
        return secondaryOutput.isEmpty() ? 0 : (mainOutput.isEmpty() ? Mth.clamp(chance, 0.01, 0.99) : chance);
    }
}
