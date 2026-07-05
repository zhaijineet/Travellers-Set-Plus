package net.zhaiji.travellerssetplus.mixin.curios;

import net.zhaiji.travellerssetplus.compat.curios.CuriosMixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import twilightforest.item.travellers_gear.TravellersGogglesItem;

/**
 * 修复 Curios 调用 inventoryTick 时传入 slotId=-1 导致护目镜地图自动更新逻辑被跳过的问题。
 * <p>
 * Curios 在 tick 饰品时以 slotId=-1 调用 inventoryTick，而 TravellersGogglesItem 原方法检查
 * slotId != Inventory.INVENTORY_SIZE + EquipmentSlot.HEAD.getIndex() 时直接 return。
 * 此 mixin 在 slotId=-1 且 Curios 集成启用时将其替换为标准头部槽位值，使地图同步逻辑正常执行。
 */
@Mixin(TravellersGogglesItem.class)
public abstract class TravellersGogglesItemMixin {
    @ModifyVariable(
        method = "inventoryTick",
        at = @At("HEAD"),
        argsOnly = true,
        index = 4
    )
    private int travellerssetplus$inventoryTick(int slotId) {
        return CuriosMixinUtil.fixGogglesSlotId(slotId);
    }
}
