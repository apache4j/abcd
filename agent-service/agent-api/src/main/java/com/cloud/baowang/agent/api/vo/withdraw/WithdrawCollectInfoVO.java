package com.cloud.baowang.agent.api.vo.withdraw;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="提款信息搜集VO")
@I18nClass
public class WithdrawCollectInfoVO {

    @Schema(description = "code字段")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.WITHDRAW_COLLECT)
    private String filedCode;

    @Schema(description = "名称")
    private String filedCodeText;

    @Schema(description = "校验标志")
    private Boolean checkFlag;

}
