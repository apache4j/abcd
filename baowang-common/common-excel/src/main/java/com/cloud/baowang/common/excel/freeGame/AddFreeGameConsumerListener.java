package com.cloud.baowang.common.excel.freeGame;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.List;

/**
 * @className: AddFreeGameConsumerListener
 * @author: wade
 * @description: 添加旋转次数解析
 * @date: 7/6/25 17:49
 */
public class AddFreeGameConsumerListener  extends AnalysisEventListener<AddFreeGameReadExcelDTO> {

    private final List<AddFreeGameReadExcelDTO> rowList;

    public AddFreeGameConsumerListener(List<AddFreeGameReadExcelDTO> rowList) {
        this.rowList = rowList;
    }

    @Override
    public void invoke(AddFreeGameReadExcelDTO data, AnalysisContext context) {
        rowList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 可选：添加日志、校验等
    }
}
