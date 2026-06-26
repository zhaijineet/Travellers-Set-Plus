package net.zhaiji.travellerssetplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.zhaiji.travellerssetplus.compat.curios.CuriosMixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twilightforest.client.event.TravellersClientEvents;

/**
 * 在 TravellersClientEvents 的各客户端事件处理方法中包装 getItemBySlot 调用，
 * 优先从 Curios 饰品栏查找旅行者套装，使饰品栏中的套装客户端效果完整生效。
 */
@Mixin(TravellersClientEvents.class)
public abstract class TravellersClientEventsMixin {
    @WrapOperation(
        method = {
            "handleAgileRanger",
            "handleStraightAhead",
            "updateZoomState",
            "swapHotbar"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private ItemStack travellerssetplus$wrapGetItemBySlotLocalPlayer(
        LocalPlayer entity,
        EquipmentSlot slot,
        Operation<ItemStack> original
    ) {
        return CuriosMixinUtil.wrapGetItemBySlot(entity, slot, original);
    }

    @WrapOperation(
        method = "slowZoomSensitivity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private ItemStack travellerssetplus$wrapGetItemBySlotPlayer(Player entity, EquipmentSlot slot, Operation<ItemStack> original) {
        return CuriosMixinUtil.wrapGetItemBySlot(entity, slot, original);
    }

    @WrapOperation(
        method = "renderGlovesInFirstPerson",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/AbstractClientPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private ItemStack travellerssetplus$wrapGetItemBySlotAbstractClientPlayer(
        AbstractClientPlayer entity,
        EquipmentSlot slot,
        Operation<ItemStack> original
    ) {
        return CuriosMixinUtil.wrapRenderableGetItemBySlot(entity, slot, original);
    }
}
