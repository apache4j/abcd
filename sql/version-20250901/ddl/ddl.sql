CREATE TABLE `site_activity_template` (
  `id` bigint NOT NULL COMMENT '主键id',
  `activity_template` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动模版编号',
  `activity_name` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动名称',
  `site_code` varchar(10) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点编码',
  `bind_status` int DEFAULT NULL COMMENT '绑定状态 1绑定,0解绑',
  `created_time` bigint DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `unique_sc_ac` (`site_code`,`activity_template`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='站点活动模版表绑定关系表';

CREATE TABLE `system_activity_template` (
  `id` bigint NOT NULL COMMENT '主键id',
  `activity_template` varchar(20) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动模版编码',
  `activity_name` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '活动名称',
  `created_time` bigint DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `unique_activity_no` (`activity_template`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin COMMENT='活动模版表';

ALTER TABLE user_coin_record ADD COLUMN `desc_info` varchar(500) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '描述信息';
ALTER TABLE user_coin_record MODIFY COLUMN `order_no` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '关联订单号';

ALTER TABLE user_deposit_withdrawal ADD COLUMN `ifsc_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'IFSC码(印度)' AFTER country;
ALTER TABLE agent_deposit_withdrawal ADD COLUMN `ifsc_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'IFSC码(印度)' AFTER country;

ALTER TABLE `site_activity_free_wheel`
    ADD COLUMN `venue_code` varchar(64) NULL COMMENT '平台编号' ,
ADD COLUMN `access_parameters` varchar(20) NULL COMMENT '游戏id',
ADD COLUMN `bet_limit_amount` decimal(22, 4) NULL COMMENT '投注限额' ;

  ALTER TABLE `site_activity_assign_day`
      ADD COLUMN `venue_code` varchar(64) NULL COMMENT '平台编号' ,
  ADD COLUMN `access_parameters` varchar(20) NULL COMMENT '游戏id',
  ADD COLUMN `bet_limit_amount` decimal(22, 4) NULL COMMENT '投注限额' ;

ALTER TABLE `site_activity_free_game_consume`
    ADD COLUMN `currency_code` varchar(20) NULL COMMENT '币种' ;

ALTER TABLE user_withdrawal_manual_record ADD COLUMN `ifsc_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'IFSC码(印度)' AFTER country;
ALTER TABLE agent_withdrawal_manual_record ADD COLUMN `ifsc_code` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'IFSC码(印度)' AFTER country;





ALTER TABLE `agent_review`
MODIFY COLUMN `parent_id` varchar(10) NULL DEFAULT NULL COMMENT '父节点 存上级agentId' AFTER `id`;

	CREATE TABLE `agent_info_relation` (
  `id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '主键id',
  `site_code` varchar(20) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '站点code',
  `ancestor_agent_id` VARCHAR(10) NOT NULL COMMENT '祖先代理ID（上级代理，最顶层可以是自己）',
  `descendant_agent_id` VARCHAR(10) NOT NULL COMMENT '子孙代理ID（下级代理，最底层可以是自己）',
  `agent_depth` INT NOT NULL COMMENT '层级深度：0 表示自己，1 表示直接父子关系，2 表示隔一层的祖孙，以此类推',
  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '创建人',
  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '修改人',
  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) COMMENT '主键ID',
  UNIQUE KEY `idx_ancestor_descendant` (`ancestor_agent_id`, `descendant_agent_id`) COMMENT '保证每对祖先-子孙关系唯一',
  KEY `idx_descendant` (`descendant_agent_id`) COMMENT '子孙代理ID索引：加速查询某个代理的所有上级'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='代理关系闭包表：存储所有代理的祖先-子孙层级关系';




ALTER TABLE venue_info ADD  `venue_join_type` int DEFAULT NULL COMMENT '1:多游戏,2:单场馆,3:游戏' AFTER venue_type;



ALTER TABLE venue_info ADD `venue_desc` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '场馆描述' AFTER `currency_code`;


ALTER TABLE `venue_info`  MODIFY COLUMN `venue_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '游戏场馆名称' AFTER `venue_platform_name`;

CREATE TABLE `game_one_venue` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                  `site_code` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '站点CODE',
                                  `game_one_id` bigint NOT NULL COMMENT 'game_one_class.id',
                                  `currency_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '货币代码',
                                  `venue_code` varchar(64) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '场馆',
                                  `sort` int DEFAULT '1' COMMENT '排序',
                                  `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                  `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                  `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                  `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                  UNIQUE KEY `pk_game_one_id_currency_code_venue_code` (`game_one_id`,`currency_code`,`venue_code`) COMMENT '唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin  COMMENT='一级分类,场馆配置';



ALTER TABLE venue_info ADD `pc_background_code`  varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'pc_场馆背景图' AFTER `h5_venue_icon`;
ALTER TABLE venue_info ADD `pc_logo_code`  varchar(255) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT 'pc_场馆LOGO' AFTER `pc_background_code`;


ALTER TABLE `site_activity_free_game_record`
    ADD COLUMN `wash_ratio` decimal(8,2) DEFAULT NULL COMMENT '洗码倍率' ;




ALTER TABLE user_manual_up_down_record
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;

ALTER TABLE agent_transfer_record
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;

ALTER TABLE agent_transfer_record
    MODIFY COLUMN transfer_agent_id varchar(20) NULL DEFAULT NULL COMMENT '转账账号id' ;

ALTER TABLE agent_withdrawal_manual_record
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;

ALTER TABLE report_agent_recharge_withdraw
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;

ALTER TABLE report_user_recharge_withdraw
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;

ALTER TABLE user_coin_record
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;

ALTER TABLE user_deposit_withdrawal
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;

ALTER TABLE user_manual_up_down_record
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;

ALTER TABLE user_platform_coin_manual_up_down_record
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;

ALTER TABLE user_platform_coin_record
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;

ALTER TABLE user_withdrawal_manual_record
    MODIFY COLUMN agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;


ALTER TABLE user_review
    MODIFY COLUMN super_agent_id varchar(20) NULL DEFAULT NULL COMMENT '代理id' ;


ALTER TABLE hot_wallet_address
    MODIFY COLUMN `user_id` varchar(20)DEFAULT NULL COMMENT '会员/代理id';

ALTER TABLE site_security_change_log
    MODIFY COLUMN `user_id` varchar(20)DEFAULT NULL COMMENT '会员/代理id';


ALTER TABLE order_record ADD  `transaction_id` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '转账ID' AFTER venue_code;
ALTER TABLE order_record ADD  `ex_id1` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备用' AFTER transaction_id;
ALTER TABLE order_record ADD  `ex_id2` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备用' AFTER ex_id1;

ALTER TABLE user_coin_record ADD  `ex_id1` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备用1' AFTER desc_info;
ALTER TABLE user_coin_record ADD  `ex_id2` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备用2' AFTER ex_id1;
ALTER TABLE user_coin_record ADD  `ex_id3` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备用3' AFTER ex_id2;


ALTER TABLE user_activity_typing_amount_record ADD  `ex_id1` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备用1' AFTER creator;
ALTER TABLE user_activity_typing_amount_record ADD  `ex_id2` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备用2' AFTER ex_id1;
ALTER TABLE user_activity_typing_amount_record ADD  `ex_id3` varchar(100) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备用3' AFTER ex_id2;
