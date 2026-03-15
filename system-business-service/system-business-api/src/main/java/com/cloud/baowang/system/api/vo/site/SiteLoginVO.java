package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerVO;
import com.cloud.baowang.system.api.vo.site.siteDetail.SiteVenueQueryVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点列表返回对象")
@I18nClass
public class SiteLoginVO implements Serializable {

    /* 站点编号 */
    @Schema(description = "站点编号")
    private String siteCode;

    /* 站点名称 */
    @Schema(description = "站点名称")
    private String siteName;

    /* 站点前缀 */
    @Schema(description = "站点前缀")
    private String sitePrefix;

    /* 站点类型 */
    @Schema(description = "站点类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_TYPE)
    private Integer siteType;

    @Schema(description = "站点类型文本")
    private String siteTypeText;

    /*后台名称*/
    @Schema(description = "后台名称")
    private String bkName;

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

    /* 支持语言 */
    @Schema(description = "支持语言")
    private String language;

    @Schema(description = "支持语言列表")
    private List<CodeValueNoI18VO> languageList;
    @Schema(description = "语言列表")
    private List<LanguageManagerVO> languageManagerVOS;

    /* 支持币种 */
    @Schema(description = "支持币种")
    private String currency;

    @Schema(description = "支持币种Codes")
    @I18nField(type = I18nFieldTypeConstants.DICT_CURRENT_CODE_ARR)
    private String currencyCodes;

    /* 支持币种列表 */
    @Schema(description = "支持币种列表")
    private List<I18nMsgFrontVO> currencyCodesCurrentFrontList;

    @Schema(description = "站点时区")
    private String timezone;

    @Schema(description = "站点平台币币种信息")
    private String platCurrencyCode;
    @Schema(description = "站点平台币币种信息")
    private String platCurrencyName;
    @Schema(description = "站点平台币币种符号")
    private String platCurrencySymbol;
    @Schema(description = "邮箱短信验证码有效时间 单位分钟")
    private Integer codeExpireTime;

    @Schema(description = "返水开关 0-禁用 1-启用")
    private Integer rebateStatus;
    @Schema(description = "盘口模式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_HANDICAP_MODE)
    private Integer handicapMode;
    @Schema(description = "0:国际盘 1:大陆盘")
    private String handicapModeText;
}
