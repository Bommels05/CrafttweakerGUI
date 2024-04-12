package de.bommels05.ctgui.emi;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.widget.RecipeButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class RecipeEditButtonWidget extends RecipeButtonWidget {
    private static ResourceLocation TEXTURE = new ResourceLocation(CraftTweakerGUI.MOD_ID, "textures/gui/edit_button.png");

    public RecipeEditButtonWidget(int x, int y, EmiRecipe recipe) {
        super(x, y, 0, 0, recipe);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        this.playButtonSound();
        Minecraft.getInstance().setScreen(new RecipeEditScreen<>(new EmiSupportedRecipe<>(recipe), recipe.getId()));
        return true;
    }

    @Override
    public void render(GuiGraphics raw, int mouseX, int mouseY, float delta) {
        EmiDrawContext context = EmiDrawContext.wrap(raw);
        context.resetColor();
        context.drawTexture(TEXTURE, x, y, 12, 12, u, v + getTextureOffset(mouseX, mouseY), 12, 12, 12, 24);
    }
}
