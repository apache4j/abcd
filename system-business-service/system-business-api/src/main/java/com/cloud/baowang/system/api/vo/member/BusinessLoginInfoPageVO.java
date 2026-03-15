package com.cloud.baowang.system.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(description = "登录日志VO对象")
public class BusinessLoginInfoPageVO {

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "登录时间")
    private Long accessTime;

    @Schema(description = "登录IP")
    private String ipaddr;

    @Schema(description = "终端设备号")
    private String deviceCode;

    @Schema(description = "登录地点")
    private String loginLocation;

    /**
     * 状态 0成功 1失败
     */
    @Schema(description = "登录状态 0 成功 1 失败")
    private Integer status;
}
