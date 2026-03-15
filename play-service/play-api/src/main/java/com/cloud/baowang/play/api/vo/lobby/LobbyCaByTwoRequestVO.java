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
@Schema(title = "根据二级分类ID查询游戏详情")
public class LobbyCaByTwoRequestVO extends PageVO {

    @Schema(description = "游戏二级标签ID,如果要查指定二级标签的游戏则传该字段",required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String gameTwoId;

    @Schema(description = "排序字段:1:你可能喜欢,2:最受欢迎的,3:最新的,4:A-Z,5:Z-A")
    private Integer sortFile;

    @Schema(description = "游戏供应商")
    private List<String> venueIds;




}
