package com.cloud.baowang.common.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class SensitiveDataJsonSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // 脱敏处理：用星号替代
            String maskedValue = maskSensitiveData(value);
            gen.writeString(maskedValue);
        } else {
            gen.writeNull();
        }
    }

    private String maskSensitiveData(String value) {
        if (value == null || value.length() <= 2) {
            return value; // 如果字符串为空或长度不大于2，返回原字符串
        }
        return value.substring(0, 2) + // 追加前两位
                "*".repeat(value.length() - 2);
    }
}