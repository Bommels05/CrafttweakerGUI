package de.bommels05.ctgui.mixin;

import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.screen.ChangeListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {

    private PauseScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void initButton(CallbackInfo ci) {
        if (Config.listButton) {
            Optional<Button> button = children().stream().filter(listener -> listener instanceof Button b && b.getMessage().getContents() instanceof TranslatableContents).map(b -> (Button) b).filter(b -> ((TranslatableContents) b.getMessage().getContents()).getKey().equals("gui.advancements")).findFirst();
            if (button.isPresent()) {
                Button b = button.get();
                addRenderableWidget(Button.builder(Component.literal("CT"), b2 -> Minecraft.getInstance().setScreen(new ChangeListScreen())).bounds(b.getX() - 24, b.getY(), 20, 20).build());
            }
        }
    }

}
