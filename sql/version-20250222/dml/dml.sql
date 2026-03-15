
-- 场馆费率 类型下拉框多语言

INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (12229898133199, 'venue_proportion_type', '0', 'LOOKUP_venue_proportion_type_0', '场馆负盈利费率', '场馆负盈利费率', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (12298983133198, 'venue_proportion_type', '1', 'LOOKUP_venue_proportion_type_1', '场馆有效流水费率', '场馆有效流水费率', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (12298983133197, 'venue_proportion_type', '2', 'LOOKUP_venue_proportion_type_2', '负盈利&有效流水费率', '负盈利&有效流水费率', 1, NULL, '1', NULL);



INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_1', 'en-US', 'Venue effective turnover rate', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_1', 'zh-TW', '場館有效流水費率', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_1', 'pt-BR', 'Taxa de rotatividade efetiva do local', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_1', 'zh-CN', '场馆有效流水费率', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_1', 'vi-VN', 'Tỷ lệ luân chuyển hiệu quả của địa điểm', 1, 1, NULL, NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_2', 'en-US', 'Negative profit', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_2', 'zh-TW', '負獲利', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_2', 'pt-BR', 'Lucro negativo', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_2', 'zh-CN', '负盈利&有效流水费率', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_2', 'vi-VN', 'Lợi nhuận âm', 1, 1, NULL, NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_0', 'en-US', 'Negative profit rate of venues', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_0', 'zh-TW', '場館負獲利費率', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_0', 'pt-BR', 'Taxa de lucro negativa dos locais', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_0', 'zh-CN', '场馆负盈利费率', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_venue_proportion_type_0', 'vi-VN', 'Tỷ lệ lợi nhuận âm của các địa điểm', 1, 1, NULL, NULL);

-- sql还原
UPDATE i18n_message SET message = 'Third-Party', created_time = 1, updated_time = 1735274608799, creator = '1', updater = NULL WHERE message_key = 'LOOKUP_10880' AND language = 'en-US';
UPDATE i18n_message SET message = '三方', created_time = 1, updated_time = 1735274608799, creator = '1', updater = NULL WHERE message_key = 'LOOKUP_10880' AND language = 'zh-TW';
UPDATE i18n_message SET message = 'Canal de Terceiros', created_time = 1, updated_time = 1735274608799, creator = '1', updater = NULL WHERE message_key = 'LOOKUP_10880' AND language = 'pt-BR';
UPDATE i18n_message SET message = '三方', created_time = 1, updated_time = 1735274608799, creator = '1', updater = NULL WHERE message_key = 'LOOKUP_10880' AND language = 'zh-CN';
UPDATE i18n_message SET message = 'Kênh Ba Bên', created_time = 1, updated_time = 1736152779752, creator = '1', updater = NULL WHERE message_key = 'LOOKUP_10880' AND language = 'vi-VN';

UPDATE i18n_message
SET message = '线下', created_time = 1, updated_time = 1735274608799, creator = '1', updater = NULL
WHERE message_key = 'LOOKUP_10881' AND language = 'en-US';

UPDATE i18n_message
SET message = '线下', created_time = 1, updated_time = 1735274608799, creator = '1', updater = NULL
WHERE message_key = 'LOOKUP_10881' AND language = 'zh-TW';

UPDATE i18n_message
SET message = 'Offline', created_time = 1, updated_time = 1735274608799, creator = '1', updater = NULL
WHERE message_key = 'LOOKUP_10881' AND language = 'pt-BR';

UPDATE i18n_message
SET message = '线下', created_time = 1, updated_time = 1735274608799, creator = '1', updater = NULL
WHERE message_key = 'LOOKUP_10881' AND language = 'zh-CN';

UPDATE i18n_message
SET message = 'Thanh Toán Offline', created_time = 1, updated_time = 1735274608799, creator = '1', updater = NULL
WHERE message_key = 'LOOKUP_10881' AND language = 'vi-VN';

UPDATE system_param set value_desc ='三方' where `type` ='CHANNEL_TYPE' and value ='LOOKUP_10880';
UPDATE system_param set value_desc ='线下' where `type` ='CHANNEL_TYPE' and value ='LOOKUP_10881';


-- 老数据默认设置出款类型为三方
update user_deposit_withdrawal set payout_type ='THIRD' where 1=1;
update agent_deposit_withdrawal set payout_type = 'THIRD' where 1= 1;

INSERT INTO system_param (id,`type`,code,value,value_desc,description,created_time,updated_time,creator,updater) VALUES
	 (12298983133199,'pay_out_type','THIRD','LOOKUP_12700','三方通道','待出款选择出款方式下拉框',1,NULL,'1',NULL),
	 (12298983133200,'pay_out_type','OFFLINE','LOOKUP_12701','人工提款','待出款选择出款方式下拉框',1,NULL,'1',NULL);


INSERT INTO i18n_message (message_type, message_key, `language`, message, created_time, updated_time, creator, updater) VALUES
	('BACK_END', 'LOOKUP_12700', 'en-US', 'Third-Party Channel', 1, 1735274608799, '1', NULL),
	('BACK_END', 'LOOKUP_12700', 'zh-TW', '三方通道', 1, 1735274608799, '1', NULL),
	('BACK_END', 'LOOKUP_12700', 'pt-BR', 'Canal de Terceiros', 1, 1735274608799, '1', NULL),
	('BACK_END', 'LOOKUP_12700', 'zh-CN', '三方通道', 1, 1735274608799, '1', NULL),
	('BACK_END', 'LOOKUP_12700', 'vi-VN', 'Kênh Ba Bên', 1, 1736152779752, '1', NULL);

INSERT INTO i18n_message (message_type, message_key, `language`, message, created_time, updated_time, creator, updater) VALUES
	('BACK_END', 'LOOKUP_12701', 'en-US', 'Manual Withdrawal', 1, 1735274608799, '1', NULL),
	('BACK_END', 'LOOKUP_12701', 'zh-TW', '人工提款', 1, 1735274608799, '1', NULL),
	('BACK_END', 'LOOKUP_12701', 'pt-BR', 'Retirada Manual', 1, 1735274608799, '1', NULL),
	('BACK_END', 'LOOKUP_12701', 'zh-CN', '人工提款', 1, 1735274608799, '1', NULL),
	('BACK_END', 'LOOKUP_12701', 'vi-VN', 'Rút Tiền Thủ Công', 1, 1736152779752, '1', NULL);

UPDATE i18n_message SET  message = 'Khuyến mãi' WHERE message_key = 'LOOKUP_11233' and language = 'vi-VN';

UPDATE `i18n_message` SET message = 'Gửi tiền qua đại lý', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_10581' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Gửi tiền qua đại lý', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_10592' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = '30 ngày', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_10992' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = '7 ngày', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_10991' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = '24 giờ', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_10990' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = '60 ngày', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_10993' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = '90 ngày', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_10994' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Tổng số phạt góc', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_SBA_BET_TYPE_473' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = '7 ngày', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_11622' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = '30 ngày', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_11623' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Weekly Bet', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_11652' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Manual Deposit', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_10443' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Manual Withdrawal', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_10453' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Percentage service fee', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_FEE_TYPE_0' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Phí giao dịch theo tỷ lệ phần trăm', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_FEE_TYPE_0' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Fixed fee', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_FEE_TYPE_1' AND language = 'en-US';
UPDATE `i18n_message` SET message = 'Phí giao dịch theo số tiền cố định', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_FEE_TYPE_1' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Percentage service fee+Fixed fee', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_FEE_TYPE_2' AND language = 'en-US';
UPDATE `i18n_message` SET message = '百分比+固定金額手續費', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_FEE_TYPE_2' AND language = 'zh-TW';
UPDATE `i18n_message` SET message = 'Phí giao dịch theo tỷ lệ phần trăm + số tiền cố định', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_FEE_TYPE_2' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Manual Withdrawal', `updated_time` = '1740043713055' WHERE message_key = 'LOOKUP_TRADE_TYPE_14' AND language = 'en-US';

INSERT INTO system_param (id,`type`,code,value,value_desc,description,created_time,updated_time,creator,updater) VALUES
	 (12298983133203,'site_status','2','LOOKUP_12712','维护','站点状态',1,NULL,'1',NULL),
	 (12298983133202,'site_status','1','LOOKUP_12711','启用','站点状态',1,NULL,'1',NULL),
	 (12298983133201,'site_status','0','LOOKUP_12710','禁用','站点状态',1,NULL,'1',NULL);


INSERT INTO i18n_message (message_type,message_key,`language`,message,created_time,updated_time,creator,updater) VALUES
	('BACK_END','LOOKUP_12710','en-US','Disable',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12710','zh-TW','禁用',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12710','pt-BR','Desativar',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12710','zh-CN','禁用',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12710','vi-VN','Vô hiệu hóa',1,1735274608799,'1',NULL),

	('BACK_END','LOOKUP_12711','en-US','Enable',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12711','zh-TW','啟用',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12711','pt-BR','habilitar',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12711','zh-CN','启用',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12711','vi-VN','Kích hoạt',1,1736152779752,'1',NULL),

	('BACK_END','LOOKUP_12712','en-US','Maintenance',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12712','zh-TW','維護中',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12712','pt-BR','Manutenção',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12712','zh-CN','维护中',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_12712','vi-VN','Bảo trì',1,1735274608799,'1',NULL);

	INSERT INTO system_param (id,`type`,code,value,value_desc,description,created_time,updated_time,creator,updater) VALUES
    	 (12298983133204,'site_domain_type','8','LOOKUP_10899','维护页','域名类型-维护页',1,NULL,'1',NULL);

INSERT INTO i18n_message (message_type,message_key,`language`,message,created_time,updated_time,creator,updater) VALUES
	('BACK_END','LOOKUP_10899','en-US','Maintenance Page',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_10899','zh-TW','維護頁',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_10899','pt-BR','Página de Manutenção',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_10899','zh-CN','维护页',1,1735274608799,'1',NULL),
	('BACK_END','LOOKUP_10899','vi-VN','Trang bảo trì',1,1735274608799,'1',NULL);

UPDATE `i18n_message` SET message = 'Unsettled', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_10220' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Sắp diễn ra', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_10800' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Unsettled', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_11020' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Nhiệm vụ người mới', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_11040' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Nhiệm vụ ngày', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_11041' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Nhiệm vụ tuần', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_11042' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Ice Hockey', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_SPORT_4' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Volleyball', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_SPORT_6' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Esports', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_SPORT_43' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Nhiệm vụ người mới', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_11602' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Unsettled', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_11630' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Thưởng thăng cấp', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_11694' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Nhiệm vụ người mới', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_DIVIDEND_TYPE_3' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Nhiệm vụ ngày', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_DIVIDEND_TYPE_4' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Nhiệm vụ tuần', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_DIVIDEND_TYPE_5' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Nhiệm vụ ngày', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_11605' AND language = 'vi-VN';
UPDATE `i18n_message` SET message = 'Nhiệm vụ tuần', `updated_time` = '1740220106554' WHERE message_key = 'LOOKUP_11606' AND language = 'vi-VN';

INSERT INTO system_param (id, type, code, value, value_desc, description, created_time, updated_time, creator, updater) VALUES (1222013133207, 'recharge_type', 'manual_recharge', 'LOOKUP_10443', '人工存款', '充值-充值类型', 1, NULL, '1', NULL);
INSERT INTO system_param (id, type, code, value, value_desc, description, created_time, updated_time, creator, updater) VALUES (1222013133208, 'withdraw_type', 'manual_withdraw', 'LOOKUP_10453', '人工提款', '提款-提款类型', 1, NULL, '1', NULL);
INSERT INTO system_param (id, type, code, value, value_desc, description, created_time, updated_time, creator, updater) VALUES (1222013133209, 'fee_type', '0', 'LOOKUP_FEE_TYPE_0', '百分比手续费', '手续费类型', 1, NULL, '1', NULL);
INSERT INTO system_param (id, type, code, value, value_desc, description, created_time, updated_time, creator, updater) VALUES (1222013133210, 'fee_type', '1', 'LOOKUP_FEE_TYPE_1', '固定金额手续费', '手续费类型', 1, NULL, '1', NULL);
INSERT INTO system_param (id, type, code, value, value_desc, description, created_time, updated_time, creator, updater) VALUES (1222013133211, 'fee_type', '2', 'LOOKUP_FEE_TYPE_2', '百分比+固定金额手续费', '手续费类型', 1, NULL, '1', NULL);
INSERT INTO system_param (id, type, code, value, value_desc, description, created_time, updated_time, creator, updater) VALUES (1222013133212, 'trade_way_type', 'manual_withdraw', 'LOOKUP_TRADE_TYPE_14', '人工提款', '交易记录-交易类型', 1, NULL, '1', NULL);

delete from `i18n_message` where message_type = 'BACK_END' and message_key = 'LOOKUP_10443';
delete from `i18n_message` where message_type = 'BACK_END' and message_key = 'LOOKUP_10453';
delete from `i18n_message` where message_type = 'BACK_END' and message_key = 'LOOKUP_FEE_TYPE_0';
delete from `i18n_message` where message_type = 'BACK_END' and message_key = 'LOOKUP_FEE_TYPE_1';
delete from `i18n_message` where message_type = 'BACK_END' and message_key = 'LOOKUP_FEE_TYPE_2';
delete from `i18n_message` where message_type = 'BACK_END' and message_key = 'LOOKUP_TRADE_TYPE_14';
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10443', 'en-US', 'Manual deposit', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10443', 'zh-TW', '人工存款', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10443', 'pt-BR', 'Depósito manual', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10443', 'zh-CN', '人工存款', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10443', 'vi-VN', 'Gửi tiền thủ công', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10453', 'en-US', 'Manual withdraw', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10453', 'zh-TW', '人工提款', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10453', 'pt-BR', 'Retirada manual', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10453', 'zh-CN', '人工提款', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_10453', 'vi-VN', 'Rút tiền thủ công', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_0', 'en-US', 'Percentage Fee', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_0', 'zh-TW', '百分比手續費', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_0', 'pt-BR', 'Taxa de porcentagem', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_0', 'zh-CN', '百分比手续费', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_0', 'vi-VN', 'Phí phần trăm', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_1', 'en-US', 'Fixed amount fee', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_1', 'zh-TW', '固定金額手續費', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ('BACK_END', 'LOOKUP_FEE_TYPE_1', 'pt-BR', 'Taxa de valor fixo', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_1', 'zh-CN', '固定金额手续费', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_1', 'vi-VN', 'Phí cố định', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_2', 'en-US', 'Percentage Fee+Fixed amount fee', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_2', 'zh-TW', '百分比手續費+固定金額手續費', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_2', 'pt-BR', 'Taxa de porcentagem+Taxa de valor fixo', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_2', 'zh-CN', '百分比手续费+固定金额手续费', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_FEE_TYPE_2', 'vi-VN', 'Phí phần trăm+Phí cố định', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_TRADE_TYPE_14', 'en-US', 'Manual withdraw', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_TRADE_TYPE_14', 'zh-TW', '人工提款', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_TRADE_TYPE_14', 'pt-BR', 'Retirada manual', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_TRADE_TYPE_14', 'zh-CN', '人工提款', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_TRADE_TYPE_14', 'vi-VN', 'Rút tiền thủ công', 1, 1736152779752, '1', NULL);


-- 补充多语言
UPDATE i18n_message SET message = 'Thưởng huy hiệu', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11232' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Chờ xử lý', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_10220' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Khuyến mãi', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_10616' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Khuyến mãi', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_10670' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Partners', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_10880' AND language = 'en-US';
UPDATE i18n_message SET message = '三方通道', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_10880' AND language = 'zh-TW';
UPDATE i18n_message SET message = 'Bên thứ ba', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_10880' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Manual Withdrawal', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_10881' AND language = 'en-US';
UPDATE i18n_message SET message = '人工出款', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_10881' AND language = 'zh-TW';
UPDATE i18n_message SET message = 'Rút tiền thủ công', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_10881' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Chờ xử lý', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11020' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'N.Vụ người mới', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11040' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'N.Vụ ngày', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11041' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'N.Vụ tuần', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11042' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Khuyến mãi', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11601' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'N.Vụ người mới', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11602' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Thưởng huy hiệu', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11603' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Khuyến mãi', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_BUSINESS_COIN_TYPE_4' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Khuyến mãi', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_COIN_TYPE_9' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Chờ xử lý', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11630' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Khuyến mãi', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_PALTFORM_BUSINESS_COIN_TYPE_2' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Thưởng huy hiệu', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_PALTFORM_BUSINESS_COIN_TYPE_3' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Khuyến mãi', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_PALTFORM_COIN_TYPE_2' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Thưởng huy hiệu', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_PALTFORM_COIN_TYPE_3' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'N.Vụ người mới', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_DIVIDEND_TYPE_3' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'N.Vụ ngày', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_DIVIDEND_TYPE_4' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Khuyến mãi', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_DIVIDEND_TYPE_2' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Thưởng huy hiệu', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_DIVIDEND_TYPE_6' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'N.Vụ tuần', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_DIVIDEND_TYPE_5' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'N.Vụ ngày', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11605' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Rương huy hiệu', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_DIVIDEND_TYPE_7' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'Rương huy hiệu', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11604' AND language = 'vi-VN';
UPDATE i18n_message SET message = 'N.Vụ tuần', updated_time = '1740386418987' WHERE message_key = 'LOOKUP_11606' AND language = 'vi-VN';

delete from system_param where type = 'deposit_account' and value = 'LOOKUP_11775';
INSERT INTO system_param (id,`type`,code,value,value_desc,description,created_time,updated_time,creator,updater) VALUES
	 (12298983133206,'deposit_account','0','LOOKUP_11775','账号','账号',1,NULL,'1',NULL);

delete from i18n_message where message_key = 'LOOKUP_11775' and message_type = 'BACK_END';
INSERT INTO i18n_message (message_type,message_key,`language`,message,created_time,updated_time,creator,updater) VALUES
	 ('BACK_END','LOOKUP_11775','en-US','Account',1,1735274608799,'1',NULL),
	 ('BACK_END','LOOKUP_11775','zh-TW','帳號',1,1735274608799,'1',NULL),
	 ('BACK_END','LOOKUP_11775','pt-BR','Conta',1,1735274608799,'1',NULL),
	 ('BACK_END','LOOKUP_11775','zh-CN','账号',1,1735274608799,'1',NULL),
	 ('BACK_END','LOOKUP_11775','vi-VN','Tài khoản',1,1735274608799,'1',NULL);


delete from system_param where value = 'LOOKUP_PLATFORM_REPLY';
INSERT INTO system_param (id,`type`,code,value,value_desc,description,created_time,updated_time,creator,updater) VALUES
	 (12298983133207,'platform_reply','0','LOOKUP_PLATFORM_REPLY','平台客服','平台客服使用',1,NULL,'1',NULL);

delete from i18n_message where message_key = 'LOOKUP_PLATFORM_REPLY';
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES
('BACK_END', 'LOOKUP_PLATFORM_REPLY', 'en-US', 'Customer service', 1, 1, NULL, NULL),
('BACK_END', 'LOOKUP_PLATFORM_REPLY', 'zh-TW', '平台客服', 1, 1, NULL, NULL),
('BACK_END', 'LOOKUP_PLATFORM_REPLY', 'pt-BR', '平台客服', 1, 1, NULL, NULL),
('BACK_END', 'LOOKUP_PLATFORM_REPLY', 'zh-CN', '平台客服', 1, 1, NULL, NULL),
('BACK_END', 'LOOKUP_PLATFORM_REPLY', 'vi-VN', 'Dịch vụ Chăm sóc khách hàng', 1, 1, NULL, NULL);



update i18n_message inm set message='Dear Partner: Congratulations on your successful withdrawal of%s%s~' where inm.message_key ='BIZ_AGENT_WITHDRAWAL_SUCCESS_CONTENT_8100' and inm.language ='en-US';

update i18n_message inm set message='Kính gửi Đối tác: Chúc mừng bạn đã rút tiền thành công với số tiền%s%s~' where inm.message_key ='BIZ_AGENT_WITHDRAWAL_SUCCESS_CONTENT_8100' and inm.language ='vi-VN';


update i18n_message inm set message='尊敬的合伙人：您的代理佣金%s%s已发放成功，请查收。' where inm.message_key ='BIZ_AGENT_COMMISSION_PAYMENT_SUCCESS_CONTENT_8100' and inm.language ='zh-CN';
update i18n_message inm set message='尊敬的合夥人：您的代理佣金%s%s已發放成功，請查收。' where inm.message_key ='BIZ_AGENT_COMMISSION_PAYMENT_SUCCESS_CONTENT_8100' and inm.language ='zh-TW';
update i18n_message inm set message='Dear Partner: Your agent commission%s%s has been issued successfully, please check your account' where inm.message_key ='BIZ_AGENT_COMMISSION_PAYMENT_SUCCESS_CONTENT_8100' and inm.language ='en-US';
update i18n_message inm set message='Kính gửi Đối tác: Hoa hồng đại lý của bạn%s%s đã được phân phát thành công, vui lòng kiểm tra tài khoản của bạn.' where inm.message_key ='BIZ_AGENT_COMMISSION_PAYMENT_SUCCESS_CONTENT_8100' and inm.language ='vi-VN';

update i18n_message inm set inm.message = 'Name' where inm.message_key = 'LOOKUP_11768' and inm.language = 'en-US';
update i18n_message inm set inm.message = '姓名' where inm.message_key = 'LOOKUP_11768' and inm.language = 'zh-TW';
update i18n_message inm set inm.message = 'Nome' where inm.message_key = 'LOOKUP_11768' and inm.language = 'pt-BR';
update i18n_message inm set inm.message = '姓名' where inm.message_key = 'LOOKUP_11768' and inm.language = 'zh-CN';
update i18n_message inm set inm.message = 'Họ tên' where inm.message_key = 'LOOKUP_11768' and inm.language = 'vi-VN';
update system_param set value_desc = '姓名' where value ='LOOKUP_11768';

