package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title ="会员提款配置请求VO")
public class UserWithdrawConfigRequestVO {


    @Schema(description ="币种")
    private String currency;

    private String siteCode;



}
