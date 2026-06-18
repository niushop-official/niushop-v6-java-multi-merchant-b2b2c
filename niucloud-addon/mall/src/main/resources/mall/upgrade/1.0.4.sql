CREATE TABLE `{{prefix}}mall_full_gift`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '店铺id',
    `title` varchar(100) NOT NULL DEFAULT '' COMMENT '活动名称',
    `member_type` varchar(50) NOT NULL DEFAULT '' COMMENT '会员规则',
    `member_type_json` text NULL COMMENT '会员规则内容',
    `start_time` int NOT NULL DEFAULT 0 COMMENT '活动开始时间',
    `end_time` int NOT NULL DEFAULT 0 COMMENT '活动结束时间',
    `status` varchar(20) NOT NULL DEFAULT '' COMMENT '活动状态：wait=未开始；process=进行中；end=已结束；close=已关闭',
    `goods_type` varchar(50) NOT NULL DEFAULT '' COMMENT '适用范围类型：all=全部商品；selected=指定SKU；selected_not=排除指定SKU',
    `rule_type` varchar(20) NULL DEFAULT NULL COMMENT '规则类型  ladder-阶梯  cycle-循环',
    `rule_json` text NOT NULL COMMENT '满减规则配置（JSON格式，存储阶梯/循环优惠规则）',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序权重（数字越小越靠前）',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '更新时间',
    `creator` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人（管理员账号）',
    `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '活动备注',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_status`(`status` ASC) USING BTREE,
    INDEX `idx_time`(`start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB COMMENT = '满赠活动主表' ROW_FORMAT = Dynamic;

CREATE TABLE `{{prefix}}mall_full_gift_goods`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '店铺id',
    `full_gift_id` int NOT NULL DEFAULT 0 COMMENT '活动id',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
    `sku_id` int NOT NULL DEFAULT 0 COMMENT '规格id',
    `goods_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动类型',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动状态',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `{{prefix}}mall_full_reduction`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '店铺id',
    `title` varchar(100) NOT NULL DEFAULT '' COMMENT '活动名称',
    `type` varchar(20) NOT NULL DEFAULT '' COMMENT '活动类型：amount=满金额减/折；num=满件数减/折',
    `promotion_type` varchar(20) NULL DEFAULT NULL COMMENT 'discount 折扣  /   reduction 减价',
    `member_type` varchar(50) NOT NULL DEFAULT '' COMMENT '会员规则',
    `member_type_json` text NULL COMMENT '会员规则内容',
    `start_time` int NOT NULL DEFAULT 0 COMMENT '活动开始时间',
    `end_time` int NOT NULL DEFAULT 0 COMMENT '活动结束时间',
    `status` varchar(20) NOT NULL DEFAULT '' COMMENT '活动状态：wait=未开始；process=进行中；end=已结束；close=已关闭',
    `goods_type` varchar(50) NOT NULL DEFAULT '' COMMENT '适用范围类型：all=全部商品；selected=指定SKU；selected_not=排除指定SKU',
    `rule_type` varchar(20) NULL DEFAULT '' COMMENT '规则类型  ladder-阶梯  cycle-循环',
    `rule_json` text NOT NULL COMMENT '满减规则配置（JSON格式，存储阶梯/循环优惠规则）',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序权重（数字越小越靠前）',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '更新时间',
    `creator` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人（管理员账号）',
    `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '活动备注',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_status`(`status` ASC) USING BTREE,
    INDEX `idx_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
    INDEX `idx_activity_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB COMMENT = '满减活动主表（含规则/SKU关联）' ROW_FORMAT = Dynamic;

CREATE TABLE `{{prefix}}mall_full_reduction_goods`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '店铺id',
    `full_reduction_id` int NOT NULL DEFAULT 0 COMMENT '活动id',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
    `sku_id` int NOT NULL DEFAULT 0 COMMENT '规格id',
    `goods_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动类型',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动状态',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `{{prefix}}mall_goods_import_record`  (
    `record_id` int NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL DEFAULT 0,
    `main_id` int NOT NULL DEFAULT 0 COMMENT '操作人id',
    `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件路径',
    `goods_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品类型  real.实物商品 virtual.虚拟商品',
    `total_num` int NOT NULL DEFAULT 0 COMMENT '总数',
    `success_num` int NOT NULL DEFAULT 0 COMMENT '成功总数',
    `fail_num` int NOT NULL DEFAULT 0 COMMENT '失败总数',
    `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0.待处理 1.处理中 2.处理完成 3.处理失败',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '修改时间',
    `output` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对外输出记录',
    `fail_output` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '失败记录',
    `fail_remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '失败原因',
    `duration` int NOT NULL DEFAULT 0 COMMENT '耗时',
    PRIMARY KEY (`record_id`) USING BTREE,
    INDEX `IDX_mgir_status`(`status` ASC) USING BTREE,
    INDEX `IDX_mgir_site_id`(`site_id` ASC) USING BTREE,
    INDEX `IDX_mgir_main_id`(`main_id` ASC) USING BTREE,
    INDEX `IDX_mgir_goods_type`(`goods_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品导入记录总表' ROW_FORMAT = Dynamic;

CREATE TABLE `{{prefix}}mall_goods_import_rows`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `record_id` int NOT NULL DEFAULT 0,
    `goods_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `sku_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `goods_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品类型  real.实物商品 virtual.虚拟商品',
    `data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
    `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0.待处理 1.处理中 2.处理成功 3.处理失败',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '修改时间',
    `relate_goods_id` int NOT NULL DEFAULT 0 COMMENT '关联goods_id mall_goods_sku.goods_id',
    `relate_sku_id` int NOT NULL DEFAULT 0 COMMENT '关联sku_id mall_goods_sku.sku_id',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `IDX_mgirs_record_id`(`record_id` ASC) USING BTREE,
    INDEX `IDX_mgirs_status`(`status` ASC) USING BTREE,
    INDEX `IDX_mgirs_goods_id`(`goods_id` ASC) USING BTREE,
    INDEX `IDX_mgirs_sku_id`(`sku_id` ASC) USING BTREE,
    INDEX `IDX_mgirs_relate_goods_id`(`relate_goods_id` ASC) USING BTREE,
    INDEX `IDX_mgirs_relate_sku_id`(`relate_sku_id` ASC) USING BTREE,
    INDEX `IDX_mgirs_goods_type`(`goods_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品导入记录行表' ROW_FORMAT = Dynamic;

CREATE TABLE `{{prefix}}mall_order_gift_goods`  (
    `order_gift_id` int NOT NULL AUTO_INCREMENT COMMENT '  ',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
    `member_id` int NOT NULL DEFAULT 0 COMMENT '购买会员id',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
    `sku_id` int NOT NULL DEFAULT 0 COMMENT '商品规格id',
    `goods_name` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品名称',
    `sku_name` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品规格名称',
    `goods_image` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品图片',
    `sku_image` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT ''  COMMENT 'sku规格图片',
    `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品单价',
    `num` int NOT NULL DEFAULT 0 COMMENT '购买数量',
    `goods_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品总价',
    `goods_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品类型',
    `original_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品原价',
    `supply_goods_id` int NOT NULL DEFAULT 0 COMMENT '供货商商品id',
    `supply_id` int NOT NULL DEFAULT 0 COMMENT '供货商id',
    `supply_sku_id` int NOT NULL DEFAULT 0 COMMENT '供货商商品sku_id',
    `cost_goods_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品成本总额相关',
    PRIMARY KEY (`order_gift_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单项表' ROW_FORMAT = Dynamic;



ALTER TABLE `{{prefix}}mall_goods` MODIFY COLUMN `status` tinyint NOT NULL DEFAULT 1 COMMENT '商品状态，1上架 0下架 -3强制下架';

ALTER TABLE `{{prefix}}mall_goods` MODIFY COLUMN `audit_status` int NOT NULL DEFAULT 0 COMMENT '审核状态，1待审核 2审核通过 -1审核未通过';

ALTER TABLE `{{prefix}}mall_goods` MODIFY COLUMN `audit_reason` varchar(3000) NOT NULL DEFAULT '' COMMENT '审核原因';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `timer_on_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '自动上架开关，1.开启，0.关闭';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `timer_on_time` int NOT NULL DEFAULT 0 COMMENT '自动上架时间';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `timer_off_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '自动下架开关，1.开启，0.关闭';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `timer_off_time` int(10) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '自动下架时间';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `support_pay_type` varchar(255) NOT NULL DEFAULT '' COMMENT '支持的支付方式';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `is_enable_refund` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许退款';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `point_deduct_type` varchar(20) NOT NULL DEFAULT 'site' COMMENT '积分抵扣类型 custom：自定义 site:继承店铺';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `point_deduct_rate` float(10, 2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣比例';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `source_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '商品来源 0.本地添加 1.导入商品 2.采集商品';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `source_goods_id` varchar(255) NOT NULL DEFAULT '' COMMENT '导入/采集商品关联goods_id/spu_id';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `source_flatform` varchar(255) NOT NULL DEFAULT '' COMMENT '采集商品平台标识  例如:99api等等';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `source_party` varchar(255) NOT NULL DEFAULT '' COMMENT '采集商品第三方标识  例如：taobao、tmall、jd等等';

ALTER TABLE `{{prefix}}mall_goods` ADD INDEX `IDX_goods_source_type`(`source_type` ASC) USING BTREE;

ALTER TABLE `{{prefix}}mall_goods` ADD INDEX `IDX_goods_source_goods_id`(`source_goods_id` ASC) USING BTREE;



ALTER TABLE `{{prefix}}mall_goods_sku` ADD COLUMN `source_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '商品来源 0.本地添加 1.导入商品 2.采集商品';

ALTER TABLE `{{prefix}}mall_goods_sku` ADD COLUMN `source_goods_id` varchar(255) NOT NULL DEFAULT '' COMMENT '导入/采集商品关联goods_id/spu_id';

ALTER TABLE `{{prefix}}mall_goods_sku` ADD COLUMN `source_sku_id` varchar(255) NOT NULL DEFAULT '' COMMENT '导入/采集商品关联sku_id';

ALTER TABLE `{{prefix}}mall_goods_sku` ADD COLUMN `source_flatform` varchar(255) NOT NULL DEFAULT '' COMMENT '采集商品平台标识  例如:99api等等';

ALTER TABLE `{{prefix}}mall_goods_sku` ADD COLUMN `source_party` varchar(255) NOT NULL DEFAULT ''  COMMENT '采集商品第三方标识  例如：taobao、tmall、jd等等';

ALTER TABLE `{{prefix}}mall_goods_sku` ADD INDEX `IDX_goods_source_type`(`source_type` ASC) USING BTREE;

ALTER TABLE `{{prefix}}mall_goods_sku` ADD INDEX `IDx_goods_source_goods_id`(`source_goods_id` ASC) USING BTREE;

ALTER TABLE `{{prefix}}mall_goods_sku` ADD INDEX `IDx_goods_source_sku_id`(`source_sku_id` ASC) USING BTREE;



ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `use_point` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '使用积分数';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `point_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `refund_point_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '退款返还积分抵扣金额';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `refund_deduct_point` decimal(10, 2) NOT NULL DEFAULT 0 COMMENT '退款返还抵扣积分';

ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `full_reduction_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '参与满减活动扣减金额';




ALTER TABLE `{{prefix}}mall_order_discount_goods` ADD COLUMN `discount_type` varchar(20) NOT NULL DEFAULT '' COMMENT '详细优惠类型';

ALTER TABLE `{{prefix}}mall_order_discount_goods` ADD COLUMN `title` varchar(255) NOT NULL DEFAULT '' COMMENT '优惠标题';

ALTER TABLE `{{prefix}}mall_order_discount_goods` ADD COLUMN `content` varchar(255) NOT NULL DEFAULT '' COMMENT '优惠描述';

ALTER TABLE `{{prefix}}mall_order_discount_goods` ADD COLUMN `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间';

ALTER TABLE `{{prefix}}mall_order_discount_goods` MODIFY COLUMN `order_discount_id` int NOT NULL DEFAULT 0 COMMENT '订单参与优惠id';


ALTER TABLE `{{prefix}}mall_order_goods` ADD COLUMN `is_gift` tinyint NOT NULL DEFAULT 0 COMMENT '是否是赠品';

ALTER TABLE `{{prefix}}mall_order_goods` ADD COLUMN `use_point` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '使用积分数';

ALTER TABLE `{{prefix}}mall_order_goods` ADD COLUMN `point_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额';

ALTER TABLE `{{prefix}}mall_order_goods` ADD COLUMN `full_reduction_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '满减活动扣减金额';



ALTER TABLE `{{prefix}}mall_order_group` ADD COLUMN `use_point` int NOT NULL DEFAULT 0 COMMENT '使用积分数';

ALTER TABLE `{{prefix}}mall_order_group` ADD COLUMN `point_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额';

CREATE TABLE `{{prefix}}mall_order_delivery_content`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `trade_id` int NOT NULL DEFAULT 0 COMMENT '业务id',
  `trade_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务类型',
  `trade_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务编号',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '发货内容或核销码',
  `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单虚拟发货内容表' ROW_FORMAT = Dynamic;


ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `virtual_delivery_type` varchar(255) NOT NULL DEFAULT 'auto' COMMENT '虚拟商品发货方式，auto：自动发货，manual：手动发货，verify：到店核销';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `virtual_delivery_content` text NULL COMMENT '虚拟商品发货内容';

ALTER TABLE `{{prefix}}mall_goods` MODIFY COLUMN `virtual_receive_type` varchar(255) NOT NULL DEFAULT 'artificial' COMMENT '虚拟商品收货方式(自动发货后)，auto：自动收货，artificial：买家确认收货';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `virtual_refund` tinyint NOT NULL DEFAULT 0 COMMENT '虚拟商品退款，0：不允许退款，1：付款成功后指定天数内有效，2：过期自动退款（核销有效期为1、2时生效）';

ALTER TABLE `{{prefix}}mall_goods` ADD COLUMN `virtual_refund_day` int NOT NULL DEFAULT 0 COMMENT '付款成功后几天内可退款，为0不限制';


ALTER TABLE `{{prefix}}mall_order` ADD COLUMN `sub_delivery_type` varchar(255) NOT NULL DEFAULT '' COMMENT '发货方式';

ALTER TABLE `{{prefix}}mall_order` MODIFY COLUMN `use_point` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '抵扣积分';

ALTER TABLE `{{prefix}}mall_order` MODIFY COLUMN `refund_deduct_point` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '退款返还抵扣积分';


ALTER TABLE `{{prefix}}mall_order_goods` ADD COLUMN `goods_config` text NULL COMMENT '商品对应配置';

ALTER TABLE `{{prefix}}mall_order_goods` ADD COLUMN `virtual_refund_type` tinyint NOT NULL DEFAULT 0 COMMENT '虚拟商品退款类型（0不允许退款 1主动退 2自动退）';


ALTER TABLE `{{prefix}}mall_order_group` ADD COLUMN `full_reduction_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '满减活动扣减金额';
