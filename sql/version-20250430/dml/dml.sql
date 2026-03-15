-- 删除所有的任务配置
delete from site_task_config_next;
UPDATE site_task_config SET wash_ratio = NULL,venue_type = null,venue_code=null,status=0  WHERE sub_task_type IN (
                                                                                                         'betDaily',
                                                                                                         'profitDaily',
                                                                                                         'negativeDaily',
                                                                                                         'betWeek',
                                                                                                         'profitWeek',
                                                                                                         'negativeWeek'
    );



-- 参数字典福利中心返水福利发放后用户领取过期时间设定和返水脚本执行时间
INSERT INTO `system_dict_config` (`id`, `dict_code`, `config_name`, `config_description`, `config_category`, `config_param`, `hint_info`, `creator`, `updater`, `created_time`, `updated_time`, `site_code`, `is_sync_site`, `decimal_places`, `type`) VALUES (30, 29, '福利中心返水福利发放后用户领取过期时间设定', '小时', 0, '10', '', 'mufan01', 'mufan01', 1733814879502, 1741181836310, '0', 0, 0, 1);
INSERT INTO `system_dict_config` (`id`, `dict_code`, `config_name`, `config_description`, `config_category`, `config_param`, `hint_info`, `creator`, `updater`, `created_time`, `updated_time`, `site_code`, `is_sync_site`, `decimal_places`, `type`) VALUES (31, 30, '返水脚本执行时间', '时间', 0, '10', '', 'mufan01', 'mufan01', 1733814879502, 1741181836310, '0', 0, 0, 1);

-- 会员账变记录，平台币账变记录，人工调整类型 流水类型
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (112312334262337, 'business_coin_type', '10', 'LOOKUP_BUSINESS_COIN_TYPE_10', '返水', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (112312334262339, 'coin_type', '23', 'LOOKUP_COIN_TYPE_23', '返水增加金额', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (112312334262340, 'coin_type', '24', 'LOOKUP_COIN_TYPE_24', '返水扣除金额', '账变记录-业务类型', 1, NULL, '1', NULL);

INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (112312334262341, 'paltform_business_coin_type', '5', 'LOOKUP_PALTFORM_BUSINESS_COIN_TYPE_5', '返水', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (112312334262342, 'paltform_coin_type', '5', 'LOOKUP_PALTFORM_COIN_TYPE_5', '返水', '账变记录-业务类型', 1, NULL, '1', NULL);

INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (112312334262343, 'manual_adjust_type', '10', 'LOOKUP_11204', '会员返水', '会员资金调整类型加款-会员返水', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (112312334262344, 'manual_adjust_down_type', '10', 'LOOKUP_11274', '会员返水', '会员资金调整类型减款-会员返水', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (112312334262345, 'typing_adjust_type', '9', 'LOOKUP_TYPING_ADJUST_TYPE_9', '会员返水', '打码量-调整类型', 1, NULL, '1', NULL);

-- 参数字典所有和时间单位相关的都只能输入整数更改
update system_dict_config set decimal_places =0 where dict_code in (1,2,3,4,5,6,11,12,13,19,23,24,25,29,30);


-- 更改参数字典
update system_dict_config set config_name='用户trc和erc归集阈值' where dict_code=0;


-- uat已执行不需要操作im弹珠增加币种
INSERT INTO venue_info
(venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_platform_name, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
VALUES( 'MARBLES', 2, 'PKR', 3, 'MARBLES', 'IM弹珠', 'IM弹珠', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/6b874962adf4447d8d65f9f591f92b81.png', 'baowang/4564f2150ff048c68e5b7246abc0b622.png', NULL, '', '', 0.00, 10.00, 0, 1, 'http://34.92.223.188:8080', 'http://34.92.223.188:8081', '', 'OPqMLj7XjBj39FHCiLDdihpcYXG9ZrMI', '', '', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741701054089, NULL, NULL, NULL, NULL, 'davis01', 'sheldon0987');
INSERT INTO venue_info
(venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_platform_name, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
VALUES( 'MARBLES', 2, 'USDT', 3, 'MARBLES', 'IM弹珠', 'IM弹珠', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/6b874962adf4447d8d65f9f591f92b81.png', 'baowang/4564f2150ff048c68e5b7246abc0b622.png', NULL, '', '', 0.00, 10.00, 0, 1, 'http://34.92.223.188:8080', 'http://34.92.223.188:8081', '', 'OPqMLj7XjBj39FHCiLDdihpcYXG9ZrMI', '', '', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741701054089, NULL, NULL, NULL, NULL, 'davis01', 'sheldon0987');
INSERT INTO venue_info
(venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_platform_name, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
VALUES( 'MARBLES', 2, 'USD', 3, 'MARBLES', 'IM弹珠', 'IM弹珠', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/6b874962adf4447d8d65f9f591f92b81.png', 'baowang/4564f2150ff048c68e5b7246abc0b622.png', NULL, '', '', 0.00, 10.00, 0, 1, 'http://34.92.223.188:8080', 'http://34.92.223.188:8081', '', 'OPqMLj7XjBj39FHCiLDdihpcYXG9ZrMI', '', '', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741701054089, NULL, NULL, NULL, NULL, 'davis01', 'sheldon0987');
INSERT INTO venue_info
(venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_platform_name, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
VALUES( 'MARBLES', 2, 'PHP', 3, 'MARBLES', 'IM弹珠', 'IM弹珠', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/6b874962adf4447d8d65f9f591f92b81.png', 'baowang/4564f2150ff048c68e5b7246abc0b622.png', NULL, '', '', 0.00, 10.00, 0, 1, 'http://34.92.223.188:8080', 'http://34.92.223.188:8081', '', 'OPqMLj7XjBj39FHCiLDdihpcYXG9ZrMI', '', '', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741701054089, NULL, NULL, NULL, NULL, 'davis01', 'sheldon0987');
INSERT INTO venue_info
(venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_platform_name, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
VALUES( 'MARBLES', 2, 'VND', 3, 'MARBLES', 'IM弹珠', 'IM弹珠', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/6b874962adf4447d8d65f9f591f92b81.png', 'baowang/4564f2150ff048c68e5b7246abc0b622.png', NULL, '', '', 0.00, 10.00, 0, 1, 'http://34.92.223.188:8080', 'http://34.92.223.188:8081', '', 'OPqMLj7XjBj39FHCiLDdihpcYXG9ZrMI', '', '', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741701054089, NULL, NULL, NULL, NULL, 'davis01', 'sheldon0987');
INSERT INTO venue_info
(venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_platform_name, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
VALUES( 'MARBLES', 2, 'INR', 3, 'MARBLES', 'IM弹珠', 'IM弹珠', 'baowang/78e613ff606240d5b78bbb68d2e085ff.PNG', 'baowang/6b874962adf4447d8d65f9f591f92b81.png', 'baowang/4564f2150ff048c68e5b7246abc0b622.png', NULL, '', '', 0.00, 10.00, 0, 1, 'http://34.92.223.188:8080', 'http://34.92.223.188:8081', '', 'OPqMLj7XjBj39FHCiLDdihpcYXG9ZrMI', '', '', NULL, 'sheldon0987', 1723011991353, 'xingyao1', 1741701054089, NULL, NULL, NULL, NULL, 'davis01', 'sheldon0987');
-- uat已执行不需要操作im电子正式增加币种
INSERT INTO venue_info
( venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_platform_name, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
VALUES( 'IM', 1, 'INR', 4, 'IM', 'IMPERIUM', 'IMPERIUM电子', '', 'baowang/a2f5d1be838340c2ba798d2a8d0e538f.png', 'baowang/3e1545ca663a4f6794bf0b422a8136ee.png', 'BIZ_VENUE_ICON_8778', 'BIZ_PC_VENUE_ICON_33152', 'BIZ_H5_VENUE_ICON_33153', 0.00, 12.00, 0, 1, NULL, 'https://asiaapi.net', NULL, '941138', '0', 'uB1Gt2tku7y1btVt3sDF', NULL, 'superAdmin01', 1723015995501, 'davis01', 1743410527329, NULL, NULL, NULL, NULL, 'superAdmin02', 'yycs08');
INSERT INTO venue_info
( venue_code, venue_currency_type, currency_code, venue_type, venue_platform, venue_platform_name, venue_name, venue_icon, pc_venue_icon, h5_venue_icon, icon_i18n_code, pc_icon_i18n_code, h5_icon_i18n_code, valid_proportion, venue_proportion, proportion_type, status, bet_url, api_url, game_url, merchant_no, aes_key, merchant_key, bet_key, creator_name, created_time, updater_name, updated_time, remark, maintenance_start_time, maintenance_end_time, name_prefix, creator, updater)
VALUES( 'IM', 1, 'PKR', 4, 'IM', 'IMPERIUM', 'IMPERIUM电子', '', 'baowang/a2f5d1be838340c2ba798d2a8d0e538f.png', 'baowang/3e1545ca663a4f6794bf0b422a8136ee.png', 'BIZ_VENUE_ICON_8778', 'BIZ_PC_VENUE_ICON_33152', 'BIZ_H5_VENUE_ICON_33153', 0.00, 12.00, 0, 1, NULL, 'https://asiaapi.net', NULL, '941136', '0', '2WB2YYtFXQ2WonKb4W3i', NULL, 'superAdmin01', 1723015995501, 'davis01', 1743410527329, NULL, NULL, NULL, NULL, 'superAdmin02', 'yycs08');
-- 更改im弹珠游戏类型变更为体育
update venue_info set venue_type=9   where venue_code='MARBLES'