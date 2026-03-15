package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author kimi
 */
@Builder
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台-id带分页请求对象")
public class IdPageVO extends PageVO {

    @Schema(description = "id")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;
}
