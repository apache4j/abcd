package com.cloud.baowang.system.po.bank;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

/**
 * 系统字典配置表 PO 类
 */
@Data
@TableName("system_channel_bank_relation_base")
public class SystemChannelBankRelationBasePO extends BasePO {

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 提款方式ID
     */
    private String channelId;

    /**
     * 通道代码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 银行通道状态（1-全量 2-非全量 3-未配置）
     */
    private String bankChannelStatus;


}
