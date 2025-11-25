package com.niu.mall.service.api.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.mall.entity.active.MallActive;
import com.niu.mall.entity.active.MallActiveGoods;
import com.niu.mall.enums.active.ActiveStatusEnum;
import com.niu.mall.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.mapper.active.MallActiveMapper;
import com.niu.mall.service.api.marketing.IMallDiscountService;
import com.niu.mall.service.api.marketing.param.DiscountGoodsSearchParam;
import com.niu.mall.service.api.marketing.param.MallDiscountListParam;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsInfoVo;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsListVo;
import com.niu.mall.service.api.marketing.vo.MallDiscountListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IMallDiscountServiceImpl implements IMallDiscountService {

    @Resource
    MallActiveMapper mallActiveMapper;

    @Resource
    MallActiveGoodsMapper mallActiveGoodsMapper;

    @Resource
    ICoreConfigService coreConfigService;

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
     * @param searchParam
     */
    @Override
    public PageResult<MallDiscountGoodsListVo> getGoodsPage(PageParam pageParam, DiscountGoodsSearchParam searchParam) {
        MPJQueryWrapper<MallActiveGoods> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("ag")
                .innerJoin("?_shop_goods g ON ag.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix));
        wrapper.select("g.*,ag.active_goods_value,ag.active_goods_status");
        wrapper.in("ag.active_goods_status", new String[]{ ActiveStatusEnum.ACTIVE.getStatus(), ActiveStatusEnum.NOT_ACTIVE.getStatus() });
        wrapper.eq("ag.active_class", "discount");
        wrapper.eq("ag.active_id", searchParam.getActiveId());

        IPage<MallDiscountGoodsListVo> iPage = mallActiveGoodsMapper.selectJoinPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), MallDiscountGoodsListVo.class, wrapper);
        for (MallDiscountGoodsListVo vo: iPage.getRecords()) {
            JSONArray activeGoodsValue = JSONUtil.parseArray(vo.getActiveGoodsValue());
            activeGoodsValue.toList(JSONObject.class).stream().filter(i -> i.getInt("is_enabled") == 1).collect(Collectors.toList()).sort(Comparator.comparingDouble(o -> ((JSONObject)o).getDouble("discount_price")));
            if (activeGoodsValue.size() > 0) {
                JSONObject sku = activeGoodsValue.getJSONObject(0);
                MallDiscountGoodsListVo.GoodsSku skuVo = JSONUtil.toBean(sku, MallDiscountGoodsListVo.GoodsSku.class);
                skuVo.setActiveDiscountPrice(ObjectUtil.defaultIfNull(sku.getBigDecimal("discount_price"), skuVo.getSalePrice()));
                skuVo.setActiveDiscountRate(sku.getBigDecimal("discount_rate"));
                skuVo.setActiveDiscountType(sku.getStr("discount_type"));
                skuVo.setActiveReduceMoney(sku.getBigDecimal("reduce_money"));
                skuVo.setActiveSpecifyPrice(sku.getBigDecimal("specify_price"));
                vo.setGoodsSku(skuVo);
            }

            MallDiscountGoodsListVo.ActiveGoods activeGoodsVo = new MallDiscountGoodsListVo.ActiveGoods();
            activeGoodsVo.setActiveGoodsStatus(vo.getActiveGoodsStatus());
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
        if(ObjectUtil.isNotEmpty(mallDiscountListParam.getActiveName()))
        {
            queryWrapper.like("active_name", mallDiscountListParam.getActiveName());
        }
        if(ObjectUtil.isNotEmpty(mallDiscountListParam.getLimit()))
        {
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
}
