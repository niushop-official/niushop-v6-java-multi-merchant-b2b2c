package com.niu.core.event.fenxiao.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分销商品详情（含规格列表）
 */
@Data
public class FenxiaoGoodsDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规格列表
     */
    private List<FenxiaoGoodsSkuVo> skuList;
}
