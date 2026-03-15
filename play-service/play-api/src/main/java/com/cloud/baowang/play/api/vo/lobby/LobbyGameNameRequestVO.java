package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: sheldon
 * @Date: 4/2/24 10:28 上午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "根据游戏名称模糊查询对象")
public class LobbyGameNameRequestVO extends PageVO {

    @Schema(description = "模糊查询游戏名称",required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String gameName;

    @Schema(description = "一级分类ID")
    private String gameOneClassId;

    @Schema(description = "二级分类ID")
    private String gameTwoId;



}
