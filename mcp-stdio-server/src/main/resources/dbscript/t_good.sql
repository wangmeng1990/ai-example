CREATE TABLE `t_good` (
      `id` varchar(50) NOT NULL,
      `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品编码',
      `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '商品名称',
      `price` decimal(10,2) NOT NULL,
      `description` text,
      `category` varchar(50) DEFAULT NULL,
      `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      `edited_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
      `creator_id` varchar(50) DEFAULT NULL COMMENT '创建人',
      `editor_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '最后修改人',
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品信息';