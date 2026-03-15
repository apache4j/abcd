package com.cloud.baowang.play.api.vo.s128;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Data
public class BetReq {

    @JacksonXmlProperty(localName =  "api_key")
    private String apiKey;

    @JacksonXmlProperty(localName =  "agent_code")
    private String agentCode;

    @JacksonXmlProperty(localName =  "ticket_id")
    private String ticketId;

    @JacksonXmlProperty(localName =  "login_id")
    private String loginId;

    @JacksonXmlProperty(localName =  "arena_code")
    private String arenaCode;

    private String arenaNameCn;

    @JacksonXmlProperty(localName =  "match_no")
    private String matchNo;

    /**
     * format:yyyy-MM-dd
     */
    @JacksonXmlProperty(localName =  "match_date")
    private String matchDate;

    @JacksonXmlProperty(localName =  "fight_no")
    private String fightNo;

    /**
     * format:yyyy-MM-dd HH:mm:ss
     */
    @JacksonXmlProperty(localName =  "fight_datetime")
    private String fightDatetime;

    /**
     * MERON/WALA/BDD/FTD
     */
    @JacksonXmlProperty(localName =  "bet_on")
    private String betOn;

    @JacksonXmlProperty(localName =  "odds_given")
    private BigDecimal oddsGiven;

    @JacksonXmlProperty(localName =  "stake")
    private Integer stake;

    @JacksonXmlProperty(localName =  "stake_money")
    private BigDecimal stakeMoney;

    @JacksonXmlProperty(localName =  "created_datetime")
    private String createdDatetime;


    public boolean valid() {
        return StringUtils.isEmpty(apiKey)
                || StringUtils.isEmpty(agentCode)
                || ticketId == null
                || StringUtils.isEmpty(loginId)
                || StringUtils.isEmpty(arenaCode)
                || StringUtils.isEmpty(matchNo)
                || StringUtils.isEmpty(matchDate)
                || StringUtils.isEmpty(fightNo)
                || StringUtils.isEmpty(fightDatetime)
                || StringUtils.isEmpty(betOn)
                || oddsGiven == null
                || stake == null
                || stakeMoney == null
                || StringUtils.isEmpty(createdDatetime);
    }

}
