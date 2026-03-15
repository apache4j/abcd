
-- 新增FTG游戏场馆配置
INSERT INTO venue_info (`id`, venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_name, venue_icon, pc_venue_icon,
                        h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url,
                        merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time,
                        name_prefix, creator, `updater`)
VALUES (18299887735236946, 'FTG', 2, 'CNY,MYR,VND,USDT,USD', 8, 'FTG', 'FTG捕鱼', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0.00, 0, 1, NULL,
        'https://asia.h93r.com/', NULL, 'e6bd0841', 'b6ddd7cfc1134899f50740af1d7bd52b', 'aaac6629', NULL, 'davis01', 1724326423777, 'davis02', 1739952947703, NULL, NULL, NULL, NULL, 'superAdmin02', 'sheldon0987');


-- 新增捕鱼场馆类型
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1229898313233, 'venue_type', '8', 'LOOKUP_10967', '捕鱼', '平台类型', 20240806223102, NULL, '1', NULL);

-- 新增FTG场馆名称
INSERT INTO system_param (`id`, type, code, value, value_desc, description, created_time, updated_time, creator, `updater`) VALUES (1121312319, 'venue_code', 'FTG', 'LOOKUP_VENUE_INIT_NAME_FTG', 'FTG捕鱼', 'FTG捕鱼', 1, NULL, '1', NULL);

-- 新增FTG场馆名称多语言
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_FTG', 'en-US', 'FTGFishing', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_FTG', 'zh-TW', 'FTG捕鱼', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_FTG', 'pt-BR', 'FTG Pesca', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_FTG', 'zh-CN', 'FTG捕鱼', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_FTG', 'vi-VN', 'FTGCâu cá', 1, 1736152779752, '1', NULL);

-- 新增FTG 场馆类型名称 多语言
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10967', 'en-US', 'Fishing', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10967', 'zh-TW', '捕鱼', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10967', 'pt-BR', 'Pesca', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10967', 'zh-CN', '捕鱼', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10967', 'vi-VN', 'Câu cá', 1, 1736152779752, '1', NULL);



-- FTG场馆结果状态类型
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313233, 'ftg_order_status', 'N', 'LOOKUP_ftg_order_status_n', '未知', '平台类型', 20240806223102, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313234, 'ftg_order_status', 'W', 'LOOKUP_ftg_order_status_w', '赢', '平台类型', 20240806223102, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313235, 'ftg_order_status', 'L', 'LOOKUP_ftg_order_status_l', '输', '平台类型', 20240806223102, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313236, 'ftg_order_status', 'C', 'LOOKUP_ftg_order_status_c', '回滚', '平台类型', 20240806223102, NULL, '1', NULL);



-- FTG场馆结果状态类型
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313152, 'ftg_order_status', 'N', 'LOOKUP_ftg_order_status_n', '未知', '平台类型', 20240806223102, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313153, 'ftg_order_status', 'W', 'LOOKUP_ftg_order_status_w', '赢', '平台类型', 20240806223102, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313154, 'ftg_order_status', 'L', 'LOOKUP_ftg_order_status_l', '输', '平台类型', 20240806223102, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313155, 'ftg_order_status', 'C', 'LOOKUP_ftg_order_status_c', '回滚', '平台类型', 20240806223102, NULL, '1', NULL);




-- FTG场馆结果状态类型 多语言
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_n', 'en-US', 'unknown', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_n', 'zh-TW', '未知', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_n', 'pt-BR', 'desconhecido', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_n', 'zh-CN', '未知', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_n', 'vi-VN', 'không rõ', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_w', 'en-US', 'win', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_w', 'zh-TW', '赢', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_w', 'pt-BR', 'ganhar', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_w', 'zh-CN', '赢', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_w', 'vi-VN', 'thắng', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_l', 'en-US', 'lose', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_l', 'zh-TW', '输', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_l', 'pt-BR', 'perder', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_l', 'zh-CN', ' 输', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_l', 'vi-VN', 'thua', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_c', 'en-US', 'rollback', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_c', 'zh-TW', '復原', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_c', 'pt-BR', 'reverter', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_c', 'zh-CN', ' 回滚', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_order_status_c', 'vi-VN', 'thua', 1, 1736152779752, '1', NULL);


-- FTG 游戏类型
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313156, 'ftg_game_type', 'slots', 'LOOKUP_ftg_game_type_slots', '老虎机', '游戏类型', 20240806223102, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313157, 'ftg_game_type', 'table', 'LOOKUP_ftg_game_type_table', '桌游', '游戏类型', 20240806223102, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313158, 'ftg_game_type', 'fishing', 'LOOKUP_ftg_game_type_fishing', '捕鱼', '游戏类型', 20240806223102, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (1221298313159, 'ftg_game_type', 'arcade', 'LOOKUP_ftg_game_type_arcade', '街机', '游戏类型', 20240806223102, NULL, '1', NULL);

-- FTG 游戏类型 多语言
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_slots', 'en-US', 'Slot Machines', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_slots', 'zh-TW', '老虎機', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_slots', 'pt-BR', 'Máquinas caça-níqueis', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_slots', 'zh-CN', '老虎机', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_slots', 'vi-VN', 'Máy đánh bạc', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_table', 'en-US', 'table', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_table', 'zh-TW', '桌遊', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_table', 'pt-BR', 'Jogos de tabuleiro', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_table', 'zh-CN', '桌游', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_table', 'vi-VN', 'Trò chơi trên bàn', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_fishing', 'en-US', 'table', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_fishing', 'zh-TW', '捕魚', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_fishing', 'pt-BR', 'Pesca', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_fishing', 'zh-CN', '捕鱼', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_fishing', 'vi-VN', 'Câu cá', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_arcade', 'en-US', 'Arcade', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_arcade', 'zh-TW', '街機', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_arcade', 'pt-BR', 'Arcada', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_arcade', 'zh-CN', '街机', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_ftg_game_type_arcade', 'vi-VN', 'Trò chơi điện tử', 1, 1736152779752, '1', NULL);

-- 游戏权限修改
UPDATE business_menu SET menu_key = 'common/Game/GameConfig/GameManagement/getGameCodeList' where id in (
    SELECT id FROM `business_menu` WHERE `menu_key` = 'common/Game/GameConfig/GameManagement/getShGameCodeList'
);

-- 王牌彩票
INSERT INTO `venue_info` (`id`, `venue_code`, `venue_currency_type`, `currency_code`, `venue_type`, `venue_platform`, `venue_name`, `venue_icon`, `pc_venue_icon`, `h5_venue_icon`, `icon_i18n_code`, `pc_icon_i18n_code`, `h5_icon_i18n_code`, `valid_proportion`, `venue_proportion`, `proportion_type`, `status`, `bet_url`, `api_url`, `game_url`, `merchant_no`, `aes_key`, `merchant_key`, `bet_key`, `creator_name`, `created_time`, `updater_name`, `updated_time`, `remark`, `maintenance_start_time`, `maintenance_end_time`, `name_prefix`, `creator`, `updater`)
VALUES (18210725432131314, 'WP_ACELT', 2, 'EUR,KVND,CNY,MYR,PHP,USDT,USD,VND,PKR', 5, 'WP_ACELT', '王牌彩票', 'baowang/fdb55997706c4612944d75128d0a8b11.PNG', 'baowang/64fbe12afb684c84a8f88a3aee1fe0c0.jpg', 'baowang/0d7aac6ebc24414992b9b713b5943857.jpg', NULL, '', '', 0.57, 0.00, 1, 1, 'https://ace.aceltapi.com', 'https://ace.aceltapi.com', NULL, 'dz13142', '2zybmDydhtKGdxINpeljOpOGAL/5KrG9f9+sIQWU90U=', '2zybmDydhtKGdxINpeljOpOGAL/5KrG9f9+sIQWU90U=', '2zybmDydhtKGdxINpeljOpOGAL/5KrG9f9+sIQWU90U=', '1', 1685790832973,
        'sheldon0987', 1741320100867, NULL, NULL, NULL, NULL, 'superAdmin02', 'fangyi666');

-- 王牌彩票
INSERT INTO system_param (`id`, type, code, value, value_desc, description, created_time, updated_time, creator, `updater`) VALUES (112312334232319, 'venue_code', 'WP_ACELT', 'LOOKUP_VENUE_INIT_NAME_WP_ACELT', '王牌彩票', '王牌彩票', 1, NULL, '1', NULL);

-- 王牌彩票 多语言名称
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_WP_ACELT', 'en-US', 'Ace Lottery', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_WP_ACELT', 'zh-TW', '王牌彩券', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_WP_ACELT', 'pt-BR', 'Loteria Ace', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_WP_ACELT', 'zh-CN', '王牌彩票', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_WP_ACELT', 'vi-VN', 'Xổ số Ace', 1, 1736152779752, '1', NULL);


-- 总控游戏 增加上一次状态
ALTER TABLE  game_info  add `last_status` int DEFAULT NULL COMMENT '最后一次状态 : (1 开启中 2 维护中 3 已禁用)' AFTER `status`;

-- 站点场馆增加状态
ALTER TABLE  site_venue  add `status` int DEFAULT '1' COMMENT '状态: (1 开启中 2 维护中 3 已禁用)' AFTER `site_code`;
ALTER TABLE  site_venue  add `last_status` int DEFAULT NULL COMMENT '总控修改的:最后一次状态 : (1 开启中 2 维护中 3 已禁用)' AFTER `site_code`;
ALTER TABLE  site_venue  add `site_last_status` int DEFAULT NULL COMMENT '站点修改的:最后一次状态 : (1 开启中 2 维护中 3 已禁用)' AFTER `last_status`;

-- 站点游戏增加状态
ALTER TABLE  site_game  add `status` int DEFAULT '1' COMMENT '状态: (1 开启中 2 维护中 3 已禁用)' AFTER `site_code`;
ALTER TABLE  site_game  add `last_status` int DEFAULT NULL COMMENT '总控修改的:最后一次状态 : (1 开启中 2 维护中 3 已禁用)' AFTER `site_code`;
ALTER TABLE  site_game  add `site_last_status` int DEFAULT NULL COMMENT '站点修改的:最后一次状态 : (1 开启中 2 维护中 3 已禁用)' AFTER `last_status`;


-- 站点游戏增加维护字段
ALTER TABLE site_game add  `maintenance_start_time` bigint DEFAULT NULL COMMENT '维护开始时间' AFTER   `venue_code`;
ALTER TABLE site_game add  `maintenance_end_time` bigint DEFAULT NULL COMMENT '维护结束时间' AFTER `maintenance_start_time`;
ALTER TABLE site_game add  `remark` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT '' COMMENT '备注' AFTER `maintenance_end_time`;


-- 站点场馆增加维护字段
ALTER TABLE site_venue add  `maintenance_start_time` bigint DEFAULT NULL COMMENT '维护开始时间' AFTER   `venue_code`;
ALTER TABLE site_venue add  `maintenance_end_time` bigint DEFAULT NULL COMMENT '维护结束时间' AFTER `maintenance_start_time`;
ALTER TABLE site_venue add  `remark` varchar(200) COLLATE utf8mb4_0900_bin DEFAULT '' COMMENT '备注' AFTER `maintenance_end_time`;




-- 增加IM弹场馆 (目前是测试环境的)

-- INSERT INTO venue_info (id,venue_code,venue_currency_type,currency_code,venue_type,venue_platform,venue_name,venue_icon,pc_venue_icon,h5_venue_icon,icon_i18n_code,pc_icon_i18n_code,h5_icon_i18n_code,valid_proportion,venue_proportion,proportion_type,status,bet_url,api_url,game_url,merchant_no,aes_key,merchant_key,bet_key,creator_name,created_time,updater_name,updated_time,remark,maintenance_start_time,maintenance_end_time,name_prefix,creator,updater) VALUES
-- (1826583527022666953,'MARBLES',2,'CNY',3,'MARBLES','IM弹珠','baowang/78e613ff606240d5b78bbb68d2e085ff.PNG','baowang/6b874962adf4447d8d65f9f591f92b81.png','baowang/4564f2150ff048c68e5b7246abc0b622.png',NULL,'','',0.00,10.00,0,1,'http://operatorapi-report.staging.imaegisapi.com','http://operatorapi-staging.imaegisapi.com/','','Oj4llPYgOYfTIwzylWTJ1ipJ9XTzHgdb','','',NULL,'sheldon0987',1723011991353,'xingyao1',1741701054089,NULL,NULL,NULL,NULL,'davis01','sheldon0987');


-- 增加IM弹珠游戏


-- INSERT INTO game_info (game_id,game_name,game_i18n_code,venue_id,venue_code,venue_name,venue_type,status,support_device,label,corner_labels,icon,icon_i18n_code,se_icon_i18n_code,vt_icon_i18n_code,ht_icon_i18n_code,game_desc,game_desc_i18n_code,remark,is_rebate,access_parameters,maintenance_start_time,maintenance_end_time,created_time,updated_time,creator,updater) VALUES
-- ('imlotto60001','秋名山- 娱乐版','BIZ_GAME_NAME_0069511',1826583527022666953,'MARBLES','IM弹珠',3,1,'0,1,2,3,4,5',0,0,'baowang/cb726fb4a2d1434a823332850b0b70ce.png','','','','',NULL,'','',0,'imlotto60001',NULL,NULL,1739952832858,1739953200754,'sheldon0987','sheldon0987'),
-- ('imlotto60002','大峡谷 - 娱乐版','BIZ_GAME_NAME_0069511',1826583527022666953,'MARBLES','IM弹珠',3,1,'0,1,2,3,4,5',0,0,'baowang/cb726fb4a2d1434a823332850b0b70ce.png','','','','',NULL,'','',0,'imlotto60002',NULL,NULL,1739952832858,1739953200754,'sheldon0987','sheldon0987'),
-- ('imlotto60003','夏威夷火山 - 娱乐版','BIZ_GAME_NAME_0069511',1826583527022666953,'MARBLES','IM弹珠',3,1,'0,1,2,3,4,5',0,0,'baowang/cb726fb4a2d1434a823332850b0b70ce.png','','','','',NULL,'','',0,'imlotto60003',NULL,NULL,1739952832858,1739953200754,'sheldon0987','sheldon0987'),
-- ('imlotto60004','亚马逊森林 - 娱乐版','BIZ_GAME_NAME_0069511',1826583527022666953,'MARBLES','IM弹珠',3,1,'0,1,2,3,4,5',0,0,'baowang/cb726fb4a2d1434a823332850b0b70ce.png','','','','',NULL,'','',0,'imlotto60004',NULL,NULL,1739952832858,1739953200754,'sheldon0987','sheldon0987'),
-- ('imlotto60005','侏罗纪公园 - 娱乐版','BIZ_GAME_NAME_0069511',1826583527022666953,'MARBLES','IM弹珠',3,1,'0,1,2,3,4,5',0,0,'baowang/cb726fb4a2d1434a823332850b0b70ce.png','','','','',NULL,'','',0,'imlotto60005',NULL,NULL,1739952832858,1739953200754,'sheldon0987','sheldon0987'),
-- ('imlotto60006','幸运农场 - 娱乐版','BIZ_GAME_NAME_0069511',1826583527022666953,'MARBLES','IM弹珠',3,1,'0,1,2,3,4,5',0,0,'baowang/cb726fb4a2d1434a823332850b0b70ce.png','','','','',NULL,'','',0,'imlotto60006',NULL,NULL,1739952832858,1739953200754,'sheldon0987','sheldon0987'),
-- ('imlotto60007','西伯利亚冰原 - 娱乐版','BIZ_GAME_NAME_0069511',1826583527022666953,'MARBLES','IM弹珠',3,1,'0,1,2,3,4,5',0,0,'baowang/cb726fb4a2d1434a823332850b0b70ce.png','','','','',NULL,'','',0,'imlotto60007',NULL,NULL,1739952832858,1739953200754,'sheldon0987','sheldon0987'),
-- ('imlotto60008','蒙古大草原 - 娱乐版','BIZ_GAME_NAME_0069511',1826583527022666953,'MARBLES','IM弹珠',3,1,'0,1,2,3,4,5',0,0,'baowang/cb726fb4a2d1434a823332850b0b70ce.png','','','','',NULL,'','',0,'imlotto60008',NULL,NULL,1739952832858,1739953200754,'sheldon0987','sheldon0987');


-- IM弹珠游戏

-- INSERT INTO system_param (`type`,code,value,value_desc,description,created_time,updated_time,creator,updater) VALUES
-- ('venue_code','MARBLES','LOOKUP_VENUE_INIT_NAME_IM_D','IM弹珠','IM弹珠',1,NULL,'1',NULL);
--
-- IM弹珠游戏多语言
-- INSERT INTO i18n_message (message_type,message_key,`language`,message,created_time,updated_time,creator,updater) VALUES
-- ('BACK_END','LOOKUP_VENUE_INIT_NAME_IM_D','vi-VN','IM Viên bi',1,1736152779752,'1',NULL),
-- ('BACK_END','LOOKUP_VENUE_INIT_NAME_IM_D','zh-CN','IM弹珠',1,1735274608799,'1',NULL),
-- ('BACK_END','LOOKUP_VENUE_INIT_NAME_IM_D','pt-BR','Mármores IM',1,1735274608799,'1',NULL),
-- ('BACK_END','LOOKUP_VENUE_INIT_NAME_IM_D','zh-TW','IM 彈珠',1,1735274608799,'1',NULL),
-- ('BACK_END','LOOKUP_VENUE_INIT_NAME_IM_D','en-US','IM Marbles',1,1735274608799,'1',NULL);


-- v8 游戏
INSERT INTO game_info (game_id,game_name,game_i18n_code,venue_id,venue_code,venue_name,venue_type,status,last_status,support_device,label,corner_labels,icon,icon_i18n_code,se_icon_i18n_code,vt_icon_i18n_code,ht_icon_i18n_code,game_desc,game_desc_i18n_code,remark,is_rebate,access_parameters,maintenance_start_time,maintenance_end_time,created_time,updated_time,creator,updater) VALUES
	 ('8310','番摊','BIZ_GAME_NAME_8310',1826583527022666948,'V8','V8棋牌',3,2,NULL,'0,1,2,3,4,5',0,0,'baowang/538dfb053ba742fd9dd4a72d883e694a.png','BIZ_GAME_ICON_8310','BIZ_SE_GAME_ICON_8310','BIZ_VT_GAME_ICON_8310','BIZ_HT_GAME_ICON_8310',NULL,'BIZ_GAME_DESC_8310','123456',0,'8310',1741237200000,1741323600000,1739952832858,1741261968469,'sheldon0987','davis01'),
	 ('8290','鱼虾蟹','BIZ_GAME_NAME_8290',1826583527022666948,'V8','V8棋牌',4,1,NULL,'0,1,2,3,4,5',0,0,'baowang/4fd4d369684f49249c6eb51b544e686e.png','BIZ_GAME_ICON_8290','BIZ_SE_GAME_ICON_8290','BIZ_VT_GAME_ICON_8290','BIZ_HT_GAME_ICON_8290',NULL,'BIZ_GAME_DESC_8290',NULL,0,'8290',NULL,NULL,1739952832858,1741263049547,'sheldon0987','davis01'),
	 ('8540','色碟','BIZ_GAME_NAME_8540',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'baowang/386e2275510941b5b3e1e9fe333831a5.jpg','BIZ_GAME_ICON_8540','BIZ_SE_GAME_ICON_8540','BIZ_VT_GAME_ICON_8540','BIZ_HT_GAME_ICON_8540',NULL,'BIZ_GAME_DESC_8540',NULL,0,'8540',NULL,NULL,1739952832858,1741263053468,'sheldon0987','davis01'),
	 ('8200','百人骰宝','BIZ_GAME_NAME_8200',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'baowang/07c61a90615645a38665d2039e253c46.png','BIZ_GAME_ICON_8200','BIZ_SE_GAME_ICON_8200','BIZ_VT_GAME_ICON_8200','BIZ_HT_GAME_ICON_8200',NULL,'BIZ_GAME_DESC_8200',NULL,0,'8200',NULL,NULL,1739952832858,1741271863655,'sheldon0987','davis01'),
	 ('3890','看四张抢庄牛牛','BIZ_GAME_NAME_3890',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'baowang/5bcafb147ea344dc9e58c0fe5d0e4021.jpg','BIZ_GAME_ICON_3890','BIZ_SE_GAME_ICON_3890','BIZ_VT_GAME_ICON_3890','BIZ_HT_GAME_ICON_3890',NULL,'BIZ_GAME_DESC_3890',NULL,0,'3890',NULL,NULL,1739952832858,1741271868248,'sheldon0987','davis01'),
	 ('910','百家乐','BIZ_GAME_NAME_910',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'baowang/abc1245729a24cf48eb0f5d482796162.jpg','BIZ_GAME_ICON_910','BIZ_SE_GAME_ICON_910','BIZ_VT_GAME_ICON_910','BIZ_HT_GAME_ICON_910',NULL,'BIZ_GAME_DESC_910',NULL,0,'910',NULL,NULL,1739952832858,1741616645777,'sheldon0987','aomiao01'),
	 ('860','三公','BIZ_GAME_NAME_860',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'baowang/726a83e328c54b45860bdbc0d34fb26d.png','BIZ_GAME_ICON_860','BIZ_SE_GAME_ICON_860','BIZ_VT_GAME_ICON_860','BIZ_HT_GAME_ICON_860',NULL,'BIZ_GAME_DESC_860',NULL,0,'860',NULL,NULL,1739952832858,1741616618710,'sheldon0987','aomiao01'),
	 ('8600','卡特','BIZ_GAME_NAME_8600',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'baowang/18d1b58adc6248b1bb9edc74237efa1f.png','BIZ_GAME_ICON_8600','BIZ_SE_GAME_ICON_8600','BIZ_VT_GAME_ICON_8600','BIZ_HT_GAME_ICON_8600',NULL,'BIZ_GAME_DESC_8600',NULL,0,'8600',NULL,NULL,1739952832858,1741271874210,'sheldon0987','davis01'),
	 ('220','炸金花1','BIZ_GAME_NAME_220',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_220','BIZ_SE_GAME_ICON_220','BIZ_VT_GAME_ICON_220','BIZ_HT_GAME_ICON_220',NULL,'BIZ_GAME_DESC_220',NULL,0,'220',NULL,NULL,1739952832858,1741401729556,'sheldon0987','davis01'),
	 ('830','抢庄牛牛','BIZ_GAME_NAME_830',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_830','BIZ_SE_GAME_ICON_830','BIZ_VT_GAME_ICON_830','BIZ_HT_GAME_ICON_830',NULL,'BIZ_GAME_DESC_830',NULL,0,'830',NULL,NULL,1739952832858,1741401754860,'sheldon0987','davis01');
INSERT INTO game_info (game_id,game_name,game_i18n_code,venue_id,venue_code,venue_name,venue_type,status,last_status,support_device,label,corner_labels,icon,icon_i18n_code,se_icon_i18n_code,vt_icon_i18n_code,ht_icon_i18n_code,game_desc,game_desc_i18n_code,remark,is_rebate,access_parameters,maintenance_start_time,maintenance_end_time,created_time,updated_time,creator,updater) VALUES
	 ('620','德州扑克','BIZ_GAME_NAME_620',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_620','BIZ_SE_GAME_ICON_620','BIZ_VT_GAME_ICON_620','BIZ_HT_GAME_ICON_620',NULL,'BIZ_GAME_DESC_620',NULL,0,'620',NULL,NULL,1739952832858,1741401761376,'sheldon0987','davis01'),
	 ('950','红黑大战','BIZ_GAME_NAME_950',1826583527022666948,'V8','V8棋牌',3,2,NULL,'0,1,2,3,4,5',0,0,'baowang/92ba1629e02e4498ac1e1acb78b9124a.png','BIZ_GAME_ICON_950','BIZ_SE_GAME_ICON_950','BIZ_VT_GAME_ICON_950','BIZ_HT_GAME_ICON_950',NULL,'BIZ_GAME_DESC_950','123456',0,'950',1741237200000,1741323600000,1739952832858,1741616695804,'sheldon0987','aomiao01'),
	 ('3101','疯狂点子牛','BIZ_GAME_NAME_3101',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_3101','BIZ_SE_GAME_ICON_3101','BIZ_VT_GAME_ICON_3101','BIZ_HT_GAME_ICON_3101',NULL,'BIZ_GAME_DESC_3101',NULL,0,'3101',NULL,NULL,1739952832858,1741401766973,'sheldon0987','davis01'),
	 ('680','新斗地主','BIZ_GAME_NAME_680',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_680','BIZ_SE_GAME_ICON_680','BIZ_VT_GAME_ICON_680','BIZ_HT_GAME_ICON_680',NULL,'BIZ_GAME_DESC_680',NULL,0,'680',NULL,NULL,1739952832858,1741401741416,'sheldon0987','davis01'),
	 ('8700','炸弹13','BIZ_GAME_NAME_8700',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_8700','BIZ_SE_GAME_ICON_8700','BIZ_VT_GAME_ICON_8700','BIZ_HT_GAME_ICON_8700',NULL,'BIZ_GAME_DESC_8700',NULL,0,'8700',NULL,NULL,1739952832858,1741401724434,'sheldon0987','davis01'),
	 ('630','十三水','BIZ_GAME_NAME_630',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_630','BIZ_SE_GAME_ICON_630','BIZ_VT_GAME_ICON_630','BIZ_HT_GAME_ICON_630',NULL,'BIZ_GAME_DESC_630',NULL,0,'630',NULL,NULL,1739952832858,1741401735509,'sheldon0987','davis01'),
	 ('8210','大小骰宝','BIZ_GAME_NAME_8210',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_8210','BIZ_SE_GAME_ICON_8210','BIZ_VT_GAME_ICON_8210','BIZ_HT_GAME_ICON_8210',NULL,'BIZ_GAME_DESC_8210',NULL,0,'8210',NULL,NULL,1739952832858,1741401747573,'sheldon0987','davis01'),
	 ('1101','塔拉牌','BIZ_GAME_NAME_1101',1826583527022666948,'V8','V8棋牌',3,2,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_1101','BIZ_SE_GAME_ICON_1101','BIZ_VT_GAME_ICON_1101','BIZ_HT_GAME_ICON_1101',NULL,'BIZ_GAME_DESC_1101','123456',0,'1101',1741237200000,1741323600000,1739952832858,1741261968534,'sheldon0987','davis01'),
	 ('1102','乌特','BIZ_GAME_NAME_1102',1826583527022666948,'V8','V8棋牌',3,2,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_1102','BIZ_SE_GAME_ICON_1102','BIZ_VT_GAME_ICON_1102','BIZ_HT_GAME_ICON_1102',NULL,'BIZ_GAME_DESC_1102','123456',0,'1102',1741237200000,1741323600000,1739952832858,1741261968538,'sheldon0987','davis01'),
	 ('1603','大小骰宝_迷你','BIZ_GAME_NAME_1603',1826583527022666948,'V8','V8棋牌',3,2,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_1603','BIZ_SE_GAME_ICON_1603','BIZ_VT_GAME_ICON_1603','BIZ_HT_GAME_ICON_1603',NULL,'BIZ_GAME_DESC_1603','123456',0,'1603',1741237200000,1741323600000,1739952832858,1741261968542,'sheldon0987','davis01');
INSERT INTO game_info (game_id,game_name,game_i18n_code,venue_id,venue_code,venue_name,venue_type,status,last_status,support_device,label,corner_labels,icon,icon_i18n_code,se_icon_i18n_code,vt_icon_i18n_code,ht_icon_i18n_code,game_desc,game_desc_i18n_code,remark,is_rebate,access_parameters,maintenance_start_time,maintenance_end_time,created_time,updated_time,creator,updater) VALUES
	 ('930','百人牛牛','BIZ_GAME_NAME_930',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_930','BIZ_SE_GAME_ICON_930','BIZ_VT_GAME_ICON_930','BIZ_HT_GAME_ICON_930',NULL,'BIZ_GAME_DESC_930',NULL,0,'930',NULL,NULL,1739952832858,1741401776307,'sheldon0987','davis01'),
	 ('519','拉密','BIZ_GAME_NAME_519',1826583527022666948,'V8','V8棋牌',3,2,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_519','BIZ_SE_GAME_ICON_519','BIZ_VT_GAME_ICON_519','BIZ_HT_GAME_ICON_519',NULL,'BIZ_GAME_DESC_519','123456',0,'519',1741237200000,1741323600000,1739952832858,1741261968549,'sheldon0987','davis01'),
	 ('1604','闪电宾果','BIZ_GAME_NAME_1604',1826583527022666948,'V8','V8棋牌',3,2,NULL,'0,1,2,3,4,5',0,0,'girth','BIZ_GAME_ICON_1604','BIZ_SE_GAME_ICON_1604','BIZ_VT_GAME_ICON_1604','BIZ_HT_GAME_ICON_1604',NULL,'BIZ_GAME_DESC_1604','123456',0,'1604',1741237200000,1741323600000,1739952832858,1741261968552,'sheldon0987','davis01'),
	 ('512','印度炸金花','BIZ_GAME_NAME_512',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'baowang/e502b713c936469d85777b8d716b82df.png','BIZ_GAME_ICON_512','BIZ_SE_GAME_ICON_512','BIZ_VT_GAME_ICON_512','BIZ_HT_GAME_ICON_512',NULL,'BIZ_GAME_DESC_512',NULL,0,'512',NULL,NULL,1739952832858,1741616763212,'sheldon0987','aomiao01'),
	 ('3005','欧式轮盘','BIZ_GAME_NAME_3005',1826583527022666948,'V8','V8棋牌',3,1,NULL,'0,1,2,3,4,5',0,0,'baowang/169a3c27e9ef422fb628b9df17fab742.png','BIZ_GAME_ICON_3005','BIZ_SE_GAME_ICON_3005','BIZ_VT_GAME_ICON_3005','BIZ_HT_GAME_ICON_3005',NULL,'BIZ_GAME_DESC_3005',NULL,0,'3005',NULL,NULL,1739952832858,1741401781528,'sheldon0987','davis01'),
	 ('1105','炸彈6','BIZ_GAME_NAME_1105',1826583527022666948,'V8','V8棋牌',3,2,NULL,'0,1,2,3,4,5',0,0,'baowang/a8af99f0ad3f4ac0b728d161915c8a3a.png','BIZ_GAME_ICON_1105','BIZ_SE_GAME_ICON_1105','BIZ_VT_GAME_ICON_1105','BIZ_HT_GAME_ICON_1105',NULL,'BIZ_GAME_DESC_1105','123456',0,'1105',1741237200000,1741323600000,1739952832858,1741261968564,'sheldon0987','davis01');


-- v8
INSERT INTO system_param (`type`,code,value,value_desc,description,created_time,updated_time,creator,updater) VALUES
	 ('venue_code','V8','LOOKUP_VENUE_INIT_NAME_V8','V8棋牌','V8棋牌',1,NULL,'1',NULL);

-- v8多语言
INSERT INTO i18n_message (message_type,message_key,`language`,message,created_time,updated_time,creator,updater) VALUES
	 ('BACK_END','LOOKUP_VENUE_INIT_NAME_V8','vi-VN','V8 Trang chủ',1,1736152779752,'1',NULL),
	 ('BACK_END','LOOKUP_VENUE_INIT_NAME_V8','zh-CN','V8 棋牌',1,1735274608799,'1',NULL),
	 ('BACK_END','LOOKUP_VENUE_INIT_NAME_V8','pt-BR','Mármores V8',1,1735274608799,'1',NULL),
	 ('BACK_END','LOOKUP_VENUE_INIT_NAME_V8','zh-TW','V8 棋牌',1,1735274608799,'1',NULL),
	 ('BACK_END','LOOKUP_VENUE_INIT_NAME_V8','en-US','V8 Chess and Card',1,1735274608799,'1',NULL);

-- V8场馆
    INSERT INTO venue_info
    (id, venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
    VALUES(1826583527022666948, 'V8', 1, 'MYR', 3, 'V8', 'V8棋牌', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/69bad00247664df5b070e274aec97370.jpg', 'baowang/29219e4b507b47a3aa5fb40f004a60f2.jpg', NULL, 'BIZ_PC_VENUE_ICON_50930', 'BIZ_H5_VENUE_ICON_50931', 0.00, 10.00, 0, 1, 'https://record.qsem295.com', 'https://api.qsem295.com', NULL, '82894', 'E06F1947857DC75E', '32C4525F36FE6A23', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741261997373, NULL, NULL, NULL, NULL, NULL, 'davis01');
    INSERT INTO venue_info
    (id, venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
    VALUES(1826583527022666949, 'V8', 1, 'USD', 3, 'V8', 'V8棋牌', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/69bad00247664df5b070e274aec97370.jpg', 'baowang/29219e4b507b47a3aa5fb40f004a60f2.jpg', NULL, 'BIZ_PC_VENUE_ICON_50934', 'BIZ_H5_VENUE_ICON_50935', 0.00, 10.00, 0, 1, 'https://record.qsem295.com', 'https://api.qsem295.com', '', '82893', '465B563C2B8D824B', 'D95BDCCCC835DECA', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741261997373, NULL, NULL, NULL, NULL, 'davis01', 'davis01');
    INSERT INTO venue_info
    (id, venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
    VALUES(1826583527022666950, 'V8', 1, 'PHP', 3, 'V8', 'V8棋牌', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/69bad00247664df5b070e274aec97370.jpg', 'baowang/29219e4b507b47a3aa5fb40f004a60f2.jpg', NULL, 'BIZ_PC_VENUE_ICON_50932', 'BIZ_H5_VENUE_ICON_50933', 0.00, 10.00, 0, 1, 'https://record.qsem295.com', 'https://api.qsem295.com', NULL, '82895', 'DB08A2FCB4BB5D26', '377FCECF2F892CEC', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741261997373, NULL, NULL, NULL, NULL, 'davis01', 'davis01');
    INSERT INTO venue_info
    (id, venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
    VALUES(1826583527022666951, 'V8', 1, 'CNY', 3, 'V8', 'V8棋牌', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/69bad00247664df5b070e274aec97370.jpg', 'baowang/29219e4b507b47a3aa5fb40f004a60f2.jpg', NULL, 'BIZ_PC_VENUE_ICON_50928', 'BIZ_H5_VENUE_ICON_50929', 0.00, 10.00, 0, 1, 'https://record.qsem295.com', 'https://api.qsem295.com', NULL, '82892', 'E94AAA3712ACEDAB', '7244FBAC894AADE3', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741261997373, NULL, NULL, NULL, NULL, 'davis01', 'davis01');
    INSERT INTO venue_info
    (id, venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
    VALUES(1826583527022666952, 'V8', 1, 'KVND', 3, 'V8', 'V8棋牌', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/69bad00247664df5b070e274aec97370.jpg', 'baowang/29219e4b507b47a3aa5fb40f004a60f2.jpg', NULL, 'BIZ_PC_VENUE_ICON_50936', 'BIZ_H5_VENUE_ICON_50937', 0.00, 10.00, 0, 1, 'https://record.qsem295.com', 'https://api.qsem295.com', NULL, '82891', 'FCA48BB3EDF3276A', 'E2EA13EBAD76CF61', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741261997373, NULL, NULL, NULL, NULL, NULL, 'davis01');
    INSERT INTO venue_info
    (id, venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
    VALUES(1826583527022666966, 'V8', 1, 'INR', 3, 'V8', 'V8棋牌', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/69bad00247664df5b070e274aec97370.jpg', 'baowang/29219e4b507b47a3aa5fb40f004a60f2.jpg', NULL, 'BIZ_PC_VENUE_ICON_50930', 'BIZ_H5_VENUE_ICON_50931', 0.00, 10.00, 0, 1, 'https://record.qsem295.com', 'https://api.qsem295.com', NULL, '82889', 'B5066E92E651F7FA', '73BE6B342F147892', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741261997373, '', NULL, NULL, NULL, NULL, NULL);
    INSERT INTO venue_info
    (id, venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
    VALUES(1826583527022697948, 'V8', 1, 'USDT', 3, 'V8', 'V8棋牌', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/69bad00247664df5b070e274aec97370.jpg', 'baowang/29219e4b507b47a3aa5fb40f004a60f2.jpg', NULL, 'BIZ_PC_VENUE_ICON_50934', 'BIZ_H5_VENUE_ICON_50935', 0.00, 10.00, 0, 1, 'https://record.qsem295.com', 'https://api.qsem295.com', '', '82893', '465B563C2B8D824B', 'D95BDCCCC835DECA', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741261997373, NULL, NULL, NULL, NULL, 'davis01', 'davis01');

UPDATE `i18n_message` SET message = '王牌彩票', `updated_time` = '1741342220338' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_WP_ACELT' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Ace Xổ số', `updated_time` = '1741342220338' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_WP_ACELT' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'V8 Poker', `updated_time` = '1741342220338' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_V8' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'V8棋牌', `updated_time` = '1741342220338' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_V8' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'V8 Cờ vua', `updated_time` = '1741342220338' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_V8' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'IM Marble', `updated_time` = '1741342220338' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_IM_D' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'IM彈珠', `updated_time` = '1741342220338' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_IM_D' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'IM Bắn bi', `updated_time` = '1741342220338' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_IM_D' AND language = 'vi-VN';
