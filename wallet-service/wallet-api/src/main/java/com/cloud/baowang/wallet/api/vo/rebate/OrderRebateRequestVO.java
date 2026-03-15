package com.cloud.baowang.wallet.api.vo.rebate;

import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 23/6/23 4:41 PM
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "用户订单返水请求对象")
public class OrderRebateRequestVO implements Serializable {

    /* 返水关联的注单订单号集合 */
    @Schema(title = "返水关联的注单订单号集合")
    private List<String> relationOrderId;

    /* 返水订单号 */
    @Schema(title = "返水订单号")
    private String orderId;

    /* 站点code */
    @Schema(title = "站点code")
    private String siteCode;

    /* 会员账号 */
    @Schema(title = "会员账号")
    private String userAccount;

    /* 会员id */
    @Schema(title = "会员id")
    private String userId;

    /* 会员账号 */
    @Schema(title = "代理账号")
    private String agentAccount;

    /* 会员姓名 */
    @Schema(title = "会员姓名")
    private String userName;

    /* 有效投注金额 */
    @Schema(title = "有效投注金额")
    private BigDecimal validBetAmount;

    /* 返水金额 */
    @Schema(title = "返水金额")
    private BigDecimal rebateAmount;

    @Schema(title = "返水时间")
    private Long rebateTime;

    @Schema(title = "用户信息")
    private WalletUserInfoVO userInfoVO;

    @Schema(title = "流水倍数")
    private BigDecimal rebateMultiple;

    @Schema(title = "统计开始时间")
    private long recordStartTime;

    @Schema(title = "统计结束时间")
    private long recordEndTime;

    @Schema(title = "过期时间")
    private long expireTime;

    @Schema(title = "红包标识 0:周红包,1:月红包,2:周体育")
    private Integer flag;

    @Schema(title = "客户端展示类型")
    private String customerCoinType;

    @Schema(title = "主币种")
    private String mainCurrency;

    @Schema(title = "vip段位code")
    private Integer vipRankCode;
}
