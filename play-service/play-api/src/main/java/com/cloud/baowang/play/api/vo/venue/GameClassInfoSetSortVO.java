package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Author: sheldon
 * @Date: 3/26/24 2:19 下午
 */
@Data
@Schema(description = "一级分类排序请求对象")
public class GameClassInfoSetSortVO {

    @Schema(description = "排序类型: true:目录排序,false:首页排序", required = true)
    @NotNull
    private Boolean type;

    @Schema(description = "分类顺序数组", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<GameClassInfoSetSortDetailVO> voList;

    @Schema(description = "币种", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String currencyCode;

    private String creator;

    private String updater;

}
