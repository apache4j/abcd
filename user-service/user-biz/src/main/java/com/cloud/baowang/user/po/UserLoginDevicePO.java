package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员登录设备记录表
 */
@Data
@Accessors(chain = true)
@TableName("user_login_device")
@Schema(description = "会员登录设备记录表")
public class UserLoginDevicePO extends BasePO implements Serializable {

    @Schema(description = "会员ID")
    private String userAccount;

    @Schema(description = "会员本设备最后一次登录ip")
    private String loginIp;

    @Schema(description = "会员本设备最后一次登录时间")
    private Long loginTime;

    @Schema(description = "登录设备")
    private String loginDevice;

    @Schema(description = "登录地址")
    private String loginAddress;

    @Schema(description = "登录状态 0 未使用  1正在使用")
    private Integer status;
}
