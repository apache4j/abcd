package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "二级分类排序-入参")
@I18nClass
public class GameTwoSortReqVO {

    @Schema(description = "一级分类ID")
    private String gameOneClassId;

    @Schema(description = "分类顺序数组", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<GameClassInfoSetSortDetailVO> voList;


}
