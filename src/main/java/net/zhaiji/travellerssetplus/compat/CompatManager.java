package net.zhaiji.travellerssetplus.compat;

import net.neoforged.fml.ModList;

/**
 * 管理可选模组兼容，防止未安装的模组类被触发加载。
 */
public class CompatManager {
    public static final boolean CURIOS_LOADED = ModList.get().isLoaded("curios");
}
