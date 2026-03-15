package com.cloud.baowang.agent.api.vo.domian;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "域名的变更记录BO")
@I18nClass
public class AgentDomainRecordResponseVO extends BaseVO implements Serializable {

    @Schema(description = "域名Id")
    private String domainName;

    @Schema(description = "变更类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.RECORD_DOMAIN)
    private Integer recordType;

    @Schema(description = "变更类型的名字")
    private String recordTypeText;

    @Schema(description = "变更前")
    private String beforeText;

    @Schema(description = "变更后")
    private String afterText;

    @Schema(description = "备注")
    private String remark;

}
