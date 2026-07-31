package com.huaji.galgamebyhuaji.myUtil;

/**
 * Vault 路径工具类
 * 根据 KV 引擎版本自动构建正确的路径
 * KV v1: {backend}/{path}
 * KV v2: {backend}/data/{path}
 */
public class VaultPathUtil {
    private VaultPathUtil() {}

    /**
     * 构建 Vault KV 引擎的路径，根据 KV 版本自动添加 data/ 前缀
     *
     * @param kvVersion KV 版本 (1 或 2)
     * @param pathParts 路径组成部分 (第一个通常是 backend)
     * @return 正确的 Vault 路径
     */
    public static String buildPath(int kvVersion, String... pathParts) {
        String path = FileUtil.formatUrl("", pathParts);
        if (kvVersion == 2) {
            // KV v2 需要在 backend 后插入 data/
            int firstSlash = path.indexOf('/');
            if (firstSlash > 0) {
                path = path.substring(0, firstSlash) + "/data/" + path.substring(firstSlash + 1);
            }
        }
        return path;
    }
}