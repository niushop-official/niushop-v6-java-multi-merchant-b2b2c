package com.niu.mall.service.core.goods.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsCategoryConfigVo视图
 */
@Data
public class ShopGoodsCategoryConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer level = 2;
    private String template = "style-1";
    private Goods goods = new Goods();
    private String sort = "default";

    @Data
    public static class Goods {
        private String style = "single-cols";
    }
}
