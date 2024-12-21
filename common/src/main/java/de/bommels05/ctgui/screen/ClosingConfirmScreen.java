package de.bommels05.ctgui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClosingConfirmScreen extends ConfirmScreen {
    private final Screen previous;

    public ClosingConfirmScreen(Screen previous, BooleanConsumer consumer, Component title, Component message) {
        super(consumer, title, message);
        this.previous = previous;
    }

    public ClosingConfirmScreen(Screen previous, BooleanConsumer booleanConsumer, Component title, Component message, Component yesButton, Component noButton) {
        super(booleanConsumer, title, message, yesButton, noButton);
        this.previous = previous;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(previous);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            //Don't let ESC trigger the consumer
            this.onClose();
            return true;
        } else {
            return super.keyPressed(i, j, k);
        }
    }
}
