package com.ledger.config;

import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    // 定义日期时间格式
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd";
    
    // 创建线程安全的 SimpleDateFormat 实例
    private static final ThreadLocal<SimpleDateFormat> dateFormat = 
        ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_TIME_FORMAT));

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            // 配置 java.util.Date 和 java.sql.Timestamp 的序列化格式
            builder.simpleDateFormat(DATE_TIME_FORMAT);
            builder.serializers(
                new DateSerializer(false, dateFormat.get())
            );
            
            // 配置 Java 8 的 LocalDateTime 序列化格式（如果有使用）
            builder.serializers(
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
            );
            
            // 配置反序列化格式（如果需要）
            // builder.deserializers(new DateDeserializers.DateDeserializer(
            //     DateDeserializers.DateDeserializer.instance,
            //     dateFormat.get(),
            //     DATE_TIME_FORMAT
            // ));
        };
    }
}