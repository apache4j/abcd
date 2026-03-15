CREATE TABLE `bet_coin_join` (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `venue_code` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '场馆',
                                 `order_id` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '三方-订单号扣款的唯一ID',
                                 `trans_id` varchar(50) COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '我方扣费-订单唯一ID',
                                 `bet_id` varchar(50) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '下注订单号',
                                 `user_account` varchar(50) COLLATE utf8mb4_0900_bin NOT NULL COMMENT '用户账号',
                                 `amount` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
                                 `created_time` bigint DEFAULT NULL COMMENT '创建时间',
                                 `updated_time` bigint DEFAULT NULL COMMENT '更新时间',
                                 `remark` varchar(1024) COLLATE utf8mb4_0900_bin DEFAULT NULL COMMENT '备注',
                                 `creator` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                 `updater` varchar(50) COLLATE utf8mb4_0900_bin DEFAULT NULL,
                                 PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                 UNIQUE KEY `uk_order_id` (`order_id`),
                                 UNIQUE KEY `uk_trans_id` (`trans_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='扣费_下注关联关系表';

ALTER TABLE site_recharge_way ADD `sort_order` int DEFAULT '1' COMMENT '排序' AFTER   `status` ;
ALTER TABLE site_withdraw_way ADD `sort_order` int DEFAULT '1' COMMENT '排序' AFTER   `status` ;


ALTER TABLE site_recharge_channel ADD `sort_order` int DEFAULT '1' COMMENT '排序' AFTER   `status` ;
ALTER TABLE site_withdraw_channel ADD `sort_order` int DEFAULT '1' COMMENT '排序' AFTER   `status` ;


