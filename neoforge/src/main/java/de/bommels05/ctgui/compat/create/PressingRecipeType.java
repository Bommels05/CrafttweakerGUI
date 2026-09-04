package de.bommels05.ctgui.compat.create;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.RangedRecipeOption;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class PressingRecipeType extends SupportedRecipeType<PressingRecipe> {
    private final RangedRecipeOption<PressingRecipe> outputCount = new RangedRecipeOption<>(Component.translatable("ctgui.editing.options.output_count"), 1, 2);

    public PressingRecipeType() {
        super(CraftTweakerGUI.rl(Create.ID, "pressing"));

        initAreas();
        addOption(outputCount, (r, value) -> {
            initAreas();
            return getBuilder().withItemIngredients(r.getIngredients()).withItemOutputs(CreateRecipeUtils.limitOutputs(r, value)).build();
        });
    }

    private void initAreas() {
        clearAreas();
        addAreaEmptyRightClick(27, 51, 17, 17, (r, am) -> {
            return getBuilder().require(am.ensureAmount(1, 1).ingredient()).withItemOutputs((NonNullList<ProcessingOutput>) r.getRollableResults()).build();
        }, r -> {
            return new AmountedIngredient(r.getIngredients().get(0), 1);
        });

        for (int i = 0; i < outputCount.get(); i++) {
            int outputIndex = i;
            addAreaScrollAmountEmptyRightClick(131 + i * 19, 50, 17, 17, (r, am) -> {
                return getBuilder().withItemIngredients(r.getIngredients()).withItemOutputs(CreateRecipeUtils.whereOutput(r, am.asStack(), outputIndex)).build();
            }, r -> {
                return AmountedIngredient.of(r.getRollableResultsAsItemStacks().get(outputIndex));
            });
        }
    }

    @Override
    public PressingRecipe onInitialize(@Nullable PressingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return getBuilder().require(Ingredient.EMPTY).output(ItemStack.EMPTY).build();
        }
        outputCount.set(recipe.getRollableResults().size());
        initAreas();
        return null;
    }

    @Override
    public boolean isValid(PressingRecipe recipe) {
        return CreateRecipeUtils.ingredientsAndResultsValid(recipe);
    }

    @Override
    public Object getEmiRecipe(PressingRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(PressingRecipe recipe, String id) {
        return getCTJsonString(recipe, id);
    }

    private StandardProcessingRecipe.Builder<PressingRecipe> getBuilder() {
        return new StandardProcessingRecipe.Builder<>(PressingRecipe::new, nullRl());
    }
}
