package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("venue_info")
public class VenueInfoPO extends BasePO {


    /**
     * 游戏场馆名称
     */
    private String venueName;

    /**
     * 币种类型
     */
    private Integer venueCurrencyType;


    /**
     * 场馆费率类型:0:场馆负盈利费率,1:场馆有效流水费率,2:负盈利&有效流水费率
     */
    private Integer proportionType;


    /**
     * 场馆有效流水费率
     */
    private BigDecimal validProportion;


    /**
     * 游戏平台名称
     */
    private String venuePlatform;


    /**
     * 游戏-平台名称
     */
    private String venuePlatformName;


    /**
     * 游戏平台CODE
     */
    private String venueCode;

    /**
     * 场馆负盈利费率
     */
    private BigDecimal venueProportion;

    /**
     * 币种
     */
    private String currencyCode;


    /**
     * 场馆类型 1:体育,2:视讯,3:棋牌,4:电子,5:彩票,6:斗鸡,7:电竞
     */
    private Integer venueType;

    /**
     * PC-游戏平台图标
     */
    private String pcVenueIcon;

    /**
     * H5-游戏平台图标
     */
    private String h5VenueIcon;

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
     * 状态（ 状态:1:开启中,2:维护中,3:已禁用)
     */
    private Integer status;


    /**
     * API URL
     */
    private String apiUrl;

    /**
     * 拉单 URL
     */
    private String betUrl;

    /**
     * 游戏 URL
     */
    private String gameUrl;

    /**
     * 拉单key
     */
    private String betKey;

    /**
     * 商户编码
     */
    private String merchantNo;

    /**
     * AES 密钥
     */
    private String aesKey;

    /**
     * 商户密钥
     */
    private String merchantKey;

    /**
     * 备注
     */
    private String remark;

    /**
     * 维护开始时间
     */
    private Long maintenanceStartTime;

    /**
     * 维护结束时间
     */
    private Long maintenanceEndTime;

    /**
     * 场馆接入类型:1:数据源,2:场馆,3:游戏
     */
    private Integer venueJoinType;

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


    private String middleIconI18nCode;




}
