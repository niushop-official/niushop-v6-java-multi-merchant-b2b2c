package com.niu.core.service.api.shop.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.core.entity.site.Site;
import com.niu.core.service.admin.web.vo.WebConfigVo;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WebShopListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("config")
    private WebConfigVo webConfigVo;

    private List<SiteWithShopInfo> list;
    
    @Data
    public static class SiteWithShopInfo extends Site {
        
        @JsonProperty("goods_list")
        private List<MallGoodsListVo> goodsList;
        
        private ShopInfo shop;
        
        @Data
        public static class ShopInfo {
            @JsonProperty("is_self")
            private Integer isSelf;
        }
    }
}
