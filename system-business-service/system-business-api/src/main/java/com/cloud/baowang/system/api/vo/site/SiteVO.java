package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
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
 * @Author : 小智
 * @Date : 2024/7/26 13:42
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点列表返回对象")
@I18nClass
public class SiteVO implements Serializable {

    /* 站点编号 */
    @Schema(description = "站点编号")
    private String siteCode;

    /* 站点名称 */
    @Schema(description = "站点名称")
    private String siteName;

    /* 站点前缀 */
    @Schema(description = "站点前缀")
    private String sitePrefix;

    @Schema(description = "下载页地址")
    private String downLoadDomainAddr;

    /* 所属公司 */
    @Schema(description = "所属公司")
    private String company;

    /* 站点类型 */
    @Schema(description = "站点类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_TYPE)
    private Integer siteType;

    @Schema(description = "站点类型文本")
    private String siteTypeText;

    /* *//* 站点模式 *//*
    @Schema(description = "站点模式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_MODEL)
    private Integer siteModel;

    *//* 站点模式文本 *//*
    @Schema(description = "站点模式文本")
    private String siteModelText;*/

    /* 状态 */
    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_STATUS)
    private Integer status;

    @Schema(description = "维护时间-开始")
    private Long maintenanceTimeStart;

    @Schema(description = "维护时间-结束")
    private Long maintenanceTimeEnd;

    /* 状态文本 */
    @Schema(description = "状态")
    private String statusText;

    /*抽成方案*/
    @Schema(description ="抽成方案 0:负盈利 1:有效流水")
    private Integer commissionPlan;

    @Schema(description = "抽成方案文本")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_PLAN)
    private String commissionPlanText;

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

    /* 站点管理员账号 */
    @Schema(description = "站点管理员账号")
    private String siteAdminAccount;
    @Schema(description = "白名单")
    private String allowIps;

    /* 备注 */
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "站点时区")
    private String timezone;

    @Schema(description = "最近的步骤")
    private String step;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "操作时间")
    private Long operatorTime;

    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "场馆信息")
    private List<SiteVenueQueryVO> siteVenueInfoVOS;

    @Schema(description = "站点平台币币种信息")
    private String platCurrencyCode;
    @Schema(description = "站点平台币币种信息")
    private String platCurrencyName;
    @Schema(description = "站点平台币币种符号")
    private String platCurrencySymbol;
    @Schema(description = "邮箱短信验证码有效时间 单位分钟")
    private Integer codeExpireTime;

    @Schema(description = "语言管理配置信息")
    private List<LanguageValidListCacheVO> validListCacheVOS;

    @Schema(description = "是否显示验证码  0 不显示  1 显示")
    private Integer showCaptcha;

    @Schema(description = "pc下载地址-皮肤2使用")
    private String pcDownLoadUrl;

    @Schema(description = "h5下载提示图标")
    private String iconFullUrl;

    @Schema(description = "返水开关 0-禁用 1-启用")
    private Integer rebateStatus;

    @Schema(description = "保证金开关")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_STATUS)
    private Integer guaranTeeFlag;

    @Schema(description = "保证金开关状态 0-禁用 1-启用")
    private String guaranTeeFlagText;

    @Schema(description = "盘口模式 盘口模式 0海外盘 1华人盘")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_HANDICAP_MODE)
    private Integer handicapMode;

    @Schema(description = "0:国际盘 1:大陆盘")
    private String handicapModeText;

}
