package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(title = "加密货币充值地址返回对象")
public class CryptoCurrencyDepositAddressVO {


    /**
     * 链/协议类型
     */
    @Schema(description = "链/协议类型")
    private String chinaType;


    @Schema(description = "充值地址")
    private String address;

    /**
     * 会员ID
     */
    private String userId;
}
