package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("site_venue_config")
public class SiteVenueConfigPO extends BasePO {

    /**
     * 站点后台
     */
    private String siteCode;

    /**
     * 游戏平台CODE
     */
    private String venueCode;

    /**
     * 游戏场馆名称
     */
    private String venueName;


    /**
     * PC-图片多语言
     */
    private String pcIconI18nCode;


    /**
     * H5-图片多语言
     */
    private String h5IconI18nCode;

    /**
     * pc_场馆背景图
     */
    private String pcBackgroundCode;

    /**
     * pc_场馆LOGO
     */
    private String pcLogoCode;

    /**
     * 场馆描述
     */
    private String venueDesc;


    /**
     * 小图标1-多语言
     */
    private String smallIcon1I18nCode;


    /**
     * 小图标2-多语言
     */
    private String smallIcon2I18nCode;


    /**
     * 小图标3-多语言
     */
    private String smallIcon3I18nCode;


    /**
     * 小图标4-多语言
     */
    private String smallIcon4I18nCode;


    /**
     * 小图标5-多语言
     */
    private String smallIcon5I18nCode;


    /**
     * 小图标6-多语言
     */
    private String smallIcon6I18nCode;


    /**
     * 游戏横版图标-多语言
     */
    private String htIconI18nCode;

    /**
     * 游戏中版图标-多语言
     */
    private String middleIconI18nCode;

}
