package com.cloud.baowang.agent.api.vo.commission;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 审核信息
 */
@Data
@I18nClass
@Accessors(chain = true)
@Schema(title = "佣金审核-审核信息", description = "佣金审核-审核信息")
public class CommissionReviewInfoVO {

    @Schema(description = "一审人")
    private String oneReviewer;

    @Schema(description = "二审人")
    private String secondReviewer;


    @Schema(description = "一审状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_OPERATION)
    private Integer oneOrderStatus;

    @Schema(description = "一审状态名称")
    private String oneOrderStatusText;


    @Schema(description = "二审状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_OPERATION)
    private Integer secondOrderStatus;

    @Schema(description = "二审状态名称")
    private String secondOrderStatusText;


    @Schema(description = "一审时间")
    private Long oneReviewFinishTime = 0L;
    @Schema(description = "二审时间")
    private Long secondReviewFinishTime = 0L;

    @Schema(description = "一审备注")
    private String oneReviewRemark;

    @Schema(description = "二审备注")
    private String secondReviewRemark;

}
