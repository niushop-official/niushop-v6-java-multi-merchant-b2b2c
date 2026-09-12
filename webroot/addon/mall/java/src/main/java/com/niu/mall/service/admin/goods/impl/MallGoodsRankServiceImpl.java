package com.niu.mall.service.admin.goods.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsRank;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.mall.enums.goods.GoodsRankTypeEnum;
import com.niu.mall.enums.goods.GoodsRuleTypeEnum;
import com.niu.mall.enums.goods.GoodsSourceTypeEnum;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsRankMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.admin.goods.IMallGoodsRankService;
import com.niu.mall.service.admin.goods.param.*;
import com.niu.mall.service.admin.goods.vo.*;
import com.niu.mall.service.core.goods.ICoreGoodsRankService;
import com.niu.mall.service.core.goods.param.ShopGoodsRankConfigParam;
import com.niu.mall.service.core.goods.vo.ShopGoodsRankConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 商品排行榜实现
 */
@Service
@RequiredArgsConstructor
public class MallGoodsRankServiceImpl implements IMallGoodsRankService {
    private final MallGoodsMapper mallGoodsMapper;
    private final MallGoodsSkuMapper mallGoodsSkuMapper;
    private final MallGoodsRankMapper mallGoodsRankMapper;
    private final ICoreGoodsRankService coreGoodsRankService;

    @Override
    public PageResult<MallGoodsRankListVo> page(PageParam pageParam, MallGoodsRankSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getOrder())) {
            queryWrapper.orderBy(true, "desc".equalsIgnoreCase(searchParam.getSort()), searchParam.getOrder());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getName())) {
            queryWrapper.like("name", searchParam.getName());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getRankType())) {
            queryWrapper.eq("rank_type", searchParam.getRankType());
        }

        IPage<MallGoodsRank> iPage = mallGoodsRankMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallGoodsRankListVo> list = new ArrayList<>(CollectionUtil.size(iPage.getRecords()));
        for (MallGoodsRank item : iPage.getRecords()) {
            MallGoodsRankListVo vo = new MallGoodsRankListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setRankTypeName(GoodsRankTypeEnum.getNameByValue(item.getRankType()));
            vo.setGoodsSourceName(GoodsSourceTypeEnum.getNameByValue(item.getGoodsSource()));
            vo.setRuleTypeName(GoodsRuleTypeEnum.getNameByValue(item.getRuleType()));
            vo.setShowGoodsNum(checkRankData(item));
            vo.setGoodsJson(ObjectUtil.isEmpty(item.getGoodsJson()) ? new JSONArray() : JSONUtil.parseArray(item.getGoodsJson()));
            vo.setBrandIds(ObjectUtil.isEmpty(item.getBrandIds()) || !item.getBrandIds().startsWith("[") ? new JSONArray() : JSONUtil.parseArray(item.getBrandIds()));
            vo.setCategoryIds(ObjectUtil.isEmpty(item.getCategoryIds())|| !item.getCategoryIds().startsWith("[") ? new JSONArray() : JSONUtil.parseArray(item.getCategoryIds()));
            vo.setLabelIds(ObjectUtil.isEmpty(item.getLabelIds())|| !item.getLabelIds().startsWith("[") ? new JSONArray() : JSONUtil.parseArray(item.getLabelIds()));
            vo.setCreateTime(DateUtils.timestampToString(item.getCreateTime()));
            list.add(vo);
        }
        return PageResult.build(iPage, list);
    }

    @Override
    public Integer add(MallGoodsRankParam addParam) {
        QueryWrapper<MallGoodsRank> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(addParam.getName())) {
            queryWrapper.eq("name", addParam.getName());
        }
        if (mallGoodsRankMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("商品榜单已存在，请检查");
        }

        MallGoodsRank model = checkRankData(addParam, new MallGoodsRank());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setSiteId(RequestUtils.defaultSiteId());
        mallGoodsRankMapper.insert(model);
        return model.getRankId();
    }

    @Override
    public void edit(Integer id, MallGoodsRankParam editParam) {
        MallGoodsRank model = mallGoodsRankMapper.selectOne(
                new QueryWrapper<MallGoodsRank>()
                        .eq("rank_id", id));

        Assert.notNull(model, "商品排行榜不存在");

        QueryWrapper<MallGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", editParam.getName())
                .ne("rank_id", editParam.getRankId());
        if (mallGoodsRankMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("商品榜单已存在");
        }

        checkRankData(editParam, model);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        mallGoodsRankMapper.updateById(model);
    }

    @Override
    public MallGoodsRankInfoVo info(Integer id) {
        MallGoodsRank model = mallGoodsRankMapper.selectOne(
                new QueryWrapper<MallGoodsRank>()
                        .eq("rank_id", id));

        Assert.notNull(model, "商品排行榜不存在");

        MallGoodsRankInfoVo vo = new MallGoodsRankInfoVo();
        BeanUtils.copyProperties(model, vo);
        vo.setCreateTime(DateUtils.timestampToString(model.getCreateTime()));
        vo.setUpdateTime(DateUtils.timestampToString(model.getUpdateTime()));
        vo.setIsShowSearch(model.getIsShowSearch());
        vo.setConfig(ObjectUtil.isEmpty(model.getConfig()) ? new JSONObject() : JSONUtil.parseObj(model.getConfig()));
        vo.setGoodsJson(ObjectUtil.isEmpty(model.getGoodsJson()) ? Collections.emptyList() : JSONUtil.toList(JSONUtil.parseArray(model.getGoodsJson()), MallGoodsRankInfoVo.GoodsJson.class));
        vo.setGoodsList(ObjectUtil.isEmpty(model.getGoodsJson()) ? Collections.emptyList() : JSONUtil.parseArray(model.getGoodsJson()));
        vo.setCategoryIds(ObjectUtil.isEmpty(model.getCategoryIds())  || !model.getCategoryIds().startsWith("[") ? Collections.emptyList() : JSONUtil.parseArray(model.getCategoryIds()));
        vo.setLabelIds(ObjectUtil.isEmpty(model.getLabelIds())   || !model.getLabelIds().startsWith("[") ? Collections.emptyList() : JSONUtil.parseArray(model.getLabelIds()));
        vo.setBrandIds(ObjectUtil.isEmpty(model.getBrandIds()) || !model.getBrandIds().startsWith("[") ? Collections.emptyList() : JSONUtil.parseArray(model.getBrandIds()));

        if (ObjectUtil.isEmpty(model.getGoodsSource()) || !GoodsSourceTypeEnum.GOODS.getValue().equals(model.getGoodsSource())) {
            return vo;
        }

        List<Integer> goodsId = JSONUtil.parseArray(model.getGoodsJson())
                .stream()
                .map(JSONUtil::parseObj)
                .map(g -> g.getInt("goodsId"))
                .toList();

        Map<Integer, Integer> goodsSortMap = JSONUtil.parseArray(model.getGoodsJson())
                .stream()
                .map(JSONUtil::parseObj)
                .collect(Collectors.toMap(g -> g.getInt("goodsId"), g -> g.getInt("sort", 0)));

        List<MallGoods> mallGoods = Collections.emptyList();
        Map<Integer, List<MallGoodsSku>> skuMap = Collections.emptyMap();
        if (ObjectUtil.isNotEmpty(goodsId)) {
            QueryWrapper<MallGoods> goodsQueryWrapper = new QueryWrapper<>();
            goodsQueryWrapper.in("goods_id", goodsId);
            mallGoods = mallGoodsMapper.selectList(goodsQueryWrapper);

            QueryWrapper<MallGoodsSku> shopGoodsSkuQueryWrapper = new QueryWrapper<>();
            shopGoodsSkuQueryWrapper.in("goods_id", goodsId);
            skuMap = mallGoodsSkuMapper.selectList(shopGoodsSkuQueryWrapper).stream().collect(Collectors.groupingBy(MallGoodsSku::getGoodsId));
        }

        List<MallGoodsRankInfoVo.GoodsList> goodsList = new ArrayList<>(CollectionUtil.size(mallGoods));
        for (MallGoods shopGood : mallGoods) {
            shopGood.setSort(0);
            if (goodsSortMap.containsKey(shopGood.getGoodsId())) {
                shopGood.setSort(goodsSortMap.get(shopGood.getGoodsId()));
            }
            MallGoodsRankInfoVo.GoodsList info = new MallGoodsRankInfoVo.GoodsList();
            BeanUtils.copyProperties(shopGood, info);
            info.setGoodsTypeName(GoodsTypeEnum.getNameByType(shopGood.getGoodsType()));

            MallGoodsRankInfoVo.GoodsList.GoodsSkuDTO sku = new MallGoodsRankInfoVo.GoodsList.GoodsSkuDTO();
            List<MallGoodsSku> skuList = skuMap.get(info.getGoodsId());
            MallGoodsSku goodsSku = ObjectUtil.isEmpty(skuList) ? null : skuList.get(0);
            if (goodsSku != null) {
                BeanUtils.copyProperties(goodsSku, sku);
                sku.setPrice(goodsSku.getPrice().toString());
            }

            info.setGoodsSku(sku);
            goodsList.add(info);
        }

        vo.setGoodsList(goodsList);
        return vo;
    }

    @Override
    public MallGoodsRankOptionData getOptionData() {
        JSONObject type = new JSONObject();
        for (GoodsRankTypeEnum value : GoodsRankTypeEnum.values()) {
            type.set(value.getValue(), value.getName());
        }
        JSONObject source = new JSONObject();
        for (GoodsSourceTypeEnum value : GoodsSourceTypeEnum.values()) {
            source.set(value.getValue(), value.getName());
        }
        JSONObject rule = new JSONObject();
        for (GoodsRuleTypeEnum value : GoodsRuleTypeEnum.values()) {
            rule.set(value.getValue(), value.getName());
        }
        return new MallGoodsRankOptionData(type, source, rule);
    }

    @Override
    public void del(Integer id) {
        QueryWrapper<MallGoodsRank> queryWrapper = new QueryWrapper<MallGoodsRank>().eq("rank_id", id);
        MallGoodsRank rank = mallGoodsRankMapper.selectOne(queryWrapper);
        Assert.notNull(rank, "商品榜单不存在，请检查");
        mallGoodsRankMapper.deleteById(rank);
    }

    @Override
    public ShopGoodsRankConfigVo getGoodsRankConfig() {
        return coreGoodsRankService.getGoodsRankConfig();
    }

    @Override
    public void setGoodsRankConfig(ShopGoodsRankConfigParam param) {
        param.setSiteId(RequestUtils.defaultSiteId());
        coreGoodsRankService.setGoodsRankConfig(param);
    }

    @Override
    public void editSort(MallGoodsRankEditSortParam param) {
        if (ObjectUtil.isEmpty(param.getRankId())) {
            throw new RuntimeException("商品榜单不存在");
        }

        MallGoodsRank rank = mallGoodsRankMapper.selectById(param.getRankId());
        if (ObjectUtil.isEmpty(rank)) {
            throw new RuntimeException("商品榜单不存在");
        }

        rank.setSort(param.getSort());
        mallGoodsRankMapper.updateById(rank);
    }

    @Override
    public void batchDelete(MallGoodsRankBatchDeleteParam param) {
        if (ObjectUtil.isEmpty(param.getRankId())) {
            throw new RuntimeException("商品榜单不存在");
        }
        mallGoodsRankMapper.deleteBatchIds(param.getRankId());
    }

    @Override
    public MallGoodsRankSelectPageResult getSelectPage(PageParam pageParam, MallGoodsRankSelectPageParam param) {
        List<Integer> verifyRankIds = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(param.getVerifyRankIds())) {
            verifyRankIds = mallGoodsRankMapper.selectBatchIds(param.getVerifyRankIds())
                    .stream()
                    .map(MallGoodsRank::getRankId)
                    .toList();
        }

        QueryWrapper<MallGoodsRank> rankQueryWrapper = new QueryWrapper<>();
        rankQueryWrapper.orderByDesc("sort")
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(param.getName())) {
            rankQueryWrapper.like("name", param.getName());
        }

        if (ObjectUtil.isNotEmpty(param.getRankType())) {
            rankQueryWrapper.eq("rank_type", param.getRankType());
        }

        if (ObjectUtil.isNotEmpty(param.getStatus())) {
            rankQueryWrapper.eq("status", param.getStatus());
        }

        Page<MallGoodsRank> mallGoodsRankPage = mallGoodsRankMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), rankQueryWrapper);
        List<MallGoodsRankSelectPageInfo> list = new ArrayList<>(CollectionUtil.size(mallGoodsRankPage.getRecords()));
        for (MallGoodsRank record : mallGoodsRankPage.getRecords()) {
            MallGoodsRankSelectPageInfo info = new MallGoodsRankSelectPageInfo();
            BeanUtils.copyProperties(record, info);
            info.setShowGoodsNum(checkRankData(record));
            info.setRankTypeName(GoodsRankTypeEnum.getNameByValue(record.getRankType()));
            info.setGoodsSourceName(GoodsSourceTypeEnum.getNameByValue(record.getGoodsSource()));
            info.setRuleTypeName(GoodsRuleTypeEnum.getNameByValue(record.getRuleType()));
            list.add(info);
        }

        return new MallGoodsRankSelectPageResult(mallGoodsRankPage, list, verifyRankIds);
    }

    @Override
    public void modifyStatus(MallGoodsRankModifyStatusParam param) {
        MallGoodsRank rank = mallGoodsRankMapper.selectById(param.getRankId());
        rank.setStatus(param.getStatus());
        mallGoodsRankMapper.updateById(rank);
    }

    private long checkRankData(MallGoodsRank item) {
        QueryWrapper<MallGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("delete_time", 0)
                .eq("status", 1)
                .eq("audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());

        GoodsSourceTypeEnum sourceType = GoodsSourceTypeEnum.getEnumByValue(item.getGoodsSource());
        if (sourceType == null) {
            return mallGoodsMapper.selectCount(goodsQueryWrapper);
        }

        switch (sourceType) {
            case ALL -> { }
            case GOODS -> {
                JSONArray goodsJson = JSONUtil.parseArray(item.getGoodsJson());
                List<Integer> goodsId = goodsJson.stream()
                        .map(JSONUtil::parseObj)
                        .map(goods -> goods.getInt("goodsId"))
                        .toList();
                if (ObjectUtil.isNotEmpty(goodsId)) {
                    goodsQueryWrapper.in("goods_id", goodsId);
                }
            }
            case CATEGORY -> {
                JSONArray categoryIds = JSONUtil.parseArray(item.getCategoryIds());
                if (ObjectUtil.isNotEmpty(categoryIds)) {
                    goodsQueryWrapper.in("goods_mall_category", categoryIds);
                }
            }
            case BRAND -> {
                JSONArray brandIds = JSONUtil.parseArray(item.getBrandIds());
                if (ObjectUtil.isNotEmpty(brandIds)) {
                    goodsQueryWrapper.in("brand_id", brandIds);
                }
            }
            case LABEL -> {
                JSONArray labels = JSONUtil.parseArray(item.getLabelIds());
                if (ObjectUtil.isNotEmpty(labels)) {
                    goodsQueryWrapper.and(wrapper -> labels.forEach(id -> wrapper.or().like("label_ids", id)));
                }
            }
        }

        return mallGoodsMapper.selectCount(goodsQueryWrapper);
    }

    private MallGoodsRank checkRankData(MallGoodsRankParam param, MallGoodsRank model) {
        BeanUtils.copyProperties(param, model);
        if (ObjectUtil.isNotEmpty(param.getConfig())) {
            model.setConfig(JSONUtil.toJsonStr(param.getConfig()));
        } else {
            model.setConfig("");
        }
        if (param.getIsShowSearch() != null) {
            model.setIsShowSearch(param.getIsShowSearch());
        }
        switch (GoodsSourceTypeEnum.getEnumByValue(param.getGoodsSource())) {
            case GOODS -> {
                model.setGoodsJson(ObjectUtil.isEmpty(param.getGoodsJson()) ? "" : JSONUtil.toJsonStr(param.getGoodsJson()));
                model.setCategoryIds("");
                model.setBrandIds("");
                model.setLabelIds("");
            }
            case CATEGORY -> {
                model.setCategoryIds(ObjectUtil.isEmpty(param.getCategoryIds()) ? "" : JSONUtil.toJsonStr(param.getCategoryIds()));
                model.setGoodsJson("");
                model.setBrandIds("");
                model.setLabelIds("");
            }
            case BRAND -> {
                model.setBrandIds(ObjectUtil.isEmpty(param.getBrandIds()) ? "" : JSONUtil.toJsonStr(param.getBrandIds()));
                model.setCategoryIds("");
                model.setGoodsJson("");
                model.setLabelIds("");
            }
            case LABEL -> {
                model.setLabelIds(ObjectUtil.isEmpty(param.getLabelIds()) ? "" : JSONUtil.toJsonStr(param.getLabelIds()));
                model.setCategoryIds("");
                model.setBrandIds("");
                model.setGoodsJson("");
            }
        }
        return model;
    }

}
