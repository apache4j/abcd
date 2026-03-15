package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: sheldon
 * @Date: 4/2/24 10:28 上午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "游戏详情入参查询对象")
public class LobbyGameDetailRequestVO extends PageVO {

    @Schema(description = "一级分类ID")
    private String gameOneId;

    @Schema(description = "二级分类ID")
    private String gameTwoId;

    @Schema(description = "二级分类ID", hidden = true)
    private List<String> gameTwoIds;

    @Schema(description = "标签类型:0:无,1:热门-推荐,2:新游戏")
    private Integer label;

    @Schema(description = "模糊查询游戏名称")
    private String gameNameCode;

    @Schema(description = "游戏code")
    private String gameId;

    @Schema(description = "场馆CODE")
    private String venueCode;

    @Schema(description = "状态", hidden = true)
    private List<Integer> statusIds;

    @Schema(description = "游戏多语言CODE", hidden = true)
    private List<String> gameI18nCodeList;

    @Schema(description = "游戏ID", hidden = true)
    private List<String> ids;

    @Schema(description = "排序类型", hidden = true)
    private Integer sortStatus;

    @Schema(description = "查询收藏的游戏", hidden = true)
    private String collectionUserId;

    @Schema(description = "币种", hidden = true)
    private String currencyCode;

}
