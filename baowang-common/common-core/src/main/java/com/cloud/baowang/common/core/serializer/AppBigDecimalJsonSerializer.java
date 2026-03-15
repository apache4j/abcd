package com.cloud.baowang.common.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author Sheldon,小数保留两位
 */
public class AppBigDecimalJsonSerializer extends JsonSerializer {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    static {
        // 设置为直接截断，不做四舍五入
        df.setRoundingMode(RoundingMode.DOWN);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof BigDecimal amount) {
            // 截断到两位小数
            BigDecimal truncated = amount.setScale(2, RoundingMode.DOWN);
            gen.writeNumber(df.format(truncated));
        } else {
            gen.writeObject(value);
        }
    }
}
