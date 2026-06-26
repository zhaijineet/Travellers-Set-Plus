package net.zhaiji.travellerssetplus.compat.curios;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * 自动修复概率计算回调，委托给 TravellersGearLogic.getAutoRepairChance 原方法
 */
@FunctionalInterface
public interface AutoRepairChanceFunction {
    /**
     * 计算考虑日光和暮色森林维度增益后的自动修复概率
     *
     * @param baseProb 基础修复概率
     * @param level    当前世界
     * @param pos      实体所在坐标
     * @return 增益后的实际修复概率
     */
    double apply(double baseProb, Level level, BlockPos pos);
}
