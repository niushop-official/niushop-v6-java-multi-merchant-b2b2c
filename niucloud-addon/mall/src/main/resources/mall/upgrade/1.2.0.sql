ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `recommend_goods_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '关联推荐商品id集合';
ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `circle_id` int NOT NULL DEFAULT 0 COMMENT '商圈id';
ALTER TABLE `{{prefix}}mall_order_refund` ADD COLUMN `circle_id` int NOT NULL DEFAULT 0 COMMENT '商圈id';