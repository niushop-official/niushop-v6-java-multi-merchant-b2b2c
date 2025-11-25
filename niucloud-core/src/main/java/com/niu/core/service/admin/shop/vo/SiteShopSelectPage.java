package com.niu.core.service.admin.shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.service.admin.site.vo.SiteAdminVo;
import io.swagger.models.auth.In;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
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
