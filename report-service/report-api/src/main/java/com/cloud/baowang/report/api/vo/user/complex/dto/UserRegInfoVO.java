package com.cloud.baowang.report.api.vo.user.complex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @auther amos
 * @create 2024-11-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员注册人数信息")
public class UserRegInfoVO   {
    private String date;
    /**币种*/
    private String currency;
    /** 会员注册人数 */
    private Integer memberRegistrationCount;
    /** 会员注册人数:后台 */
    private Integer memberRegistrationCountBacked;
    /** 会员注册人数:PC */
    private Integer memberRegistrationCountPc;
    /** 会员注册人数:android app */
    private Integer memberRegistrationCountAndroidApp;
    /** 会员注册人数:android h5 */
    private Integer memberRegistrationCountAndroidH5;
    /** 会员注册人数:ios app */
    private Integer memberRegistrationCountIosApp;
    /** 会员注册人数:ios h5 */
    private Integer memberRegistrationCountIosH5;

    private Integer registry;
    private Integer count;
    private String siteCode;

}
