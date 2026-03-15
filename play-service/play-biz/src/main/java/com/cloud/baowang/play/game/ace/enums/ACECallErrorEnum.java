package com.cloud.baowang.play.game.ace.enums;

import com.cloud.baowang.play.api.vo.ace.res.ACEBaseRes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ACECallErrorEnum {


    CODE_0("0", "Success"),
    CODE_1("1", "Insufficient balance. The error should be returned in the response on the Bet request."),
    CODE_2("2", "Player username was not found. Should be returned in the response on any request sent by PROVIDER if the issue occurred."),
    CODE_3("3", "Bet is not allowed. Should be returned in any case when the player is not allowed to play a specific game."),
    CODE_4("4", "Player authentication failed due to invalid, not found or expired token. Should be returned in the response on Authentication request."),
    CODE_6("6", "Player username is banned/disabled. OPERATOR will return this error in the response of any request if player account is banned or disabled."),
    CODE_8("8", "Game was not found or disabled. This error should be returned on Bet request if the game cannot be played."),
    CODE_9("9", "Rejected transaction. Should be returned in the response on any request sent by PROVIDER if the issue occurred."),
    CODE_100("100", "Internal server error. OPERATOR will return this error code if their system has internal problem and cannot process the request."),

    ;

    private final String code;
    private final String desc;

    //每个枚举值实现自己的返回逻辑
    public ACEBaseRes toResVO(ACEBaseRes resVO) {
        resVO.setError(this.code);
        resVO.setDescription(this.desc);
        return resVO;
    }

    public ACEBaseRes toResVO() {
        return ACEBaseRes.builder().error(this.code).description(this.desc).build();
    }
}
