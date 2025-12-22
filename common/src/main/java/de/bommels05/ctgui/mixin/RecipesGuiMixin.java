package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.jei.JeiSupportedRecipe;
import de.bommels05.ctgui.screen.BetterIconButton;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipesGui.class)
public abstract class RecipesGuiMixin extends Screen {

    @Shadow(remap = false)
    @Final
    private IRecipeGuiLogic logic;
    @Shadow(remap = false)
    @Final
    private IconButton nextPage;

    @Unique
    private SpriteIconButton crafttweakerGUI$newRecipeButton;

    private RecipesGuiMixin() {
        super(null);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"), remap = false)
    protected void addButton(CallbackInfo ci) {
        if (Config.editMode) {
            crafttweakerGUI$newRecipeButton = new BetterIconButton(13, 13, CraftTweakerGUI.rl("jei:textures/jei/atlas/gui/icons/recipe_transfer.png"), 7, 7, button -> {
                Minecraft.getInstance().setScreen(new RecipeEditScreen<>(new JeiSupportedRecipe<>(logic.getSelectedRecipeCategory().getRecipeType().getUid()), null));
            });
            crafttweakerGUI$newRecipeButton.active = false;
        }
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    protected void initButton(CallbackInfo ci) {
        if (crafttweakerGUI$newRecipeButton != null) {
            crafttweakerGUI$newRecipeButton.setX(nextPage.getX() - 15);
            crafttweakerGUI$newRecipeButton.setY(nextPage.getY());
            this.addRenderableWidget(crafttweakerGUI$newRecipeButton);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lmezz/jei/gui/recipes/RecipeGuiLayouts;draw(Lnet/minecraft/client/gui/GuiGraphics;II)Ljava/util/Optional;", shift = At.Shift.AFTER))
    protected void renderButton(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (crafttweakerGUI$newRecipeButton != null) {
            crafttweakerGUI$newRecipeButton.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Inject(method = "updateLayout", at = @At(value = "RETURN", ordinal = 1), remap = false)
    protected void updateButtons(CallbackInfo ci) {
        if (crafttweakerGUI$newRecipeButton != null) {
            crafttweakerGUI$newRecipeButton.active = RecipeTypeManager.isTypeSupported(logic.getSelectedRecipeCategory().getRecipeType().getUid());
        }
    }
}
