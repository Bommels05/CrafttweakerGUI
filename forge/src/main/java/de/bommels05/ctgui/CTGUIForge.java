package de.bommels05.ctgui;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;

@Mod(CraftTweakerGUI.MOD_ID)
public class CTGUIForge {
    private static final Logger LOGGER = LogUtils.getLogger();

    public CTGUIForge() {
        if (FMLLoader.getDist().isClient()) {
            new ClientInit(FMLJavaModLoadingContext.get().getModEventBus());
        } else {
            LOGGER.info("CraftTweaker GUI detected on dedicated server, not loading");
        }
    }

}
