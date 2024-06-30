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

    @Shadow
    private List<Object> rightButtons;
    @Shadow
    @Final
    public EmiRecipe recipe ;
    @Unique
    private int buttonIndex = 0;

    @Inject(method = "<init>(Ldev/emi/emi/api/recipe/EmiRecipe;)V", at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/recipe/EmiRecipe;supportsRecipeTree()Z"))
    protected void addButtons(EmiRecipe recipe, CallbackInfo ci) {
        if (CraftTweakerGUI.shouldShowEditButton(recipe.getCategory().getId(), recipe.getId(), recipe)) {
            try {
                rightButtons.add(Class.forName("dev.emi.emi.screen.RecipeDisplay$ButtonType").getEnumConstants()[2]);
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
