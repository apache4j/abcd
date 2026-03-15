package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/5/2 13:35
 * @Version: V1.0
 **/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressBalanceMessageVO extends MessageBaseVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 链类型
     */
    private String chainType;
    /**
     * 网络
     */
    private String networkType;
    /**
     * 币种
     */
    private String coinCode;
    /**
     * 地址
     */
    private String addressNo;
    /**
     *余额
     */
    private BigDecimal balance;
}
