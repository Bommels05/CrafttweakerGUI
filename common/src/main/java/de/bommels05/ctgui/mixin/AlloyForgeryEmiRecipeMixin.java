package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.compat.alloyforgery.AlloyForgeRecipeType;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.screen.WidgetGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wraith.alloyforgery.compat.emi.AlloyForgeryEmiRecipe;
import wraith.alloyforgery.recipe.AlloyForgeRecipe;

import java.util.ArrayList;
import java.util.Map;

@Mixin(AlloyForgeryEmiRecipe.class)
public class AlloyForgeryEmiRecipeMixin {
    @Shadow(remap = false)
    @Final
    private Map<AlloyForgeRecipe.OverrideRange, ItemStack> overrides;
    @Shadow(remap = false)
    @Final
    private int minForgeTier;
    @Unique
    private int ctgui$tierOverrideIndex;
    @Unique
    private boolean ctgui$ignoreClicks = false;

    @Inject(method = "addWidgets", at = @At(value = "RETURN"), remap = false)
    private void onAddWidgets(WidgetHolder widgets, CallbackInfo ci) {
        ctgui$tierOverrideIndex = 0;

        if (Minecraft.getInstance().screen instanceof RecipeEditScreen<?> screen && screen.getRecipeType() instanceof AlloyForgeRecipeType t) {
            if (widgets instanceof WidgetGroup group) {
                for (Widget widget : group.widgets) {
                    if (widget instanceof CustomButtonWidgetAccessor accessor) {
                        ctgui$ignoreClicks = true;
                        for (int i = 0; i < t.getCurrentPage(); i++) {
                            accessor.getAction().click(0, 0, 0);
                        }
                        ctgui$ignoreClicks = false;
                        return;
                    }
                }
            }
        }
    }

    @Inject(method = "lambda$addWidgets$2", at = @At(value = "HEAD"), remap = false)
    private void onViewOverride(CallbackInfo ci) {
        ctgui$tierOverrideIndex++;
        if (ctgui$tierOverrideIndex > overrides.size()) {
            ctgui$tierOverrideIndex = 0;
        }

        if (!ctgui$ignoreClicks && Minecraft.getInstance().screen instanceof RecipeEditScreen<?> screen && screen.getRecipeType() instanceof AlloyForgeRecipeType t) {
            t.setCurrentPage(ctgui$tierOverrideIndex, ctgui$tierOverrideIndex > 0 ? new ArrayList<>(overrides.keySet()).get(ctgui$tierOverrideIndex - 1).lowerBound() : minForgeTier);
        }
    }
}
