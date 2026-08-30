package de.bommels05.ctgui.compat.create;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class CreateRecipeUtils {

    public static boolean ingredientsAndResultsValid(ProcessingRecipe<?, ?> recipe) {
        return recipe.getIngredients().stream().noneMatch(Ingredient::isEmpty) && recipe.getRollableResultsAsItemStacks().stream().noneMatch(ItemStack::isEmpty);
    }

    public static NonNullList<ProcessingOutput> limitOutputs(ProcessingRecipe<?, ?> recipe, int outputCount) {
        NonNullList<ProcessingOutput> outputs = NonNullList.withSize(outputCount, ProcessingOutput.EMPTY);
        List<ProcessingOutput> oldOutputs = recipe.getRollableResults();
        for (int i = 0; i < outputCount && i < oldOutputs.size(); i++) {
            outputs.set(i, oldOutputs.get(i));
        }
        return outputs;
    }

    public static NonNullList<ProcessingOutput> whereOutput(ProcessingRecipe<?, ?> recipe, ItemStack output, int i) {
        List<ProcessingOutput> outputs = new ArrayList<>(recipe.getRollableResults());
        outputs.set(i, new ProcessingOutput(output, 1));
        return NonNullList.copyOf(outputs);
    }

}
