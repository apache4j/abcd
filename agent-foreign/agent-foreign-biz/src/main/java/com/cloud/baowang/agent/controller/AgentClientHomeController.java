package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.api.AgentClientHomeApi;
import com.cloud.baowang.agent.api.api.AgentCommissionApi;
import com.cloud.baowang.agent.api.vo.CurrencyCodeReqVO;
import com.cloud.baowang.agent.api.vo.commission.RateDetailVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.agent.api.vo.agent.clienthome.DataCompareGraphParam;
import com.cloud.baowang.agent.api.vo.agent.clienthome.DataCompareGraphVO;
import com.cloud.baowang.wallet.api.vo.agent.DepositRecordResponseVO;
import com.cloud.baowang.agent.api.vo.agent.clienthome.GetHomeAgentInfoResponseVO;
import com.cloud.baowang.play.api.vo.agent.GetNewest5OrderRecordParam;
import com.cloud.baowang.play.api.vo.agent.GetNewest5OrderRecordVO;
import com.cloud.baowang.wallet.api.vo.agent.LatestDepositParam;
import com.cloud.baowang.agent.api.vo.agent.clienthome.MonthClientAgentResponseVO;
import com.cloud.baowang.agent.api.vo.agent.clienthome.SaveQuickEntryParam;
import com.cloud.baowang.agent.api.vo.agent.clienthome.SelectQuickEntryParam;
import com.cloud.baowang.agent.api.vo.agent.clienthome.SelectQuickEntryResponse;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.user.api.api.notice.AgentNoticeApi;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.wallet.api.api.AgentClientHomeLatestDepositApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 代理PC和H5 首页统计
 */
@Tag(name = "代理PC和H5 首页统计")
@AllArgsConstructor
@RestController
@RequestMapping("/client-home/api")
public class AgentClientHomeController {

    private final AgentClientHomeApi agentClientHomeApi;

    private final AgentClientHomeLatestDepositApi agentClientHomeLatestDepositApi;

    private final OrderRecordApi orderRecordApi;

    private final AgentNoticeApi agentNoticeApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final AgentCommissionApi agentCommissionApi;

    @Operation(summary = "代理端-币种下拉框")
    @PostMapping(value = "/getCurrencyList")
    public ResponseVO<List<CodeValueVO>> getCurrencyDownBox() {
        return ResponseVO.success(siteCurrencyInfoApi.getCurrencyDownBox(CurrReqUtils.getSiteCode()));
    }


    @Operation(summary = "个人资料-代理信息")
    @PostMapping(value = "/getHomeAgentInfo")
    public ResponseVO<GetHomeAgentInfoResponseVO> getHomeAgentInfo(@RequestBody CurrencyCodeReqVO currencyCodeReqVO) {
        currencyCodeReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        currencyCodeReqVO.setAgentId(CurrReqUtils.getOneId());
        return agentClientHomeApi.getHomeAgentInfo(currencyCodeReqVO);
    }

    @Operation(summary = "本期统计")
    @PostMapping(value = "/monthStatistics")
    public ResponseVO<MonthClientAgentResponseVO> monthStatistics(@RequestBody  CurrencyCodeReqVO currencyCodeReqVO) {
        currencyCodeReqVO.setAgentId(CurrReqUtils.getOneId());
        currencyCodeReqVO.setCurrentAgent(CurrReqUtils.getAccount());
        currencyCodeReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentClientHomeApi.monthStatistics(currencyCodeReqVO);
    }

    @Operation(summary = "查询快捷入口")
    @PostMapping(value = "/selectQuickEntry")
    public ResponseVO<SelectQuickEntryResponse> selectQuickEntry(@Valid @RequestBody SelectQuickEntryParam vo) {
        vo.setCurrentId(CurrReqUtils.getOneId());
        vo.setCurrentAgent(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentClientHomeApi.selectQuickEntry(vo);
    }

    @Operation(summary = "编辑保存快捷入口")
    @PostMapping(value = "/saveQuickEntry")
    public ResponseVO<?> saveQuickEntry(@Valid @RequestBody SaveQuickEntryParam vo) {
        vo.setCurrentId(CurrReqUtils.getOneId());
        vo.setCurrentAgent(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentClientHomeApi.saveQuickEntry(vo);
    }

    @Operation(summary = "数据对比 曲线图")
    @PostMapping(value = "/dataCompareGraph")
    public ResponseVO<DataCompareGraphVO> dataCompareGraph(@Valid @RequestBody DataCompareGraphParam vo) {
        vo.setCurrentId(CurrReqUtils.getOneId());
        vo.setCurrentAgent(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTimeZoneId(CurrReqUtils.getTimezone());
        return agentClientHomeApi.dataCompareGraph(vo);
    }

    @Operation(summary = "最新存款")
    @PostMapping(value = "/latestDeposit")
    public ResponseVO<List<DepositRecordResponseVO>> latestDeposit(@RequestBody LatestDepositParam vo) {
        vo.setCurrentId(CurrReqUtils.getOneId());
        vo.setCurrentAgent(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentClientHomeLatestDepositApi.latestDeposit(vo);
    }

    @Operation(summary = "游戏输赢")
    @PostMapping("/getNewest5OrderRecord")
    public ResponseVO<List<GetNewest5OrderRecordVO>> getNewest5OrderRecord(@RequestBody GetNewest5OrderRecordParam param) {
        param.setAgentAccount(CurrReqUtils.getAccount());
        param.setSiteCode(CurrReqUtils.getSiteCode());
        return orderRecordApi.getNewest5OrderRecord(param);
    }

    @Operation(summary = "显示未读的最新一条通知")
    @PostMapping(value = "/getAgentNoticeNotReadFirst")
    public ResponseVO<UserNoticeRespVO> getAgentNoticeNotReadFirst() {
        AgentNoticeVO agentNoticeVO=AgentNoticeVO.builder()
                .userAccount(CurrReqUtils.getAccount())
                .siteCode(CurrReqUtils.getSiteCode())
                .userId(CurrReqUtils.getOneId())
                .build();
        return agentNoticeApi.getAgentNoticeNotReadFirst(agentNoticeVO);
    }


    @Operation(summary = "负盈利佣金比例（原佣金比例）、有效流水返点比例")
    @PostMapping(value = "/getRateDetail")
    public ResponseVO<RateDetailVO> getRateDetail() {
        return agentCommissionApi.getRateDetail(CurrReqUtils.getOneId());
    }

}
