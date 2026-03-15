package com.cloud.baowang.wallet.api.vo;

import lombok.Data;

@Data
public class SiteRechargeChannelVO {

    /**
     * 通道配置ID
     * SystemRechargeChannel.id
     */
    private String channelId;

    /**
     * 站点代码
     */
    private String siteCode;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;
    /**
     * wayId
     */
    private String wayId;

}
