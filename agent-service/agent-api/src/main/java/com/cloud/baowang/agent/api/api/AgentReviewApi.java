package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.StatusVO;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewDetailsVO;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewResponseVO;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewVO;
import com.cloud.baowang.agent.api.vo.agentreview.UserAccountUpdateVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @Desciption: 代理审核相关
 * @Author: Ford
 * @Date: 2024/5/30 11:19
 * @Version: V1.0
 **/
@FeignClient(contextId = "remoteAgentReviewApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 新增代理商审核 AgentReview")
public interface AgentReviewApi {

    String PREFIX = ApiConstants.PREFIX + "/agent-review/api";

    @PostMapping(value = PREFIX+"/lock")
    @Operation(description = "锁单或解锁")
    ResponseVO lock(@RequestBody StatusVO vo, @RequestParam("adminName") String adminName) ;


    @Operation(description = "一审通过-提交")
    @PostMapping(value = PREFIX+"/reviewSuccess")
    ResponseVO reviewSuccess(@RequestBody ReviewVO vo,
                                    @RequestParam("registerIp") String registerIp,
                                    @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName) ;


    @Schema(description ="一审拒绝-提交")
    @PostMapping(value = PREFIX+"/reviewFail")
    ResponseVO reviewFail(@RequestBody ReviewVO vo,
                          @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Schema(description ="审核列表")
    @PostMapping(value = PREFIX+"/getReviewPage")
    ResponseVO<Page<AgentReviewResponseVO>> getReviewPage(@RequestBody AgentReviewPageVO vo, @RequestParam("adminName") String adminName) ;

    @Schema(description ="审核详情")
    @PostMapping(value = PREFIX+"/getReviewDetails")
    ResponseVO<AgentReviewDetailsVO> getReviewDetails(@RequestBody IdVO vo) ;

    @Schema(description ="查询代理页签下的未审核数量角标")
    @PostMapping(value = PREFIX+"/getNotReviewNum")
    ResponseVO<List<UserAccountUpdateVO>> getNotReviewNum(@RequestParam("siteCode")  String siteCode) ;

    @Schema(description ="查询代理页签下的未审核数量角标 Map")
    @PostMapping(value = PREFIX+"/getNotReviewNumMap")
    ResponseVO<Map<String, Long>> getNotReviewNumMap(@RequestParam("siteCode")  String siteCode) ;

}
