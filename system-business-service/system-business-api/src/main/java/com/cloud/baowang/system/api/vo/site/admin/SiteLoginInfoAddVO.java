package com.cloud.baowang.system.api.vo.site.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录日志添加请求对象")
public class SiteLoginInfoAddVO {


    @Schema(description = "用户名")
    private String userName;


    /**
     * 状态 0成功 1失败
     */
    @Schema(description = " 状态 0成功 1失败 ")
    private Integer status;

    /**
     * 地址
     */
    @Schema(description = "地址")
    private String ipaddr;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String msg;

    /**
     * 登录地点
     */
    @Schema(description = "登录地点")
    private String loginLocation;

    /**
     * 终端设备号
     **/
    @Schema(description = "终端设备号")
    private String deviceCode;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;

}
