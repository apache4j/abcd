INSERT INTO `system_activity_template` (`id`, `activity_template`, `activity_name`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1, 'FIRST_DEPOSIT', '首次充值', 1755658725000, 1755658725000, 'superadmin', 'superadmin');
INSERT INTO `system_activity_template` (`id`, `activity_template`, `activity_name`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (2, 'SECOND_DEPOSIT', '二次充值', 1755658725000, 1755658725000, 'superadmin', 'superadmin');
INSERT INTO `system_activity_template` (`id`, `activity_template`, `activity_name`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (3, 'FREE_WHEEL', '免费旋转', 1755658725000, 1755658725000, 'superadmin', 'superadmin');
INSERT INTO `system_activity_template` (`id`, `activity_template`, `activity_name`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (4, 'ASSIGN_DAY', '指定日期存款', 1755658725000, 1755658725000, 'superadmin', 'superadmin');
INSERT INTO `system_activity_template` (`id`, `activity_template`, `activity_name`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (5, 'LOSS_IN_SPORTS', '游戏类别负盈利', 1755658725000, 1755658725000, 'superadmin', 'superadmin');
INSERT INTO `system_activity_template` (`id`, `activity_template`, `activity_name`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (6, 'DAILY_COMPETITION', '每日竞赛', 1755658725000, 1755658725000, 'superadmin', 'superadmin');
INSERT INTO `system_activity_template` (`id`, `activity_template`, `activity_name`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (7, 'SPIN_WHEEL', '转盘', 1755658725000, 1755658725000, 'superadmin', 'superadmin');
INSERT INTO `system_activity_template` (`id`, `activity_template`, `activity_name`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (8, 'RED_BAG_RAIN', '红包雨', 1755658725000, 1755658725000, 'superadmin', 'superadmin');
INSERT INTO `system_activity_template` (`id`, `activity_template`, `activity_name`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (9, 'CHECKIN', '签到', 1755658725000, 1755658725000, 'superadmin', 'superadmin');
INSERT INTO `system_activity_template` (`id`, `activity_template`, `activity_name`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (10, 'STATIC', '静态', 1755658725000, 1755658725000, 'superadmin', 'superadmin');

SET
@paramLatestId = (SELECT MAX(id) FROM system_param)  + 1;
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`,
                            `creator`, `updater`)
VALUES (@paramLatestId, 'withdraw_collect', 'ifscCode', 'LOOKUP_11333', 'IFSC', '提现信息收集', 1, NULL, '1',
        NULL);

SET
@paramLatestId = (SELECT MAX(id) FROM system_param)  + 1;
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`,
                            `creator`, `updater`)
VALUES (@paramLatestId, 'ifsc', '0', 'LOOKUP_11776', 'IFSC', 'IFSC', 1, NULL, '1', NULL);

update site_activity_free_game_consume a
    join user_info b
on a.user_id=b.user_id
    set a.currency_code = b.main_currency;
INSERT INTO `system_param` (`type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`,
                            `creator`, `updater`)
VALUES ('acelt_game_info', 'AZSSC', 'LOOKUP_ACELT_GAME_AZSSC', '澳洲幸运5', '澳洲幸运5', 1, NULL, '1', NULL),
       ('acelt_game_info', 'AZPK10', 'LOOKUP_ACELT_GAME_AZPK10', '澳洲幸运10', '澳洲幸运10', 1, NULL, '1', NULL);




UPDATE venue_info SET venue_join_type = 1 where venue_code in ('ACELT','WP_ACELT','WP_ACELT_02','WP_ACELT_03','SBA');

UPDATE venue_info SET venue_join_type = 2 where venue_code in ('CMD','BTI','S128','TF');

UPDATE venue_info SET venue_join_type = 3 where venue_join_type is null;

UPDATE game_info
SET venue_name = CONCAT('LOOKUP_VENUE_INIT_NAME_', venue_code);

INSERT INTO `language_manager` (`id`, `site_code`, `name`, `code`, `show_code`, `icon`, `sort`, `status`, `operate_time`, `operator`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES (7, '0', 'हिन्दी', 'hi-IN', 'IN', 'baowang/fe027c65f775452982e5469acfcd7f22.png', 1, 1, 1756262377802, 'lizheng01', 1, NULL, '1785263098836471810', NULL);



INSERT INTO `system_param` (`type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES
    ('dtx_sa_game_detail', 'DTRDragonBig', 'LOOKUP_SA_GAME_dtx_DTRDragonBig', '龙-大', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRDragonSmall', 'LOOKUP_SA_GAME_dtx_DTRDragonSmall', '龙-小', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRDragonOdd', 'LOOKUP_SA_GAME_dtx_DTRDragonOdd', '龙-单', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRDragonEven', 'LOOKUP_SA_GAME_dtx_DTRDragonEven', '龙-双', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRDragonRed', 'LOOKUP_SA_GAME_dtx_DTRDragonRed', '龙-红', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRDragonBlack', 'LOOKUP_SA_GAME_dtx_DTRDragonBlack', '龙-黑', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRTigerBig', 'LOOKUP_SA_GAME_dtx_DTRTigerBig', '虎-大', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRTigerSmall', 'LOOKUP_SA_GAME_dtx_DTRTigerSmall', '虎-小', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRTigerOdd', 'LOOKUP_SA_GAME_dtx_DTRTigerOdd', '虎-单', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRTigerEven', 'LOOKUP_SA_GAME_dtx_DTRTigerEven', '虎-双', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRTigerRed', 'LOOKUP_SA_GAME_dtx_DTRTigerRed', '虎-红', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL),
    ('dtx_sa_game_detail', 'DTRTigerBlack', 'LOOKUP_SA_GAME_dtx_DTRTigerBlack', '虎-黑', 'SA-视讯龙虎-结果游戏详情', 1, NULL, '1', NULL);