package com.niu.core.service.api.shop.param;

import lombok.Data;

/**
 * 商户入驻申请搜索参数
 */
@Data
public class SiteApplySearchParam {

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 行业分类id
     */
    private Integer categoryId;

    /**
     * 套餐id
     */
    private Integer groupId;

    /**
     * 创建时间
     */
    private String[] createTime;

    /**
     * 申请会员id
     */
    private Integer memberId;
}
