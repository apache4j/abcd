CREATE TABLE `agent_commission_venue_report` (
                                                 `id` bigint NOT NULL COMMENT 'ID',
                                                 `site_code` varchar(32) NOT NULL COMMENT '站点编码',
                                                 `agent_id` varchar(32) NOT NULL COMMENT '代理ID',
                                                 `venue_type` int NOT NULL COMMENT '场馆类型(对应枚举)',
                                                 `commission_type` tinyint NOT NULL COMMENT '佣金类型(0-直属会员 1-下级代理)',
                                                 `plan_rate` decimal(10,4) NOT NULL DEFAULT '0' COMMENT '佣金比例',
                                                 `diff_rate` decimal(10,4) DEFAULT '0' COMMENT '佣金极差比例',
                                                 `commission_amount` decimal(18,4) NOT NULL DEFAULT '0' COMMENT '佣金金额',
                                                 `valid_amount` decimal(18,4) NOT NULL DEFAULT '0' COMMENT '有效流水',
                                                 `start_time` bigint DEFAULT NULL COMMENT '统计开始时间',
                                                 `end_time` bigint DEFAULT NULL COMMENT '统计结束时间',
                                                 `apply_time` bigint DEFAULT NULL COMMENT '申请时间',
                                                 PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='代理佣金-场馆维度返点报表';


CREATE TABLE `agent_commission_plan_turnover` (
  `id` varchar(64) NOT NULL COMMENT '主键，业务生成',
  `site_code` varchar(50) NOT NULL COMMENT '站点编码',
  `plan_code` varchar(50) NOT NULL COMMENT '方案编码',
  `plan_name` varchar(100) NOT NULL COMMENT '方案名称',
  `remark` varchar(2000) DEFAULT NULL COMMENT '备注',
  `creator` varchar(50) DEFAULT NULL,
  `created_time` bigint DEFAULT NULL,
  `updater` varchar(50) DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_code` (`plan_code`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='有效流水佣金方案主表';


CREATE TABLE `agent_commission_plan_turnover_config` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `plan_code` varchar(50) NOT NULL COMMENT '方案编码',
  `venue_type` int NOT NULL COMMENT '游戏类型',
  `currency` varchar(10) NOT NULL COMMENT '币种',
  `tier_num` int NOT NULL COMMENT '等级',
  `bet_amount` decimal(20,2) NOT NULL COMMENT '有效投注',
  `rate` decimal(10,4) NOT NULL COMMENT '返佣比例',
  `creator` varchar(50) DEFAULT NULL,
  `created_time` bigint DEFAULT NULL,
  `updater` varchar(50) DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_venue_currency_bet`
    (`plan_code`,`venue_type`,`currency`,`bet_amount`),
  KEY `idx_plan_code` (`plan_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='有效流水佣金方案配置表';


INSERT INTO `sms_channel_config` (`id`, `address`, `address_code`, `area_code`, `channel_id`, `channel_name`, `channel_code`, `platform_code`, `auth_count`, `status`, `host`, `user_id`, `user_account`, `password`, `template`, `remark`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('90000', '越南', 'VN', '84', '90001', 'BUKA', 'VNBUKA', 'oKVNcU6J', 0, 0, 'https://api.onbuka.com/v3', '1000', 'dBfsTLc6', 'ipxI9FZ1', 'Your verification code is %s', NULL, 1723708677193, 1739940084342, NULL, 'feng01');
INSERT INTO `sms_channel_config` (`id`, `address`, `address_code`, `area_code`, `channel_id`, `channel_name`, `channel_code`, `platform_code`, `auth_count`, `status`, `host`, `user_id`, `user_account`, `password`, `template`, `remark`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('90002', '印尼', 'ID', '62', '90002', 'BUKA', 'IDBUKA', 'oKVNcU6J', 0, 0, 'https://api.onbuka.com/v3', '1000', 'dBfsTLc6', 'ipxI9FZ1', 'Your verification code is %s', NULL, 1723708677193, 1739940084342, NULL, 'feng01');

INSERT INTO `dev_bwintl`.`mail_channel_config` (`id`, `channel_id`, `channel_name`, `channel_code`, `auth_count`, `host`, `port`, `user_id`, `user_account`, `password`, `sender`, `api_key`, `status`, `template`, `remark`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('10001', '10002', 'MAILER邮件', 'MAILER', 0, 'http://websiteapi.ssycloud.com', NULL, NULL, 'skyboy', NULL, NULL, 'mlsn.e79e65532c26cc5ea1ed2e3238d73c46afe5eb4428c792a5b3a314227b8c8e46', 1, 'vywj2lpke6pg7oqz', NULL, NULL, 1769756624028, NULL, 'jerry02');