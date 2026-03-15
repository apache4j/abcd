package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Schema(title = "会员提款记录返回对象")
@Data
@I18nClass
public class UserWithdrawRecordPagesVO implements Serializable {
    @Schema(description = "总计")
    private UserWithdrawRecordVO total;
    @Schema(description = "小计")
    private UserWithdrawRecordVO small;
    @Schema(description = "分页数据")
    private Page<UserWithdrawRecordVO> pages;
}
