package de.bommels05.ctgui.jei;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.common.gui.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.List;

public class JeiRecipeDecorator<T> implements IRecipeCategoryDecorator<T> {

    @Override
    public void draw(T recipe, IRecipeCategory<T> category, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        int mX = (int) mouseX;
        int mY = (int) mouseY;
        ResourceLocation id = category.getRegistryName(recipe);
        ChangedRecipeManager.ChangedRecipe<?> change = ChangedRecipeManager.getAffectingChange(id);
        if (change != null /*&& !(change.getRecipe() instanceof TagRecipe && change.wasExported())*/) {
            graphics.fill(0, 0, category.getHeight(), category.getHeight(), FastColor.ARGB32.color(157, 148, 60, 60));
            if (new Rect2i(0, 0, category.getWidth(), category.getHeight()).contains(mX, mY)) {
                TooltipRenderer.drawHoveringText(graphics, List.of(Component.translatable(change.getType() == ChangedRecipeManager.ChangedRecipe.Type.CHANGED ? "ctgui.recipe_changed" : "ctgui.recipe_removed")), mX, mY);
            }
        } else if (id != null) {
            int width = Minecraft.getInstance().font.width("+");
            int x = category.getWidth() + (1 + width) - 8;
            if (id.getNamespace().equals(CraftTweakerGUI.MOD_ID)) {
                graphics.drawString(Minecraft.getInstance().font, "+", x, -5, 65280, false);
                if (new Rect2i(x, -5, width, 6).contains(mX, mY)) {
                    TooltipRenderer.drawHoveringText(graphics, List.of(Component.translatable("ctgui.recipe_added")), x, -5);
                }
            } else if (Config.customRecipeIndicator && id.getNamespace().equals(CraftTweakerConstants.MOD_ID)) {
                graphics.drawString(Minecraft.getInstance().font, "+", x, -5, 16762624, false);
                if (new Rect2i(x, -5, width, 6).contains(mX, mY)) {
                    TooltipRenderer.drawHoveringText(graphics, List.of(Component.translatable("ctgui.recipe_custom")), x, -5);
                }
            }
        }
    }
}
