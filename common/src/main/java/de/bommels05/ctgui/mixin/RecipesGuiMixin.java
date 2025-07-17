package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.jei.JeiSupportedRecipe;
import de.bommels05.ctgui.jei.JeiViewerUtils;
import de.bommels05.ctgui.screen.BetterIconButton;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import de.bommels05.ctgui.screen.ScreenUtils;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.elements.GuiIconButton;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeGuiLayouts;
import mezz.jei.gui.recipes.RecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(RecipesGui.class)
public abstract class RecipesGuiMixin extends Screen {

    @Shadow(remap = false)
    @Final
    private IRecipeGuiLogic logic;
    @Shadow(remap = false)
    @Final
    private GuiIconButton nextPage;
    @Shadow
    public abstract void init();
    @Shadow
    public abstract void onClose();

    @Shadow(remap = false)
    @Final
    private RecipeGuiLayouts layouts;
    @Unique
    private SpriteIconButton newRecipeButton;
    @Unique
    private List<SpriteIconButton> editButtons;

    private RecipesGuiMixin() {
        super(null);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"), remap = false)
    protected void addButton(CallbackInfo ci) {
        if (Config.editMode) {
            newRecipeButton = new BetterIconButton(13, 13, CraftTweakerGUI.rl("jei:textures/jei/atlas/gui/icons/recipe_transfer.png"), 7, 7, button -> {
                Minecraft.getInstance().setScreen(new RecipeEditScreen<>(new JeiSupportedRecipe<>(logic.getSelectedRecipeCategory().getRecipeType().getUid()), null));
            });
            newRecipeButton.active = false;
            editButtons = new ArrayList<>();
        }
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    protected void initButton(CallbackInfo ci) {
        if (newRecipeButton != null) {
            newRecipeButton.setX(nextPage.getX() - 15);
            newRecipeButton.setY(nextPage.getY());
            this.addRenderableWidget(newRecipeButton);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lmezz/jei/gui/recipes/RecipeGuiLayouts;draw(Lnet/minecraft/client/gui/GuiGraphics;II)Ljava/util/Optional;", shift = At.Shift.AFTER))
    protected void renderButton(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (newRecipeButton != null) {
            newRecipeButton.render(graphics, mouseX, mouseY, partialTick);
            editButtons.forEach(b -> b.render(graphics, mouseX, mouseY, partialTick));
        }
    }

    @Inject(method = "updateLayout", at = @At(value = "RETURN", ordinal = 1), remap = false)
    protected void addEditButtons(CallbackInfo ci) {
        if (newRecipeButton != null) {
            newRecipeButton.active = RecipeTypeManager.isTypeSupported(logic.getSelectedRecipeCategory().getRecipeType().getUid());

            for (SpriteIconButton b : editButtons) {//Foreach breaks the mixin here for some reason...
                removeWidget(b);
            }
            editButtons.clear();
            for (RecipeLayoutWithButtons<?> layoutWithButtons : ((RecipeGuiLayoutsAccessor) this.layouts).getRecipeLayoutsWithButtons()) {
                IRecipeLayoutDrawable<?> recipeLayout = layoutWithButtons.recipeLayout();
                if (CraftTweakerGUI.shouldShowEditButton(recipeLayout.getRecipeCategory().getRecipeType().getUid(),
                        ((IRecipeCategory<Object>) recipeLayout.getRecipeCategory()).getRegistryName(recipeLayout.getRecipe()), JeiViewerUtils.rightEither(recipeLayout))) {
                    Rect2i area = recipeLayout.getRecipeTransferButtonArea();
                    SpriteIconButton button = new BetterIconButton(area.getWidth(), area.getHeight(), ScreenUtils.EDIT_ICON_TEXTURE, 9, 9, b -> {
                        this.onClose();
                        Minecraft.getInstance().setScreen(new RecipeEditScreen<>(CraftTweakerGUI.getViewerUtils().toSupportedRecipe(JeiViewerUtils.rightEither(recipeLayout)), ((IRecipeCategory<Object>) recipeLayout.getRecipeCategory()).getRegistryName(recipeLayout.getRecipe())));
                    });
                    button.setX(recipeLayout.getRect().getX() + area.getX());
                    button.setY(recipeLayout.getRect().getY() + area.getY() - 30);
                    addRenderableWidget(button);
                    editButtons.add(button);
                }
            }
        }
    }
}
