package com.cloud.baowang.site.controller.merchant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentMerchantModifyReviewApi;
import com.cloud.baowang.agent.api.enums.MerchantModifyTypeEnums;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyReviewLockVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantModifyPageQueryVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantModifyReviewVO;
import com.cloud.baowang.agent.api.vo.merchant.AuditVO;
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


/**
 * @author: ford
 * @Description: 代理相关
 */
@Tag(name = "商务信息修改审核")
@RestController
@RequestMapping("/merchantModify/api")
@AllArgsConstructor
public class AgentMerchantModifyReviewController {
    private final AgentMerchantModifyReviewApi merchantApi;
    private final SystemParamApi paramApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.USER_REVIEW_REVIEW_STATUS);
        param.add(CommonConstant.USER_REVIEW_REVIEW_OPERATION);
        param.add(CommonConstant.USER_REVIEW_LOCK_STATUS);
        return paramApi.getSystemParamsByList(param);
    }

    @PostMapping("pageQuery")
    @Operation(summary = "分页查询")
    public ResponseVO<Page<AgentMerchantModifyReviewVO>> pageQuery(@RequestBody AgentMerchantModifyPageQueryVO queryVO) {
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        queryVO.setOperator(CurrReqUtils.getAccount());
        queryVO.setReviewApplicationType(MerchantModifyTypeEnums.MERCHANT_ACCOUNT_STATUS.getType());
        return merchantApi.pageQuery(queryVO);
    }

    @GetMapping("detail")
    @Operation(summary = "详情")
    public ResponseVO<AgentMerchantModifyReviewVO> detail(@RequestParam("id") String id) {
        String account = CurrReqUtils.getAccount();
        return merchantApi.detail(id,account);
    }

    @GetMapping("lock")
    @Operation(summary = "锁单")
    public ResponseVO<Boolean> lock(@RequestParam("id") String id) {
        return merchantApi.lock(id, CurrReqUtils.getAccount());
    }

    @GetMapping("unLock")
    @Operation(summary = "解锁")
    public ResponseVO<Boolean> unLock(@RequestParam("id") String id) {
        return merchantApi.unLock(id, CurrReqUtils.getAccount());
    }

    @PostMapping("approveReview")
    @Operation(summary = "审核通过")
    public ResponseVO<Boolean> approveReview(@RequestBody AuditVO auditVO) {
        String account = CurrReqUtils.getAccount();
        auditVO.setAccount(account);
        return merchantApi.approveReview(auditVO);
    }

    @PostMapping("rejectReview")
    @Operation(summary = "审核驳回")
    public ResponseVO<Boolean> rejectReview(@RequestBody AuditVO auditVO) {
        String account = CurrReqUtils.getAccount();
        auditVO.setAccount(account);
        return merchantApi.rejectReview(auditVO);
    }


}
