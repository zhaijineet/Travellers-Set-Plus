package net.zhaiji.travellerssetplus;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class TravellersSetPlusConfig {
    public static boolean enableCuriosIntegration;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder()
        .comment("Config")
        .push("Config");

    private static final ModConfigSpec.BooleanValue ENABLE_CURIOS_INTEGRATION = BUILDER
        .comment(
            "是否启用 Curios 饰品系统集成（需要 Curios 已安装，修改后需重启生效）",
            "Enable Curios trinket system integration (requires Curios to be installed, changes require restart)"
        )
        .define(
            "enableCuriosIntegration",
            true
        );

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static void handlerModConfigEvent(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            enableCuriosIntegration = ENABLE_CURIOS_INTEGRATION.get();
        }
    }
}
