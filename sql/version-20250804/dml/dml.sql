UPDATE i18n_message SET message = '单币种场馆' WHERE `language` = 'zh-CN' and message_key = 'LOOKUP_11030';

UPDATE i18n_message SET message = '多币种场馆' WHERE `language` = 'zh-CN' and message_key = 'LOOKUP_11031';

UPDATE user_deposit_withdrawal set pay_audit_time = updated_time WHERE `status` = '101' AND pay_audit_time IS NULL;

UPDATE agent_deposit_withdrawal set pay_audit_time = updated_time WHERE `status` = '101' AND pay_audit_time IS NULL;



INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'pay_channel_name', 'SQPay', 'LOOKUP_11278', '十全支付', '支付渠道', 1, NULL, '1', NULL);


INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11278', 'en-US', '十全支付', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11278', 'zh-TW', '十全支付', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11278', 'pt-BR', '十全支付', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11278', 'zh-CN', '十全支付', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11278', 'vi-VN', '十全支付', 1, NULL, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_11278', 'ko-KR', '十全支付', 1, NULL, '1', NULL);
