package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "游戏一级分类悬浮配置-新增")
public class GameOneFloatConfigAddReqVO extends PageVO {

    @Schema(description = "修改的时候要传的ID")
    private String id;

    @Schema(description = "一级分类ID")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String gameOneId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "游戏一级分类悬浮配置")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<GameOneFloatConfigDetailAddReqVO> detailList;



}
