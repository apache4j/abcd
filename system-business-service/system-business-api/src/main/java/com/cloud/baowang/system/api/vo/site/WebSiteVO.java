package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerVO;
import com.cloud.baowang.system.api.vo.language.LanguageValidListCacheVO;
import com.cloud.baowang.system.api.vo.site.siteDetail.SiteVenueQueryVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : mufan
 * @Date : 2026/6/27 13:42
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点列表返回对象")
@I18nClass
public class WebSiteVO implements Serializable {

    /* 站点编号 */
    @Schema(description = "站点编号")
    private String siteCode;

    /* 站点名称 */
    @Schema(description = "站点名称")
    private String siteName;


    @Schema(description = "下载页地址")
    private String downLoadDomainAddr;

    /*皮肤管理*/
    @Schema(description = "皮肤管理")
    private String skin;

    /*长logo*/
    @Schema(description = "长logo")
    private String longLogo;
    @Schema(description = "长logo图片地址-只做编辑展示用")
    private String longLogoImage;

    /*短logo*/
    @Schema(description = "短logo")
    private String shortLogo;
    @Schema(description = "短logo图片地址-只做编辑展示用")
    private String shortLogoImage;

    @Schema(description = "站点平台币币种信息")
    private String platCurrencyName;

    @Schema(description = "邮箱短信验证码有效时间 单位分钟")
    private Integer codeExpireTime;

    @Schema(description = "语言管理配置信息")
    private List<LanguageValidListCacheVO> validListCacheVOS;

    @Schema(description = "是否显示验证码  0 不显示  1 显示")
    private Integer showCaptcha;

    @Schema(description = "h5下载提示图标")
    private String iconFullUrl;

    @Schema(description = "pc下载地址-皮肤2使用")
    private String pcDownLoadUrl;

    @Schema(description = "站点时区")
    private String timezone;

    /*黑底-长logo*/
    @Schema(description = "黑夜-长logo")
    private String blackLongLogo;
    @Schema(description = "黑夜-长logo图片地址-只做编辑展示用")
    private String blackLongLogoImage;

    /*黑底-短logo*/
    @Schema(description = "黑夜-短logo")
    private String blackShortLogo;
    @Schema(description = "黑夜-短logo图片地址-只做编辑展示用")
    private String blackShortLogoImage;
}
