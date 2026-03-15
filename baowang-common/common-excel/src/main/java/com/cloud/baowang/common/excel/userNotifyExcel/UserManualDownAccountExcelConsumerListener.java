package com.cloud.baowang.common.excel.userNotifyExcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.List;

/**
 * 用户通知导入特定用户excel读取监听 只读用户编号 忽略第一行
 * 用户活动人工派发通知导入特定用户excel读取监听 只读用户编号 忽略第一行
 */
public class UserManualDownAccountExcelConsumerListener extends AnalysisEventListener<UserManualDownAccountReadExcelDTO> {

    private final List<UserManualDownAccountReadExcelDTO> rowList;


    public UserManualDownAccountExcelConsumerListener(List<UserManualDownAccountReadExcelDTO> rowList) {
        this.rowList = rowList;
    }

    @Override
    public void invoke(UserManualDownAccountReadExcelDTO data, AnalysisContext context) {
        rowList.add(data);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 在这里可以对读取到的数据进行处理或输出
    }
}
