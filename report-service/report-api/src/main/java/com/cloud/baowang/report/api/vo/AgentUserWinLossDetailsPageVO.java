/**
 * @(#)AgentUserListVO.java, 10月 25, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.report.api.vo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * <h2></h2>
 *
 * @author wayne
 * date 2023/10/25
 */
@Data
@Schema(title = "代理会员盈亏盈亏明细列表请求参数")
public class AgentUserWinLossDetailsPageVO extends PageVO {

    @Schema(title = "站点")
    private String siteCode;

    @NotBlank(message = "用户账号不能为空")
    @Schema(title = "用户账号")
    private String searchAccount;
    @NotBlank(message = "搜索时间范围不能为空")
    @Schema(title = "搜索时间范围")
    private Integer searchTimeType;
    @Schema(title = "代理账号")
    private String agentAccount;
    @Schema(title = "自定义搜索时间范围开始时间")
    private Long statTime;
    @Schema(title = "自定义搜索时间范围开始时间")
    private Long endTime;

    @Schema(title = "搜索用户层级")
    private String searchLevelType;
    @Schema(title = "搜索用户类型")
    private String searchAccountType;
    private String curAgentId;
    @Schema(title = "代理账号集合")
    private List<String> searchAgentAccounts;
}
