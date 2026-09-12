ALTER TABLE `{{prefix}}goods_service` ADD COLUMN `addon` text NOT NULL COMMENT '插件';

ALTER TABLE `{{prefix}}sys_stat` ADD COLUMN `circle_commission` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商圈佣金';

ALTER TABLE `{{prefix}}sys_stat` ADD COLUMN `circle_refund_commission` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商圈退还佣金';

ALTER TABLE `{{prefix}}site_shop` ADD COLUMN `service_rate` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '佣金比率';

ALTER TABLE `{{prefix}}site_shop` ADD COLUMN `circle_id` int NOT NULL DEFAULT 0 COMMENT '商圈id';

ALTER TABLE `{{prefix}}site_shop` ADD COLUMN `child_category_id` int NOT NULL DEFAULT 0 COMMENT '子级行业类目(不涉及系统业务)';

ALTER TABLE `{{prefix}}site_shop` ADD COLUMN `keywords` varchar(3000) NOT NULL DEFAULT '' COMMENT '店铺关键词';

ALTER TABLE `{{prefix}}site_shop` MODIFY COLUMN `category_id` int NOT NULL DEFAULT 0 COMMENT '商户类目id';

ALTER TABLE `{{prefix}}site` ADD COLUMN `business_state` tinyint NOT NULL DEFAULT 1 COMMENT '营业状态（0已打烊，1营业中）';

ALTER TABLE `{{prefix}}separ_account` ADD COLUMN `trade_scene` varchar(32) NOT NULL DEFAULT 1 COMMENT '订单来源';

DROP TABLE IF EXISTS `{{prefix}}sys_operation_log`;
CREATE TABLE `{{prefix}}sys_operation_log` (
         `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
         `main_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '操作人类型',
         `main_id` int NOT NULL DEFAULT '0' COMMENT '操作人id',
         `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
         `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志内容',
         `param_json` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '请求参数',
         `create_time` int DEFAULT NULL COMMENT '创建时间',
         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';

DROP TABLE IF EXISTS `{{prefix}}site_sett_account`;
CREATE TABLE `{{prefix}}site_sett_account`  (
          `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
          `main_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '组织类型',
          `main_id` int NOT NULL DEFAULT 0 COMMENT '组织id',
          `type` tinyint NOT NULL DEFAULT 1 COMMENT '提现账户类型（1银行卡，2支付宝，3微信，4微信零钱）',
          `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '提现账号对应姓名',
          `account_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '提现银行账号',
          `account_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收款码图片',
          `bank_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行名称',
          `bank_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '银行地址',
          `bind_member_id` int NOT NULL DEFAULT 0 COMMENT '绑定会员',
          `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
          `update_time` int NOT NULL DEFAULT 0 COMMENT '更新时间',
          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '结算账号' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}site_supply`;
CREATE TABLE `{{prefix}}site_supply`  (
            `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
            `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
            `service_rate` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '服务费比率',
            `supply_sale_num` int NOT NULL DEFAULT 0 COMMENT '供货商商品销量',
            `supply_up_num` int NOT NULL DEFAULT 0 COMMENT '供货商上架商品数',
            `supply_follow_num` int NOT NULL DEFAULT 0 COMMENT '关注站点数',
            `supply_goods_num` int NOT NULL DEFAULT 0 COMMENT '供货商商品总数',
            `business_status` tinyint NOT NULL DEFAULT 1 COMMENT '营业状态（0已打烊，1营业中）',
            `category_id` int NOT NULL DEFAULT 0,
            `child_category_id` int NOT NULL DEFAULT 0,
            `keywords` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '店铺关键词',
            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '供货商扩展表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}site_o2o`;
CREATE TABLE `{{prefix}}site_o2o`  (
         `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
         `site_id` int NOT NULL DEFAULT '0' COMMENT '系统站点ID（关联ns_site）',
         `business_type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '主体类型 1个人 2个体 3企业',
         `business_status` tinyint NOT NULL DEFAULT '1' COMMENT '营业状态（0已打烊，1营业中）',
         `service_rate` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '佣金比率',
         `id_card_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '法人姓名',
         `id_card_front` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '身份证正面',
         `id_card_back` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '身份证反面',
         `create_time` int NOT NULL DEFAULT '0',
         `update_time` int NOT NULL DEFAULT '0',
         `category_id` int NOT NULL DEFAULT '0' COMMENT '主营一级行业ID',
         `child_category_id` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '主营二级行业ID',
         `site_score` decimal(3,2) NOT NULL DEFAULT '5.00' COMMENT '店铺评分（0.00~5.00）',
         `per_capita` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '人均消费',
         `facility_service` varchar(500) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '设施服务，逗号分隔',
         `keywords` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '店铺关键词  用于搜出',
         `evaluate_num` int NOT NULL DEFAULT '0' COMMENT '评价次数',
         `store_photo` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '门头照   多张，隔开',
         `sale_num` int NOT NULL DEFAULT '0' COMMENT '销量',
         `circle_id` int NOT NULL DEFAULT '0' COMMENT '商圈id',
         PRIMARY KEY (`id`) USING BTREE,
         UNIQUE INDEX `site_id`(`site_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'O2O站点业务表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}site_category`;
CREATE TABLE `{{prefix}}site_category`  (
      `category_id` int NOT NULL AUTO_INCREMENT COMMENT '行业类目ID',
      `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '行业名称',
      `level` tinyint(1) NOT NULL DEFAULT 1 COMMENT '层级：1=一级 2=二级',
      `pid` int NOT NULL DEFAULT 0 COMMENT '父级ID，一级为0',
      `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
      `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：1=启用 0=禁用',
      `create_time` int NOT NULL DEFAULT 0,
      `update_time` int NOT NULL DEFAULT 0,
      `addon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '归属应用',
      `service_rate` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '服务费比率',
      `group_id` int NOT NULL DEFAULT 0 COMMENT '套餐id',
      `banner_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '轮播图',
      PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '行业类目表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{{prefix}}site_apply`;
CREATE TABLE `{{prefix}}site_apply`  (
       `apply_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
       `site_id` int NOT NULL DEFAULT 0 COMMENT '开店店铺id',
       `member_id` int NOT NULL DEFAULT 0 COMMENT '申请会员',
       `site_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商户名称',
       `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户姓名',
       `user_mobile` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户手机号',
       `category_id` int NOT NULL DEFAULT 0 COMMENT '所属行业id',
       `child_category_id` int NOT NULL DEFAULT 0 COMMENT '子级行业id',
       `business_license` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '营业执照图片路径',
       `status` tinyint NOT NULL COMMENT '申请状态（1待审核，2已通过，-1已拒绝）',
       `create_time` int NOT NULL DEFAULT 0 COMMENT '申请时间',
       `update_time` int NOT NULL DEFAULT 0 COMMENT '修改时间',
       `audit_time` int NOT NULL DEFAULT 0 COMMENT '审核通过时间',
       `refuse_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '拒绝原因',
       `open_status` tinyint NOT NULL DEFAULT 1 COMMENT '开店状态（1待开店，2已开店）',
       `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '店铺登录账号',
       `form_record_id` int NOT NULL DEFAULT 0 COMMENT '万能表单记录id',
       `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '店铺登录密码',
       `uid` int NOT NULL DEFAULT 0 COMMENT '店铺用户id',
       `addon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '所属应用',
       `apply_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '申请方式  member 客户端用户申请  site 站点端用户申请',
       `circle_id` int NOT NULL DEFAULT 0 COMMENT '商圈id',
       PRIMARY KEY (`apply_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商户入驻申请表' ROW_FORMAT = Dynamic;
