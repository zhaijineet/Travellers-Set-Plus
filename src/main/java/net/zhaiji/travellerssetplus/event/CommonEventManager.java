package net.zhaiji.travellerssetplus.event;

import net.neoforged.bus.api.IEventBus;
import net.zhaiji.travellerssetplus.TravellersSetPlusConfig;
import net.zhaiji.travellerssetplus.compat.CompatManager;
import net.zhaiji.travellerssetplus.compat.curios.CuriosCompat;

public class CommonEventManager {
    public static void init(IEventBus modBus, IEventBus gameBus) {
        modBusListener(modBus);
        gameBusListener(gameBus);
    }

    private static void modBusListener(IEventBus modBus) {
        modBus.addListener(TravellersSetPlusConfig::handlerModConfigEvent);
        modBus.addListener(CommonEventHandler::handleAddPackFindersEvent);

        if (CompatManager.CURIOS_LOADED) {
            modBus.addListener(CuriosCompat::registerCapabilities);
        }
    }

    private static void gameBusListener(IEventBus gameBus) {
        if (CompatManager.CURIOS_LOADED) {
            gameBus.addListener(CuriosCompat::onLivingDamagePre);
        }
    }
}
