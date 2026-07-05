package net.zhaiji.travellerssetplus.mixin.curios;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.zhaiji.travellerssetplus.compat.curios.CuriosMixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twilightforest.asmhooks.MapHooks;

/**
 * 在 MapHooks.updateMapsInGoggles 中包装 getItemBySlot 调用，
 * 优先从 Curios 饰品栏查找旅行者护目镜，使饰品栏中的护目镜的 ITEM_DISPLAY 修饰符生效。
 */
@Mixin(MapHooks.class)
public abstract class MapHooksMixin {
    @WrapOperation(
        method = "updateMapsInGoggles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private static ItemStack travellerssetplus$updateMapsInGoggles(Player player, EquipmentSlot slot1, Operation<ItemStack> original) {
        return CuriosMixinUtil.wrapGetItemBySlot(player, slot1, original);
    }
}
