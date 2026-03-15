UPDATE site_activity_second_recharge SET venue_type = NULL;
UPDATE site_activity_first_recharge  SET venue_type = NULL;
UPDATE site_activity_assign_day  SET venue_type = NULL;

ALTER TABLE `user_login_info`
    ADD INDEX `idx_user_id`(`user_id`) USING BTREE;

-- 会员登录版本号
ALTER TABLE user_login_info ADD version varchar(100) NULL COMMENT '版本号';
ALTER TABLE user_login_info ADD super_agent_account varchar(30) NULL COMMENT '上级代理账号';


ALTER TABLE `site_vip_rank_currency_config`
    ADD COLUMN `daily_withdrawal_nums_limit` int DEFAULT NULL COMMENT '单日提款次数上限' AFTER `day_withdraw_limit`;

ALTER TABLE `site_vip_rank_currency_config`
    ADD COLUMN `daily_withdraw_amount_limit` decimal(20,2) DEFAULT NULL COMMENT '单日提款额度最大值' AFTER `daily_withdrawal_nums_limit`;

ALTER TABLE `user_withdraw_config`
    ADD COLUMN `daily_withdrawal_nums_limit` int DEFAULT NULL COMMENT '单日提款次数上限-非免费' AFTER `single_max_withdraw_amount`;

ALTER TABLE `user_withdraw_config`
    ADD COLUMN `daily_withdraw_amount_limit` decimal(20,2) DEFAULT NULL COMMENT '单日提款额度最大值-非免费' AFTER `daily_withdrawal_nums_limit`;

CREATE TABLE `user_withdraw_config_detail` (
                                               `id` bigint NOT NULL COMMENT 'ID',
                                               `site_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点编码',
                                               `user_id` bigint DEFAULT NULL COMMENT '会员ID',
                                               `user_account` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '会员账号',
                                               `currency_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '货币代码',
                                               `vip_rank_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '段位',
                                               `day_withdraw_count` int DEFAULT NULL COMMENT '单日提款次数上限',
                                               `max_withdraw_amount` decimal(20,2) DEFAULT NULL COMMENT '单日提款金额上限',
                                               `single_day_withdraw_count` int DEFAULT NULL COMMENT '单日免费提款',
                                               `single_max_withdraw_amount` decimal(20,2) DEFAULT NULL COMMENT '单日免费提款总额',
                                               `creator` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建者',
                                               `updater` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '更新者',
                                               `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                               `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                               PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='会员提款设置详情-单个会员特殊配置';


CREATE TABLE `site_new_user_guide_step_record` (
                                                   `id` BIGINT NOT NULL COMMENT '主键',
                                                   `site_code` VARCHAR(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
                                                   `user_id` VARCHAR(10) NOT NULL COMMENT '用户ID（唯一）',
                                                   `user_account` VARCHAR(100) NOT NULL COMMENT '会员账号',
                                                   `step` INT DEFAULT NULL COMMENT '完成步骤',
                                                   `created_time` BIGINT DEFAULT NULL COMMENT '创建时间',
                                                   `updated_time` BIGINT DEFAULT NULL COMMENT '修改时间',
                                                   `creator` VARCHAR(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                   `updater` VARCHAR(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                                   PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                                   UNIQUE KEY `uk_user_id` (`user_id`),
                                                   UNIQUE KEY `uk_site_user_account` (`site_code`, `user_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='新手指引步骤记录表';


ALTER TABLE `site_user_invite_record`
    ADD COLUMN `valid_first_deposit` int DEFAULT NULL COMMENT '有效首存达标 0-否 1-是' AFTER `register_time`;

ALTER TABLE `site_user_invite_record`
    ADD COLUMN `valid_total_deposit` int DEFAULT NULL COMMENT '累计存款达标 0-否 1-是' AFTER `valid_first_deposit`;


ALTER TABLE `site_user_invite_record`
    ADD COLUMN `first_deposit_time`  bigint DEFAULT NULL COMMENT '首存时间' AFTER `valid_total_deposit`;

ALTER TABLE `site_user_invite_record`
    ADD COLUMN `first_deposit_amount`  decimal(20,2) DEFAULT 0 COMMENT '首存金额' AFTER `first_deposit_time`;

ALTER TABLE `site_user_invite_record`
    ADD COLUMN `deposit_amount_total`   decimal(20,2) DEFAULT 0 COMMENT '累计存款金额' AFTER `first_deposit_amount`;



ALTER TABLE `site_activity_second_recharge`
    MODIFY COLUMN `discount_type` int NULL COMMENT '优惠方式类型，0.百分比，1.固定' ;



ALTER TABLE `user_registration_info`
    ADD INDEX `idx_site_create_account`(`site_code`, `registration_time`, `member_account`);


ALTER TABLE `user_registration_info`
    ADD UNIQUE INDEX `uk_member_id`(`member_id`) USING BTREE;

CREATE TABLE `site_info_change_record` (
  `id` bigint NOT NULL COMMENT '主键，唯一标识每条记录',
  `option_code` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '操作对象名称，对应站点名称',
  `option_name` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '操作对象code，对应站点编码',
  `option_model_name` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '操作模块名称',
  `option_type` tinyint DEFAULT NULL COMMENT '0:新增、1:修改,2:删除',
  `option_status` tinyint DEFAULT NULL COMMENT '状态(0:失败,1:成功)',
  `login_ip` varchar(21) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '登入ip',
  `change_after` text COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '变更后的状态描述json保存',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人，记录创建操作的用户',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间（以时间戳形式表示）',
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人，记录创建操作的用户',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间（以时间戳形式表示）',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='场馆操作日记记录表';