CREATE TABLE `site_task_flash_card_base` (
                                             `id` bigint NOT NULL COMMENT '主键id',
                                             `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
                                             `task_type` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '任务类型',
                                             `activity_name_i18n_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动名称-多语言',
                                             `activity_introduce_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动简介',
                                             `account_type` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动生效的账户类型',
                                             `show_terminal` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动展示终端',
                                             `entrance_picture_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '入口图-移动端',
                                             `entrance_picture_pc_i18n_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '入口图-PC端',
                                             `status` int DEFAULT NULL COMMENT '状态 0已禁用 1开启中',
                                             `created_time` bigint DEFAULT NULL,
                                             `updated_time` bigint DEFAULT NULL,
                                             `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                             `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                             PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                             UNIQUE KEY  `ux_sc_ty` (`site_code`,`task_type`) COMMENT '站点索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='任务图卡-基本表';

ALTER TABLE `site_activity_base`
    ADD COLUMN `forbid_time` bigint NULL COMMENT '活动禁用操作时间' ;

-- 检索信息配置表
CREATE TABLE `site_seo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `site_code` varchar(50) NOT NULL COMMENT '站点CODE',
  `title` varchar(50) NOT NULL COMMENT '标题',
  `meta` varchar(160)  NOT NULL COMMENT '网站摘要',
  `lang` varchar(50)  NOT NULL COMMENT '语言',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(50)  DEFAULT NULL,
  `updater` varchar(50)  DEFAULT NULL,
   PRIMARY KEY (`id`)
) AUTO_INCREMENT=10000 COMMENT='站点检索信息配置';
ALTER TABLE site_seo ADD CONSTRAINT uq_site_lang UNIQUE (site_code,lang);


CREATE TABLE site_download_config (
                                      id BIGINT NOT NULL PRIMARY KEY COMMENT '主键',
                                      site_code VARCHAR(64) NOT NULL COMMENT '站点编码',
                                      jump_type VARCHAR(10) DEFAULT NULL COMMENT '下载调整 1-安装包 2-域名地址',
                                      android_download_url VARCHAR(500) DEFAULT NULL COMMENT '安卓下载地址',
                                      ios_download_url VARCHAR(500) DEFAULT NULL COMMENT 'iOS下载地址',
                                      domain_url VARCHAR(500) DEFAULT NULL COMMENT 'optionType=2 的时候存',
                                      icon VARCHAR(255) DEFAULT NULL COMMENT '下载图标',
                                      banner VARCHAR(255) DEFAULT NULL COMMENT '轮播图配置',
                                      creator VARCHAR(64) DEFAULT NULL COMMENT '创建人',
                                      created_time BIGINT DEFAULT NULL COMMENT '创建时间（时间戳）',
                                      updater VARCHAR(64) DEFAULT NULL COMMENT '更新人',
                                      updated_time BIGINT DEFAULT NULL COMMENT '更新时间（时间戳）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点下载配置表';



ALTER TABLE report_agent_static_day  ADD COLUMN `deposit_total_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '存款总额' AFTER `deposit_withdraw_fee_amount`;
ALTER TABLE report_agent_static_day  ADD COLUMN `withdraw_total_amount` decimal(20, 4) NULL DEFAULT 0.00 COMMENT '提款总额' AFTER `deposit_total_amount`;


ALTER TABLE agent_login_record ADD COLUMN agent_label_id TEXT  COMMENT '代理标签';

ALTER TABLE `report_user_deposit_withdraw`  ADD COLUMN `deposit_subordinates_nums` INT(10) DEFAULT 0 COMMENT '代理代存人数' AFTER `deposit_total_amount`;
ALTER TABLE `report_user_deposit_withdraw`  ADD COLUMN `deposit_subordinates_times` INT(10) DEFAULT 0 COMMENT '代理代存次数' AFTER `deposit_subordinates_nums`;
ALTER TABLE `report_user_deposit_withdraw`  ADD COLUMN `deposit_subordinates_amount` DECIMAL(22,4) DEFAULT 0 COMMENT '代理代存金额' AFTER `deposit_subordinates_times`;

ALTER TABLE user_withdrawal_manual_record MODIFY COLUMN `deposit_withdraw_surname` VARCHAR ( 255 ) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '存取款姓';
ALTER TABLE agent_withdrawal_manual_record MODIFY COLUMN `deposit_withdraw_surname` VARCHAR ( 255 ) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '存取款姓';

ALTER TABLE `cq9_transaction_record`
    ADD COLUMN `round_array` varchar(2000) NULL COMMENT '2一组请求的mtcode' ;
