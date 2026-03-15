package com.cloud.baowang.user.api.vo.freegame;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * @Desciption: 查询用户主货币
 * @Author: Ford
 * @Date: 2024/10/8 11:43
 * @Version: V1.0
 **/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = " 查询用户主货币")
public class GetUserInfoCurrencyReqVO {

    /**
     * 站点编码
     */
    @Schema(description = "siteCode",hidden = true)
    private String siteCode;

    /**
     * 用户list
     */
    @Schema(description = "用户list")
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private String userAccounts;
}
