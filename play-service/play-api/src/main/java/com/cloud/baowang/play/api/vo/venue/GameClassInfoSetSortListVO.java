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
public class GameClassInfoSetSortListVO {

    @Schema(description = "一级分类排序请求数组对象", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<GameClassInfoSetSortVO> list;

}
