package com.cloud.baowang.system.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * @author qiqi
 */
@Data
@Schema(description = "系统登录日志返回字段")
public class BusinessLoginInfoVO implements Serializable {

    @Schema(description = "会员账号")
    private String userName;

    @Schema(description = "账号id")
    private String userId;

    @Schema(description = "登录IP地址")
    private String ipaddr;

    @Schema(description = "登录地点")
    private String loginLocation;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "终端设备号")
    private String deviceCode;

    @Schema(description = "账号类型")
    private Integer status;

    @Schema(description = "账号类型-名字")
    private String statusName;

    @Schema(description = "访问时间")
    private Long accessTime;



}
