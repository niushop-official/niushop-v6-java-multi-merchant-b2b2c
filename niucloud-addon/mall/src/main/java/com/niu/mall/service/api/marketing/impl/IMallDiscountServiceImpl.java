package com.niu.mall.service.api.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.mall.common.MarketingJsonMapper;
import com.niu.mall.entity.active.MallActive;
import com.niu.core.entity.active.MallActiveGoods;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.core.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.mapper.active.MallActiveMapper;
import com.niu.mall.service.api.marketing.IMallDiscountService;
import com.niu.mall.service.api.marketing.param.MallDiscountListParam;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsInfoVo;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsPageVo;
import com.niu.mall.service.api.marketing.vo.MallDiscountListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class IMallDiscountServiceImpl implements IMallDiscountService {

    @Resource
    MallActiveMapper mallActiveMapper;

    @Resource
    MallActiveGoodsMapper mallActiveGoodsMapper;

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    private SiteMapper siteMapper;

    @Override
    public MallDiscountGoodsInfoVo getInfoByGoods(Integer goodsId) {
        MallActiveGoods shopActiveGoods = mallActiveGoodsMapper.selectOne(new QueryWrapper<MallActiveGoods>()
                .eq("active_goods_status", "active")
                .eq("goods_id", goodsId)
                .eq("active_class", "discount")
                .last("limit 1")
        );
        MallDiscountGoodsInfoVo vo = new MallDiscountGoodsInfoVo();
        if (shopActiveGoods != null) {
            BeanUtils.copyProperties(shopActiveGoods, vo);
            vo.setActive(mallActiveMapper.selectById(shopActiveGoods.getActiveId()));
            return vo;
        }
        return null;
    }

    /**
     * 限时折扣配置
     *
     * @return
     */
    @Override
    public JSONArray getDiscountBannerConfig() {
        return coreConfigService.getConfigArrayValue(RequestUtils.defaultSiteId(), "SHOP_DISCOUNT_BANNER_CONFIG");
    }

    /**
     * 获取活动商品列表
     * 对齐 PHP DiscountService::getGoodsPage：mall_active_goods + mall_goods_sku + mall_goods，不使用 mall_goods_site
     */
    @Override
    public PageResult<MallDiscountGoodsPageVo> getGoodsPage(PageParam pageParam, Integer activeId) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallActiveGoods> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("ag")
                .innerJoin("?_mall_goods_sku gs ON ag.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_mall_goods g ON gs.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix));
        wrapper.select("ag.goods_id",
                "gs.sku_id", "gs.sku_name", "gs.sku_image",
                "gs.stock", "gs.price",
                "g.goods_type", "g.goods_name");
        wrapper.eq("ag.active_id", activeId);

        IPage<MallDiscountGoodsPageVo> iPage = mallActiveGoodsMapper.selectJoinPage(
                new Page<>(page, limit), MallDiscountGoodsPageVo.class, wrapper);

        List<MallDiscountGoodsPageVo> goodsData = new ArrayList<>();
        for (MallDiscountGoodsPageVo row : iPage.getRecords()) {
            String goodsType = row.getGoodsType() == null ? "" : row.getGoodsType();
            goodsData.add(MallDiscountGoodsPageVo.builder()
                    .goodsId(row.getGoodsId())
                    .skuId(row.getSkuId())
                    .skuName(row.getSkuName())
                    .skuImage(row.getSkuImage())
                    .stock(row.getStock())
                    .price(row.getPrice())
                    .goodsTypeName(GoodsTypeEnum.getNameByType(goodsType))
                    .goodsType(goodsType)
                    .goodsName(row.getGoodsName())
                    .build());
        }
        return PageResult.build(page, limit, iPage.getTotal(), goodsData);
    }

    /**
     * 限时折扣列表
     *
     * @param mallDiscountListParam
     * @return
     */
    @Override
    public List<MallDiscountListVo> getList(MallDiscountListParam mallDiscountListParam) {
        QueryWrapper<MallActive> queryWrapper = new QueryWrapper();
        queryWrapper.in("active_status", Arrays.asList("active", "not_active"))
                .eq("active_class", "discount");
        if (ObjectUtil.isNotEmpty(mallDiscountListParam.getActiveName())) {
            queryWrapper.like("active_name", mallDiscountListParam.getActiveName());
        }
        if (ObjectUtil.isNotEmpty(mallDiscountListParam.getLimit())) {
            queryWrapper.last("limit " + mallDiscountListParam.getLimit());
        }
        queryWrapper.orderByAsc("start_time");
        List<MallActive> shopActiveList = mallActiveMapper.selectList(queryWrapper);
        List<MallDiscountListVo> list = new LinkedList<>();
        for (MallActive item : shopActiveList) {
            MallDiscountListVo vo = new MallDiscountListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 单条数据详情
     * 对齐 PHP DiscountService::detail：snake_case 字段 + site + id
     */
    @Override
    public Map<String, Object> detail(Integer id) {
        MallActive active = mallActiveMapper.selectById(id);
        if (active == null) {
            return new HashMap<>();
        }

        Map<String, Object> info = MarketingJsonMapper.toSnakeCaseMap(active);

        if (active.getSiteId() != null) {
            Site site = siteMapper.selectById(active.getSiteId());
            if (site != null) {
                Map<String, Object> siteInfo = new LinkedHashMap<>();
                siteInfo.put("site_name", site.getSiteName());
                siteInfo.put("site_id", site.getSiteId());
                siteInfo.put("logo", site.getLogo());
                info.put("site", siteInfo);
            }
        }

        info.put("id", active.getActiveId());
        // 保证前端可读到 start_time / end_time（秒级时间戳，外层再格式化为 Y-m-d H:i）
        info.put("start_time", active.getStartTime() == null ? 0L : active.getStartTime());
        info.put("end_time", active.getEndTime() == null ? 0L : active.getEndTime());

        return info;
    }

}
