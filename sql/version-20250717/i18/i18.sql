
-- DELETE from i18n_message where message_key='LOOKUP_10882';

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10882', 'en-US', 'Site Customization', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10882', 'zh-TW', '站点自定义', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10882', 'pt-BR', 'Personalização do site', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10882', 'zh-CN', '站点自定义', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10882', 'vi-VN', 'Tùy chỉnh trang web', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10882', 'ko-KR', '사이트 사용자 정의', 1745826480664, 1745826480664, '1', '1');


INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'CHANNEL_TYPE', 'SITE_CUSTOM', 'LOOKUP_10882', '站点自定义', '渠道类型', 1, NULL, '1', NULL);


INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'security_source_coin_type', '8', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_8', '会员人工提款', '保证金业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'security_source_coin_type', '9', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_9', '代理人工提款', '保证金业务类型', 1, NULL, '1', NULL);



INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_8', 'zh-CN', '会员人工提款', 1, 1751022817545, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_8', 'en-US', 'Member Manual WithDraw', 1, 1751022817545, '1', NULL);



INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_9', 'zh-CN', '代理人工提款', 1, 1751022817545, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_9', 'en-US', 'Agent Manual WithDraw', 1, 1751022817545, '1', NULL);
UPDATE `i18n_message` SET message = 'Trang web (Hậu đài)', `updated_time` = '1753089313507' WHERE message_key = 'LOOKUP_10940' AND language = 'vi-VN';

UPDATE `i18n_message` SET message = 'Trang web (Hậu đài)', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_10940' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'FASTSPIN 슬롯', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_FASTSPIN' AND language = 'ko-KR';
UPDATE `i18n_message` SET message = 'FASTSPIN Điện tử', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_VENUE_INIT_NAME_FASTSPIN' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Agent Manual Withdrawal', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_9' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_9', 'zh-TW', '代理人工提款', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_9', 'ko-KR', '에이전트 수동 출금', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_9', 'vi-VN', 'Đại lý rút tiền thủ công', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Member Manual Withdrawal', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_8' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_8', 'zh-TW', '會員人工提款', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_8', 'ko-KR', '회원 수동 출금', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_8', 'vi-VN', 'Thành viên rút tiền thủ công', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Site Customize', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_10882' AND language = 'en-US';
UPDATE `i18n_message` SET message = '站點自定義', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_10882' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Tuỳ chỉnh trang web', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_10882' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Decrease overdraft limlt', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_7' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_7', 'zh-TW', '減少透支額度', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_7', 'ko-KR', '오버드래프트 한도 감소', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_7', 'vi-VN', 'Giảm hạn mức thấu chi', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Increase overdraft limlt', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_6' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_6', 'zh-TW', '增加透支額度', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_6', 'ko-KR', '오버드래프트 한도 증가', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_6', 'vi-VN', 'Tăng hạn mức thấu chi', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Available overdraft limit', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_AVAILABLE' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_AVAILABLE', 'zh-TW', '剩餘透支額度', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_AVAILABLE', 'ko-KR', '오버드래프트 한도 잔여', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_AVAILABLE', 'vi-VN', 'Hạn mức thấu chi còn lại', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Suspended overdraft limit', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_FROZEN' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_FROZEN', 'zh-TW', '凍結透支額度', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_FROZEN', 'ko-KR', '오버드래프트 한도 동결', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_FROZEN', 'vi-VN', 'Hạn mức thấu chi bị đóng băng', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Suspended margin', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_FROZEN' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_FROZEN', 'zh-TW', '凍結保證金', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_FROZEN', 'ko-KR', '증거금 동결', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_FROZEN', 'vi-VN', 'Tiền ký quỹ bị đóng băng', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW', 'zh-TW', '透支額度', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW', 'ko-KR', '오버드래프트 한도', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW', 'vi-VN', 'Hạn mức thấu chi', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Margin', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_AVAILABLE' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_AVAILABLE', 'zh-TW', '保證金', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_AVAILABLE', 'ko-KR', '증거금', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_AVAILABLE', 'vi-VN', 'Tiền ký quỹ', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Deduction from the overdraft limit', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SECURITY_COIN_TYPE_14' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_14', 'zh-TW', '透支額度抵扣', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_14', 'ko-KR', '오버드래프트 한도에서 차감', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_14', 'vi-VN', 'Khấu trừ hạn mức thấu chi', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Overdraft limit reduction failed', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SECURITY_COIN_TYPE_13' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_13', 'zh-TW', '減少透支額度失敗', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_13', 'ko-KR', '오버드래프트 한도 감소 실패', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_13', 'vi-VN', 'Giảm hạn mức thấu chi thất bại', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Overdraft limit reduction successful', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SECURITY_COIN_TYPE_12' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_12', 'zh-TW', '減少透支額度成功', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_12', 'ko-KR', '오버드래프트 한도 감소 성공', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_12', 'vi-VN', 'Giảm hạn mức thấu chi thành công', 1, 1753166090856, 1, 1753166090856);
UPDATE `i18n_message` SET message = 'Decrease overdraft limlt', `updated_time` = '1753166090856' WHERE message_key = 'LOOKUP_SECURITY_COIN_TYPE_11' AND language = 'en-US';
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_11', 'zh-TW', '減少透支額度', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_11', 'ko-KR', '오버드래프트 한도 감소', 1, 1753166090856, 1, 1753166090856);
INSERT INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) VALUES ('BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_11', 'vi-VN', 'Giảm hạn mức thấu chi', 1, 1753166090856, 1, 1753166090856);

INSERT INTO system_param (type, code, value, value_desc, description,created_time) VALUES ('venue_code', 'NEXTSPIN', 'LOOKUP_VENUE_INIT_NAME_NEXTSPIN', 'NEXTSPIN电子', 'NEXTSPIN电子',UNIX_TIMESTAMP()*1000);
DELETE  FROM i18n_message where message_key = 'LOOKUP_VENUE_INIT_NAME_NEXTSPIN';
INSERT INTO i18n_message (message_type, message_key, language, message,created_time) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_NEXTSPIN', 'vi-VN', 'NEXTSPIN điện tử',UNIX_TIMESTAMP()*1000);
INSERT INTO i18n_message (message_type, message_key, language, message,created_time) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_NEXTSPIN', 'zh-CN', 'NEXTSPIN电子',UNIX_TIMESTAMP()*1000);
INSERT INTO i18n_message (message_type, message_key, language, message,created_time) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_NEXTSPIN', 'pt-BR', 'Eletrônica NEXTSPIN',UNIX_TIMESTAMP()*1000);
INSERT INTO i18n_message (message_type, message_key, language, message,created_time) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_NEXTSPIN', 'zh-TW', 'NEXTSPIN電子',UNIX_TIMESTAMP()*1000);
INSERT INTO i18n_message (message_type, message_key, language, message,created_time) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_NEXTSPIN', 'en-US', 'NEXTSPIN Slots',UNIX_TIMESTAMP()*1000);
INSERT INTO i18n_message (message_type, message_key, language, message,created_time) VALUES ( 'BACK_END', 'LOOKUP_VENUE_INIT_NAME_NEXTSPIN', 'ko-KR', 'NEXTSPIN 슬롯',UNIX_TIMESTAMP()*1000);