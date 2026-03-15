/*
package com.cloud.baowang.admin.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentReviewApi;
import com.cloud.baowang.agent.api.vo.StatusVO;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewDetailsVO;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewResponseVO;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewVO;
import com.cloud.baowang.agent.api.vo.agentreview.UserAccountUpdateVO;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.IPUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

*/
/**
 * @author: kimi
 *//*

@Tag(name =  "新增代理审核")
@RestController
@RequestMapping("/agent-review/api")
public class AgentReviewController {

    @Autowired
    private  AgentReviewApi agentReviewApi;

    @Autowired
    private SystemParamApi systemParamApi;

    @Operation(description ="下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 审核状态-下拉框
        ResponseVO<List<CodeValueVO>> reviewStatusResponseVO =
                systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_REVIEW_STATUS);
        // 锁单状态-下拉框
        ResponseVO<List<CodeValueVO>> lockStatusResponseVO =
                systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_LOCK_STATUS);
        // 审核操作-下拉框
        ResponseVO<List<CodeValueVO>> reviewOperationResponseVO =
                systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_REVIEW_OPERATION);

        List<CodeValueVO> reviewStatus = Lists.newArrayList();
        if (reviewStatusResponseVO.getCode() == ResultCode.SUCCESS.getCode()) {
            reviewStatus = reviewStatusResponseVO.getData();
        }

        List<CodeValueVO> lockStatus = Lists.newArrayList();
        if (lockStatusResponseVO.getCode() == ResultCode.SUCCESS.getCode()) {
            lockStatus = lockStatusResponseVO.getData();
        }

        List<CodeValueVO> reviewOperation = Lists.newArrayList();
        if (reviewOperationResponseVO.getCode() == ResultCode.SUCCESS.getCode()) {
            reviewOperation = reviewOperationResponseVO.getData();
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("reviewStatus", reviewStatus);
        result.put("lockStatus", lockStatus);
        result.put("reviewOperation", reviewOperation);
        return ResponseVO.success(result);
    }


    @Operation(description = "锁单或解锁")
    @PostMapping(value = "/lock")
    public ResponseVO lock(@RequestBody StatusVO vo) {
        return agentReviewApi.lock(vo, CurrentRequestUtils.getCurrentUserAccount(), CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(description = "一审通过-提交")
    @PostMapping(value = "/reviewSuccess")
    public ResponseVO reviewSuccess(@RequestBody ReviewVO vo, HttpServletRequest request) {
        String registerIp = IPUtil.getIp(request);
        return ResponseVO.success(
                agentReviewApi.reviewSuccess(vo, registerIp, CurrentRequestUtils.getCurrentUserAccount(), CurrentRequestUtils.getCurrentUserAccount()));
    }

    @Operation(description = "一审拒绝-提交")
    @PostMapping(value = "/reviewFail")
    public ResponseVO reviewFail(@RequestBody ReviewVO vo) {
        return agentReviewApi.reviewFail(vo, CurrentRequestUtils.getCurrentUserAccount(), CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(description = "审核列表")
    @PostMapping(value = "/getReviewPage")
    public ResponseVO<Page<AgentReviewResponseVO>> getReviewPage(@RequestBody AgentReviewPageVO vo) {
        return agentReviewApi.getReviewPage(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(description = "审核详情")
    @PostMapping(value = "/getReviewDetails")
    public ResponseVO<AgentReviewDetailsVO> getReviewDetails(@RequestBody IdVO vo) {
        return agentReviewApi.getReviewDetails(vo);
    }

    @Operation(description ="查询代理页签下的未审核数量角标")
    @PostMapping(value = "/getNotReviewNum")
    public ResponseVO<List<UserAccountUpdateVO>> getNotReviewNum() {
        return agentReviewApi.getNotReviewNum(null);
    }
}
*/
