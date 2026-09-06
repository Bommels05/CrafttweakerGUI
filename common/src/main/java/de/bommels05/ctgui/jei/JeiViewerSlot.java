package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.ViewerSlot;
import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static de.bommels05.ctgui.jei.CTGUIJeiPlugin.RUNTIME;

public class JeiViewerSlot implements ViewerSlot {
    private final IRecipeSlotDrawable slot;

    public JeiViewerSlot(Ingredient ingredient, int x, int y) {
        this.slot = createSlot(Arrays.asList(ingredient.getItems()), x, y);
    }

    public JeiViewerSlot(ItemStack stack, int x, int y) {
        this.slot = createSlot(List.of(stack), x, y);
    }

    public <S, T> JeiViewerSlot(SpecialAmountedIngredient<S, T> ingredient, int x, int y) {
        List<S> stacks = ingredient.getStacks();
        if (stacks.isEmpty()) {
            this.slot = createSlot(List.of(), x, y);
        } else {
            S first = stacks.getFirst();
            IIngredientType<S> type = RUNTIME.getIngredientManager().getIngredientTypeChecked(first).orElseThrow();
            this.slot = createSlot(type, stacks, x, y);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.pose().translate(1, 1, 0);
        this.slot.draw(graphics, mouseOver(mouseX, mouseY));
        graphics.pose().translate(-1, -1, 0);
    }

    @Override
    public void renderTooltip(Screen screen, GuiGraphics graphics, int mouseX, int mouseY) {
        if (mouseOver(mouseX, mouseY)) {
            this.slot.drawTooltip(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseOver(int mouseX, int mouseY) {
        return this.slot.isMouseOver(mouseX, mouseY);
    }

    @Override
    public ItemStack getStack() {
        return this.slot.getDisplayedItemStack().orElse(ItemStack.EMPTY);
    }

    private static IRecipeSlotDrawable createSlot(List<ItemStack> itemStacks, int x, int y) {
        return createSlot(VanillaTypes.ITEM_STACK, itemStacks, x, y);
    }

    private static <V> IRecipeSlotDrawable createSlot(IIngredientType<V> ingredientType, List<V> ingredients, int x, int y) {
        IIngredientManager ingredientManager = RUNTIME.getIngredientManager();
        IRecipeManager recipeManager = RUNTIME.getRecipeManager();

        List<Optional<ITypedIngredient<?>>> typedIngredients = new ArrayList<>();
        for (V ingredient : ingredients) {
            Optional<ITypedIngredient<V>> typedIngredient = ingredientManager.createTypedIngredient(ingredientType, ingredient, false);
            @SuppressWarnings("unchecked") Optional<ITypedIngredient<?>> cast = (Optional<ITypedIngredient<?>>) (Object) typedIngredient;
            typedIngredients.add(cast);
        }
        IRecipeSlotDrawable slot = recipeManager.createRecipeSlotDrawable(RecipeIngredientRole.RENDER_ONLY, typedIngredients, Set.of(), 0);
        slot.setPosition(x, y);
        return slot;
    }
}
