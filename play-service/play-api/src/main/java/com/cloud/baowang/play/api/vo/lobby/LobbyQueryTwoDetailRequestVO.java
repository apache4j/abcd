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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "查询游戏大厅二级分类详情")
public class LobbyQueryTwoDetailRequestVO  {

    @Schema(description = "一级分类ID", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String gameOneId;


}
