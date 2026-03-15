package com.cloud.baowang.play.api.vo.cq9.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @className:      TransactionRecordRsp
 * @author:     wade
 * @description:  返回帐变记录
 * @date:    25/2/25 16:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRecordRsp {

        /** 交易紀錄編號 */
        private String _id;

        /** 該交易的動作，如 bet, endround, debit, credit, rollin, rollout, payoff */
        private String action;

        /** 目標帳號 */
        private Target target;

        /** 交易狀態 */
        private Status status;

        /** 交易前餘額，支援 12+4 小數位 */
        private BigDecimal before;

        /** 交易後餘額，支援 12+4 小數位 */
        private BigDecimal balance;

        /** 幣別，例如 CNY */
        private String currency;

        /** 事件列表，當 endround 和 體彩 API 會回傳多個 mtcode */
        private List<Event> event;

        @Data
        @Builder
        public static class Target {
            /** 使用者帳號 */
            private String account;
        }

        @Data
        @Builder
        public static class Status {
            /** 交易開始時間 */
            private String createtime;

            /** 交易結束時間 */
            private String endtime;

            /** 交易狀態 (success, refund) ，交易失敗時一律回傳 1014 */
            private String status;

            /** 狀態編碼 */
            private String code;

            /** 狀態訊息 */
            private String message;

            /** 回傳時間 */
            private String datetime;
        }

        @Data
        @Builder
        public static class Event {
            /** 交易代碼，唯一不重複 */
            private String mtcode;

            /** 該筆交易的金額，支援 12+4 小數位 */
            private BigDecimal amount;

            /** 我方發送時間 */
            private String eventtime;
        }
    }

