package com.niu.core.service.site.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品服务参数
 */
@Data
public class GoodsServiceSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceName;

    /** 插件标识，用于筛选适用该插件的商品服务 */
    private String addon;

}
