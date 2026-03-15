package com.cloud.baowang.common.excel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HeaderInfo {
    String name;

    String alias;

    Integer columnWidth;

    /**
     * ex.
     * writer.addHeaderAlias("orderNo", "订单号").setColumnWidth(columnIndex, 30);
     *
     * @param name
     * @param alias
     * @param columnWidht
     */
    public HeaderInfo(String name, String alias, Integer columnWidht) {
        this.name = name;
        this.alias = alias;
        this.columnWidth = columnWidht;
    }
}
