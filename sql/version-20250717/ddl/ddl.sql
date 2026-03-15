


ALTER TABLE `site_recharge_channel`  ADD COLUMN `recv_user_name` varchar(128) NULL COMMENT '姓名/电子钱包姓名' AFTER `sort_order`;
ALTER TABLE `site_recharge_channel`  ADD COLUMN `recv_bank_code` varchar(128) NULL COMMENT '银行编码' AFTER `recv_user_name`;
ALTER TABLE `site_recharge_channel`  ADD COLUMN `recv_bank_name` varchar(255) NULL COMMENT '银行名称' AFTER `recv_bank_code`;
ALTER TABLE `site_recharge_channel`  ADD COLUMN `recv_bank_branch` varchar(255) NULL COMMENT '开户行' AFTER `recv_bank_name`;
ALTER TABLE `site_recharge_channel`  ADD COLUMN `recv_bank_card` varchar(128) NULL COMMENT '银行帐号/电子钱包地址/虚拟币地址' AFTER `recv_bank_branch`;
ALTER TABLE `site_recharge_channel`  ADD COLUMN `recv_bank_account` varchar(128) NULL COMMENT '电子钱包账户' AFTER `recv_bank_card`;
ALTER TABLE `site_recharge_channel`  ADD COLUMN `recv_qr_code` varchar(512) NULL COMMENT '收款码' AFTER `recv_bank_account`;


ALTER TABLE `user_deposit_withdrawal`  ADD COLUMN `recv_user_name` varchar(128) NULL COMMENT '收款姓名/电子钱包姓名' AFTER `user_label_id`;
ALTER TABLE `user_deposit_withdrawal`  ADD COLUMN `recv_bank_code` varchar(128) NULL COMMENT '收款银行编码' AFTER `recv_user_name`;
ALTER TABLE `user_deposit_withdrawal`  ADD COLUMN `recv_bank_name` varchar(255) NULL COMMENT '收款银行名称' AFTER `recv_bank_code`;
ALTER TABLE `user_deposit_withdrawal`  ADD COLUMN `recv_bank_branch` varchar(255) NULL COMMENT '收款开户行' AFTER `recv_bank_name`;
ALTER TABLE `user_deposit_withdrawal`  ADD COLUMN `recv_bank_account` varchar(128) NULL COMMENT '收款电子钱包账户' AFTER `recv_bank_branch`;
ALTER TABLE `user_deposit_withdrawal`  ADD COLUMN `recv_qr_code` varchar(512) NULL COMMENT '收款码' AFTER `recv_bank_account`;


ALTER TABLE `agent_deposit_withdrawal`  ADD COLUMN `recv_user_name` varchar(128) NULL COMMENT '收款姓名/电子钱包姓名' AFTER `payout_type`;
ALTER TABLE `agent_deposit_withdrawal`  ADD COLUMN `recv_bank_code` varchar(128) NULL COMMENT '收款银行编码' AFTER `recv_user_name`;
ALTER TABLE `agent_deposit_withdrawal`  ADD COLUMN `recv_bank_name` varchar(255) NULL COMMENT '收款银行名称' AFTER `recv_bank_code`;
ALTER TABLE `agent_deposit_withdrawal`  ADD COLUMN `recv_bank_branch` varchar(255) NULL COMMENT '收款开户行' AFTER `recv_bank_name`;
ALTER TABLE `agent_deposit_withdrawal`  ADD COLUMN `recv_bank_account` varchar(128) NULL COMMENT '收款电子钱包账户' AFTER `recv_bank_branch`;
ALTER TABLE `agent_deposit_withdrawal`  ADD COLUMN `recv_qr_code` varchar(512) NULL COMMENT '收款码' AFTER `recv_bank_account`;


ALTER TABLE user_deposit_withdrawal MODIFY COLUMN   `file_key` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '进出款凭证附件key';
ALTER TABLE agent_deposit_withdrawal MODIFY COLUMN   `file_key` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '进出款凭证附件key';

ALTER TABLE user_withdrawal_manual_record MODIFY COLUMN   `file_key` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '进出款凭证附件key';
ALTER TABLE agent_withdrawal_manual_record MODIFY COLUMN   `file_key` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '进出款凭证附件key';

CREATE TABLE `nextspin_transaction_record` (
  `id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '交易记录编号',
  `account` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '用户账号',
  `transfer_id` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '注單號',
  `return_number` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '返回交易号',
  `request_json` varchar(2000) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '请求入参',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
  KEY `idx_ti` (`transfer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='nextspin交易记录表';




DROP TABLE sport_events_recommend;

CREATE TABLE `sport_events_recommend` (
                                          `id` bigint NOT NULL COMMENT 'ID',
                                          `league_id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '联赛ID',
                                          `league_name` varchar(200) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '联赛名称',
                                          `events_id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '赛事ID',
                                          `events_code` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '赛事CODE',
                                          `team_name` varchar(200) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '主队VS客队',
                                          `home_name` varchar(200) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '主队名称',
                                          `home_id` varchar(200) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '主队id',
                                          `away_id` varchar(200) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '客队id',
                                          `away_name` varchar(200) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '客队名称	',
                                          `start_time` bigint DEFAULT NULL COMMENT '开赛-开始时间',
                                          `end_time` bigint DEFAULT NULL COMMENT '开赛-结束时间',
                                          `text_info` text COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '原生报文',
                                          `sport_type` int NOT NULL COMMENT '体育项目ID:1: 足球。2: 篮球。3: 美式足球。4: 冰上曲棍球。9: 羽毛球。24: 手球。26: 橄榄球。43: 电子竞技',
                                          `sport_name` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '体育项目名称',
                                          `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                          `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                          `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                          UNIQUE KEY `uk_events_id` (`events_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='体育赛事推荐';


CREATE TABLE `site_sport_events_recommend` (
                                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                               `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点CODE',
                                               `venue_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏场馆CODE',
                                               `sport_recommend_id` bigint DEFAULT NULL COMMENT 'sport_events_recommend.id',
                                               `league_id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '联赛ID',
                                               `events_id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '赛事ID',
                                               `sport_type` int NOT NULL COMMENT '体育项目ID:1: 足球。2: 篮球。3: 美式足球。4: 冰上曲棍球。9: 羽毛球。24: 手球。26: 橄榄球。43: 电子竞技',
                                               `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                               `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                               `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                               `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                               PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                               UNIQUE KEY `uk_site_code_events_info_id` (`site_code`,`sport_recommend_id`),
                                               UNIQUE KEY `uk_site_code_events_id` (`site_code`,`events_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin  COMMENT='站点-体育赛事热门推荐';