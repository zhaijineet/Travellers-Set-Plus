package net.zhaiji.travellerssetplus;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.zhaiji.travellerssetplus.event.CommonEventManager;

@Mod(TravellersSetPlus.MODID)
public class TravellersSetPlus {
    public static final String MODID = "travellers_set_plus";

    public TravellersSetPlus(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, TravellersSetPlusConfig.SPEC);

        CommonEventManager.init(modEventBus, NeoForge.EVENT_BUS);
    }
}
