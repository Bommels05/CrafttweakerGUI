package de.bommels05.ctgui.emi;

import com.mojang.logging.LogUtils;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import de.bommels05.ctgui.SupportedRecipe;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.api.SupportedRecipeType;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.recipe.EmiShapedRecipe;
import dev.emi.emi.recipe.EmiTagRecipe;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.WidgetGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.slf4j.Logger;

public class EmiSupportedRecipe<R extends Recipe<?>, T extends SupportedRecipeType<R>> implements SupportedRecipe<R, T> {

    private final Logger LOGGER = LogUtils.getLogger();
    private EmiRecipe recipe;
    private R mcRecipe;
    private final T type;
    private WidgetGroup widgets;

    @SuppressWarnings("unchecked")
    public EmiSupportedRecipe(EmiRecipe recipe) {
        this.recipe = recipe;
        RecipeHolder<R> holder = ((RecipeHolder<R>) recipe.getBackingRecipe());
        type = (T) RecipeTypeManager.getType(recipe.getCategory().getId());
        mcRecipe = type.getAlternativeEmiRecipeGetter() != null ? type.getAlternativeEmiRecipeGetter().apply(recipe) : (holder != null ? holder.value() : null);
    }

    @SuppressWarnings("unchecked")
    public EmiSupportedRecipe(ResourceLocation category) {
        type = (T) RecipeTypeManager.getType(category);
    }

    @Override
    public int getWidth() {
        return recipe.getDisplayWidth();
    }

    @Override
    public int getHeight() {
        return recipe.getDisplayHeight();
    }

    @Override
    public Component getCategoryName() {
        return recipe.getCategory().getName();
    }

    @Override
    public void render(int x, int y, GuiGraphics graphics, int mouseX, int mouseY, Screen screen) {
        graphics.pose().translate(x, y, 0);
        if (widgets == null) {
            widgets = new WidgetGroup(recipe, x, y, getWidth(), Math.min(getHeight(), 135));
            recipe.addWidgets(widgets);
        }
        try {
            renderWidgets(x, y, graphics, mouseX, mouseY, screen, widgets);
        } catch (Throwable t) {
            widgets.error(t);
            renderWidgets(x, y, graphics, mouseX, mouseY, screen, widgets);
            LOGGER.error("Error rendering recipe while editing", t);
        }
    }

    @Override
    public void mouseClicked(int x, int y, int mouseX, int mouseY, int button) {
        if (widgets != null) {
            try {
                widgetsClicked(x, y, mouseX, mouseY, button, widgets);
            } catch (Throwable t) {
                LOGGER.error("Error handling recipe mouse click while editing", t);
            }
        }
    }

    private static void renderWidgets(int x, int y, GuiGraphics graphics, int mouseX, int mouseY, Screen screen, WidgetGroup widgets) {
        int mY = mouseY - y;
        int mX = mouseX - x;
        for (Widget widget : widgets.widgets) {
            widget.render(graphics, mX, mY, Minecraft.getInstance().getFrameTime());
        }
        graphics.pose().translate(-x, -y, 0);
        for (Widget widget : widgets.widgets) {
            if (widget.getBounds().contains(mX, mY)) {
                EmiRenderHelper.drawTooltip(screen, EmiDrawContext.wrap(graphics), widget.getTooltip(mX, mY), mouseX, mouseY);
            }
        }
    }

    private static void widgetsClicked(int x, int y, int mouseX, int mouseY, int button, WidgetGroup widgets) {
        int mY = mouseY - y;
        int mX = mouseX - x;
        for (Widget widget : widgets.widgets) {
            if (!(widget instanceof SlotWidget) && widget.getBounds().contains(mX, mY)) {
                widget.mouseClicked(mX, mY, button);
            }
        }
    }

    @Override
    public T getType() {
        return type;
    }

    @Override
    public R getRecipe() {
        return mcRecipe;
    }

    @Override
    public void setRecipe(R r) throws UnsupportedViewerException {
        this.mcRecipe = r;
        this.recipe = EmiViewerUtils.INSTANCE.getViewerRecipe(type, r);
        this.widgets = null;
    }
}
