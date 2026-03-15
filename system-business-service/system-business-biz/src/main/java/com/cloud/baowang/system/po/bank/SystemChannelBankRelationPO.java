package com.cloud.baowang.system.po.bank;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

/**
 * 系统字典配置表 PO 类
 */
@Data
@TableName("system_channel_bank_relation")
public class SystemChannelBankRelationPO extends BasePO {

    /**
     * 货币代码
     */
    private String configId;

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
     * 银行卡id
     */
    private String bankId;

    /**
     * 银行编码
     */
    private String bankCode;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 通道-银行编码映射
     */
    private String bankChannelMapping;


}
