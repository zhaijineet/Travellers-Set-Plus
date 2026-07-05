package net.zhaiji.travellerssetplus.mixin.curios;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.zhaiji.travellerssetplus.compat.curios.CuriosMixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.item.travellers_gear.TravellersGearLogic;

/**
 * 在 TravellersGearLogic 的各方法中包装 getItemBySlot 调用，
 * 优先从 Curios 饰品栏查找旅行者套装，使饰品栏中的旅行者套装能够应用各修饰符效果。
 */
@Mixin(TravellersGearLogic.class)
public abstract class TravellersGearLogicMixin {
    @Shadow
    private static double getAutoRepairChance(double baseProb, Level level, BlockPos pos) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @WrapOperation(
        method = {
            "travellersBootsStraightAhead",
            "travellersWingsSidestepCooldownSound",
            "travellersWingsGradualGlide",
            "travellersWingsHighJump",
            "travellersVestHaste",
            "tryPerformSidestep",
            "performDoubleJump"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private static ItemStack travellerssetplus$getItemBySlot(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
        return CuriosMixinUtil.wrapGetItemBySlot(entity, slot, original);
    }

    @Inject(
        method = "travellersGearAutoRepair",
        at = @At("RETURN")
    )
    private static void travellerssetplus$travellersGearAutoRepair(LivingEntity livingEntity, CallbackInfo callbackInfo) {
        CuriosMixinUtil.autoRepairCurios(
            livingEntity,
            // 不要替换成方法引用，因为mixin类在运行时不存在
            (baseProb, level, pos) -> getAutoRepairChance(baseProb, level, pos)
        );
    }
}
