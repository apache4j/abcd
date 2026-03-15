package com.cloud.baowang.agent.api.vo.label;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <h2></h2>
 *
 * @author kimi
 * date 2023/10/12
 */
@Data
@Schema(title = "根据代理 查询标签集合 VO")
public class GetLabelsByAgentAccountVO {

    @Schema(title = "标签名称")
    private String label;

    @Schema(title = "会员账号")
    private String userAccount;
}
