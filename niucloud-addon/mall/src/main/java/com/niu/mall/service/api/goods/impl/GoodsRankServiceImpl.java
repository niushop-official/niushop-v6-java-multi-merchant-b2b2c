package com.niu.mall.service.api.goods.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsRank;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.enums.goods.GoodsRankTypeEnum;
import com.niu.mall.enums.goods.GoodsSourceTypeEnum;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsRankMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.admin.goods.param.MallGoodsRankSearchParam;
import com.niu.mall.service.api.goods.IGoodsRankService;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.api.goods.param.GoodsRankComponentParam;
import com.niu.mall.service.api.goods.param.MallGoodsRankGoodsPageParam;
import com.niu.mall.service.api.goods.vo.GoodsRankComponentVo;
import com.niu.mall.service.api.goods.vo.MallGoodsRankGoodsListInfo;
import com.niu.mall.service.api.goods.vo.MallGoodsRankGoodsPageVo;
import com.niu.mall.service.api.goods.vo.MallGoodsSkuRankVo;
import com.niu.mall.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.mall.service.core.goods.ICoreGoodsRankService;
import com.niu.mall.service.core.goods.param.GetShowPriceParam;
import com.niu.mall.service.core.goods.vo.ShopGoodsRankConfigVo;
import com.niu.mall.service.core.goods.vo.ShowPriceVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsRankServiceImpl implements IGoodsRankService {
    private final IGoodsService goodsService;
    private final MallGoodsMapper mallGoodsMapper;
    private final ICoreUploadService coreUploadService;
    private final MallGoodsSkuMapper mallGoodsSkuMapper;
    private final IMemberLevelService memberLevelService;
    private final MallGoodsRankMapper mallGoodsRankMapper;
    private final ICoreGoodsRankService coreGoodsRankService;
    private final ICoreGoodsActivePriceService coreGoodsActivePriceService;

    @Override
    public ShopGoodsRankConfigVo getGoodsRankConfig() {
        return coreGoodsRankService.getGoodsRankConfig();
    }

    @Override
    public List<MallGoodsRank> page(MallGoodsRankSearchParam searchParam) {
        QueryWrapper<MallGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                .orderByDesc("sort")
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getName())) {
            queryWrapper.like("name", searchParam.getName());
        }

        return mallGoodsRankMapper.selectList(queryWrapper);
    }

    @Override
    public MallGoodsRankGoodsPageVo getGoodsPage(PageParam pageParam, MallGoodsRankGoodsPageParam param) {
        QueryWrapper<MallGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("rank_id", param.getRankId());
        List<MallGoodsRank> ranks = mallGoodsRankMapper.selectList(queryWrapper);

        if (ObjectUtil.isEmpty(ranks)) {
            return new MallGoodsRankGoodsPageVo();
        }

        MallGoodsRank rank = ranks.get(0);
        return new MallGoodsRankGoodsPageVo(getGoodsList(pageParam, ranks, 0), rank.getRankId(), rank.getName());
    }

    @Override
    public GoodsRankComponentVo getRankComponents(PageParam pageParam, GoodsRankComponentParam param) {
        QueryWrapper<MallGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        if (param.getRankId() > 0) {
            queryWrapper.eq("rank_id", param.getRankId())
                    .orderByDesc("sort")
                    .orderByDesc("create_time");
        }

        List<MallGoodsRank> ranks = mallGoodsRankMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(ranks)) {
            return new GoodsRankComponentVo();
        }

        GoodsRankComponentVo vo = new GoodsRankComponentVo();
        vo.setRankId(ranks.get(0).getRankId());
        vo.setName(ranks.get(0).getName());
        vo.setGoodsList(getGoodsList(pageParam, ranks, 3).getData());
        return vo;
    }

    private PageResult<MallGoodsRankGoodsListInfo> getGoodsList(PageParam pageParam, List<MallGoodsRank> ranks, int limit) {
        if (ObjectUtil.isEmpty(ranks)) {
            return new PageResult<>();
        }

        MallGoodsRank rank = ranks.get(0);
        long dateEnd = System.currentTimeMillis() / 1000;
        long dateStart = switch (GoodsRankTypeEnum.getEnumByValue(rank.getRankType())) {
            case DAY -> (DateUtil.beginOfDay(new Date()).getTime()) / 1000;
            case WEEK -> (DateUtil.beginOfWeek(new Date()).getTime()) / 1000;
            case MONTH -> (DateUtil.beginOfMonth(new Date()).getTime()) / 1000;
            case QUARTER -> (DateUtil.beginOfQuarter(new Date()).getTime()) / 1000;
        };

        GoodsSourceTypeEnum source = GoodsSourceTypeEnum.getEnumByValue(rank.getGoodsSource());
        if (GoodsSourceTypeEnum.GOODS.equals(source)) {
            List<MallGoodsRankGoodsListInfo> list = getGoodsListGoodsSource(rank, dateStart, dateEnd);

            if (limit == 0) {
                PageResult<MallGoodsRankGoodsListInfo> result = PageResult.build(1, 1, list.size(), list);
                return getGoodsListPage(result);
            }

            if (CollectionUtil.size(list) > limit) {
                list = list.subList(0, limit);
            }

            List<MallGoodsRankGoodsListInfo> infos = getGoodsListAll(list);
            return PageResult.build(1, 1, infos.size(), infos);
        }

        String order = "stat_" + rank.getRuleType() + "_num";
        MPJQueryWrapper<MallGoods> goodsMPJQueryWrapper = new MPJQueryWrapper<>();
        goodsMPJQueryWrapper.setAlias("goods")
                .eq("goods.status", 1)
                .eq("goods.delete_time", 0)
                .orderByDesc(order)
                .orderByDesc("goods_sort")
                .orderByDesc("create_time")
                .groupBy("goods.goods_id, goods.sort, goods.create_time")
                .leftJoin(("?_mall_goods_stat stat on goods.goods_id = stat.goods_id and date_time BETWEEN " + dateStart + " AND " + dateEnd).replace("?_", GlobalConfig.tablePrefix))
                .select("goods.site_id,goods.goods_id,goods_name,goods_cover,unit,goods.sale_num,member_discount,sum(stat.sale_num)+ goods.virtual_sale_num as stat_sale_num,sum(stat.collect_num) as stat_collect_num,sum(stat.evaluate_num) as stat_evaluate_num,sum(stat.access_num) as stat_access_num,goods.create_time,goods.sort as goods_sort");

        if (limit > 0) {
            goodsMPJQueryWrapper.last("limit " + limit);
        }

        MPJQueryWrapper<MallGoods> queryWrapper = switch (source) {
            case CATEGORY ->
                    goodsMPJQueryWrapper.and(wrapper -> JSONUtil.parseArray(rank.getCategoryIds()).forEach(id -> wrapper.or().like("goods_category", id)));
            case BRAND -> goodsMPJQueryWrapper.in("goods.brand_id", JSONUtil.parseArray(rank.getBrandIds()));
            case LABEL ->
                    goodsMPJQueryWrapper.and(wrapper -> JSONUtil.parseArray(rank.getLabelIds()).forEach(id -> wrapper.or().like("label_ids", id)));
            default -> goodsMPJQueryWrapper;
        };

        if (limit == 0) {
            Page<Map<String, Object>> iPage = mallGoodsMapper.selectMapsPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper);
            IPage<MallGoodsRankGoodsListInfo> convert = iPage.convert(map -> {
                MallGoodsRankGoodsListInfo info = new MallGoodsRankGoodsListInfo();
                ReflectionUtils.doWithFields(info.getClass(), field -> {
                    field.setAccessible(true);

                    Object v = map.get(StrUtil.toUnderlineCase(field.getName()));

                    if (v == null) {
                        return;
                    }

                    if (field.getName().equals("createTime")) {
                        field.set(info, DateUtils.timestampToString(Long.parseLong(v.toString())));
                        return;
                    }

                    if (field.getType().equals(Integer.class)) {
                        field.set(info, Integer.parseInt(v.toString()));
                        return;
                    }

                    field.set(info, v);
                });

                String thumb = coreUploadService.thumb(RequestUtils.defaultSiteId(), info.getGoodsCover(),
                        UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
                info.setGoodsCoverThumbMid(thumb);
                return info;
            });

            return getGoodsListPage(PageResult.build(convert));
        }

        List<MallGoodsRankGoodsListInfo> list = mallGoodsMapper.selectJoinList(MallGoodsRankGoodsListInfo.class, queryWrapper);
        List<MallGoodsRankGoodsListInfo> infos = getGoodsListAll(list);
        return PageResult.build(1, 1, infos.size(), infos);
    }

    private List<MallGoodsRankGoodsListInfo> getGoodsListAll(List<MallGoodsRankGoodsListInfo> list) {
        if (ObjectUtil.isEmpty(list)) {
            return list;
        }

        MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
        for (int i = 0; i < list.size(); i++) {
            MallGoodsRankGoodsListInfo info = list.get(i);
            info.setRankNum(i + 1);
            MallGoodsSku sku = getGoodsSku(info.getGoodsId());
            MallGoodsSkuRankVo skuVo = new MallGoodsSkuRankVo();
            BeanUtils.copyProperties(sku, skuVo);
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(sku.getGoodsId())
                    .salePrice(sku.getSalePrice())
                    .skuId(sku.getSkuId())
                    .price(sku.getPrice())
                    .memberDiscount(info.getMemberDiscount())
                    .memberPrice(sku.getMemberPrice())
                    .memberId(RequestUtils.memberId())
                    .build());
            skuVo.setShowPrice(skuShowPrice.getShowPrice());
            skuVo.setShowType(skuShowPrice.getShowType());
            info.setGoodsSku(skuVo);


            String thumb = coreUploadService.thumb(RequestUtils.defaultSiteId(), info.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
            info.setGoodsCoverThumbMid(thumb);
            if (sku == null) {
                continue;
            }



            info.setGoodsSku(skuVo);
        }

        return list;
    }

    private PageResult<MallGoodsRankGoodsListInfo> getGoodsListPage(PageResult<MallGoodsRankGoodsListInfo> pageResult) {
        if (ObjectUtil.isEmpty(pageResult.getData())) {
            return pageResult;
        }

        MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
        for (int i = 0; i < pageResult.getData().size(); i++) {
            MallGoodsRankGoodsListInfo info = pageResult.getData().get(i);
            info.setRankNum(i + 1);
            MallGoodsSku sku = getGoodsSku(info.getGoodsId());
            MallGoodsSkuRankVo skuVo = new MallGoodsSkuRankVo();
            BeanUtils.copyProperties(sku, skuVo);

            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(sku.getGoodsId())
                    .salePrice(sku.getSalePrice())
                    .skuId(sku.getSkuId())
                    .price(sku.getPrice())
                    .memberDiscount(info.getMemberDiscount())
                    .memberPrice(sku.getMemberPrice())
                    .memberId(RequestUtils.memberId())
                    .build());
            skuVo.setShowPrice(skuShowPrice.getShowPrice());
            skuVo.setShowType(skuShowPrice.getShowType());
            info.setGoodsSku(skuVo);
            if (sku == null) {
                continue;
            }
        }

        return pageResult;
    }

    private List<MallGoodsRankGoodsListInfo> getGoodsListGoodsSource(MallGoodsRank rank, long dateStart, long dateEnd) {
        List<Integer> configGoodsIds = JSONUtil.parseArray(rank.getGoodsJson())
                .stream()
                .map(JSONUtil::parseObj)
                .map(o -> o.getInt("goodsId"))
                .toList();

        Map<Integer, JSONObject> sortGoodsJson = JSONUtil.parseArray(rank.getGoodsJson())
                .stream()
                .map(JSONUtil::parseObj)
                .collect(Collectors.toMap(o -> o.getInt("goodsId"), o -> o));

        MPJQueryWrapper<MallGoods> goodsMPJQueryWrapper = new MPJQueryWrapper<>();
        goodsMPJQueryWrapper.setAlias("goods")
                .in("goods.goods_id", configGoodsIds)
                .eq("goods.is_gift", 0)
                .eq("goods.status", 1)
                .eq("goods.delete_time", 0)
                .orderByDesc("stat_" + rank.getRuleType() + "_num")
                .orderByDesc("goods_sort")
                .orderByDesc("create_time")
                .leftJoin(("?_mall_goods_stat stat on goods.goods_id = stat.goods_id  AND date_time BETWEEN " + dateStart + " AND " + dateEnd).replace("?_", GlobalConfig.tablePrefix))
                .groupBy("goods.goods_id, goods_sort, create_time")
                .select("goods.site_id,goods.goods_id,goods_name,goods_cover,unit,goods.sale_num + goods.virtual_sale_num as sale_num,member_discount,sum(stat.sale_num)+ goods.virtual_sale_num as stat_sale_num,sum(stat.collect_num) as stat_collect_num,sum(stat.evaluate_num) as stat_evaluate_num,sum(stat.access_num) as stat_access_num, goods.create_time create_time,goods.sort as goods_sort");

        return mallGoodsMapper.selectJoinMaps(goodsMPJQueryWrapper)
                .stream()
                .map(JSONUtil::parseObj)
                .map(o -> {
                    MallGoodsRankGoodsListInfo info = o.toBean(MallGoodsRankGoodsListInfo.class);
                    Integer sort = Optional.ofNullable(sortGoodsJson.get(info.getGoodsId()))
                            .map(obj -> obj.getInt("sort"))
                            .orElse(0);
                    info.setSort(sort);
                    info.setCreateTime(DateUtils.timestampToString(o.getLong("create_time")));

                    String cover = coreUploadService.thumb(RequestUtils.defaultSiteId(), info.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
                    info.setGoodsCoverThumbMid(cover);
                    return info;
                })
                .peek(o -> {
                    o.setStatSaleNum(Optional.ofNullable(o.getStatSaleNum()).orElse(0));
                    o.setStatCollectNum(Optional.ofNullable(o.getStatCollectNum()).orElse(0));
                    o.setStatEvaluateNum(Optional.ofNullable(o.getStatEvaluateNum()).orElse(0));
                    o.setStatAccessNum(Optional.ofNullable(o.getStatAccessNum()).orElse(0));
                    o.setSort(Optional.ofNullable(o.getSort()).orElse(0));
                    o.setGoodsSort(Optional.ofNullable(o.getGoodsSort()).orElse(0));
                })
                .sorted(Comparator
                        .<MallGoodsRankGoodsListInfo>comparingInt(o -> JSONUtil.parseObj(o).getInt("stat" + StrUtil.upperFirst(rank.getRuleType()) + "Num")).reversed()
                        .thenComparingInt(MallGoodsRankGoodsListInfo::getSort).reversed()
                        .thenComparingInt(MallGoodsRankGoodsListInfo::getGoodsSort).reversed()
                        .thenComparingLong(o -> DateUtils.StringToTimestamp(o.getCreateTime())).reversed())
                .toList();
    }

    private MallGoodsSku getGoodsSku(Integer goodsId) {
        QueryWrapper<MallGoodsSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id", goodsId);
        List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(queryWrapper);
        return ObjectUtil.isEmpty(skuList) ? null : skuList.get(0);
    }
}
