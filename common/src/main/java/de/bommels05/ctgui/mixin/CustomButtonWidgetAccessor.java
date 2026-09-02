package de.bommels05.ctgui.mixin;

import dev.emi.emi.api.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import wraith.alloyforgery.compat.emi.CustomButtonWidget;

@Mixin(CustomButtonWidget.class)
public interface CustomButtonWidgetAccessor {

    @Accessor(value = "action", remap = false)
    public ButtonWidget.ClickAction getAction();

}
