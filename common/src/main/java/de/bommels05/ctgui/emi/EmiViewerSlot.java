package de.bommels05.ctgui.emi;

import de.bommels05.ctgui.ViewerSlot;
import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class EmiViewerSlot implements ViewerSlot {

    private final SlotWidget slot;

    public EmiViewerSlot(Ingredient ingredient, int x, int y) {
        slot = new SlotWidget(EmiIngredient.of(ingredient), x, y);
    }

    public EmiViewerSlot(ItemStack stack, int x, int y) {
        slot = new SlotWidget(EmiStack.of(stack), x, y);
    }

    public <S, T> EmiViewerSlot(SpecialAmountedIngredient<S, T> ingredient, int x, int y) {
        slot = new SlotWidget(EmiViewerUtils.of(ingredient), x, y);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        slot.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderTooltip(Screen screen, GuiGraphics graphics, int mouseX, int mouseY) {
        if (mouseOver(mouseX, mouseY)) {
            EmiRenderHelper.drawTooltip(screen, EmiDrawContext.wrap(graphics), slot.getTooltip(mouseX, mouseY), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseOver(int mouseX, int mouseY) {
        return slot.getBounds().contains(mouseX, mouseY);
    }

    @Override
    public ItemStack getStack() {
        return slot.getStack().isEmpty() ? ItemStack.EMPTY : slot.getStack().getEmiStacks().get(0).getItemStack();
    }
}
