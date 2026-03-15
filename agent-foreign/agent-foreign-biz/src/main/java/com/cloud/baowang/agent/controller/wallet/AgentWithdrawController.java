package com.cloud.baowang.agent.controller.wallet;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.AgentWithdrawApi;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.vo.CurrencyCodeReqVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentTradeRecordDetailRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentOrderNoVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithDrawApplyVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawConfigRequestVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawConfigResponseVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawExchangeRateVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawOrderDetailVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawRecordDetailResponseVO;
import com.cloud.baowang.agent.api.vo.withdraw.ClientAgentWithdrawRecordRequestVO;
import com.cloud.baowang.agent.api.vo.withdraw.ClientAgentWithdrawRecordResponseVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawWayApi;
import com.cloud.baowang.wallet.api.vo.recharge.AgentCurrencyWithdrawWayVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayListVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "代理端-财务中心-佣金取款")
@RestController
@AllArgsConstructor
@RequestMapping("/agentWithdraw/api")
public class AgentWithdrawController {

    private final SystemParamApi systemParamApi;
    private final SystemWithdrawWayApi withdrawWayApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final AgentWithdrawApi agentWithdrawApi;

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final AgentInfoApi agentInfoApi;


    @Operation(summary = "获取提款币种列表")
    @PostMapping("withdrawCurrencyList")
    public ResponseVO<List<AgentCurrencyWithdrawWayVO>> withdrawCurrencyList() {

        String siteCode = CurrReqUtils.getSiteCode();
        List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS = siteCurrencyInfoApi.getValidBySiteCode(siteCode);
        WithdrawWayRequestVO withdrawWayRequestVO = new WithdrawWayRequestVO();
        withdrawWayRequestVO.setSiteCode(siteCode);
        List<AgentCurrencyWithdrawWayVO> agentCurrencyWithdrawWayVOS = new ArrayList<>();
        for (SiteCurrencyInfoRespVO currencyInfoRespVO : siteCurrencyInfoRespVOS) {
            if(CommonConstant.business_one.equals(currencyInfoRespVO.getStatus())){
                AgentCurrencyWithdrawWayVO agentCurrencyWithdrawWayVO = ConvertUtil.entityToModel(currencyInfoRespVO, AgentCurrencyWithdrawWayVO.class);
                agentCurrencyWithdrawWayVOS.add(agentCurrencyWithdrawWayVO);
            }
        }
        return ResponseVO.success(agentCurrencyWithdrawWayVOS);

    }

    @Operation(summary = "获取提款方式列表")
    @PostMapping("withdrawWayList")
    public ResponseVO<List<WithdrawWayListVO>> withdrawWayList(@RequestBody CurrencyCodeReqVO currencyCodeReqVO) {

        String siteCode = CurrReqUtils.getSiteCode();
        List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS = siteCurrencyInfoApi.getValidBySiteCode(siteCode);
        boolean contains = siteCurrencyInfoRespVOS.stream()
                .anyMatch(obj -> obj.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode()));
        if(!contains){
            return ResponseVO.success(new ArrayList<> ());
        }

        WithdrawWayRequestVO withdrawWayRequestVO = new WithdrawWayRequestVO();
        withdrawWayRequestVO.setSiteCode(siteCode);
        List<WithdrawWayListVO> withdrawWayListVOS = withdrawWayApi.agentWithdrawWayList(withdrawWayRequestVO);
        Map<String, List<WithdrawWayListVO>> rechargeWayGroup = withdrawWayListVOS.stream()
                .collect(Collectors.groupingBy(WithdrawWayListVO::getCurrencyCode));
        return ResponseVO.success(rechargeWayGroup.get(currencyCodeReqVO.getCurrencyCode()));

    }


    @Operation(summary = "获取提款配置")
    @PostMapping("getWithdrawConfig")
    public ResponseVO<AgentWithdrawConfigResponseVO> getWithdrawConfig(@RequestBody AgentWithdrawConfigRequestVO withdrawConfigRequestVO) {

        withdrawConfigRequestVO.setAgentId(CurrReqUtils.getOneId());
        ResponseVO<AgentWithdrawConfigResponseVO> responseVO = agentWithdrawApi.getAgentWithdrawConfig(withdrawConfigRequestVO);

        return responseVO;
    }

   /* @Operation(summary = "获取取款订单详情")
    @PostMapping("withdrawOrderDetail")
    public ResponseVO<AgentWithdrawOrderDetailVO> withdrawOrderDetail(@RequestBody AgentOrderNoVO orderNoVO) {
        return agentWithdrawApi.withdrawOrderDetail(orderNoVO);
    }*/

    @Operation(summary = "代理提款申请")
    @PostMapping("withdrawApply")
    public ResponseVO<Integer> withdrawApply(@RequestBody AgentWithDrawApplyVO vo) {
        vo.setAgentId(CurrReqUtils.getOneId());
        String ip = CurrReqUtils.getReqIp();
        vo.setApplyIp(ip);
        Integer deviceType = CurrReqUtils.getReqDeviceType();
        vo.setDeviceType(String.valueOf(deviceType));
        vo.setApplyDomain(CurrReqUtils.getReferer());
        vo.setDeviceNo(CurrReqUtils.getReqDeviceId());
        return agentWithdrawApi.agentWithdrawApply(vo);

    }

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS);
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {
            Map<String, List<CodeValueVO>> data = resp.getData();
            List<CodeValueVO> currencyDownBox = siteCurrencyInfoApi.getCurrencyDownBox(CurrReqUtils.getSiteCode());
            data.put("currency_code", currencyDownBox);
            resp.setData(data);
        }
        return resp;
    }

    @Operation(summary = "代理端取款记录")
    @PostMapping("clientAgentWithdrawRecorder")
    public ResponseVO<Page<ClientAgentWithdrawRecordResponseVO>> clientAgentWithdrawRecorder(@RequestBody ClientAgentWithdrawRecordRequestVO vo) {
        vo.setAgentId(CurrReqUtils.getOneId());
        return ResponseVO.success(agentWithdrawApi.clientAgentWithdrawRecorder(vo));
    }

    @Operation(summary = "代理端取款款记录-订单详情")
    @PostMapping("clientAgentWithdrawRecordDetail")
    public ResponseVO<AgentWithdrawRecordDetailResponseVO> clientAgentWithdrawRecordDetail(@RequestBody AgentTradeRecordDetailRequestVO vo) {

        return ResponseVO.success(agentWithdrawApi.clientAgentWithdrawRecordDetail(vo));
    }


    @Operation(summary = "获取代理提款汇率")
    @PostMapping("getAgentWithdrawExchange")
    public ResponseVO<AgentWithdrawExchangeRateVO> getAgentWithdrawExchange(@RequestBody CurrencyCodeReqVO currencyCodeReqVO){

        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        exchangeRateRequestVO.setCurrencyCode(currencyCodeReqVO.getCurrencyCode());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.WITHDRAW.getCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);

        BigDecimal platformExchangeRate =  siteCurrencyInfoApi.getCurrencyFinalRate(CurrReqUtils.getSiteCode(), currencyCodeReqVO.getCurrencyCode());
        AgentWithdrawExchangeRateVO agentWithdrawExchangeRateVO = new AgentWithdrawExchangeRateVO();
        agentWithdrawExchangeRateVO.setExchangeRate(exchangeRate);
        agentWithdrawExchangeRateVO.setPlatformExchangeRate(platformExchangeRate);

        return ResponseVO.success(agentWithdrawExchangeRateVO);
    }
}
