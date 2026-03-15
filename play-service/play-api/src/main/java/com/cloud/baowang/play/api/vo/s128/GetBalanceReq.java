package com.cloud.baowang.play.api.vo.s128;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;


@Data
public class GetBalanceReq {

    @JacksonXmlProperty(localName = "api_key")
    private String apiKey;

    @JacksonXmlProperty(localName = "agent_code")
    private String agentCode;

    @JacksonXmlProperty(localName = "login_id")
    private String loginId;


}
