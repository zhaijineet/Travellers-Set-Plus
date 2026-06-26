package net.zhaiji.travellerssetplus.compat.curios;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.zhaiji.travellerssetplus.TravellersSetPlusConfig;
import net.zhaiji.travellerssetplus.compat.CompatManager;
import twilightforest.init.TFDataAttachments;
import twilightforest.init.TFDataComponents;
import twilightforest.init.custom.TravellersModifiersManager;

import java.util.Optional;

/**
 * 聚合所有 Curios 相关的 Mixin 委托逻辑，每个公开方法内部会先检查 Curios 集成是否启用。
 */
public class CuriosMixinUtil {
    /**
     * 包装 getItemBySlot：Curios 集成启用时优先从饰品栏查找旅行者套装，未命中则回退到原方法。
     */
    public static ItemStack wrapGetItemBySlot(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
        if (CompatManager.CURIOS_LOADED && TravellersSetPlusConfig.enableCuriosIntegration) {
            ItemStack curiosStack = CuriosCompat.getTravellersGearFromCurios(entity, slot).orElse(ItemStack.EMPTY);
            if (!curiosStack.isEmpty()) return curiosStack;
        }
        return original.call(entity, slot);
    }

    /**
     * 包装 getItemBySlot（渲染专用）：与 {@link #wrapGetItemBySlot} 相同，但额外检查 Curios 的 render 开关，
     * 尊重玩家在 Curios GUI 中对每个槽位的渲染状态设置。
     */
    public static ItemStack wrapRenderableGetItemBySlot(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
        if (CompatManager.CURIOS_LOADED && TravellersSetPlusConfig.enableCuriosIntegration) {
            ItemStack curiosStack = CuriosCompat.getRenderableTravellersGearFromCurios(entity, slot).orElse(ItemStack.EMPTY);
            if (!curiosStack.isEmpty()) return curiosStack;
        }
        return original.call(entity, slot);
    }

    /**
     * 包装 Inventory.getArmor：Curios 集成启用且查询的是 LEGS 槽位时，
     * 优先从 Curios 翅膀槽位返回旅行者翅膀，其次查找腰带槽位，未命中则回退到原方法。
     * <p>
     * 翅膀优先于腰带，因为腰带可以在合成中并入翅膀，使翅膀同时拥有快捷栏存储能力。
     */
    public static ItemStack wrapGetArmor(Inventory inventory, int slotIndex, Operation<ItemStack> original) {
        if (CompatManager.CURIOS_LOADED && TravellersSetPlusConfig.enableCuriosIntegration && slotIndex == EquipmentSlot.LEGS.getIndex()) {
            Optional<ItemStack> curiosStack = CuriosCompat.findFromSlot(inventory.player, CuriosCompat.SLOT_BACK)
                .or(() -> CuriosCompat.findFromSlot(inventory.player, CuriosCompat.SLOT_BELT));
            if (curiosStack.isPresent()) return curiosStack.get();
        }
        return original.call(inventory, slotIndex);
    }

    /**
     * 在暮色森林原版自动修复之后，对 Curios 饰品栏中的旅行者套装执行相同的自动修复逻辑。
     *
     * @param autoRepairChanceFunction 自动修复概率计算回调，由 mixin 通过 @Shadow 委托给原版 private static 方法
     */
    public static void autoRepairCurios(LivingEntity livingEntity, AutoRepairChanceFunction autoRepairChanceFunction) {
        if (!CompatManager.CURIOS_LOADED || !TravellersSetPlusConfig.enableCuriosIntegration) return;
        if (livingEntity.level().isClientSide()) return;

        long lastHitTime = livingEntity.getData(TFDataAttachments.LAST_DAMAGE_ARMOR_TIME);
        if (livingEntity.level().getGameTime() - lastHitTime <= 10 * 20) return;

        Level level = livingEntity.level();
        for (ItemStack stack : CuriosCompat.getAllDamageableTravellersGearFromCurios(livingEntity)) {
            Float probability = stack.get(TFDataComponents.AUTO_REPAIR_PROBABILITY);
            if (probability == null) continue;
            if (!TravellersModifiersManager.isModifierActive(livingEntity, stack, TravellersModifiersManager.AUTO_REPAIR_MODIFIER)) continue;
            double boostedProbability = autoRepairChanceFunction.apply(probability, level, livingEntity.blockPosition());
            if (boostedProbability > level.random.nextFloat()) {
                stack.setDamageValue(Math.max(stack.getDamageValue() - 1, 0));
            }
        }
    }

    /**
     * 修复 Curios 调用 inventoryTick 时传入 slotId=-1 导致护目镜地图自动更新逻辑被跳过的问题。
     * 在 slotId=-1 且 Curios 集成启用时将其替换为标准头部槽位值。
     */
    public static int fixGogglesSlotId(int slotId) {
        if (slotId == -1 && CompatManager.CURIOS_LOADED && TravellersSetPlusConfig.enableCuriosIntegration) {
            return Inventory.INVENTORY_SIZE + EquipmentSlot.HEAD.getIndex();
        }
        return slotId;
    }

    /**
     * 从 Curios 饰品栏查找对应装备槽位组的旅行者套装，未启用集成时返回 empty。
     */
    public static Optional<ItemStack> getStackForGroup(LivingEntity livingEntity, EquipmentSlotGroup group) {
        if (!CompatManager.CURIOS_LOADED || !TravellersSetPlusConfig.enableCuriosIntegration) return Optional.empty();
        return CuriosCompat.getTravellersGearFromCurios(livingEntity, group);
    }
}
