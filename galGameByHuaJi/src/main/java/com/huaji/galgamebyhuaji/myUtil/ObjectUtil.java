package com.huaji.galgamebyhuaji.myUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
    
    /**
     * 合并两个对象，返回新实例
     *
     * @param deft1   对象1（默认值）
     * @param object2 对象2（优先级高）
     * @param <T>     对象类型
     *
     * @return 合并后的新对象
     */
    public static <T> T mergeObject(T deft1, T object2) {
        if (deft1 == null && object2 == null) return null;
        if (deft1 == null) return object2;
        if (object2 == null) return deft1;
        try {
            // 创建新实例（要求有无参构造）
            @SuppressWarnings("unchecked")
            T result = (T) deft1.getClass().getDeclaredConstructor().newInstance();
            // 获取所有字段（包括父类）
            List<Field> fields = getAllFields(deft1.getClass());
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                // 基本类型跳过（无法赋 null，通常实体类使用包装类型）
                if (fieldType.isPrimitive()) continue;
                // 取 object2 的字段值
                Object val2 = field.get(object2);
                boolean isEmpty2 = isEmptyValue(val2, fieldType);
                // 若 object2 非空，优先使用
                if (!isEmpty2) {
                    field.set(result, val2);
                    continue;
                }
                // 否则取 deft1 的值
                Object val1 = field.get(deft1);
                boolean isEmpty1 = isEmptyValue(val1, fieldType);
                if (!isEmpty1) {
                    field.set(result, val1);
                } else {
                    // 两者都为空，置 null
                    field.set(result, null);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("合并对象失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 判断值是否为空（null 或空字符串）
     */
    private static boolean isEmptyValue(Object value, Class<?> fieldType) {
        if (value == null) {
            return true;
        }
        // 如果是 String，使用自定义判空
        if (fieldType == String.class) {
            return MyStringUtil.isNull((String) value);
        }
        if (fieldType == List.class)
            return ListUtil.isNull((List) value);
        // 其他类型视为非空
        return false;
    }
    
    /**
     * 获取类的所有字段（包括父类，不包含 Object）
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields;
    }
}
