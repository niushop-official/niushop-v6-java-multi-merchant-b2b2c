ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `extend_field_1` varchar(255) NOT NULL DEFAULT '' COMMENT '扩展字段1';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `extend_field_2` varchar(255) NOT NULL DEFAULT '' COMMENT '扩展字段2';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `extend_data` text NULL COMMENT '扩展数据';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `settlement_timer` int NOT NULL DEFAULT 0 COMMENT '预计结算时间';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `perk_status` int NOT NULL DEFAULT 1 COMMENT '补贴状态';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `perk_time` int NOT NULL DEFAULT 0 COMMENT '补贴时间';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `evaluate_timer` int NOT NULL DEFAULT 0 COMMENT '自动好评时间，评价后重置为0';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `weapp_version` varchar(255) NOT NULL DEFAULT '' COMMENT '小程序类型，developer为开发版；trial为体验版；formal为正式版；默认为正式版';


ALTER TABLE `{{prefix}}mall_order_refund` ADD COLUMN `weapp_version` varchar(255) NOT NULL DEFAULT '' COMMENT '小程序类型，developer为开发版；trial为体验版；formal为正式版；默认为正式版';