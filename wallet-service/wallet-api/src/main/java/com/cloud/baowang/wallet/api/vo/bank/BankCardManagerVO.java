package com.cloud.baowang.wallet.api.vo.bank;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.enums.SystemParamTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "银行卡管理返回vo")
@I18nClass
public class BankCardManagerVO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "银行名称")
    @I18nField
    private String bankName;
    @Schema(description = "银行代码")
    private String bankCode;
    @Schema(description = "图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;
    @Schema(description = "图标完整url")
    private String iconFileUrl;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "排序")
    private Integer sort;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.FRONT_END_SHOW_STATUS)
    @Schema(description = "状态 0隐藏 1显示")
    private Integer status;
    @Schema(description = "状态文本 0隐藏 1显示")
    private String statusText;
    @Schema(description = "操作时间")
    private Long operateTime;
    @Schema(description = "操作人")
    private String operator;
}
