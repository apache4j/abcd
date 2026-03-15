package com.cloud.baowang.common.excel.handler;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.util.Arrays;

public class SimpleCellWriteHandler implements CellWriteHandler {


    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {
        //只处理头部
        if (isHead) {
            // 获取注解，下载的vo需要添加注解@ExcelProperty才进行替换
            ExcelProperty excelProperty = head.getField().getAnnotation(ExcelProperty.class);
            if (excelProperty != null) {
                String[] values = excelProperty.value();
                if (values != null && values.length != 0) {
                    String message = I18nMessageUtil.getI18NMessageInAdvice(values[0]);
                    head.setHeadNameList(Arrays.asList(message));
                }
            }

        }

    }

}
