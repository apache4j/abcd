package com.cloud.baowang.play.game.pp.enums;

import com.cloud.baowang.play.api.vo.pp.PPBaseResVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum PPErrorCodeEnum {

    CODE_0("0", "Success") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            resVO.setError(CODE_0.code);
            resVO.setDescription(CODE_0.desc);
            return resVO;
        }
    },
    //余额不足。 该错误应在投注请求的响应中返回。
    CODE_1("1", "Insufficient balance. The error should be returned in the response on theBet request") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            return PPBaseResVO.builder().error(this.code).description(this.desc).build();
        }
    },
    //未找到玩家或已注销。 如果无法找到玩家或在娱乐场运营商处登出，则应在 Pragmatic Play 发送的任何请求的响应中返回。
    CODE_2("2", "Player not found or is logged out") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            return PPBaseResVO.builder().error(this.code).description(this.desc).build();
        }
    },
    //不允许投注。 当玩家不被允许玩特定游戏时，无论如何都应该返回。 例如，因为特殊奖金。
    CODE_3("3", "Bet is not allowed") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            return PPBaseResVO.builder().error(this.code).description(this.desc).build();
        }
    },
    //由于令牌无效、未找到或过期，玩家身份验证失败。
    CODE_4("4", "Player authentication failed due to invalid") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            return PPBaseResVO.builder().error(this.code).description(this.desc).build();
        }
    },
    //无效的哈希码。 如果哈希码验证失败，则应在 Pragmatic Play 发送的任何请求的响应中返回。
    CODE_5("5", "Invalid hash code") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            return PPBaseResVO.builder().error(this.code).description(this.desc).build();
        }
    },
    //玩家被冻结。 如果玩家帐户被禁止或冻结，娱乐场运营商将在响应任何请求时返回此错误。
    CODE_6("6", "Player is frozen") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            return PPBaseResVO.builder().error(this.code).description(this.desc).build();
        }
    },
    //请求参数错误，请检查post参数。
    CODE_7("7", "Bad parameters in the request") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            return PPBaseResVO.builder().error(this.code).description(this.desc).build();
        }
    },
    //游戏未找到或已禁用。 如果由于某种原因游戏无法进行，则应在投注请求时返回此错误。 即使游戏被禁用，包含获胜金额的投注结果请求也应按预期处理。
    CODE_8("8", "Game is not found or disabled") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            return PPBaseResVO.builder().error(this.code).description(this.desc).build();
        }
    },
    //已达到投注限额。 该准则与受监管的市场相关
    CODE_50("50", "Bet limit has been reached") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            return PPBaseResVO.builder().error(this.code).description(this.desc).build();
        }
    },
    // Internal server error
    CODE_100("100", "Internal server error") {
        @Override
        public PPBaseResVO toResVO(PPBaseResVO resVO) {
            return PPBaseResVO.builder().error(this.code).description(this.desc).build();
        }
    },

    ;

    String code;
    String desc;

    public static PPErrorCodeEnum getByCode(String code) {

        for (PPErrorCodeEnum errorCodeEnum : PPErrorCodeEnum.values()) {
            if (errorCodeEnum.code.equals(code)) {
                return errorCodeEnum;
            }
        }
        return null; // 异常
    }

    // 抽象方法：每个枚举值实现自己的返回逻辑
    public abstract PPBaseResVO toResVO(PPBaseResVO resVO);

}
