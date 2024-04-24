package de.bommels05.ctgui;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public interface ViewerUtils<R> {

    public <T extends Recipe<?>> void inject(ChangedRecipeManager.ChangedRecipe<T> recipe);

    public <T extends Recipe<?>> void unInject(ChangedRecipeManager.ChangedRecipe<T> recipe);

    public boolean isCustomTagRecipe(R recipe);

    public Component getCategoryName(ResourceLocation id);

    public <R2 extends Recipe<?>, T extends SupportedRecipeType<R2>> SupportedRecipe<R2, T> toSupportedRecipe(R recipe);

    public default <R2 extends Recipe<?>, T extends SupportedRecipeType<R2>> SupportedRecipe<R2, T> toSupportedRecipe(T type, R2 recipe) throws UnsupportedViewerException {
        return toSupportedRecipe(getViewerRecipe(type, recipe));
    };

    public <R2 extends Recipe<?>> R getViewerRecipe(SupportedRecipeType<R2> type, R2 recipe) throws UnsupportedViewerException;

    public ViewerSlot newSlot(Ingredient ingredient, int x, int y);

    public ViewerSlot newSlot(ItemStack stack, int x, int y);

    //Rendering
    public boolean keyPressed(int keyCode, int scanCode, int modifiers);

    public boolean charTyped(char c, int modifiers);

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY);

    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton);

    public boolean mouseScrolled(double mouseX, double mouseY, double amount);

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton);

    public void renderStart(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public void renderEnd(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public void init(Screen screen);

}
