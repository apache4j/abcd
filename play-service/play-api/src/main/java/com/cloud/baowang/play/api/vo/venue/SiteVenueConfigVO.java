package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@I18nClass
@Schema(description = "站点后台场馆配置信息")
public class SiteVenueConfigVO implements Serializable {


    /**
     * 游戏平台CODE
     */
    private String venueCode;

    /**
     * 场馆ID
     */
    private String venueId;

    /**
     * 负盈利手续费
     */
    private BigDecimal handlingFee;

    /**
     * 场馆有效流水费率
     */
    private BigDecimal validProportion;


    /**
     * 站点CODE
     */
    private String siteCode;


    /**
     * 状态
     */
    private Integer status;

    /**
     * 总控:最后一次状态
     */
    private Integer lastStatus;


    /**
     * 站点:最后一次状态
     */
    private Integer siteLastStatus;


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
     * 场馆描述
     */
    private String venueDesc;

    /**
     * 游戏场馆名称
     */
    private String venueName;


    /**
     * 背景图
     */
    private String pcBackgroundCode;

    /**
     * 场馆图标
     */
    private String pcLogoCode;


    /**
     * PC-图片
     */
    private String pcIconI18nCode;

    /**
     * H5图片
     */
    private String h5IconI18nCode;


    /**
     * 皮肤4:中等图
     */
    private String middleIconI18nCode;


    @Schema(description = "冠名标签")
    private Integer siteLabelChangeType;



}
