package com.cloud.baowang.site.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoModifyReviewApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoChangeRecordPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoChangeRecordQueryVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyReviewDetailQueryVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyReviewDetailVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyReviewLockVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyReviewPageQueryVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyReviewPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyReviewVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: wade
 */
@Tag(name = "代理账号审核")
@RestController
@AllArgsConstructor
@RequestMapping("/agent-modify-review/api")
public class AgentInfoModifyReviewController {

    private final AgentInfoModifyReviewApi agentInfoModifyReviewApi;

    @Operation(summary = "锁单/解锁")
    @PostMapping("/lock")
    public ResponseVO<Void> lock(@Valid @RequestBody AgentInfoModifyReviewLockVO vo) {
        String operator = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setOperator(operator);
        vo.setSiteCode(siteCode);
        return agentInfoModifyReviewApi.lock(vo);
    }

    @Operation(summary = "代理信息编辑审核接口")
    @PostMapping("/review")
    public ResponseVO<Void> review(@Valid @RequestBody AgentInfoModifyReviewVO vo) {
        String currentUserAccount = CurrReqUtils.getAccount();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(currentUserAccount);
        return agentInfoModifyReviewApi.review(vo);
    }

    @Operation(summary = "代理信息编辑审核列表分页查询接口")
    @PostMapping("/pageList")
    public ResponseVO<Page<AgentInfoModifyReviewPageVO>> pageList(@RequestBody AgentInfoModifyReviewPageQueryVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        vo.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return agentInfoModifyReviewApi.pageList(vo);
    }

    @Operation(summary = "代理信息编辑审核详情接口")
    @PostMapping("/detail")
    public ResponseVO<AgentInfoModifyReviewDetailVO> detail(@Valid @RequestBody AgentInfoModifyReviewDetailQueryVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return agentInfoModifyReviewApi.detail(vo);
    }

    @Operation(summary = "代理信息变更记录")
    @PostMapping("/record/pageList")
    public ResponseVO<Page<AgentInfoChangeRecordPageVO>> recordPageList(@RequestBody AgentInfoChangeRecordQueryVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setDataDesensitization(vo.getDataDesensitization());
        return agentInfoModifyReviewApi.recordPageList(vo);
    }


    @Operation(summary = "查询下拉框")
    @PostMapping("/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        String siteCode = CurrReqUtils.getSiteCode();
        return agentInfoModifyReviewApi.getDownBox(siteCode);
    }


}
