package com.cloud.baowang.system.po.site.config.media;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 媒体号配置表实体类
 */
@Data
@TableName("site_media_info")
public class SiteMediaInfoPO {

    private Long id;             // 主键ID
    private String siteCode;     // 站点编码
    private Long updatedTime;    // 更新时间
    private String updater;      // 更新人
    private String imgUrl;       // 图片URL
    private String imgLink;      // 图片跳转链接
}
