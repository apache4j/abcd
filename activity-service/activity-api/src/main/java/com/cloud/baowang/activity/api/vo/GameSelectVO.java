package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: GameSelectVO
 * @author: wade
 * @description: 选择的游戏大类
 * @date: 6/3/25 21:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameSelectVO {
    @Schema(title = "主键id")
    private String baseId;

    @Schema(description = "赠送金额", example = "50.00")
    private String activityTemplate;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    private String venueType;



}
