package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(title = "会员提款记录返回对象")
@Data
@I18nClass
public class UserWithdrawRecordNotCollectInfoPagesVO implements Serializable {
    @Schema(description = "总计")
    private UserWithdrawRecordVO total;
    @Schema(description = "小计")
    private UserWithdrawRecordVO small;
    @Schema(description = "分页数据")
    private Page<UserWithdrawRecordNotCollectInfoVO> pages;
}
