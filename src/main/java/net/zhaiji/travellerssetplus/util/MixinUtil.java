package net.zhaiji.travellerssetplus.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.zhaiji.travellerssetplus.TravellersSetPlusConfig;
import twilightforest.TFRegistries;
import twilightforest.item.travellers_gear.TravellersArmorItem;
import twilightforest.item.travellers_gear.modifiers.InsertableTravellersModifier;
import twilightforest.item.travellers_gear.modifiers.TravellersModifier;

public class MixinUtil {
    public static int getConfiguredModifierSlots(int originalSlots, EquipmentSlot equipmentSlot) {
        if (originalSlots <= 0) return originalSlots;
        int configured = TravellersSetPlusConfig.getModifierSlotsLimit(equipmentSlot);
        return configured < 0 ? Integer.MAX_VALUE : configured;
    }

    public static int getEffectiveTooltipSlots(
        TravellersArmorItem armorItem,
        Operation<Integer> original,
        Item.TooltipContext tooltipContext
    ) {
        int configuredSlots = original.call(armorItem);
        if (configuredSlots <= 0) return 0;
        HolderLookup.Provider registries = tooltipContext.registries();
        if (registries == null) return 0;
        EquipmentSlot equipmentSlot = armorItem.getEquipmentSlot();
        int totalInsertable = countTotalInsertableModifiers(registries, equipmentSlot);
        return Math.min(configuredSlots, totalInsertable);
    }

    private static int countTotalInsertableModifiers(HolderLookup.Provider registries, EquipmentSlot equipmentSlot) {
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
