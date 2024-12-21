package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.jei.JeiSupportedRecipe;
import de.bommels05.ctgui.jei.JeiViewerUtils;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipesGui.class)
public abstract class RecipesGuiMixin extends Screen {

    /*@Shadow
    @Final
    private Textures textures;*/
    @Shadow(remap = false)
    @Final
    private IRecipeGuiLogic logic;
    @Shadow(remap = false)
    @Final
    private GuiIconButton nextPage;
    /*@Shadow
    @Final
    private List<RecipeTransferButton> recipeTransferButtons;
    @Shadow
    @Final
    private IRecipeTransferManager recipeTransferManager;*/
    @Shadow
    public abstract void init();
    @Shadow
    public abstract void onClose();

    @Shadow(remap = false)
    @Final
    private RecipeGuiLayouts layouts;
    @Unique
    private SpriteIconButton newRecipeButton;
    /*@Unique
    private int index = 0;
    @Unique
    private int index2 = 0;*/

    private RecipesGuiMixin() {
        super(null);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"), remap = false)
    protected void addButton(CallbackInfo ci) {
        if (Config.editMode) {
            newRecipeButton = SpriteIconButton.builder(Component.empty(), button -> {
                Minecraft.getInstance().setScreen(new RecipeEditScreen<>(new JeiSupportedRecipe<>(logic.getSelectedRecipeCategory().getRecipeType().getUid()), null));
            }, true).size(13, 13).sprite(ResourceLocation.parse("jei:icons/recipe_transfer"), 7, 7).build();
            newRecipeButton.active = false;
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

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lmezz/jei/gui/recipes/RecipeGuiLayouts;draw(Lnet/minecraft/client/gui/GuiGraphics;II)Ljava/util/Optional;"))
    protected void renderButton(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (newRecipeButton != null) {
            newRecipeButton.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Inject(method = "updateLayout", at = @At(value = "RETURN"), remap = false)
    protected void updateButton(CallbackInfo ci) {
        if (newRecipeButton != null) {
            newRecipeButton.active = RecipeTypeManager.isTypeSupported(logic.getSelectedRecipeCategory().getRecipeType().getUid());
        }
    }

    @Inject(method = "updateLayout", at = @At(value = "RETURN"), remap = false)
    protected void addEditButtons(CallbackInfo ci) {
        for (RecipeLayoutWithButtons<?> layoutWithButtons : ((RecipeGuiLayoutsAccessor) this.layouts).getRecipeLayoutsWithButtons()) {
            IRecipeLayoutDrawable<?> recipeLayout = layoutWithButtons.recipeLayout();
            if (CraftTweakerGUI.shouldShowEditButton(recipeLayout.getRecipeCategory().getRecipeType().getUid(),
                    ((IRecipeCategory<Object>) recipeLayout.getRecipeCategory()).getRegistryName(recipeLayout.getRecipe()), JeiViewerUtils.rightEither(recipeLayout))) {
                Rect2i area = recipeLayout.getRecipeTransferButtonArea();
                SpriteIconButton button = SpriteIconButton.builder(Component.empty(), b -> {
                    this.onClose();
                    Minecraft.getInstance().setScreen(new RecipeEditScreen<>(CraftTweakerGUI.getViewerUtils().toSupportedRecipe(JeiViewerUtils.rightEither(recipeLayout)), ((IRecipeCategory<Object>) recipeLayout.getRecipeCategory()).getRegistryName(recipeLayout.getRecipe())));
                }, true).size(area.getWidth(), area.getHeight()).sprite(ScreenUtils.EDIT_ICON_TEXTURE, 9, 9).build();
                button.setX(area.getX());
                button.setY(area.getY() - 15);
                addRenderableWidget(button);
            }
        }
        //index++;
    }

    /*@Inject(method = "addRecipeTransferButtons", at = @At(value = "HEAD"), remap = false)
    protected void resetIndex(CallbackInfo ci) {
        index = 0;
    }

    @ModifyArg(method = "lambda$tick$9", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    protected int ignoreEditButtons(int original) {
        if (index2 == 0) {
            index2++;
            if (this.recipeTransferButtons.get(original) instanceof RecipeEditButton<?> button) {
                return button.getIndex();
            }
            return this.recipeTransferButtons.stream().filter(b -> !(b instanceof RecipeEditButton<?>)).toList().indexOf(this.recipeTransferButtons.get(original));
        }
        index2 = 0;
        return original;
    }*/

}
