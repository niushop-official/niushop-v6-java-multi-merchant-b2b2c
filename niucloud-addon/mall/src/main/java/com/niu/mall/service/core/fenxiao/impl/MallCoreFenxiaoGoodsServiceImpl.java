package com.niu.mall.service.core.fenxiao.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageResult;
import com.niu.core.event.fenxiao.param.FenxiaoGoodsListParam;
import com.niu.core.event.fenxiao.vo.FenxiaoGoodsDetailVo;
import com.niu.core.event.fenxiao.vo.FenxiaoGoodsSelectVo;
import com.niu.core.event.fenxiao.vo.FenxiaoGoodsSkuVo;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.core.fenxiao.IMallCoreFenxiaoGoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商城分销商品服务实现
 */
@Service
public class MallCoreFenxiaoGoodsServiceImpl implements IMallCoreFenxiaoGoodsService {

    @Resource
    MallGoodsMapper mallGoodsMapper;
    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    /**
     * 获取商城分销可选商品列表
     *
     * @param param 查询参数
     * @return PageResult<FenxiaoGoodsSelectVo>
     */
    @Override
    public PageResult<FenxiaoGoodsSelectVo> getGoodsList(FenxiaoGoodsListParam param) {
        // 解析分页参数
        int page = param.getPage() != null ? param.getPage() : 1;
        int limit = param.getLimit() != null ? param.getLimit() : 10;
        Integer siteId = param.getSiteId();

        // MPJ联表：商品 + 默认SKU + 站点
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("g.goods_id,g.site_id,g.goods_name,g.goods_cover,g.stock,g.sale_num,g.status,gs.sku_id,gs.price,s.site_name")
                .setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site s ON g.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("g.status", 1)
                .eq("g.audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus())
                .eq("g.delete_time", 0);
        if (siteId != null) {
            queryWrapper.eq("g.site_id", siteId);
        }
        if (ObjectUtil.isNotEmpty(param.getGoodsName())) {
            queryWrapper.like("g.goods_name", param.getGoodsName());
        }
        if (ObjectUtil.isNotEmpty(param.getExcludeGoodsIds())) {
            queryWrapper.notIn("g.goods_id", param.getExcludeGoodsIds());
        }
        if (ObjectUtil.isNotEmpty(param.getGoodsIds())) {
            queryWrapper.in("g.goods_id", param.getGoodsIds());
        }
        queryWrapper.orderByDesc("g.create_time");

        // 分页查询并映射VO
        IPage<Map<String, Object>> iPage = mallGoodsMapper.selectJoinMapsPage(new Page<>(page, limit), queryWrapper);
        List<FenxiaoGoodsSelectVo> goodsList = new ArrayList<>();
        for (Map<String, Object> item : iPage.getRecords()) {
            FenxiaoGoodsSelectVo goods = new FenxiaoGoodsSelectVo();
            goods.setGoodsId(toInteger(item.get("goods_id")));
            goods.setSiteId(toInteger(item.get("site_id")));
            goods.setGoodsName(toString(item.get("goods_name")));
            goods.setGoodsCover(toString(item.get("goods_cover")));
            goods.setStock(toInteger(item.get("stock")));
            goods.setSaleNum(toInteger(item.get("sale_num")));
            goods.setStatus(toInteger(item.get("status")));
            goods.setSkuId(toInteger(item.get("sku_id")));
            goods.setPrice(toBigDecimal(item.get("price")));
            goods.setCalculatePrice(goods.getPrice());
            // 填充站点信息
            FenxiaoGoodsSelectVo.SiteInfo siteInfo = new FenxiaoGoodsSelectVo.SiteInfo();
            siteInfo.setSiteId(goods.getSiteId());
            siteInfo.setSiteName(toString(item.get("site_name")));
            goods.setSite(siteInfo);
            goodsList.add(goods);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(goodsList);
    }

    /**
     * 获取商城分销商品详情（含规格列表）
     *
     * @param param 查询参数，goodsId 必填
     * @return FenxiaoGoodsDetailVo
     */
    @Override
    public FenxiaoGoodsDetailVo getGoodsInfo(FenxiaoGoodsListParam param) {
        FenxiaoGoodsDetailVo detailVo = new FenxiaoGoodsDetailVo();
        Integer goodsId = param.getGoodsId();
        // 无商品ID时返回空规格列表
        if (goodsId == null) {
            detailVo.setSkuList(new ArrayList<>());
            return detailVo;
        }
        // 查询全部规格并转换VO
        List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", goodsId));
        List<FenxiaoGoodsSkuVo> skuVoList = new ArrayList<>();
        for (MallGoodsSku sku : skuList) {
            FenxiaoGoodsSkuVo skuVo = new FenxiaoGoodsSkuVo();
            skuVo.setSkuId(sku.getSkuId() != null ? sku.getSkuId().longValue() : null);
            skuVo.setGoodsId(sku.getGoodsId());
            skuVo.setSiteId(sku.getSiteId());
            skuVo.setSkuName(sku.getSkuName());
            skuVo.setSkuImage(sku.getSkuImage());
            skuVo.setSkuNo(sku.getSkuNo());
            skuVo.setSkuSpecFormat(sku.getSkuSpecFormat());
            skuVo.setPrice(sku.getPrice());
            skuVo.setMarketPrice(sku.getMarketPrice());
            skuVo.setSalePrice(sku.getSalePrice());
            skuVo.setCostPrice(sku.getCostPrice());
            skuVo.setStock(sku.getStock());
            skuVo.setIsDefault(sku.getIsDefault());
            skuVoList.add(skuVo);
        }
        detailVo.setSkuList(skuVoList);
        return detailVo;
    }

    /**
     * Map值转Integer
     *
     * @param value 原始值
     * @return Integer
     */
    private Integer toInteger(Object value) {
        return value != null ? Integer.parseInt(value.toString()) : null;
    }

    /**
     * Map值转String
     *
     * @param value 原始值
     * @return String
     */
    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }

    /**
     * Map值转BigDecimal
     *
     * @param value 原始值
     * @return BigDecimal
     */
    private BigDecimal toBigDecimal(Object value) {
        return value != null ? new BigDecimal(value.toString()) : null;
    }
}
