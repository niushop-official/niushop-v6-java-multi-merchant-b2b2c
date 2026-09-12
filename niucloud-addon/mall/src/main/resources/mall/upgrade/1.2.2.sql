ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `vip_card_discount` varchar(255) NOT NULL DEFAULT '' COMMENT '付费会员卡折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price';

ALTER TABLE `{{prefix}}mall_goods_sku` ADD COLUMN `svip_price` text NULL COMMENT '付费会员价，json格式，数据结构为：{"card_1":"10.00","card_2":"10.00"}';

ALTER TABLE `{{prefix}}mall_goods_sku` ADD COLUMN `svip_discount` text NULL COMMENT '付费会员折扣，json格式，数据结构为：{"card_1":"8","card_2":"5"}';

ALTER TABLE `{{prefix}}mall_goods_rank` ADD COLUMN `is_show_search` tinyint NOT NULL DEFAULT 0 COMMENT '是否显示到搜索页';

ALTER TABLE `{{prefix}}mall_goods_rank` ADD COLUMN `config` text NULL COMMENT '榜单设置';
