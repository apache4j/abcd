
update site_activity_free_game_record set send_status=1 where order_type=2 ;


INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623469, 'typing_adjust_type', '10', 'LOOKUP_TYPING_ADJUST_TYPE_10', '风控调整', '打码量-调整类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623471, 'business_coin_type', '11', 'LOOKUP_BUSINESS_COIN_TYPE_11', '风控调整', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623472, 'manual_adjust_down_type', '11', 'LOOKUP_11275', '风控调整', '会员资金调整类型减款-风控调整', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623473, 'coin_type', '25', 'LOOKUP_COIN_TYPE_25', '风控调整增加金额', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623474, 'coin_type', '26', 'LOOKUP_COIN_TYPE_26', '风控调整扣除金额', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623475, 'manual_adjust_type', '11', 'LOOKUP_11205', '风控调整', '会员资金调整类型加款-风控调整', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623476, 'paltform_business_coin_type', '6', 'LOOKUP_PALTFORM_BUSINESS_COIN_TYPE_6', '其他调整', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623477, 'paltform_coin_type', '6', 'LOOKUP_PALTFORM_COIN_TYPE_6', 'VIP福利(平台币上分)', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623478, 'paltform_coin_type', '7', 'LOOKUP_PALTFORM_COIN_TYPE_7', 'VIP福利(平台币下分)', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623479, 'paltform_coin_type', '8', 'LOOKUP_PALTFORM_COIN_TYPE_8', '活动优惠(平台币上分)', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623480, 'paltform_coin_type', '9', 'LOOKUP_PALTFORM_COIN_TYPE_9', '活动优惠(平台币下分', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623481, 'paltform_coin_type', '10', 'LOOKUP_PALTFORM_COIN_TYPE_10', '其他(平台币上分)', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623482, 'paltform_coin_type', '11', 'LOOKUP_PALTFORM_COIN_TYPE_11', '其他(平台币下分)', '账变记录-业务类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623483, 'platform_coin_manual_adjust_way', '1', 'LOOKUP_PLATFORM_COIN_MANUAL_ADJUST_WAY_1', '平台币上分', '平台币上下分-调整方式', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623484, 'platform_coin_manual_adjust_way', '2', 'LOOKUP_PLATFORM_COIN_MANUAL_ADJUST_WAY_2', '平台币下分', '平台币上下分-调整方式', 1, NULL, '1', NULL);

INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623485, 'platform_coin_manual_adjust_up_type', '1', 'LOOKUP_PLATFORM_COIN_MANUAL_ADJUST_UP_TYPE_1', '会员VIP优惠', '平台币上分-调整类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623486, 'platform_coin_manual_adjust_up_type', '2', 'LOOKUP_PLATFORM_COIN_MANUAL_ADJUST_UP_TYPE_2', '会员活动', '平台币上分-调整类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623487, 'platform_coin_manual_adjust_up_type', '3', 'LOOKUP_PLATFORM_COIN_MANUAL_ADJUST_UP_TYPE_3', '其他调整', '平台币上分-调整类型', 1, NULL, '1', NULL);

INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623488, 'platform_coin_manual_adjust_down_type', '1', 'LOOKUP_PLATFORM_COIN_MANUAL_ADJUST_DOWN_TYPE_1', '会员VIP优惠', '平台币下分-调整类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623489, 'platform_coin_manual_adjust_down_type', '2', 'LOOKUP_PLATFORM_COIN_MANUAL_ADJUST_DOWN_TYPE_2', '会员活动', '平台币下分-调整类型', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623490, 'platform_coin_manual_adjust_down_type', '3', 'LOOKUP_PLATFORM_COIN_MANUAL_ADJUST_DOWN_TYPE_3', '其他调整', '平台币下分-调整类型', 1, NULL, '1', NULL);

INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623491, 'platform_coin_review_status', '1', 'LOOKUP_PLATFORM_COIN_REVIEW_STATUS_1', '待处理', '审核状态', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623492, 'platform_coin_review_status', '2', 'LOOKUP_PLATFORM_COIN_REVIEW_STATUS_2', '处理中', '审核状态', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623493, 'platform_coin_review_status', '3', 'LOOKUP_PLATFORM_COIN_REVIEW_STATUS_3', '审核通过', '审核状态', 1, NULL, '1', NULL);
INSERT INTO `system_param` (`id`, `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES (1123123342623494, 'platform_coin_review_status', '4', 'LOOKUP_PLATFORM_COIN_REVIEW_STATUS_4', '一审拒绝', '审核状态', 1, NULL, '1', NULL);








 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '0', 'LOOKUP_SECURITY_COIN_TYPE_0', '会员存款', '保证金帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '1', 'LOOKUP_SECURITY_COIN_TYPE_1', '会员提款', '保证金帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '2', 'LOOKUP_SECURITY_COIN_TYPE_2', '代理存款', '保证金帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '3', 'LOOKUP_SECURITY_COIN_TYPE_3', '代理提款', '保证金帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '4', 'LOOKUP_SECURITY_COIN_TYPE_4', '提款失败', '提款失败帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '5', 'LOOKUP_SECURITY_COIN_TYPE_5', '提款成功保证金', '提款成功帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '6', 'LOOKUP_SECURITY_COIN_TYPE_6', '增加保证金', '增加保证金帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '7', 'LOOKUP_SECURITY_COIN_TYPE_7', '减少保证金', '减少保证金帐变类型', 1, NULL, '1', NULL);
  INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'security_coin_type', '8', 'LOOKUP_SECURITY_COIN_TYPE_8', '减少保证金成功', '减少保证金帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '9', 'LOOKUP_SECURITY_COIN_TYPE_9', '减少保证金失败', '减少保证金失败帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '10', 'LOOKUP_SECURITY_COIN_TYPE_10', '增加透支额度', '增加保证金透支额度帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '11', 'LOOKUP_SECURITY_COIN_TYPE_11', '减少透支额度', '减少保证金透支额度帐变类型', 1, NULL, '1', NULL);
   INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'security_coin_type', '12', 'LOOKUP_SECURITY_COIN_TYPE_12', '减少透支额度成功', '减少保证金帐变类型', 1, NULL, '1', NULL);
  INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'security_coin_type', '13', 'LOOKUP_SECURITY_COIN_TYPE_13', '减少透支额度失败', '减少保证金透支额度失败帐变类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_coin_type', '14', 'LOOKUP_SECURITY_COIN_TYPE_14', '透支额度抵扣', '透支额度抵扣帐变类型', 1, NULL, '1', NULL);


 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_0', 'zh-CN', '会员存款', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_0', 'en-US', 'Member Deposit', 1, 1751022817545, '1', NULL);


 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_1', 'zh-CN', '会员提款', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_1', 'en-US', 'Member withdrawal', 1, 1751022817545, '1', NULL);


 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_2', 'zh-CN', '代理存款', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_2', 'en-US', 'Deposit agent', 1, 1751022817545, '1', NULL);


 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_3', 'zh-CN', '代理提款', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_3', 'en-US', 'Agent withdrawal', 1, 1751022817545, '1', NULL);


 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_4', 'zh-CN', '提款失败', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_4', 'en-US', 'Withdrawal failed', 1, 1751022817545, '1', NULL);

 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_5', 'zh-CN', '提款成功', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_5', 'en-US', 'Reduce success', 1, 1751022817545, '1', NULL);


 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_6', 'zh-CN', '增加保证金', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_6', 'en-US', 'Increase margin', 1, 1751022817545, '1', NULL);


 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_7', 'zh-CN', '减少保证金', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_7', 'en-US', 'Reduce margin', 1, 1751022817545, '1', NULL);


 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_8', 'zh-CN', '减少保证金成功', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_8', 'en-US', 'Reduce margin success', 1, 1751022817545, '1', NULL);

 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_9', 'zh-CN', '减少保证金失败', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_9', 'en-US', 'Reduce margin failed', 1, 1751022817545, '1', NULL);

 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_10', 'zh-CN', '增加透支额度', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_10', 'en-US', 'Increase overdraft limit', 1, 1751022817545, '1', NULL);

 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_11', 'zh-CN', '减少透支额度', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_11', 'en-US', 'Reduce overdraft limit', 1, 1751022817545, '1', NULL);


 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_12', 'zh-CN', '减少透支额度成功', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_12', 'en-US', 'Reduce overdraft limit success', 1, 1751022817545, '1', NULL);

 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_13', 'zh-CN', '减少透支额度失败', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_13', 'en-US', 'Reduce overdraft limit failed', 1, 1751022817545, '1', NULL);

  INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_14', 'zh-CN', '透支额度抵扣', 1, 1751022817545, '1', NULL);
  INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'BACK_END', 'LOOKUP_SECURITY_COIN_TYPE_14', 'en-US', 'Overdraft limit deduction', 1, 1751022817545, '1', NULL);


 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'site_security_balance_account', 'AVAILABLE', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_AVAILABLE', '保证金', '保证金账户类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'site_security_balance_account', 'OVERDRAW', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW', '透支额度', '保证金账户类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'site_security_balance_account', 'FROZEN', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_FROZEN', '冻结保证金', '保证金账户类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'site_security_balance_account', 'OVERDRAW_FROZEN', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_FROZEN', '冻结透支额度', '保证金账户类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'site_security_balance_account', 'OVERDRAW_AVAILABLE', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_AVAILABLE', '剩余透支额度', '保证金账户类型', 1, NULL, '1', NULL);




  INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_AVAILABLE', 'zh-CN', '保证金', 1, 1751022817545, '1', NULL);
  INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_AVAILABLE', 'en-US', 'margin', 1, 1751022817545, '1', NULL);

   INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW', 'zh-CN', '透支额度', 1, 1751022817545, '1', NULL);
   INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW', 'en-US', 'Overdraft limit', 1, 1751022817545, '1', NULL);

   INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_FROZEN', 'zh-CN', '冻结保证金', 1, 1751022817545, '1', NULL);
   INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_FROZEN', 'en-US', 'Freeze margin', 1, 1751022817545, '1', NULL);

   INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_FROZEN', 'zh-CN', '冻结透支额度', 1, 1751022817545, '1', NULL);
   INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_FROZEN', 'en-US', 'Freeze overdraft limit', 1, 1751022817545, '1', NULL);

   INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_AVAILABLE', 'zh-CN', '剩余透支额度', 1, 1751022817545, '1', NULL);
   INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'BACK_END', 'LOOKUP_SITE_SECURITY_BALANCE_ACCOUNT_OVERDRAW_AVAILABLE', 'en-US', 'Remaining overdraft limit', 1, 1751022817545, '1', NULL);


 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_status', '0', 'LOOKUP_SECURITY_STATUS_0', '关闭', '保证金管理状态', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_status', '1', 'LOOKUP_SECURITY_STATUS_1', '已开启', '保证金管理状态', 1, NULL, '1', NULL);

 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_STATUS_0', 'zh-CN', '关闭', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_STATUS_0', 'en-US', 'Close', 1, 1751022817545, '1', NULL);

 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_STATUS_1', 'zh-CN', '已开启', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_STATUS_1', 'en-US', 'Open', 1, 1751022817545, '1', NULL);



 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_account_status', '1', 'LOOKUP_SECURITY_ACCOUNT_STATUS_1', '正常', '保证金账户状态', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_account_status', '2', 'LOOKUP_SECURITY_ACCOUNT_STATUS_2', '预警', '保证金账户状态', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'security_account_status', '3', 'LOOKUP_SECURITY_ACCOUNT_STATUS_3', '透支', '保证金账户状态', 1, NULL, '1', NULL);

 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_1', 'zh-CN', '正常', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_1', 'en-US', 'Normal', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_1', 'ko-KR', '정상', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_1', 'vi-VN', 'Bình thường', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_1', 'pt-BR', 'Normal', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_1', 'zh-TW', '正常', 1, 1751022817545, '1', NULL);



 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_2', 'zh-CN', '预警', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_2', 'en-US', 'Early Warning', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_2', 'ko-KR', '조기 경고', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_2', 'vi-VN', 'Cảnh báo sớm', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_2', 'pt-BR', 'Alerta precoce', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_2', 'zh-TW', '預警', 1, 1751022817545, '1', NULL);

 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_3', 'zh-CN', '透支', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_3', 'en-US', 'Overdraw', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_3', 'ko-KR', '당좌 대월', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_3', 'vi-VN', 'thấu chi', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_3', 'pt-BR', 'Alerta precoce', 1, 1751022817545, '1', NULL);
 INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'BACK_END', 'LOOKUP_SECURITY_ACCOUNT_STATUS_3', 'zh-TW', '透支', 1, 1751022817545, '1', NULL);



 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_source_coin_type', '0', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_0', '会员存款', '保证金业务类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_source_coin_type', '1', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_1', '会员提款', '保证金业务类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_source_coin_type', '2', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_2', '代理存款', '保证金业务类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_source_coin_type', '3', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_3', '代理提款', '保证金业务类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_source_coin_type', '4', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_4', '增加保证金', '保证金业务类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_source_coin_type', '5', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_5', '减少保证金', '保证金业务类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_source_coin_type', '6', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_6', '增加透支额度', '保证金业务类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_source_coin_type', '7', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_7', '减少透支额度', '保证金业务类型', 1, NULL, '1', NULL);



INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_0', 'zh-CN', '会员存款', 1, 1751022817545, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_0', 'en-US', 'Member Deposit', 1, 1751022817545, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_1', 'zh-CN', '会员提款', 1, 1751022817545, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_1', 'en-US', 'Member withdrawal', 1, 1751022817545, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_2', 'zh-CN', '代理存款', 1, 1751022817545, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_2', 'en-US', 'Deposit agent', 1, 1751022817545, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_3', 'zh-CN', '代理提款', 1, 1751022817545, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_3', 'en-US', 'Agent withdrawal', 1, 1751022817545, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_4', 'zh-CN', '增加保证金', 1, 1751022817545, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_4', 'en-US', 'Increase margin', 1, 1751022817545, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_5', 'zh-CN', '减少保证金', 1, 1751022817545, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_5', 'en-US', 'Reduce margin', 1, 1751022817545, '1', NULL);


INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_6', 'zh-CN', '增加透支额度', 1, 1751022817545, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_6', 'en-US', 'Increase overdraft limit', 1, 1751022817545, '1', NULL);

INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_7', 'zh-CN', '减少透支额度', 1, 1751022817545, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
VALUES ( 'BACK_END', 'LOOKUP_SECURITY_SOURCE_COIN_TYPE_7', 'en-US', 'Reduce overdraft limit', 1, 1751022817545, '1', NULL);




INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_user_type', 'user', 'LOOKUP_SECURITY_USER_TYPE_1', '会员', '保证金帐号类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_user_type', 'agent', 'LOOKUP_SECURITY_USER_TYPE_2', '代理', '保证金帐号类型', 1, NULL, '1', NULL);
 INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
 VALUES ( 'security_user_type', 'site', 'LOOKUP_SECURITY_USER_TYPE_3', '站点', '保证金帐号类型', 1, NULL, '1', NULL);


  INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'BACK_END', 'LOOKUP_SECURITY_USER_TYPE_1', 'zh-CN', '会员', 1, 1751022817545, '1', NULL);
  INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
  VALUES ( 'BACK_END', 'LOOKUP_SECURITY_USER_TYPE_1', 'en-US', 'User', 1, 1751022817545, '1', NULL);


   INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'BACK_END', 'LOOKUP_SECURITY_USER_TYPE_2', 'zh-CN', '代理', 1, 1751022817545, '1', NULL);
   INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
   VALUES ( 'BACK_END', 'LOOKUP_SECURITY_USER_TYPE_2', 'en-US', 'Agent', 1, 1751022817545, '1', NULL);


    INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
    VALUES ( 'BACK_END', 'LOOKUP_SECURITY_USER_TYPE_3', 'zh-CN', '站点', 1, 1751022817545, '1', NULL);
    INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
    VALUES ( 'BACK_END', 'LOOKUP_SECURITY_USER_TYPE_3', 'en-US', 'Site', 1, 1751022817545, '1', NULL);



    INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
    VALUES ( 'security_amount_direct', '+', 'LOOKUP_AMOUNT_DIRECT_1', '收入', '保证金收支类型', 1, NULL, '1', NULL);
    INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`)
    VALUES ( 'security_amount_direct', '-', 'LOOKUP_AMOUNT_DIRECT_2', '支出', '保证金收支类型', 1, NULL, '1', NULL);

    INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
    VALUES ( 'BACK_END', 'LOOKUP_AMOUNT_DIRECT_1', 'zh-CN', '收入', 1, 1751022817545, '1', NULL);
    INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
    VALUES ( 'BACK_END', 'LOOKUP_AMOUNT_DIRECT_1', 'en-US', '+', 1, 1751022817545, '1', NULL);

    INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
    VALUES ( 'BACK_END', 'LOOKUP_AMOUNT_DIRECT_2', 'zh-CN', '支出', 1, 1751022817545, '1', NULL);
    INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`)
    VALUES ( 'BACK_END', 'LOOKUP_AMOUNT_DIRECT_2', 'en-US', '-', 1, 1751022817545, '1', NULL);






INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES

    ( 'sh_game_type', '21', 'LOOKUP_SH_GAME_TYPE_21', '越南鱼虾蟹', '视讯游戏类型', 1, NULL, '1', NULL);


INSERT INTO `system_param` ( `type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES

    ( 'sh_game_type', '22', 'LOOKUP_SH_GAME_TYPE_22', '闪电龙虎', '视讯游戏类型', 1, NULL, '1', NULL);



INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_SH_GAME_TYPE_21', 'en-US', 'Vietnamese fish, shrimp and crab', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_SH_GAME_TYPE_21', 'zh-TW', '越南魚蝦蟹', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_SH_GAME_TYPE_21', 'pt-BR', 'Peixe, camarão e caranguejo vietnamitas', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_SH_GAME_TYPE_21', 'zh-CN', '越南鱼虾蟹', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_SH_GAME_TYPE_21', 'vi-VN', 'Cá, tôm và cua Việt Nam', 1, 1, NULL, NULL);





INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_SH_GAME_TYPE_22', 'en-US', 'Lightning Dragon Tiger', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_SH_GAME_TYPE_22', 'zh-TW', '閃電龍虎', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_SH_GAME_TYPE_22', 'pt-BR', 'Dragão Tigre Relâmpago', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_SH_GAME_TYPE_22', 'zh-CN', '闪电龙虎', 1, 1, NULL, NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) VALUES ('BACK_END', 'LOOKUP_SH_GAME_TYPE_22', 'vi-VN', 'Sét Rồng Hổ', 1, 1, NULL, NULL);





INSERT INTO `system_param` (`type`, `code`, `value`, `value_desc`, `description`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    ( 'venue_sport_list', 'SBA', 'LOOKUP_VENUE_SPORT', '沙巴体育', '沙巴体育', 1, NULL, '1', NULL);





INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_SPORT', 'en-US', 'Saba Sports/Sportsbook', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_SPORT', 'zh-TW', '沙巴體育', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_SPORT', 'pt-BR', 'Sabá Esportes', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_SPORT', 'zh-CN', '沙巴体育', 1, 1735274608799, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_SPORT', 'vi-VN', 'Thể thao Saba', 1, 1736152779752, '1', NULL);
INSERT INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `created_time`, `updated_time`, `creator`, `updater`) VALUES ( 'BACK_END', 'LOOKUP_VENUE_SPORT', 'ko-KR', '사바 스포츠', 1745826480664, 1745826480664, '1', '1');


