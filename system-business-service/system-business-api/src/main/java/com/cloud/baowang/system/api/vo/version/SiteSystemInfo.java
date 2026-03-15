package com.cloud.baowang.system.api.vo.version;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.language.LanguageValidListCacheVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "APP请求站点属性")
@I18nClass
public class SiteSystemInfo {

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "语言列表")
    private List<LanguageManagerListVO> languageList;
    @Schema(description = "站点默认的语言列表")
    private List<LanguageValidListCacheVO> validListCacheVOS;

    @Schema(description = "下载链接-安卓")
    private String androidDownloadUrl;

    @Schema(description = "下载链接-ios")
    private String iosDownloadUrl;

    @Schema(description = "首页链接")
    private String homePageUrl;

    @Schema(description = "长logo图片地址")
    private String longLogoImage;

    @Schema(description = "短logo图片地址")
    private String shortLogoImage;

    @Schema(description = "轮播图")
    private List<I18nMsgFrontVO> downloadImgI18nList;

    @Schema(description = "h5下载提示图标")
    private String iconFullUrl;

    @Schema(description = "下载调整 1-安装包地址 2-域名地址")
    private String jumpType;

    @Schema(description = "2-域名地址")
    private String domainUrl;


}
