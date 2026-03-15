package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("hot_wallet_address_trade_record")
public class HotWalletAddressTradeRecordPO extends BasePO {

    /**
     * 交易HASH
     */
    private String tradeHash;


    /**
     * 热钱包地址
     */
    private String address;

    /**
     * 交易，归集信息
     */
    private String jsonStr;


}
