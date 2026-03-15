package com.cloud.baowang.wallet.po.bank;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("bank_card_manager")
public class BankCardManagerPO extends BasePO {
    @Schema(description = "银行名称")
    private String bankName;
    @Schema(description = "银行代码")
    private String bankCode;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "状态 0启用 1禁用")
    private Integer status;
    @Schema(description = "操作时间")
    private Long operateTime;
    @Schema(description = "操作人")
    private String operator;
}
