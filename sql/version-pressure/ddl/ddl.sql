CREATE INDEX idx_site_activity_order_record
ON site_activity_order_record (site_code, user_account, receive_status, created_time, receive_time);

CREATE INDEX idx_site_vip_award_record
ON site_vip_award_record (site_code, user_account, receive_status, record_start_time);

CREATE INDEX idx_site_task_order_record
ON site_task_order_record (site_code, user_account, task_type, receive_status, receive_start_time);

CREATE INDEX idx_medal_reward_record
ON medal_reward_record (site_code, user_account, open_status, complete_time, reward_amount);

CREATE INDEX idx_medal_acquire_record
ON medal_acquire_record (site_code, user_account, lock_status, complete_time, unlock_time);

ALTER TABLE `order_record`
ADD INDEX `idx_site_venue_bet_time_user`(`site_code`, `venue_type`, `bet_time`, `user_id`) USING BTREE;



-- 删除多余索引
ALTER TABLE user_info DROP INDEX idx_register_ip;
ALTER TABLE user_info DROP INDEX idx_site_code;
ALTER TABLE user_info DROP INDEX idx_account_type;
ALTER TABLE user_info DROP INDEX idx_user_account;
ALTER TABLE user_info DROP INDEX idx_create_time;
ALTER TABLE user_info DROP INDEX idx_super_agent_account;

-- 邀请码索引,不需要使用siteCode
ALTER TABLE user_info DROP INDEX idex_site_code_invite;
CREATE INDEX idex_site_code_invite USING BTREE ON user_info (friend_invite_code);

-- 添加索引
ALTER TABLE user_login_info
    ADD INDEX idx_user_created_time (user_id, created_time DESC);

ALTER TABLE user_login_info drop index idx_site_code;


-- 站点首页
ALTER TABLE user_deposit_withdrawal ADD INDEX idx_site_status_time (site_code, updated_time, status,type);

CREATE INDEX idx_site_time_type ON user_manual_up_down_record(site_code, updated_time, adjust_type, balance_change_status, adjust_way);

CREATE INDEX idx_site_deposit_time ON agent_deposit_subordinates(site_code, deposit_time);



-- 账变记录
ALTER TABLE user_coin_record DROP INDEX idx_created_time, ADD INDEX idx_site_code_created_time (site_code, created_time);
ALTER TABLE user_coin_record ADD INDEX idx_site_code_user_account (site_code, user_account);
ALTER TABLE user_coin ADD INDEX idx_site_code_user_account (site_code, user_account);

-- 存取表
ALTER TABLE user_deposit_withdrawal ADD INDEX idx_type_status (type, status);
ALTER TABLE agent_deposit_withdrawal ADD INDEX idx_type_status (type, status);

-- 会员流水变更记录
ALTER TABLE user_typing_amount_record DROP INDEX created_time_idx,ADD INDEX idx_site_code_created_time ( site_code, created_time );

-- 会员主货币账变记录
ALTER TABLE user_coin_record DROP INDEX idx_created_time;
ALTER TABLE user_coin_record DROP INDEX idx_user_account;
ALTER TABLE user_coin_record DROP INDEX idx_coin_type;

ALTER TABLE user_coin_record ADD INDEX idx_account_status_vip_rank_account_type_currency_coin_value (account_status,vip_rank,account_type,currency,coin_value);
ALTER TABLE user_coin_record ADD INDEX idx_business_coin_type_coin_type_balance_type (business_coin_type,coin_type,balance_type);

-- vip奖金发放记录
ALTER TABLE site_vip_award_record ADD INDEX idx_receive_expired( receive_status,expired_time);
ALTER TABLE site_vip_award_record DROP INDEX ix_user_account;

-- order_record优化
ALTER TABLE `bwintl_core`.`order_record`
DROP INDEX `idx_first_settle_time`;

ALTER TABLE `bwintl_core`.`order_record`
ADD INDEX `idx_user_id`(`user_id`) USING BTREE;

-- 账变优化
ALTER TABLE `bwintl_core`.`user_coin_record`
ADD INDEX `idx_ct_sc_btc_as`(`created_time` ASC, `site_code` ASC, `business_coin_type` ASC, `account_status` ASC) USING BTREE;



-- 会员活动流水变更记录
ALTER TABLE user_activity_typing_amount_record ADD INDEX idx_site_cde_created_time_user_account (site_code,created_time,user_account);


-- sql优化提交
ALTER TABLE site_task_order_record ADD INDEX idx_site_code_created_time( site_code,created_time);
ALTER TABLE site_task_order_record ADD INDEX idx_site_code_receive_time( site_code,receive_time);


-- 会员每日盈亏-消息体增加索引
ALTER TABLE report_user_win_lose_message ADD INDEX idx_win_lose_type_status(type_order, STATUS);
-- 会员每日盈亏 增加索引
ALTER TABLE report_user_win_lose ADD INDEX idx_report_win_lose_day_user_agent(day_hour_millis, user_id,agent_id);

-- 平台币兑换记录
ALTER TABLE user_platform_transfer_record ADD INDEX idx_site_code_order_time(site_code, order_time);
ALTER TABLE user_platform_transfer_record ADD INDEX idx_ui_ot_sc(user_id, order_time,site_code);

-- 会员活动记录
ALTER TABLE site_activity_order_record ADD INDEX idx_activity_code_tatus_sessionid(redbag_session_id,site_code, receive_status);

-- 会员领取任务
ALTER TABLE site_task_order_record DROP INDEX idx_receive_status;
ALTER TABLE site_task_order_record ADD INDEX idx_task_site_code_receive_status(site_code,user_id,receive_status);
-- VIP奖励发放记录
ALTER TABLE site_vip_award_record DROP INDEX idx_receive_expired;
ALTER TABLE site_vip_award_record ADD  INDEX`idx_receive_expired` (`expired_time`,`receive_status`);

-- 代理存提信息
ALTER TABLE agent_deposit_withdrawal ADD INDEX idx_site_code_status(site_code,`status`);
ALTER TABLE agent_deposit_withdrawal ADD INDEX idx_ai_ut_status(agent_id,updated_time,`status`);

-- 会员充值取款累计
alter table report_user_recharge_withdraw add index idx_ui_dhm_ua(user_id,day_hour_millis,user_account);
alter table user_info add index idx_ui_sc(user_id,site_code);
-- 会员盈亏添加索引
ALTER TABLE `report_user_win_lose`
    ADD INDEX `idx_dm_sc_at`(`day_hour_millis`, `site_code`, `account_type`);


ALTER TABLE `report_user_win_lose`
    ADD INDEX `idx_upt_st`(`updated_time`, `site_code`) USING BTREE;


alter table user_info add index idx_ri_sc(register_ip,site_code);
-- 会员存取信息
alter table user_deposit_withdrawal add index idx_ua_dwtc_sc_s(user_account,deposit_withdraw_type_code,site_code,status);

