package net.zhaiji.travellerssetplus;

import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class TravellersSetPlusConfig {
    public static boolean enableCuriosIntegration;
    public static int gogglesModifierSlots;
    public static int vestModifierSlots;
    public static int wingsModifierSlots;
    public static int bootsModifierSlots;

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

    private static final ModConfigSpec.IntValue GOGGLES_MODIFIER_SLOTS = BUILDER
        .comment(
            "旅行者护目镜的修饰符槽位上限，-1 表示无限制",
            "Modifier slot limit for Travellers Goggles. -1 means unlimited"
        )
        .defineInRange("gogglesModifierSlots", -1, -1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue VEST_MODIFIER_SLOTS = BUILDER
        .comment(
            "旅行者胸甲的修饰符槽位上限，-1 表示无限制",
            "Modifier slot limit for Travellers Vest. -1 means unlimited"
        )
        .defineInRange("vestModifierSlots", -1, -1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue WINGS_MODIFIER_SLOTS = BUILDER
        .comment(
            "旅行者翼翅的修饰符槽位上限，-1 表示无限制",
            "Modifier slot limit for Travellers Wings. -1 means unlimited"
        )
        .defineInRange("wingsModifierSlots", -1, -1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue BOOTS_MODIFIER_SLOTS = BUILDER
        .comment(
            "旅行者靴子的修饰符槽位上限，-1 表示无限制",
            "Modifier slot limit for Travellers Boots. -1 means unlimited"
        )
        .defineInRange("bootsModifierSlots", -1, -1, Integer.MAX_VALUE);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int getModifierSlotsLimit(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> gogglesModifierSlots;
            case CHEST -> vestModifierSlots;
            case LEGS -> wingsModifierSlots;
            case FEET -> bootsModifierSlots;
            default -> 0;
        };
    }

    public static void handlerModConfigEvent(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            enableCuriosIntegration = ENABLE_CURIOS_INTEGRATION.get();
            gogglesModifierSlots = GOGGLES_MODIFIER_SLOTS.get();
            vestModifierSlots = VEST_MODIFIER_SLOTS.get();
            wingsModifierSlots = WINGS_MODIFIER_SLOTS.get();
            bootsModifierSlots = BOOTS_MODIFIER_SLOTS.get();
        }
    }
}
