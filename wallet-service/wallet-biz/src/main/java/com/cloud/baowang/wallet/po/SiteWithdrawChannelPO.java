package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
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
@TableName("site_withdraw_channel")
public class SiteWithdrawChannelPO extends BasePO {

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
     * 排序
     */
    private Integer sortOrder;

}
