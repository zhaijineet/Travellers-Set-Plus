package net.zhaiji.travellerssetplus.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import twilightforest.TFRegistries;
import twilightforest.item.travellers_gear.TravellersArmorItem;
import twilightforest.item.travellers_gear.modifiers.InsertableTravellersModifier;
import twilightforest.item.travellers_gear.modifiers.TravellersModifier;

public class MixinUtil {
    /**
     * 将原本拥有升级槽位的旅行者装备的修饰符上限修改为无限制。
     */
    public static int getUnlimitedModifierSlots(int originalSlots) {
        return originalSlots > 0 ? Integer.MAX_VALUE : originalSlots;
    }

    /**
     * 统计该装备槽位下可安装的非能力型 InsertableTravellersModifier 数量，用于 Tooltip 显示剩余可升级数量。
     * 原槽位数 ≤ 0 或注册表缺失时返回 0。
     */
    public static int countInsertableModifiers(
        TravellersArmorItem armourItem,
        Operation<Integer> original,
        Item.TooltipContext tooltipContext
    ) {
        if (original.call(armourItem) <= 0) return 0;
        HolderLookup.Provider registries = tooltipContext.registries();
        if (registries == null) return 0;
        EquipmentSlot equipmentSlot = armourItem.getEquipmentSlot();
        return (int) registries
            .lookupOrThrow(TFRegistries.Keys.TRAVELLERS_MODIFIERS)
            .listElements()
            .filter(modifierHolder -> {
                TravellersModifier modifier = modifierHolder.value();
                return modifier instanceof InsertableTravellersModifier
                       && !modifier.isAbility()
                       && modifier.group().test(equipmentSlot);
            })
            .count();
    }
}
