package com.cloud.baowang.play.api.vo.pt2;

import lombok.Data;

@Data
public class PT2BaseVO {
    private String requestId;
    private String username;

    //token <登录带入>
    private String externalToken;

    private String gameRoundCode;
    //游戏代码名称
    private String gameCodeName;

    private String transactionCode;

    private String transactionDate;


}
