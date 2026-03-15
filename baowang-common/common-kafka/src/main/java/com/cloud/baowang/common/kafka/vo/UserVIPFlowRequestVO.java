package com.cloud.baowang.common.kafka.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 29/6/23 6:49 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "会员VIP流水请求对象")
public class UserVIPFlowRequestVO implements Serializable {

    @Schema(title = "会员账户")
    private String userAccount;

    @Schema(title = "会员id")
    private String userId;

    @Schema(title = "游戏大类")
    private Integer venueType;

//    @ApiModelProperty("充值金额")
//    private BigDecimal depositAmount = BigDecimal.ZERO;

    @Schema(title = "有效流水金额")
    private BigDecimal validAmount = BigDecimal.ZERO;

    /**
     * 账号类型 1测试 2正式
     */
    private Integer accountType;

    @Schema(title = "siteCode")
    private String siteCode;
}
