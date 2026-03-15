package com.cloud.baowang.agent.api.vo.agentreview;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员账号审核返回对象")
public class UserAccountUpdateVO {

    /**
     * 统计数量
     */
    private Long num;

    /**
     * 路由
     */
    private String router;

    /**
     * 0: 新增代理审核 1:代理账户修改审核 2: 会员转代审核 3:会员溢出审核
     */
    private String countType;
}
