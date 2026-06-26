package net.zhaiji.travellerssetplus.mixin.curios;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.zhaiji.travellerssetplus.compat.curios.CuriosMixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twilightforest.item.travellers_gear.TravellersArmorBeltItem;

/**
 * 在 TravellersArmorBeltItem.travellersTrySwapHotbar 中包装 getArmor 调用，
 * 由于原方法直接使用 getInventory().getArmor(LEGS) 而非 getItemBySlot，不会被 getItemBySlot 的 WrapOperation 覆盖。
 */
@Mixin(TravellersArmorBeltItem.class)
public abstract class TravellersArmorBeltItemMixin {
    @WrapOperation(
        method = "travellersTrySwapHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;getArmor(I)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private static ItemStack travellerssetplus$wrapGetArmor(
        Inventory inventory, int slotIndex, Operation<ItemStack> original
    ) {
        return CuriosMixinUtil.wrapGetArmor(inventory, slotIndex, original);
    }
}
