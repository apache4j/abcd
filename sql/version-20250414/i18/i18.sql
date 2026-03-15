DELETE FROM `i18n_message` WHERE `message_type` = 'BACK_END' AND message_key IN ('LOOKUP_AGENT_LEVEL_1', 'LOOKUP_AGENT_LEVEL_2', 'LOOKUP_AGENT_LEVEL_3', 'LOOKUP_AGENT_LEVEL_4');

INSERT INTO `system_param` (`type`, `code`, `value`, `value_desc`, `description`, `created_time`,`creator`)VALUES('agent_level', '1', 'LOOKUP_AGENT_LEVEL_1', '一级代理', '一级代理', UNIX_TIMESTAMP()*1000, 'sql' );
INSERT INTO `system_param` (`type`, `code`, `value`, `value_desc`, `description`, `created_time`,`creator`)VALUES('agent_level', '2', 'LOOKUP_AGENT_LEVEL_2', '二级代理', '二级代理', UNIX_TIMESTAMP()*1000, 'sql' );
INSERT INTO `system_param` (`type`, `code`, `value`, `value_desc`, `description`, `created_time`,`creator`)VALUES('agent_level', '3', 'LOOKUP_AGENT_LEVEL_3', '三级代理', '三级代理', UNIX_TIMESTAMP()*1000, 'sql' );
INSERT INTO `system_param` (`type`, `code`, `value`, `value_desc`, `description`, `created_time`,`creator`)VALUES('agent_level', '4', 'LOOKUP_AGENT_LEVEL_4', '四级代理', '四级代理', UNIX_TIMESTAMP()*1000, 'sql' );

INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_1', 'zh-CN', '一级代理',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_2', 'zh-CN', '二级代理',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_3', 'zh-CN', '三级代理',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_4', 'zh-CN', '四级代理',UNIX_TIMESTAMP()*1000);

INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_1', 'vi-VN', 'đại lý cấp 1',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_2', 'vi-VN', 'đại lý cấp 2',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_3', 'vi-VN', 'đại lý cấp 3',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_4', 'vi-VN', 'đại lý cấp 4',UNIX_TIMESTAMP()*1000);

INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_1', 'pt-BR', 'agente nível 1',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_2', 'pt-BR', 'agente nível 2',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_3', 'pt-BR', 'agente nível 3',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_4', 'pt-BR', 'agente nível 4',UNIX_TIMESTAMP()*1000);

INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_1', 'zh-TW', '一級代理',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_2', 'zh-TW', '二級代理',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_3', 'zh-TW', '三級代理',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_4', 'zh-TW', '四級代理',UNIX_TIMESTAMP()*1000);

INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_1', 'en-US', 'agent level 1',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_2', 'en-US', 'agent level 2',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_3', 'en-US', 'agent level 3',UNIX_TIMESTAMP()*1000);
INSERT INTO `i18n_message` (`message_type`, message_key, language, message, created_time) VALUES ( 'BACK_END', 'LOOKUP_AGENT_LEVEL_4', 'en-US', 'agent level 4',UNIX_TIMESTAMP()*1000);



UPDATE `i18n_message` SET message = 'Cá/Tôm/Cua', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_ES_GAME_TYPE_43' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Cá/Tôm/Cua', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_ACELT_PLAY_TYPE_smp_dxtb_yxx' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Tier 4 Agent', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_AGENT_LEVEL_4' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Đại lý cấp 4', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_AGENT_LEVEL_4' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Tier 3 Agent', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_AGENT_LEVEL_3' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Đại lý cấp 3', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_AGENT_LEVEL_3' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Tier 2 Agent', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_AGENT_LEVEL_2' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Đại lý cấp 2', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_AGENT_LEVEL_2' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Tier 1 Agent', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_AGENT_LEVEL_1' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Đại lý cấp 1', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_AGENT_LEVEL_1' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Weekly Invite friend', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_11811' AND language = 'en-US';
UPDATE `i18n_message` SET message = '每周邀請好友', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_11811' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Daily Deposit', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_11810' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Gửi tiền hàng ngày', `updated_time` = '1743767738430' WHERE message_key = 'LOOKUP_11810' AND language = 'vi-VN';

--国际化
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (112312334262324, 'dict_hintInfo', '3', 'LOOKUP_12713', '请联系客服', '字典参数错误提示内容', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('BACK_END', 'LOOKUP_12713', 'zh-CN', '请联系客服', 1743681322000, NULL, NULL, NULL);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('BACK_END', 'LOOKUP_12713', 'zh-TW', '請聯繫客服', 1743681322000, NULL, NULL, NULL);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('BACK_END', 'LOOKUP_12713', 'en-US', 'Please contact customer service', 1743681322000, NULL, NULL, NULL);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('BACK_END', 'LOOKUP_12713', 'pt-BR', 'Entre em contato com o atendimento ao cliente', 1743681322000, NULL, NULL, NULL);
INSERT INTO `i18n_message` (`message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('BACK_END', 'LOOKUP_12713', 'vi-VN', 'Vui lòng liên hệ CSKH', 1743681322000, NULL, NULL, NULL);

-- 提现账号信息  银行卡号 改成 银行账号
UPDATE system_param SET `value_desc` = '银行账号' WHERE `type` = 'withdraw_collect' AND  `code` = 'bankCard' AND `value` = 'LOOKUP_11322';

UPDATE i18n_message SET `message` = 'bank account number'  WHERE `message_type` = 'BACK_END' and `message_key` = 'LOOKUP_11322' AND `language` = 'en-US';
UPDATE i18n_message SET `message` = '銀行帳號'  WHERE `message_type` = 'BACK_END' and `message_key` = 'LOOKUP_11322' AND `language` = 'zh-TW';
UPDATE i18n_message SET `message` = 'número da conta bancária'  WHERE `message_type` = 'BACK_END' and `message_key` = 'LOOKUP_11322' AND `language` = 'pt-BR';
UPDATE i18n_message SET `message` = '银行账号'  WHERE `message_type` = 'BACK_END' and `message_key` = 'LOOKUP_11322' AND `language` = 'zh-CN';
UPDATE i18n_message SET `message` = 'số tài khoản ngân hàng'  WHERE `message_type` = 'BACK_END' and `message_key` = 'LOOKUP_11322' AND `language` = 'vi-VN';