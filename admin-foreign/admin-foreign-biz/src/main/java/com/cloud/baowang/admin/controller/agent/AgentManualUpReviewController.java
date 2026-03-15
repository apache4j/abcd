/*
package com.cloud.baowang.admin.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.enums.AgentAdjustTypeEnum;
import com.cloud.baowang.agent.api.vo.StatusVO;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpReviewPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpReviewResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentUpReviewDetailsVO;
import com.cloud.baowang.admin.utils.CommonAdminUtils;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.system.api.constant.AdminPermissionApiConstant;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.funding.UserManualUpReviewNumberEnum;
import com.cloud.baowang.common.core.utils.IPUtil;
import com.cloud.baowang.common.core.vo.SystemParamVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

*/
/**
 * @author: kimi
 *//*

@Tag(name = "代理人工加额审核")
@RestController
@RequestMapping("/agent-manual-up-review/api")
public class AgentManualUpReviewController {

    private final HttpServletRequest request;

    private final SystemParamApi systemParamApi;

    private final AgentManualUpApi agentManualUpApi;


    @Autowired
    public AgentManualUpReviewController(HttpServletRequest request,
                                         SystemParamApi systemParamApi,
                                         AgentManualUpApi agentManualUpApi) {
        this.request = request;
        this.systemParamApi = systemParamApi;
        this.agentManualUpApi = agentManualUpApi;
    }

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        boolean waitOneReview = false;
        boolean waitTwoReview = false;
        List<String> adminApis = CommonAdminUtils.getAdminApis();
        if (null == adminApis) {
            waitOneReview = true;
            waitTwoReview = true;
        } else {
            if (adminApis.contains(AdminPermissionApiConstant.AGENT_MANUAL_WAIT_ONE_REVIEW)) {
                waitOneReview = true;
            }
            if (adminApis.contains(AdminPermissionApiConstant.AGENT_MANUAL_WAIT_TWO_REVIEW)) {
                waitTwoReview = true;
            }
        }

        // 锁单状态-下拉框
        ResponseVO<List<CodeValueVO>> lockStatusResponseVO = systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_LOCK_STATUS);
        // 待一审、待二审
        List<SystemParamVO> reviewList = new ArrayList<>(UserManualUpReviewNumberEnum.getList()
                .stream()
                .map(item ->
                        SystemParamVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList());
        if (!waitOneReview) {
            reviewList.remove(new SystemParamVO(
                    null,
                    UserManualUpReviewNumberEnum.WAIT_ONE_REVIEW.getCode().toString(),
                    UserManualUpReviewNumberEnum.WAIT_ONE_REVIEW.getName()));
        }
        if (!waitTwoReview) {
            reviewList.remove(new SystemParamVO(
                    null,
                    UserManualUpReviewNumberEnum.WAIT_TWO_REVIEW.getCode().toString(),
                    UserManualUpReviewNumberEnum.WAIT_TWO_REVIEW.getName()));
        }

        List<CodeValueVO> lockStatus = Lists.newArrayList();
        if (lockStatusResponseVO.getCode() == ResultCode.SUCCESS.getCode()) {
            lockStatus = lockStatusResponseVO.getData();
        }

        // 调整类型
        List<SystemParamVO> adjustType = AgentAdjustTypeEnum.getList().stream().map(item ->
                SystemParamVO.builder().code(item.getCode()).value(item.getName()).build()).toList();

        Map<String, Object> result = Maps.newHashMap();
        result.put("lockStatus", lockStatus);
        result.put("reviewList", reviewList);
        result.put("adjustType", adjustType);

        return ResponseVO.success(result);
    }

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = "/lockManualUp")
    public ResponseVO<?> lockManualUp(@Valid @RequestBody StatusVO vo) {
        return agentManualUpApi.lockManualUp(vo,CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "一审通过-提交")
    @PostMapping(value = "/oneReviewSuccessManualUp")
    public ResponseVO<?> oneReviewSuccessManualUp(@Valid @RequestBody ReviewVO vo) {
        return agentManualUpApi.oneReviewSuccessManualUp(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "一审拒绝-提交")
    @PostMapping(value = "/oneReviewFailManualUp")
    public ResponseVO<?> oneReviewFailManualUp(@Valid @RequestBody ReviewVO vo) {
        return agentManualUpApi.oneReviewFailManualUp(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "审核列表")
    @PostMapping(value = "/getUpReviewPageManualUp")
    public ResponseVO<Page<AgentManualUpReviewResponseVO>> getUpReviewPageManualUp(@Valid @RequestBody AgentManualUpReviewPageVO vo) {
        return ResponseVO.success(agentManualUpApi.getUpReviewPageManualUp(vo, CurrentRequestUtils.getCurrentUserAccount()));
    }

    @Operation(summary = "审核详情")
    @PostMapping(value = "/getUpReviewDetailsManualUp")
    public ResponseVO<AgentUpReviewDetailsVO> getUpReviewDetailsManualUp(@Valid @RequestBody IdVO vo) {
        return agentManualUpApi.getUpReviewDetailsManualUp(vo);
    }

    @Operation(summary = "二审-锁单或解锁")
    @PostMapping(value = "/twoLockManualUp")
    public ResponseVO<?> twoLockManualUp(@Valid @RequestBody StatusVO vo) {
        return agentManualUpApi.twoLockManualUp(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "二审通过-提交")
    @PostMapping(value = "/twoReviewSuccessManualUp")
    public ResponseVO<?> twoReviewSuccessManualUp(@Valid @RequestBody ReviewVO vo) {
        return agentManualUpApi.twoReviewSuccessManualUp(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "二审拒绝-提交")
    @PostMapping(value = "/twoReviewFailManualUp")
    public ResponseVO<?> twoReviewFailManualUp(@Valid @RequestBody ReviewVO vo) {
        return agentManualUpApi.twoReviewFailManualUp(vo, CurrentRequestUtils.getCurrentUserAccount());
    }
}
*/
