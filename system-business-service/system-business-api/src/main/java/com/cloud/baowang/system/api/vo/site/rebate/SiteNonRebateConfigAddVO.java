package com.cloud.baowang.system.api.vo.site.rebate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@Schema(description = "不返水配置列表vo")
public class SiteNonRebateConfigAddVO implements Serializable {

    private String siteCode;

    @Schema(description = "单条记录id 编辑需要")
    private String id;

    @Schema(description = "场馆类型 code")
    private String venueType;
    @Schema(description = "场馆类型 value")
    private String venueValue;

    @Schema(description = "场馆名称 code")
    private String venueCode;

    @Schema(description = "场馆名称 value")
    private String venueName;

    @Schema(description = "游戏名称")
    private List<GameInfoRebateVO> gameInfo;

}
