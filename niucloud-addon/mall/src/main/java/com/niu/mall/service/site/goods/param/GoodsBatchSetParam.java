package com.niu.mall.service.site.goods.param;

import cn.hutool.json.JSONArray;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 商品批量设置参数
 */
@Data
public class GoodsBatchSetParam {
        /**
         * 是否全选：0-指定商品，1-全选
         */
        @NotNull(message = "是否全选参数不能为空")
        private Integer isAll;
        
        /**
         * 商品ID列表（当isAll=0时必填）
         */
        private List<Integer> goodsIds;
        
        /**
         * 设置的值
         */
        private SetValue setValue;
        
        /**
         * 设置类型
         */
        @NotBlank(message = "设置类型不能为空")
        private String setType;
        
        /**
         * 查询条件（当isAll=1时使用）
         */
        private Where where;

        @Data
        public static class SetValue {
            private List<Integer> labelIds;
            private List<Integer> serviceIds;
            private Object posterId;
            private Object formId;
            private Object brandId;
            private List<String> goodsCategory;
            private List<String> deliveryType;
            private Object deliveryTemplateId;
            private Integer deliveryMoney;
            private Object feeType;
            private Integer isFreeShipping;
            private Object memberDiscount;
            private String stock;
            private String stockType;
            private Integer virtualSaleNum;
            private Object mallGoodsCategory;
        }

        @Data
        public static class Where {
            private String goodsName;
            private List<String> goodsCategory;
            private String goodsType;
            private String brandId;
            private String labelIds;
            private String goodsSource;
            private String order;
            private String sort;
            private String startPrice;
            private String endPrice;
            private String startSaleNum;
            private String endSaleNum;
            private String status;
        }

}
