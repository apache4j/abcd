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
@Schema(description = "会员登录人数信息")
public class UserLoginReportVO   {
    private String date;
    /**币种*/
    private String currency;
    /** 会员登录人数 */
    private Integer memberLoginCount;
    /** 会员登录人数:后台 */
    private Integer memberLoginCountBacked;
    /** 会员登录人数:PC */
    private Integer memberLoginCountPc;
    /** 会员登录人数:android app */
    private Integer memberLoginCountAndroidApp;
    /** 会员登录人数:android h5 */
    private Integer memberLoginCountAndroidH5;
    /** 会员登录人数:ios app */
    private Integer memberLoginCountIosApp;
    /** 会员登录人数:ios h5 */
    private Integer memberLoginCountIosH5;

    private Integer loginTerminal;
    private Integer count;
    private String siteCode;


}
