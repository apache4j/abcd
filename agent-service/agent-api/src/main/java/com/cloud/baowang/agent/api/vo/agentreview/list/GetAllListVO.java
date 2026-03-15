package com.cloud.baowang.agent.api.vo.agentreview.list;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * GetAllListVO
 * </p>
 *
 * @author kimi
 * @since 2023-10-10
 */
@Data
@Schema(description = "GetAllListVO对象")
public class GetAllListVO {

    @Schema(description = "代理id")
    private String id;
    @Schema(description = "代理编号")
    private String agentId;

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "父级id")
    private String parentId;

    @Schema(description = "层级结果 用逗号拼接")
    private String path;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "注册时间")
    private Long registerTime;
}
