package net.zhaiji.travellerssetplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.zhaiji.travellerssetplus.compat.curios.CuriosMixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twilightforest.client.overlay.ItemDisplayOverlay;

/**
 * 在 ItemDisplayOverlay.render 中包装 getItemBySlot 调用，
 * 优先从 Curios 饰品栏查找旅行者护目镜，使物品展示覆盖层在饰品栏也生效。
 */
@Mixin(ItemDisplayOverlay.class)
public abstract class ItemDisplayOverlayMixin {
    @WrapOperation(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private static ItemStack travellerssetplus$render(Player instance, EquipmentSlot slot1, Operation<ItemStack> original) {
        return CuriosMixinUtil.wrapGetItemBySlot(instance, slot1, original);
    }
}
