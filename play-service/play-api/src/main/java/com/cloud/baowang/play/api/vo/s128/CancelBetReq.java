package com.cloud.baowang.play.api.vo.s128;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CancelBetReq {

    @JacksonXmlProperty(localName = "api_key")
    private String apiKey;

    @JacksonXmlProperty(localName = "agent_code")
    private String agentCode;

    @JacksonXmlProperty(localName = "ticket_id")
    private String ticketId;

    @JacksonXmlProperty(localName = "login_id")
    private String loginId;


    public boolean valid() {
        return StringUtils.isEmpty(apiKey)
                || StringUtils.isEmpty(agentCode)
                || StringUtils.isEmpty(ticketId)
                || StringUtils.isEmpty(loginId);
    }

}
