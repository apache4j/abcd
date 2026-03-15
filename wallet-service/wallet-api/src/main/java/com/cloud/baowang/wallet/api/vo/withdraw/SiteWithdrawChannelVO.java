package com.cloud.baowang.wallet.api.vo.withdraw;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 站点提款通道配置表
 * </p>
 *
 * @author qiqi
 */
@Getter
@Setter
public class SiteWithdrawChannelVO {

    /**
     * 通道配置ID
     * SiteWithdrawChannel.id
     */
    private String channelId;
    /**
     * 提款方式主键（system_withdraw_way表id）
     */
    private String withdrawWayId;

    /**
     * 站点代码
     */
    private String siteCode;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

}
