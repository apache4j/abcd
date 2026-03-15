package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 审核信息
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@Schema(description = "审核信息")
@I18nClass
public class AgentWithdrawReviewInfoVO {

    @Schema(description = "审核人")
    private String auditUser;

    @Schema(description = "审核时间")
    private Long auditTime;

    @Schema(description = "审核信息")
    private String auditInfo;

    @Schema(description = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AUDIT_STATUS)
    private Integer auditStatus;

    private String auditStatusText;
}
