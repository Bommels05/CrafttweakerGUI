package de.bommels05.ctgui.emi;

import com.mojang.logging.LogUtils;
import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.ViewerUtils;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeManager;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.registry.EmiRecipes;
import dev.emi.emi.screen.WidgetGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmiViewerUtils implements ViewerUtils {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static Map<EmiStack, List<EmiRecipe>> byInput;
    private static Map<EmiStack, List<EmiRecipe>> byOutput;
    private static Map<EmiRecipeCategory, List<EmiRecipe>> byCategory;
    private static Map<ResourceLocation, EmiRecipe> byId;

    @Override
    public <T extends Recipe<?>> void inject(ChangedRecipeManager.ChangedRecipe<T> recipe) {
        try {
            initFields();
            ResourceLocation id = new ResourceLocation(CraftTweakerGUI.MOD_ID, recipe.getId());
            EmiRecipes.recipeIds.put(recipe.getRecipe(), id);
            EmiRecipe r = recipe.getRecipeType().getEmiRecipe(recipe.getRecipe());
            r.getInputs().stream().map(EmiIngredient::getEmiStacks).forEach(stacks -> {
                for (EmiStack input : stacks) {
                    List<EmiRecipe> recipes = new ArrayList<>(byInput.getOrDefault(input, new ArrayList<>()));
                    recipes.add(r);
                    byInput.put(input, recipes);
                }
            });
            for (EmiStack output : r.getOutputs()) {
                List<EmiRecipe> recipes = new ArrayList<>(byOutput.getOrDefault(output, new ArrayList<>()));
                recipes.add(r);
                byOutput.put(output, recipes);
            }
            List<EmiRecipe> recipes = new ArrayList<>(byCategory.getOrDefault(r.getCategory(), new ArrayList<>()));
            recipes.add(r);
            byCategory.put(r.getCategory(), recipes);
            byId.put(id, r);
        } catch (Throwable t) {
            LOGGER.error("Could not inject recipe change at runtime", t);
        }
    }

    @Override
    public <T extends Recipe<?>> void unInject(ChangedRecipeManager.ChangedRecipe<T> recipe) {
        try {
            initFields();
            EmiRecipe r = recipe.getRecipeType().getEmiRecipe(recipe.getRecipe());
            ResourceLocation id = new ResourceLocation(CraftTweakerGUI.MOD_ID, recipe.getId());
            r.getInputs().stream().map(EmiIngredient::getEmiStacks).forEach(stacks -> {
                for (EmiStack input : stacks) {
                    List<EmiRecipe> recipes = new ArrayList<>(byInput.get(input).stream().filter(r2 -> !r2.getId().equals(id)).toList());
                    byInput.put(input, recipes);
                }
            });
            for (EmiStack output : r.getOutputs()) {
                List<EmiRecipe> recipes = new ArrayList<>(byOutput.get(output).stream().filter(r2 -> !r2.getId().equals(id)).toList());
                byOutput.put(output, recipes);
            }
            List<EmiRecipe> recipes = new ArrayList<>(byCategory.get(r.getCategory()).stream().filter(r2 -> !r2.getId().equals(id)).toList());
            byCategory.put(r.getCategory(), recipes);
            byId.remove(id);
        } catch (Throwable t) {
            LOGGER.error("Could not unInject recipe change at runtime", t);
        }
    }

    @SuppressWarnings("unchecked")
    private void initFields() throws NoSuchFieldException, IllegalAccessException {
        EmiRecipeManager manager = EmiApi.getRecipeManager();
        Class<? extends EmiRecipeManager> clazz = manager.getClass();
        Field byInputF = clazz.getDeclaredField("byInput");
        Field byOutputF = clazz.getDeclaredField("byOutput");
        Field byCategoryF = clazz.getDeclaredField("byCategory");
        Field byIdF = clazz.getDeclaredField("byId");
        byInputF.setAccessible(true);
        byOutputF.setAccessible(true);
        byCategoryF.setAccessible(true);
        byIdF.setAccessible(true);
        byInput = (Map<EmiStack, List<EmiRecipe>>) byInputF.get(manager);
        byOutput = (Map<EmiStack, List<EmiRecipe>>) byOutputF.get(manager);
        byCategory = (Map<EmiRecipeCategory, List<EmiRecipe>>) byCategoryF.get(manager);
        byId = (Map<ResourceLocation, EmiRecipe>) byIdF.get(manager);
    }

    public static int getPage(WidgetGroup group) {
        try {
            Class<?> pageSlot = Class.forName("dev.emi.emi.api.recipe.EmiIngredientRecipe$PageSlotWidget");
            for (Widget widget : group.widgets) {
                if (pageSlot.isAssignableFrom(widget.getClass())) {
                    Field managerField = pageSlot.getDeclaredField("manager");
                    managerField.setAccessible(true);
                    Object manager = managerField.get(widget);
                    Field pageField = manager.getClass().getDeclaredField("currentPage");
                    pageField.setAccessible(true);
                    return (int) pageField.get(manager);
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Could not determine page of tag recipe. This could cause problems while editing!", t);
        }
        return 0;
    }
}
