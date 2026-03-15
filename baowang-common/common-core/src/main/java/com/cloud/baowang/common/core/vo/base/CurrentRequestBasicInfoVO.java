package com.cloud.baowang.common.core.vo.base;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption: 当前请求信息
 * @Author: Ford
 * @Date: 2024/9/28 11:56
 * @Version: V1.0
 **/
@Data
@Schema(description = "当前请求信息")
@I18nClass
public class CurrentRequestBasicInfoVO {
    @Schema(description = "当前语言")
    String language ;
    @Schema(description = "当前代理")
    String userAgent ;
    @Schema(description = "当前域名")
    String referer ;
    @Schema(description = "当前用户编号")
    String userId ;
    @Schema(description = "当前用户账号")
    String userAccount ;
    @Schema(description = "当前登录IP")
    String loginIp ;
    @Schema(description = "当前站点编号")
    String siteCode ;
    @Schema(description = "当前时区")
    String timezone;
    @Schema(description = "设备类型")
    Integer deviceType;
    @Schema(description = "当前平台币种代码")
    private String platCurrencyCode;
    @Schema(description = "当前平台币种名称")
    private String platCurrencyName;
    @Schema(description = "当前平台币种符号")
    private String platCurrencySymbol;
    @Schema(description = "当前平台币种图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String platCurrencyIcon;
    @Schema(description = "当前平台币种图标全路径")
    private String platCurrencyIconFileUrl;
    @Schema(description = "数据脱敏 true 需要脱敏 false 不需要脱敏")
    Boolean dataDesensitization;
    @Schema(description = "当前用户访问的域名")
    private String bizCustom;
}
