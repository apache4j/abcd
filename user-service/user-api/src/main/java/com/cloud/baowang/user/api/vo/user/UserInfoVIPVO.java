package com.cloud.baowang.user.api.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @Author 小智
 * @Date 11/5/23 6:28 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户基础vip信息")
public class UserInfoVIPVO implements Serializable {

    @Schema(description ="用户id")
    private String userId;

    @Schema(description ="用户账号")
    private String userAccount;

    @Schema(description ="VIP等级")
    private Integer vipGradeCode;

    @Schema(description ="VIP等级名称")
    private String vipGradeName;

    @Schema(description ="VIP段位")
    private Integer vipRankCode;

    @Schema(description ="VIP段位名称")
    private String vipRankName;



}
