
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (112312334232323, 'sub_task_type', 'depositDaily', 'LOOKUP_11810', '每日存款', '任务类型', 1, NULL, '1', NULL);


INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (112312334232324, 'sub_task_type', 'inviteFriendsWeek', 'LOOKUP_11811', '每周邀请任务', '任务类型', 1, NULL, '1', NULL);

-- 	DELETE from i18n_message where message_key='LOOKUP_11810'
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ( 'BACK_END', 'LOOKUP_11810', 'en-US', 'Daily deposits', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ( 'BACK_END', 'LOOKUP_11810', 'zh-TW', '每日存款', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ('BACK_END', 'LOOKUP_11810', 'pt-BR', 'Depósito Diário', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ( 'BACK_END', 'LOOKUP_11810', 'zh-CN', '每日存款', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ( 'BACK_END', 'LOOKUP_11810', 'vi-VN', 'Nạp Tiền Hàng Ngày', 1, 1736152779752, '1', NULL);

-- DELETE from i18n_message where message_key='LOOKUP_11811';
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ( 'BACK_END', 'LOOKUP_11811', 'en-US', 'Invite Friends Weekly', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ('BACK_END', 'LOOKUP_11811', 'zh-TW', '每週邀請好友', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ( 'BACK_END', 'LOOKUP_11811', 'pt-BR', 'Convidar amigos semanalmente', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ( 'BACK_END', 'LOOKUP_11811', 'zh-CN', '每周邀请好友', 1, 1735274608799, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ( 'BACK_END', 'LOOKUP_11811', 'vi-VN', 'Mời bạn bè hàng tuần', 1, 1736152779752, '1', NULL);


