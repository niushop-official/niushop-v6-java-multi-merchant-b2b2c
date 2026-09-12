DROP TABLE IF EXISTS `{{prefix}}site_waimai`;
CREATE TABLE `{{prefix}}site_waimai` (
                                         `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `site_id` int NOT NULL DEFAULT '0' COMMENT '系统站点ID（关联{{prefix}}site）',
                                         `business_type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '主体类型 1个人 2个体 3企业',
                                         `business_status` tinyint NOT NULL DEFAULT '1' COMMENT '营业状态（0已打烊，1营业中）',
                                         `service_rate` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '佣金比率',
                                         `id_card_name` varchar(50) NOT NULL DEFAULT '' COMMENT '法人姓名',
                                         `id_card_front` varchar(255) NOT NULL DEFAULT '' COMMENT '身份证正面',
                                         `id_card_back` varchar(255) NOT NULL DEFAULT '' COMMENT '身份证反面',
                                         `create_time` int NOT NULL DEFAULT '0',
                                         `update_time` int NOT NULL DEFAULT '0',
                                         `category_id` int NOT NULL DEFAULT '0' COMMENT '主营一级行业ID',
                                         `child_category_id` varchar(300) NOT NULL DEFAULT '0' COMMENT '主营二级行业ID',
                                         `site_score` decimal(3,2) NOT NULL DEFAULT '5.00' COMMENT '店铺评分（0.00~5.00）',
                                         `per_capita` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '人均消费',
                                         `facility_service` varchar(500) NOT NULL DEFAULT '' COMMENT '服务设施，逗号分隔',
                                         `keywords` varchar(3000) NOT NULL DEFAULT '' COMMENT '店铺关键词  用于搜出',
                                         `evaluate_num` int NOT NULL DEFAULT '0' COMMENT '评价次数',
                                         `store_photo` varchar(3000) NOT NULL DEFAULT '' COMMENT '门头照   多张，隔开',
                                         `sale_num` int NOT NULL DEFAULT '0' COMMENT '销量',
                                         `circle_id` int NOT NULL DEFAULT '0' COMMENT '商圈id',
                                         `shop_banner` text COMMENT '店铺轮播图',
                                         `food_business_license` varchar(2000) NOT NULL DEFAULT '' COMMENT '食品经营许可证',
                                         `other_info` text COMMENT '其它资料',
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='外卖站点业务表';

DROP TABLE IF EXISTS `{{prefix}}site_front_warehouse`;
CREATE TABLE `{{prefix}}site_front_warehouse` (
                                                  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                  `site_id` int NOT NULL DEFAULT '0' COMMENT '系统站点ID（关联{{prefix}}site）',
                                                  `business_type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '主体类型 1个人 2个体 3企业',
                                                  `business_status` tinyint NOT NULL DEFAULT '1' COMMENT '营业状态（0已打烊，1营业中）',
                                                  `service_rate` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '佣金比率',
                                                  `id_card_name` varchar(50) NOT NULL DEFAULT '' COMMENT '法人姓名',
                                                  `id_card_front` varchar(255) NOT NULL DEFAULT '' COMMENT '身份证正面',
                                                  `id_card_back` varchar(255) NOT NULL DEFAULT '' COMMENT '身份证反面',
                                                  `create_time` int NOT NULL DEFAULT '0',
                                                  `update_time` int NOT NULL DEFAULT '0',
                                                  `category_id` int NOT NULL DEFAULT '0' COMMENT '主营一级行业ID',
                                                  `child_category_id` varchar(300) NOT NULL DEFAULT '0' COMMENT '主营二级行业ID',
                                                  `site_score` decimal(3,2) NOT NULL DEFAULT '5.00' COMMENT '店铺评分（0.00~5.00）',
                                                  `keywords` varchar(3000) NOT NULL DEFAULT '' COMMENT '店铺关键词  用于搜出',
                                                  `evaluate_num` int NOT NULL DEFAULT '0' COMMENT '评价次数',
                                                  `store_photo` varchar(3000) NOT NULL DEFAULT '' COMMENT '门头照   多张，隔开',
                                                  `sale_num` int NOT NULL DEFAULT '0' COMMENT '销量',
                                                  `circle_id` int NOT NULL DEFAULT '0' COMMENT '商圈id',
                                                  `goods_up_num` int NOT NULL DEFAULT '0' COMMENT '商品上架数据量',
                                                  `quality_score` decimal(10,2) DEFAULT NULL,
                                                  `service_score` decimal(10,2) DEFAULT NULL,
                                                  `logistics_score` decimal(10,2) DEFAULT NULL,
                                                  `packaging_score` decimal(10,2) DEFAULT NULL,
                                                  PRIMARY KEY (`id`),
                                                  UNIQUE KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='前置仓站点业务表';

DROP TABLE IF EXISTS `{{prefix}}goods_label`;
CREATE TABLE `{{prefix}}goods_label` (
                                         `label_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '标签ID',
                                         `site_id` int NOT NULL DEFAULT '0' COMMENT '站点id',
                                         `label_name` varchar(255) NOT NULL DEFAULT '' COMMENT '标签名称',
                                         `group_id` int NOT NULL DEFAULT '0' COMMENT '标签分组id',
                                         `style_type` varchar(255) NOT NULL DEFAULT '' COMMENT '效果设置，diy：自定义，icon：图片',
                                         `color_json` varchar(255) NOT NULL DEFAULT '' COMMENT '自定义颜色（文字、背景、边框），json格式',
                                         `icon` varchar(255) NOT NULL DEFAULT '' COMMENT '图标',
                                         `status` int NOT NULL DEFAULT '0' COMMENT '状态，1：启用，0：关闭',
                                         `memo` varchar(255) NOT NULL DEFAULT '' COMMENT '标签说明',
                                         `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
                                         `create_time` int NOT NULL DEFAULT '0' COMMENT '创建时间',
                                         `update_time` int NOT NULL DEFAULT '0' COMMENT '更新时间',
                                         PRIMARY KEY (`label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品标签表';

DROP TABLE IF EXISTS `{{prefix}}goods_badge`;
CREATE TABLE `{{prefix}}goods_badge` (
                                         `badge_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '角标ID',
                                         `site_id` int NOT NULL DEFAULT '0' COMMENT '站点id',
                                         `badge_name` varchar(255) NOT NULL DEFAULT '' COMMENT '角标名称',
                                         `group_id` int NOT NULL DEFAULT '0' COMMENT '标签分组id',
                                         `style_type` varchar(255) NOT NULL DEFAULT '' COMMENT '效果设置，diy：自定义，icon：图片',
                                         `color_json` varchar(255) NOT NULL DEFAULT '' COMMENT '自定义颜色（文字、背景、边框），json格式',
                                         `icon` varchar(255) NOT NULL DEFAULT '' COMMENT '图标',
                                         `status` int NOT NULL DEFAULT '0' COMMENT '状态，1：启用，0：关闭',
                                         `memo` varchar(255) NOT NULL DEFAULT '' COMMENT '角标说明',
                                         `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
                                         `create_time` int NOT NULL DEFAULT '0' COMMENT '创建时间',
                                         `update_time` int NOT NULL DEFAULT '0' COMMENT '更新时间',
                                         PRIMARY KEY (`badge_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品角标表';

DROP TABLE IF EXISTS `{{prefix}}newcomer_gift_records`;
CREATE TABLE `{{prefix}}newcomer_gift_records` (
                                                   `record_id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                                   `member_id` int NOT NULL DEFAULT '0' COMMENT '会员id',
                                                   `gifts` varchar(255) NOT NULL DEFAULT '' COMMENT '礼包内容',
                                                   `create_time` int NOT NULL DEFAULT '0' COMMENT '参与时间',
                                                   PRIMARY KEY (`record_id`),
                                                   KEY `idx_member_id` (`member_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='新人礼发放记录表';



ALTER TABLE `{{prefix}}delivery_local_delivery` ADD COLUMN `free_goods_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '满多少元免配送费';

ALTER TABLE `{{prefix}}goods_brand` ADD COLUMN `addon` varchar(255) NOT NULL DEFAULT '' COMMENT '所属应用';

ALTER TABLE `{{prefix}}goods_category` ADD COLUMN `addon` varchar(255) NOT NULL DEFAULT '' COMMENT '所属应用';

ALTER TABLE `{{prefix}}member` ADD COLUMN `is_newcomer_gift` tinyint NOT NULL DEFAULT 0 COMMENT '是否领取新人礼';

ALTER TABLE `{{prefix}}member_address` ADD COLUMN `phone` varchar(255) NOT NULL DEFAULT '' COMMENT '固定电话';

ALTER TABLE `{{prefix}}invoice` MODIFY COLUMN `trade_type` varchar(10) NOT NULL DEFAULT '' COMMENT '业务类型';