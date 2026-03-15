package com.cloud.baowang.user.api.vo.freegame;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


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
@Schema(description = " 查询用户主货币返回")
public class GetUserInfoCurrencyRespVO {


    /**
     * 用户list
     */
    @Schema(description = "第一个用户的货币")
    private String currency;

    /**
     * 用户list
     */
    @Schema(description = "是否都是同一货币")
    private Boolean isSingleCurrency = true;

    /**
     * 用户list
     */
    @Schema(description = "不同货币的账户")
    private List<String> userAccounts;
}
