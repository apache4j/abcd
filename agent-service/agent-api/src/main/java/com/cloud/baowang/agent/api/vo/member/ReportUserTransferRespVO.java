package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@I18nClass
@Schema(title = "会员转代次数相应")
public class ReportUserTransferRespVO implements Serializable {

    @Schema(title = "转代会员ID")
    private String userId;
    @Schema(title = "转代会员ID")
    private String agentId;

    @Schema(title = "转代会员ID")
    private String siteCode;

    @Schema(title = "转代会员次数")
    private Integer userTransferCount;


}
