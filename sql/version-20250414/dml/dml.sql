--添加巴基斯坦柬埔寨印度三个地区
INSERT INTO `area_admin_manage` (`id`, `area_id`, `area_code`, `country_name`, `country_code`, `max_length`, `min_length`, `status`, `icon`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('10008', 83222, '855', '柬埔寨', 'KH', 111, 1, 1, 'baowang/14a000af106c4e37b918034ad5e7373e.png', 1725420241236, 1743647928656, NULL, 'ethan001');
INSERT INTO `area_admin_manage` (`id`, `area_id`, `area_code`, `country_name`, `country_code`, `max_length`, `min_length`, `status`, `icon`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('10009', 83222, '92', '巴基斯坦', 'PK', 111, 1, 1, 'baowang/b091af15a74047c08a56287b426dd302.png', 1725420241236, 1744169456700, NULL, 'ethan001');
INSERT INTO `area_admin_manage` (`id`, `area_id`, `area_code`, `country_name`, `country_code`, `max_length`, `min_length`, `status`, `icon`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('10010', 83222, '91', '印度', 'IN', 111, 1, 1, 'baowang/14a000af106c4e37b918034ad5e7373e.png', 1725420241236, 1743647918306, NULL, 'ethan001');
--明姨短消息3个渠道
INSERT INTO `sms_channel_config` (`id`, `address`, `address_code`, `area_code`, `channel_id`, `channel_name`, `channel_code`, `platform_code`, `auth_count`, `status`, `host`, `user_id`, `user_account`, `password`, `template`, `remark`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('80000', '柬埔寨', 'KH', '855', '80001', '明姨', 'KHMYS', 'MYS', 0, 1, 'http://47.242.85.7:9090/sms/batch/v2', '1000', '8aRaqx', '9Yl41Y', 'Your verification code is %s', NULL, 1723708677193, 1733374770351, 'mufan01', 'mufan01');
INSERT INTO `sms_channel_config` (`id`, `address`, `address_code`, `area_code`, `channel_id`, `channel_name`, `channel_code`, `platform_code`, `auth_count`, `status`, `host`, `user_id`, `user_account`, `password`, `template`, `remark`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('81000', '巴基斯坦', 'PK', '92', '81001', '明姨', 'PKMYS', 'MYS', 0, 1, 'http://47.242.85.7:9090/sms/batch/v2', '1000', 'TBPpW7', 'N2AkqX', 'Your verification code is %s', NULL, 1723708677193, 1733374770351, 'mufan01', 'mufan01');
INSERT INTO `sms_channel_config` (`id`, `address`, `address_code`, `area_code`, `channel_id`, `channel_name`, `channel_code`, `platform_code`, `auth_count`, `status`, `host`, `user_id`, `user_account`, `password`, `template`, `remark`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('82000', '印度', 'IN', '91', '82001', '明姨', 'INMYS', 'MYS', 0, 1, 'http://47.242.85.7:9090/sms/batch/v2', '1000', 'xo89bz', 'jcW3RL', 'Your verification code is %s', NULL, 1723708677193, 1733374770351, 'mufan01', 'mufan01');

--同IP可注册的最大用户数
INSERT INTO `system_dict_config` (`id`, `dict_code`, `config_name`, `config_description`, `config_category`, `config_param`, `hint_info`, `creator`, `updater`, `created_time`, `updated_time`, `site_code`, `is_sync_site`, `decimal_places`, `type`) VALUES (29, 28, '同一IP可注册的最大用户数', '个数', 0, '10', '', 'mufan01', 'mufan01', 1733814879502, 1741181836310, '0', 1, 0, 1);
--站点同IP可注册的最大用户数
INSERT INTO `system_dict_config` (`id`, `dict_code`, `config_name`, `config_description`, `config_category`, `config_param`, `hint_info`, `creator`, `updater`, `created_time`, `updated_time`, `site_code`, `is_sync_site`, `decimal_places`, `type`) VALUES (1897560156403336842, 28, '同一IP可注册的最大用户数', '个数', 0, '10', 'LOOKUP_12713', 'mufan01', 'mufan01', 1733814879502, 1741181836310, 'dv8bm4', 0, 0, 1);


-- 体育详情修改
UPDATE  `system_param` SET `value` = ' 主队+n' WHERE `id` = 102938209;
UPDATE `system_param` SET `value` = ' 和局(x+n)' WHERE `id` = 102938210;
UPDATE `system_param` SET `value` = ' 客队-n' WHERE `id` = 102938211;

UPDATE `system_param` SET `value` = ' 主队+n' WHERE `id` = 102939671;
UPDATE `system_param` SET `value` = ' 客队-n' WHERE `id` = 102939673;

UPDATE `system_param` SET `value` = ' 主队+n' WHERE `id` = 102940715;
UPDATE `system_param` SET `value` = ' 和局(x+n)' WHERE `id` = 102940716;
UPDATE `system_param` SET `value` = ' 客队-n' WHERE `id` = 102940717;

UPDATE `system_param` SET `value` = ' 主队+n' WHERE `id` = 102940718;
UPDATE `system_param` SET `value` = ' 和局(x+n)' WHERE `id` = 102940719;
UPDATE `system_param` SET `value` = ' 客队-n' WHERE `id` = 102940720;