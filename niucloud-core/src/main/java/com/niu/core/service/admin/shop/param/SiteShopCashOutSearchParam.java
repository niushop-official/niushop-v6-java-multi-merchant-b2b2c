package com.niu.core.service.admin.shop.param;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商家站点提现表参数
 */
@Data
public class SiteShopCashOutSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 提现交易号 */
    private String cashOutNo;

    /** 申请时间 */
    private String[] createTime;

    /** 审核时间 */
    private String[] auditTime;

    /** 转账时间 */
    private String[] transferTime;

    /** 转账提现类型 */
    private String transferType;

    /** 状态（1待审核 2待转账 3已转账 -1拒绝 -2 已取消） */
    private Integer status;

    private String keyword;

}
