package com.cloud.baowang.system.api.vo.site.rebate;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@Schema(description = "不返水配置列表vo")
@I18nClass
public class SiteNonRebateConfigVO implements Serializable {

    private String id;

    private String siteCode;

    @Schema(description = "场馆类型 code")
    private String venueType;
    @Schema(description = "场馆类型 value")
    private String venueValue;

    @Schema(description = "场馆名称 code")
    private String venueCode;

    @Schema(description = "场馆名称 value")
    @I18nField
    private String venueName;

    @Schema(description = "游戏信息")
    private List<GameInfoRebateVO> gameInfo;

    @Schema(description = "操作人")
    private String updater;

    @Schema(description = "最近操作时间")
    private String updatedTime;

}
