package net.zhaiji.travellerssetplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.Item;
import net.zhaiji.travellerssetplus.util.MixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.item.travellers_gear.TravellersArmorItem;

@Mixin(TravellersArmorItem.class)
public abstract class TravellersArmorItemMixin {
    /**
     * 将原本拥有升级槽位的旅行者装备的修饰符上限修改为无限制
     */
    @Inject(
        method = "getModifierSlots",
        at = @At("RETURN"),
        cancellable = true
    )
    public void travellerssetplus$unlimitedModifierSlots(CallbackInfoReturnable<Integer> callbackInfo) {
        int originalSlots = callbackInfo.getReturnValue();
        int modifiedSlots = MixinUtil.getUnlimitedModifierSlots(originalSlots);
        if (modifiedSlots != originalSlots) {
            callbackInfo.setReturnValue(modifiedSlots);
        }
    }

    /**
     * 将 Tooltip 中的槽位数替换为该装备实际可安装的修饰符总数，使空槽位显示剩余可升级的数量
     */
    @WrapOperation(
        method = "appendHoverText",
        at = @At(
            value = "INVOKE",
            target = "Ltwilightforest/item/travellers_gear/TravellersArmorItem;getModifierSlots()I"
        )
    )
    public int travellerssetplus$showAvailableSlots(
        TravellersArmorItem instance,
        Operation<Integer> original,
        @Local(argsOnly = true) Item.TooltipContext tooltipContext
    ) {
        return MixinUtil.countInsertableModifiers(instance, original, tooltipContext);
    }
}
