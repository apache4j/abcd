package com.cloud.baowang.site.controller.merchant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentMerchantApi;
import com.cloud.baowang.agent.api.api.AgentMerchantModifyReviewApi;
import com.cloud.baowang.agent.api.api.AgentMerchantReviewRecordApi;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.enums.MerchantModifyTypeEnums;
import com.cloud.baowang.agent.api.vo.merchant.AddMerchantPageQueryVO;
import com.cloud.baowang.agent.api.vo.merchant.AddMerchantVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantPageRespVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantModifyVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author: ford
 * @Description: 代理相关
 */
@Tag(name = "商务管理相关")
@RestController
@RequestMapping("/agentMerchant/api")
@AllArgsConstructor
public class AgentMerchantController {

    private final AgentMerchantApi merchantApi;
    private final AgentMerchantReviewRecordApi merchantReviewApi;
    private final AgentMerchantModifyReviewApi modifyReviewApi;
    private final SystemParamApi paramApi;
    private final RiskApi riskApi;

    @Operation(summary = "新增商务")
    @PostMapping(value = "/addMerchant")
    public ResponseVO<Boolean> addMerchant(@RequestBody @Validated AddMerchantVO vo) {
        vo.setApplication(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return merchantReviewApi.addMerchant(vo);
    }

    @Operation(summary = "商务列表")
    @PostMapping("pageQuery")
    public ResponseVO<Page<AgentMerchantPageRespVO>> pageQuery(@RequestBody AddMerchantPageQueryVO queryVO) {
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return merchantApi.pageQuery(queryVO);
    }

    @GetMapping("getDownBox")
    @Operation(summary = "账号状态下拉框")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        ResponseVO<List<CodeValueVO>> resp = paramApi.getSystemParamByType(CommonConstant.AGENT_STATUS);
        if (resp.isOk()) {
            List<CodeValueVO> data = resp.getData();
            String normalCode = AgentStatusEnum.NORMAL.getCode();
            String loginLockCode = AgentStatusEnum.LOGIN_LOCK.getCode();
            data = data.stream()
                    .filter(item -> normalCode.equals(item.getCode()) || loginLockCode.equals(item.getCode()))
                    .toList();
            resp.setData(data);
        }
        return resp;
    }

    @GetMapping("getRiskDownBox")
    @Operation(summary = "风控下拉框")
    public ResponseVO<List<RiskLevelResVO>> getRiskDownBox() {
        RiskLevelDownReqVO reqVO = new RiskLevelDownReqVO();
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setRiskControlType(RiskTypeEnum.RISK_BUSINESS.getCode());
        return riskApi.getRiskLevelList(reqVO);
    }

    @PostMapping("initInfoModify")
    @Operation(summary = "发起信息变更")
    public ResponseVO<Boolean> initInfoModify(@RequestBody MerchantModifyVO modifyVO) {
        modifyVO.setSiteCode(CurrReqUtils.getSiteCode());
        modifyVO.setOperator(CurrReqUtils.getAccount());
        modifyVO.setReviewApplicationType(MerchantModifyTypeEnums.MERCHANT_ACCOUNT_STATUS.getType());
        return modifyReviewApi.initInfoModify(modifyVO);
    }
}
