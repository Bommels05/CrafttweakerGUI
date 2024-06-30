package de.bommels05.ctgui.emi;

import com.mojang.logging.LogUtils;
import de.bommels05.ctgui.*;
import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.jei.JeiViewerUtils;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeManager;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.jemi.JemiRecipe;
import dev.emi.emi.recipe.EmiTagRecipe;
import dev.emi.emi.registry.EmiRecipes;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.WidgetGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmiViewerUtils implements ViewerUtils<EmiRecipe> {

    public static EmiViewerUtils INSTANCE;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Map<EmiStack, List<EmiRecipe>> byInput;
    private static Map<EmiStack, List<EmiRecipe>> byOutput;
    private static Map<EmiRecipeCategory, List<EmiRecipe>> byCategory;
    private static Map<ResourceLocation, EmiRecipe> byId;


    public EmiViewerUtils() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Instance already set");
        }
        INSTANCE = this;
    }

    @Override
    public <T extends Recipe<?>> void inject(ChangedRecipeManager.ChangedRecipe<T> recipe) {
        try {
            initFields();
            ResourceLocation id = new ResourceLocation(CraftTweakerGUI.MOD_ID, recipe.getId());
            EmiRecipes.recipeIds.put(recipe.getRecipe(), id);
            EmiRecipe r = getViewerRecipe(recipe.getRecipeType(), recipe.getRecipe());
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
            EmiRecipe r = getViewerRecipe(recipe.getRecipeType(), recipe.getRecipe());
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

    @Override
    public boolean isCustomTagRecipe(EmiRecipe recipe) {
        return (recipe instanceof EmiTagRecipe r && ChangedRecipeManager.idAlreadyUsed(r.key.location().toString()));
    }

    @Override
    public Component getCategoryName(ResourceLocation id) {
        return getCategory(id).getName();
    }

    @Override
    public <R extends Recipe<?>, T extends SupportedRecipeType<R>> SupportedRecipe<R, T> toSupportedRecipe(EmiRecipe recipe) {
        return new EmiSupportedRecipe<>(recipe);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R2 extends Recipe<?>> EmiRecipe getViewerRecipe(SupportedRecipeType<R2> type, R2 recipe) throws UnsupportedViewerException {
        try {
            Object r = type.getEmiRecipe(recipe);
            if (r instanceof EmiRecipe emiRecipe) {
                return emiRecipe;
            } else {
                throw new IllegalStateException("getEmiRecipe must return an EmiRecipe");
            }
        } catch (UnsupportedViewerException e) {
            if (CraftTweakerGUI.getLoaderUtils().isModLoaded("jei")) {
                try {
                    return new JemiRecipe<>(getCategory(type.getId()), (IRecipeCategory<R2>) JeiViewerUtils.getCategory(type.getId()), recipe);
                } catch (UnsupportedViewerException ignored) {}
            }
            throw e;
        }
    }

    @Override
    public ViewerSlot newSlot(Ingredient ingredient, int x, int y) {
        return new EmiViewerSlot(ingredient, x, y);
    }

    @Override
    public ViewerSlot newSlot(ItemStack stack, int x, int y) {
        return new EmiViewerSlot(stack, x, y);
    }

    @Override
    public <S, T> ViewerSlot newSlotSpecial(SpecialAmountedIngredient<S, T> ingredient, int x, int y) {
        return new EmiViewerSlot(ingredient, x, y);
    }

    @Override
    public <S, T> void renderIngredientSpecial(SpecialAmountedIngredient<S, T> ingredient, GuiGraphics graphics, int x, int y, float partialTick) {
        of(ingredient).render(graphics, x, y, partialTick);
    }

    public static <S, T> EmiIngredient of(SpecialAmountedIngredient<S, T> ingredient) {
        if (ingredient.isTag()) {
            return EmiIngredient.of(ingredient.getTag());
        } else {
            S stack = ingredient.getStack();
            if (stack instanceof ItemStack itemStack) {
                return EmiStack.of(itemStack);
            } else {
                return (EmiIngredient) CraftTweakerGUI.getLoaderUtils().getEmiIngredient(stack);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return EmiScreenManager.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        return EmiScreenManager.search.charTyped(c, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        return EmiScreenManager.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        return EmiScreenManager.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return EmiScreenManager.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return EmiScreenManager.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void renderStart(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EmiDrawContext context = EmiDrawContext.wrap(graphics);
        context.push();
        EmiPort.setPositionTexShader();
        EmiScreenManager.render(context, mouseX, mouseY, partialTick);
        context.pop();
    }

    @Override
    public void renderEnd(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EmiScreenManager.drawForeground(EmiDrawContext.wrap(graphics), mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EmiDrawContext context = EmiDrawContext.wrap(graphics);
        EmiScreenManager.drawBackground(context, mouseX, mouseY, partialTick);
    }

    @Override
    public void init(Screen screen) {
        EmiScreenManager.addWidgets(screen);
    }

    public static EmiRecipeCategory getCategory(ResourceLocation id) {
        return EmiApi.getRecipeManager().getCategories().stream().filter(category -> category.getId().equals(id)).findFirst().orElseThrow();
    }
}
