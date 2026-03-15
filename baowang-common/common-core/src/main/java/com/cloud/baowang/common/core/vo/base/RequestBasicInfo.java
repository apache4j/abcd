package com.cloud.baowang.common.core.vo.base;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * @Desciption: 当前请求基本信息
 * 中控后台、app、站点后台、代理后台
 * @Author: Ford
 * @Date: 2024/7/26 15:24
 * @Version: V1.0
 **/
@Data
@FieldNameConstants
public class RequestBasicInfo {
    /**
     * 用户唯一标识id
     */
    private String oneId;
    /**
     * 登录账号
     */
    private String userAccount;
    /**
     * 当前登录号名称
     */
    private List<String> roleIds;
    /**
     * 站点编码
     */
    private String siteCode;
    /**
     * 站点时区
     */
    private String timezone;

    /**
     * 语言
     */
    private String language;

    /**
     *  盘口模式 0:国际盘 1:大陆盘
     *  总控后台为null
     *  其他默认为0
     */
    private Integer handicapMode;
    /**
     * 当前用户登录ip
     */
    private String requestIp;

    private String userAgent;

    private String referer;

    /**
     * 设备终端请求来源:IOS/H5/PC/
     * {@link com.cloud.baowang.common.core.enums.DeviceType}
     */
    private Integer reqClientSource;

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization;

    /**
     * 当前平台币种名称
     */
    private String platCurrencyName;

    /**
     * 当前平台币种符号
     */
    private String platCurrencySymbol;


    /**
     * 当前平台币种图标
     */
    private String platCurrencyIcon;

    /**
     * 当前用户访问的域名
     */
    private String bizCustom;

    /**
     * 终端设备号
     */
    private String deviceId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 终端设备号
     */
    private String deviceTypeVersion;
}
