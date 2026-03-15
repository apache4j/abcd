package com.cloud.baowang.site.controller.agent;

import com.cloud.baowang.agent.api.api.AgentAddApi;
import com.cloud.baowang.agent.api.api.AgentInfoModifyReviewApi;
import com.cloud.baowang.agent.api.api.AgentMerchantApi;
import com.cloud.baowang.agent.api.vo.agentreview.AddGeneralAgentVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: ford
 * @Description: 代理相关
 */
@Tag(name = "新增代理")
@RestController
@RequestMapping("/agent-add/api")
@AllArgsConstructor
public class AgentAddController {

    private final AgentAddApi agentAddApi;

    private final AgentInfoModifyReviewApi agentInfoModifyReviewApi;

    private final AgentMerchantApi agentMerchantApi;


    @Operation(description = "新增总代")
    @PostMapping(value = "/addGeneralAgent")
    public ResponseVO addGeneralAgent(@RequestBody @Validated  AddGeneralAgentVO vo ) {
        vo.setAdminId(CurrReqUtils.getOneId());
        vo.setAdminName(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        //vo.setDeviceId(CurrReqUtils.getReqDeviceId());
        return agentAddApi.addGeneralAgent(vo);
    }


    @Operation(summary = "查询下拉框")
    @PostMapping("/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        String siteCode = CurrReqUtils.getSiteCode();
        return agentInfoModifyReviewApi.getDownBox(siteCode);
    }

    @Operation(summary = "商务信息下拉列表")
    @PostMapping("/getAgentMerchantList")
    public ResponseVO< List<AgentMerchantVO>> getAgentMerchantList() {
        String siteCode = CurrReqUtils.getSiteCode();
       return ResponseVO.success(agentMerchantApi.getList(siteCode));
    }

   /* @Operation(description ="下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {

        // 代理类型
        List<SystemParamVO> agentType = AgentTypeEnum.getList()
                .stream()
                .map(item ->
                        SystemParamVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();

        // 代理归属 1推广 2招商 3官资
        List<SystemParamVO> agentAttribution = AgentAttributionEnum.getList()
                .stream()
                .map(item ->
                        SystemParamVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList();
        // 代理类别 1常规代理 2流量代理
        List<SystemParamVO> agentCategory = AgentCategoryEnum.getList()
                .stream()
                .map(item ->
                        SystemParamVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList();

        //会员福利
        List<SystemParamVO> agentUserBenefit = AgentUserBenefitEnum.getList()
                .stream()
                .map(item ->
                        SystemParamVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList();
        Map<String, Object> result = Maps.newHashMap();
        result.put("agentType", agentType);
        result.put("agentAttribution", agentAttribution);
        result.put("agentCategory", agentCategory);
        result.put("agentUserBenefit", agentUserBenefit);

        return ResponseVO.success(result);
    }*/

}
