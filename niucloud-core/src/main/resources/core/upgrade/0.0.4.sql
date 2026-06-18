CREATE TABLE `{{prefix}}member_import`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
    `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
    `main_id` int NOT NULL DEFAULT 0 COMMENT '操作人id',
    `status` int NOT NULL DEFAULT 1 COMMENT '状态 进行中  已完成  已失败',
    `total_num` int NOT NULL DEFAULT 0 COMMENT '总导入数',
    `success_num` int NOT NULL DEFAULT 0 COMMENT '成功导入数',
    `fail_num` int NOT NULL DEFAULT 0 COMMENT '失败导入数',
    `repeat_num` int NOT NULL DEFAULT 0 COMMENT '手机号重复数',
    `data` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '导入文件的路径',
    `output` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对外输出记录',
    `fail_output` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '失败记录',
    `fail_remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '失败原因',
    `duration` int NOT NULL DEFAULT 0 COMMENT '耗时',
    `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
    `update_time` int NOT NULL DEFAULT 0 COMMENT '操作时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员导入记录表' ROW_FORMAT = DYNAMIC;

CREATE TABLE `{{prefix}}member_visit`  (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `member_id` int NOT NULL DEFAULT 0 COMMENT '会员id',
    `visit_time` int NOT NULL DEFAULT 0 COMMENT '访问时间',
    `site_id` int NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员访问日志表' ROW_FORMAT = Dynamic;

ALTER TABLE `{{prefix}}delivery_store` MODIFY COLUMN `support_delivery` text NULL COMMENT '支持的配送服务商';

ALTER TABLE `{{prefix}}delivery_store` MODIFY COLUMN `extend_data` text NULL COMMENT '配送服务商扩展数据';

ALTER TABLE `{{prefix}}member` ADD COLUMN `is_old` tinyint NOT NULL DEFAULT 0 COMMENT '是否老用户，1是 0否';

ALTER TABLE `{{prefix}}site_payment` ADD COLUMN `audit_status` int NOT NULL DEFAULT 0 COMMENT '审核状态 1 审核中 2 审核成功  3 审核失败';

ALTER TABLE `{{prefix}}site_payment` ADD COLUMN `audit_fail_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '失败原因';

ALTER TABLE `{{prefix}}site_payment` MODIFY COLUMN `extend_no` varchar(255) NOT NULL DEFAULT '' COMMENT '扩展编号';

ALTER TABLE `{{prefix}}site_payment` MODIFY COLUMN `is_settle` int NOT NULL DEFAULT 1 COMMENT '是否结算';

ALTER TABLE `{{prefix}}site_payment` MODIFY COLUMN `withdrawal_no` varchar(255) NOT NULL DEFAULT '' COMMENT '取现编号';

ALTER TABLE `{{prefix}}site_payment` MODIFY COLUMN `settle_desc` varchar(1000) NOT NULL DEFAULT '' COMMENT '结算开通情况';

ALTER TABLE `{{prefix}}site_payment_apply` MODIFY COLUMN `status` varchar(255) NOT NULL DEFAULT '' COMMENT '状态  1.待审核 2.审核中 3.审核通过 -1.审核失败';

ALTER TABLE `{{prefix}}site_payment_apply` MODIFY COLUMN `apply_time` int NOT NULL DEFAULT 0 COMMENT '提交时间';

ALTER TABLE `{{prefix}}site_shop_apply` ADD COLUMN `form_record_id` int NOT NULL DEFAULT 0 COMMENT '万能表单记录id';

ALTER TABLE `{{prefix}}site_shop_member` ADD COLUMN `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间';

ALTER TABLE `{{prefix}}site_shop_member` ADD COLUMN `is_old` tinyint NOT NULL DEFAULT 0 COMMENT '是否老用户，1是 0否';

ALTER TABLE `{{prefix}}sys_export` ADD COLUMN `success_num` int NOT NULL DEFAULT 0 COMMENT '导出成功数量';

ALTER TABLE `{{prefix}}sys_export` ADD COLUMN `fail_num` int NOT NULL DEFAULT 0 COMMENT '导出失败数量';

ALTER TABLE `{{prefix}}sys_export` ADD COLUMN `search_condition` varchar(2000) NOT NULL DEFAULT '' COMMENT '导出查询条件';

ALTER TABLE `{{prefix}}sys_export` ADD COLUMN `data_column` longtext NULL COMMENT '导出数据列';

ALTER TABLE `{{prefix}}article` MODIFY COLUMN `content` longtext NULL COMMENT '文章内容';

ALTER TABLE `{{prefix}}sys_agreement` MODIFY COLUMN `content` longtext NULL COMMENT '协议内容';
