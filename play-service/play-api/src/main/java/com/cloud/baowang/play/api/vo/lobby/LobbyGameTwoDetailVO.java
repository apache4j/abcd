package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: sheldon
 * @Date: 3/30/24 9:23 上午
 */

@Data
@Builder
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "二级分类详情")
public class LobbyGameTwoDetailVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "名称")
    @I18nField
    private String name;

    @Schema(description = "标签:0:无,1:热门,2:新游戏")
    private Integer label;

    @Schema(description = "是否更多游戏: true=更多")
    private boolean hasMoreGames ; // 或者 true，视情况而定


    @Schema(title = "图片")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(title = "图片")
    private String iconFileUrl;

    @Schema(description = "二级分类排序",hidden = true)
    private Long classTwoSort;


    @Schema(description = "子列表")
    private List<LobbyGameInfoVO> gameInfoList;

}
