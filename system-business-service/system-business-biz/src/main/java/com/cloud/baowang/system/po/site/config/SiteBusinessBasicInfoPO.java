package com.cloud.baowang.system.po.site.config;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("site_business_basic_info")
public class SiteBusinessBasicInfoPO {
    @TableId
    private Long id;

    private String phone;

    private String email;

    private String telegram;

    private String wechat;

    private String qq;

    private String whatsApp;

    private String messenger;

    private String businessName;

    private String siteCode;

    private Long updatedTime;

    private String updater;

    private String h5Icon;

    private String pcIcon;

    private Integer sort;

}
