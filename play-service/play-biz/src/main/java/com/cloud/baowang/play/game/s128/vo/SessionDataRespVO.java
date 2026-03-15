package com.cloud.baowang.play.game.s128.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "get_session_id")
public class SessionDataRespVO {
    @JacksonXmlProperty(localName = "status_code")
    private String statusCode;
    @JacksonXmlProperty(localName = "status_text")
    private String statusText;
    @JacksonXmlProperty(localName = "session_id")
    private String sessionId;

}
