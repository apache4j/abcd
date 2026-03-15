package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author sheldon
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "一级分类排序参数对象")
public class GameSortRequestVO extends PageVO {

    @Schema(description = "目录名称")
    private Boolean directorySort;

    @Schema(description = "首页名称")
    private Boolean homeSort;

    @Schema(description = "币种")
    private String currencyCode;

}
