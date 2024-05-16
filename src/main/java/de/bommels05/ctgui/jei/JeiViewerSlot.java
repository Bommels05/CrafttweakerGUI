package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.ViewerSlot;
import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.common.gui.TooltipRenderer;
import mezz.jei.library.gui.ingredients.RecipeSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static de.bommels05.ctgui.jei.CTGUIJeiPlugin.RUNTIME;

public class JeiViewerSlot implements ViewerSlot {

    private final RecipeSlot slot;
    private ItemStack stack;
    private Ingredient ingredient;

    public JeiViewerSlot(Ingredient ingredient, int x, int y) {
        this(Arrays.asList(ingredient.getItems()), x, y);
        this.ingredient = ingredient;
    }

    public JeiViewerSlot(ItemStack stack, int x, int y) {
        this(List.of(stack), x, y);
        this.stack = stack;
    }

    private JeiViewerSlot(List<ItemStack> stack, int x, int y) {
        this.slot = new RecipeSlot(RUNTIME.getIngredientManager(), RecipeIngredientRole.RENDER_ONLY, x, y, 0);
        this.slot.set(stack.stream().map(this::of).toList(), Set.of(), RUNTIME.getIngredientVisibility());
    }

    @SuppressWarnings("unchecked")
    public <S, T> JeiViewerSlot(SpecialAmountedIngredient<S, T> ingredient, int x, int y) {
        this.slot = new RecipeSlot(RUNTIME.getIngredientManager(), RecipeIngredientRole.RENDER_ONLY, x, y, 0);
        List<S> stacks = ingredient.getStacks();
        this.slot.set((List<Optional<ITypedIngredient<?>>>) (List<?>) stacks.stream().map(s -> RUNTIME.getIngredientManager().createTypedIngredient(s)).toList(), Set.of(), RUNTIME.getIngredientVisibility());
    }

    @SuppressWarnings("unchecked")
    private Optional<ITypedIngredient<?>> of(ItemStack stack) {
        return (Optional<ITypedIngredient<?>>) ((Optional<?>) RUNTIME.getIngredientManager().createTypedIngredient(VanillaTypes.ITEM_STACK, stack));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.pose().translate(1, 1, 0);
        slot.draw(graphics);
        if (mouseOver(mouseX, mouseY)) {
            slot.drawHoverOverlays(graphics);
        }
        graphics.pose().translate(-1, -1, 0);
    }

    @Override
    public void renderTooltip(Screen screen, GuiGraphics graphics, int mouseX, int mouseY) {
        if (mouseOver(mouseX, mouseY)) {
            TooltipRenderer.drawHoveringText(graphics, slot.getTooltip(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseOver(int mouseX, int mouseY) {
        return slot.getRect().contains(mouseX, mouseY);
    }

    @Override
    public ItemStack getStack() {
        return stack == null ? (ingredient.getItems().length > 0 ? ingredient.getItems()[0] : ItemStack.EMPTY) : stack;
    }
}
