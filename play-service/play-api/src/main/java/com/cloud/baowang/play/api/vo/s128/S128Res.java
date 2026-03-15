package com.cloud.baowang.play.api.vo.s128;


import com.cloud.baowang.play.api.enums.s128.S128BetErrorCodeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;



@Data
public class S128Res {

    @JacksonXmlProperty(localName = "status_code")
    private String statusCode;

    @JacksonXmlProperty(localName = "status_text")
    private String statusText;

    @JsonIgnore
    public boolean isOk(){
        return S128BetErrorCodeEnum.SUCCESS.getCode().equals(this.statusCode);
    }


}
