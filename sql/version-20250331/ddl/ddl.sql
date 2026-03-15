-- 会员主货币账变记录，平台币账变记录 增加会员账号类型字段
ALTER TABLE user_coin_record ADD `account_type` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账号类型 1-测试 2-正式' AFTER account_status;

ALTER TABLE user_platform_coin_record ADD `account_type` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账号类型 1-测试 2-正式' AFTER account_status;




ALTER TABLE `site_task_config`
    ADD COLUMN `task_config_json` text NULL COMMENT '任务配置（每日任务-日累计存款；每周任务-邀请好友）' ;

ALTER TABLE `site_task_config_next`
    ADD COLUMN `task_config_json` text NULL COMMENT '任务配置（每日任务-日累计存款；每周任务-邀请好友）' ;


ALTER TABLE `site_task_order_record`
    ADD COLUMN `step` int NULL COMMENT '每日存款任务/每周邀请好友排序值' ;


ALTER TABLE `user_info`
    ADD COLUMN `ip_address` varchar(100) NULL COMMENT 'IP归属地' ;




CREATE TABLE `channel_sending_statistic` (
                                             `id` bigint NOT NULL COMMENT '主键',
                                             `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
                                             `site_name` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点名称',
                                             `channel_name` varchar(30) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '通道名称',
                                             `channel_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '通道代码',
                                             `channel_id` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '通道id',
                                             `host` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '通道地址',
                                             `channel_type` varchar(2) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '通道类型 1-短信 2-邮箱',
                                             `address` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '使用地区',
                                             `address_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '地区code',
                                             `receiver` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '接收者',
                                             `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                             PRIMARY KEY (`id`) /*T![clustered_index] NONCLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='短信/通道发送统计';


ALTER TABLE user_info DROP INDEX idx_site_code_account;
ALTER TABLE user_info DROP INDEX idx_site_code_phone;
ALTER TABLE user_info ADD INDEX idex_site_code_invite( `friend_invite_code`,`site_code`);
ALTER TABLE user_info ADD UNIQUE INDEX uk_site_code_phone(`site_code`, `phone`);

