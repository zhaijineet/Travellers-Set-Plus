package net.zhaiji.travellerssetplus.mixin.curios;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.zhaiji.travellerssetplus.compat.curios.CuriosMixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.init.custom.TravellersModifiersManager;

/**
 * 在 TravellersModifiersManager.getStackForGroup 中优先从 Curios 饰品栏查找旅行者套装，
 * 使通过 isModifierActive(LivingEntity, ResourceKey) 路径检测的修饰符效果能在饰品栏生效。
 */
@Mixin(TravellersModifiersManager.class)
public abstract class TravellersModifiersManagerMixin {
    @Inject(
        method = "getStackForGroup",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void travellerssetplus$checkCurios(
        LivingEntity livingEntity,
        EquipmentSlotGroup group,
        CallbackInfoReturnable<ItemStack> callbackInfo
    ) {
        CuriosMixinUtil.getStackForGroup(livingEntity, group).ifPresent(callbackInfo::setReturnValue);
    }
}
