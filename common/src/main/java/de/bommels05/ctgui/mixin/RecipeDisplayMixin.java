package de.bommels05.ctgui.mixin;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.emi.RecipeEditButtonWidget;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.recipe.EmiTagRecipe;
import dev.emi.emi.screen.RecipeDisplay;
import dev.emi.emi.widget.RecipeDefaultButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeDisplay.class)
public class RecipeDisplayMixin {

    @Shadow(remap = false)
    private List<Object> rightButtons;
    @Shadow(remap = false)
    private List<Object> leftButtons;
    @Shadow(remap = false) @Final
    public EmiRecipe recipe;
    @Shadow(remap = false)
    private int rows;
    @Shadow(remap = false)
    private int leftWidth;
    @Shadow(remap = false)
    private int rightWidth;
    @Shadow(remap = false) @Final
    private int height;
    @Unique
    private int buttonIndex = 0;

    @Inject(method = "<init>(Ldev/emi/emi/api/recipe/EmiRecipe;)V", at = @At(value = "RETURN"), remap = false)
    protected void addButtons(EmiRecipe recipe, CallbackInfo ci) {
        if (CraftTweakerGUI.shouldShowEditButton(recipe.getCategory().getId(), recipe.getId(), recipe)) {
            try {
                rightButtons.add(Class.forName("dev.emi.emi.screen.RecipeDisplay$ButtonType").getEnumConstants()[2]);
                this.rows = Math.max(1, (this.height + 8 + 2) / 14);
                this.leftWidth = Math.max(0, (this.leftButtons.size() + this.rows - 1) / this.rows * 14 - 1);
                this.rightWidth = Math.max(0, (this.rightButtons.size() + this.rows - 1) / this.rows * 14 - 1);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @ModifyArg(method = "addButtons", at = @At(value = "INVOKE", target = "Ldev/emi/emi/screen/WidgetGroup;add(Ldev/emi/emi/api/widget/Widget;)Ldev/emi/emi/api/widget/Widget;"), remap = false)
    protected Widget constructButtons(Widget widget) {
        if (widget instanceof RecipeDefaultButtonWidget) {
            //The second Recipe Default Button is actually our edit button or if there is none the first one
            if (buttonIndex == 1 || !recipe.supportsRecipeTree()) {
                return new RecipeEditButtonWidget(widget.getBounds().x(), widget.getBounds().y(), recipe);
            }
            buttonIndex++;
        }
        return widget;
    }

    @Inject(method = "addButtons", at = @At(value = "HEAD"), remap = false)
    protected void countButtons(CallbackInfo ci) {
        buttonIndex = 0;
    }

}
