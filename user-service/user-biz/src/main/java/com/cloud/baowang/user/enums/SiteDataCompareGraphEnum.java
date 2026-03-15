package com.cloud.baowang.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 30/6/23 3:33 PM
 * @Version : 1.0
 *      *
 *      *  NOTE : 2025/6/19 新需求改动
 *      *   一. 平台数据概览
 *      *  1. 新增会员
 *      *  2. 首存人数
 *      *  3. 登录人数 NOTE 弃用
 *      *  4. 存款金额==会员存款金额 => 平台输赢概览
 *      *  5. 取款金额==会员取款金额 => 平台输赢概览
 *      *  6. 总输赢, NOTE 弃用或转到 11
 *      *
 *      *  7. 投注人数 NOTE 新加的
 *      *  8. 会员存款人数 NOTE 新加的
 *      *  9. 会员提款人数 NOTE 新加的
 *      *
 *      *  二. 平台输赢概览
 *      *
 *      *  10. 平台游戏输赢 NOTE 新加的
 *      *  11. 平台净输赢 NOTE 新加的
 *      *  12. 平台盈亏 NOTE 新加的
 *      *  13. 有效注单数量 NOTE 新加的
 *      *  14. 有效投注金额 NOTE 新加的
 *      *
 */
@Getter
@AllArgsConstructor
public enum SiteDataCompareGraphEnum {

    NEW_REGISTERED("1", "新注册人数"),

    FIRST_DEPOSIT("2", "首存人数"),

    LOGIN_COUNT("3", "登录人数"),

    DEPOSIT_AMOUNT("4","存款金额") ,

    WITHDRAWAL_AMOUNT("5", "取款金额"),

    TOTAL_WINS_LOSSES("6", "总输赢"),

    NUMBER_OF_BETTORS("7", "投注人数"),

    NUMBER_OF_DEPOSITORS("8", "会员存款人数"),
    NUMBER_OF_WITHDRAWALS("9", "会员提款人数"),

    GAME_WINS_LOSSES("10", "平台游戏输赢"),
    PLATFORM_NET_WINS_LOSSES("11", "平台净输赢"),
    PLATFORM_WINS_LOSSES("12", "平台盈亏"),

    NUMBER_OF_VALID_BETS("13", "有效注单数量"),
    EFFECTIVE_BET_AMOUNT("14", "有效投注金额"),

    ;

    private final String code;
    private final String name;


    public static SiteDataCompareGraphEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        SiteDataCompareGraphEnum[] types = SiteDataCompareGraphEnum.values();
        for (SiteDataCompareGraphEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<SiteDataCompareGraphEnum> getList() {
        return Arrays.asList(values());
    }

}
