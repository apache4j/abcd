package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "风险控制层级返回对象")
@I18nClass
public class RiskLevelResVO extends BaseVO {
    @Schema(title = "风控类型")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.RISK_CONTROL_TYPE)
    private String riskControlType;
    @Schema(title = "风控类型名称")
    private String riskControlTypeText;
    @Schema(title = "风控类型状态")
    private String riskControlTypeState;
    @Schema(title = "风控层级")
    private String riskControlLevel;
    @Schema(title = "风控层级code")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long riskControlLevelCode;
    @Schema(title = "风控层级描述")
    private String riskControlLevelDescribe;
    @Schema(title = "删除数据标记 0.删除，1.正常")
    private Integer status;
    @Schema(title = "最近操作人")
    private String recentOperatorName;
    @Schema(title = "创建人")
    private String creatorName;
}
