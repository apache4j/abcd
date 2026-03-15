package com.cloud.baowang.play.api.vo.lobby;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: sheldon
 * @Date: 3/30/24 9:23 上午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "游戏大厅-体育彩票返回对象")
public class LobbyPEGameInfoVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "图标")
    private String pcIcon;

    @Schema(description = "备注")
    private String remark;



}
