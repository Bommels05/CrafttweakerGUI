package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.ViewerSlot;
import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.library.gui.ingredients.CycleTicker;
import mezz.jei.library.gui.ingredients.RecipeSlot;
import mezz.jei.library.gui.recipes.layout.builder.RecipeSlotBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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

    private JeiViewerSlot(List<ItemStack> stacks, int x, int y) {
        this.slot = (RecipeSlot) ((RecipeSlotBuilder) new RecipeSlotBuilder(RUNTIME.getIngredientManager(), 0, RecipeIngredientRole.RENDER_ONLY).setPosition(x, y).addItemStacks(stacks)).build(Set.of(), CycleTicker.createWithRandomOffset()).second();
    }

    @SuppressWarnings("unchecked")
    public <S, T> JeiViewerSlot(SpecialAmountedIngredient<S, T> ingredient, int x, int y) {
        List<S> stacks = ingredient.getStacks();
        this.slot = (RecipeSlot) ((RecipeSlotBuilder) new RecipeSlotBuilder(RUNTIME.getIngredientManager(), 0, RecipeIngredientRole.RENDER_ONLY).setPosition(x, y).addIngredients(
                (IIngredientType<S>) (stack.isEmpty() ? VanillaTypes.ITEM_STACK : RUNTIME.getIngredientManager().getIngredientTypeChecked(stacks.get(0)).orElseThrow()),
                stacks)).build(Set.of(), CycleTicker.createWithRandomOffset()).second();
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
            graphics.renderTooltip(Minecraft.getInstance().font, slot.getTooltip(), Optional.empty(), mouseX, mouseY);
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
