package com.cloud.baowang.play.wallet.vo.req.bti;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Line {
    @JacksonXmlProperty(isAttribute = true, localName = "LineID")
    private String lineId;

    @JacksonXmlProperty(isAttribute = true, localName = "Stake")
    private BigDecimal stake;

    @JacksonXmlProperty(isAttribute = true, localName = "OddsDec")
    private BigDecimal oddsDec;

    @JacksonXmlProperty(isAttribute = true, localName = "Gain")
    private BigDecimal gain;

    @JacksonXmlProperty(isAttribute = true, localName = "LiveScore1")
    private String liveScore1;

    @JacksonXmlProperty(isAttribute = true, localName = "LiveScore2")
    private String liveScore2;

    @JacksonXmlProperty(isAttribute = true, localName = "HomeTeam")
    private String homeTeam;

    @JacksonXmlProperty(isAttribute = true, localName = "AwayTeam")
    private String awayTeam;

    @JacksonXmlProperty(isAttribute = true, localName = "TransEventName")
    private String transEventName;

    @JacksonXmlProperty(isAttribute = true, localName = "TransEventTypeName")
    private String transEventTypeName;

    @JacksonXmlProperty(isAttribute = true, localName = "TransLeagueName")
    private String transLeagueName;

    @JacksonXmlProperty(isAttribute = true, localName = "TransYourBet")
    private String transYourBet;

    @JacksonXmlProperty(isAttribute = true, localName = "TransHomeTeam")
    private String transHomeTeam;

    @JacksonXmlProperty(isAttribute = true, localName = "TransAwayTeam")
    private String transAwayTeam;

    @JacksonXmlProperty(isAttribute = true, localName = "TransBranchName")
    private String transBranchName;

    @JacksonXmlProperty(isAttribute = true, localName = "Status")
    private String status;

    @JacksonXmlProperty(isAttribute = true, localName = "EventState")
    private String eventState;

    @JacksonXmlProperty(isAttribute = true, localName = "CustomerID")
    private String customerId;

    @JacksonXmlProperty(isAttribute = true, localName = "BetID")
    private String betId;

    @JacksonXmlProperty(isAttribute = true, localName = "BetTypeName")
    private String betTypeName;

    @JacksonXmlProperty(isAttribute = true, localName = "LineTypeID")
    private String lineTypeId;

    @JacksonXmlProperty(isAttribute = true, localName = "LineTypeName")
    private String lineTypeName;

    @JacksonXmlProperty(isAttribute = true, localName = "RowTypeID")
    private String rowTypeId;

    @JacksonXmlProperty(isAttribute = true, localName = "BranchID")
    private String branchId;

    @JacksonXmlProperty(isAttribute = true, localName = "BranchName")
    private String branchName;

    @JacksonXmlProperty(isAttribute = true, localName = "LeagueID")
    private String leagueId;

    @JacksonXmlProperty(isAttribute = true, localName = "LeagueName")
    private String leagueName;

    @JacksonXmlProperty(isAttribute = true, localName = "MasterLeagueID")
    private String masterLeagueId;

    @JacksonXmlProperty(isAttribute = true, localName = "CreationDate")
    private String creationDate;

    @JacksonXmlProperty(isAttribute = true, localName = "YourBet")
    private String yourBet;

    @JacksonXmlProperty(isAttribute = true, localName = "EventTypeID")
    private String eventTypeId;

    @JacksonXmlProperty(isAttribute = true, localName = "EventTypeName")
    private String eventTypeName;

    @JacksonXmlProperty(isAttribute = true, localName = "EventDate")
    private String eventDate;

    @JacksonXmlProperty(isAttribute = true, localName = "MasterEventID")
    private String masterEventId;

    @JacksonXmlProperty(isAttribute = true, localName = "EventID")
    private String eventId;

    @JacksonXmlProperty(isAttribute = true, localName = "NewMasterEventID")
    private String newMasterEventId;

    @JacksonXmlProperty(isAttribute = true, localName = "NewEventID")
    private String newEventId;

    @JacksonXmlProperty(isAttribute = true, localName = "NewLeagueID")
    private String newLeagueId;

    @JacksonXmlProperty(isAttribute = true, localName = "NewLineID")
    private String newLineId;

    @JacksonXmlProperty(isAttribute = true, localName = "EventName")
    private String eventName;

    @JacksonXmlProperty(isAttribute = true, localName = "TeamMappingID")
    private String teamMappingId;

    @JacksonXmlProperty(isAttribute = true, localName = "BetTypeID")
    private String betTypeId;

    @JacksonXmlProperty(isAttribute = true, localName = "Odds")
    private String odds;

    @JacksonXmlProperty(isAttribute = true, localName = "DBOdds")
    private BigDecimal dbOdds;

    @JacksonXmlProperty(isAttribute = true, localName = "PromotionID")
    private String promotionId;

    @JacksonXmlProperty(isAttribute = true, localName = "Score")
    private String score;

    @JacksonXmlProperty(isAttribute = true, localName = "IsLive")
    private String isLive;

    @JacksonXmlProperty(isAttribute = true, localName = "Points")
    private BigDecimal points;

    @JacksonXmlProperty(isAttribute = true, localName = "EachWaySetting")
    private String eachWaySetting;

    // 其余几十个属性不用一一声明，统一装到 extraAttributes
    private Map<String, String> extraAttributes = new LinkedHashMap<>();

    @JsonAnySetter
    public void put(String key, Object val) {
        // 未在上面显式声明的属性都会进这里
        extraAttributes.put(key, val == null ? null : String.valueOf(val));
    }

    @JsonAnyGetter
    public Map<String, String> any() {
        return extraAttributes;
    }
}
