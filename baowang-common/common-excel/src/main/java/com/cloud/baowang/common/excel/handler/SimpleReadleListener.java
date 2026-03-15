package com.cloud.baowang.common.excel.handler;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.cloud.baowang.common.excel.ExcelUtil;


import java.util.Map;

public class SimpleReadleListener<T> extends AnalysisEventListener<T> {

    private final Class<T> clazz;

    public SimpleReadleListener(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        ReadRowHolder readRowHolder = context.readRowHolder();
        int rowIndex = readRowHolder.getRowIndex();
        int currentHeadRowNumber = context.readSheetHolder().getHeadRowNumber();
        if (currentHeadRowNumber == rowIndex + 1) {
            //表头触发 表头国际化转换
            ExcelUtil.buildUpdateHeadAgain(context, headMap, clazz);
        }
    }

    @Override
    public void invoke(T fillData, AnalysisContext analysisContext) {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
