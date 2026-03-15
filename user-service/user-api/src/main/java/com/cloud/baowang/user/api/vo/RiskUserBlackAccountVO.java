package com.cloud.baowang.user.api.vo;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RiskUserBlackAccountVO extends BaseVO {
    @Schema(title = "账号")
    private String riskControlAccount;
    @Schema(title = "会员账号")
    private String userAccount;
    @Schema(title = "风控类型code")
    private String riskControlTypeCode;
    @Schema(title = "注册站点")
    private String registerDomain;
    @Schema(title = "注册时间")
    private Long registerTime;
    @Schema(title = "最后登录时间")
    private Long lastLoginTime;
}
