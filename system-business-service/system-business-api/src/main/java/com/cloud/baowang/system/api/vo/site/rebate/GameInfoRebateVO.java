package com.cloud.baowang.system.api.vo.site.rebate;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "不返水配置查询游戏信息")
public class GameInfoRebateVO  implements Serializable {

    @Schema(description = "游戏名称")
    private String gameName;

    @Schema(description = "游戏id")
    private String gameId;

    @Schema(description = "游戏id")
    private String thirdGameId;

}
