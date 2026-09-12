ALTER TABLE `{{prefix}}mall_coupon` ADD COLUMN `addon` varchar(255) NOT NULL DEFAULT '' COMMENT '所属应用';

ALTER TABLE `{{prefix}}mall_coupon_member` ADD COLUMN `trade_type` varchar(255) NOT NULL DEFAULT '' COMMENT '业务类型';

ALTER TABLE `{{prefix}}mall_full_reduction`
    ADD COLUMN `addon` varchar(50) NOT NULL DEFAULT '' COMMENT '业务应用标识';

ALTER TABLE `{{prefix}}mall_full_reduction_goods`
    ADD COLUMN `trade_type` varchar(50) NOT NULL DEFAULT '' COMMENT '商品交易类型';