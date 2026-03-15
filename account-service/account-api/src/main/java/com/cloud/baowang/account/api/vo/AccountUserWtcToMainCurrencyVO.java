package com.cloud.baowang.account.api.vo;

import com.cloud.baowang.account.api.enums.AccountUserCoinEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ford
 * @Date 2025-10-14
 */
@Data
@Schema( description = "会员WTC转主货币请求对象")
public class AccountUserWtcToMainCurrencyVO {

    @Schema( description ="平台币账变对象")
    private AccountUserPlatformCoinAddReqVO userPlatformCoinAddReqVO;

    @Schema( description ="主货币账变对象")
    private AccountUserCoinAddReqVO userCoinAddReqVO;

}
