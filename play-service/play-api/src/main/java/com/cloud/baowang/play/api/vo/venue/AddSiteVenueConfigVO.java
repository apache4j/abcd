package com.cloud.baowang.play.api.vo.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "游戏平台VO对象")
public class AddSiteVenueConfigVO implements Serializable {

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

    private String adminVenueName;


    /**
     * PC-图片多语言
     */
    private String pcIconI18nCode;

    private String adminPcIconI18nCode;


    /**
     * H5-图片多语言
     */
    private String h5IconI18nCode;

    private String adminH5IconI18nCode;

    /**
     * pc_场馆背景图
     */
    private String pcBackgroundCode;
    private String adminPcBackgroundCode;

    /**
     * pc_场馆LOGO
     */
    private String pcLogoCode;
    private String adminPcLogoCode;
    /**
     * 场馆描述
     */
    private String venueDesc;
    private String adminVenueDesc;


    /**
     * 小图标1-多语言
     */
    private String smallIcon1I18nCode;
    private String adminSmallIcon1I18nCode;


    /**
     * 小图标2-多语言
     */
    private String smallIcon2I18nCode;
    private String adminSmallIcon2I18nCode;


    /**
     * 小图标3-多语言
     */
    private String smallIcon3I18nCode;
    private String adminSmallIcon3I18nCode;

    /**
     * 小图标4-多语言
     */
    private String smallIcon4I18nCode;
    private String adminSmallIcon4I18nCode;

    /**
     * 小图标5-多语言
     */
    private String smallIcon5I18nCode;
    private String adminSmallIcon5I18nCode;

    /**
     * 小图标6-多语言
     */
    private String smallIcon6I18nCode;
    private String adminSmallIcon6I18nCode;

    /**
     * 游戏横版图标-多语言
     */
    private String htIconI18nCode;
    private String adminHtIconI18nCode;
    /**
     * 游戏中版图标-多语言
     */
    private String middleIconI18nCode;
    private String adminMiddleIconI18nCode;

}
