package com.cloud.baowang.site.controller.agentManualUpDown;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.enums.AgentManualAdjustTypeEnum;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewListVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpReviewPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpReviewResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentUpReviewDetailsVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: kimi
 */
@Tag(name = "代理人工加额审核")
@RestController
@RequestMapping("/agent-manual-up-review/api")
@RequiredArgsConstructor
public class AgentManualUpReviewController {

    private final HttpServletRequest request;

    private final SystemParamApi systemParamApi;

    private final AgentManualUpApi agentManualUpApi;


    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> arr = new ArrayList<>();
        arr.add(CommonConstant.USER_REVIEW_LOCK_STATUS);
        arr.add(CommonConstant.USER_REVIEW_REVIEW_OPERATION);
        arr.add(CommonConstant.AGENT_MANUAL_ADJUST_TYPE);
        //代理钱包类型
        arr.add(CommonConstant.AGENT_WALLET_TYPE);
        return systemParamApi.getSystemParamsByList(arr);
    }

    @GetMapping("getAdjustTypeListByWalletType")
    @Operation(summary = "根据选择的钱包类型，获取加额类型下拉")
    public ResponseVO<List<CodeValueVO>> getAdjustTypeListByWalletType(@RequestParam("walletType") Integer walletType) {
        //加额类型
        ResponseVO<List<CodeValueVO>> resp = systemParamApi.getSystemParamByType(CommonConstant.AGENT_MANUAL_ADJUST_TYPE);
        if (resp.isOk()) {
            List<CodeValueVO> list = resp.getData();
            List<AgentManualAdjustTypeEnum> enums = AgentManualAdjustTypeEnum.listByWalletType(walletType);
            List<String> enumCodes = enums.stream()
                    .map(AgentManualAdjustTypeEnum::getCode) // 假设 getCode() 返回 code
                    .toList();
            return ResponseVO.success(list.stream()
                    .filter(codeValue -> enumCodes.contains(codeValue.getCode())) // 假设 getCode() 返回 code
                    .toList());
        }
        return ResponseVO.success();
    }

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = "/lockManualUp")
    public ResponseVO<?> lockManualUp(@Valid @RequestBody StatusListVO vo) {
        return agentManualUpApi.lockManualUp(vo, CurrReqUtils.getAccount());
    }

    @Operation(summary = "一审通过-提交")
    @PostMapping(value = "/oneReviewSuccessManualUp")
    public ResponseVO<?> oneReviewSuccessManualUp(@Valid @RequestBody ReviewListVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentManualUpApi.oneReviewSuccessManualUp(vo, CurrReqUtils.getAccount());
    }

    @Operation(summary = "一审拒绝-提交")
    @PostMapping(value = "/oneReviewFailManualUp")
    public ResponseVO<?> oneReviewFailManualUp(@Valid @RequestBody ReviewListVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentManualUpApi.oneReviewFailManualUp(vo, CurrReqUtils.getAccount());
    }

    @Operation(summary = "审核列表")
    @PostMapping(value = "/getUpReviewPageManualUp")
    public ResponseVO<Page<AgentManualUpReviewResponseVO>> getUpReviewPageManualUp(@Valid @RequestBody AgentManualUpReviewPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(agentManualUpApi.getUpReviewPageManualUp(vo, CurrReqUtils.getAccount()));
    }

    @Operation(summary = "审核详情")
    @PostMapping(value = "/getUpReviewDetailsManualUp")
    public ResponseVO<AgentUpReviewDetailsVO> getUpReviewDetailsManualUp(@Valid @RequestBody IdVO vo) {
        return agentManualUpApi.getUpReviewDetailsManualUp(vo);
    }


}
