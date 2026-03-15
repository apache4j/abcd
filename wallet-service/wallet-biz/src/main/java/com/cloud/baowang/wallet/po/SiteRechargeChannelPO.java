package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 站点充值通道配置表
 * </p>
 *
 * @author ford
 * @since 2024-07-27 05:23:06
 */
@Getter
@Setter
@TableName("site_recharge_channel")
public class SiteRechargeChannelPO extends BasePO {

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

    /**
     * 姓名/电子钱包姓名
     */
    private String recvUserName;

    /**
     * 银行编码
     */
    private String recvBankCode;
    /**
     * 银行名称
     */
    private String recvBankName;
    /**
     * 开户行
     */
    private String recvBankBranch;
    /**
     * 银行帐号/电子钱包地址/虚拟币地址
     */
    private String recvBankCard;
    /**
     * 电子钱包账户
     */
    private String recvBankAccount;

    /**
     * 收款码
     */
    private String recvQrCode;

    /**
     * VIP等级使用范围
     */
    private String vipGradeUseScope;

}
