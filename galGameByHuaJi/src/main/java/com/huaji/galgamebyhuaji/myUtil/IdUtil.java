package com.huaji.galgamebyhuaji.myUtil;

import java.util.UUID;

public class IdUtil {
    public static String getRandomId(String prefix) {
        if (MyStringUtil.isNull(prefix))
            prefix = UUID.randomUUID().toString().substring(0, 20);
        prefix = prefix.replaceAll("_+$", "");
        if (prefix.isEmpty())
            prefix = UUID.randomUUID().toString().substring(0, 20);
        String raw = String.format("%s_%s_%s", prefix, TimeUtil.getNowTime(), UUID.randomUUID());
        String cleaned = raw.replaceAll("_+", "_");
        return cleaned.length() <= 50 ? cleaned : cleaned.substring(0, 50);
    }
}
