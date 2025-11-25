package com.niu.core.service.api.shop.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
* 店铺扩展表列表
*/
@Data
public class SiteShopSelectPage implements Serializable {

private static final long serialVersionUID = 1L;
        // 当前请求页
        private long currentPage;

        // 每页大小
        private long perPage;

        // 总记录数
        private long total;
        // 记录结果
        private List<SiteShopListVo> data;

        private List<Integer> verifySiteIds;


}
