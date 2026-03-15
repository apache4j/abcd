ALTER TABLE `site_activity_free_game_record`
    MODIFY COLUMN `bet_win_lose` DECIMAL(22,4) DEFAULT NULL COMMENT '投注盈亏';

ALTER TABLE `report_user_win_lose`
    ADD COLUMN `plat_adjust_amount` decimal(22, 4) default 0 COMMENT '调整金额(其他调整)-平台币' ,
ADD COLUMN `tips_amount` decimal(22, 4) default 0 COMMENT '打赏金额' ,
ADD COLUMN `risk_amount` decimal(22, 4) default 0 COMMENT '封控金额-主货币';
-- 场馆每日盈亏
ALTER TABLE `report_user_venue_win_lose`
        ADD COLUMN `tips_amount` decimal(22, 4) DEFAULT '0' COMMENT '打赏金额' ;
ALTER TABLE `report_user_venue_win_lose`
                ADD COLUMN `user_win_loss_amount` decimal(22, 4) DEFAULT '0' COMMENT '用户输赢' ;
-- 平台报表
ALTER TABLE `report_site_statistics`
  ADD COLUMN `plat_adjust_amount` decimal(22, 4) NULL COMMENT '调整金额(其他调整)-平台币' ,
  ADD COLUMN `tips_amount` decimal(22, 4) NULL COMMENT '打赏金额' ,
  ADD COLUMN `risk_amount` decimal(22, 4) NULL COMMENT '封控金额-主货币';


-- 代理报表
ALTER TABLE `report_agent_static_day`  ADD COLUMN `plat_adjust_amount` decimal(22, 4) NULL default 0  NULL COMMENT '调整金额(其他调整)-平台币' AFTER `updated_time`;
ALTER TABLE `report_agent_static_day`  ADD COLUMN `tips_amount` decimal(22, 4) NULL default 0  NULL COMMENT '打赏金额' AFTER `plat_adjust_amount`;
ALTER TABLE `report_agent_static_day`  ADD COLUMN `risk_amount` decimal(22, 4) NULL default 0  NULL COMMENT '封控金额-主货币' AFTER `tips_amount`;

-- 商务报表
ALTER TABLE `report_agent_merchant_static_day`  ADD COLUMN `tips_amount` decimal(22, 4) NULL default 0  NULL COMMENT '打赏金额' AFTER `already_use_amount`;
ALTER TABLE `report_agent_merchant_static_day`  ADD COLUMN `adjust_amount` decimal(22, 4) NULL default 0  NULL COMMENT '调整金额(其他调整)' AFTER `tips_amount`;


ALTER TABLE `report_user_info_statement`
    ADD COLUMN `plat_adjust_amount` decimal(22, 4)  default 0 COMMENT '调整金额(其他调整)-平台币' ,
ADD COLUMN `tips_amount` decimal(22, 4) default 0 COMMENT '打赏金额' ,
ADD COLUMN `risk_amount` decimal(22, 4) default 0 COMMENT '封控金额-主货币';

-- 平台币上下分
CREATE TABLE `user_platform_coin_manual_up_down_record` (
                                                            `id` bigint NOT NULL COMMENT '主键id',
                                                            `site_code` varchar(20) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
                                                            `agent_id` bigint DEFAULT NULL COMMENT '代理id',
                                                            `agent_account` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '代理账号',
                                                            `user_account` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账号',
                                                            `user_id` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员id',
                                                            `user_name` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '姓名',
                                                            `vip_grade_code` int DEFAULT NULL COMMENT 'vip等级code',
                                                            `order_no` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '订单号',
                                                            `adjust_way` int DEFAULT NULL COMMENT '调整方式:1-平台币上分，2-平台币下分',
                                                            `adjust_type` int DEFAULT NULL COMMENT '调整类型:1 会员VIP优惠,2 会员活动,3 其他调整',
                                                            `activity_template` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动模板system_param activity_template code值',
                                                            `activity_id` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动ID',
                                                            `currency_code` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT ' 币种code',
                                                            `platform_currency_code` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '平台币种code',
                                                            `adjust_amount` decimal(22,4) DEFAULT NULL COMMENT '调整金额',
                                                            `running_water_multiple` decimal(8,2) DEFAULT NULL COMMENT '流水倍数',
                                                            `certificate_address` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '上传附件地址',
                                                            `apply_reason` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '申请原因',
                                                            `apply_time` bigint DEFAULT NULL COMMENT '申请时间',
                                                            `applicant` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '申请人',
                                                            `audit_datetime` bigint DEFAULT NULL COMMENT '审核时间',
                                                            `audit_id` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '审核人',
                                                            `audit_remark` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '审核备注',
                                                            `audit_status` int DEFAULT NULL COMMENT '审核状态（1-待处理 2-处理中，3-审核通过，4-审核拒绝）',
                                                            `review_operation` int DEFAULT NULL COMMENT '审核操作1.一审审核',
                                                            `balance_change_status` int DEFAULT NULL COMMENT '账变状态0。账变失败，1.账变成功',
                                                            `lock_status` int DEFAULT NULL COMMENT '锁单状态 0未锁 1已锁',
                                                            `locker` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '锁单人',
                                                            `created_time` bigint DEFAULT NULL,
                                                            `updated_time` bigint DEFAULT NULL,
                                                            `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                            `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                            `device_id` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                            PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                                            KEY `user_account_status_index` (`user_account`,`audit_status`),
                                                            KEY `adjust_way_index` (`adjust_way`),
                                                            KEY `adjust_type_index` (`adjust_type`),
                                                            KEY `updated_time_index` (`updated_time`),
                                                            KEY `agent_id_status_index` (`agent_id`,`audit_status`),
                                                            KEY `agent_account_status_index` (`agent_account`,`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='会员平台币上下分记录';


-- 三方汇率更新时间
ALTER TABLE system_rate_config add COLUMN `third_rate_time` bigint NULL DEFAULT NULL COMMENT '三方汇率更新时间' AFTER `rate_type`;

-- 综合报表
ALTER TABLE `report_membership_stats` ADD COLUMN `platform_total_adjust` DECIMAL(22, 4) DEFAULT 0 AFTER `member_adjustments_reduce_people_number`;

ALTER TABLE `report_membership_stats` ADD COLUMN `platform_add_amount` DECIMAL(22, 4) DEFAULT 0 AFTER `platform_total_adjust`;

ALTER TABLE `report_membership_stats` ADD COLUMN `platform_add_people_num` INT DEFAULT 0 AFTER `platform_add_amount`;

ALTER TABLE `report_membership_stats` ADD COLUMN `platform_reduce_amount` DECIMAL(22, 4) DEFAULT 0 AFTER `platform_add_people_num`;

ALTER TABLE `report_membership_stats` ADD COLUMN `platform_reduce_people_nums` INT DEFAULT 0 AFTER `platform_reduce_amount`;

ALTER TABLE `report_membership_stats` ADD COLUMN `tips_amount` DECIMAL(22, 4) DEFAULT 0 AFTER `platform_reduce_people_nums`;

ALTER TABLE `report_membership_stats` ADD COLUMN `risk_amount` DECIMAL(22, 4) DEFAULT 0 AFTER `tips_amount`;

ALTER TABLE `report_membership_stats` ADD COLUMN `risk_add_amount` DECIMAL(22, 4) DEFAULT 0 AFTER `risk_amount`;

ALTER TABLE `report_membership_stats` ADD COLUMN `risk_add_people_num` INT DEFAULT 0 AFTER `risk_add_amount`;

ALTER TABLE `report_membership_stats` ADD COLUMN `risk_reduce_amount` DECIMAL(22, 4) DEFAULT 0 AFTER `risk_add_people_num`;

ALTER TABLE `report_membership_stats` ADD COLUMN `risk_reduce_people_num` INT DEFAULT 0 AFTER `risk_reduce_amount`;


 ALTER TABLE `report_site_statistics` MODIFY COLUMN `first_deposit_amount` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `deposit_amount` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `withdrawal_amount` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `deposit_withdrawal_difference` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `site_vip_benefits` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `site_promotional_offers` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `site_used_offers` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `site_other_adjustments` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `betting_amount` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `valid_betting` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `member_profit_loss` DECIMAL(22, 4) DEFAULT '0.0000';
 ALTER TABLE `report_site_statistics` MODIFY COLUMN `net_profit` DECIMAL(22, 4) DEFAULT '0.0000';


ALTER TABLE `site_activity_base`
    ADD COLUMN `show_flag` int NULL COMMENT '是否展示 0 不展示，1 展示' ;

ALTER TABLE `site_info`  ADD COLUMN `guaran_tee_flag` tinyint DEFAULT 0 COMMENT '保证金状态(0:禁用,1:启用)';



CREATE TABLE `site_security_adjust_review` (
  `id` bigint NOT NULL,
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点编码',
  `apply_time` bigint DEFAULT NULL COMMENT ' 申请时间\n\n提交审核的时间信息\n\n',
  `first_review_time` bigint DEFAULT NULL COMMENT ' 一审完成时间\n\n一审完成后的的时间信息\n\n',
  `review_order_number` varchar(45) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT ' 审核单号\n\n系统生成\n\n',
  `review_operation` int DEFAULT NULL COMMENT '审核操作 system_param review_operation值',
  `review_status` int DEFAULT NULL COMMENT '审核状态 system_param review_status值',
  `apply_user` varchar(45) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT ' 申请人\n\n审核提出的后台账号信息\n\n',
  `first_reviewer` varchar(45) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT ' 一审人\n\n一审审核的后台账号信息\n\n',
  `lock_status` int DEFAULT NULL COMMENT ' 锁单状态 1锁单 0 解锁',
  `adjust_type` varchar(32) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '调整类型',
  `adjust_amount` decimal(32,4) DEFAULT NULL COMMENT '调整金额',
  `locker` varchar(45) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '锁单人',
  `review_remark` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '一审完成备注',
  `created_time` bigint DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `currency` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
  `remark` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '申请原因',
  `site_name` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点名称',
  `lock_time` bigint DEFAULT NULL COMMENT '锁定时间',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='证金调整审核表';

CREATE TABLE `site_security_balance` (
  `id` bigint NOT NULL COMMENT 'ID',
  `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `site_name` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点名称',
  `company` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '所属公司',
  `site_type` tinyint DEFAULT NULL COMMENT '站点类型',
  `currency` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
  `available_balance` decimal(32,4) DEFAULT NULL COMMENT '可用金额',
  `frozen_balance` decimal(32,4) DEFAULT NULL COMMENT '冻结金额',
  `threshold_amount` decimal(32,4) DEFAULT NULL COMMENT '预警阀值',
  `overdraw_amount` decimal(32,4) DEFAULT NULL COMMENT '透支额度',
  `remain_overdraw` decimal(32,4) DEFAULT NULL COMMENT '剩余透支额度',
  `frozen_overdraw` decimal(32,4) DEFAULT NULL COMMENT '冻结透支金额',
  `security_status` int DEFAULT '1' COMMENT '保证金开启状态 0:未开启 1:已开启',
  `account_status` int DEFAULT '1' COMMENT '保证金账户状态 1:正常 2:预警 3:透支',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='保证金余额';

CREATE TABLE `site_security_change_log` (
  `id` bigint NOT NULL COMMENT 'ID',
  `order_no` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '帐变记录订单号',
  `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `site_name` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点名称',
  `company` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '所属公司',
  `site_type` tinyint DEFAULT NULL COMMENT '站点类型',
  `currency` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
  `balance_account` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账户保证金账户类型 AVAILABLE:可用 FROZEN:冻结',
  `source_order_no` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '来源订单号',
  `source_coin_type` varchar(32) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '来源订单类型',
  `user_type` varchar(32) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '帐号类型: user:会员 agent:代理 site:站点',
  `user_id` bigint DEFAULT NULL COMMENT '会员ID',
  `user_name` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员名称',
  `coin_type` varchar(32) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '帐变类型',
  `amount_direct` varchar(2) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '收支类型 +:收入 -:支出',
  `before_amount` decimal(32,4) DEFAULT NULL COMMENT '帐变前余额',
  `change_amount` decimal(32,4) DEFAULT NULL COMMENT '帐变金额',
  `after_amount` decimal(32,4) DEFAULT NULL COMMENT '帐变后金额',
  `change_time` bigint DEFAULT NULL COMMENT '帐变时间',
  `memo` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备注',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '修改人',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `idx_order_no` (`order_no`,`coin_type`,`balance_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='保证金帐变记录';


CREATE TABLE `site_task_over_view_config` (
                                              `id` bigint NOT NULL COMMENT '主键id',
                                              `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
                                              `expand_status` int DEFAULT NULL COMMENT '任务配置1展开，2-隐藏，默认是1',
                                              `created_time` bigint DEFAULT NULL,
                                              `updated_time` bigint DEFAULT NULL,
                                              `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                              `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                              PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='任务配置是否展示表';

ALTER TABLE `site_activity_free_game_record`
    ADD COLUMN `send_status` int NULL COMMENT 'free_game_send_status 0-发送中,1-成功,2-失败';


ALTER TABLE agent_deposit_withdrawal ADD wtc_usd_exchange_rate  decimal(20,4) DEFAULT NULL COMMENT 'WTC-USD汇率（总控)，用于保证金计算' AFTER platform_currency_exchange_rate;
ALTER TABLE user_deposit_withdrawal ADD currency_usd_exchange_rate  decimal(20,4) DEFAULT NULL COMMENT '主货币-USD汇率（总控)，用于保证金计算' AFTER exchange_rate;

alter table user_coin_record add index idx_ui_ct_ctt(user_id,coin_type,created_time desc);

ALTER TABLE `report_top_agent_static_day`
ADD COLUMN `tips_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '打赏金额-主货币' AFTER `win_loss_amount_user`;


ALTER TABLE `agent_commission_review_record` ADD COLUMN `second_review_finish_time` BIGINT NULL COMMENT '二审完成时间' AFTER `locker`;
ALTER TABLE `agent_commission_review_record` ADD COLUMN `second_review_start_time` BIGINT NULL COMMENT '二审开始时间' AFTER `second_review_finish_time`;


ALTER TABLE `agent_commission_review_record` ADD COLUMN `second_reviewer` VARCHAR(64) NULL COMMENT '二审人' AFTER `second_review_finish_time`;

ALTER TABLE `agent_commission_review_record` ADD COLUMN `second_review_remark` VARCHAR(255) NULL COMMENT '二审备注' AFTER `second_reviewer`;

ALTER TABLE `agent_commission_review_record` ADD COLUMN `adjust_commission_amount`  DECIMAL(22, 4) DEFAULT 0  COMMENT '调整金额' AFTER `second_review_remark`;
ALTER TABLE `agent_commission_review_record` ADD COLUMN `adjust_commission_remark` VARCHAR(255) NULL COMMENT '调整金额-备注' AFTER `adjust_commission_amount`;
ALTER TABLE `agent_commission_review_record` ADD COLUMN `final_status` INT NULL COMMENT '订单最终态' AFTER `adjust_commission_remark`;

ALTER TABLE `agent_commission_review_record` MODIFY COLUMN `order_status` INT COMMENT '1:待一审, 2:一审审核, 3:审核通过, 4:一审拒绝, 5:一审成功, 6:待二审, 7:二审审核, 8:二审拒绝, 9:二审驳回';

CREATE TABLE `system_channel_bank_relation` (
                                                `id` bigint unsigned NOT NULL COMMENT '主键ID',
                                                `config_id` bigint DEFAULT NULL COMMENT 'system_withdraw_channel_info关联id',
                                                `currency_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '货币代码',
                                                `channel_id` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '提款方式id',
                                                `channel_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '通道代码',
                                                `channel_name` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '通道名称',
                                                `bank_id` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '银行卡id',
                                                `bank_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '银行编码',
                                                `bank_name` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '银行名称',
                                                `bank_channel_mapping` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '通道-银行编码',
                                                `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                                `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
                                                `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='银行-通道编码配置';


CREATE TABLE `system_channel_bank_relation_base` (
                                                     `id` bigint unsigned NOT NULL COMMENT '主键ID',
                                                     `currency_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '货币代码',
                                                     `channel_id` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '提款方式id',
                                                     `channel_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '通道代码',
                                                     `channel_name` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '通道名称',
                                                     `bank_channel_status` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '1-全量 2-非全量 3-未配置',
                                                     `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                                     `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
                                                     `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                     `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                     PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='银行-通道编码配置';




CREATE TABLE `sport_events_info` (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `venue_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏场馆CODE',
                                     `sport_type` int NOT NULL COMMENT '体育项目ID:1: 足球。2: 篮球。3: 美式足球。4: 冰上曲棍球。9: 羽毛球。24: 手球。26: 橄榄球。43: 电子竞技',
                                     `league_id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '联赛ID',
                                     `tstamp` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL,
                                     `league_name_en` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '联赛名称（英文）',
                                     `league_name_jp` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '联赛名称（日文）',
                                     `league_name_cs` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '联赛名称（捷克语）',
                                     `league_name_th` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '联赛名称（泰语）',
                                     `league_name_ko` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '联赛名称（韩语）',
                                     `league_name_vn` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '联赛名称（越南语）',
                                     `league_name_zh_cn` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '联赛名称（简体中文）',
                                     `league_name_ch` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '联赛名称（繁體中文）',
                                     `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                     `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                     `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                     `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                     PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                     UNIQUE KEY `sport_events_info_events_id` (`venue_code`,`league_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='体育联赛';

CREATE TABLE `site_events` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                               `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点CODE',
                               `venue_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏场馆CODE',
                               `league_id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '联赛ID',
                               `sport_type` int NOT NULL COMMENT '体育项目ID:1: 足球。2: 篮球。3: 美式足球。4: 冰上曲棍球。9: 羽毛球。24: 手球。26: 橄榄球。43: 电子竞技',
                               `events_info_id` bigint DEFAULT NULL COMMENT 'sport_events_info.id',
                               `sort` int NOT NULL DEFAULT '1' COMMENT '首页- 热门排序',
                               `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                               `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                               `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                               `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                               PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                               UNIQUE KEY `uk_site_code_events_info_id` (`site_code`,`events_info_id`),
                               UNIQUE KEY `uk_site_code_league_id` (`site_code`,`league_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='站点-体育联赛排序';





ALTER TABLE site_activity_daily_robot ADD COLUMN `init_robot_bet_amount` decimal(20,2) DEFAULT NULL COMMENT '初始化-投注金额(流水WTC)' AFTER robot_account;

ALTER TABLE site_activity_daily_robot ADD COLUMN `max_robot_bet_amount` decimal(20,2) DEFAULT NULL COMMENT '机器人流水最高阀值' AFTER robot_bet_amount;

ALTER TABLE site_activity_daily_robot ADD COLUMN bet_growth_pct decimal(20,2) DEFAULT NULL COMMENT '流水增长百分比(%)' AFTER init_robot_bet_amount;

ALTER TABLE site_activity_daily_robot ADD COLUMN edit TINYINT(1) DEFAULT 0 COMMENT '是否编辑过（1是,0否）,这个字段每天晚上会重新恢复成0' AFTER init_robot_bet_amount;

ALTER TABLE site_activity_daily_robot ADD `version` int DEFAULT 1 COMMENT '版本号' AFTER edit;

ALTER TABLE `agent_commission_review_record` ADD COLUMN `apply_amount`  DECIMAL(22, 4) DEFAULT 0  COMMENT '申请金额' AFTER `adjust_commission_amount`;

ALTER TABLE agent_commission_grant_record MODIFY COLUMN commission_amount DECIMAL(22, 4) COMMENT '发放金额';
ALTER TABLE `agent_commission_grant_record` ADD COLUMN `apply_amount`  DECIMAL(22, 4) DEFAULT 0  COMMENT '申请金额' AFTER `commission_amount`;
ALTER TABLE `agent_commission_grant_record` ADD COLUMN `adjust_amount`  DECIMAL(22, 4) DEFAULT 0  COMMENT '调整金额' AFTER `apply_amount`;

ALTER TABLE `agent_rebate_final_report` ADD COLUMN `adjust_amount`  DECIMAL(22, 4) DEFAULT 0  COMMENT '调整金额' AFTER `every_user_amount`;
ALTER TABLE `agent_rebate_final_report` ADD COLUMN `rebate_adjust_amount`  DECIMAL(22, 4) DEFAULT 0  COMMENT '有效流水佣金-调整金额' AFTER `adjust_amount`;


ALTER TABLE `agent_commission_final_report` ADD COLUMN `review_adjust_amount`  DECIMAL(22, 4) DEFAULT 0  COMMENT '调整金额-审核界面调整' AFTER `commission_amount`;

ALTER TABLE `agent_commission_final_report` ADD COLUMN `tips_amount`  DECIMAL(22, 4) DEFAULT 0  COMMENT '打赏金额' AFTER `plan_code`;
ALTER TABLE `agent_commission_final_report` ADD COLUMN `bet_win_loss`  DECIMAL(22, 4) DEFAULT 0  COMMENT '会员输赢' AFTER `tips_amount`;


ALTER TABLE `agent_commission_expect_report` ADD COLUMN `tips_amount`  DECIMAL(22, 4) DEFAULT 0  COMMENT '打赏金额' AFTER `plan_code`;
ALTER TABLE `agent_commission_expect_report` ADD COLUMN `bet_win_loss`  DECIMAL(22, 4) DEFAULT 0  COMMENT '会员输赢' AFTER `tips_amount`;


ALTER TABLE`report_user_win_lose_message`
    MODIFY COLUMN `json_str` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '消息体' ;

ALTER TABLE `report_user_venue_win_lose_message` MODIFY COLUMN `json_str` varchar(1200) DEFAULT NULL;
