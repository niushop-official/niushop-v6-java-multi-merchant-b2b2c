CREATE TABLE `{{prefix}}niu_sms_template`  (
                                     `id` int NOT NULL AUTO_INCREMENT,
                                     `site_id` int NULL DEFAULT 0 COMMENT '站点ID',
                                     `sms_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '短信服务商类型 niuyun-牛云 aliyun-阿里云 tencent-腾讯',
                                     `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '子账号名称',
                                     `template_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '模版key',
                                     `template_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '模版id',
                                     `template_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '模版类型',
                                     `template_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '模版内容',
                                     `param_json` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '参数变量',
                                     `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '上下架状态',
                                     `audit_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '报备、审核状态',
                                     `audit_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '审核结果/拒绝原因',
                                     `report_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '报备、审核信息',
                                     `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
                                     `update_time` int NOT NULL DEFAULT 0 COMMENT '修改时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '牛云短信模板表' ROW_FORMAT = Dynamic;

ALTER TABLE `{{prefix}}goods_brand` ADD COLUMN `audit_status` tinyint NOT NULL DEFAULT 1 COMMENT '审核状态 1待审核 2已通过 -1已拒绝';

ALTER TABLE `{{prefix}}goods_brand` ADD COLUMN `audit_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '审核拒绝原因';

ALTER TABLE `{{prefix}}goods_brand` ADD COLUMN `audit_time` int NOT NULL DEFAULT 0 COMMENT '审核时间';

ALTER TABLE `{{prefix}}goods_brand` ADD COLUMN `status` tinyint NOT NULL DEFAULT 1 COMMENT '显示状态 1显示 2隐藏';

ALTER TABLE `{{prefix}}goods_brand` ADD COLUMN `is_hot` tinyint NOT NULL DEFAULT 2 COMMENT '是否热门 1是 2否';


ALTER TABLE `{{prefix}}invoice` ADD COLUMN `content_type` varchar(255) NOT NULL DEFAULT '' COMMENT '内容类型';


ALTER TABLE `{{prefix}}pay` ADD COLUMN `pay_platform` varchar(255) NOT NULL DEFAULT '' COMMENT '支付平台 wechat  微信支付   ali 支付宝支付  douyin 抖音支付  union银联支付';


ALTER TABLE `{{prefix}}site_shop` ADD COLUMN `contact_name` varchar(255) NOT NULL DEFAULT '' COMMENT '联系人姓名';

ALTER TABLE `{{prefix}}site_shop` ADD COLUMN `background_h5` varchar(255) NOT NULL DEFAULT '' COMMENT 'h5商户背景图';


ALTER TABLE `{{prefix}}sys_stat` ADD COLUMN `recharge_order_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '充值订单金额';

ALTER TABLE `{{prefix}}sys_stat` ADD COLUMN `recharge_give_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '充值赠送金额';

ALTER TABLE `{{prefix}}sys_stat` ADD COLUMN `visit_num` int NOT NULL DEFAULT 0 COMMENT '访客数';

CREATE TABLE `{{prefix}}delivery_weapp_shipping`  (
                                            `id` int NOT NULL AUTO_INCREMENT,
                                            `site_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '站点id集合',
                                            `trade_id` int NOT NULL DEFAULT 0 COMMENT '交易id  group_id',
                                            `trade_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易类型  mall_group或points_mall',
                                            `trade_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易单号  group_id',
                                            `order_number_type` int NOT NULL DEFAULT 0 COMMENT '订单单号类型，1，使用下单商户号和商户侧单号；2，使用微信支付单号',
                                            `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '原支付交易对应的微信订单号',
                                            `logistics_type` int NOT NULL DEFAULT 0 COMMENT '物流模式，1、实体物流配送采用快递公司进行实体物流配送形式 2、同城配送 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式 4、用户自提',
                                            `delivery_mode` int NOT NULL DEFAULT 0 COMMENT '发货模式，1、UNIFIED_DELIVERY（统一发货）2、SPLIT_DELIVERY（分拆发货）',
                                            `openid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '小程序用户标识',
                                            `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
                                            `status` tinyint NOT NULL DEFAULT 0 COMMENT '录入状态，1成功；0失败',
                                            `appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '小程序APPID',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小程序发货信息录入表' ROW_FORMAT = Dynamic;


CREATE TABLE `{{prefix}}delivery_weapp_shipping_split`  (
                                                  `id` int NOT NULL AUTO_INCREMENT,
                                                  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
                                                  `shipping_id` int NOT NULL DEFAULT 0 COMMENT '发货id',
                                                  `trade_id` int NOT NULL DEFAULT 0 COMMENT '交易id',
                                                  `trade_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易类型',
                                                  `trade_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交易单号',
                                                  `split_trade_id` int NOT NULL DEFAULT 0 COMMENT '订单交易id',
                                                  `split_trade_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单交易类型',
                                                  `split_trade_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单交易单号',
                                                  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '原支付交易对应的微信订单号',
                                                  `is_all_delivered` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分拆发货模式时必填，用于标识分拆发货模式下是否已全部发货完成',
                                                  `status` tinyint NOT NULL DEFAULT 0 COMMENT '录入状态，1成功；0失败',
                                                  `errcode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '错误码',
                                                  `errmsg` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '错误原因',
                                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小程序发货信息拆单表' ROW_FORMAT = Dynamic;


CREATE TABLE `{{prefix}}delivery_weapp_shipping_split_item`  (
                                                       `id` int NOT NULL AUTO_INCREMENT,
                                                       `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
                                                       `split_id` int NOT NULL DEFAULT 0 COMMENT '拆单记录id',
                                                       `item_trade_id` int NOT NULL DEFAULT 0 COMMENT '订单项id',
                                                       `trade_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务类型',
                                                       `tracking_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '物流单号',
                                                       `express_company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '物流公司编码',
                                                       `item_image` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品图片',
                                                       `item_desc` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品信息',
                                                       `consignor_contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '寄件人联系方式，示例值: `189****1234',
                                                       `receiver_contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收件人联系方式，示例值: `189****1234',
                                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小程序发货物流信息表' ROW_FORMAT = Dynamic;


CREATE TABLE `{{prefix}}recharge_order`  (
                                   `order_id` int NOT NULL AUTO_INCREMENT,
                                   `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
                                   `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单编号',
                                   `order_from` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单来源',
                                   `order_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单类型',
                                   `out_trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '支付流水号',
                                   `order_status` int NOT NULL DEFAULT 0 COMMENT '订单状态',
                                   `refund_status` int NOT NULL DEFAULT 0 COMMENT '退款状态',
                                   `member_id` int NOT NULL DEFAULT 0 COMMENT '会员id',
                                   `ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '会员ip',
                                   `member_message` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '会员留言信息',
                                   `order_item_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单项目金额',
                                   `order_face_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实际充值到账金额',
                                   `order_give_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '充值赠送金额',
                                   `order_give_point` int NOT NULL DEFAULT 0 COMMENT '充值赠送积分',
                                   `order_give_growth` int NOT NULL DEFAULT 0 COMMENT '充值赠送成长值',
                                   `order_discount_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单优惠金额',
                                   `order_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
                                   `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
                                   `pay_time` int NOT NULL DEFAULT 0 COMMENT '订单支付时间',
                                   `close_time` int NOT NULL DEFAULT 0 COMMENT '订单关闭时间',
                                   `is_delete` int NOT NULL DEFAULT 0 COMMENT '是否删除(针对后台)',
                                   `is_enable_refund` int NOT NULL DEFAULT 0 COMMENT '是否允许退款',
                                   `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商家留言',
                                   `timeout` int NOT NULL DEFAULT 0 COMMENT '通用业务超时时间记录',
                                   `invoice_id` int NOT NULL DEFAULT 0 COMMENT '发票id，0表示不开发票',
                                   `close_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关闭原因',
                                   `settlement_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'wait' COMMENT '结算状态',
                                   `weapp_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '小程序类型，developer为开发版；trial为体验版；formal为正式版；默认为正式版',
                                   PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单表' ROW_FORMAT = Dynamic;


CREATE TABLE `{{prefix}}recharge_order_item`  (
                                        `order_item_id` int NOT NULL AUTO_INCREMENT,
                                        `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
                                        `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
                                        `member_id` int NOT NULL DEFAULT 0 COMMENT '购买会员id',
                                        `item_id` int NOT NULL DEFAULT 0 COMMENT '项目id',
                                        `item_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '项目类型',
                                        `item_name` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '项目名称',
                                        `item_image` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '项目图片',
                                        `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '项目单价',
                                        `num` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '购买数量',
                                        `item_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '项目总价',
                                        `is_refund` int NOT NULL DEFAULT 0 COMMENT '是否退款',
                                        `refund_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退款编号',
                                        `refund_status` int NOT NULL DEFAULT 0 COMMENT '退款状态',
                                        `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
                                        PRIMARY KEY (`order_item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单项表' ROW_FORMAT = Dynamic;


CREATE TABLE `{{prefix}}recharge_order_item_refund`  (
                                               `refund_id` int NOT NULL AUTO_INCREMENT,
                                               `order_item_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
                                               `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
                                               `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单编号',
                                               `refund_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '退款单号',
                                               `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
                                               `member_id` int NOT NULL DEFAULT 0 COMMENT '会员id',
                                               `num` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '退货数量',
                                               `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '总退款',
                                               `status` int NOT NULL DEFAULT 0 COMMENT '退款状态',
                                               `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
                                               `audit_time` int NOT NULL DEFAULT 0 COMMENT '审核时间',
                                               `transfer_time` int NOT NULL DEFAULT 0 COMMENT '转账时间',
                                               `item_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '项目类型',
                                               PRIMARY KEY (`refund_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单退款表' ROW_FORMAT = Dynamic;


CREATE TABLE `{{prefix}}recharge_order_log`  (
                                       `id` int NOT NULL AUTO_INCREMENT,
                                       `order_id` int NOT NULL DEFAULT 0 COMMENT '订单id',
                                       `main_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '操作人类型',
                                       `main_id` int NOT NULL DEFAULT 0 COMMENT '操作人id',
                                       `status` int NOT NULL DEFAULT 0 COMMENT '订单状态',
                                       `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
                                       `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '日志内容',
                                       `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单日志表' ROW_FORMAT = Dynamic;


CREATE TABLE `{{prefix}}recharge_package`  (
                                     `package_id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                                     `site_id` int NOT NULL DEFAULT 0 COMMENT '站点ID',
                                     `package_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '套餐名称',
                                     `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '充值金额',
                                     `give_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '赠送金额',
                                     `point` int NOT NULL DEFAULT 0 COMMENT '赠送积分',
                                     `growth` int NOT NULL DEFAULT 0 COMMENT '赠送成长值',
                                     `gift_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '赠品json',
                                     `sale_num` int NOT NULL DEFAULT 0 COMMENT '发放数量',
                                     `sort` int NOT NULL DEFAULT 0 COMMENT '排序号',
                                     `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0关闭 1开启）',
                                     `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
                                     `update_time` int NOT NULL DEFAULT 0 COMMENT '修改时间',
                                     PRIMARY KEY (`package_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值套餐表' ROW_FORMAT = Dynamic;


CREATE TABLE `{{prefix}}sys_adminapp`  (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `uid` int NOT NULL DEFAULT 0 COMMENT '用户id',
                                 `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
                                 `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '设置项类型  app  应用  stat统计  todo 待办事项',
                                 `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置数据',
                                 `create_time` int NOT NULL DEFAULT 0 COMMENT '添加时间',
                                 `update_time` int NOT NULL DEFAULT 0 COMMENT '添加时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `create_time`(`create_time` ASC) USING BTREE,
                                 INDEX `site_id`(`site_id` ASC) USING BTREE,
                                 INDEX `uid`(`uid` ASC) USING BTREE,
                                 INDEX `type`(`type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户手机管理端偏好设置表' ROW_FORMAT = Dynamic;