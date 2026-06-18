DROP TABLE IF EXISTS `{{prefix}}mall_active`;
CREATE TABLE `{{prefix}}mall_active`  (
    `active_id` int NOT NULL AUTO_INCREMENT COMMENT '活动id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `active_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动名称',
    `active_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '活动说明',
    `active_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动类型(店铺活动，会员活动，商品活动)',
    `active_goods_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品活动类型（单品，独立商品，店铺整体商品）',
    `active_goods_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '参与活动商品信息',
    `active_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动类别',
    `active_class_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动类别子分类（活动管理）',
    `relate_member` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '参与会员条件(默认全部)',
    `active_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '活动扩展信息数据',
    `start_time` int NOT NULL DEFAULT 0 COMMENT '活动开始时间',
    `end_time` int NOT NULL DEFAULT 0 COMMENT '活动结束时间',
    `active_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动状态',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '添加时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '修改时间',
    `active_order_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '活动累计金额',
    `active_order_num` int NOT NULL DEFAULT 0 COMMENT '活动累计订单数',
    `active_member_num` int NOT NULL DEFAULT 0 COMMENT '活动参与会员数',
    `active_success_num` int NOT NULL DEFAULT 0 COMMENT '活动成功参与会员数',
    PRIMARY KEY (`active_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺营销活动表（整体活动）' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_active_goods`;
CREATE TABLE `{{prefix}}mall_active_goods`  (
    `active_goods_id` int NOT NULL AUTO_INCREMENT COMMENT '活动商品id',
    `active_id` int NOT NULL DEFAULT 0 COMMENT '活动id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
    `active_goods_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品活动类型（单品，独立商品，店铺整体商品）',
    `active_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品活动类别',
    `active_goods_label` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动商品标签（针对活动有标签）',
    `active_goods_category` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动商品分类（针对活动有分类）',
    `active_goods_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '活动商品信息数据',
    `active_goods_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动状态',
    `active_goods_point` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '活动商品积分（展示，搜索）',
    `active_goods_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '活动商品价格（展示，搜索）',
    `active_goods_stock` int NOT NULL DEFAULT 0 COMMENT '活动商品库存（针对参与库存）',
    `active_goods_order_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '活动累计金额',
    `active_goods_order_num` int NOT NULL DEFAULT 0 COMMENT '活动累计订单数',
    `active_goods_member_num` int NOT NULL DEFAULT 0 COMMENT '活动参与会员数',
    `active_goods_success_num` int NOT NULL DEFAULT 0 COMMENT '活动成功参与会员数',
    PRIMARY KEY (`active_goods_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺营销活动' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_cart`;
CREATE TABLE `{{prefix}}mall_cart`  (
    `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车表ID',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `member_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '会员ID',
    `goods_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商品ID',
    `sku_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT 'sku id',
    `num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商品数量',
    `market_type` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '活动类型',
    `market_type_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '活动id',
    `create_time` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '添加时间',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '购物车商品状态',
    `invalid_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '失效原因',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `goods_id`(`goods_id` ASC) USING BTREE,
    INDEX `member_id`(`member_id` ASC) USING BTREE,
    INDEX `sku_id`(`sku_id` ASC) USING BTREE,
    INDEX `type`(`market_type` ASC) USING BTREE,
    INDEX `type_id`(`market_type_id` ASC) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_coupon`;
CREATE TABLE `{{prefix}}mall_coupon`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',
    `start_time` int NOT NULL DEFAULT 0 COMMENT '活动开启时间',
    `end_time` int NOT NULL DEFAULT 0 COMMENT '活动结束时间',
    `remain_count` int NOT NULL DEFAULT 0 COMMENT '剩余数量',
    `receive_count` int NOT NULL DEFAULT 0 COMMENT '已领取数量',
    `give_count` int NOT NULL DEFAULT 0 COMMENT '已发放数量',
    `limit_count` int NOT NULL DEFAULT 0 COMMENT '单个会员限制领取数量',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT ' 状态 1 正常 2 未开启 3 已无效',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '添加时间',
    `price` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '面值',
    `min_condition_money` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '商品最低多少金额可用优惠券',
    `type` tinyint NOT NULL DEFAULT 0 COMMENT '优惠券类型 1通用优惠券 2商品品类优惠券 3商品优惠券',
    `receive_type` int NOT NULL DEFAULT 0 COMMENT '领取方式',
    `valid_type` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '有效时间',
    `length` int NOT NULL DEFAULT 0 COMMENT '有效期时长(天)',
    `valid_start_time` int NOT NULL DEFAULT 0 COMMENT '有效期开始时间',
    `valid_end_time` int NOT NULL DEFAULT 0 COMMENT '有效期结束时间',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
    `receive_status` tinyint NOT NULL DEFAULT 1 COMMENT ' 状态 1 正常 2 关闭',
    `join_site_ids` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '支持使用的站点',
    `is_all_site_join` int NOT NULL DEFAULT 0 COMMENT '是否支持全部站点',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `status`(`status` ASC) USING BTREE,
    INDEX `title`(`title` ASC) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '优惠券表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_coupon_goods`;
CREATE TABLE `{{prefix}}mall_coupon_goods`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `coupon_id` int NOT NULL DEFAULT 0 COMMENT '优惠券模板id',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
    `category_id` int NOT NULL DEFAULT 0 COMMENT '分类id',
    `brand_id` int NOT NULL DEFAULT 0 COMMENT '商品品牌id',
    `mall_category_id` int NOT NULL DEFAULT 0 COMMENT '平台分类id',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `index_category_id`(`category_id` ASC) USING BTREE,
    INDEX `index_coupon_id`(`coupon_id` ASC) USING BTREE,
    INDEX `index_goods_id`(`goods_id` ASC) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '优惠券商品或品类关联表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_coupon_member`;
CREATE TABLE `{{prefix}}mall_coupon_member`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '优惠券发放记录id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `coupon_id` int NOT NULL DEFAULT 0 COMMENT '优惠券id',
    `member_id` int NOT NULL DEFAULT 0 COMMENT '会员id',
    `create_time` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '领取时间',
    `expire_time` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '过期时间',
    `use_time` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用时间',
    `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '优惠券类型',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态',
    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '优惠券名称',
    `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '面值',
    `min_condition_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最低使用门槛',
    `receive_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '领取方式',
    `trade_id` int NOT NULL DEFAULT 0 COMMENT '关联业务id',
    `memo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `coupon_id`(`coupon_id` ASC) USING BTREE,
    INDEX `member_id`(`member_id` ASC) USING BTREE,
    INDEX `status`(`status` ASC) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '优惠券会员领取记录表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{{prefix}}mall_goods`;
CREATE TABLE `{{prefix}}mall_goods`  (
    `goods_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品名称',
    `goods_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'real' COMMENT '商品类型',
    `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '副标题',
    `goods_cover` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品封面',
    `goods_image` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品图片',
    `goods_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品分类',
    `goods_mall_category` int NOT NULL DEFAULT 0 COMMENT '平台分类',
    `goods_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品介绍',
    `brand_id` int NOT NULL DEFAULT 0 COMMENT '商品品牌id',
    `label_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签组',
    `service_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品服务',
    `unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '件' COMMENT '单位',
    `stock` int NOT NULL DEFAULT 0 COMMENT '商品库存（总和）',
    `sale_num` int NOT NULL DEFAULT 0 COMMENT '销量',
    `virtual_sale_num` int NOT NULL DEFAULT 0 COMMENT '虚拟销量',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '商品状态，1上架 0下架 -3强制下架',
    `audit_status` int NOT NULL DEFAULT 0 COMMENT '审核状态，1待审核 2审核通过 -1审核未通过',
    `audit_reason` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '审核原因',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
    `delivery_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '支持的配送方式',
    `is_free_shipping` tinyint NOT NULL DEFAULT 1 COMMENT '是否免邮',
    `fee_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '运费设置，选择模板：template，固定运费：fixed',
    `delivery_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '固定运费',
    `delivery_template_id` int NOT NULL DEFAULT 0 COMMENT '运费模板',
    `virtual_auto_delivery` tinyint NOT NULL DEFAULT 0 COMMENT '虚拟商品是否自动发货',
    `virtual_delivery_type` varchar(255) NOT NULL DEFAULT 'auto' COMMENT '虚拟商品发货方式，auto：自动发货，manual：手动发货，verify：到店核销',
    `virtual_receive_type` varchar(255) NOT NULL DEFAULT 'artificial' COMMENT '虚拟商品收货方式(自动发货后)，auto：自动收货，artificial：买家确认收货',
    `virtual_delivery_content` text NULL COMMENT '虚拟商品发货内容',
    `virtual_verify_type` tinyint NOT NULL DEFAULT 0 COMMENT '虚拟商品核销有效期类型，0：不限，1：购买后几日有效，2：指定过期日期',
    `virtual_indate` int NOT NULL DEFAULT 0 COMMENT '虚拟到期时间',
    `virtual_refund` tinyint NOT NULL DEFAULT 0 COMMENT '虚拟商品退款，0：不允许退款，1：付款成功后指定天数内有效，2：过期自动退款（核销有效期为1、2时生效）',
    `virtual_refund_day` int NOT NULL DEFAULT 0 COMMENT '付款成功后几天内可退款，为0不限制',
    `supplier_id` int NOT NULL DEFAULT 0 COMMENT '供应商id',
    `attr_format` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品参数内容，json格式',
    `is_discount` int NOT NULL DEFAULT 0 COMMENT '是否参与限时折扣',
    `member_discount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price',
    `form_id` int NOT NULL DEFAULT 0 COMMENT '万能表单id',
    `is_limit` tinyint NOT NULL DEFAULT 0 COMMENT '商品是否限购(0:否 1:是)',
    `limit_type` tinyint NOT NULL DEFAULT 1 COMMENT '限购类型，1：单次限购，2：单人限购',
    `max_buy` int NOT NULL DEFAULT 0 COMMENT '限购数',
    `min_buy` int NOT NULL DEFAULT 0 COMMENT '起购数',
    `access_num` int NOT NULL DEFAULT 0 COMMENT '访问次数(浏览量)',
    `cart_num` int NOT NULL DEFAULT 0 COMMENT '加入购物车数量',
    `pay_num` int NOT NULL DEFAULT 0 COMMENT '支付件数',
    `pay_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '支付总金额',
    `collect_num` int NOT NULL DEFAULT 0 COMMENT '收藏数量',
    `evaluate_num` int NOT NULL DEFAULT 0 COMMENT '评论数量',
    `refund_num` int NOT NULL DEFAULT 0 COMMENT '退款件数',
    `refund_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '退款总额',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '修改时间',
    `delete_time` int NOT NULL DEFAULT 0 COMMENT '删除时间',
    `shop_sort` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商品排序',
    `supply_id` int NOT NULL DEFAULT 0 COMMENT '供应商id',
    `supply_goods_id` int NOT NULL DEFAULT 0 COMMENT '关联供应商商品id',
    `supply_sync` tinyint NOT NULL DEFAULT 0 COMMENT '供应商设置同步，1.开启，0.关闭',
    `is_supply_update` tinyint NOT NULL DEFAULT 0 COMMENT '供应商是否有更新',
    `apply_time` int NOT NULL DEFAULT 0 COMMENT '申请审核时间',
    `virtual_verify_mobile` tinyint NOT NULL DEFAULT 0 COMMENT '虚拟商品是否核销手机号，1.开启，0.关闭',
    `goods_video` varchar(555) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品视频',
    `attr_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '平台商品参数id，支持多个',
    `shop_attr_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '店铺商品参数id，支持多个',
    `diy_detail_id` int NOT NULL DEFAULT 0 COMMENT '自定义详情id',
    `timer_on_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '自动上架开关，1.开启，0.关闭',
    `timer_on_time` int NOT NULL DEFAULT 0 COMMENT '自动上架时间',
    `timer_off_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '自动下架开关，1.开启，0.关闭',
    `timer_off_time` int(10) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '自动下架时间',
    `support_pay_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '支持的支付方式',
    `is_enable_refund` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许退款',
    `point_deduct_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '积分抵扣类型 custom：自定义 site:继承店铺',
    `point_deduct_rate` float(10, 2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣比例',
    `source_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '商品来源 0.本地添加 1.导入商品 2.采集商品',
    `source_goods_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '导入/采集商品关联goods_id/spu_id',
    `source_flatform` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采集商品平台标识  例如:99api等等',
    `source_party` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采集商品第三方标识  例如：taobao、tmall、jd等等',
    `is_enable_point_deduct` int NOT NULL DEFAULT 0,
    PRIMARY KEY (`goods_id`) USING BTREE,
    INDEX `idx_goods_category`(`goods_category` ASC) USING BTREE,
    INDEX `idx_goods_create_time`(`create_time` ASC) USING BTREE,
    INDEX `idx_goods_delete_time`(`delete_time` ASC) USING BTREE,
    INDEX `idx_goods_name`(`goods_name` ASC) USING BTREE,
    INDEX `idx_goods_sort`(`sort` ASC) USING BTREE,
    INDEX `idx_goods_status`(`status` ASC) USING BTREE,
    INDEX `idx_goods_sub_title`(`sub_title` ASC) USING BTREE,
    INDEX `IDX_ns_goods_goods_class`(`goods_type` ASC) USING BTREE,
    INDEX `IDX_goods_source_type`(`source_type` ASC) USING BTREE,
    INDEX `IDX_goods_source_goods_id`(`source_goods_id` ASC) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_goods_browse`;
CREATE TABLE `{{prefix}}mall_goods_browse`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL DEFAULT 0,
    `member_id` int NOT NULL DEFAULT 0 COMMENT '浏览人',
    `sku_id` int NOT NULL DEFAULT 0 COMMENT 'sku_id',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
    `browse_time` int NOT NULL DEFAULT 0 COMMENT '浏览时间',
    `goods_cover` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品图片',
    `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品名称',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品浏览历史' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_goods_collect`;
CREATE TABLE `{{prefix}}mall_goods_collect`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `member_id` int NOT NULL DEFAULT 0 COMMENT '会员id',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '收藏时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `IDX_member_collect_goods`(`goods_id` ASC) USING BTREE,
    INDEX `IDX_member_collect_member`(`member_id` ASC) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品收藏记录表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_goods_evaluate`;
CREATE TABLE `{{prefix}}mall_goods_evaluate`  (
    `evaluate_id` int NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
    `order_goods_id` int NOT NULL DEFAULT 0 COMMENT '订单项ID',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品ID',
    `member_id` int NOT NULL DEFAULT 0 COMMENT '会员ID',
    `member_head` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '会员头像',
    `member_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '会员名称',
    `content` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '评价内容',
    `images` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '评价图片',
    `is_anonymous` tinyint NOT NULL DEFAULT 1 COMMENT '1匿名  2不匿名',
    `scores` tinyint NOT NULL DEFAULT 1 COMMENT '评论分数 1-5',
    `is_audit` tinyint NOT NULL DEFAULT 1 COMMENT '审核状态 1待审 2通过 3拒绝',
    `explain_first` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '解释内容',
    `topping` int NOT NULL DEFAULT 0 COMMENT '排序 置顶',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '评论时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '修改时间',
    `quality_score` decimal(2, 1) NOT NULL DEFAULT 5.0 COMMENT '商品质量',
    `service_score` decimal(2, 1) NOT NULL DEFAULT 5.0 COMMENT '商家服务',
    `logistics_score` decimal(2, 1) NOT NULL DEFAULT 5.0 COMMENT '发货物流',
    PRIMARY KEY (`evaluate_id`) USING BTREE,
    INDEX `idx_mall_goods_evaluate_create_time`(`create_time` ASC) USING BTREE,
    INDEX `idx_mall_goods_evaluate_goods_id`(`goods_id` ASC) USING BTREE,
    INDEX `idx_mall_goods_evaluate_is_anonymous`(`is_anonymous` ASC) USING BTREE,
    INDEX `idx_mall_goods_evaluate_is_audit`(`is_audit` ASC) USING BTREE,
    INDEX `idx_mall_goods_evaluate_member_id`(`member_id` ASC) USING BTREE,
    INDEX `idx_mall_goods_evaluate_order_id`(`order_id` ASC) USING BTREE,
    INDEX `idx_mall_goods_evaluate_scores`(`scores` ASC) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品评价表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_goods_label`;
CREATE TABLE `{{prefix}}mall_goods_label`  (
    `label_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `label_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签名称',
    `group_id` int NOT NULL DEFAULT 0 COMMENT '标签分组id',
    `style_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '效果设置，diy：自定义，icon：图片',
    `color_json` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '自定义颜色（文字、背景、边框），json格式',
    `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图标',
    `status` int NOT NULL DEFAULT 0 COMMENT '状态，1：启用，0：关闭',
    `memo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签说明',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '更新时间',
    PRIMARY KEY (`label_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品标签表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_goods_rank`;
CREATE TABLE `{{prefix}}mall_goods_rank`  (
    `rank_id` int NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '榜单名称',
    `rank_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '排行周期 day=天，week=周，month=月, quarter=季度',
    `goods_source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源类型 goods=指定商品，category=指定分类，brand=指定品牌, label=指定标签',
    `rule_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '排序规则 sale=按照销量，collect=按收藏数，evaluate=按评价数, access=按照浏览量',
    `goods_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品信息',
    `category_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品分类id',
    `brand_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '商品品牌id',
    `label_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品标签id，多个逗号隔开',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` int NOT NULL DEFAULT 1 COMMENT '显示状态（0不显示 1显示）',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '修改时间',
    PRIMARY KEY (`rank_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品排行榜' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_goods_sku`;
CREATE TABLE `{{prefix}}mall_goods_sku`  (
    `sku_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品sku_id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品sku名称',
    `sku_image` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'sku主图',
    `sku_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品sku编码',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
    `sku_spec_format` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'sku规格格式',
    `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'sku单价',
    `market_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '划线价',
    `sale_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实际卖价（有活动显示活动价，默认原价）',
    `cost_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'sku成本价',
    `min_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '控价区间最小值',
    `max_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '控价区间最大值',
    `stock` int NOT NULL DEFAULT 0 COMMENT '商品sku库存',
    `weight` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '重量（单位kg）',
    `volume` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '体积（单位立方米）',
    `sale_num` int NOT NULL DEFAULT 0 COMMENT '销量',
    `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认',
    `member_price` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '会员价，json格式，指定会员价，数据结构为：{\"level_1\":\"10.00\",\"level_2\":\"10.00\"}',
    `supply_id` int NOT NULL DEFAULT 0 COMMENT '供应商id',
    `supply_sku_id` int NOT NULL DEFAULT 0 COMMENT '关联供应商商品规格id',
    `is_sell` tinyint NOT NULL DEFAULT 1 COMMENT '是否售卖，0.否，1.是',
    `source_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '商品来源 0.本地添加 1.导入商品 2.采集商品',
    `source_goods_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '导入/采集商品关联goods_id/spu_id',
    `source_sku_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '导入/采集商品关联sku_id',
    `source_flatform` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采集商品平台标识  例如:99api等等',
    `source_party` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采集商品第三方标识  例如：taobao、tmall、jd等等',
    PRIMARY KEY (`sku_id`) USING BTREE,
    INDEX `idx_goods_sku_is_default`(`is_default` ASC) USING BTREE,
    INDEX `idx_goods_sku_price`(`price` ASC) USING BTREE,
    INDEX `idx_goods_sku_sale_price`(`sale_price` ASC) USING BTREE,
    INDEX `IDX_goods_source_type`(`source_type` ASC) USING BTREE,
    INDEX `IDx_goods_source_goods_id`(`source_goods_id` ASC) USING BTREE,
    INDEX `IDx_goods_source_sku_id`(`source_sku_id` ASC) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品规格表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_goods_spec`;
CREATE TABLE `{{prefix}}mall_goods_spec`  (
    `spec_id` int NOT NULL AUTO_INCREMENT COMMENT '规格id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '关联商品id',
    `spec_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '规格项名称',
    `spec_values` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '规格值名称，多个逗号隔开',
    `supply_spec_id` int NOT NULL DEFAULT 0 COMMENT '供应商规格id',
    PRIMARY KEY (`spec_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品规格项/值表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_goods_stat`;
CREATE TABLE `{{prefix}}mall_goods_stat`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `date` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '日期',
    `date_time` int NOT NULL DEFAULT 0 COMMENT '时间戳',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
    `cart_num` int NOT NULL DEFAULT 0 COMMENT '加入购物车数量',
    `sale_num` int NOT NULL DEFAULT 0 COMMENT '商品销量（下单数）',
    `pay_num` int NOT NULL DEFAULT 0 COMMENT '支付件数',
    `pay_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '支付总金额',
    `refund_num` int NOT NULL DEFAULT 0 COMMENT '退款件数',
    `refund_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '退款总额',
    `access_num` int NOT NULL DEFAULT 0 COMMENT '访问次数（浏览量）',
    `collect_num` int NOT NULL DEFAULT 0 COMMENT '收藏数量',
    `evaluate_num` int NOT NULL DEFAULT 0 COMMENT '评论数量',
    `goods_visit_member_count` int NOT NULL DEFAULT 0 COMMENT '商品访客数',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `site_id`(`site_id` ASC) USING BTREE,
    INDEX `date`(`date` ASC) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品数据统计' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_order`;
CREATE TABLE `{{prefix}}mall_order`  (
    `order_id` int NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `site_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '店铺（站点）名称',
    `group_id` int NOT NULL DEFAULT 0 COMMENT '订单组id',
    `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单编号',
    `body` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单内容',
    `order_type` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单类型',
    `order_from` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单来源',
    `out_trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '支付流水号',
    `status` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单状态',
    `member_id` int NOT NULL DEFAULT 0 COMMENT '会员id',
    `ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'ip',
    `goods_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品金额',
    `delivery_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '配送金额',
    `discount_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    `mall_discount_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '平台优惠金额',
    `order_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
    `invoice_id` int NOT NULL DEFAULT 0 COMMENT '发票id，0表示不开发票',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `pay_time` int NOT NULL DEFAULT 0 COMMENT '订单支付时间',
    `delivery_time` int NOT NULL DEFAULT 0 COMMENT '订单发货时间',
    `take_time` int NOT NULL DEFAULT 0 COMMENT '订单收货时间',
    `finish_time` int NOT NULL DEFAULT 0 COMMENT '订单完成时间',
    `close_time` int NOT NULL DEFAULT 0 COMMENT '订单关闭时间',
    `delete_time` int NOT NULL DEFAULT 0 COMMENT '是否删除(针对后台)',
    `timeout` int NOT NULL DEFAULT 0 COMMENT '通用业务超时时间记录',
    `delivery_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配送方式',
    `sub_delivery_type` varchar(255) NOT NULL DEFAULT '' COMMENT '发货方式',
    `take_store_id` int NOT NULL DEFAULT 0 COMMENT '配送点/自提点',
    `taker_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货人',
    `taker_mobile` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货人手机号',
    `taker_province` int NOT NULL DEFAULT 0 COMMENT '收货省',
    `taker_city` int NOT NULL DEFAULT 0 COMMENT '收货市',
    `taker_district` int NOT NULL DEFAULT 0 COMMENT '收货区县',
    `taker_address` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货地址',
    `taker_full_address` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货详细地址',
    `taker_longitude` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货地址经度',
    `taker_latitude` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货详细纬度',
    `taker_store_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货门店',
    `is_enable_refund` int NOT NULL DEFAULT 0 COMMENT '是否允许退款',
    `member_remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '会员留言信息',
    `shop_remark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商家留言',
    `close_remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关闭原因',
    `close_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关闭来源(未支付自动关闭   手动关闭  退款关闭)',
    `refund_status` int NOT NULL DEFAULT 1 COMMENT '退款状态  1不存在退款  2 部分退款  3 全部退款',
    `has_goods_types` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '包含的商品类型 json',
    `is_evaluate` int NOT NULL DEFAULT 0 COMMENT '是否评论',
    `mall_remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '卖家留言',
    `mall_rate` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '平台分成比率',
    `refund_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计退款金额',
    `refund_delivery_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计退还运费',
    `shop_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '店铺结算金额',
    `mall_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '平台手续费',
    `shop_refund_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '店铺退款结算金额',
    `mall_refund_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '平台退款手续费',
    `mall_coupon_money` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '平台优惠券补贴',
    `mall_refund_coupon_money` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '返还平台优惠券补贴',
    `shop_commission` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '店铺支付佣金',
    `shop_refund_commission` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '店铺退还佣金',
    `sett_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商户分账结算状态',
    `coupon_sett_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '平台手续费结算状态',
    `is_sync_supply` int NOT NULL DEFAULT 1 COMMENT '是否同步到供应商 1 已同步  2 未同步',
    `supply_id` int NOT NULL DEFAULT 0 COMMENT '供应商id',
    `supply_order_id` int NOT NULL DEFAULT 0 COMMENT '供货商订单id',
    `cost_goods_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品成本总价',
    `supply_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '供货商支出',
    `supply_refund_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '返还供货商支出',
    `settlement_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'wait' COMMENT '结算状态',
    `settlement_time` int NOT NULL DEFAULT 0 COMMENT '结算时间',
    `form_record_id` int NOT NULL DEFAULT 0 COMMENT '万能表单记录id',
    `supply_delivery_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '供货商运费',
    `buyer_ask_delivery_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '购买人要求的配送/发货/自提时间（文本）',
    `extend_field_1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
    `extend_field_2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
    `extend_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '扩展数据',
    `settlement_timer` int NOT NULL DEFAULT 0 COMMENT '预计结算时间',
    `perk_status` int NOT NULL DEFAULT 1 COMMENT '补贴状态',
    `perk_time` int NOT NULL DEFAULT 0 COMMENT '补贴时间',
    `evaluate_timer` int NOT NULL DEFAULT 0 COMMENT '自动好评时间，评价后重置为0',
    `weapp_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '小程序类型，developer为开发版；trial为体验版；formal为正式版；默认为正式版',
    `use_point` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '抵扣积分',
    `point_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额',
    `refund_point_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '退款返还积分抵扣金额',
    `refund_deduct_point` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '退款返还抵扣积分',
    `full_reduction_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '参与满减活动扣减金额',
    PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_order_batch_delivery`;
CREATE TABLE `{{prefix}}mall_order_batch_delivery`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `main_id` int NOT NULL DEFAULT 0 COMMENT '操作人id',
    `status` int NOT NULL DEFAULT 1 COMMENT '状态 进行中  已完成  已失败',
    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '操作类型 批量发货  批量打单 ....' COMMENT '操作类型',
    `total_num` int NOT NULL DEFAULT 0 COMMENT '总发货单数',
    `success_num` int NOT NULL DEFAULT 0 COMMENT '成功发货单数',
    `fail_num` int NOT NULL DEFAULT 0 COMMENT '失败发货单数',
    `data` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '导入文件的路径',
    `output` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对外输出记录',
    `fail_output` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '失败记录',
    `fail_remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '失败原因',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '操作时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单批量发货表' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `{{prefix}}mall_order_delivery`;
CREATE TABLE `{{prefix}}mall_order_delivery`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发货编号',
    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '包裹名称',
    `delivery_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配送方式',
    `sub_delivery_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '详细配送方式',
    `express_company_id` int NOT NULL DEFAULT 0 COMMENT '快递公司id',
    `express_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配送单号',
    `local_deliver_id` int NOT NULL DEFAULT 0 COMMENT '同城配送员',
    `status` int NOT NULL DEFAULT 0 COMMENT '状态  ',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
    `local_delivery_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '同城配送单号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单发货表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_order_discount`;
CREATE TABLE `{{prefix}}mall_order_discount`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
    `order_goods_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '参与的订单商品项',
    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '类型 discount 优惠，gift 赠送',
    `num` int NOT NULL DEFAULT 0 COMMENT '使用数量',
    `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    `discount_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '优惠类型',
    `discount_type_id` int NOT NULL DEFAULT 0 COMMENT '优惠类型id',
    `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单优惠说明',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `status` int NOT NULL DEFAULT 1 COMMENT '状态',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单优惠表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_order_discount_goods`;
CREATE TABLE `{{prefix}}mall_order_discount_goods`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
    `order_goods_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '参与的订单商品项',
    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '类型 discount 优惠，gift 赠送',
    `discount_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '详细优惠类型',
    `order_discount_id` int NOT NULL DEFAULT 0 COMMENT '订单参与优惠id',
    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '优惠标题',
    `num` int NOT NULL DEFAULT 0 COMMENT '使用数量',
    `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '优惠描述',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单项优惠表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_order_goods`;
CREATE TABLE `{{prefix}}mall_order_goods`  (
    `order_goods_id` int NOT NULL AUTO_INCREMENT COMMENT '  ',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
    `member_id` int NOT NULL DEFAULT 0 COMMENT '购买会员id',
    `goods_id` int NOT NULL DEFAULT 0 COMMENT '商品id',
    `sku_id` int NOT NULL DEFAULT 0 COMMENT '商品规格id',
    `goods_name` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品名称',
    `sku_name` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品规格名称',
    `goods_image` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品图片',
    `sku_image` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'sku规格图片',
    `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品单价',
    `num` int NOT NULL DEFAULT 0 COMMENT '购买数量',
    `goods_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品总价',
    `is_enable_refund` int NOT NULL DEFAULT 0 COMMENT '是否允许退款',
    `goods_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品类型',
    `delivery_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配送状态',
    `delivery_id` int NOT NULL DEFAULT 0 COMMENT '发货单号',
    `discount_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    `status` int NOT NULL DEFAULT 0 COMMENT '状态',
    `order_refund_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退款单号',
    `order_goods_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单项实付金额',
    `original_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品原价',
    `verify_count` int NOT NULL DEFAULT 0 COMMENT '已核销次数',
    `verify_expire_time` int NOT NULL DEFAULT 0 COMMENT '过期时间 0 为永久',
    `is_verify` int NOT NULL DEFAULT 0 COMMENT '是否需要核销',
    `shop_active_refund` tinyint NOT NULL DEFAULT 0 COMMENT '商家主动退款（0否  1是）',
    `shop_active_refund_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商家主动退款金额',
    `mall_discount_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '平台优惠',
    `supply_goods_id` int NOT NULL DEFAULT 0 COMMENT '供货商商品id',
    `supply_id` int NOT NULL DEFAULT 0 COMMENT '供货商id',
    `supply_sku_id` int NOT NULL DEFAULT 0 COMMENT '供货商商品sku_id',
    `delivery_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '运费',
    `cost_goods_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品成本总额相关',
    `supply_order_id` int NOT NULL DEFAULT 0 COMMENT '供应商订单id',
    `supply_order_goods_id` int NOT NULL DEFAULT 0 COMMENT '供应商订单项id',
    `form_record_id` int NOT NULL DEFAULT 0 COMMENT '万能表单记录id',
    `use_point` int NOT NULL DEFAULT 0 COMMENT '使用积分数',
    `point_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额',
    `full_reduction_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '满减活动扣减金额',
    `is_gift` tinyint NOT NULL DEFAULT 0 COMMENT '是否是赠品',
    `goods_config` text NULL COMMENT '商品对应配置',
    `virtual_refund_type` tinyint NOT NULL DEFAULT 0 COMMENT '虚拟商品退款类型（0不允许退款 1主动退 2自动退）',
    PRIMARY KEY (`order_goods_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单项表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_order_group`;
CREATE TABLE `{{prefix}}mall_order_group`  (
    `group_id` int NOT NULL AUTO_INCREMENT,
    `member_id` int NOT NULL DEFAULT 0 COMMENT '会员id',
    `body` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单内容',
    `goods_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品金额',
    `delivery_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '配送金额',
    `shop_discount_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '店铺优惠金额',
    `use_point` int NOT NULL DEFAULT 0 COMMENT '使用积分数',
    `point_deduct_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额',
    `full_reduction_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '满减活动扣减金额',
    `mall_discount_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '平台优惠金额',
    `order_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
    `shop_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '店铺结算金额',
    `mall_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '平台结算金额',
    `status` int NOT NULL DEFAULT 1 COMMENT '状态',
    `is_bundle_pay` int NOT NULL DEFAULT 0 COMMENT '是否只能捆绑支付',
    `out_trade_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外部交易号',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `pay_time` int NOT NULL DEFAULT 0 COMMENT '支付时间',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id(跨店支付为0)',
    `taker_full_address` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货地址',
    PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单组表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_order_group_discount`;
CREATE TABLE `{{prefix}}mall_order_group_discount`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `site_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '站点ids',
    `order_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单ids',
    `group_id` int NOT NULL DEFAULT 0 COMMENT '订单组id',
    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '类型 discount 优惠，gift 赠送',
    `num` int NOT NULL DEFAULT 0 COMMENT '使用数量',
    `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    `discount_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '优惠类型',
    `discount_type_id` int NOT NULL DEFAULT 0 COMMENT '优惠类型id',
    `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单优惠说明',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `status` int NOT NULL DEFAULT 1 COMMENT '状态',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单组优惠表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_order_log`;
CREATE TABLE `{{prefix}}mall_order_log`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
    `main_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '操作人类型',
    `main_id` int NOT NULL DEFAULT 0 COMMENT '操作人id',
    `status` int NULL DEFAULT NULL DEFAULT 0  COMMENT '订单状态',
    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志内容',
    `create_time` int NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单日志表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_order_refund`;
CREATE TABLE `{{prefix}}mall_order_refund`  (
    `refund_id` int NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
    `order_goods_id` int NOT NULL DEFAULT 0 COMMENT '订单项id',
    `order_refund_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '退款单号',
    `refund_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '退款方式 ',
    `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '退款原因 ',
    `member_id` int NOT NULL DEFAULT 0 COMMENT '会员id',
    `apply_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '申请退款',
    `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实际退款',
    `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '退款状态',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `transfer_time` int NOT NULL DEFAULT 0 COMMENT '转账时间',
    `remark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '描述' COMMENT '描述',
    `voucher` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '凭证' COMMENT '凭证',
    `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源 system 系统 member 会员',
    `timeout` int NOT NULL DEFAULT 0 COMMENT '操作超时时间',
    `refund_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退款交易号',
    `delivery` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退货配送信息',
    `shop_reason` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商家拒绝原因',
    `refund_address` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商家退货地址',
    `is_refund_delivery` int NOT NULL DEFAULT 0 COMMENT '是否退运费',
    `refund_commission` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '退还佣金',
    `supply_id` int NOT NULL DEFAULT 0 COMMENT '供应商id',
    `supply_refund_id` int NOT NULL DEFAULT 0 COMMENT '供应商退款id',
    `weapp_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '小程序类型，developer为开发版；trial为体验版；formal为正式版；默认为正式版',
    PRIMARY KEY (`refund_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单退款表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_order_refund_log`;
CREATE TABLE `{{prefix}}mall_order_refund_log`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
    `order_refund_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退款编号',
    `main_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '操作人类型',
    `main_id` int NOT NULL DEFAULT 0 COMMENT '操作人id',
    `status` int NULL DEFAULT NULL DEFAULT 0 COMMENT '退款状态',
    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志内容',
    `create_time` int NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单退款日志表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_shop_category`;
CREATE TABLE `{{prefix}}mall_shop_category`  (
    `category_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类id',
    `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分类名称',
    `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分类图',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `commission_rate` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '手续费比率',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '添加时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '更新时间',
    PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺分类' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_web_floor`;
CREATE TABLE `{{prefix}}mall_web_floor`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `floor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '楼层名称',
    `temp` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置模板',
    `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置参数',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '添加时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '更新时间',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0隐藏 1显示）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '首页楼层' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}mall_full_gift`;
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

DROP TABLE IF EXISTS `{{prefix}}mall_full_gift_goods`;
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

DROP TABLE IF EXISTS `{{prefix}}mall_full_reduction`;
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
    `rule_type` varchar(20) NULL DEFAULT NULL COMMENT '规则类型  ladder-阶梯  cycle-循环',
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

DROP TABLE IF EXISTS `{{prefix}}mall_full_reduction_goods`;
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

DROP TABLE IF EXISTS `{{prefix}}mall_goods_import_record`;
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

DROP TABLE IF EXISTS `{{prefix}}mall_goods_import_rows`;
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

DROP TABLE IF EXISTS `{{prefix}}mall_order_gift_goods`;
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
    `sku_image` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'sku规格图片',
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

DROP TABLE IF EXISTS `{{prefix}}mall_order_delivery_content`;
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