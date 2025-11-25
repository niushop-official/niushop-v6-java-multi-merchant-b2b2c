package com.niu.core.service.admin.shop.param;

import cn.hutool.json.JSONArray;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 店铺扩展表参数
 */
@Data
public class SiteShopSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 关键字 */
    private String keyword;

    /** 状态 */
    private Integer status;

    /** 所属分组 */
    private Integer groupId;

    /** 分类 */
    private Integer categoryId;

    /** 是否自营 */
    private Integer isSelf;

    /** 到期时间 */
    private String[] expireTime;

    /** 申请时间 */
    private String[] createTime;

    /** 应用 */
    private String app;

}
