package com.cloud.baowang.wallet.api.vo.fundrecord;

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
public class ReviewInfoVO {

    @Schema(description = "审核人")
    private String auditId;

    @Schema(description = "审核时间")
    private Long auditDatetime;

    @Schema(description = "审核备注")
    private String auditRemark;
    @Schema(description = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer auditStatus;
    @Schema(description = "审核状态")
    private String auditStatusText;
}
