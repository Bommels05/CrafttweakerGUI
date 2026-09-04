package de.bommels05.ctgui.compat.create;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
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

public class MillingRecipeType extends SupportedRecipeType<MillingRecipe> {
    private final RangedRecipeOption<MillingRecipe> outputCount = new RangedRecipeOption<>(Component.translatable("ctgui.editing.options.output_count"), 1, 4);

    public MillingRecipeType() {
        super(CraftTweakerGUI.rl(Create.ID, "milling"));

        initAreas();
        addOption(outputCount, (r, value) -> {
            initAreas();
            return getBuilder().withItemIngredients(r.getIngredients()).withItemOutputs(CreateRecipeUtils.limitOutputs(r, value)).build();
        });
    }

    private void initAreas() {
        clearAreas();
        addAreaScrollAmountEmptyRightClick(15, 9, 17, 17, (r, am) -> {
            return getBuilder().require(am.ingredient()).withItemOutputs((NonNullList<ProcessingOutput>) r.getRollableResults()).build();
        }, r -> {
            return new AmountedIngredient(r.getIngredients().get(0), 1);
        });
        for (int i = 0; i < outputCount.get(); i++) {
            int xOffset = i % 2 == 0 ? 0 : 19;
            int yOffset = (i / 2) * -19;

            int outputIndex = i;
            addAreaScrollAmountEmptyRightClick(outputCount.get() == 1 ? 139 : 133 + xOffset, 27 + yOffset, 17, 17, (r, am) -> {
                return getBuilder().withItemIngredients(r.getIngredients()).withItemOutputs(CreateRecipeUtils.whereOutput(r, am.asStack(), outputIndex)).build();
            }, r -> {
                return AmountedIngredient.of(r.getRollableResultsAsItemStacks().get(outputIndex));
            });
        }
    }

    @Override
    public MillingRecipe onInitialize(@Nullable MillingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return getBuilder().require(Ingredient.EMPTY).output(ItemStack.EMPTY).build();
        }
        outputCount.set(recipe.getRollableResults().size());
        initAreas();
        return null;
    }

    @Override
    public boolean isValid(MillingRecipe recipe) {
        return CreateRecipeUtils.ingredientsAndResultsValid(recipe);
    }

    @Override
    public Object getEmiRecipe(MillingRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(MillingRecipe recipe, String id) {
        return getCTJsonString(recipe, id);
    }

    private StandardProcessingRecipe.Builder<MillingRecipe> getBuilder() {
        return new StandardProcessingRecipe.Builder<>(MillingRecipe::new, nullRl());
    }
}
