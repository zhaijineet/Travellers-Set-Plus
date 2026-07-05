package net.zhaiji.travellerssetplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.zhaiji.travellerssetplus.util.MixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.item.travellers_gear.TravellersArmorItem;

@Mixin(TravellersArmorItem.class)
public abstract class TravellersArmorItemMixin {
    @Unique
    private TravellersArmorItem travellerssetplus$self() {
        return (TravellersArmorItem) (Object) this;
    }

    @Inject(
        method = "getModifierSlots",
        at = @At("RETURN"),
        cancellable = true
    )
    public void travellerssetplus$getModifierSlots(CallbackInfoReturnable<Integer> callbackInfo) {
        int originalSlots = callbackInfo.getReturnValue();
        EquipmentSlot equipmentSlot = travellerssetplus$self().getEquipmentSlot();
        int configuredSlots = MixinUtil.getConfiguredModifierSlots(originalSlots, equipmentSlot);
        if (configuredSlots != originalSlots) {
            callbackInfo.setReturnValue(configuredSlots);
        }
    }

    @WrapOperation(
        method = "appendHoverText",
        at = @At(
            value = "INVOKE",
            target = "Ltwilightforest/item/travellers_gear/TravellersArmorItem;getModifierSlots()I"
        )
    )
    public int travellerssetplus$appendHoverText(
        TravellersArmorItem instance,
        Operation<Integer> original,
        @Local(argsOnly = true) Item.TooltipContext tooltipContext
    ) {
        return MixinUtil.getEffectiveTooltipSlots(instance, original, tooltipContext);
    }
}
