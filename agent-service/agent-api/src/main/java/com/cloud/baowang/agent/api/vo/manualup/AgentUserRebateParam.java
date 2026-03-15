package com.cloud.baowang.agent.api.vo.manualup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 16/10/23 2:21 PM
 * @Version : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
 @Schema(title ="代理基本信息团队信息-会员人工返水传入参数")
public class AgentUserRebateParam implements Serializable {

    @Schema(title ="下级会员id集合")
    private List<String> userAgentId;

    @Schema(title ="加减code")
    private Integer justWay;

    @Schema(title ="加减额类型")
    private Integer manualType;

    @Schema(title ="开始时间")
    private Long startTime;

    @Schema(title ="结束时间")
    private Long endTime;

}
