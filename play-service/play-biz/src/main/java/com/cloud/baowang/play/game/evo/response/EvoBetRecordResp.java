package com.cloud.baowang.play.game.evo.response;


import com.alibaba.fastjson2.annotation.JSONType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Evo Bet Record Response
 * Evo 投注记录响应
 */
@Data
public class EvoBetRecordResp {

    private String json;

    /**
     * The unique game round identifier
     * 游戏局唯一标识
     */
    private String id;

    /**
     * The date and time when game round started
     * 游戏开始时间
     */
    private String startedAt;

    /**
     * The date and time when game round settled
     * 游戏结算时间
     */
    private String settledAt;

    /**
     * Game round status (Resolved/Cancelled)
     * 游戏状态（已结算/已取消）
     */
    private String status;

    /**
     * The game type value for particular table
     * 游戏类型
     */
    private String gameType;

    /**
     * The game subtype value for particular table
     * 游戏子类型
     */
    private String gameSubType;

    /**
     * Game provider
     * 游戏供应商
     */
    private String gameProvider;

    /**
     * Game sub provider
     * 游戏子供应商
     */
    private String gameSubProvider;

    /**
     * Table details
     * 游戏桌信息
     */
    private Table table;

    /**
     * Dealer details
     * 荷官信息
     */
    private Dealer dealer;

    /**
     * Default casino currency
     * 默认货币
     */
    private String currency;

    /**
     * Sum of player total bets in game round
     * 游戏局总投注金额
     */
    private BigDecimal wager;


    /**
     * Sum of player total payouts in game round including winnings
     * 游戏局总派彩金额（包含中奖）
     */
    private BigDecimal payout;

    /**
     * List of participants
     * 参与玩家列表
     */
    private List<Participant> participants;

    /**
     * 新增：完整结果映射
     */
    private Result result;
    /**
     * 新增：完整结果映射
     */
    @Data
    public static class Result {
        private DealerResult dealer;
        private Map<String, SeatResult> seats; // SeatId -> SeatResult
        private List<String> burnedCards;

        private LightningPayTable lightningPayTable; // 新增
        @Data
        public static class LightningPayTable {
            private Integer id;
            private Map<String, Integer> value; // 数字开头 key 用 Map<String,Integer> 接收
        }

        @Data
        public static class DealerResult {
            private int score;
            private List<String> cards;
            private List<String> bonusCards;
            private boolean isBlackjack;
        }

        @Data
        public static class SeatResult {
            private List<DecisionRecord> decisions;
            private int score;
            private String outcome;
            private List<String> bonusCards;
            private List<String> cards;

            @Data
            public static class DecisionRecord {
                private String recordedAt;
                private String type;
            }
        }
    }


    @Data
    public static class Table {
        private String id; // Table ID / 桌子唯一标识
        private String name; // Table Name / 桌子名称
    }

    @Data
    public static class Dealer {
        private String uid; // Dealer ID / 荷官唯一标识
        private String name; // Dealer Name / 荷官名称
    }

    @Data
    public static class Participant {
        private String casinoId; // Casino assigned ID / 玩家在赌场的唯一标识
        private String playerGameId; // Unique per player per game / 玩家局内唯一标识
        private String status; // Player round status / 玩家局状态（已结算/已取消）
        private String playerId; // External user ID / 外部用户ID
        private String screenName; // Player alias / 玩家昵称
        private String sessionId; // Session ID by Evolution / Evolution 分配的会话ID
        private String casinoSessionId; // Session ID by Licensee / Licensee 分配的会话ID
        private String channel; // Player channel / 玩家渠道（desktop/mobile/other）
        private String device; // Player device / 玩家设备（PC/iPhone等）
        private String os; // Player OS / 玩家操作系统
        private String currency; // Currency code / 货币代码
        private BigDecimal betCoverageSimple; // Probability to win / 中奖概率
        private List<Bet> bets; // Player bets / 玩家投注
        private List<Bet> rewardBets; // Player reward bets / 奖励投注
        private String playMode; // Play mode / 游戏模式
        private String mathId; // Math model ID / 数学模型ID
        private List<String> configOverlays; // Config overlay IDs / 配置覆盖ID
        private String brandId; // Brand ID / 品牌ID
        private String skinId; // Skin ID / 桌面皮肤ID
        private String aamsParticipationId; // AAMS Participation ID / AAMS参与ID
        private String aamsSessionId; // AAMS Session ID / AAMS会话ID
        private List<Seat> seats; // Player seats / 玩家座位
        private Decision decision; // Player decision / 玩家决策
        private List<Decision> decisions; // Multiple decisions / 多决策
        private Map<String, Object> hands; //  21 点
        //private List<Hand> hands; // Blackjack hands / 黑杰克手牌
        private FreeBet freebet; // Free bet info / 免费投注
        private Object appliedMultiplier; // Multiplier applied / 已应用倍数
        private Object acquiredMultiplier; // Multiplier acquired / 当前局赢得倍数
        private SideBets sideBets; // Side bets / 旁注
        private QualificationSpin qualificationSpin; // Qualification spin / 资格旋转
        private TopUpResults topUpResults; // Top up results / 补充旋转结果
        private Boolean useNewBetCodes; // Mega Ball bet code type / Mega Ball投注码类型
        private BigDecimal betStakePerCard; // Stake per card / 每张卡投注
        private Integer cardsCount; // Number of cards / 卡片数量
        private List<String> cards; // Cards drawn / 卡片结果
        private BigDecimal totalMultiplier; // Total multiplier / 总倍数
        private Bonus bonus; // Bonus info / 奖励信息
        private String leftOnRoll; // Craps leave info / 玩家提前离开轮ID
        private List<Pick> picks; // Gonzo picks / Gonzo选择
        private List<Multiplier> multipliers; // Multipliers won / 赢得倍数
        private List<GameStep> gameSteps; // Cash Or Crash game steps / 游戏步骤
        private QualificationResult qualificationResult; // Qualification result / 资格结果
        private String subType; // Participant subtype / 玩家子类型
        private String resultLink; // Link to game result / 游戏结果链接

        @Data
        public static class Bet {
            private String code; // Bet code / 投注代码
            private BigDecimal stake; // Bet amount / 投注金额
            private BigDecimal payout; // Payout amount / 派彩金额
            private String placedOn; // Placed time / 投注时间
            private String transactionId; // External transaction ID / 外部交易ID
            private String owTransactionId; // One Wallet transaction ID / One Wallet交易ID
            private String currencyRateVersion; // Currency rate version / 汇率版本
        }

        @Data
        public static class Seat {
            private String seatId; // Seat ID / 座位ID
            private BigDecimal resultAmount; // Seat result / 座位结果
        }

        @Data
        public static class Decision {
            private String decidedAt; // Decision timestamp / 决策时间
            private String decisionType; // Decision type / 决策类型
        }

        @Data
        public static class Hand {
            private String handId; // Hand ID / 手牌ID
            private String result; // Hand result / 手牌结果
        }

        @Data
        public static class FreeBet {
            private String decision; // Free bet decision / 免费投注决策
        }

        @Data
        public static class AppliedMultiplier {
            private BigDecimal value; // Multiplier value / 倍数值
            private String prevGameId; // Previous game ID / 上一局ID
            private String acquiredAt; // Acquired time / 获得时间
            private BigDecimal previousFee; // Previous bet / 上一局投注
            private BigDecimal currentFee; // Current bet / 当前局投注
        }

        @Data
        public static class SideBets {
            private BigDecimal playerPair;
            private BigDecimal bankerPair;
            private BigDecimal perfectPair;
            private BigDecimal eitherPair;
            private BigDecimal playerBonus;
            private BigDecimal bankerBonus;
            private BigDecimal superSix;
            private BigDecimal perfectPairBlackjack;
            private BigDecimal side21p3;
            private BigDecimal sideAnyPair;
            private BigDecimal sideAABonus;
            private BigDecimal side5p1;
            private BigDecimal sidePairPlus;
            private BigDecimal side6CardBonus;
            private BigDecimal sidePairOrBetter;
            private BigDecimal side3p3;
            private BigDecimal sideTrips;
            private BigDecimal sideBestFive;
            private BigDecimal sideBonus;
            private BigDecimal sideJackpot;
            private BigDecimal suitedTie;
        }

        @Data
        public static class QualificationSpin {
            private String spinMode;
            private BigDecimal betMultiplier;
            private BigDecimal initialStake;
            private List<List<String>> screenReels;
            private List<WinLine> winLines;
            private List<Scatter> scatter;

            @Data
            public static class WinLine {
                private Integer number;
                private String symbolId;
                private Integer length;
                private List<List<Integer>> winCombination;
                private BigDecimal multiplier;
            }

            @Data
            public static class Scatter {
                private List<BigDecimal> multipliers;
                private Integer length;
                private BigDecimal totalMultiplier;
                private String symbolId;
            }
        }

        @Data
        public static class TopUpResults {
            private List<Spin> spins;
            private Multipliers multipliers;

            @Data
            public static class Spin {
                private BigDecimal betMultiplier;
                private List<String> symbols;
                private TotalMultiplier totalMultiplier;

                @Data
                public static class TotalMultiplier {
                    private BigDecimal value;
                    private String side;
                }
            }

            @Data
            public static class Multipliers {
                private BigDecimal red;
                private BigDecimal blue;
            }
        }

        @Data
        public static class Bonus {
            private String type;
            private Integer row;
            private Integer column;
            private Boolean auto;
            private LocalDateTime decidedAt;
            private Flapper flapper;

            @Data
            public static class Flapper {
                private String type;
                private String color;
            }
        }

        @Data
        public static class Pick {
            private Integer requested;
            private Utilized utilized;

            @Data
            public static class Utilized {
                private Position position;
                private Boolean auto;
                private Boolean win;

                @Data
                public static class Position {
                    private Integer row;
                    private Integer column;
                }
            }
        }

        @Data
        public static class Multiplier {
            private String betCode;
            private BigDecimal value;
        }

        @Data
        public static class GameStep {
            private String type;
            private Boolean autoContinue;
            private BigDecimal potentialWin;
            private String proposedAt;
            private String decidedAt;
            private Boolean auto;
            private BigDecimal totalWin;
            private String decisionType;
        }

        @Data
        public static class QualificationResult {
            private String gameRoundId;
            private String qualifiedAt;
            private TopUpResults topUp;
            private String skipGameRoundDecidedAt;
            private Multipliers coinFinalMultipliers;
            private BigDecimal coinWinMultiplier;

            @Data
            public static class Multipliers {
                private BigDecimal red;
                private BigDecimal blue;
            }
        }
    }
}


