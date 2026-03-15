package com.cloud.baowang.user.api.vo.userlabel;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author kimi
 */
@Data
@Schema(description = "标签对应的会员 分页查询 Request")
public class GetUserPageByLabelIdRequestVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "标签id")
    @NotNull(message = "标签id不能为空")
    private String labelId;
}
