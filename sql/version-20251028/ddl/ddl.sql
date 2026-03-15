--- 兑换码相关表
CREATE TABLE `site_activity_redemption_code_base_info` (
  `id` bigint NOT NULL COMMENT '兑换码id，采用雪花算法生成的id',
  `order_no` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '兑换码订单号',
  `category` int NOT NULL COMMENT '兑换码类型,0:通用兑换码,1:唯一兑换码',
  `platform_or_fiat_currency` varchar(5) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '平台币或法币：0:平台币，1:法币',
  `activity_rule_i18n_code` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动规则，多语言',
  `activity_rule_desc_i18n_code` text COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动规则描述，多语言',
  `head_picture_app_i18n_code` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'app端活动头图',
  `head_picture_pc_i18n_code` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'PC端活动头图',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间（最初操作时间）',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间（最新操作时间）',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人（最初操作人）',
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '修改人（最新操作人）',
  `client_switch` int DEFAULT NULL COMMENT '客户端开关,0:关闭，1:开启',
  `deadline_type` int DEFAULT NULL COMMENT '活动时效类型,0:限时，1:长期',
  `site_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点编码',
  `status` int DEFAULT NULL COMMENT '状态：0:禁用，1:正常',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin;
CREATE TABLE `site_activity_redemption_gen_code_info` (
  `id` bigint NOT NULL COMMENT '兑换码生成ID',
  `activity_detail_id` bigint DEFAULT NULL COMMENT '兑换码明细表主键',
  `code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '6位兑换码,生成规则:数值字母随机组合',
  `batch_no` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '批次号,10位数字',
  `currency` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种,冗余字段',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '修改人',
  `status` int DEFAULT NULL COMMENT '状态：0:未兑换，1:已兑换',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='兑换码表';
CREATE TABLE `site_activity_redemption_code_exchange_info` (
  `id` bigint NOT NULL COMMENT '兑换码领取记录id',
  `code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '兑换码',
  `category` int DEFAULT NULL COMMENT '兑换码类型,0:通用兑换码,1:唯一兑换码',
  `currency` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '兑换码币种',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '兑换金额',
  `batch_no` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '兑换码批次号',
  `user_id` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '兑换会员ID',
  `created_time` bigint DEFAULT NULL COMMENT '兑换时间',
  `updated_time` bigint DEFAULT NULL COMMENT '修改时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人,填写user_id',
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '修改人',
  `order_no` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '订单号',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='兑换码兑换表';

CREATE TABLE `site_activity_redemption_code_detail_info` (
  `id` bigint NOT NULL COMMENT '兑换码详情id',
  `activity_id` bigint DEFAULT NULL COMMENT '活动ID，关联兑换码主键id',
  `category` int DEFAULT NULL COMMENT '兑换码类型,0:通用兑换码,1:唯一兑换码',
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点编码',
  `top_limit` int DEFAULT NULL COMMENT '兑换使用人数上限,兑换码类型=0，且top_limit=0时，表示为通用码，没有兑换人数限制；兑换码类型=1时，top_limit=1,1人1码',
  `condition` int DEFAULT NULL COMMENT '兑换条件,1:无限制用户，2:存款用户，3：当天存款用户（兑换码生效当天）；4：三天内存款用户',
  `award` decimal(10,2) DEFAULT NULL COMMENT '奖金',
  `quantity` int DEFAULT NULL COMMENT '兑换码数量',
  `currency` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
  `order_no` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '订单号，活动兑换码主表中的order_no,作为冗余字段，不需要查主表',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '修改人',
  `wash_ratio` decimal(8,2) DEFAULT NULL COMMENT '流码倍率,可以针对每种币种设置不同的值',
  `start_time` bigint DEFAULT NULL COMMENT '兑换码生效时间,精确到秒',
  `end_time` bigint DEFAULT NULL COMMENT '兑换码失效时间',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='兑换码详情表,每个币种的兑换码都会保存一条记录';

DROP TABLE IF EXISTS `account_business_transfer`;
CREATE TABLE `account_business_transfer` (
  `id` bigint NOT NULL,
  `business_coin_type` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账变业务类型 0游戏 1财务 2红利 3佣金 4转账 5管理 ',
  `coin_type` varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账变类型',
  `source_account_type_from` char(2) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '从来源账号类型 0会员、1代理、2平台、3三方支付、4三方游戏',
  `wallet_type_from` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '从钱包类型  0现金账户、1冻结账户、2平台币账户、3红利账户、4场馆账户、5额度账户、6佣金账户,7盈亏账户、8贷记账户\n',
  `source_account_type_to` char(2) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '到来源账号类型0会员、1代理、2平台、3三方支付、4三方游戏',
  `wallet_type_to` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '到钱包类型  0现金账户、1冻结账户、2平台币账户、3红利账户、4场馆账户、5额度账户、6佣金账户,7盈亏账户、8贷记账户',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='业务类型帐变类型业务流转表';

DROP TABLE IF EXISTS `account_coin`;
CREATE TABLE `account_coin` (
  `id` bigint NOT NULL,
  `account_no` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账户编号',
  `account_name` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账号名称 用户名称，代理名称，三方充值渠道code，三方游戏code\n',
  `source_account_type` char(2) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '来源账号类型  0会员、1代理、2平台、3三方支付、4三方游戏',
  `source_account_no` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '来源用户编号 userId、agentId等，三方充值渠道code，三方游戏code\n',
  `account_category` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '账户归属分类: 0现金账户、1冻结账户、2平台币账户、3红利账户、4场馆账户、5额度账户、6佣金账户,7盈亏账户',
  `currency_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
  `account_type` char(1) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账户类型 0:借记账户、1:贷记账户',
  `balance_amount` decimal(22,4) DEFAULT NULL COMMENT '账户余额',
  `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `account_status` char(1) COLLATE utf8mb4_0900_bin DEFAULT '1' COMMENT '1:启用 0:禁用',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `uk_account_no` (`account_no`) COMMENT '账户编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='账户信息表';

DROP TABLE IF EXISTS `account_coin_record`;
CREATE TABLE `account_coin_record` (
  `id` bigint NOT NULL,
  `account_no` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账户编号',
  `order_no` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '帐变流水订单号',
  `inner_order_no` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '关联内部订单号',
  `third_order_no` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '三方关联订单号',
  `coin_value` decimal(22,4) DEFAULT NULL COMMENT '金额改变数量',
  `coin_from` decimal(22,4) DEFAULT NULL COMMENT '账变前金额',
  `coin_to` decimal(22,4) DEFAULT NULL COMMENT '账变后金额',
  `currency_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '币种',
  `balance_type` char(1) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '收支类型 1+收入, 1-支出',
  `business_coin_type` varchar(6) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账变业务类型',
  `coin_type` varchar(6) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账变类型',
  `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点code',
  `venue_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '游戏场馆CODE',
  `account_name` varchar(128) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '账号名称 用户名称，代理名称，三方充值渠道code',
  `source_account_no` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '来源用户编号 userId、agentId等，三方充值渠道code',
  `source_account_type` char(2) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '来源账号类型  0会员、1代理、2平台、3三方支付、4三方游戏',
  `account_category` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '账户归属分类: 0现金账户、1冻结账户、2平台币账户、3红利账户、4场馆账户、5额度账户、6佣金账户',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
  KEY `idx_bc_ct_ion_ton_an` (`business_coin_type`,`coin_type`,`inner_order_no`,`third_order_no`,`account_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='账户交易流水表';
