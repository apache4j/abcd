package com.cloud.baowang.play.game.evo.response;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import java.util.List;

/**
 * EVO 投注信息 DTO
 * 对应游戏类型及下注配置信息
 */
@Data
public class EVOBetInfo {

    /**
     * 游戏类型，例如 "apollopays"
     */
    private String gameType;

    /**
     * 游戏类型名称，例如 "Apollo Pays"
     */
    private String gameTypeName;

    /**
     * 游戏子类型（可为空）
     */
    private String gameSubtype;

    /**
     * 游戏子类型名称（可为空）
     */
    private String gameSubtypeName;

    /**
     * 投注配置列表
     */
    private List<BetInfoDTO> bets;

    /**
     * 投注配置 DTO
     */
    @Data
    public static class BetInfoDTO {
        /**
         * 投注标识，例如 "Spin"、"FeatureBuy"
         */
        private String bet;

        /**
         * 投注名称，例如 "Spin"、"Feature Buy"
         */
        private String betName;

        /**
         * 投注分类，例如 "main"
         */
        private String betCategory;

        /**
         * 投注分类名称，例如 "Main Bet"
         */
        private String betCategoryName;

        /**
         * 投注类型标识，例如 "spin"、"feature_buy"
         */
        private String betType;

        /**
         * 投注类型名称，例如 "Spin"、"Feature Buy"
         */
        private String betTypeName;

        /**
         * 是否为初始投注标识（true 表示是初始投注）
         */
        private boolean initialBettingFlag;

    }
}
