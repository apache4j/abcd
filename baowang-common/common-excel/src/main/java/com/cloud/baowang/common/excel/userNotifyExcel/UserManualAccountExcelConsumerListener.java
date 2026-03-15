package com.cloud.baowang.common.excel.userNotifyExcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.cloud.baowang.common.excel.freeGame.AddFreeGameReadExcelDTO;

import java.util.List;
import java.util.function.Consumer;

/**
 * 用户通知导入特定用户excel读取监听 只读用户编号 忽略第一行
 * 用户活动人工派发通知导入特定用户excel读取监听 只读用户编号 忽略第一行
 */
public class UserManualAccountExcelConsumerListener extends AnalysisEventListener<UserManualAccountReadExcelDTO> {

    private final List<UserManualAccountReadExcelDTO> rowList;


    public UserManualAccountExcelConsumerListener(List<UserManualAccountReadExcelDTO> rowList) {
        this.rowList = rowList;
    }

    @Override
    public void invoke(UserManualAccountReadExcelDTO data, AnalysisContext context) {
        rowList.add(data);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 在这里可以对读取到的数据进行处理或输出
    }
}
