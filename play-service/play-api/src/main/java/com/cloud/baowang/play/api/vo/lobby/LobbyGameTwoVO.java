package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author: sheldon
 * @Date: 3/30/24 9:23 上午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "二级分类详情")
@I18nClass
public class LobbyGameTwoVO  {


    @Schema(description = "id")
    private String id;

    @Schema(title = "游戏一级分类ID")
    private String gameOneClassId;


    @Schema(description = "名称")
    @I18nField
    private String name;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(title = "图标")
    private String iconFileUrl;


    @Schema(description = "二级分类,皮肤4横图标",hidden = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String classTwoHtIconI18nCode;

    @Schema(title = "二级分类,皮肤4横图标")
    private String classTwoHtIconI18nCodeFileUrl;

    @Schema(description = "最高返水")
    private BigDecimal maxRebateAmount;

    @Schema(description = "赛事数量")
    private Long eventsNumber;

    @Schema(description = "冠名标签")
    private Integer siteLabelChangeType;



}
