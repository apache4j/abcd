package com.cloud.baowang.wallet.api.vo.withdraw;


import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="校验会员提款信息返回对象")
public class CheckUserWithdrawInfoVO {


    @Schema(description ="是否绑定交易密码 0否 1是")
    private Integer bindWithdrawPassword;

    @Schema(description ="是否绑定手机号 0否 1是")
    private Integer bindPhone;


}
