package de.bommels05.ctgui;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CraftTweakerGUI.MOD_ID)
public class CTGUINeo {
    private static final Logger LOGGER = LogUtils.getLogger();

    public CTGUINeo(IEventBus modBus, Dist dist) {
        if (dist.isClient()) {
            new ClientInit(modBus);
        } else {
            LOGGER.info("CraftTweaker GUI detected on dedicated server, not loading");
        }
    }

}
