package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.play.api.api.third.S128GameApi;
import com.cloud.baowang.play.api.vo.s128.*;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/s128")
@Tag(name = "s128单一钱包")
@AllArgsConstructor
public class S128Controller {

    private S128GameApi s128GameApi;

    @Operation(summary = "用户余额")
    @PostMapping( value = "/get_balance.aspx",produces = MediaType.APPLICATION_XML_VALUE)
    public GetBalanceRes getBalance(@RequestParam("api_key") String apiKey,
                                    @RequestParam("agent_code") String agentCode,
                                    @RequestParam("login_id") String loginId) {
        log.info("s128 收到请求  参数 apikey ：{}", apiKey);
        GetBalanceReq req = new GetBalanceReq();
        req.setApiKey(apiKey);
        req.setAgentCode(agentCode);
        req.setLoginId(loginId);
        return s128GameApi.getBalance(req);
    }

    @Operation(summary = "投注")
    @PostMapping( value = "/bet.aspx",produces = MediaType.APPLICATION_XML_VALUE)
    public BetRes bet(@RequestParam("api_key") String apiKey,
                      @RequestParam("agent_code") String agentCode,
                      @RequestParam("ticket_id") String ticketId,
                      @RequestParam("login_id") String loginId,
                      @RequestParam("arena_code") String arenaCode,
                      @RequestParam("match_no") String matchNO,
                      @RequestParam("match_date") String matchDate,
                      @RequestParam("fight_no") String fightNo,
                      @RequestParam("fight_datetime") String fightDatetime,
                      @RequestParam("bet_on") String betOn,
                      @RequestParam("odds_given") BigDecimal oddsGiven,
                      @RequestParam("stake") Integer stake,
                      @RequestParam("stake_money") BigDecimal stakeMoney,
                      @RequestParam("created_datetime") String createdDatetime) {
        BetReq req = new BetReq();
        req.setBetOn(betOn);
        req.setArenaCode(arenaCode);
        req.setArenaNameCn(arenaCode);
        req.setFightNo(fightNo);
        req.setApiKey(apiKey);
        req.setAgentCode(agentCode);
        req.setLoginId(loginId);
        req.setCreatedDatetime(createdDatetime);
        req.setMatchNo(matchNO);
        req.setTicketId(ticketId);
        req.setMatchDate(matchDate);
        req.setFightDatetime(fightDatetime);
        req.setOddsGiven(oddsGiven);
        req.setStake(stake);
        req.setStakeMoney(stakeMoney);
        return s128GameApi.bet(req);
    }

    @Operation(summary = "取消投注")
    @PostMapping( value = "/cancel_bet.aspx",produces = MediaType.APPLICATION_XML_VALUE)
    public CancelBetRes cancelBet(@RequestParam("api_key") String apiKey,
                                  @RequestParam("agent_code") String agentCode,
                                  @RequestParam("ticket_id") String ticketId,
                                  @RequestParam("login_id") String loginId
                                  ) {
        CancelBetReq req = new CancelBetReq();
        req.setAgentCode(agentCode);
        req.setApiKey(apiKey);
        req.setTicketId(ticketId);
        req.setLoginId(loginId);
        return s128GameApi.cancelBet(req);
    }


}
