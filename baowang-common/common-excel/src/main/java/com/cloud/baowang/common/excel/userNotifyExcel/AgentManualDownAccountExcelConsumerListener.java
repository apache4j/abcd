package com.cloud.baowang.common.excel.userNotifyExcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.List;

/**

 */
public class AgentManualDownAccountExcelConsumerListener extends AnalysisEventListener<AgentManualDownAccountReadExcelDTO> {

    private final List<AgentManualDownAccountReadExcelDTO> rowList;


    public AgentManualDownAccountExcelConsumerListener(List<AgentManualDownAccountReadExcelDTO> rowList) {
        this.rowList = rowList;
    }

    @Override
    public void invoke(AgentManualDownAccountReadExcelDTO data, AnalysisContext context) {
        rowList.add(data);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 在这里可以对读取到的数据进行处理或输出
    }
}
