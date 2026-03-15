package com.cloud.baowang.play.game.pp.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "get_pp_res_vo")
public class PPResVO {

    @JacksonXmlProperty(localName = "Code")
    private String Code;
    @JacksonXmlProperty(localName = "Message")
    private String Message;
    @JacksonXmlProperty(localName = "Method")
    private String Method;
    @JacksonXmlProperty(localName = "ResourceType")
    private String ResourceType;
    @JacksonXmlProperty(localName = "RequestId")
    private String RequestId;
    @JacksonXmlProperty(localName = "HostId")
    private String HostId;

}
