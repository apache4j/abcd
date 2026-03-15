package com.cloud.baowang.common.excel.handler;


import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.property.ExcelWriteHeadProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;


public class SimpleSheetWriteHandler implements SheetWriteHandler {
    @Autowired
    private MessageSource messageSource;

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        String sheetName = writeSheetHolder.getSheetName();
        //sheetname 目前不支持国际化
        //String message = I18nMessageUtil.getI18NMessage(sheetName);
        //String message = messageSource.getMessage(sheetName, null, LocaleContextHolder.getLocale());
        writeSheetHolder.setSheetName(sheetName);
    }
}
