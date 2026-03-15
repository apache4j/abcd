package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author : kimi
 * @Date : 26/6/23 11:23 AM
 * @Version : 1.0
 */
@Data
@Schema(description ="客户端获取VIP福利")
public class GetVIPAwardVO {
    /**
     *  奖励类型(0升级礼金 1生日礼金 2周红包)
     */
    @Schema(description ="奖励类型 0升级礼金 4生日礼金 1周红包")
    private Integer awardType;

    @Schema(description ="奖励类型 0升级礼金 4生日礼金 1周红包e")
    private String awardTypeName;

    @Schema(description ="奖励金额")
    private BigDecimal awardAmount;

    @Schema(description ="领取状态 (0:未领取 客户端-立即领取,1:已领取 客户端-已领取,2:已过期 客户端- 已过期,3:没资格-立即领取(置灰))")
    private Integer receiveStatus;

    @Schema(description ="订单号")
    private String orderId;

    @Schema(description ="排序")
    private Integer sort;
}
