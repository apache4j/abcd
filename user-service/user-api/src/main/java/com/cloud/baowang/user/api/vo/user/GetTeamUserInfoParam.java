package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @className: GetTeamUserInfo
 * @author: wade
 * @description: 根据会员账号数组查询基本信息数组
 * @date: 2024/5/31 22:26
 */
@Data
@Schema(description = "根据会员账号数组查询基本信息数组")
public class GetTeamUserInfoParam {

    private String siteCode;
    private String timeZone;
    private String agentId;
    /**
     * 迭代所有代理下级代理账号
     */
    private List<String> allDownAgentNum;



    @Schema( description = "开始时间")
    private Long startTime;

    @Schema( description = "结束时间")
    private Long endTime;


}
