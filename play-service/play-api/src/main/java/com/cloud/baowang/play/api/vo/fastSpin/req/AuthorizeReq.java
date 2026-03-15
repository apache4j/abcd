package com.cloud.baowang.play.api.vo.fastSpin.req;

import com.cloud.baowang.play.api.vo.fastSpin.AcctInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizeReq {

    String merchantCode;
    String token;
    String acctIp;
    String game;
    String language;
    String exitUrl;
    String serialNo;
    Boolean mobile;
    Boolean fun;
    Boolean menuMode;
    Boolean fullScreen;

    AcctInfo acctInfo;
}
