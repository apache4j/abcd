package com.cloud.baowang.play.api.vo.winto.req;

import lombok.Data;

@Data
public class WintoBaseVO  {

    private String operatorCode;

    private String userToken;

    private String ip;

    private String gameId;

    private String userName;

    private String nickName;

    private String currency;

    private String agentId;
}
