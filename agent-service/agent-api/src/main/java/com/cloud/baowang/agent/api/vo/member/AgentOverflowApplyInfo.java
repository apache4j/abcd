package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(description = "会员溢出-本次申请信息")
@Data
@I18nClass
public class AgentOverflowApplyInfo implements Serializable {
    @Schema(description = "订单号")
    private String eventId;

    @Schema(description = "溢出会员账号")
    private String memberName;

    @Schema(description = "推广设备")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TYPE)
    private String device;

    @Schema(description = "推广设备")
    private String deviceText;

    @Schema(description = "推广链接")
    private String link;

    @Schema(description = "当前上级")
    private String currentAgentName;

    @Schema(description = "转入代理账号")
    private String transferAgentName;

    @Schema(description = "申请附图")
    private String image;

    @Schema(description = "图片数组")
    private List<String> imageArr;

    @Schema(description = "备注")
    private String applyRemark;

    @Schema(description = "审核员")
    private String auditName;

    @Schema(description = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer auditStatus;

    @Schema(description = "审核状态")
    private String auditStatusText;

    @Schema(description = "审核时间")
    private Long auditDatetime;

    @Schema(description = "审核备注")
    private String auditRemark;

}
