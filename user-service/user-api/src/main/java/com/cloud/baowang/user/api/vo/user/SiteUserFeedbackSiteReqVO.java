package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "意见反馈站点req")
public class SiteUserFeedbackSiteReqVO extends PageVO implements Serializable {

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "vipLevel")
    private String vipGradeCode;

    @Schema(description = "锁单状态")
    private String lockStatus;

    @Schema(description = "问题类型")
    private String type;

    @Schema(description = "当前操作人")
    private String curOperator;
}
