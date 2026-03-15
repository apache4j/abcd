package com.cloud.baowang.play.wallet.vo.req.bti;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;
@Data
@JacksonXmlRootElement(localName = "Bets")
public class Bets {
    // <Bets cust_id="..." player_level_id="..." ...>
    @JacksonXmlProperty(isAttribute = true, localName = "cust_id")
    private String custId;

    @JacksonXmlProperty(isAttribute = true, localName = "player_level_id")
    private String playerLevelId;

    @JacksonXmlProperty(isAttribute = true, localName = "reserve_id")
    private String reserveId;

    @JacksonXmlProperty(isAttribute = true, localName = "amount")
    private String amount;

    @JacksonXmlProperty(isAttribute = true, localName = "currency_code")
    private String currencyCode;

    @JacksonXmlProperty(isAttribute = true, localName = "platform")
    private String platform;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Bet")
    private List<Bet>  bet;

}
