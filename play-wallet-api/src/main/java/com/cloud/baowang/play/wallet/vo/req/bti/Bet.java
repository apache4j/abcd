package com.cloud.baowang.play.wallet.vo.req.bti;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class Bet {
    @JacksonXmlProperty(isAttribute = true, localName = "BetID")
    private String betId;

    @JacksonXmlProperty(isAttribute = true, localName = "BetTypeID")
    private String betTypeId;

    @JacksonXmlProperty(isAttribute = true, localName = "BetTypeName")
    private String betTypeName;

    @JacksonXmlProperty(isAttribute = true, localName = "Stake")
    private BigDecimal stake;

    @JacksonXmlProperty(isAttribute = true, localName = "OrgStake")
    private BigDecimal orgStake;

    @JacksonXmlProperty(isAttribute = true, localName = "Gain")
    private BigDecimal gain;

    @JacksonXmlProperty(isAttribute = true, localName = "DBOdds")
    private BigDecimal dbOdds;

    @JacksonXmlProperty(isAttribute = true, localName = "PromotionID")
    private String promotionId;

    @JacksonXmlProperty(isAttribute = true, localName = "IsLive")
    private String isLive;

    @JacksonXmlProperty(isAttribute = true, localName = "NumberOfBets")
    private String numberOfBets;

    @JacksonXmlProperty(isAttribute = true, localName = "Status")
    private String status;

    @JacksonXmlProperty(isAttribute = true, localName = "IsFreeBet")
    private String isFreeBet;

    @JacksonXmlProperty(isAttribute = true, localName = "BonusID")
    private String bonusId;

    @JacksonXmlProperty(isAttribute = true, localName = "FreebetAmount")
    private BigDecimal freebetAmount;

    @JacksonXmlProperty(isAttribute = true, localName = "RealAmount")
    private BigDecimal realAmount;

    @JacksonXmlProperty(isAttribute = true, localName = "CreationDate")
    private String creationDate;

    @JacksonXmlProperty(isAttribute = true, localName = "PurchaseBetID")
    private String purchaseBetId;

    @JacksonXmlProperty(isAttribute = true, localName = "CommomStatusID")
    private String commomStatusId;

    @JacksonXmlProperty(isAttribute = true, localName = "Odds")
    private String odds;

    @JacksonXmlProperty(isAttribute = true, localName = "OddsDec")
    private BigDecimal oddsDec;

    @JacksonXmlProperty(isAttribute = true, localName = "ComboBetNumersLines")
    private String comboBetNumbersLines;

    @JacksonXmlProperty(isAttribute = true, localName = "ReferenceID")
    private String referenceId;

    @JacksonXmlProperty(isAttribute = true, localName = "ReserveAmountType")
    private String reserveAmountType;

    @JacksonXmlProperty(isAttribute = true, localName = "ReserveAmountTypeID")
    private String reserveAmountTypeId;

    @JacksonXmlProperty(isAttribute = true, localName = "LineID")
    private String lineId;

    @JacksonXmlProperty(isAttribute = true, localName = "LiveScore1")
    private String liveScore1;

    @JacksonXmlProperty(isAttribute = true, localName = "LiveScore2")
    private String liveScore2;

    @JacksonXmlProperty(isAttribute = true, localName = "EventState")
    private String eventState;

    @JacksonXmlProperty(isAttribute = true, localName = "LineTypeID")
    private String lineTypeId;

    @JacksonXmlProperty(isAttribute = true, localName = "LineTypeName")
    private String lineTypeName;

    @JacksonXmlProperty(isAttribute = true, localName = "RowTypeID")
    private String rowTypeId;

    @JacksonXmlProperty(isAttribute = true, localName = "BranchName")
    private String branchName;

    @JacksonXmlProperty(isAttribute = true, localName = "LeagueID")
    private String leagueId;

    @JacksonXmlProperty(isAttribute = true, localName = "MasterLeagueID")
    private String masterLeagueId;

    @JacksonXmlProperty(isAttribute = true, localName = "EventName")
    private String eventName;

    @JacksonXmlProperty(isAttribute = true, localName = "LeagueName")
    private String leagueName;

    @JacksonXmlProperty(isAttribute = true, localName = "YourBet")
    private String yourBet;

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

    @JacksonXmlProperty(isAttribute = true, localName = "EventTypeID")
    private String eventTypeId;

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

    @JacksonXmlProperty(isAttribute = true, localName = "TeamMappingID")
    private String teamMappingId;

    @JacksonXmlProperty(isAttribute = true, localName = "Score")
    private String score;

    @JacksonXmlProperty(isAttribute = true, localName = "EventTypeName")
    private String eventTypeName;

    @JacksonXmlProperty(isAttribute = true, localName = "Points")
    private BigDecimal points;

    @JacksonXmlProperty(isAttribute = true, localName = "BranchId")
    private String branchId;

    @JacksonXmlProperty(isAttribute = true, localName = "EachWaySetting")
    private String eachWaySetting;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Lines")
    private List<Line> lines = new ArrayList<>();
}
