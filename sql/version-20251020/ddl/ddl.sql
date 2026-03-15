ALTER TABLE system_activity_template ADD COLUMN `handicap_mode` int DEFAULT 0 COMMENT '盘口模式 0:国际盘 1:大陆盘' AFTER activity_template;
ALTER TABLE site_info ADD COLUMN `handicap_mode` int DEFAULT 0 COMMENT '盘口模式 0:国际盘 1:大陆盘';
ALTER TABLE skin_info ADD COLUMN `handicap_mode` int DEFAULT 0 COMMENT '盘口模式 0:国际盘 1:大陆盘';

CREATE TABLE `user_receive_account` (
                                        `id` bigint unsigned NOT NULL COMMENT '主键ID',
                                        `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点代码',
                                        `user_id` varchar(10) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员id',
                                        `user_account` varchar(100) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员账号',
                                        `receive_type` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币',
                                        `surname` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '银行卡/电子钱包姓名',
                                        `user_email` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '邮箱',
                                        `area_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '手机区号',
                                        `user_phone` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '银行卡/电子钱包手机号',
                                        `bank_name` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '银行名称',
                                        `bank_branch` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '开户行',
                                        `bank_code` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '银行代码',
                                        `bank_card` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '银行帐号',
                                        `ifsc_code` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'ifsc码',
                                        `province_name` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '省份',
                                        `city_name` varchar(512) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '城市',
                                        `detail_address` varchar(512) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '城市详细地址',
                                        `electronic_wallet_account` varchar(512) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '电子钱包账户',
                                        `electronic_wallet_name` varchar(512) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '电子钱包名称',
                                        `network_type` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '链网络类型',
                                        `address_no` varchar(256) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '电子钱包/加密货币收款地址',
                                        `binding_status` int DEFAULT NULL COMMENT '绑定状态 0未绑定 1绑定中',
                                        `risk_control_level_id` bigint DEFAULT NULL COMMENT '风控层级id',
                                        `risk_control_level` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '风控层级',
                                        `default_flag` int DEFAULT NULL COMMENT '是否默认 0否 1是',
                                        `remark` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备注',
                                        `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                        `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
                                        `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                        `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                        PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='会员收款信息';


-- site_activity_order_record

CREATE TABLE `site_activity_order_record_v2` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `activity_name_i18n_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动名称-多语言',
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点编码',
  `order_no` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '订单号',
  `activity_id` bigint DEFAULT NULL COMMENT '所属活动',
  `activity_template` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动模板',
  `activity_no` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动编号',
  `user_id` varchar(10) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员id',
  `user_name` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '姓名',
  `user_account` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账号',
  `account_type` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账号类型 1-测试 2-正式',
  `super_agent_id` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '代理账号',
  `vip_grade_code` int DEFAULT NULL COMMENT 'VIP等级',
  `vip_rank_code` int DEFAULT NULL COMMENT 'vip段位code',
  `distribution_type` int DEFAULT NULL COMMENT '派发方式: 0:玩家自领-过期作废，1:玩家自领-过期自动派发，2:立即派发',
  `receive_start_time` bigint DEFAULT NULL COMMENT '可领取开始时间',
  `receive_end_time` bigint DEFAULT NULL COMMENT '可领取结束时间',
  `receive_status` int DEFAULT NULL COMMENT '领取状态',
  `final_rate` decimal(65,2) DEFAULT NULL COMMENT '发放礼金时的汇率',
  `activity_amount` decimal(22,4) DEFAULT NULL COMMENT '活动赠送金额',
  `plat_activity_amount` decimal(22,4) DEFAULT NULL COMMENT '赠送金额-转成平台币金额-用于报表',
  `currency_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
  `principal_amount` decimal(12,2) DEFAULT NULL COMMENT '本金',
  `running_water_multiple` decimal(15,2) DEFAULT NULL COMMENT '流水倍数',
  `running_water` decimal(22,4) DEFAULT NULL COMMENT '流水要求',
  `remark` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备注',
  `receive_time` bigint DEFAULT NULL COMMENT '领取时间',
  `device_no` varchar(80) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '领取时用户-设备号',
  `ip` varchar(80) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '领取时用户-ip',
  `redbag_session_id` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '红包雨场次id',
  `reward_rank` int DEFAULT NULL COMMENT '奖品vip段位',
  `prize_type` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '奖品类型',
  `prize_name` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '奖品名称',
  `created_time` bigint DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_account` (`user_id`,`activity_id`) COMMENT '联合索引',
  KEY `idx_ip` (`ip`) COMMENT 'IP索引',
  KEY `inx_status` (`receive_status`) COMMENT '领取状态索引',
  UNIQUE KEY `order_no_index` (`order_no`) COMMENT '活动订单不论是什么站点必须唯一',
  KEY `idx_site_activity_order_record` (`site_code`,`user_account`,`receive_status`,`created_time`,`receive_time`),
  KEY `idx_activity_code_tatus_sessionid` (`redbag_session_id`,`site_code`,`receive_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='会员活动奖励记录-v2';


ALTER TABLE user_deposit_withdrawal ADD COLUMN `electronic_wallet_name` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '钱包名称' AFTER ifsc_code;
ALTER TABLE agent_deposit_withdrawal ADD COLUMN `electronic_wallet_name` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '钱包名称' AFTER ifsc_code;

ALTER TABLE user_withdrawal_manual_record ADD COLUMN `electronic_wallet_name` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '钱包名称' AFTER ifsc_code;
ALTER TABLE agent_withdrawal_manual_record ADD COLUMN `electronic_wallet_name` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '钱包名称' AFTER ifsc_code;



ALTER TABLE  system_recharge_channel ADD COLUMN   `vip_grade_use_scope` varchar(512) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'VIP等级使用范围';

ALTER TABLE  site_recharge_way ADD COLUMN   `vip_grade_use_scope` varchar(512) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'VIP等级使用范围';

ALTER TABLE  site_recharge_channel ADD COLUMN   `vip_grade_use_scope` varchar(512) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'VIP等级使用范围';

CREATE TABLE `site_vip_option` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `site_code` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `vip_upgrade_exp` decimal(20,4) DEFAULT NULL COMMENT '升级流水经验',
  `currency_code` varchar(100) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '货币代码',
  `vip_grade_code` int DEFAULT NULL COMMENT 'vip等级code',
  `vip_grade_name` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'vip等级名称',
  `vip_icon` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'vip图标地址',
  `relegation_amount` decimal(20,4) DEFAULT NULL COMMENT '保级流水',
  `relegation_days` int DEFAULT NULL COMMENT '保级天数',
  `promotion_bonus` decimal(20,4) DEFAULT NULL COMMENT '晋级礼金',
  `promotion_bonus_multiple` decimal(10,2) DEFAULT NULL COMMENT '晋级礼金流水倍数',
  `week_bonus` decimal(20,4) DEFAULT NULL COMMENT '周红包金额',
  `week_bonus_type` tinyint DEFAULT NULL COMMENT '周红包类型 0:先发后打 1:先打后发',
  `week_bonus_amount_multiple` decimal(10,2) DEFAULT NULL COMMENT '周红包流水倍数',
  `week_bonus_amount_total` decimal(10,2) DEFAULT NULL COMMENT '周红包流水总金额',
  `age_amount` decimal(20,4) DEFAULT NULL COMMENT '每年生日红包',
  `age_amount_multiple` decimal(20,4) DEFAULT NULL COMMENT '每年生日红包流水倍数',
  `daily_withdrawal_free_num` int DEFAULT NULL COMMENT '单日免费提款次数',
  `daily_withdrawal_num_limit` int DEFAULT NULL COMMENT '单日提款次数上限',
  `daily_withdrawal_free_amount_limit` decimal(10,2) DEFAULT NULL COMMENT '单日免费提款金额',
  `daily_withdraw_amount_limit` decimal(20,2) DEFAULT NULL COMMENT '单日提款额度上限',
  `encry_coin_fee` tinyint DEFAULT NULL COMMENT '是否有加密货币提款手续费(0:没有,1:有)',
  `rebate_config` int DEFAULT NULL COMMENT '是否日反水0:没有,1:有',
  `remark` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备注',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `deposit_amount_limit` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '存款快捷金额,字符串逗号分隔',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `unique_sc_cc_vc` (`site_code`,`currency_code`,`vip_grade_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin AUTO_INCREMENT=1976840091062244659 COMMENT='站点VIP大陆盘等级配置';

CREATE TABLE `site_vip_option_currency_config` (
  `id` bigint NOT NULL COMMENT '主键',
  `site_vip_option_id` bigint NOT NULL COMMENT 'site_vip_option外键id',
  `withdraw_fee` decimal(10,2) DEFAULT '0' COMMENT '提现手续费',
  `withdraw_fee_type` int DEFAULT NULL COMMENT '手续费类型,0.百分比,1.固定手续费',
  `withdraw_way_id` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '提款方式id',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='站点vip等级大陆盘币种信息配置表';

CREATE TABLE `user_vip_flow_record_cn` (
  `id` bigint NOT NULL COMMENT '主键id',
  `user_id` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员ID全局唯一',
  `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `user_account` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账户',
  `vip_grade_code` int DEFAULT NULL COMMENT '当前VIP等级code',
  `next_vip_grade_code` int DEFAULT NULL COMMENT 'VIP升级后等级code',
  `bet_amount_exe` decimal(22,4) DEFAULT NULL COMMENT '单次有效流水金额',
  `finish_bet_amount` decimal(22,4) DEFAULT NULL COMMENT '当前VIP流水金额',
  `upgrade_bet_amount` decimal(22,4) DEFAULT NULL COMMENT 'VIP升级总流水金额',
  `finish_relegation_amount` decimal(22,4) DEFAULT NULL COMMENT '保级流水金额',
  `grade_relegation_amount` decimal(22,4) DEFAULT NULL COMMENT '保级总流水金额',
  `relegation_days` int DEFAULT NULL COMMENT '保级天数',
  `up_vip_time` varchar(12) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '升级到VIP等级的初始时间yyyy-mm-dd',
  `relegation_days_time` varchar(12) COLLATE utf8mb4_0900_bin DEFAULT '0' COMMENT '降级时间',
  `currency_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
  KEY `ix_create_time` (`created_time`),
  KEY `ix_user_account` (`user_id`,`vip_grade_code`),
  KEY `idx_ui_ct_sc` (`user_id`,`created_time`,`site_code`),
  KEY `idx_rdt` (`relegation_days_time`),
  KEY `idx_cc` (`currency_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='VIP大陆盘流水记录';


CREATE TABLE `site_vip_change_record_cn` (
  `id` varchar(20) COLLATE utf8mb4_0900_bin NOT NULL,
  `site_code` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `change_type` int DEFAULT NULL COMMENT '升降级标识(0:升级,1:j降级)',
  `user_id` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员id',
  `user_account` varchar(100) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员账号',
  `account_type` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账号类型 1-测试 2-正式',
  `account_status` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账号状态',
  `user_label_id` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员标签id',
  `user_label` varchar(1000) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员标签名称',
  `user_risk_level_id` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员风控层级id',
  `user_risk_level` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员风控层级',
  `vip_old` int DEFAULT NULL COMMENT '变更前VIP段位',
  `vip_now` int DEFAULT NULL COMMENT '变更后VIP段位',
  `is_artificial_change` int DEFAULT '0' COMMENT '是否人工调级0自动升级 1人工调级',
  `up_vip_time` varchar(12) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'vip升级时间',
  `change_time` bigint DEFAULT NULL COMMENT '变更时间',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='VIP大陆盘升降级记录';

ALTER TABLE user_withdraw_config ADD COLUMN `vip_grade_code` int DEFAULT NULL COMMENT 'VIP等级code';

ALTER TABLE user_withdraw_config_detail ADD COLUMN `vip_grade_code` int DEFAULT NULL COMMENT 'VIP等级code';

ALTER TABLE  user_withdraw_config MODIFY COLUMN   `single_day_withdraw_count` int DEFAULT NULL COMMENT '单日免费次数';
ALTER TABLE  user_withdraw_config MODIFY COLUMN    `single_max_withdraw_amount` decimal(20,2) DEFAULT NULL COMMENT '单日免费额度';


ALTER TABLE `user_info`
    ADD COLUMN `auth_status` tinyint DEFAULT '0' COMMENT '身份验证 0-未验证 1-验证' ;


ALTER TABLE  agent_deposit_withdrawal MODIFY COLUMN   pay_third_url VARCHAR(4000) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '存取三方支付URL'




-- site_vip_award_record definition

CREATE TABLE `site_vip_award_record_v2` (
  `id` bigint NOT NULL COMMENT '主键id',
  `site_code` varchar(40) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `order_id` varchar(40) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '订单号',
  `award_type` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '奖励类型(0:升级礼金，1:1号奖励,2:16号奖励)',
  `currency` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
  `award_amount` decimal(10,2) DEFAULT NULL COMMENT '奖励金额',
  `agent_id` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '上级代理id',
  `agent_account` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '代理账号',
  `receive_type` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '领取方式(0:手动领取,1:自动领取)',
  `receive_status` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '领取状态(0:未领取,1:已领取,2:已过期)',
  `user_id` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员id',
  `user_account` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账号',
  `vip_grade_code` int DEFAULT NULL COMMENT 'VIP等级',
  `vip_rank_code` int DEFAULT NULL COMMENT 'VIP段位code',
  `account_type` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账号类型',
  `record_start_time` bigint DEFAULT NULL COMMENT '统计开始时间',
  `record_end_time` bigint DEFAULT NULL COMMENT '统计结束时间',
  `receive_time` bigint DEFAULT NULL COMMENT '领取时间',
  `expired_time` bigint DEFAULT NULL COMMENT '过期时间',
  `creator` bigint DEFAULT NULL COMMENT '创建人',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updater` bigint DEFAULT NULL COMMENT '更新人',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_receive` (`user_id`,`receive_status`),
  UNIQUE KEY `ix_order_id` (`order_id`),
  KEY `ix_create_time` (`created_time`),
  KEY `ix_receive_time_type` (`receive_time`,`award_type`),
  KEY `idx_site_vip_award_record` (`site_code`,`user_account`,`receive_status`,`record_start_time`),
  KEY `idx_receive_expired` (`expired_time`,`receive_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='VIP奖励发放记录-v2';

-- 增加字段
ALTER TABLE site_vip_award_record_v2 ADD COLUMN require_typing_amount VARCHAR(50) COMMENT '需要完成的打码量';


ALTER TABLE system_recharge_channel ADD COLUMN `ext_param` VARCHAR(200) DEFAULT NULL COMMENT '扩展参数';
ALTER TABLE system_withdraw_channel ADD COLUMN `ext_param` VARCHAR(200) DEFAULT NULL COMMENT '扩展参数';




ALTER TABLE user_account_update_review ADD COLUMN ext_param VARCHAR(200) DEFAULT NULL COMMENT '扩展参数';










CREATE TABLE `site_venue_config` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                     `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏场馆CODE',
                                     `venue_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏场馆CODE',
                                     `venue_desc` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '场馆描述',
                                     `middle_icon_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '皮肤4:中等图-多语言',
                                     `venue_name` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏场馆名称',
                                     `pc_background_code` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'pc_场馆背景图',
                                     `pc_logo_code` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'pc_场馆LOGO',
                                     `icon_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '图片多语言',
                                     `pc_icon_i18n_code` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'pc_场馆图标-多语言',
                                     `h5_icon_i18n_code` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'H5_场馆图标-多语言',
                                     `ht_icon_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏横版图标-多语言',
                                     `small_icon1_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '小图标1-多语言',
                                     `small_icon2_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '小图标2-多语言',
                                     `small_icon3_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '小图标3-多语言',
                                     `small_icon4_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '小图标4-多语言',
                                     `small_icon5_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '小图标5-多语言',
                                     `small_icon6_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '小图标6-多语言',
                                     `creator_name` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人',
                                     `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                     `updater_name` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '更新人名称',
                                     `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                     `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                     `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                     PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                     UNIQUE KEY `uk_site_code_venue_code` (`site_code`,`venue_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='皮肤4:站点场馆配置';


ALTER TABLE venue_info ADD `ht_icon_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '皮肤4:游戏横版图标-多语言' AFTER `h5_icon_i18n_code`;
ALTER TABLE venue_info ADD `middle_icon_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '皮肤4:中等图-多语言' AFTER `ht_icon_i18n_code`;
ALTER TABLE venue_info ADD `small_icon1_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '皮肤4:小图标1-多语言' AFTER `ht_icon_i18n_code`;
ALTER TABLE venue_info ADD `small_icon2_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '皮肤4:小图标2-多语言' AFTER `small_icon1_i18n_code`;
ALTER TABLE venue_info ADD `small_icon3_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '皮肤4:小图标3-多语言' AFTER `small_icon2_i18n_code`;
ALTER TABLE venue_info ADD `small_icon4_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '皮肤4:小图标4-多语言' AFTER `small_icon3_i18n_code`;
ALTER TABLE venue_info ADD `small_icon5_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '皮肤4:小图标5-多语言' AFTER `small_icon4_i18n_code`;
ALTER TABLE venue_info ADD `small_icon6_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '皮肤4:小图标6-多语言' AFTER `small_icon5_i18n_code`;



ALTER TABLE game_one_class_info ADD `prize_pool_total` decimal(26,6) DEFAULT NULL COMMENT '皮肤4:国内盘字段:奖金池' AFTER `home_sort`;
ALTER TABLE game_one_class_info ADD `prize_pool_start` decimal(26,6) DEFAULT NULL COMMENT '皮肤4:国内盘字段:奖金池开始金额' AFTER `prize_pool_total`;
ALTER TABLE game_one_class_info ADD `prize_pool_end` decimal(26,6) DEFAULT NULL COMMENT '皮肤4:国内盘字段:奖金池结束金额' AFTER `prize_pool_start`;
ALTER TABLE game_one_class_info ADD `rebate_venue_type`  int DEFAULT NULL COMMENT '皮肤4:返水场馆类型标签' AFTER `prize_pool_end`;


ALTER TABLE game_two_class_info ADD `ht_icon_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '皮肤4:横图-多语言' AFTER `type_i18n_code`;





ALTER TABLE `site_banner_config`  MODIFY COLUMN `game_one_class_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '展示位置(大于0=一级分类ID,0:首页,-1:皮肤四,我的,-2:皮肤四,优惠活动)' AFTER `site_code`;

ALTER TABLE `game_one_class_info` add `icon2`  varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '一级分类侧边栏图片2';


CREATE TABLE `game_one_float_config` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                         `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点',
                                         `game_one_id` bigint NOT NULL COMMENT '一级分类,geme_one_class_info.id',
                                         `game_two_id` bigint DEFAULT NULL COMMENT '二级分类,geme_two_class_info.id',
                                         `venue_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '场馆',
                                         `model` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'CA:多游戏,SBA:沙巴体育,LOTTERY:彩票,SIGN_VENUE:单场馆',
                                         `float_name_i18n_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '悬浮名称-多语言',
                                         `logo_icon_i18n_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '品牌图标-多语言',
                                         `medium_icon_i18n_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '中图标-多语言',
                                         `status` int DEFAULT '1' COMMENT '状态（ 1 开启中 2 维护中 3 已禁用）',
                                         `remark` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT '' COMMENT '备注',
                                         `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                         `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                         `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                         `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                         PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin  COMMENT='一级分类悬浮表';





CREATE TABLE `db_sport_transfer_record` (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `venue_code` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '场馆',
                                            `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
                                            `user_id` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员id',
                                            `trans_id` varchar(50) COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '转账ID',
                                            `bet_id` varchar(50) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '下注订单号',
                                            `order_status` int NOT NULL COMMENT '订单状态: 1:投注(扣款),2:结算派彩(加款),3:注单取消(加款),4:注单取消回滚(扣款),5:结算回滚(扣款),6:拒单 (加款),9:提前部分结算（加款）,10:提前全额结算（加款）,11:提前结算取消（扣款),12:提前结算取消回滚（加款）,13:人工加款（加款）,14:人工扣款 (扣款),20:用户预约下注(扣款),21:用户预约投注取消（加款）',
                                            `transfer_type` int NOT NULL COMMENT '1:加款,2:扣款',
                                            `amount` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
                                            `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                            `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                            `remark` varchar(1024) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备注',
                                            `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                            `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                            PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='DB场馆转账记录';


CREATE TABLE `db_sport_transfer_record_detail` (
                                                   `id` bigint NOT NULL COMMENT 'ID',
                                                   `venue_code` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '场馆',
                                                   `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
                                                   `user_id` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员id',
                                                   `bet_id` varchar(50) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '下注订单号',
                                                   `order_status` int NOT NULL COMMENT '订单状态: 1:投注(扣款),2:结算派彩(加款),3:注单取消(加款),4:注单取消回滚(扣款),5:结算回滚(扣款),6:拒单 (加款),9:提前部分结算（加款）,10:提前全额结算（加款）,11:提前结算取消（扣款),12:提前结算取消回滚（加款）,13:人工加款（加款）,14:人工扣款 (扣款),20:用户预约下注(扣款),21:用户预约投注取消（加款）',
                                                   `amount` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
                                                   `type` int NOT NULL COMMENT '0:未确认,1:已确认,2:已取消',
                                                   `transfer_type` int NOT NULL COMMENT '1:加款,2:扣款',
                                                   `settle_count` int DEFAULT '0' COMMENT '累计加款结算次数',
                                                   `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                                   `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                                   `remark` varchar(1024) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备注',
                                                   `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                   `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                   PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                                   UNIQUE KEY `uk_bet_id` (`bet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='DB场馆转账子单记录';

ALTER TABLE `risk_ctrl_black_account`
    ADD COLUMN `risk_control_account_name` varchar(255) NULL COMMENT '黑名单账号名称:银行卡,虚拟币,电子钱包' ;


ALTER TABLE `site_risk_ctrl_black_account`
    ADD COLUMN `risk_control_account_name` varchar(255) NULL COMMENT '黑名单账号名称:银行卡,虚拟币,电子钱包' ;

ALTER TABLE `user_info` DROP INDEX `uk_site_code_phone`;
ALTER TABLE `user_info`  ADD UNIQUE INDEX `uk_site_code_phone`( `phone` ASC, `area_code` ASC,`site_code` ASC) USING BTREE;

ALTER TABLE `site_rebate_reward_record` ADD COLUMN `currency_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '货币代码' ;
ALTER TABLE `site_rebate_reward_record` ADD COLUMN `site_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点代码' ;


ALTER TABLE `user_rebate_record` ADD COLUMN `expect_rebate_amount` decimal(24, 4) NULL DEFAULT NULL COMMENT '预计返水总额' AFTER `creator`;

ALTER TABLE `user_rebate_venue_record` ADD COLUMN `act_rebate_amount` decimal(24, 4) NULL DEFAULT NULL COMMENT '实际返水金额' AFTER `status`;



ALTER TABLE `site_activity_order_record`
    ADD COLUMN `show_flag` tinyint  DEFAULT 1 COMMENT '1-展示，0不展示' ;

ALTER TABLE `site_activity_order_record_v2`
    ADD COLUMN `show_flag` tinyint  DEFAULT 1 COMMENT '1-展示，0不展示' ;


    ALTER TABLE `user_rebate_record`  ADD COLUMN `day_millis` bigint NULL DEFAULT NULL COMMENT '站点日期 当天起始时间戳' ;
    ALTER TABLE `user_rebate_record`  ADD COLUMN `day_str` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT 'day字段对应的字符串(此字段只用于表查看，不涉及任何代码关联)';

     ALTER TABLE `user_rebate_venue_record`  ADD COLUMN `day_millis` bigint NULL DEFAULT NULL COMMENT '站点日期 当天起始时间戳' ;
        ALTER TABLE `user_rebate_venue_record`  ADD COLUMN `day_str` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT 'day字段对应的字符串(此字段只用于表查看，不涉及任何代码关联)';



ALTER TABLE site_rebate_config ADD COLUMN currency_code VARCHAR(50) AFTER site_code;

ALTER TABLE site_rebate_config  CHANGE vip_rank_code vip_grade_code INT NULL COMMENT 'VIP等级编码';
ALTER TABLE site_rebate_config  CHANGE vip_rank_name vip_grade_name VARCHAR(100) NULL COMMENT 'VIP等级名称';



-- 商务信息
CREATE TABLE `site_business_basic_info` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                            `site_code` varchar(64) DEFAULT NULL COMMENT '站点编码',
                                            `phone` varchar(50) DEFAULT NULL COMMENT '电话',
                                            `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                                            `telegram` varchar(100) DEFAULT NULL COMMENT 'Telegram',
                                            `wechat` varchar(100) DEFAULT NULL COMMENT '微信',
                                            `qq` varchar(50) DEFAULT NULL COMMENT 'QQ',
                                            `whats_app` varchar(100) DEFAULT NULL COMMENT 'WhatsApp',
                                            `messenger` varchar(100) DEFAULT NULL COMMENT 'Messenger',
                                            `business_name` varchar(255) DEFAULT NULL COMMENT '多语言商务名称',
                                            `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                            `updater` varchar(64) DEFAULT NULL COMMENT '更新人',
                                            `h5_icon` varchar(255) DEFAULT NULL,
                                            `pc_icon` varchar(255) DEFAULT NULL,
                                            `sort` int DEFAULT NULL,
                                            PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='站点商务基本信息';


-- site_activity_base

CREATE TABLE `site_activity_base_v2` (
  `id` bigint NOT NULL COMMENT '主键id',
  `xxl_job_id` bigint DEFAULT NULL COMMENT '任务ID',
  `activity_no` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动编号',
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `activity_name_i18n_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动名称-多语言',
  `label_id` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动分类-活动分类主键',
  `activity_deadline` int DEFAULT NULL COMMENT '活动时效-ActivityDeadLineEnum',
  `activity_start_time` bigint DEFAULT NULL COMMENT '活动开始时间',
  `activity_end_time` bigint DEFAULT NULL COMMENT '活动结束时间',
  `show_start_time` bigint DEFAULT NULL COMMENT '活动展示开始时间',
  `show_end_time` bigint DEFAULT NULL COMMENT '活动展示结束时间',
  `activity_template` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动模板-同system_param activity_template',
  `wash_ratio` decimal(8,2) DEFAULT NULL COMMENT '洗码倍率',
  `account_type` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动生效的账户类型',
  `support_terminal` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动参与终端',
  `show_terminal` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动展示终端',
  `entrance_picture_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '入口图-移动端',
  `entrance_picture_pc_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '入口图-PC端',
  `head_picture_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动头图-移动端',
  `head_picture_pc_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动头图-PC端',
  `switch_phone` int DEFAULT NULL COMMENT '完成手机号绑定才能参与：0：关，1:开',
  `switch_email` int DEFAULT NULL COMMENT '完成邮箱绑定才能参与：0：关，1:开',
  `switch_ip` int DEFAULT NULL COMMENT '同登录IP只能1次：0：关，1:开',
  `activity_rule_i18n_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动规则,多语言',
  `activity_desc_i18n_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动描述,多语言',
  `sort` int DEFAULT NULL COMMENT '顺序',
  `status` int DEFAULT NULL COMMENT '状态 0已禁用 1开启中',
  `created_time` bigint DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `activity_introduce_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动简介',
  `delete_flag` int DEFAULT '1' COMMENT '删除标志 0-删除，1-没有删除',
  `show_flag` int DEFAULT NULL COMMENT '是否展示 0 不展示，1 展示',
  `forbid_time` bigint DEFAULT NULL COMMENT '活动禁用操作时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_index_01` (`activity_no`) COMMENT '活动编号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='优惠活动-基本表-v2';

-- 增加皮肤四内容
ALTER TABLE site_activity_base_v2 ADD COLUMN recommend_terminals VARCHAR(50) COMMENT '注册成功弹窗终端';
ALTER TABLE site_activity_base_v2 ADD COLUMN recommended int DEFAULT 0 COMMENT '是否推荐活动（0.不推荐。 1. 推荐）';
ALTER TABLE site_activity_base_v2 ADD COLUMN pic_showup_pc_i18n_code VARCHAR(50) COMMENT '弹窗宣传图PC';
ALTER TABLE site_activity_base_v2 ADD COLUMN pic_showup_app_i18n_code VARCHAR(50) COMMENT '弹窗宣传图APP';

-- site_activity_first_recharge

CREATE TABLE `site_activity_first_recharge_v2` (
  `id` bigint NOT NULL COMMENT '主键',
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `activity_id` bigint DEFAULT NULL COMMENT '所属活动id',
  `discount_type` int NOT NULL COMMENT '优惠方式类型，0.百分比，1.固定',
  `conditional_value` text COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '对应的活动条件值',
  `participation_mode` int DEFAULT NULL COMMENT '参与方式,0.手动参与，1.自动参与',
  `distribution_type` int DEFAULT NULL COMMENT '派发方式0.过期作废，1.过期自动派发,2.立即派发',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `venue_type` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏大类',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='首存活动规则配置信息表-v2';

-- 增加皮肤四内容（币种类型）
ALTER TABLE site_activity_first_recharge_v2 ADD COLUMN platform_or_fiat_currency VARCHAR(5) DEFAULT 0 COMMENT '活动币种类型（0.平台币，1. 法币）';



-- site_activity_second_recharge

CREATE TABLE `site_activity_second_recharge_v2` (
  `id` bigint NOT NULL COMMENT '主键',
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `activity_id` bigint DEFAULT NULL COMMENT '所属活动id',
  `discount_type` int DEFAULT NULL COMMENT '优惠方式类型，0.百分比，1.固定',
  `conditional_value` text COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '对应的活动条件值',
  `participation_mode` int DEFAULT NULL COMMENT '参与方式,0.手动参与，1.自动参与',
  `distribution_type` int DEFAULT NULL COMMENT '派发方式0.过期作废，1.过期自动派发,2.立即派发',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `venue_type` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏大类',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='次存活动规则配置信息表-v2';

-- 增加皮肤四内容（币种类型）
ALTER TABLE site_activity_second_recharge_v2 ADD COLUMN platform_or_fiat_currency VARCHAR(5) DEFAULT 0 COMMENT '活动币种类型（0.平台币，1. 法币）';

-- site_activity_assign_day

CREATE TABLE `site_activity_assign_day_v2` (
  `id` bigint NOT NULL COMMENT '主键id',
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `activity_id` bigint DEFAULT NULL COMMENT '所属活动',
  `week_days` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '指定日期 周一、周二等',
  `discount_type` int DEFAULT NULL COMMENT '优惠方式 0:百分比 1:固定金额',
  `distribution_type` int DEFAULT NULL COMMENT '派发方式0.过期作废，1.过期自动派发,2.立即派发',
  `participation_mode` int DEFAULT NULL COMMENT '参与方式,0.手动参与，1.自动参与',
  `condition_val` text COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '匹配条件 jsonArray格式阶梯次数:{min_deposit_amt,max_deposit_amt,acquire_num}',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `venue_type` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏大类',
  `venue_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '平台编号',
  `access_parameters` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏id',
  `bet_limit_amount` decimal(22,4) DEFAULT NULL COMMENT '投注限额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='指定日期存款活动-v2';

-- 增加皮肤四内容（币种类型）
ALTER TABLE site_activity_assign_day_v2 ADD COLUMN platform_or_fiat_currency VARCHAR(5) DEFAULT 0 COMMENT '活动币种类型（0.平台币，1. 法币）';

CREATE TABLE `site_activity_event_record_v2` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `code` varchar(100) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '这个字段主要是使 site_code+day+activity_template+user_id。唯一索引生效。使site_code+day+activity_template+user_id+code 唯一约束生效',
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点编码',
  `activity_id` bigint DEFAULT NULL COMMENT '所属活动',
  `day` bigint DEFAULT NULL COMMENT '参与当天的开始时间戳',
  `calculate_type` int DEFAULT NULL COMMENT '结算周期,0:日结,1:周结,2:月结',
  `status` int DEFAULT NULL COMMENT '发放状态,0=未发放，1=已发放',
  `activity_template` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动模板',
  `user_id` varchar(10) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员id',
  `user_account` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账号',
  `vip_rank` int DEFAULT NULL COMMENT 'vip等级',
  `device_no` varchar(80) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '用户-设备号',
  `ip` varchar(80) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '用户-ip',
  `created_time` bigint DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_account` (`user_id`,`activity_id`) COMMENT '联合索引',
  KEY `idx_ip` (`ip`) COMMENT 'IP索引',
  KEY `user_index` (`code`,`day`,`calculate_type`,`activity_template`,`user_id`) COMMENT '用户活动唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin AUTO_INCREMENT=4574578567464594578 COMMENT='会员活动参与记录-v2';

-- 赛事包赔活动表 site_activity_contest_payout_v2
CREATE TABLE `site_activity_contest_payout_v2` (
  `id` bigint NOT NULL COMMENT '主键id',
  `activity_id` bigint DEFAULT NULL COMMENT '所属活动',
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `activity_scope` char(1) COLLATE utf8mb4_0900_bin DEFAULT '1' COMMENT '活动适用范围,0:全体会员,1:新注册会员',
  `venue_type` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏类别,类别=体育',
  `venue_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '场馆编号',
  `venue_name` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '场馆名称',
  `third_a_day_app_i18n_code` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '三方A赛事推荐图-移动端白天图',
  `third_a_night_app_i18n_code` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '三方A赛事推荐图-移动端夜间图',
  `third_a_day_pc_i18n_code` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '三方A赛事推荐图- PC端白天图',
  `third_a_night_pc_i18n_code` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '三方A赛事推荐图- PC端夜间图',
  `third_b_day_app_i18n_code` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '三方B赛事推荐图-移动端白天图',
  `third_b_night_app_i18n_code` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '三方B赛事推荐图-移动端夜间图',
  `third_b_day_pc_i18n_code` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '三方B赛事推荐图- PC端白天图',
  `third_b_night_pc_i18n_code` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '三方B赛事推荐图- PC端夜间图',
  `access_parameters` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏访问参数，游戏id',
  `platform_or_fiat_currency` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT '0' COMMENT '活动币种类型（0.平台币，1. 法币）,作为扩展字段',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '修改人',
  `remark` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='赛事包赔活动-v2';

ALTER TABLE agent_manual_up_down_record ADD COLUMN device_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '设备号' AFTER currency_code;

ALTER TABLE `user_receive_account` ADD COLUMN `ifsc_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT 'ifsc码' AFTER `bank_card`;

ALTER TABLE `user_receive_account` DROP COLUMN `ifscCode`;

update system_dict_config SET is_sync_site=1 WHERE dict_code=30 and site_code='0';

ALTER TABLE `site_info` ADD COLUMN `black_long_logo` varchar(150) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '黑底-长logo' AFTER `long_logo`;

ALTER TABLE `site_info` ADD COLUMN `black_short_logo` varchar(150) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '黑底-短logo' AFTER `long_logo`;


ALTER TABLE site_venue
    ADD COLUMN `site_label_change_type` INT DEFAULT NULL COMMENT '冠名标签' AFTER `venue_code`;

ALTER TABLE game_two_class_info
    ADD COLUMN `site_label_change_type` INT DEFAULT NULL COMMENT '冠名标签' AFTER `site_code`;



ALTER TABLE site_banner_config
    ADD COLUMN dark_banner_url VARCHAR(120)  DEFAULT NULL COMMENT '黑底轮播图'
AFTER banner_url;

ALTER TABLE site_banner_config
    ADD COLUMN dark_h5_banner_url VARCHAR(120)  DEFAULT NULL COMMENT '黑底轮播图-h5'
AFTER banner_url;


ALTER TABLE `site_non_rebate_config`
    MODIFY COLUMN `venue_code` VARCHAR(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '场馆代码';



ALTER TABLE site_activity_base
    ADD COLUMN float_icon_show_flag TINYINT(1) NOT NULL DEFAULT 0
COMMENT '未登录首页浮动图标是否展示（0 不展示 1 展示）';

ALTER TABLE site_activity_base
    ADD COLUMN float_icon_app_i18n_code VARCHAR(128) DEFAULT NULL
    COMMENT '未登录首页浮动图标(移动端)-i18n code';

ALTER TABLE site_activity_base
    ADD COLUMN float_icon_pc_i18n_code VARCHAR(128) DEFAULT NULL
    COMMENT '未登录首页浮动图标(PC端)-i18n code';

ALTER TABLE site_activity_base
    ADD COLUMN float_icon_sort INT NOT NULL DEFAULT 0
    COMMENT '浮标排序 越大越靠前';

CREATE TABLE `agent_closure` (
                                 `parent_node_id` VARCHAR(255) NOT NULL COMMENT '父节点ID',
                                 `child_node_id`  VARCHAR(255) NOT NULL COMMENT '后代节点ID',
                                 `distance`       INT NOT NULL COMMENT '层级距离：0=自己，1=直接下级，2=二级下级...',
                                 PRIMARY KEY (`parent_node_id`, `child_node_id`) /*T![clustered_index] CLUSTERED */,

                                 KEY `idx_child_distance_parent` (`child_node_id`, `distance`, `parent_node_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_bin
  COMMENT='代理层级关系（Closure Table），distance 表示层级';

ALTER TABLE site_risk_ctrl_black_account
    ADD COLUMN ip_whitelist VARCHAR(255) DEFAULT NULL COMMENT 'IP白名单，多个用逗号分隔';


