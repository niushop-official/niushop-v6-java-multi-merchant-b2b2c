package com.niu.mall.service.admin.goods.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ShopGoods列表视图
 */
@Data
public class MallSelectGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long currentPage;

    // 每页大小
    private long perPage;

    // 总记录数
    private long total;

    // 记录结果
    private List<ListVo> data;

    private List<Integer> verifyGoodsIds;

    private List<Integer> verifySkuIds;

    @Data
    public static class ListVo {
        private Integer goodsId;  // 商品id
        private Integer skuId;  // skuid
        private Integer siteId;  // 站点id
        private String  siteName; //站点名称
        private String goodsName;  // 商品名称
        private String goodsType;  // 商品类型
        private String goodsCover;  // 商品封面
        private String goodsImage;
        private String goodsCoverThumbSmall;
        private String goodsCoverThumbMid;
        private Integer saleNum;  // 销量
        private Integer status;  // 商品状态（1.正常0下架）
        private Integer stock;
        private Integer sort;  // 排序
        private String goodsTypeName;
        private String goodsEditPath;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime; // 创建时间
        @JsonProperty("goodsSku")
        private GoodsSku goodsSku;
        @JsonProperty("skuList")
        private List<MallGoodsSku> skuList;

        @JsonProperty("site")
        private Site site;

        public String getGoodsTypeName() {
            return GoodsTypeEnum.getNameByType(this.goodsType);
        }

        public String getGoodsEditPath() {
            Map<String, Map<String, Object>> map = GoodsTypeEnum.getMap();
            return map.get(this.goodsType).get("path").toString();
        }
    }

    @Data
    public static class GoodsSku {
        private Integer goodsId;
        private Integer skuId;
        private String skuName;
        private String skuSpecFormat;
        private BigDecimal price;
        private Integer stock;
        private BigDecimal marketPrice; // 划线价
        private BigDecimal salePrice; // 实际卖价（有活动显示活动价，默认原价）
        private BigDecimal costPrice; // sku成本价
        private BigDecimal weight; // 重量（单位kg）
        private BigDecimal volume; // 体积（单位立方米）
        private Integer saleNum; // 销量
        private Integer isDefault; // 是否默认
        private String memberPrice; // 会员价，json格式，指定会员价，数据结构为：{"level_1":"10.00","level_2":"10.00"}
    }


    @Data
    public static class Site   {

        private static final long serialVersionUID = 1L;

        /** 主键 */
        @TableId(value="site_id", type= IdType.AUTO)
        private Integer siteId;

        /** 站点名称 */
        private String siteName;

        /** 分组ID(0:不限制) */
        private Integer groupId;

        /** 关键字 */
        private String keywords;

        /** 站点类型 */
        private String appType;

        /** 站点logo */
        private String logo;

        /** 简介 */
        @TableField(value = "`desc`")
        private String desc;

        /** 状态 1-正常 0-体验期 2-已到期 */
        private Integer status;

        /** 纬度 */
        private String latitude;

        /** 经度 */
        private String longitude;

        /** 省 */
        private Integer provinceId;

        /** 市 */
        private Integer cityId;

        /** 区 */
        private Integer districtId;

        /** 详细地址 */
        private String address;

        /** 完整地址 */
        private String fullAddress;

        /** 客服电话 */
        private String phone;

        /** 营业时间 */
        private String businessHours;

        /** 创建时间 */
        private Long createTime;

        /** 到期时间（如果是0 无限期） */
        private Long expireTime; //

        /** 前台名称 */
        private String frontEndName;

        /** 前台logo */
        private String frontEndLogo;

        /** 前台icon */
        private String frontEndIcon;

        /** 网站图标 */
        private String icon;

        /** 最大会员码值 */
        private Integer memberNo;

        /** 站点应用 */
        private String app;

        /** 站点包含的插件 */
        private String addons;

        /** 站点已执行初始化方法的插件 */
        private String initalledAddon;

        /** 站点域名 */
        private String siteDomain;

        private Integer isinit;

    }
}
