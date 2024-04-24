package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.common.gui.TooltipRenderer;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.gui.recipes.IOnClickHandler;
import mezz.jei.gui.recipes.RecipeTransferButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RecipeEditButton<T> extends RecipeTransferButton {

    private final IOnClickHandler clickHandler;
    private final int index;

    public RecipeEditButton(IRecipeLayoutDrawable<T> recipeLayout, Textures textures, Runnable onClose, int index) {
        super(new EditIconDrawable(), recipeLayout, textures, onClose);
        this.index = index;
        this.clickHandler = (mouseX, mouseY) -> {
            onClose.run();
            Minecraft.getInstance().setScreen(new RecipeEditScreen<>(CraftTweakerGUI.getViewerUtils().toSupportedRecipe(recipeLayout), recipeLayout.getRecipeCategory().getRegistryName(recipeLayout.getRecipe())));
        };
    }

    @Override
    public void update(Rect2i area, IRecipeTransferManager recipeTransferManager, @Nullable AbstractContainerMenu container, Player player) {
        this.setX(area.getX());
        this.setY(area.getY() - 15);
        this.width = area.getWidth();
        this.height = area.getHeight();
    }

    @Override
    public void drawToolTip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            TooltipRenderer.drawHoveringText(graphics, List.of(Component.translatable("ctgui.list.edit")), mouseX, mouseY);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            clickHandler.onClick(mouseX, mouseY);
        }
    }

    public int getIndex() {
        return index;
    }
}
