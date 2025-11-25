package com.niu.core.service.admin.goods.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsAttr列表视图
 */
@Data
public class GoodsAttrListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer attrId;  // 商品参数id
    private Integer siteId;  // 站点id
    private String attrName;  // 参数名称
    private String attrValueFormat; // 参数值，json格式
    private Integer sort;  // 参数排序号

}
