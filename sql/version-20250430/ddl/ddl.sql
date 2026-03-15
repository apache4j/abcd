-- 区块链钱包地址
ALTER TABLE hot_wallet_address
ADD INDEX idx_covering( china_type,network_type ,user_id, address);

CREATE TABLE `site_activity_check_in` (
                                          `id` bigint NOT NULL COMMENT '主键id',
                                          `base_id` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动主键id',
                                          `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
                                          `deposit_amount` decimal(20,2) DEFAULT NULL COMMENT '存款金额',
                                          `bet_amount` decimal(20,2) DEFAULT NULL COMMENT '投注金额',
                                          `reward_week` text COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '周奖励配置',
                                          `reward_month` text COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '月奖励配置',
                                          `check_in_switch` int DEFAULT NULL COMMENT '补签开关',
                                          `deposit_amount_today` decimal(20,2) DEFAULT NULL COMMENT '当日存款金额',
                                          `bet_amount_today` decimal(20,2) DEFAULT NULL COMMENT '当日投注金额',
                                          `push_switch` int DEFAULT NULL COMMENT '极光推送开关',
                                          `push_terminal` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '极光推送终端',
                                          `created_time` bigint DEFAULT NULL,
                                          `updated_time` bigint DEFAULT NULL,
                                          `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                          UNIQUE KEY `idx_sitec_code_base_id` (`site_code`,`base_id`) COMMENT '站点索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='签到活动配置表';
-- todo 考虑任务配置历史数据。

delete from site_task_config_next;
UPDATE site_task_config SET wash_ratio = NULL,venue_type = null,venue_code=null  WHERE sub_task_type IN (
                        'betDaily',
                        'profitDaily',
                        'negativeDaily',
                        'betWeek',
                        'profitWeek',
                        'negativeWeek'
    );

CREATE TABLE `site_check_in_record` (
                                        `id` varchar(64) COLLATE utf8mb4_0900_bin NOT NULL,
                                        `day_millis` bigint DEFAULT NULL COMMENT '站点日期 当天起始时间戳,签到日期',
                                        `day_str` varchar(19) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'day字段对应的字符串(签到日期)',
                                        `user_id` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '用户编号',
                                        `user_account` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账号',
                                        `main_currency` varchar(100) DEFAULT NULL COMMENT '主货币',
                                        `agent_account` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '上级代理账号',
                                        `agent_id` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '上级代理编号',
                                        `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点编码',
                                        `account_type` varchar(5) DEFAULT NULL COMMENT '账号类型 1-测试 2-正式',
                                        `remark` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                        `status` int DEFAULT NULL COMMENT '状态 0未签到 1已签到',
                                        `order_no` varchar(64) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '奖励订单号',
                                        `vip_grade_code` int DEFAULT NULL COMMENT 'VIP等级',
                                        `vip_rank_code` int DEFAULT NULL COMMENT 'vip段位code',
                                        `created_time` bigint DEFAULT NULL,
                                        `updated_time` bigint DEFAULT NULL,
                                        `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                        `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                        PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                        KEY `idx_user_id` (`user_id`,`day_millis`),
                                        UNIQUE KEY `uk_order_no` (`order_no`),
                                        KEY `idx_site_code` (`site_code`,`user_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='会员签到记录表';

-- 返水配置相关
ALTER TABLE `site_info`
    ADD COLUMN `rebate_status` int(2) DEFAULT 0 COMMENT '返水开关 0-禁用 1-开启' AFTER `maintenance_time_end`;

CREATE TABLE `site_rebate_config` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                      `site_code` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
                                      `vip_rank_code` int DEFAULT NULL COMMENT 'vip段位code',
                                      `vip_rank_name` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'vip段位名称',
                                      `sports_rebate` decimal(20,4) DEFAULT NULL COMMENT '体育返水',
                                      `esports_rebate` decimal(20,4) DEFAULT NULL COMMENT '电竞',
                                      `video_rebate` decimal(20,4) DEFAULT NULL COMMENT '视讯返水',
                                      `poker_rebate` decimal(20,4) DEFAULT NULL COMMENT '棋牌返水',
                                      `slots_rebate` decimal(20,4) DEFAULT NULL COMMENT '电子返水',
                                      `lottery_rebate` decimal(20,4) DEFAULT NULL COMMENT '彩票返水',
                                      `cockfighting_rebate` decimal(20,4) DEFAULT NULL COMMENT '斗鸡返水',
                                      `fishing_rebate` decimal(20,4) DEFAULT NULL COMMENT '捕鱼返水',
                                      `daily_limit` decimal(20,4) DEFAULT '0' COMMENT '单日上限',
                                      `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                      `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                      `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                      `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                      `status` int DEFAULT NULL COMMENT '1-开启, 0-关闭',
                                      `marbles_rebate` decimal(20,4) DEFAULT '0' COMMENT '弹珠返水',
                                      PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin AUTO_INCREMENT=1897560148457911491 COMMENT='返水配置';

CREATE TABLE `site_non_rebate_config` (
                                          `id` bigint NOT NULL COMMENT '主键id',
                                          `site_code` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
                                          `venue_type` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '对应venue_type中code',
                                          `venue_value` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '对应venue_type中value',
                                          `venue_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '场馆code',
                                          `venue_name` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '场馆value',
                                          `game_info` json DEFAULT NULL,
                                          `updater` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '操作人',
                                          `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                          `creator` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          `created_time` bigint DEFAULT NULL,
                                          PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='站点不返水配置表';


-- vip段位配置增加是否显示反水特权
ALTER TABLE `site_vip_rank` ADD COLUMN `rebate_config` int DEFAULT '0' COMMENT '是否显示反水特权配置0:没有,1:有' AFTER `luxurious_gifts`;


-- 返水审核相关
CREATE TABLE `user_rebate_venue_record` (
                                            `id` bigint NOT NULL COMMENT '主键id',
                                            `site_code` varchar(20) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
                                            `user_account` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账号',
                                            `user_id` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员id',
                                            `order_no` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '订单号',
                                            `valid_amount` decimal(24,4) NOT NULL COMMENT '有效投注',
                                            `rebate_amount` decimal(24,4) NOT NULL COMMENT '返水筋额',
                                            `rebate_percent` decimal(10,4) NOT NULL COMMENT '返水比例',
                                            `venue_type` int NOT NULL COMMENT '场馆类型',
                                            `currency_code` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种code',
                                            `created_time` bigint DEFAULT NULL COMMENT '统计日期',
                                            `issue_time` bigint DEFAULT NULL COMMENT '发放日期',
                                            `receive_time` bigint DEFAULT NULL COMMENT '领取日期',
                                            `status` int DEFAULT NULL COMMENT '订单状态0:未领取 1:已过期,2已领取',
                                            PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='返水明细表(根据场馆)';


CREATE TABLE `user_rebate_record` (
                                      `id` bigint NOT NULL COMMENT '主键id',
                                      `site_code` varchar(20) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
                                      `user_account` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账号',
                                      `user_id` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员id',
                                      `vip_rank_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'vip段位code',
                                      `vip_rank_name` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'vip段位名称',
                                      `order_no` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '订单号',
                                      `valid_amount` decimal(24,4) NOT NULL COMMENT '有效投注',
                                      `rebate_amount` decimal(24,4) NOT NULL COMMENT '返水总额',
                                      `currency_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种code',
                                      `audit_account` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '审核人',
                                      `audit_remark` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '审核备注',
                                      `order_status` int DEFAULT NULL COMMENT '审核状态（1-待审核 2-审核中，3-已派发，4-审核拒绝）',
                                      `review_operation` int DEFAULT NULL COMMENT '审核操作1.一审审核，2.结单查看',
                                      `lock_status` int DEFAULT NULL COMMENT '锁单状态 0未锁 1已锁',
                                      `locker` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '锁单人',
                                      `updated_time` bigint DEFAULT NULL,
                                      `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                      `created_time` bigint DEFAULT NULL COMMENT '统计日期',
                                      `audit_time` bigint DEFAULT NULL COMMENT '审核时间',
                                      `lock_time` bigint DEFAULT NULL COMMENT '锁单时间',
                                      `creator` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                      PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='返水审核记录';

ALTER TABLE report_user_venue_win_lose ADD COLUMN room_type VARCHAR(50) DEFAULT NULL AFTER venue_game_type COMMENT '存视讯的游戏id';

ALTER TABLE `report_user_info_statement`
    CHANGE COLUMN `gross_recoil` `rebate_amount` decimal(20, 2) NULL DEFAULT NULL COMMENT '返水';

-- 福利中心游戏返奖记录表
CREATE TABLE `site_rebate_reward_record` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `rebate_name_i18n_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '返利名称',
  `order_no` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '返利-订单号',
  `user_id` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员id',
  `super_agent_id` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '上级代理Id',
  `super_agent_account` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '上级代理账号',
  `invalid_time` bigint DEFAULT NULL COMMENT '失效时间',
  `reward_time` bigint DEFAULT NULL COMMENT '领取时间',
  `reward_amount` decimal(22,4) DEFAULT NULL COMMENT '奖励金额',
  `open_status` int DEFAULT NULL COMMENT '状态 0:未领取 1:已领取,2已过期',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='福利中心-游戏返奖记录表';
ALTER TABLE site_rebate_reward_record
ADD INDEX idx_site_rebate_userid_createdtime(user_id, created_time);


ALTER TABLE `report_agent_static_day` ADD COLUMN  `rebate_amount` DECIMAL(22, 4) DEFAULT 0 comment '返水金额';
ALTER TABLE report_top_agent_static_day ADD COLUMN  rebate_amount DECIMAL(22, 4) DEFAULT 0 comment '返水金额';

ALTER TABLE `site_activity_base`
    ADD COLUMN `delete_flag` INT NULL DEFAULT 1 COMMENT '删除标志 0-删除，1-没有删除';


ALTER TABLE `report_user_info_statement`
    MODIFY COLUMN `total_deposit` decimal(22, 4) NULL DEFAULT NULL COMMENT '总存款' ,
    MODIFY COLUMN `advanced_transfer` decimal(22, 4) NULL DEFAULT NULL COMMENT '上级转入' ,
    MODIFY COLUMN `amount_large_deposits` decimal(22, 4) NULL DEFAULT NULL COMMENT '大额存款金额',
    MODIFY COLUMN `total_withdrawal` decimal(22, 4) NULL DEFAULT NULL COMMENT '总取款' ,
    MODIFY COLUMN `amount_large_withdrawal` decimal(22, 4) NULL DEFAULT NULL COMMENT '大额取款总额' ,
    MODIFY COLUMN `poor_access` decimal(22, 4) NULL DEFAULT NULL COMMENT '存取差' ,
    MODIFY COLUMN `member_labour_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '会员活动人工加减额' ,
    MODIFY COLUMN `member_vip_labour_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '会员VIP人工加减额' ,
    MODIFY COLUMN `rebate_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '总返水' ,
    MODIFY COLUMN `other_adjustments` decimal(22, 4) NULL DEFAULT NULL COMMENT '其他调整' ,
    MODIFY COLUMN `bet_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '投注金额' ,
    MODIFY COLUMN `active_bet` decimal(22, 4) NULL DEFAULT NULL COMMENT '有效投注金额' ,
    MODIFY COLUMN `betting_profit_loss` decimal(22, 4) NULL DEFAULT NULL COMMENT '投注盈亏' ,
    MODIFY COLUMN `activity_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '优惠金额，活动优惠金额' ,
    MODIFY COLUMN `vip_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT 'vip福利' ,
    MODIFY COLUMN `already_use_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '已经使用优惠' ,
    MODIFY COLUMN `profit_and_loss` decimal(22, 4) NULL DEFAULT NULL COMMENT '净盈亏' ;

-- 修改精度
ALTER TABLE `report_user_win_lose`
    MODIFY COLUMN `bet_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '投注金额' ,
    MODIFY COLUMN `valid_bet_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '有效投注' ,
    MODIFY COLUMN `run_water_correct` decimal(22, 4) NULL DEFAULT NULL COMMENT '流水纠正' ,
    MODIFY COLUMN `bet_win_lose` decimal(22, 4) NULL DEFAULT NULL COMMENT '投注盈亏' ,
    MODIFY COLUMN `rebate_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '返水金额' ,
    MODIFY COLUMN `activity_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '优惠金额，活动优惠金额，仅当活动发放的是平台币。发放主货币，统计到已使用优惠' ,
    MODIFY COLUMN `vip_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT 'vip福利 币种是平台币' ,
    MODIFY COLUMN `adjust_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '调整金额(其他调整)' ,
    MODIFY COLUMN `repair_order_other_adjust` decimal(22, 4) NULL DEFAULT NULL COMMENT '补单其他调整' ,
    MODIFY COLUMN `profit_and_loss` decimal(22, 4) NULL DEFAULT NULL COMMENT '净盈亏' ,
    MODIFY COLUMN `already_use_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '已经使用优惠,活动发放是主货币，前端把平台币转换为主货币' ;


ALTER TABLE `site_activity_order_record`
    MODIFY COLUMN `activity_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '活动赠送金额' ,
    MODIFY COLUMN `plat_activity_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '赠送金额-转成平台币金额-用于报表' ,
    MODIFY COLUMN `running_water` decimal(22, 4) NULL DEFAULT NULL COMMENT '流水要求' ;


ALTER TABLE `site_task_order_record`
    MODIFY COLUMN `task_amount` decimal(22, 4) NULL DEFAULT NULL COMMENT '任务赠送金额' ,
    MODIFY COLUMN `running_water` decimal(22, 4) NULL DEFAULT NULL COMMENT '流水要求' ;

ALTER TABLE user_deposit_withdrawal ADD  `user_label_id` varchar(1000) DEFAULT NULL COMMENT '会员标签id';

-- 会员信息，首存金额，次存金额
ALTER TABLE user_info MODIFY COLUMN `first_deposit_amount` decimal(22,4) DEFAULT NULL COMMENT '首存金额';
ALTER TABLE user_info MODIFY COLUMN `second_deposit_amount` decimal(22,4) DEFAULT NULL COMMENT '次存金额';

-- 会员主货币账变，会员主货币账变记录
ALTER TABLE user_coin MODIFY COLUMN `total_amount` decimal(22,4) DEFAULT '0' COMMENT '总金额';
ALTER TABLE user_coin MODIFY COLUMN `freeze_amount` decimal(22,4) DEFAULT '0' COMMENT '冻结金额';
ALTER TABLE user_coin MODIFY COLUMN`available_amount` decimal(22,4) DEFAULT '0' COMMENT '可用余额';
ALTER TABLE user_coin_record MODIFY COLUMN `coin_value` decimal(22,4) DEFAULT NULL COMMENT '金额改变数量';
ALTER TABLE user_coin_record MODIFY COLUMN `coin_from` decimal(22,4) DEFAULT NULL COMMENT '账变前金额';
ALTER TABLE user_coin_record MODIFY COLUMN `coin_to` decimal(22,4) DEFAULT NULL COMMENT '账变后金额';
ALTER TABLE user_coin_record MODIFY COLUMN `coin_amount` decimal(22,4) DEFAULT NULL COMMENT '当前金额';

-- 会员平台币账变，会员平台币账变记录
ALTER TABLE user_platform_coin MODIFY COLUMN `total_amount` decimal(22,4) DEFAULT '0' COMMENT '总金额';
ALTER TABLE user_platform_coin MODIFY COLUMN `freeze_amount` decimal(22,4) DEFAULT '0' COMMENT '冻结金额';
ALTER TABLE user_platform_coin MODIFY COLUMN `available_amount` decimal(22,4) DEFAULT '0' COMMENT '可用余额';
ALTER TABLE user_platform_coin_record MODIFY COLUMN `coin_value` decimal(22,4) DEFAULT NULL COMMENT '账变金额';
ALTER TABLE user_platform_coin_record MODIFY COLUMN `coin_from` decimal(22,4) DEFAULT NULL COMMENT '账变前金额';
ALTER TABLE user_platform_coin_record MODIFY COLUMN `coin_to` decimal(22,4) DEFAULT NULL COMMENT '账变后金额';
ALTER TABLE user_platform_coin_record MODIFY COLUMN `coin_amount` decimal(22,4) DEFAULT NULL COMMENT '当前金额';

-- 会员平台币转换记录
ALTER TABLE user_platform_transfer_record MODIFY COLUMN `transfer_amount` decimal(22,4) DEFAULT NULL COMMENT '转换金额';
ALTER TABLE user_platform_transfer_record MODIFY COLUMN `target_amount` decimal(22,4) DEFAULT NULL COMMENT '目标金额';

-- 会员流水，流水变更记录
ALTER TABLE user_typing_amount MODIFY COLUMN `typing_amount` decimal(22,4) DEFAULT NULL COMMENT '打码量';
ALTER TABLE user_typing_amount_record MODIFY COLUMN `coin_from` decimal(22,4) DEFAULT NULL COMMENT '调整前流水';
ALTER TABLE user_typing_amount_record MODIFY COLUMN`coin_value` decimal(22,4) DEFAULT NULL COMMENT '调整流水';
ALTER TABLE user_typing_amount_record MODIFY COLUMN  `coin_to` decimal(22,4) DEFAULT NULL COMMENT '调整后流水';

-- 会员人工提款记录
ALTER TABLE user_withdrawal_manual_record MODIFY COLUMN `apply_amount` decimal(22,4) DEFAULT '0' COMMENT '申请金额';
ALTER TABLE user_withdrawal_manual_record MODIFY COLUMN `arrive_amount` decimal(22,4) DEFAULT '0' COMMENT '实际到账金额';
ALTER TABLE user_withdrawal_manual_record MODIFY COLUMN `fee_amount` decimal(22,4) DEFAULT '0.0000' COMMENT '手续费';

-- 会员人工加减额
ALTER TABLE user_manual_up_down_record MODIFY COLUMN `adjust_amount` decimal(22,4) DEFAULT NULL COMMENT '调整金额';
ALTER TABLE user_manual_up_down_record MODIFY COLUMN `adjust_type` int DEFAULT NULL COMMENT '调整类型:3.其他调整,4.会员提款(后台),5.会员VIP优惠,6.会员活动,10 会员返水';

-- 会员存取
ALTER TABLE user_deposit_withdrawal MODIFY COLUMN `apply_amount` decimal(22,4) DEFAULT '0' COMMENT '申请金额';
ALTER TABLE user_deposit_withdrawal MODIFY COLUMN `arrive_amount` decimal(22,4) DEFAULT '0' COMMENT '实际到账金额';
ALTER TABLE user_deposit_withdrawal MODIFY COLUMN `fee_amount` decimal(22,4) DEFAULT '0' COMMENT '手续费';
ALTER TABLE user_deposit_withdrawal MODIFY COLUMN `fee_fixed_amount` decimal(22,4) DEFAULT '0' COMMENT '会员手续费固定金额';
ALTER TABLE user_deposit_withdrawal MODIFY COLUMN `settlement_fee_percentage_amount` decimal(22,4) DEFAULT '0' COMMENT '百分比金额';
ALTER TABLE user_deposit_withdrawal MODIFY COLUMN `settlement_fee_fixed_amount` decimal(22,4) DEFAULT '0' COMMENT '固定金额 ';

-- 代理存取
ALTER TABLE agent_deposit_withdrawal MODIFY COLUMN `apply_amount` decimal(22,4) DEFAULT '0' COMMENT '申请金额';
ALTER TABLE agent_deposit_withdrawal MODIFY COLUMN `arrive_amount` decimal(22,4) DEFAULT '0' COMMENT '实际到账金额';
ALTER TABLE agent_deposit_withdrawal MODIFY COLUMN `fee_amount` decimal(22,4) DEFAULT '0.0000' COMMENT '手续费';
ALTER TABLE agent_deposit_withdrawal MODIFY COLUMN `fee_fixed_amount` decimal(22,4) DEFAULT '0' COMMENT '会员手续费固定金额 ';
ALTER TABLE agent_deposit_withdrawal MODIFY COLUMN `settlement_fee_percentage_amount` decimal(22,4) DEFAULT '0' COMMENT '百分比金额';
ALTER TABLE agent_deposit_withdrawal MODIFY COLUMN `settlement_fee_fixed_amount` decimal(22,4) DEFAULT '0' COMMENT '固定金额 ';



-- 会员存取实时报表
ALTER TABLE report_user_recharge_withdraw MODIFY COLUMN  `amount` decimal(22,4) DEFAULT '0' COMMENT '存取款金额';
ALTER TABLE report_user_recharge_withdraw MODIFY COLUMN  `large_amount` decimal(22,4) DEFAULT '0' COMMENT '大额存款金额';
ALTER TABLE report_user_recharge_withdraw MODIFY COLUMN  `deposit_subordinates_amount` decimal(22,4) DEFAULT '0' COMMENT '代理代存金额';

-- 会员存取报日报表
ALTER TABLE report_user_deposit_withdraw MODIFY COLUMN `deposit_total_amount` decimal(22,4) DEFAULT NULL COMMENT '存款总金额';
ALTER TABLE report_user_deposit_withdraw MODIFY COLUMN `big_money_withdraw_amount` decimal(22,4) DEFAULT NULL COMMENT '大额取款金额';
ALTER TABLE report_user_deposit_withdraw MODIFY COLUMN  `withdraw_total_amount` decimal(22,4) DEFAULT NULL COMMENT '取款总金额';
ALTER TABLE report_user_deposit_withdraw MODIFY COLUMN `deposit_withdrawal_difference` decimal(22,4) DEFAULT NULL COMMENT '存取款差额';

-- 代理存取实时
ALTER TABLE report_agent_recharge_withdraw MODIFY COLUMN `large_amount` decimal(22,4) DEFAULT '0' COMMENT '大额存款金额';
-- 存取报表
ALTER TABLE report_agent_deposit_withdraw MODIFY COLUMN `total_deposit_amount` decimal(22,4) DEFAULT '0.00' COMMENT '代理总存款';
ALTER TABLE report_agent_deposit_withdraw MODIFY COLUMN `total_withdraw_amount` decimal(22,4) DEFAULT '0.00' COMMENT '代理总提款';
ALTER TABLE report_agent_deposit_withdraw MODIFY COLUMN `diff_deposit_withdraw` decimal(22,4) DEFAULT '0.00' COMMENT '代理存提差';
ALTER TABLE report_agent_deposit_withdraw MODIFY COLUMN `agent_subordinates_amount` decimal(22,4) DEFAULT '0.00' COMMENT '代存会员总额';
ALTER TABLE report_agent_deposit_withdraw MODIFY COLUMN `agent_transfer_amount` decimal(22,4) DEFAULT '0.00' COMMENT '代理转账总额';
-- 代理额度钱包
ALTER TABLE agent_quota_coin MODIFY COLUMN   `total_amount` decimal(22,4) DEFAULT '0.00' COMMENT '总金额';
ALTER TABLE agent_quota_coin MODIFY COLUMN  `freeze_amount` decimal(22,4) DEFAULT '0.00' COMMENT '冻结金额';
ALTER TABLE agent_quota_coin MODIFY COLUMN  `available_amount` decimal(22,4) DEFAULT '0.00' COMMENT '可用余额';
-- 代理额度转账记录
ALTER TABLE agent_quota_transfer_record MODIFY COLUMN `amount` decimal(22,4) DEFAULT NULL COMMENT '转账金额';

-- 代理佣金钱包
ALTER TABLE agent_commission_coin MODIFY COLUMN `total_amount` decimal(22,4) DEFAULT '0.00' COMMENT '总金额';
ALTER TABLE agent_commission_coin MODIFY COLUMN `freeze_amount` decimal(22,4) DEFAULT '0.00' COMMENT '冻结金额';
ALTER TABLE agent_commission_coin MODIFY COLUMN `available_amount` decimal(22,4) DEFAULT '0.00' COMMENT '可用余额';

-- 代理账变记录
ALTER TABLE agent_coin_record MODIFY COLUMN `coin_from` decimal(22,4) DEFAULT NULL COMMENT '账变前金额';
ALTER TABLE agent_coin_record MODIFY COLUMN `coin_to` decimal(22,4) DEFAULT NULL COMMENT '账变后金额';
ALTER TABLE agent_coin_record MODIFY COLUMN `coin_amount` decimal(22,4) DEFAULT NULL COMMENT '账变金额';

-- 代理代存
ALTER TABLE agent_deposit_subordinates MODIFY COLUMN  `amount` decimal(22,4) DEFAULT NULL COMMENT '代存金额';
ALTER TABLE agent_deposit_subordinates MODIFY COLUMN  `platform_amount` decimal(22,4) DEFAULT NULL COMMENT '平台币金额';

-- 代理转账
ALTER TABLE agent_transfer_record MODIFY COLUMN  `transfer_amount` decimal(22,4) DEFAULT NULL COMMENT '转账金额';

ALTER TABLE `site_vip_award_record`
ADD INDEX `idx_user_receive`(`user_id` ASC, `receive_status` ASC) USING BTREE;

-- 综合报表decimal字段
ALTER TABLE `report_membership_stats` MODIFY COLUMN `total_member_deposit` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `total_member_withdrawal` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `member_deposit_withdrawal_difference` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `first_member_deposit_amount` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `member_betting_amount` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `member_betting_valid_amount` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `member_betting_number` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `member_profit_loss` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `member_vip_benefits_amount` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `member_activity_discounts_amount` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `used_discounts` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `member_adjustments_amount` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `member_adjustments_add_amount` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `member_adjustments_reduce_amount` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `report_membership_stats` MODIFY COLUMN `stored_members_limit` DECIMAL(22,4) DEFAULT NULL;

-- agent_transfer_record
ALTER TABLE `agent_transfer_record` MODIFY COLUMN `transfer_amount` DECIMAL(22,4) DEFAULT NULL;

-- agent_withdraw_config_detail
ALTER TABLE `agent_withdraw_config_detail` MODIFY COLUMN `withdraw_min_quota_single` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `agent_withdraw_config_detail` MODIFY COLUMN `withdraw_max_quota_single` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `agent_withdraw_config_detail` MODIFY COLUMN `withdraw_max_quota_day` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `agent_withdraw_config_detail` MODIFY COLUMN `large_withdraw_mark_amount` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `agent_withdraw_config_detail` MODIFY COLUMN `fee_rate` DECIMAL(20,2) DEFAULT NULL;
ALTER TABLE `agent_withdraw_config_detail` MODIFY COLUMN `withdraw_min_quota_single` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `agent_withdraw_config_detail` MODIFY COLUMN `withdraw_max_quota_single` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `agent_withdraw_config_detail` MODIFY COLUMN `withdraw_max_quota_day` DECIMAL(22,4) DEFAULT NULL;
ALTER TABLE `agent_withdraw_config_detail` MODIFY COLUMN `large_withdraw_mark_amount` DECIMAL(22,4) DEFAULT NULL;

-- agent_manual_up_down_record
ALTER TABLE `agent_manual_up_down_record` MODIFY COLUMN `adjust_amount` DECIMAL(22,4) DEFAULT NULL;
-- 代理报表相关
ALTER TABLE `report_agent_static_day`
ADD INDEX `idx_ai_rd_sc_cc`(`agent_id`,`report_date`, `site_code`,`currency_code`) USING BTREE;
alter table report_agent_static_bet
add index idx_ai_ua_ds(agent_id,user_account,day_str);

ALTER TABLE `report_top_agent_static_day`
ADD INDEX `idx_rd_ai_sc_cc`(`report_date`, `agent_id`,`site_code`,`currency_code`) USING BTREE;
ALTER TABLE `report_agent_merchant_static_day`
ADD INDEX `idx_rd_ma_sc_cc`(`report_date`, `merchant_account`,`site_code`,`currency_code`) USING BTREE;

alter table order_record drop index idx_settle_time;
alter table order_record drop index idx_user_id;
alter table order_record add index idx_ui_bt_sc_vt(user_id,bet_time,site_code,venue_type);
alter table order_record drop index idx_site_venue_bet_time_user;

-- 会员索引
alter table user_info add index idx_rt_sc(register_time,site_code);

ALTER TABLE `report_user_win_lose`
ADD INDEX `idx_upt_st`(`updated_time`, `site_code`) USING BTREE;

-- 代理佣金审核记录表, 代理佣金返点表decimal字段
ALTER TABLE agent_commission_review_record MODIFY COLUMN commission_amount DECIMAL(22, 4)  COMMENT '佣金金额';
ALTER TABLE agent_rebate_final_report MODIFY COLUMN rebate_amount DECIMAL(22, 4)  COMMENT '有效流水返点结算金额';
ALTER TABLE agent_rebate_final_report MODIFY COLUMN new_user_amount DECIMAL(22, 4)  COMMENT '人头费';
ALTER TABLE agent_rebate_final_report MODIFY COLUMN every_user_amount DECIMAL(22, 4)  COMMENT '每人的人头费';

-- 代理佣金结算表
ALTER TABLE agent_commission_final_report MODIFY COLUMN user_win_loss DECIMAL(22, 4)  COMMENT '会员输赢';
ALTER TABLE agent_commission_final_report MODIFY COLUMN user_win_loss_total DECIMAL(22, 4)  COMMENT '会员总输赢';
ALTER TABLE agent_commission_final_report MODIFY COLUMN venue_fee DECIMAL(22, 4)  COMMENT '场馆费';
ALTER TABLE agent_commission_final_report MODIFY COLUMN transfer_amount DECIMAL(22, 4)  COMMENT '平台币钱包转化金额 (已使用优惠)';
ALTER TABLE agent_commission_final_report MODIFY COLUMN access_fee DECIMAL(22, 4)  COMMENT '总存取手续费';
ALTER TABLE agent_commission_final_report MODIFY COLUMN adjust_amount DECIMAL(22, 4)  COMMENT '调整金额';
ALTER TABLE agent_commission_final_report MODIFY COLUMN discount_amount DECIMAL(22, 4)  COMMENT '活动优惠';
ALTER TABLE agent_commission_final_report MODIFY COLUMN vip_amount DECIMAL(22, 4)  COMMENT 'vip福利';
ALTER TABLE agent_commission_final_report MODIFY COLUMN valid_bet_amount DECIMAL(22, 4)  COMMENT '有效流水';
ALTER TABLE agent_commission_final_report MODIFY COLUMN last_month_remain DECIMAL(22, 4)  COMMENT '待冲正金额';
ALTER TABLE agent_commission_final_report MODIFY COLUMN net_win_loss DECIMAL(22, 4)  COMMENT '会员净输赢';
ALTER TABLE agent_commission_final_report MODIFY COLUMN commission_amount DECIMAL(22, 4)  COMMENT '负盈利佣金';

-- 代理佣金预期表
ALTER TABLE agent_commission_expect_report MODIFY COLUMN user_win_loss DECIMAL(22, 4)  COMMENT '会员输赢';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN user_win_loss_total DECIMAL(22, 4)  COMMENT '会员总输赢';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN venue_fee DECIMAL(22, 4)  COMMENT '场馆费';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN transfer_amount DECIMAL(22, 4)  COMMENT '平台币钱包转化金额(已使用优惠)';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN access_fee DECIMAL(22, 4)  COMMENT '总存取手续费';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN adjust_amount DECIMAL(22, 4)  COMMENT '调整金额';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN discount_amount DECIMAL(22, 4)  COMMENT '活动优惠';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN vip_amount DECIMAL(22, 4)  COMMENT 'vip福利';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN valid_bet_amount DECIMAL(22, 4)  COMMENT '有效流水';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN last_month_remain DECIMAL(22, 4)  COMMENT '待冲正金额';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN net_win_loss DECIMAL(22, 4)  COMMENT '会员净输赢';
ALTER TABLE agent_commission_expect_report MODIFY COLUMN commission_amount DECIMAL(22, 4)  COMMENT '负盈利佣金'

-- agent_rebate_config
ALTER TABLE agent_rebate_config MODIFY COLUMN new_user_amount DECIMAL(22, 4)  COMMENT '有效新增人头费'
ALTER TABLE agent_rebate_config ADD COLUMN marbles_rebate varchar(10) DEFAULT '0'  COMMENT '弹珠返水';




-- 热钱包地址
ALTER TABLE hot_wallet_address
    CHANGE COLUMN china_type chain_type varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '链类型' AFTER user_account;
ALTER TABLE hot_wallet_address ADD balance DECIMAL(22,4) DEFAULT 0 COMMENT '余额' AFTER address ;
-- 热钱包地址交易归集信息记录
CREATE TABLE `hot_wallet_address_trade_record` (
                                                   `id` bigint NOT NULL COMMENT 'ID',
                                                   `json_str` text COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '交易，归集信息',
                                                   `trade_hash` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '交易hash',
                                                   `address` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '地址',
                                                   `created_time` bigint DEFAULT NULL,
                                                   `updated_time` bigint DEFAULT NULL,
                                                   `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                   `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                   PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='热钱包地址交易，归集信息记录';

-- vip升级经验更改
ALTER TABLE `user_vip_flow_record`
    MODIFY COLUMN `valid_exe` decimal(22, 4) NULL DEFAULT NULL COMMENT '单次有效流水金额' ,
    MODIFY COLUMN `valid_sum_exe` decimal(22, 4) NULL DEFAULT NULL COMMENT '累计有效流水金额';

ALTER TABLE  `report_agent_static_day`
MODIFY COLUMN `bet_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '投注额' AFTER `currency_code`,
MODIFY COLUMN `valid_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '有效投注额' AFTER `bet_amount`,
MODIFY COLUMN `win_loss_amount_user` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '会员输赢' AFTER `valid_amount`,
MODIFY COLUMN `win_loss_amount_plat` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '平台总输赢' AFTER `win_loss_amount_user`,
MODIFY COLUMN `adjust_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '调整金额' AFTER `win_loss_amount_plat`,
MODIFY COLUMN `activity_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '活动优惠' AFTER `win_loss_rate`,
MODIFY COLUMN `vip_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT 'vip福利' AFTER `activity_amount`,
MODIFY COLUMN `already_use_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '已经使用优惠,活动发放是主货币，前端把平台币转换为主货币' AFTER `vip_amount`,
MODIFY COLUMN `deposit_withdraw_fee_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '存提手续费' AFTER `already_use_amount`;


ALTER TABLE  `report_agent_merchant_static_day`
MODIFY COLUMN `deposit_amount` decimal(32, 4) NULL DEFAULT NULL COMMENT '存款金额' AFTER `currency_code`,
MODIFY COLUMN `withdraw_amount` decimal(32, 4) NULL DEFAULT NULL COMMENT '取款金额' AFTER `deposit_amount`,
MODIFY COLUMN `deposit_withdraw_fee` decimal(32, 4) NULL DEFAULT NULL COMMENT '存提手续费' AFTER `withdraw_amount`,
MODIFY COLUMN `bet_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '投注额' AFTER `deposit_withdraw_fee`,
MODIFY COLUMN `valid_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '有效投注额' AFTER `bet_amount`,
MODIFY COLUMN `win_loss_amount_user` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '会员输赢' AFTER `valid_amount`,
MODIFY COLUMN `win_loss_amount_plat` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '平台总输赢' AFTER `win_loss_amount_user`,
MODIFY COLUMN `already_use_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '已经使用优惠,活动发放是主货币，前端把平台币转换为主货币' AFTER `win_loss_amount_plat`;

ALTER TABLE  `report_top_agent_static_day`
MODIFY COLUMN `bet_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '投注额' AFTER `currency_code`,
MODIFY COLUMN `valid_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '有效投注额' AFTER `bet_amount`,
MODIFY COLUMN `win_loss_amount_user` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '会员输赢' AFTER `valid_amount`,
MODIFY COLUMN `win_loss_amount_plat` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '平台总输赢' AFTER `win_loss_amount_user`,
MODIFY COLUMN `adjust_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '调整金额' AFTER `win_loss_amount_plat`,
MODIFY COLUMN `activity_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '活动优惠' AFTER `win_loss_rate`,
MODIFY COLUMN `vip_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT 'vip福利' AFTER `activity_amount`,
MODIFY COLUMN `already_use_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '已经使用优惠,活动发放是主货币，前端把平台币转换为主货币' AFTER `vip_amount`,
MODIFY COLUMN `deposit_amount` decimal(32, 4) NULL DEFAULT 0.00 COMMENT '存款金额' AFTER `already_use_amount`,
MODIFY COLUMN `withdraw_amount` decimal(32, 4) NULL DEFAULT 0.00 COMMENT '取款金额' AFTER `deposit_amount`,
MODIFY COLUMN `deposit_withdraw_fee_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '存提手续费' AFTER `withdraw_amount`;



ALTER TABLE `agent_rebate_report_detail`
MODIFY COLUMN `valid_amount` decimal(20, 4) NULL DEFAULT NULL COMMENT '有效流水' AFTER `currency`,
MODIFY COLUMN `rebate_amount` decimal(20, 4) NULL DEFAULT NULL COMMENT '返点金额' AFTER `rebate_rate`;


ALTER TABLE `site_activity_red_bag_record`
MODIFY COLUMN `redbag_amount` decimal(20, 4) NULL DEFAULT NULL COMMENT '红包金额' AFTER `user_account`,
MODIFY COLUMN `remaining_amount` decimal(20, 4) NULL DEFAULT NULL COMMENT '奖池剩余金额' AFTER `redbag_amount`;