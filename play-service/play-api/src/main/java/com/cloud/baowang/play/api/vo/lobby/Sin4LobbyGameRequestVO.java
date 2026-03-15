package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(title = "皮肤4-游戏大厅热门游戏")
public class Sin4LobbyGameRequestVO extends PageVO {

    @Schema(description = "二级分类ID" , required = true)
    private String gameTwoId;

    @Schema(description = "皮肤4标签:0:所有,1:热门-推荐,2:新游戏,3:收藏", required = true)
    private Integer skin4Label;

    @Schema(description = "模糊查询游戏名称",required = true)
    private String gameName;

    @Schema(description = "游戏多语言CODE", hidden = true)
    private List<String> gameI18nCodeList;


}
