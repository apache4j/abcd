package com.cloud.baowang.site.controller.merchant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentMerchantReviewRecordApi;
import com.cloud.baowang.agent.api.vo.agentreview.UserAccountUpdateVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantReviewRecordVO;
import com.cloud.baowang.agent.api.vo.merchant.AuditVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantReviewRecordPageQueryVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "新增商务审核相关")
@RestController
@RequestMapping("/agentMerchantReviewRecord/api")
@AllArgsConstructor
public class AgentMerchantReviewRecordController {
    private final AgentMerchantReviewRecordApi reviewRecordApi;
    private final SystemParamApi paramApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> arr = new ArrayList<>();
        arr.add(CommonConstant.USER_REVIEW_REVIEW_OPERATION);
        arr.add(CommonConstant.USER_REVIEW_LOCK_STATUS);
        arr.add(CommonConstant.USER_REVIEW_REVIEW_STATUS);
        return paramApi.getSystemParamsByList(arr);
    }

    @PostMapping("pageQuery")
    @Operation(summary = "列表")
    public ResponseVO<Page<AgentMerchantReviewRecordVO>> pageQuery(@RequestBody MerchantReviewRecordPageQueryVO queryVO) {
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return reviewRecordApi.pageQuery(queryVO);
    }

    @GetMapping("detail")
    @Operation(summary = "审核详情")
    public ResponseVO<AgentMerchantReviewRecordVO> detail(@RequestParam("id") String id) {

        return reviewRecordApi.detail(id);
    }


    @GetMapping("lock")
    @Operation(summary = "锁单")
    public ResponseVO<Boolean> lock(@RequestParam("id") String id) {
        String account = CurrReqUtils.getAccount();
        return reviewRecordApi.lock(id, account);
    }

    @GetMapping("unLock")
    @Operation(summary = "解锁")
    public ResponseVO<Boolean> unLock(@RequestParam("id") String id) {
        String account = CurrReqUtils.getAccount();
        return reviewRecordApi.unLock(id, account);
    }

    @PostMapping("approveReview")
    @Operation(summary = "审核通过")
    public ResponseVO<Boolean> approveReview(@RequestBody AuditVO auditVO) {
        String account = CurrReqUtils.getAccount();
        auditVO.setAccount(account);
        return reviewRecordApi.approveReview(auditVO);
    }

    @PostMapping("rejectReview")
    @Operation(summary = "审核驳回")
    public ResponseVO<Boolean> rejectReview(@RequestBody AuditVO auditVO) {
        String account = CurrReqUtils.getAccount();
        auditVO.setAccount(account);
        return reviewRecordApi.rejectReview(auditVO);
    }


}
