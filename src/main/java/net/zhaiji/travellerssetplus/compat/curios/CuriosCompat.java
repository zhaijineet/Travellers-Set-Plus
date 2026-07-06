package net.zhaiji.travellerssetplus.compat.curios;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import twilightforest.init.TFDataAttachments;
import twilightforest.init.TFDataComponents;
import twilightforest.init.TFItems;
import twilightforest.item.travellers_gear.TravellersArmorItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 为旅行者套装注册 Curios ICurio 物品能力、提供饰品栏查找工具，并处理耐久损耗。
 */
public class CuriosCompat {
    public static final String SLOT_HEAD = "head";
    public static final String SLOT_BODY = "body";
    public static final String SLOT_HANDS = "hands";
    public static final String SLOT_BACK = "back";
    public static final String SLOT_BELT = "belt";
    public static final String SLOT_FEET = "feet";

    private static final String[] DAMAGEABLE_SLOTS = {
        SLOT_HEAD,
        SLOT_BODY,
        SLOT_BACK,
        SLOT_FEET
    };

    /**
     * 为 6 件旅行者套装注册 Curios 物品能力，使其可放入对应专属槽位。
     */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
            CuriosCapability.ITEM, (stack, context) -> new ICurio() {
                @Override
                public ItemStack getStack() {
                    return stack;
                }

                @Override
                public boolean canEquip(SlotContext slotContext) {
                    return matchesSlot(stack, slotContext.identifier());
                }

                @Override
                public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
                    SlotContext slotContext, ResourceLocation id
                ) {
                    return CuriosCompat.getAttributeModifiersFromStack(stack, slotContext);
                }

                @Override
                public SoundInfo getEquipSound(SlotContext slotContext) {
                    return new SoundInfo(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F, 1.0F);
                }

                @Override
                public boolean canEquipFromUse(SlotContext slotContext) {
                    return canEquip(slotContext);
                }
            },
            TFItems.TRAVELLERS_GOGGLES,
            TFItems.TRAVELLERS_VEST,
            TFItems.TRAVELLERS_GLOVES,
            TFItems.TRAVELLERS_WINGS,
            TFItems.TRAVELLERS_BELT,
            TFItems.TRAVELLERS_BOOTS
        );
    }

    /**
     * 从物品的 {@link DataComponents#ATTRIBUTE_MODIFIERS} 中提取应在 Curios 饰品栏生效的属性修饰符。
     */
    private static Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiersFromStack(ItemStack stack, SlotContext slotContext) {
        Multimap<Holder<Attribute>, AttributeModifier> result = LinkedHashMultimap.create();
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) return result;
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            Holder<Attribute> attribute = entry.attribute();
            if (attribute == Attributes.ARMOR || attribute == Attributes.ARMOR_TOUGHNESS || attribute == Attributes.KNOCKBACK_RESISTANCE) {
                continue;
            }
            result.put(attribute, entry.modifier());
        }
        return result;
    }

    /**
     * 根据 EquipmentSlot 从 Curios 饰品栏查找对应部位的旅行者套装。
     * <p>
     * CHEST 部位会依次检查 body 和 hands 槽位，LEGS 部位会依次检查 back 和 belt 槽位。
     */
    public static Optional<ItemStack> getTravellersGearFromCurios(LivingEntity entity, EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> findFromSlot(entity, SLOT_HEAD);
            case CHEST -> findFromSlot(entity, SLOT_BODY).or(() -> findFromSlot(entity, SLOT_HANDS));
            case LEGS -> findFromSlot(entity, SLOT_BACK).or(() -> findFromSlot(entity, SLOT_BELT));
            case FEET -> findFromSlot(entity, SLOT_FEET);
            default -> Optional.empty();
        };
    }

    /**
     * 根据 EquipmentSlotGroup 从 Curios 饰品栏查找对应部位的旅行者套装。
     */
    public static Optional<ItemStack> getTravellersGearFromCurios(LivingEntity entity, EquipmentSlotGroup group) {
        if (group == EquipmentSlotGroup.HEAD) {
            return getTravellersGearFromCurios(entity, EquipmentSlot.HEAD);
        } else if (group == EquipmentSlotGroup.CHEST) {
            return getTravellersGearFromCurios(entity, EquipmentSlot.CHEST);
        } else if (group == EquipmentSlotGroup.LEGS) {
            return getTravellersGearFromCurios(entity, EquipmentSlot.LEGS);
        } else if (group == EquipmentSlotGroup.FEET) {
            return getTravellersGearFromCurios(entity, EquipmentSlot.FEET);
        }
        return Optional.empty();
    }

    /**
     * 获取 Curios 饰品栏中所有有耐久的旅行者套装物品（用于耐久损耗和自动修复）。
     */
    public static List<ItemStack> getAllDamageableTravellersGearFromCurios(LivingEntity entity) {
        return collectTravellersGear(entity, DAMAGEABLE_SLOTS, true);
    }

    /**
     * 从指定槽位标识符列表中收集 Curios 饰品栏中的旅行者套装物品。
     *
     * @param damageableOnly 为 true 时只收集有耐久值的物品
     */
    private static List<ItemStack> collectTravellersGear(LivingEntity entity, String[] slots, boolean damageableOnly) {
        List<ItemStack> result = new ArrayList<>();
        Optional<ICuriosItemHandler> handlerOpt = CuriosApi.getCuriosInventory(entity);
        if (handlerOpt.isEmpty()) return result;
        ICuriosItemHandler handler = handlerOpt.get();
        for (String identifier : slots) {
            for (SlotResult slotResult : handler.findCurios(identifier)) {
                ItemStack stack = slotResult.stack();
                if (stack.has(TFDataComponents.IS_TRAVELLERS_GEAR) && (!damageableOnly || stack.isDamageableItem())) {
                    result.add(stack);
                }
            }
        }
        return result;
    }

    /**
     * 根据 Curios 槽位标识符精确查找旅行者套装物品。
     */
    public static Optional<ItemStack> findFromSlot(LivingEntity entity, String identifier) {
        return CuriosApi.getCuriosInventory(entity)
            .flatMap(handler -> handler.findFirstCurio(stack ->
                stack.has(TFDataComponents.IS_TRAVELLERS_GEAR) && matchesSlot(stack, identifier)
            ))
            .map(SlotResult::stack);
    }

    /**
     * 从两个 Curios 槽位查找旅行者套装，若同时存在则将次槽位的标志组件注入主槽位物品的拷贝中，
     * 使渲染器在同一个模型上同时绘制两件装备的模型部件。
     * 若主槽位物品已包含该标志组件（已合成合并），直接提前返回，跳过次槽位查找。
     */
    private static Optional<ItemStack> mergeRenderableGear(
        LivingEntity entity, String primarySlot, String secondarySlot,
        DataComponentType<Unit> mergeComponent
    ) {
        Optional<ICuriosItemHandler> handler = CuriosApi.getCuriosInventory(entity);
        if (handler.isEmpty()) return Optional.empty();

        Optional<ItemStack> primaryStack = findRenderableFromSlot(handler.get(), primarySlot);
        if (primaryStack.isPresent() && primaryStack.get().has(mergeComponent)) return primaryStack;
        Optional<ItemStack> secondaryStack = findRenderableFromSlot(handler.get(), secondarySlot);
        if (primaryStack.isPresent() && secondaryStack.isPresent()) {
            ItemStack merged = primaryStack.get().copy();
            merged.set(mergeComponent, Unit.INSTANCE);
            return Optional.of(merged);
        }
        return primaryStack.isPresent() ? primaryStack : secondaryStack;
    }

    /**
     * 根据 Curios 槽位标识符精确查找旅行者套装物品，且仅返回 render 开关开启的槽位。
     * <p>
     * 用于装备渲染相关场景，尊重玩家在 Curios GUI 中对每个槽位的渲染开关设置。
     */
    public static Optional<ItemStack> findRenderableFromSlot(LivingEntity entity, String identifier) {
        return CuriosApi.getCuriosInventory(entity).flatMap(handler -> findRenderableFromSlot(handler, identifier));
    }

    /**
     * 从已获取的 Curios 饰品栏中精确查找指定槽位中 render 开关开启的旅行者套装物品。
     */
    private static Optional<ItemStack> findRenderableFromSlot(ICuriosItemHandler handler, String identifier) {
        ICurioStacksHandler stacksHandler = handler.getCurios().get(identifier);
        if (stacksHandler == null) return Optional.empty();
        NonNullList<Boolean> renders = stacksHandler.getRenders();
        for (SlotResult slotResult : handler.findCurios(identifier)) {
            ItemStack stack = slotResult.stack();
            if (!stack.has(TFDataComponents.IS_TRAVELLERS_GEAR) || !matchesSlot(stack, identifier)) {
                continue;
            }
            int index = slotResult.slotContext().index();
            if (index < renders.size() && renders.get(index)) {
                return Optional.of(stack);
            }
        }
        return Optional.empty();
    }

    /**
     * 根据 EquipmentSlot 从 Curios 饰品栏查找对应部位的旅行者套装，且仅返回 render 开关开启的槽位。
     * <p>
     * CHEST 部位会依次检查 body 和 hands 槽位，LEGS 部位会依次检查 back 和 belt 槽位。
     */
    public static Optional<ItemStack> getRenderableTravellersGearFromCurios(LivingEntity entity, EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> findRenderableFromSlot(entity, SLOT_HEAD);
            case CHEST -> mergeRenderableGear(entity, SLOT_BODY, SLOT_HANDS, TFDataComponents.TRAVELLERS_HAS_GLOVES.get());
            case LEGS -> mergeRenderableGear(entity, SLOT_BACK, SLOT_BELT, TFDataComponents.TRAVELLERS_HAS_BELT.get());
            case FEET -> findRenderableFromSlot(entity, SLOT_FEET);
            default -> Optional.empty();
        };
    }

    /**
     * 判断物品是否适合放入指定的旅行者套装 Curios 槽位。
     */
    public static boolean matchesSlot(ItemStack stack, String identifier) {
        return switch (identifier) {
            case SLOT_HEAD -> stack.is(TFItems.TRAVELLERS_GOGGLES.get());
            case SLOT_BODY -> stack.is(TFItems.TRAVELLERS_VEST.get());
            case SLOT_HANDS -> stack.is(TFItems.TRAVELLERS_GLOVES.get());
            case SLOT_BACK -> stack.is(TFItems.TRAVELLERS_WINGS.get());
            case SLOT_BELT -> stack.is(TFItems.TRAVELLERS_BELT.get());
            case SLOT_FEET -> stack.is(TFItems.TRAVELLERS_BOOTS.get());
            default -> false;
        };
    }

    /**
     * 在玩家受到伤害后对 Curios 饰品栏中的旅行者套装执行耐久损耗。
     * <p>
     * 饰品栏中的套装只提供修饰符效果、不提供护甲值，未参与伤害吸收，
     * 因此基于减伤后的最终伤害（getNewDamage）而非吸收前的伤害来计算损耗基数，
     * 破损保护与临界音效流程仍与暮色森林原版对齐。
     */
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        float damageAmount = event.getNewDamage();
        if (damageAmount <= 0) return;

        List<ItemStack> curiosGear = getAllDamageableTravellersGearFromCurios(entity);
        if (curiosGear.isEmpty()) return;

        int baseAmount = (int) Math.max(1.0F, damageAmount / 4.0F);

        ServerPlayer player = entity instanceof ServerPlayer serverPlayer ? serverPlayer : null;

        boolean anyDamaged = false;
        for (ItemStack stack : curiosGear) {
            if (!stack.canBeHurtBy(event.getSource())) continue;
            if (hurtTravellersGear(stack, baseAmount, serverLevel, player)) {
                anyDamaged = true;
            }
        }
        if (anyDamaged) {
            entity.setData(TFDataAttachments.LAST_DAMAGE_ARMOR_TIME, entity.level().getGameTime());
        }
    }

    /**
     * 对单件旅行者套装执行耐久损耗，先基于毛伤害判断破损保护与临界音效，
     * 再委托给 vanilla 的 hurtAndBreak 处理 Unbreaking 减免与耐久变化进度触发。
     */
    private static boolean hurtTravellersGear(ItemStack stack, int amount, ServerLevel serverLevel, ServerPlayer player) {
        if (TravellersArmorItem.isTravellersArmorAndBroken(stack)) return false;

        int currentDamage = stack.getDamageValue();
        int maxDamage = stack.getMaxDamage();

        int effectiveAmount = amount;
        if (currentDamage + amount >= maxDamage) {
            effectiveAmount = Math.max(0, maxDamage - currentDamage - 1);
        } else if (currentDamage + amount >= maxDamage - 1 && player != null) {
            player.playNotifySound(SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, player.getVoicePitch());
        }

        if (effectiveAmount <= 0) return false;

        stack.hurtAndBreak(
            effectiveAmount, serverLevel, player, item -> {
            }
        );
        return true;
    }
}
