package com.huaji.galgamebyhuaji.constant;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class AiConstant {
    /**
     * ai总结的客户端编码
     */
    public static final String SUM_UP_CODE = "ai-sum-up";
    public static final List<String> CODE_LIST;
    
    static {
        try {
            Field[] fields = Class.forName("com.huaji.galgamebyhuaji.constant.AiConstant").getDeclaredFields();
            CODE_LIST = new ArrayList<>(fields.length - 1);
            for (Field field : fields) {
                int modifiers = field.getModifiers(); // 获取字段的修饰符
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)&& String.class.equals(field.getType())) { // 检查是否为static final
                    System.out.println("常量名: " + field.getName() + ", 常量值: " + field.get(null)); // 获取并打印常量的值
                    CODE_LIST.add(String.valueOf(field.get(null)));
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
