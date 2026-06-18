package com.niu.mall.service.api.marketing.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.niu.mall.entity.active.MallActive;
import com.niu.mall.entity.active.MallActiveGoods;
import com.niu.mall.enums.active.ActiveStatusEnum;
import com.niu.mall.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.mapper.active.MallActiveMapper;
import com.niu.mall.service.api.marketing.IMallDiscountService;
import com.niu.mall.service.api.marketing.param.MallDiscountListParam;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsInfoVo;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsListVo;
import com.niu.mall.service.api.marketing.vo.MallDiscountListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
     *
     * @param pageParam
     * @param activeId
     */
    @Override
    public PageResult<MallDiscountGoodsListVo> getGoodsPage(PageParam pageParam, Integer activeId) {
        MPJQueryWrapper<MallActiveGoods> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("ag")
                .innerJoin("?_mall_goods g ON ag.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_mall_goods_sku gs ON ag.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_mall_goods_site gsite ON g.goods_id = gsite.goods_id AND gsite.site_id = ag.site_id".replace("?_", GlobalConfig.tablePrefix));

        wrapper.select("ag.*",
                "g.goods_name", "g.goods_type", "g.goods_image", "g.goods_cover",
                "gs.sku_id", "gs.sku_name", "gs.price", "gs.stock", "gs.sku_image",
                "gsite.sale_num AS sale_num");  // 可选，如果需要销量

        wrapper.eq("ag.active_id", activeId);
        // 如果只需要进行中的活动商品，可以添加：
        wrapper.eq("ag.active_goods_status", ActiveStatusEnum.ACTIVE.getStatus());

        IPage<MallDiscountGoodsListVo> iPage = mallActiveGoodsMapper.selectJoinPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()),
                MallDiscountGoodsListVo.class, wrapper);

        // 处理收益字段和虚拟字段
        for (MallDiscountGoodsListVo vo : iPage.getRecords()) {
            // 3. 组装 goodsSku 对象（存放 SKU 相关信息）
            MallDiscountGoodsListVo.GoodsSku skuVo = new MallDiscountGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(vo, skuVo);  // 需要确保 vo 中有 skuId、skuName、price、stock、skuImage 等字段
            // 如果活动有折扣价格，可能需要从 JSON 中解析（视业务需求）
            // 但 PHP 版直接返回原价，不涉及折扣信息，所以这里也返回原价
            vo.setGoodsSku(skuVo);

            // 4. 组装 activeGoods 对象（存放活动商品状态等）
            MallDiscountGoodsListVo.ActiveGoods activeGoodsVo = new MallDiscountGoodsListVo.ActiveGoods();
            activeGoodsVo.setActiveGoodsStatus(vo.getActiveGoodsStatus());
            // 若有其他活动相关字段，可继续填充
            vo.setActiveGoods(activeGoodsVo);
        }
        return PageResult.build(iPage);
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
     *
     * @param id 活动ID
     * @return Map，包含活动信息及 site 信息，且保证 id 字段存在
     */
    @Override
    public Map<String, Object> detail(Integer id) {
        // 1. 查询活动主表
        MallActive active = mallActiveMapper.selectById(id);
        if (active == null) {
            return new HashMap<>();
        }

        // 2. 转为 Map，便于添加额外字段
        Map<String, Object> info = BeanUtil.beanToMap(active);

        // 3. 查询站点信息（假设 Active 中有 site_id 字段）
        if (active.getSiteId() != null) {
            Site site = siteMapper.selectById(active.getSiteId());
            if (site != null) {
                Map<String, Object> siteInfo = new HashMap<>();
                siteInfo.put("site_name", site.getSiteName());
                siteInfo.put("site_id", site.getSiteId());
                siteInfo.put("logo", site.getLogo());
                info.put("site", siteInfo);
            }
        }

        // 4. 将 active_id 值赋给 id 键（原 PHP 中添加了 id 字段）
        info.put("id", active.getActiveId()); // 假设 active_id 字段名为 activeId

        return info;
    }

}
