INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (113124450, 'notice_business_type', '1', 'LOOKUP_11484', '全部商务', '商务对象', 1, NULL, '1', NULL);

INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (113124451, 'notice_business_type', '2', 'LOOKUP_11485', '特定商务', '商务对象', 1, NULL, '1', NULL);


INSERT INTO `i18n_message` (`id`, `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (2006318659206313230, 'BACK_END', 'LOOKUP_11485', 'en-US', 'Specific Business', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` (`id`, `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (2006318659206313231, 'BACK_END', 'LOOKUP_11485', 'zh-TW', '特定商務', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` (`id`, `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (2006318659206313232, 'BACK_END', 'LOOKUP_11485', 'pt-BR', 'business específico', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` (`id`, `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (2006318659206313233, 'BACK_END', 'LOOKUP_11485', 'zh-CN', '特定商务', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` (`id`, `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (2006318659206313234, 'BACK_END', 'LOOKUP_11485', 'vi-VN', 'kinh doanh cụ thể', 1, 1736152779752, '1', NULL);


INSERT INTO`i18n_message` (`id`, `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (2006318659206313235, 'BACK_END', 'LOOKUP_11484', 'en-US', 'All business', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` (`id`, `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (2006318659206313236, 'BACK_END', 'LOOKUP_11484', 'zh-TW', '全部商務', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` (`id`, `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (2006318659206313239, 'BACK_END', 'LOOKUP_11484', 'pt-BR', 'Todos os negócios', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` (`id`, `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (2006318659206313237, 'BACK_END', 'LOOKUP_11484', 'zh-CN', '全部商务', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` (`id`, `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (2006318659206313238, 'BACK_END', 'LOOKUP_11484', 'vi-VN', 'Tất cả hoạt động kinh doanh', 1, 1736152779752, '1', NULL);


-- 添加游戏
delete  from `system_param` where value = 'LOOKUP_VENUE_INIT_NAME_CQ9';
INSERT INTO `system_param` (`type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'venue_code', 'CQ9', 'LOOKUP_VENUE_INIT_NAME_CQ9', 'CQ9电子', 'CQ9电子', 1, NULL, '1', NULL);
delete  from `i18n_message` where message_key = 'LOOKUP_VENUE_INIT_NAME_CQ9';
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_CQ9', 'vi-VN', 'CQ9 điện tử', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_CQ9', 'zh-CN', 'CQ9电子', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_CQ9', 'pt-BR', 'Eletrônica CQ9', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_CQ9', 'zh-TW', 'CQ9電子', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_CQ9', 'en-US', 'CQ9 Slots', 1, 1735274608799, '1', NULL);
-- 更新翻译
UPDATE `i18n_message` SET message = 'Partners', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_10880' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Bên thứ ba', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_10880' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Manual Withdrawal', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_10881' AND language = 'en-US';
UPDATE `i18n_message` SET message = '線下', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_10881' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Rút tiền thủ công', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_10881' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'your name', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_11332' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Họ Tên', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_11332' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Result-/-First Team To Score', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_SBA_BET_TYPE_172' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Result-/-First Team ToScore', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_SBA_BET_TYPE_415' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Thưởng', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_11694' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'your name', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_11768' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Họ Tên', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_11768' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'FTG捕魚', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_FTG' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'FTG Bắn cá', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_FTG' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'PPPLUS Nổ hũ', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_PPPLUS' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'PGPLUS Nổ hũ', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_PGPLUS' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'JILIPLUS Nổ hũ', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_JILIPLUS' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Provider negative profit rate', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_venue_proportion_type_0' AND language = 'en-US';
UPDATE `i18n_message` SET message = '場館負盈利費率', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_venue_proportion_type_0' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Tỷ lệ phí lợi nhuận âm của sảnh', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_venue_proportion_type_0' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Negative profit + effective turnover rate', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_venue_proportion_type_2' AND language = 'en-US';
UPDATE `i18n_message` SET message = '負盈利&有效流水費率', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_venue_proportion_type_2' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Tỷ lệ lợi nhuận âm & Tỷ lệ phí doanh thu hợp lệ', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_venue_proportion_type_2' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Provider effective turnover rate', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_venue_proportion_type_1' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Tỷ lệ phí doanh thu hợp lệ của sảnh', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_venue_proportion_type_1' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Channels', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_12700' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Bên thứ ba', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_12700' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Rút tiền thủ công', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_12701' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Disabled', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_12710' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Enabled', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_12711' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Maintain', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_12712' AND language = 'en-US';
UPDATE `i18n_message` SET message = '維護', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_12712' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Maintenance page', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_10899' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Website customer service', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_PLATFORM_REPLY' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'CSKH nền tảng', `updated_time` = '1740992320545' WHERE message_key = 'LOOKUP_PLATFORM_REPLY' AND language = 'vi-VN';


UPDATE `i18n_message` SET message = 'Win', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_w' AND language = 'en-US';
UPDATE `i18n_message` SET message = '贏', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_w' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Thắng', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_w' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Unknown', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_n' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Không biết', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_n' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Lose', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_l' AND language = 'en-US';
UPDATE `i18n_message` SET message = '輸', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_l' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Thua', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_l' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Rollback', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_c' AND language = 'en-US';
UPDATE `i18n_message` SET message = '回滾', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_c' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Quay lại', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_order_status_c' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Table', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_game_type_table' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Slots', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_game_type_slots' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Nổ hũ', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_game_type_slots' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Fishing', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_game_type_fishing' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Bắn cá', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_ftg_game_type_fishing' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'CQ9 Nổ hũ', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_CQ9' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Specific sales', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_11485' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Kinh doanh cụ thể', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_11485' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'All sales', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_11484' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Tất cả kinh doanh', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_11484' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = '捕魚', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_10967' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Bắn cá', `updated_time` = '1741075204303' WHERE message_key = 'LOOKUP_10967' AND language = 'vi-VN';


UPDATE `i18n_message` SET message = 'Canceled', `updated_time` = '1741867934477' WHERE message_key = 'LOOKUP_COIN_TYPE_13' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'FTG Fishing', `updated_time` = '1741867934477' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_FTG' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'FTG捕魚', `updated_time` = '1741867934477' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_FTG' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'FTG Bắn cá', `updated_time` = '1741867934477' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_FTG' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Canceled', `updated_time` = '1741867934477' WHERE message_key = 'LOOKUP_11632' AND language = 'en-US';
UPDATE `i18n_message` SET message = '已取消', `updated_time` = '1741867934477' WHERE message_key = 'LOOKUP_11632' AND language = 'zh-TW';