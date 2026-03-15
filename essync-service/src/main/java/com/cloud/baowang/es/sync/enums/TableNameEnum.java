package com.cloud.baowang.es.sync.enums;

import com.cloud.baowang.es.sync.constants.TableNameConstant;
import com.cloud.baowang.es.sync.model.OrderRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

/**
 * 同步表名
 */
@Getter
@AllArgsConstructor
public enum TableNameEnum {


    ORDER_RECORD(TableNameConstant.ORDER_RECORD, OrderRecord.class);

    public final String source;
    public final Class<?> target;

    public static Class<?> targetTable(String source) {
        if (Strings.isBlank(source)) {
            return null;
        }
        for (TableNameEnum value : TableNameEnum.values()) {
            if (value.getSource().equals(source)) {
                return value.target;
            }
        }
        return null;
    }

}
