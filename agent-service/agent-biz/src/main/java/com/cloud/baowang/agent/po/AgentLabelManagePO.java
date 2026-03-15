/**
 * @(#)AgentLabelRecord.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <h2></h2>
 *
 * @author wade
 * date 2023/10/12
 */
@Data
@TableName("agent_label_manage")
@Schema(title = "代理给会员标记标签记录", description = "代理给会员标记标签记录")
public class AgentLabelManagePO extends BasePO {
    @Schema(description = "siteCode")
    private String siteCode;
    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "标签名称")
    private String label;
    @Schema(title = "会员账号")
    private String userAccount;

}
