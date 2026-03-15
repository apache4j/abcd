package com.cloud.baowang.play.api.vo.cq9.request;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CQ9EndRoundReq {

    /**
     * 用户账户，最大长度为36个字符
     */
    private String account;

    /**
     * 游戏厂商代号，最大长度为36个字符
     */
    private String gamehall;

    /**
     * 游戏代号，最大长度为36个字符
     */
    private String gamecode;

    /**
     * 注单号，同一局 bet/endround 使用相同的 roundid，最大长度为50个字符
     */
    private String roundid;

    /**
     * 事件数据列表，包含多个事件对象
     */
    private List<EventData> data;

    /**
     * 系统成单时间，格式为 RFC3339，如 2017-01-19T22:56:30-04:00
     */
    private String createTime;

    /**
     * 免费游戏次数，选填
     */
    private Integer freegame;

    /**
     * 奖励游戏次数，选填
     */
    private Integer bonus;

    /**
     * 抽奖游戏次数，选填
     */
    private Integer luckydraw;

    /**
     * 彩池奖金额，选填
     */
    private BigDecimal jackpot;

    /**
     * 彩池奖金贡献值数组，选填
     */
    private List<BigDecimal> jackpotcontribution;

    /**
     * 是否为免费券派彩，选填，true = 是，false = 否
     */
    private Boolean freeticket;


    private String wtoken;

    /**
     * 请求方式
     */
    private String callType;


    public Boolean valid() {
        if (!ObjectUtil.isAllNotEmpty(account, gamehall, gamecode,
                roundid, createTime)) {
            return false;
        }

        return CollectionUtil.isNotEmpty(data);
    }

}




