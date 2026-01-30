package com.niu.core.service.site.goods.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品品牌参数
 */
@Data
public class GoodsBrandSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String brandName;

    private Integer auditStatus;

    private Integer status;

    private JSONArray brandIds;

}
