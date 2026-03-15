package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
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
@Schema(description = "体育联赛-排序")
public class SportEventsInfoSortRequestVO extends PageVO {

    @Schema(description = "ID", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<GameClassInfoSetSortDetailVO> list;

}