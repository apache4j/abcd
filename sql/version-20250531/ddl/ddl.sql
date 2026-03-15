ALTER TABLE `site_activity_check_in`
    ADD COLUMN `reward_total` text NULL COMMENT '累计奖励配置' ;


ALTER TABLE `site_activity_check_in`
    ADD COLUMN `makeup_limit` integer NULL COMMENT '补签次数限制' ;

ALTER TABLE `site_activity_check_in`
    ADD COLUMN `free_wheel_pic` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL  COMMENT '免费旋转' ;

ALTER TABLE `site_activity_check_in`
    ADD COLUMN `spin_wheel_pic` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '转盘' ;

ALTER TABLE `site_activity_check_in`
    ADD COLUMN `amount_pic` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '奖金' ;

ALTER TABLE `site_activity_check_in`
    ADD COLUMN `make_deposit_amount` decimal(20, 2) NULL COMMENT '补签存款金额' ,
ADD COLUMN `make_bet_amount` decimal(20, 2) NULL COMMENT '补签有效投注金额' ;

CREATE TABLE `site_activity_makeup_count_record` (
                                                     `id` bigint NOT NULL COMMENT '主键ID',
                                                     `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点编码',
                                                     `activity_id` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动id',
                                                     `operation_type` tinyint(1) DEFAULT NULL COMMENT '操作类型：0-减少，1-增加',
                                                     `start_count` int DEFAULT NULL COMMENT '变更前次数',
                                                     `reward_count` int DEFAULT NULL COMMENT '变更次数',
                                                     `end_count` int DEFAULT NULL COMMENT '变更后次数',
                                                     `user_id` varchar(10) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '用户ID',
                                                     `user_account` varchar(100) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '用户账号',
                                                     `account_type` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账号类型',
                                                     `day_millis` bigint DEFAULT NULL COMMENT '站点日期的起始时间戳（毫秒）',
                                                     `day_str` varchar(19) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点日期字符串，仅供查看',
                                                     `order_number` varchar(64) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '奖励订单号（每日可重复）',
                                                     `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                                     `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                                     `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人',
                                                     `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '更新人',
                                                     PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                                     KEY `idx_user_day` (`user_id`,`day_millis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='签到活动 - 补签次数记录表';


CREATE TABLE `site_makeup_count_balance` (
                                             `id` bigint NOT NULL COMMENT '主键ID',
                                             `site_code` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
                                             `user_id` varchar(10) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员ID',
                                             `user_account` varchar(100) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员账号',
                                             `balance` int DEFAULT '0' COMMENT '当前补签次数余额',
                                             `month_millis` BIGINT DEFAULT NULL COMMENT '站点日期的起始时间戳（毫秒）',
                                             `month_str` VARCHAR(19) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点月字符串，仅供查看',
                                             `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                             `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                             `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                             `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                             PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                             UNIQUE KEY `uk_user_month` (`user_id`,`month_millis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='补签次数余额表';

ALTER TABLE `site_check_in_record`
    ADD COLUMN `reward_type` int NULL COMMENT '1-周奖励，2-月奖励，3-累计奖励' ;

ALTER TABLE `site_check_in_record`
    ADD COLUMN `reward_type_code` int NULL COMMENT '1-周奖励(1-7)，2-月奖励(1-12)，3-累计奖励' ;

alter table report_user_venue_win_lose add index idx_ai_dhm(agent_id,day_hour_millis);
alter table report_user_venue_win_lose add index idx_ui_dhm(user_id,day_hour_millis);
alter table report_user_venue_win_lose drop index idx_query;


CREATE TABLE `site_activity_free_game_balance` (
                                             `id` bigint NOT NULL COMMENT '主键ID',
                                             `site_code` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
                                             `user_id` varchar(10) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员ID',
                                             `user_account` varchar(100) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员账号',
                                             `balance` int DEFAULT '0' COMMENT '当前次数余额',
                                             `venue_code` varchar(64) DEFAULT NULL COMMENT '平台编号',
                                             `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                             `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                             `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                             `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                             PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                             UNIQUE KEY `uk_ui_vc` (`user_id`,`venue_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='免费旋转次数余额表';


DROP TABLE IF EXISTS `ip_address_area_currency`;
CREATE TABLE `ip_address_area_currency` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `category_id` VARCHAR(50)  DEFAULT NULL COMMENT '分类ID',
  `category_name` VARCHAR(50)  DEFAULT NULL COMMENT '分类名称',
  `area_code` TEXT DEFAULT NULL COMMENT '包含国家地区',
  `area_name` TEXT DEFAULT NULL COMMENT '包含国家',
  `currency_code` VARCHAR(20) DEFAULT NULL COMMENT '映射币种code',
  `currency_name` VARCHAR(20) DEFAULT NULL COMMENT '映射币种name',
  `order_sort` int DEFAULT '1' COMMENT '优先级',
  `status` int DEFAULT '0' COMMENT '状态: (1 开启, 0 已禁用)',
  `default_type` int DEFAULT '0' COMMENT '默认类型: (1 YES, 0 NO)',
  `remark` varchar(200)  DEFAULT '' COMMENT '备注',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(50) DEFAULT NULL,
  `updater` varchar(50) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='IP归属地方案表';

INSERT INTO ip_address_area_currency (`id`, `category_id`, `category_name`, `area_code`, `area_name`, `currency_code`, `currency_name`, `order_sort`, `status`, `default_type`, `remark`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1, '01ATBT', '默认方案', NULL, '[{"code":"all","name":"全部国家"}]', 'USDT', '泰达币', NULL, 1, 1,NULL, 1748710800000, 1749895542478, 'superAdmin', 'superAdmin');



ALTER TABLE system_rate_config
 ADD COLUMN  `site_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT '0' COMMENT '站点编号' AFTER `rate_type`;

ALTER TABLE `site_currency_info`
MODIFY COLUMN `final_rate` decimal(65, 2) NULL DEFAULT NULL COMMENT '转换后汇率' AFTER `currency_code`;


ALTER TABLE `site_activity_free_game_record`
    ADD COLUMN `receive_start_time` BIGINT NULL COMMENT '可领取开始时间',
ADD COLUMN `receive_end_time` BIGINT NULL COMMENT '可领取结束时间',
ADD COLUMN `receive_status` INT NULL COMMENT '已经过期',
ADD COLUMN `balance` INT NULL COMMENT '旋转次数余额',
ADD COLUMN `bet_limit_amount` DECIMAL(20,2) DEFAULT NULL COMMENT '限注金额';
ALTER TABLE `site_activity_free_game_record`
    ADD COLUMN `time_limit` bigint NULL COMMENT '次数时效' ;

ALTER TABLE `site_activity_free_game_record`
    ADD COLUMN `game_id` varchar(20) NULL COMMENT '游戏id' ;





ALTER TABLE `agent_info`  ADD COLUMN `fb_pix_id` varchar(512) NULL COMMENT 'FaceBook PixId' AFTER `merchant_name`;
ALTER TABLE `agent_info`  ADD COLUMN `fb_token` varchar(512) NULL COMMENT 'FaceBook Token' AFTER `fb_pix_id`;
ALTER TABLE `agent_info`  ADD COLUMN `google_pix_id` varchar(512) NULL COMMENT 'Google Ads PixId' AFTER `fb_token`;
ALTER TABLE `agent_info`  ADD COLUMN `google_token` varchar(512) NULL COMMENT 'Google Ads Token' AFTER `google_pix_id`;


ALTER TABLE `agent_info_modify_review`  ADD COLUMN `after_modification_json` varchar(512) NULL COMMENT '修改后数据json格式' AFTER `application_information`;



ALTER TABLE `agent_merchant_modify_review`
MODIFY COLUMN `before_fixing` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL COMMENT ' 修改前' AFTER `review_application_type`,
MODIFY COLUMN `after_modification` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL COMMENT ' 修改后' AFTER `before_fixing`;




ALTER TABLE `agent_info_modify_review`
MODIFY COLUMN `before_fixing` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL COMMENT ' 修改前\n\n对应审核类型审核前最近的原始数据信息\n\n' AFTER `agent_type`,
MODIFY COLUMN `after_modification` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL COMMENT ' 修改后\n\n对应审核类型审核时需要修改的数据信息\n\n' AFTER `before_fixing`;





CREATE TABLE `game_one_currency_sort` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                          `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点CODE',
                                          `game_one_id` bigint NOT NULL COMMENT 'geme_one_class_info.id',
                                          `currency_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '货币代码',
                                          `directory_sort` int DEFAULT '100' COMMENT '目录排序',
                                          `home_sort` int DEFAULT '100' COMMENT '首页排序',
                                          `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                          `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                          `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                          UNIQUE KEY `uk_game_one_id_currency_code` (`game_one_id`,`currency_code`) COMMENT '一个一级分类一个币种唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='一级分类-币种排序';


CREATE TABLE `game_two_currency_sort` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                          `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点CODE',
                                          `game_id` bigint DEFAULT NULL,
                                          `game_join_id` bigint NOT NULL COMMENT 'game_join_class.id',
                                          `game_two_id` bigint NOT NULL COMMENT 'game_two_class.id',
                                          `game_one_id` bigint DEFAULT NULL COMMENT '一级分类',
                                          `currency_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '货币代码',
                                          `sort` int DEFAULT '1' COMMENT '排序',
                                          `game_one_home_sort` int NOT NULL DEFAULT '1' COMMENT '一级分类-首页游戏-排序',
                                          `game_one_hot_sort` int NOT NULL DEFAULT '1' COMMENT '首页- 一级分类-热门排序',
                                          `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                          `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                          `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                          PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                          UNIQUE KEY `uk_game_join_id_currency_code` (`game_join_id`,`currency_code`) COMMENT '游戏关联二级分类ID与币种唯一',
                                          UNIQUE KEY `uk_game_id_game_two_id_currency_code` (`game_id`,`game_two_id`,`currency_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin AUTO_INCREMENT=396268903199950129 COMMENT='game_join_class表,二级分类-币种排序';




CREATE TABLE `site_game_hot_sort` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                      `site_game_id` bigint DEFAULT NULL COMMENT 'site_game.id',
                                      `game_id` bigint DEFAULT NULL COMMENT 'game_info.id',
                                      `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点CODE',
                                      `currency_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '货币代码',
                                      `home_hot_sort` int NOT NULL DEFAULT '100' COMMENT '首页- 热门排序',
--   `game_one_home_sort` int NOT NULL DEFAULT '100' COMMENT '首页 一级分类-排序',
                                      `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                      `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                      `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                      `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                      PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                      UNIQUE KEY `uk_site_code_currency_code_site_game_id` (`site_code`,`currency_code`,`site_game_id`),
                                      UNIQUE KEY `uk_site_code_currency_code_game_info_id` (`site_code`,`currency_code`,`game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='站点-游戏-币种-首页热门排序';



ALTER TABLE game_info ADD `currency_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '货币代码' after venue_code;

ALTER TABLE site_game ADD `currency_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '货币代码' after venue_code;

-- uat/生产执行 单独执行

CREATE TABLE `site_activity_free_game_consume` (
                                                   `id` bigint NOT NULL COMMENT '主键ID',
                                                   `site_code` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
                                                   `user_id` varchar(10) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员ID',
                                                   `user_account` varchar(100) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '会员账号',
                                                   `balance` int DEFAULT '0' COMMENT '当前次数余额',
                                                   `venue_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '平台编号',
                                                   `game_id` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏ID',
                                                   `bet_win_lose` decimal(22,4) DEFAULT NULL COMMENT '投注盈亏',
                                                   `order_no` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '获取来源订单号',
                                                   `bet_id` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '投注单号',
                                                   `created_time` bigint DEFAULT NULL COMMENT '创建时间（时间戳）',
                                                   `updated_time` bigint DEFAULT NULL COMMENT '更新时间（时间戳）',
                                                   `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人',
                                                   `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '更新人',
                                                   `consume_count` int DEFAULT NULL COMMENT '消耗次数\n',
                                                   PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                                   KEY `idx_user_venue` (`user_id`,`venue_code`),
                                                   KEY `idx_bet_site` (`bet_id`,`site_code`),
                                                   KEY `idx_user_site` (`user_account`,`site_code`),
                                                   KEY `idx_created_site` (`created_time`,`site_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='免费旋转次数消费记录';



-- site_activity_free_game_record uat
ALTER TABLE `site_activity_free_game_record`
    ADD INDEX `idx_create_time` (`created_time`);

ALTER TABLE `site_activity_free_game_record`
    ADD INDEX `idx_order_no` (`order_no`);

ALTER TABLE `site_activity_free_game_record`
    ADD INDEX `idx_sc_uc` (`user_account`, `site_code`);


ALTER TABLE `site_activity_free_game_record`
    ADD column `bet_win_lose` decimal(22,4) DEFAULT NULL COMMENT '投注盈亏';
ALTER TABLE `site_activity_free_game_record`
    ADD COLUMN `order_type` int default 1 COMMENT '来源1-活动，2-配置' ;

-- site_activity_free_game_record 生产执行
ALTER TABLE `site_activity_free_game_record`
    MODIFY COLUMN `bet_win_lose` decimal(22, 4) NULL DEFAULT NULL COMMENT '投注盈亏' ;

ALTER TABLE `site_activity_free_game_record`
    ADD COLUMN `order_type` int default 1 COMMENT '来源1-活动，2-配置' ;


alter table site_activity_free_game_record add index idx_sc_rs(site_code,receive_status);


