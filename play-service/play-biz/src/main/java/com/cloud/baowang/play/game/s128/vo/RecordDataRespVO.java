package com.cloud.baowang.play.game.s128.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "get_cockfight_processed_ticket")
public class RecordDataRespVO {
    @JacksonXmlProperty(localName = "status_code")
    private String statusCode;
    @JacksonXmlProperty(localName = "status_text")
    private String statusText;
    @JacksonXmlProperty(localName = "total_records")
    private Integer totalRecords;
    @JacksonXmlProperty(localName = "data")
    private String data;

}
