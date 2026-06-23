package net.zhaiji.travellerssetplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.TFRegistries;
import twilightforest.item.travellers_gear.TravellersArmorItem;
import twilightforest.item.travellers_gear.modifiers.InsertableTravellersModifier;
import twilightforest.item.travellers_gear.modifiers.TravellersModifier;

@Mixin(TravellersArmorItem.class)
public abstract class TravellersArmorItemMixin {
    /**
     * 将原本拥有升级槽位的旅行者装备的修饰符上限修改为无限制
     */
    @Inject(
        method = "getModifierSlots",
        at = @At("RETURN"),
        cancellable = true
    )
    public void travellerssetplus$unlimitedModifierSlots(CallbackInfoReturnable<Integer> callbackInfo) {
        if (callbackInfo.getReturnValue() > 0) {
            callbackInfo.setReturnValue(Integer.MAX_VALUE);
        }
    }

    /**
     * 将 Tooltip 中的槽位数替换为该装备实际可安装的修饰符总数，使空槽位显示剩余可升级的数量
     */
    @WrapOperation(
        method = "appendHoverText",
        at = @At(
            value = "INVOKE",
            target = "Ltwilightforest/item/travellers_gear/TravellersArmorItem;getModifierSlots()I"
        )
    )
    public int travellerssetplus$showAvailableSlots(
        TravellersArmorItem instance,
        Operation<Integer> original,
        @Local(argsOnly = true) Item.TooltipContext tooltipContext
    ) {
        if (original.call(instance) <= 0) return 0;
        HolderLookup.Provider registries = tooltipContext.registries();
        if (registries == null) return 0;
        EquipmentSlot equipmentSlot = instance.getEquipmentSlot();
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
