package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.DoubleRecipeOption;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.recipes.SawmillRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.SawmillIRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SawingRecipeType extends SupportedRecipeType<SawmillIRecipe> {

    private final DoubleRecipeOption<SawmillIRecipe> chance = new DoubleRecipeOption<>(Component.translatable("ctgui.editing.options.secondary_output_chance"), 0, 1);

    public SawingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "precision_sawmill"));

        addAreaScrollAmountEmptyRightClick(27, 0, 16, 17, (r, am) -> {
            return new SawmillIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getOutput(null).getMainOutput(),
                    r.getOutput(null).getMaxSecondaryOutput(), verifyChance(chance.get(), r));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(87, 18, 16, 17, (r, am) -> {
            ItemStack output = (r.getOutput(null).getMaxSecondaryOutput().isEmpty() || convertUnset(r.getOutput(null).getMaxSecondaryOutput()).isEmpty()) && am.isEmpty() ? UNSET : am.asStack();
            ItemStack secondaryOutput = convertUnset(r.getOutput(null).getMaxSecondaryOutput());
            return new SawmillIRecipe(r.getId(), r.getInput(), output,
                    secondaryOutput, verifyChance(chance.get(), secondaryOutput, output));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutput(null).getMainOutput()));
        });
        addAreaScrollAmountEmptyRightClick(103, 18, 17, 17, (r, am) -> {
            ItemStack output = (r.getOutput(null).getMainOutput().isEmpty() || convertUnset(r.getOutput(null).getMainOutput()).isEmpty()) && am.isEmpty() ? UNSET : am.asStack();
            ItemStack mainOutput = convertUnset(r.getOutput(null).getMainOutput());
            return new SawmillIRecipe(r.getId(), r.getInput(), mainOutput,
                   output, verifyChance(chance.get(), output, mainOutput));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutput(null).getMaxSecondaryOutput()));
        });
        addOption(chance, (r, chance) -> {
            return new SawmillIRecipe(r.getId(), r.getInput(), r.getOutput(null).getMainOutput(), r.getOutput(null).getMaxSecondaryOutput(), verifyChance(chance, r));
        });
    }

    @Override
    public SawmillIRecipe onInitialize(@Nullable SawmillIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new SawmillIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), UNSET, ItemStack.EMPTY, 0);
        }
        chance.set(recipe.getSecondaryChance());
        return recipe;
    }

    @Override
    public boolean isValid(SawmillIRecipe recipe) {
        return !recipe.getInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getOutput(null).getMainOutput().isEmpty() ? recipe.getOutput(null).getMaxSecondaryOutput() : recipe.getOutput(null).getMainOutput(), UNSET);
    }

    @Override
    public Object getEmiRecipe(SawmillIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(SawmillIRecipe recipe, String id) {
        return "<recipetype:mekanism:sawing>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + getCTString(recipe.getOutput(null).getMainOutput()) +
                (recipe.getOutput(null).getMaxSecondaryOutput().isEmpty() ? "" : ", " + getCTString(recipe.getOutput(null).getMaxSecondaryOutput()) + ", " + recipe.getSecondaryChance()) + ");";
    }

    @Override
    public JsonObject getRecipeJson(SawmillIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());

        SawmillRecipe.ChanceOutput output = recipe.getOutput(null);
        if (!output.getMainOutput().isEmpty()) {
            json.add(JsonConstants.MAIN_OUTPUT, SerializerHelper.serializeItemStack(output.getMainOutput()));
        }
        if (!output.getMaxSecondaryOutput().isEmpty()) {
            json.addProperty(JsonConstants.SECONDARY_CHANCE, recipe.getSecondaryChance());
            json.add(JsonConstants.SECONDARY_OUTPUT, SerializerHelper.serializeItemStack(output.getMaxSecondaryOutput()));
        }
        return json;
    }

    @Override
    public SawmillIRecipe getWithId(SawmillIRecipe r, ResourceLocation id) {
        return new SawmillIRecipe(id, r.getInput(), r.getOutput(null).getMainOutput(), r.getOutput(null).getMaxSecondaryOutput(), r.getSecondaryChance());
    }

    @Override
    public ItemStack getMainOutput(SawmillIRecipe recipe) {
        return convertUnset(recipe.getOutput(null).getMainOutput().isEmpty() ? recipe.getOutput(null).getMaxSecondaryOutput() : recipe.getOutput(null).getMainOutput());
    }

    private double verifyChance(double chance, SawmillIRecipe recipe) {
        return verifyChance(chance, recipe.getOutput(null).getMaxSecondaryOutput(), recipe.getOutput(null).getMainOutput());
    }

    private double verifyChance(double chance, ItemStack secondaryOutput, ItemStack mainOutput) {
        return secondaryOutput.isEmpty() ? 0 : (mainOutput.isEmpty() ? Mth.clamp(chance, 0.01, 0.99) : chance);
    }
}
