package net.zhaiji.travellerssetplus.mixin.curios;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.zhaiji.travellerssetplus.compat.curios.CuriosMixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twilightforest.asmhooks.PlayerHooks;

/**
 * 在 PlayerHooks.getFoodExhaustion 中包装 getItemBySlot 调用，
 * 优先从 Curios 饰品栏查找旅行者背心，使饰品栏中的背心的 EFFICIENT_EATER 修饰符生效。
 */
@Mixin(PlayerHooks.class)
public abstract class PlayerHooksMixin {
    @WrapOperation(
        method = "getFoodExhaustion",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private static ItemStack travellerssetplus$wrapGetItemBySlot(Player player, EquipmentSlot slot1, Operation<ItemStack> original) {
        return CuriosMixinUtil.wrapGetItemBySlot(player, slot1, original);
    }
}
