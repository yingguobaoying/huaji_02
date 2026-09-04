package com.huaji.galgamebyhuaji.vignaAiFrame.constant;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class AiConstant {
    /**
     * ai总结的客户端编码
     */
    public static final String AI_SUM = "AI_SUM";
    public static final List<String> CODE_LIST;
    
    static {
        try {
            Field[] fields = Class.forName("com.huaji.galgamebyhuaji.vignaAiFrame.constant.AiConstant").getDeclaredFields();
            CODE_LIST = new ArrayList<>(fields.length - 1);
            for (Field field : fields) {
                int modifiers = field.getModifiers(); // 获取字段的修饰符
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) &&
                    String.class.equals(field.getType())) { // 检查是否为static final
                    CODE_LIST.add(String.valueOf(field.get(null)));
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
