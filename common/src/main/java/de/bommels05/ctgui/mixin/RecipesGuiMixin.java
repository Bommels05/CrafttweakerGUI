package de.bommels05.ctgui.mixin;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.jei.JeiSupportedRecipe;
import de.bommels05.ctgui.jei.JeiViewerUtils;
import de.bommels05.ctgui.jei.RecipeEditButton;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import dev.emi.emi.recipe.EmiTagRecipe;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.gui.elements.GuiIconButtonSmall;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeTransferButton;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.library.gui.recipes.RecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(RecipesGui.class)
public abstract class RecipesGuiMixin extends Screen {

    @Shadow
    @Final
    private Textures textures;
    @Shadow
    @Final
    private IRecipeGuiLogic logic;
    @Shadow
    @Final
    private GuiIconButtonSmall nextPage;
    @Shadow
    @Final
    private List<RecipeTransferButton> recipeTransferButtons;
    @Shadow
    @Final
    private IRecipeTransferManager recipeTransferManager;
    @Shadow
    public abstract void init();
    @Unique
    private GuiIconButtonSmall newRecipeButton;
    @Unique
    private int index = 0;
    @Unique
    private int index2 = 0;

    private RecipesGuiMixin() {
        super(null);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    protected void addButton(CallbackInfo ci) {
        if (Config.editMode) {
            newRecipeButton = new GuiIconButtonSmall(0, 0, 13, 13, textures.getRecipeTransfer(), button -> {
                Minecraft.getInstance().setScreen(new RecipeEditScreen<>(new JeiSupportedRecipe<>(logic.getSelectedRecipeCategory().getRecipeType().getUid()), null));
            }, textures);
            newRecipeButton.active = false;
        }
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    protected void initButton(CallbackInfo ci) {
        newRecipeButton.setX(nextPage.getX() - 15);
        newRecipeButton.setY(nextPage.getY());
        this.addRenderableWidget(newRecipeButton);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lmezz/jei/gui/recipes/RecipesGui;drawLayouts(Lnet/minecraft/client/gui/GuiGraphics;II)Ljava/util/Optional;"))
    protected void renderButton(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        newRecipeButton.render(graphics, mouseX, mouseY, partialTick);
    }

    @Inject(method = "updateLayout", at = @At(value = "RETURN"), remap = false)
    protected void updateButton(CallbackInfo ci) {
        newRecipeButton.active = RecipeTypeManager.isTypeSupported(logic.getSelectedRecipeCategory().getRecipeType().getUid());
    }

    @Inject(method = "lambda$addRecipeTransferButtons$14", at = @At(value = "RETURN"), remap = false)
    protected <T> void addEditButtons(AbstractContainerMenu container, Player player, IRecipeLayoutDrawable<T> recipeLayout, CallbackInfo ci) {
        if (CraftTweakerGUI.shouldShowEditButton(recipeLayout.getRecipeCategory().getRecipeType().getUid(),
                recipeLayout.getRecipeCategory().getRegistryName(recipeLayout.getRecipe()), JeiViewerUtils.rightEither(recipeLayout))) {
            Rect2i buttonArea = recipeLayout.getRecipeTransferButtonArea();
            RecipeEditButton<?> button = new RecipeEditButton<>(recipeLayout, textures, this::onClose, index);
            button.update(buttonArea, recipeTransferManager, container, player);
            addRenderableWidget(button);
            this.recipeTransferButtons.add(button);
        }
        index++;
    }

    @Inject(method = "addRecipeTransferButtons", at = @At(value = "HEAD"), remap = false)
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
    }

}
