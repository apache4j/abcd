package com.cloud.baowang.common.core.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "站点维护vo")
@Data
@I18nClass
public class SiteMaintenanceVO {
    @Schema(description = "站点信息")
    private String siteCode;
    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "长Logo")
    private String longLogo;
    @Schema(description = "短Logo")
    private String shortLogo;

    @Schema(description = "维护页面地址")
    private String maintenancePageAddress;

    @Schema(description = "维护时间-开始时间")
    private Long maintenanceTimeStart;
    @Schema(description = "维护时间-结束时间")
    private Long maintenanceTimeEnd;

    /**
     * 0.禁用,1.启用,2.维护
     * {@link com.cloud.baowang.common.core.enums.SiteStatusEnums}
     */
    @Schema(description = "站点状态")
    private Integer siteStatus;

    @Schema(description = "客服通道地址")
    private String channelAddr;

    @Schema(description = "通道名称")
    private String channelName;

    @Schema(description = "通道code")
    private String channelCode;

    @Schema(description = "三方平台code")
    private String platformCode;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CUSTOMER_TYPE)
    @Schema(description = "客服类型;1-在线客服")
    private Integer customerType;

    @Schema(description = "客服类型名称")
    private String customerTypeText;

    @Schema(description = "entId")
    private String entId;

    @Schema(description = "merId")
    private String merId;

    @Schema(description = "secret_key")
    private String secretKey;

    @Schema(description = "App Key")
    private String appKey;

    @Schema(description = "状态", hidden = true)
    private Integer status;

    public String getMaintenancePageAddress() {
        return maintenancePageAddress==null?"":this.maintenancePageAddress;
    }

    public String getAppKey() {
        return appKey==null?"":this.appKey;
    }

    public String getSecretKey() {
        return secretKey==null?"":this.secretKey;
    }

    public String getMerId() {
        return merId==null?"":this.merId;
    }

    public String getEntId() {
        return entId==null?"":this.entId;
    }
}
