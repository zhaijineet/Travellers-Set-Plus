package net.zhaiji.travellerssetplus.mixin.curios;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.zhaiji.travellerssetplus.compat.curios.CuriosMixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twilightforest.events.TravellersGearEvents;

/**
 * 在 TravellersGearEvents 的事件处理方法中包装 getItemBySlot 调用，
 * 优先从 Curios 饰品栏查找旅行者套装，使饰品栏中的套装效果在事件处理中生效。
 */
@Mixin(TravellersGearEvents.class)
public abstract class TravellersGearEventsMixin {
    @WrapOperation(
        method = {
            "performPerfectDodge",
            "reduceSlimySolesFallDamage"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private ItemStack travellerssetplus$getItemBySlot(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
        return CuriosMixinUtil.wrapGetItemBySlot(entity, slot, original);
    }
}
