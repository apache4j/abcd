package com.cloud.baowang.play.api.vo.venue;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Schema(description = "游戏平台VO对象")
public class VenueInfoRspVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "三方平台")
    private String venuePlatform;

    @Schema(description = "游戏平台名称")
    private String venueName;

    @Schema(description = "游戏平台code")
    private String venueCode;

    @Schema(description = "钱包名称")
    private String walletName;

    @Schema(description = "状态（ 1开启中 2 维护中 0 已禁用)")
    private Integer status;

    @Schema(description = "状态（1开启中  0 锁定)")
    private Integer walletStatus;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "更新时间")
    private Long updatedTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "API UR")
    private String apiUrl;

    @Schema(description = "游戏 URL")
    private String gameUrl;

    @Schema(description = "拉单 URL")
    private String betUrl;

    @Schema(description = "拉单key")
    private String betKey;

    @Schema(description = "商户编码")
    private String merchantNo;

    @Schema(description = "AES 密钥")
    private String aesKey;

    @Schema(description = "商户密钥")
    private String merchantKey;

    @Schema(description = "维护开始时间")
    private Long maintenanceStartTime;

    @Schema(description = "维护结束时间")
    private Long maintenanceEndTime;

    @Schema(description = "场馆费率")
    private BigDecimal venueProportion;

}
