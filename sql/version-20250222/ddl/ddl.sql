-- 新增编辑中，原有费率名称更改为“场馆负盈利费率”，新增“场馆有效流水费率”。列表字段原有“场馆费率”更改为“场馆负盈利费率”，新增“场馆有效流水费率”
ALTER TABLE site_venue ADD `valid_proportion` DECIMAL (10,2) DEFAULT NULL COMMENT '场馆有效流水费率' AFTER `handling_fee`;
ALTER TABLE `site_venue` MODIFY COLUMN `handling_fee` DECIMAL (10,2) NULL DEFAULT NULL COMMENT '场馆负盈利费率' AFTER `venue_code`;
ALTER TABLE `venue_info` MODIFY COLUMN `venue_proportion` DECIMAL (10,2) NULL DEFAULT NULL COMMENT '场馆负盈利费率' AFTER `h5_icon_i18n_code`;
ALTER TABLE venue_info ADD `proportion_type` INT DEFAULT NULL COMMENT '场馆费率类型:0:场馆负盈利费率,1:场馆有效流水费率,2:负盈利&有效流水费率' AFTER venue_proportion;
ALTER TABLE `venue_info` ADD COLUMN `valid_proportion` DECIMAL (10,2) NULL DEFAULT NULL COMMENT '场馆有效流水费率' AFTER `h5_icon_i18n_code`;
UPDATE `venue_info` SET `proportion_type`=0;

-- 代理提款设置相关
DELETE FROM agent_withdraw_config_detail;
ALTER TABLE agent_withdraw_config_detail MODIFY COLUMN fee_rate DECIMAL(20,2) COMMENT '百分比费率/固定金额';
ALTER TABLE agent_withdraw_config_detail ADD withdraw_way_id BIGINT DEFAULT NUll COMMENT '提款方式ID' AFTER large_withdraw_mark_amount;
ALTER TABLE agent_withdraw_config_detail ADD fee_type TINYINT(1) DEFAULT 0 COMMENT '代理手续费类型 0百分比 1固定金额 ' AFTER withdraw_way_id;
ALTER TABLE agent_withdraw_config_detail ADD withdraw_way_i18  varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '提款方式多语言'  AFTER withdraw_way_id;

-- 段位币种金额配置补充字段
ALTER TABLE site_vip_rank_currency_config ADD withdraw_way_id VARCHAR(50) NULL COMMENT '提款方式id';
ALTER TABLE site_vip_rank_currency_config ADD withdraw_fee_type INT NULL COMMENT '手续费类型,0.百分比,1.固定手续费';
-- 段位配置新增颜色
ALTER TABLE site_vip_rank ADD rank_color varchar(50) NULL COMMENT '段位颜色';
-- 人工出款补充字段
ALTER TABLE user_deposit_withdrawal ADD payout_type varchar(15) NULL COMMENT '出款方式类型(待出款审核使用)';
ALTER TABLE agent_deposit_withdrawal ADD payout_type varchar(15) NULL COMMENT '出款方式类型(待出款审核使用)';

-- 站点新增维护时间字段
ALTER TABLE site_info ADD maintenance_time_start BIGINT NULL COMMENT '维护时间-开始时间';
ALTER TABLE site_info ADD maintenance_time_end BIGINT NULL COMMENT '维护时间-结束时间';

-- 会员人工加减额新增汇率字段
ALTER TABLE user_manual_up_down_record ADD final_rate BIGINT NULL COMMENT '发起人工加减额时,对应的平台币转换汇率';

-- 充提类型增加---
INSERT INTO `system_recharge_type` ( `id`,`currency_code`, `recharge_code`, `recharge_type`, `recharge_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379523, 'CNY', 'manual_recharge', '人工存款', 'LOOKUP_10443', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_recharge_type` ( `id`,`currency_code`, `recharge_code`, `recharge_type`, `recharge_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379524, 'MYR', 'manual_recharge', '人工存款', 'LOOKUP_10443', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_recharge_type` ( `id`,`currency_code`, `recharge_code`, `recharge_type`, `recharge_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379525, 'KVND', 'manual_recharge', '人工存款', 'LOOKUP_10443', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_recharge_type` ( `id`,`currency_code`, `recharge_code`, `recharge_type`, `recharge_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379526, 'PHP', 'manual_recharge', '人工存款', 'LOOKUP_10443', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_recharge_type` ( `id`,`currency_code`, `recharge_code`, `recharge_type`, `recharge_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379527, 'USDT', 'manual_recharge', '人工存款', 'LOOKUP_10443', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_recharge_type` ( `id`,`currency_code`, `recharge_code`, `recharge_type`, `recharge_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379528, 'BRL', 'manual_recharge', '人工存款', 'LOOKUP_10443', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_recharge_type` ( `id`,`currency_code`, `recharge_code`, `recharge_type`, `recharge_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379529, 'GBP', 'manual_recharge', '人工存款', 'LOOKUP_10443', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_recharge_type` ( `id`,`currency_code`, `recharge_code`, `recharge_type`, `recharge_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379530, 'USD', 'manual_recharge', '人工存款', 'LOOKUP_10443', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_recharge_type` ( `id`,`currency_code`, `recharge_code`, `recharge_type`, `recharge_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379531, 'THB', 'manual_recharge', '人工存款', 'LOOKUP_10443', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_recharge_type` ( `id`,`currency_code`, `recharge_code`, `recharge_type`, `recharge_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379532, 'MVR', 'manual_recharge', '人工存款', 'LOOKUP_10443', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);


INSERT INTO `system_withdraw_type` ( `id`,`currency_code`, `withdraw_type_code`, `withdraw_type`, `withdraw_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379533, 'CNY', 'manual_withdraw', '人工提款', 'LOOKUP_10453', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_withdraw_type` ( `id`,`currency_code`, `withdraw_type_code`, `withdraw_type`, `withdraw_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379534, 'MYR', 'manual_withdraw', '人工提款', 'LOOKUP_10453', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_withdraw_type` ( `id`,`currency_code`, `withdraw_type_code`, `withdraw_type`, `withdraw_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379535, 'KVND', 'manual_withdraw', '人工提款', 'LOOKUP_10453', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_withdraw_type` ( `id`,`currency_code`, `withdraw_type_code`, `withdraw_type`, `withdraw_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379536, 'PHP', 'manual_withdraw', '人工提款', 'LOOKUP_10453', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_withdraw_type` ( `id`,`currency_code`, `withdraw_type_code`, `withdraw_type`, `withdraw_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379537, 'USDT', 'manual_withdraw', '人工提款', 'LOOKUP_10453', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_withdraw_type` ( `id`,`currency_code`, `withdraw_type_code`, `withdraw_type`, `withdraw_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379538, 'BRL', 'manual_withdraw', '人工提款', 'LOOKUP_10453', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_withdraw_type` ( `id`,`currency_code`, `withdraw_type_code`, `withdraw_type`, `withdraw_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379539, 'GBP', 'manual_withdraw', '人工提款', 'LOOKUP_10453', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_withdraw_type` ( `id`,`currency_code`, `withdraw_type_code`, `withdraw_type`, `withdraw_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379540, 'USD', 'manual_withdraw', '人工提款', 'LOOKUP_10453', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_withdraw_type` ( `id`,`currency_code`, `withdraw_type_code`, `withdraw_type`, `withdraw_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379541, 'THB', 'manual_withdraw', '人工提款', 'LOOKUP_10453', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);
INSERT INTO `system_withdraw_type` ( `id`,`currency_code`, `withdraw_type_code`, `withdraw_type`, `withdraw_type_i18`, `sort_order`, `memo`, `status`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1877246011366379542, 'MVR', 'manual_withdraw', '人工提款', 'LOOKUP_10453', 1, NULL, 1, 1727702080616, 1727702080616, 'superAdmin01', NULL);

-- 会员代理存提增加字段
ALTER TABLE agent_deposit_withdrawal MODIFY COLUMN fee_rate DECIMAL(20,2) COMMENT '百分比费率';
ALTER TABLE agent_deposit_withdrawal MODIFY COLUMN settlement_fee_rate DECIMAL(20,2) COMMENT '结算百分比费率';
ALTER TABLE user_deposit_withdrawal MODIFY COLUMN fee_rate DECIMAL(20,2) COMMENT '百分比费率';
ALTER TABLE user_deposit_withdrawal MODIFY COLUMN settlement_fee_rate DECIMAL(20,2) COMMENT '结算百分比费率';


ALTER TABLE system_recharge_way MODIFY COLUMN way_fee DECIMAL(20,2) DEFAULT 0 COMMENT '百分比费率 5 代表5%';

UPDATE system_recharge_way set fee_type = 0 ;

ALTER TABLE system_withdraw_way MODIFY COLUMN way_fee DECIMAL(20,2) DEFAULT 0 COMMENT '百分比费率 5 代表5%';

UPDATE system_withdraw_way set fee_type = 0 ;
ALTER TABLE user_deposit_withdrawal ADD fee_fixed_amount DECIMAL(20,2)  DEFAULT 0 COMMENT '会员手续费固定金额 ' AFTER fee_amount;


ALTER TABLE agent_deposit_withdrawal ADD fee_fixed_amount DECIMAL(20,2)  DEFAULT 0 COMMENT '代理手续费固定金额 ' AFTER fee_amount;

ALTER TABLE user_withdrawal_manual_record ADD trade_currency_amount decimal(26,8) DEFAULT NULL COMMENT '交易币种金额' AFTER apply_amount;

CREATE TABLE `agent_withdrawal_manual_record` (
                                                  `id` bigint NOT NULL,
                                                  `site_code` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
                                                  `agent_id` bigint DEFAULT NULL COMMENT '代理ID',
                                                  `agent_account` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '代理账号',
                                                  `deposit_withdraw_type_id` bigint DEFAULT NULL COMMENT '存取款类型id',
                                                  `deposit_withdraw_type_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '存取款类型CODE',
                                                  `deposit_withdraw_way_id` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '存取款方式id',
                                                  `deposit_withdraw_way` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '存取款方式',
                                                  `order_no` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '订单号',
                                                  `currency_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
                                                  `apply_amount` decimal(20,2) DEFAULT '0' COMMENT '申请金额',
                                                  `trade_currency_amount` decimal(26,8) DEFAULT NULL COMMENT '交易币种金额',
                                                  `arrive_amount` decimal(20,2) DEFAULT '0' COMMENT '实际到账金额',
                                                  `fee_type` tinyint(1) DEFAULT '0' COMMENT '会员手续费类型 0百分比 1固定金额 ',
                                                  `fee_rate` tinyint DEFAULT NULL COMMENT '百分比费率/固定金额',
                                                  `fee_amount` decimal(20,2) DEFAULT '0.0000' COMMENT '手续费',
                                                  `account_type` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账户类型（ 银行卡为银行名称，虚拟币为币种）',
                                                  `account_branch` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账户分支（银行卡为开户行，虚拟币为链协议 如ERC20 TRC20)',
                                                  `deposit_withdraw_address` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '存取款地址（银行卡账号，虚拟币地址）',
                                                  `deposit_withdraw_name` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '存取款名字',
                                                  `deposit_withdraw_surname` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '存取款姓',
                                                  `area_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '区号',
                                                  `telephone` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '手机号',
                                                  `email` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '邮箱',
                                                  `cpf` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'cpf',
                                                  `address` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '地址',
                                                  `province` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '省',
                                                  `city` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '城市',
                                                  `postal_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '邮政编码',
                                                  `country` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '国家',
                                                  `customer_status` char(1) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '状态 0处理中 1成功 2失败',
                                                  `file_key` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '出款凭证附件key',
                                                  `device_type` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '设备终端',
                                                  `remark` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备注',
                                                  `created_time` bigint DEFAULT NULL,
                                                  `updated_time` bigint DEFAULT NULL,
                                                  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                  `device_no` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '终端设备号',
                                                  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                                  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='会员人工提款信息';

ALTER TABLE `agent_venue_rate` ADD COLUMN `valid_rate` varchar(20) NULL COMMENT '有效流水费率' AFTER `rate`;

