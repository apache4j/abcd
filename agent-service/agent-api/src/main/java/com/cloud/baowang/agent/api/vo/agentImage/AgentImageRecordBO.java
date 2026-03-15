package com.cloud.baowang.agent.api.vo.agentImage;

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
@I18nClass
public class AgentImageRecordBO extends BaseVO implements Serializable {

    @Schema(description = "图片标题")
    private String imageName;

    @Schema(description = "变更类型")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_IMAGE_CHANGE_TYPE)
    private Integer recordType;


    @Schema(description = "变更类型的名字")
    private String recordTypeText;


    @Schema(description = "变更前")
    private String beforeText;


    @Schema(description = "变更后")
    private String afterText;

    @Schema(description = "备注")
    private String remark;


    @Schema(description = "备注")
    private String createName;

    @Schema(description = "备注")
    private String updateName;

    @Schema(description = "备注")
    private String createdTimeStr;

    @Schema(description = "修改日期")
    private String updatedTimeStr;



}
