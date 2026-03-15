package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author : kimi
 * @Date : 26/6/23 11:23 AM
 * @Version : 1.0
 */
@Data
@Schema(description ="客户端获取VIP等级详情返回福利")
public class VIPGradeBenefitsDetailAPP {
    /**
     * vip 卡片
     */
    @Schema(description = "vip卡片显示以及奖励")
    private List<VIPGradeBenefitsAPP> vipBenefitAPPs;

    /**
     * 用户VIP提款信息
     */
    @Schema(description = "vip详情-用户VIP提款信息")
    private UserVipWithdrawConfigCopyAPPVO userVipWithdrawalAPP;

    /**
     * 返水
     */
    @Schema(description = "vip详情-返水")
    private SiteRebateConfigWebCopyVO  siteRebate;

    /**
     * 返水
     */
    @Schema(description = "是否展示返水配置 true 展示 false 不展示")
    private Boolean  isShowRebate;

}
