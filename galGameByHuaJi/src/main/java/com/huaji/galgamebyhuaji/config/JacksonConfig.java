package com.huaji.galgamebyhuaji.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {
    
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        // 关键：将 Date 的时区也固定为东八区，避免序列化时被转成 UTC 时间
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        
        // 2. 为 Java 8 时间类型准备格式化器
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        
        return builder -> {
            // 统一 Date 的格式
            builder.dateFormat(dateFormat);
            // 确保时间以字符串输出，而非时间戳
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
            // 注册 JavaTimeModule 并覆盖默认序列化器
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            
            // LocalDateTime：使用标准格式化器
            javaTimeModule.addSerializer(LocalDate.class,
                                         new LocalDateSerializer(dateTimeFormatter));
            javaTimeModule.addDeserializer(LocalDateTime.class,
                                           new LocalDateTimeDeserializer(dateTimeFormatter));
            
            // OffsetDateTime：关键点！
            // 使用 OffsetDateTimeSerializer.INSTANCE 作为基础，并传入不含偏移量的格式化器
            javaTimeModule.addSerializer(OffsetDateTime.class,
                                         new OffsetDateTimeSerializer(
                                                 OffsetDateTimeSerializer.INSTANCE, // 基础实例
                                                 false,                             // 不使用时间戳
                                                 dateTimeFormatter,                 // 自定义格式：yyyy-MM-dd HH:mm:ss
                                                 null                               // 默认 shape
                                         ));
            
            builder.modules(javaTimeModule);
        };
    }
}
