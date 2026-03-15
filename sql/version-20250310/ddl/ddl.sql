-- 字典值新增数据类型
ALTER TABLE system_dict_config ADD `type` INT NULL COMMENT '字典值类型,1.数字,2.字符串';



ALTER TABLE `venue_info`
    MODIFY COLUMN `aes_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT 'AES密钥' ;


DROP TABLE IF EXISTS cq9_transaction_record;
CREATE TABLE `cq9_transaction_record` (
                                          `id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '交易记录编号',
                                          `action` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '交易动作',
                                          `account` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '用户账号',
                                          `create_time` timestamp NULL DEFAULT NULL COMMENT '交易开始时间',
                                          `end_time` timestamp NULL DEFAULT NULL COMMENT '交易结束时间',
                                          `status` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '交易状态success:成功,refund:退款,一律回覆1014',
                                          `before` decimal(16,4) DEFAULT NULL COMMENT '交易前余额',
                                          `balance` decimal(16,4) DEFAULT NULL COMMENT '交易后余额',
                                          `currency` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币别',
                                          `status_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '狀態訊息',
                                          `status_message` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '狀態訊息',
                                          `response_time` timestamp NULL DEFAULT NULL COMMENT '回傳時間',
                                          `mtcode` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '交易代碼，唯一值',
                                          `amount` decimal(16,4) DEFAULT NULL COMMENT '該筆交易的金額',
                                          `event_time` timestamp NULL DEFAULT NULL COMMENT '事件發送時間,CQ9发送交易时间',
                                          `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                          `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                          `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          `request_json` varchar(2000) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '请求入参',
                                          `round_id` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '注單號',
                                          `balance_type` char(1) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '收支类型1收入,2支出',
                                          KEY `idx_mtcode` (`mtcode`),
                                          PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='交易记录表';


ALTER TABLE `agent_rebate_config`
ADD COLUMN `fish_rate` varchar(10) NULL COMMENT '捕鱼返点比例' AFTER `cockfight_rate`;




ALTER TABLE user_notice_config ADD COLUMN notice_merchant_type INT DEFAULT NULL COMMENT '1:全部商务 2:特定商务\n' AFTER notice_agent_type;

-- 转代溢出补充会员id
ALTER TABLE agent_user_overflow ADD user_id varchar(20) NULL COMMENT '会员id';
ALTER TABLE user_transfer_agent ADD user_id varchar(20) NULL COMMENT '会员id';
-- 删除无用索引
ALTER TABLE agent_user_overflow
DROP INDEX idx_member_name;
-- 调整数据库表,字段字符集
ALTER TABLE agent_user_overflow
MODIFY COLUMN member_name varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员账号',
MODIFY COLUMN account_type varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '账号类型;0-试玩,1-测试,2-正式,3-商务,4-置换',
MODIFY COLUMN transfer_agent_name varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '转入上级代理账号',
MODIFY COLUMN link varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '推广链接',
MODIFY COLUMN image varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '上传图片',
MODIFY COLUMN lock_name varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '锁单人',
MODIFY COLUMN audit_name varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '审核人',
MODIFY COLUMN audit_remark varchar(800) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '审核备注',
MODIFY COLUMN event_id varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '单号',
MODIFY COLUMN apply_name varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '申请人',
MODIFY COLUMN apply_remark varchar(800) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '申请备注',
MODIFY COLUMN user_register varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '会员注册信息',
MODIFY COLUMN transfer_agent_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '转入代理id',
MODIFY COLUMN user_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '会员id',
MODIFY COLUMN site_code varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '站点编码';


-- 调整数据库表,字段字符集
ALTER TABLE user_transfer_agent
MODIFY COLUMN user_account varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员ID',
MODIFY COLUMN site_code varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '站点编码',
MODIFY COLUMN current_agent_name varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '当前上级代理账号',
MODIFY COLUMN current_agent_id varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '当前上级代理id',
MODIFY COLUMN transfer_agent_name varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '转入上级代理账号',
MODIFY COLUMN transfer_agent_id varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '转代后的上级代理id',
MODIFY COLUMN locker_id varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '锁单人id',
MODIFY COLUMN lock_name varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '锁单人',
MODIFY COLUMN audit_id varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '审核人id',
MODIFY COLUMN audit_name varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '审核人',
MODIFY COLUMN audit_remark varchar(800) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '审核备注',
MODIFY COLUMN event_id varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '单号',
MODIFY COLUMN apply_id varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '申请人id',
MODIFY COLUMN apply_name varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '申请人',
MODIFY COLUMN apply_remark varchar(800) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '申请备注',
MODIFY COLUMN user_id varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '会员id';
-- 活动
ALTER TABLE `site_activity_first_recharge` ADD COLUMN `venue_type` varchar(50) NULL COMMENT '游戏大类' ;

ALTER TABLE `site_activity_second_recharge` ADD COLUMN `venue_type` varchar(50) NULL COMMENT '游戏大类' ;

ALTER TABLE `site_activity_assign_day` ADD COLUMN `venue_type` varchar(50) NULL COMMENT '游戏大类' ;


CREATE TABLE `user_activity_typing_amount` (
                                               `id` bigint NOT NULL,
                                               `user_account` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账号',
                                               `site_code` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
                                               `typing_amount` decimal(20,2) DEFAULT NULL COMMENT '打码量',
                                               `start_time` bigint DEFAULT NULL COMMENT '流水限制开始时间',
                                               `end_time` bigint DEFAULT NULL COMMENT '流水限制结束时间',
                                               `user_id` bigint DEFAULT NULL COMMENT '会员ID',
                                               `currency` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
                                               `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                               `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                               `limit_game_type` varchar(2) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '限制游戏:VenueTypeEnum,',
                                               PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                               UNIQUE KEY `uk_user_id` (`user_id`) COMMENT '唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='会员存款活动打码量信息';

CREATE TABLE `user_activity_typing_amount_record` (
                                                      `id` bigint NOT NULL,
                                                      `user_account` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账号',
                                                      `user_id` bigint DEFAULT NULL COMMENT '会员ID',
                                                      `msg_id` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT '' COMMENT 'mq消息id',
                                                      `account_type` varchar(2) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '账号类型',
                                                      `site_code` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
                                                      `coin_value` decimal(20,2) DEFAULT NULL COMMENT '打码量',
                                                      `coin_from` decimal(20,2) DEFAULT NULL COMMENT '打码量(变动前)',
                                                      `coin_to` decimal(20,2) DEFAULT NULL COMMENT '打码量(变动后)',
                                                      `adjust_type` varchar(2) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'TypingAmountAdjustTypeEnum',
                                                      `adjust_way` varchar(2) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '增减类型(1-增加,2-减少)',
                                                      `order_no` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '关联订单号',
                                                      `created_time` bigint DEFAULT NULL COMMENT '流水时间...',
                                                      `currency` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
                                                      `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                      PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='会员存款活动打码量信息记录表';

-- 会员流水变更记录增加消息id字段 唯一索引修改
ALTER TABLE user_typing_amount_record ADD msg_id VARCHAR(50) DEFAULT '' COMMENT 'mq消息id' AFTER user_id ;
ALTER TABLE user_typing_amount_record DROP INDEX order_no_uni_idx;
ALTER TABLE user_typing_amount_record ADD UNIQUE INDEX order_no_adjust_way_msg_id_uni_idx (order_no,adjust_way,msg_id);


CREATE TABLE `report_top_agent_static_day` (
  `id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '主键id',
  `day_millis` bigint DEFAULT NULL COMMENT '站点日期 当天起始时间戳',
  `report_type` varchar(19) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '报表统计类型 0:日报 1:月报',
  `report_date` varchar(19) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '报表统计日期 天或者月',
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
  `agent_id` varchar(10) DEFAULT NULL COMMENT '代理编号',
  `agent_account` varchar(15) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '代理账号',
  `parent_id` varchar(10) DEFAULT NULL COMMENT '父节点 存父节点agent_id',
  `parent_account` varchar(64) DEFAULT NULL COMMENT '父节点 存父节点agent_account',
  `path` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '层次id 逗号分隔',
  `level` int DEFAULT NULL COMMENT '代理层级',
  `agent_type` int DEFAULT NULL COMMENT '代理类型 1正式 2商务 3置换',
  `register_time` bigint DEFAULT NULL COMMENT '注册时间',
  `agent_attribution` int DEFAULT NULL COMMENT '代理归属 1推广 2招商 3官资',
  `agent_label_id` text DEFAULT NULL COMMENT '代理标签id',
  `risk_level_id` bigint DEFAULT NULL COMMENT '风控层级id',
  `merchant_account` varchar(15) DEFAULT NULL COMMENT '商务账号',
  `merchant_name` varchar(50) DEFAULT NULL COMMENT '商务名称',
  `team_agent_num` bigint DEFAULT '0' COMMENT '团队代理人数',
  `direct_report_num` bigint DEFAULT '0' COMMENT '直属下级人数',
  `user_num` bigint DEFAULT '0' COMMENT '团队会员人数',
  `register_user_num` bigint DEFAULT '0' COMMENT '注册人数',
  `first_deposit_num` bigint DEFAULT '0' COMMENT '首存人数',
  `first_deposit_rate` decimal(20,4) DEFAULT '0' COMMENT '首存转换率',
  `bet_user_num` bigint DEFAULT '0' COMMENT '投注人数',
  `bet_user_count` bigint DEFAULT '0' COMMENT '投注人次',
  `currency_code` varchar(32) DEFAULT NULL COMMENT '币种',
  `bet_amount` decimal(20,2) DEFAULT '0.00' COMMENT '投注额',
  `valid_amount` decimal(20,2) DEFAULT '0.00' COMMENT '有效投注额',
  `win_loss_amount_user` decimal(20,2) DEFAULT '0.00' COMMENT '会员输赢',
  `win_loss_amount_plat` decimal(20,2) DEFAULT '0.00' COMMENT '平台总输赢',
  `adjust_amount` decimal(20,2) DEFAULT '0.00' COMMENT '调整金额',
  `win_loss_rate` decimal(20,4) DEFAULT '0.00' COMMENT '盈亏比例',
  `activity_amount` decimal(20,2) DEFAULT '0.00' COMMENT '活动优惠',
  `vip_amount` decimal(20,2) DEFAULT '0.00' COMMENT 'vip福利',
  `already_use_amount` decimal(20,2) DEFAULT '0.00' COMMENT '已经使用优惠,活动发放是主货币，前端把平台币转换为主货币',
  `deposit_amount` decimal(32,2) DEFAULT '0.00' COMMENT '存款金额',
  `withdraw_amount` decimal(32,2) DEFAULT '0.00' COMMENT '取款金额',
  `deposit_withdraw_fee_amount` decimal(20,2) DEFAULT '0.00' COMMENT '存提手续费',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '修改人',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='商务总代报表';


