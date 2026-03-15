package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenVO extends BaseVO implements Serializable {
    private String id;
    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员user_id")
    private String userId;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "账号类型 1测试 2正式 3商务 4置换")
    private String accountType;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private Integer accountStatus;

    @Schema(description = "风控层级id")
    private Long riskLevelId;

    private String token;

    private Date loginTime;

    private Long expireTime;

}
