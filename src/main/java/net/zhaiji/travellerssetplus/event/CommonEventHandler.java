package net.zhaiji.travellerssetplus.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.zhaiji.travellerssetplus.TravellersSetPlus;
import net.zhaiji.travellerssetplus.TravellersSetPlusConfig;

public class CommonEventHandler {
    /**
     * 根据 config 动态注册 Curios 物品 tag 数据包，使旅行者套装可放入对应 Curios 槽位。
     * 修改 config 后需要重启游戏才能生效。
     */
    public static void handleAddPackFindersEvent(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) return;
        if (!TravellersSetPlusConfig.enableCuriosIntegration) return;

        event.addPackFinders(
            ResourceLocation.fromNamespaceAndPath(TravellersSetPlus.MODID, "travellers_set_plus_pack/curios_tags"),
            PackType.SERVER_DATA,
            Component.literal("Travellers Set Plus: Curios Tags"),
            PackSource.BUILT_IN,
            true,
            Pack.Position.TOP
        );
    }
}
