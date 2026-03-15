package com.cloud.baowang.agent.controller.wallet;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.AgentRechargeApi;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.vo.CurrencyCodeReqVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentTradeRecordDetailRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentDepositOrderDetailVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentDepositOrderFileVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeConfigRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeConfigVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentOrderNoVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeRecordDetailResponseVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeReqVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeWayReqVO;
import com.cloud.baowang.agent.api.vo.recharge.ClientAgentRechargeRecordRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.ClientAgentRechargeRecordResponseVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.AgentCurrencyRechargeWayVO;
import com.cloud.baowang.wallet.api.vo.recharge.CheckUserStatusVO;
import com.cloud.baowang.wallet.api.vo.recharge.NotRemindRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeWayListVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeWayRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayDetailRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "代理端-财务中心-额度充值")
@RestController
@AllArgsConstructor
@RequestMapping("agentRecharge/api")
public class AgentRechargeController {

    private AgentInfoApi agentInfoApi;

    private SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final SystemRechargeWayApi rechargeWayApi;

    private final AgentRechargeApi agentRechargeApi;

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final SystemRechargeWayApi systemRechargeWayApi;



    @Operation(summary = "获取币种列表")
    @PostMapping("rechargeCurrencyList")
    public ResponseVO<List<AgentCurrencyRechargeWayVO>> rechargeCurrencyList(){
        String siteCode = CurrReqUtils.getSiteCode();
        List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS = siteCurrencyInfoApi.getValidBySiteCode(siteCode);
        List<AgentCurrencyRechargeWayVO> agentCurrencyRechargeWayVOS = new ArrayList<>();
        for (SiteCurrencyInfoRespVO currencyInfoRespVO:siteCurrencyInfoRespVOS){
            if(CommonConstant.business_one.equals(currencyInfoRespVO.getStatus())) {
                AgentCurrencyRechargeWayVO agentCurrencyRechargeWayVO = ConvertUtil.entityToModel(currencyInfoRespVO, AgentCurrencyRechargeWayVO.class);
                agentCurrencyRechargeWayVOS.add(agentCurrencyRechargeWayVO);
            }
        }
        return ResponseVO.success(agentCurrencyRechargeWayVOS);
    }


    @Operation(summary = "获取充值方式列表")
    @PostMapping("rechargeWayList")
    public ResponseVO<List<RechargeWayListVO>> agentRechargeWayList(@RequestBody CurrencyCodeReqVO currencyCodeReqVO){
        String siteCode = CurrReqUtils.getSiteCode();
        List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS = siteCurrencyInfoApi.getValidBySiteCode(siteCode);
        boolean contains = siteCurrencyInfoRespVOS.stream()
                .anyMatch(obj -> obj.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode()));
        if(!contains){
            return ResponseVO.success(new ArrayList<> ());
        }
        RechargeWayRequestVO rechargeWayRequestVO  = new RechargeWayRequestVO();
        rechargeWayRequestVO.setSiteCode(siteCode);
        List<RechargeWayListVO> rechargeWayList = rechargeWayApi.agentRechargeWayList(rechargeWayRequestVO);
        Map<String,List<RechargeWayListVO>> rechargeWayGroup = rechargeWayList.stream()
                .collect(Collectors.groupingBy(RechargeWayListVO::getCurrencyCode));
        return ResponseVO.success(rechargeWayGroup.get(currencyCodeReqVO.getCurrencyCode()));
    }


    @Operation(summary = "代理充值")
    @PostMapping("agentRecharge")
    public ResponseVO<AgentOrderNoVO> userRecharge( @RequestBody AgentRechargeReqVO agentRechargeReqVO) {

        agentRechargeReqVO.setAgentId(CurrReqUtils.getOneId());
        String ip = CurrReqUtils.getReqIp();
        agentRechargeReqVO.setApplyIp(ip);
        Integer deviceType = CurrReqUtils.getReqDeviceType();
        agentRechargeReqVO.setDeviceType(String.valueOf(deviceType));
        agentRechargeReqVO.setApplyDomain(CurrReqUtils.getReferer());
        agentRechargeReqVO.setAgentAccount(CurrReqUtils.getAccount());
        agentRechargeReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        agentRechargeReqVO.setDeviceNo(CurrReqUtils.getReqDeviceId());

        //创建存款记录
        ResponseVO<AgentOrderNoVO> responseVO = agentRechargeApi.agentRecharge(agentRechargeReqVO);

        return responseVO;
    }

    @Operation(summary = "获取充值订单详情")
    @PostMapping("depositOrderDetail")
    public ResponseVO<AgentDepositOrderDetailVO> depositOrderDetail(@RequestBody AgentOrderNoVO orderNoVO) {
        return agentRechargeApi.depositOrderDetail(orderNoVO);
    }

    @Operation(summary = "上传凭证")
    @PostMapping("uploadVoucher")
    public ResponseVO<Integer> uploadVoucher(@Validated @RequestBody AgentDepositOrderFileVO depositOrderFileVO) {
        if(StringUtils.isBlank(depositOrderFileVO.getCashFlowFile())){
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return agentRechargeApi.uploadVoucher(depositOrderFileVO);
    }

    @Operation(summary = "撤销充值订单")
    @PostMapping("agentCancelDepositOrder")
    public ResponseVO<Integer> agentCancelDepositOrder( @RequestBody AgentOrderNoVO orderNoVO) {
        if(StringUtils.isBlank(orderNoVO.getOrderNo())){
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return agentRechargeApi.cancelDepositOrder(orderNoVO);
    }

    @Operation(summary = "获取代理充值配置信息")
    @PostMapping("getRechargeConfig")
    public ResponseVO<AgentRechargeConfigVO> getRechargeConfig(@RequestBody AgentRechargeConfigRequestVO rechargeConfigRequestVO){
        rechargeConfigRequestVO.setAgentId(CurrReqUtils.getOneId());
        rechargeConfigRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<AgentRechargeConfigVO> rechargeConfigVO = agentRechargeApi.getRechargeConfig(rechargeConfigRequestVO);
        return rechargeConfigVO;
    }


    @Operation(summary = "催单")
    @PostMapping("urgeOrder")
    public ResponseVO urgeOrder(@RequestBody AgentOrderNoVO vo) {
        agentRechargeApi.urgeOrder(vo);
        return ResponseVO.success();
    }


    @Operation(summary = "代理端存款记录")
    @PostMapping("clientAgentRechargeRecorder")
    public ResponseVO<Page<ClientAgentRechargeRecordResponseVO>> clientAgentRechargeRecorder(@RequestBody ClientAgentRechargeRecordRequestVO vo) {
        vo.setAgentId(CurrReqUtils.getOneId());

        return ResponseVO.success(agentRechargeApi.clientAgentRechargeRecorder(vo));
    }

    @Operation(summary = "代理端存款记录-订单详情")
    @PostMapping("clientAgentRechargeRecordDetail")
    public ResponseVO<AgentRechargeRecordDetailResponseVO> clientAgentRechargeRecordDetail(@RequestBody AgentTradeRecordDetailRequestVO vo) {

        return ResponseVO.success(agentRechargeApi.clientAgentRechargeRecordDetail(vo));
    }

    @Operation(summary = "获取充值汇率")
    @PostMapping("getRechargeExchange")
    public ResponseVO<BigDecimal> getRechargeExchange(@RequestBody AgentRechargeWayReqVO rechargeWayReqVO){
        IdReqVO idReqVO =  new IdReqVO();
        idReqVO.setId(rechargeWayReqVO.getDepositWayId());
        SystemRechargeWayDetailRespVO systemRechargeWayDetailRespVO = systemRechargeWayApi.getInfoById(idReqVO).getData();
        //获取汇率
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        exchangeRateRequestVO.setCurrencyCode(systemRechargeWayDetailRespVO.getCurrencyCode());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.RECHARGE.getCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        return ResponseVO.success(exchangeRate);
    }

    @Operation(summary = "不再提醒")
    @PostMapping("notRemind")
    public ResponseVO notRemind(@RequestBody NotRemindRequestVO notRemindRequestVO) {
        String remindKey = "recharge::noRemind::" + CurrReqUtils.getOneId()+"::"+notRemindRequestVO.getNetWorkType()+"::"+notRemindRequestVO.getCurrencyCode();
        RedisUtil.setValue(remindKey, remindKey, 3600*24L, TimeUnit.SECONDS);
        return ResponseVO.success();
    }

    @Operation(summary = "校验代理充值提款状态")
    @PostMapping("checkAgentRechargeStatus")
    public ResponseVO checkAgentRechargeStatus(@RequestBody CheckUserStatusVO checkUserStatusVO) {

        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentId(CurrReqUtils.getOneId());
        //校验代理账号状态
        if(agentInfoVO.getStatus().contains(AgentStatusEnum.DEPOSIT_WITHDRAWAL_LOCK.getCode())){
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }
        return ResponseVO.success();

    }

}
