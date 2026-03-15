package com.cloud.baowang.system.api.vo.site.agreement;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "站点帮助中心添加配置VO")
public class i18nMessagesVO {
    @NotNull(message = "id不能为空")
    private String id;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer code;

    @Schema(description = "编辑 多语言List")
    //@NotNull(message = "编辑列表不能为空")
    private List<I18nMsgFrontVO> i18nMessages;

    @Schema(description = "安卓下载地址")
    private String androidDownloadUrl;

    @Schema(description = "ios下载地址")
    private String iosDownloadUrl;

    @Schema(description = "telegram")
    private String telegram;

    @Schema(description = "skype")
    private String skype;

    @Schema(description = "app 下载页配置")
    private List<List<I18nMsgFrontVO>> i18nFileUrl;

    @Schema(description = "经营地址")
    private String businessAddress;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "图标完整url")
    private String iconFileUrl;

    @Schema(description = "1-安装包 2-域名地址")
    private String jumpType;

    @Schema(description = "域名地址")
    private String domainUrl;

    @Schema(description = "域名全路径地址")
    private String domainFullUrl;


    /**
     * 以下国内盘
     * */
    @Schema(description = "域名地址",hidden = true)
    private String siteCode;


}
