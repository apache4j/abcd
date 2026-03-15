



INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (112312334292361, 'agent_change_type', '11', 'LOOKUP_1275', '推广信息', '代理信息变更类型', 1, NULL, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_1275', 'ko-KR', '프로모션 정보', 1745826480664, 1745826480664, '1', '1');
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_1275', 'en-US', 'Promotional information', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_1275', 'vi-VN', 'Thông tin khuyến mại', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_1275', 'zh-TW', '推廣訊息', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_1275', 'pt-BR', 'Informações promocionais', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_1275', 'zh-CN', '推广信息', 1, 1735274608799, '1', NULL);



-- PP电子, 初始化sql

DELETE  FROM `system_param` where `value` = 'LOOKUP_VENUE_INIT_NAME_PP';

SET @paramLatestId = (SELECT MAX(id) FROM `system_param`)  + 1;

INSERT INTO `system_param` (`id`,`type`, `code`, `value`, `value_desc`, `description`,`created_time`) VALUES (@paramLatestId, 'venue_code', 'PP', 'LOOKUP_VENUE_INIT_NAME_PP', 'PP电子', 'PP电子',UNIX_TIMESTAMP()*1000);

DELETE  FROM `i18n_message` where `message_key` = 'LOOKUP_VENUE_INIT_NAME_PP';

INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`,`created_time`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_PP', 'vi-VN', 'PP điện tử',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`,`created_time`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_PP', 'zh-CN', 'PP电子',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`,`created_time`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_PP', 'pt-BR', 'Eletrônica PP',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`,`created_time`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_PP', 'zh-TW', 'PP電子',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`,`created_time`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_PP', 'en-US', 'PP Slots',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`,`created_time`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_PP', 'ko-KR', 'PP 슬롯',UNIX_TIMESTAMP()*1000);

DELETE  FROM  venue_info where venue_code = 'PP';

SET @venueLatestId = (SELECT MAX(id) FROM `venue_info`)  + 1;
INSERT INTO `venue_info`( `id`,`venue_code`, `venue_currency_type`, `currency_code`, `venue_type`, `venue_platform`, `venue_name`, `venue_icon`, `pc_venue_icon`, `h5_venue_icon`, `icon_i18n_code`, `pc_icon_i18n_code`, `h5_icon_i18n_code`, `valid_proportion`, `venue_proportion`, `proportion_type`, `status`, `bet_url`, `api_url`, `game_url`, `merchant_no`, `aes_key`, `merchant_key`, `bet_key`) VALUES (@venueLatestId, 'PP', 2, 'USDT,PHP,MYR,CNY,USD,KVND,PKR,INR', 4, 'PP', 'PP电子', 'baowang/banner-e17ac727.png', 'baowang/7d52a6426fb9473a8916578c987ee8a9.jpg', 'baowang/b48dfb8be237407fa2c45df6d04bc9f8.jpg', 'BIZ_VENUE_ICON_5764', 'BIZ_PC_VENUE_ICON_27834', 'BIZ_H5_VENUE_ICON_27835', NULL, 11.00, 0, 1, 'https://api.prerelease-env.biz', 'https://api.prerelease-env.biz', 'https://api.prerelease-env.biz', 'wnt_winto01', '0', '739721332dAd4b97', NULL);

-- EbPay 支付通道

INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'pay_channel_name', 'EbPay', 'LOOKUP_11269', 'EbPay', '支付渠道', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11269', 'en-US', 'EbPay', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11269', 'zh-TW', 'EbPay', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11269', 'pt-BR', 'EbPay', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11269', 'zh-CN', 'EbPay', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11269', 'vi-VN', 'EbPay', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11269', 'ko-KR', 'EbPay', 1, NULL, '1', NULL);


delete from game_one_currency_sort;

INSERT INTO game_one_currency_sort (
    site_code, game_one_id, currency_code, directory_sort, home_sort, created_time,creator
)
SELECT
    g.site_code,
    g.id AS game_one_id,
    c.currency_code,
    100 AS directory_sort,
    100 AS home_sort,
    NOW(),
    'init'
FROM
    game_one_class_info g
        CROSS JOIN
    system_currency_info c
WHERE NOT EXISTS (
    SELECT 1
    FROM game_one_currency_sort s
    WHERE
        s.site_code = g.site_code
      AND s.game_one_id = g.id
      AND s.currency_code = c.currency_code
);


delete from game_two_currency_sort;
INSERT INTO game_two_currency_sort (
    site_code, game_join_id, game_two_id, game_one_id, game_id,
    currency_code, sort, game_one_home_sort, game_one_hot_sort, creator
)
SELECT
    c.site_code,
    c.id AS game_join_id,
    c.game_two_id,
    c.game_one_id,
    c.game_id,
    s.currency_code,
    c.sort,
    c.game_one_home_sort,
    c.game_one_hot_sort,
    'init'
FROM game_join_class c
         CROSS JOIN system_currency_info s
WHERE NOT EXISTS (
    SELECT 1
    FROM game_two_currency_sort t
    WHERE t.game_join_id = c.id
      AND t.currency_code = s.currency_code
);