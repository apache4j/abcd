package com.cloud.baowang.common.gateway.vo;

import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerMaintenanceVO {
    /**
     * 站点名称
     */
    private String siteName;
    /**
     * 长Logo
     */
    private String longLogo;
    /**
     * 短Logo
     */
    private String shortLogo;
    //维护开始时间戳
    private Long beginTime;
    //维护结束时间戳
    private Long endTime;
    //维护开始时间字符串 yyyy/MM/dd HH:mm:ss
    private String beginTimeStr;
    //维护结束时间字符串 yyyy/MM/dd HH:mm:ss
    private String endTimeStr;

    // @Schema(description = "维护页面地址")
    private String maintenancePageAddress;

    // @Schema(description = "客服通道地址")
    private String channelAddr;

    // @Schema(description = "通道名称")
    private String channelName;

    // @Schema(description = "通道code")
    private String channelCode;

    // @Schema(description = "三方平台code")
    private String platformCode;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CUSTOMER_TYPE)
    // @Schema(description = "客服类型;1-在线客服")
    private Integer customerType;

    // @Schema(description = "客服类型名称")
    private String customerTypeText;

    // @Schema(description = "entId")
    private String entId;

    //@Schema(description = "merId")
    private String merId;

    // @Schema(description = "secret_key")
    private String secretKey;

    //@Schema(description = "App Key")
    private String appKey;

    // @Schema(description = "客服通道状态", hidden = true)
    private Integer status;

}
