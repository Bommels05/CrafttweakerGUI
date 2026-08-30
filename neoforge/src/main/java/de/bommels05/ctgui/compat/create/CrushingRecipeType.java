package de.bommels05.ctgui.compat.create;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
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

public class CrushingRecipeType extends SupportedRecipeType<AbstractCrushingRecipe> {
    private final RangedRecipeOption<AbstractCrushingRecipe> outputCount = new RangedRecipeOption<>(Component.translatable("ctgui.editing.options.output_count"), 1, 7);

    public CrushingRecipeType() {
        super(CraftTweakerGUI.rl(Create.ID, "crushing"));

        initAreas();
        addOption(outputCount, (r, value) -> {
            initAreas();
            return getBuilder().withItemIngredients(r.getIngredients()).withItemOutputs(CreateRecipeUtils.limitOutputs(r, value)).build();
        });
    }

    private void initAreas() {
        clearAreas();
        addAreaScrollAmountEmptyRightClick(51, 3, 17, 17, (r, am) -> {
            return getBuilder().require(am.ingredient()).withItemOutputs((NonNullList<ProcessingOutput>) r.getRollableResults()).build();
        }, r -> {
            return new AmountedIngredient(r.getIngredients().get(0), 1);
        });
        int width = outputCount.get() * 18 + (outputCount.get() - 1);
        int left = (177 / 2) - (width / 2);
        for (int i = 0; i < outputCount.get(); i++) {
            int outputIndex = i;
            addAreaScrollAmountEmptyRightClick(left + i * 19, 78, 17, 17, (r, am) -> {
                return getBuilder().withItemIngredients(r.getIngredients()).withItemOutputs(CreateRecipeUtils.whereOutput(r, am.asStack(), outputIndex)).build();
            }, r -> {
                return AmountedIngredient.of(r.getRollableResultsAsItemStacks().get(outputIndex));
            });
        }
    }

    @Override
    public AbstractCrushingRecipe onInitialize(@Nullable AbstractCrushingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return getBuilder().require(Ingredient.EMPTY).output(ItemStack.EMPTY).build();
        } else if (recipe instanceof MillingRecipe) {
            throw new UnsupportedRecipeException(Component.translatable("ctgui.editing.milling_recipe"));
        } else if (!(recipe instanceof CrushingRecipe)) {
            throw new UnsupportedRecipeException(Component.translatable("ctgui.editing.unsupported"));
        }
        return null;
    }

    @Override
    public boolean isValid(AbstractCrushingRecipe recipe) {
        return CreateRecipeUtils.ingredientsAndResultsValid(recipe);
    }

    @Override
    public Object getEmiRecipe(AbstractCrushingRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(AbstractCrushingRecipe recipe, String id) {
        return getCTJsonString(recipe, id);
    }

    private StandardProcessingRecipe.Builder<CrushingRecipe> getBuilder() {
        return new StandardProcessingRecipe.Builder<>(CrushingRecipe::new, nullRl());
    }
}
