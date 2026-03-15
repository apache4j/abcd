package com.cloud.baowang.agent.api.vo.agentCoin;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
import lombok.Data;

/**
 * @Desciption: 代存分页查询
 * @Author: Ford
 * @Date: 2024/11/11 16:54
 * @Version: V1.0
 **/
@Data
public class AgentDepositSubordinatesPageReqVo extends SitePageVO {

    //开始时间
    private Long startTime;
    //结束时间
    private Long endTime;

    //账号类型
    private Integer accountType;
}
