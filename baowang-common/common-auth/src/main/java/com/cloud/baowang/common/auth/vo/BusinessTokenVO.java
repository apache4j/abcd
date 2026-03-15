package com.cloud.baowang.common.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessTokenVO implements Serializable {

    @Schema(title = "Id")
    private String id;

    @Schema(description = "商务id")
    private String merchantId;

    @Schema(description = "商务账号")
    private String merchantAccount;

    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(description = "当前语言")
    private String language;

    @Schema(description = "账号状态 1正常 2登录锁定")
    private String status;

    private String token;

    private Date loginTime;

    private Long expireTime;

    private String siteCode;

}
