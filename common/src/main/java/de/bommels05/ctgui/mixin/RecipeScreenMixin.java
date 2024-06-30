package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.emi.EmiSupportedRecipe;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import dev.emi.emi.screen.RecipeScreen;
import dev.emi.emi.screen.RecipeTab;
import dev.emi.emi.screen.widget.SizedButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(RecipeScreen.class)
public class RecipeScreenMixin {

    @Shadow
    private List<SizedButtonWidget> arrows;
    @Shadow
    int backgroundHeight;
    @Shadow
    int x;
    @Shadow
    int y;
    @Shadow
    private List<RecipeTab> tabs;
    @Shadow
    private int tab;
    @Shadow
    private int minimumWidth;
    @Shadow
    private int buttonOff;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    protected void addNewButton(CallbackInfo ci) {
        if (Config.editMode) {
            arrows = new ArrayList<>(arrows);
            arrows.add(new SizedButtonWidget(x + backgroundHeight - 30, y + 18, 12, 12, 24, 0,
                    () -> RecipeTypeManager.isTypeSupported(tabs.get(tab).category.getId()),
                    button -> Minecraft.getInstance().setScreen(new RecipeEditScreen<>(new EmiSupportedRecipe<>(tabs.get(tab).category.getId()), null))));
        }
    }

    @Inject(method = "setRecipePageWidth", at = @At(value = "RETURN"), remap = false)
    protected void alignNewButton(CallbackInfo ci) {
        if (Config.editMode) {
            arrows.get(6).setX(x + minimumWidth - 31 + buttonOff);
            arrows.get(6).setY(y + 19);
        }
    }

}
