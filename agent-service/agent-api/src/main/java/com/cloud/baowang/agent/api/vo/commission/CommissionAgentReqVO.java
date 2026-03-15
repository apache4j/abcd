package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: fangfei
 * @createTime: 2024/09/22 16:03
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "分页请求对象")
public class CommissionAgentReqVO extends PageVO implements Serializable {
    @Schema(title = "id")
    private String id;

    @Schema(title = "方案Code", hidden = true)
    private String planCode;
}
