package com.cloud.baowang.system.po.site.config;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("site_download_config")
public class SiteDownloadConfigPO extends BasePO {

    private String siteCode;
    /** 下载调整 1-安装包 2-域名地址*/
    private String jumpType;

    /** 安卓下载地址 */
    private String androidDownloadUrl;

    /** ios下载地址 */
    private String iosDownloadUrl;

    /** optionType=2 的时候存 */
    private String domainUrl;

    /** 下载图标 */
    private String icon;

    /** 轮播图配置 */
    private String banner;
}
