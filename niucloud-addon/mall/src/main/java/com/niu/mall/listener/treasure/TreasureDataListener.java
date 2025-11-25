package com.niu.mall.listener.treasure;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 宝贝数据查询
 * Class TreasureDataListener
 * @package com.niu.mall.listener.treasure
 */
@EventCallback("mall")
@Component
public class TreasureDataListener extends CommonEventDefiner {

    @Resource
    private MallGoodsMapper mallGoodsMapper;

    @Resource
    private ICoreUploadService coreUploadService;

    @Resource
    private MallGoodsSkuMapper mallGoodsSkuMapper;

    /**
     * 处理宝贝数据查询事件
     * @param event 事件参数
     * @return 宝贝数据结果
     */
    @Override
    public CommonEventDefiner.CommonEventResult handleCallback(CommonEvent event) {
        if(event.getEventName().equals("TreasureData")){
            Object data = event.getData();
            if (data == null) {
                return new CommonEventDefiner.CommonEventResult();
            }

            // 安全地转换data为Map
            Map<String, Object> params = null;
            if (data instanceof Map) {
                params = (Map<String, Object>) data;
            } else {
                // 如果不是Map类型，返回空结果
                return new CommonEventDefiner.CommonEventResult();
            }
            String relateType = (String) params.get("relate_type");

            if ("mall".equals(relateType)) {

                QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("site_id", "goods_id", "goods_name", "sub_title", "goods_cover")
                        .eq("status", 1)
                        .orderByAsc("sort")
                        .orderByDesc("create_time");


                if (ObjectUtil.isNotEmpty(params.get("site_id"))) {
                    queryWrapper.eq("site_id", params.get("site_id"));
                }


                Map<String, Object> whereMap = (Map<String, Object>) params.get("where");
                if (whereMap != null) {

                    if (ObjectUtil.isNotEmpty(whereMap.get("keyword"))) {
                        String keyword = (String) whereMap.get("keyword");
                        queryWrapper.and(wrapper -> wrapper
                                .like("goods_name", keyword)
                                .or()
                                .like("sub_title", keyword));
                    }


                    if (ObjectUtil.isNotEmpty(whereMap.get("relate_ids"))) {
                        List<Integer> relateIds = (List<Integer>) whereMap.get("relate_ids");
                        if (CollUtil.isNotEmpty(relateIds)) {
                            queryWrapper.in("goods_id", relateIds);
                        }
                    }

                    if (ObjectUtil.isNotEmpty(whereMap.get("join_ids"))) {
                        List<Integer> joinIds = (List<Integer>) whereMap.get("join_ids");
                        if (CollUtil.isNotEmpty(joinIds)) {
                            queryWrapper.notIn("goods_id", joinIds);
                        }
                    }
                }

                if (ObjectUtil.isNotEmpty(params.get("page"))) {
                    // 分页查询
                    Map<String, Object> pageMap = (Map<String, Object>) params.get("page");
                    Integer page = (Integer) pageMap.get("page");
                    Integer limit = (Integer) pageMap.get("limit");

                    IPage<MallGoods> pageResult = mallGoodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
                    List<MallGoods> goodsList = pageResult.getRecords();

                    if (CollUtil.isNotEmpty(goodsList)) {

                        List<Integer> goodsIds = goodsList.stream()
                                .map(MallGoods::getGoodsId)
                                .collect(Collectors.toList());

                        Map<Integer, MallGoodsSku> skuMap = new HashMap<>();
                        if (CollUtil.isNotEmpty(goodsIds)) {
                            QueryWrapper<MallGoodsSku> skuQueryWrapper = new QueryWrapper<>();
                            skuQueryWrapper.in("goods_id", goodsIds)
                                    .eq("is_default", 1);
                            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(skuQueryWrapper);

                            skuMap = skuList.stream()
                                    .collect(Collectors.toMap(MallGoodsSku::getGoodsId, sku -> sku, (existing, replacement) -> existing));
                        }

                        List<Map<String, Object>> dataList = new ArrayList<>();
                        for (MallGoods goods : goodsList) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("site_id", goods.getSiteId()); // 站点id
                            item.put("treasure_name", goods.getGoodsName()); // 宝贝名称
                            item.put("treasure_sub_name", goods.getSubTitle()); // 宝贝副标题

                            String treasureImage = goods.getGoodsCover();
                            try {
                                String thumbMid = coreUploadService.thumb(RequestUtils.siteId(), goods.getGoodsCover(), UploadThumbTypeEnum.MID.getType())
                                        .getDataMap()
                                        .get(UploadThumbTypeEnum.MID.getType());
                                if (StrUtil.isNotBlank(thumbMid)) {
                                    treasureImage = thumbMid;
                                }
                            } catch (Exception e) {
                                // 如果缩略图处理失败，使用原图
                            }
                            item.put("treasure_image", treasureImage); // 宝贝图片

                            // 宝贝价格 - 从默认SKU获取
                            MallGoodsSku defaultSku = skuMap.get(goods.getGoodsId());
                            item.put("treasure_price", defaultSku != null ? defaultSku.getPrice() : null); // 宝贝价格

                            item.put("treasure_url", "/addon/mall/pages/goods/detail?goods_id=" + goods.getGoodsId()); // 宝贝跳转链接
                            item.put("relate_id", goods.getGoodsId()); // 关联业务id
                            item.put("relate_type", "mall"); // 关联业务类型
                            dataList.add(item);
                        }
                        Map<String, Object> result = new HashMap<>();
                        result.put("data", dataList);
                        result.put("total", pageResult.getTotal());
                        result.put("per_page", pageResult.getSize());
                        result.put("current_page", pageResult.getCurrent());
                        result.put("last_page", pageResult.getPages());

                        CommonEventDefiner.CommonEventResult eventResult = new CommonEventDefiner.CommonEventResult();
                        eventResult.setResult(result);
                        return eventResult;
                    } else {
                        // 空数据情况
                        Map<String, Object> result = new HashMap<>();
                        result.put("data", new ArrayList<>());
                        result.put("total", 0L);
                        result.put("per_page", limit);
                        result.put("current_page", page);
                        result.put("last_page", 0L);

                        CommonEventDefiner.CommonEventResult eventResult = new CommonEventDefiner.CommonEventResult();
                        eventResult.setResult(result);
                        return eventResult;
                    }
                } else {
                    List<MallGoods> goodsList = mallGoodsMapper.selectList(queryWrapper);

                    if (CollUtil.isNotEmpty(goodsList)) {
                        // 获取SKU信息
                        List<Integer> goodsIds = goodsList.stream()
                                .map(MallGoods::getGoodsId)
                                .collect(Collectors.toList());

                        Map<Integer, MallGoodsSku> skuMap = new HashMap<>();
                        if (CollUtil.isNotEmpty(goodsIds)) {
                            QueryWrapper<MallGoodsSku> skuQueryWrapper = new QueryWrapper<>();
                            skuQueryWrapper.in("goods_id", goodsIds)
                                    .eq("is_default", 1);
                            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(skuQueryWrapper);

                            skuMap = skuList.stream()
                                    .collect(Collectors.toMap(MallGoodsSku::getGoodsId, sku -> sku, (existing, replacement) -> existing));
                        }

                        List<Map<String, Object>> treasureList = new ArrayList<>();
                        for (MallGoods goods : goodsList) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("site_id", goods.getSiteId()); // 站点id
                            item.put("treasure_name", goods.getGoodsName()); // 宝贝名称
                            item.put("treasure_sub_name", goods.getSubTitle()); // 宝贝副标题

                            // 处理宝贝图片
                            String treasureImage = goods.getGoodsCover();
                            try {
                                String thumbMid = coreUploadService.thumb(RequestUtils.siteId(), goods.getGoodsCover(), UploadThumbTypeEnum.MID.getType())
                                        .getDataMap()
                                        .get(UploadThumbTypeEnum.MID.getType());
                                if (StrUtil.isNotBlank(thumbMid)) {
                                    treasureImage = thumbMid;
                                }
                            } catch (Exception e) {

                            }
                            item.put("treasure_image", treasureImage); // 宝贝图片

                            // 宝贝价格
                            MallGoodsSku defaultSku = skuMap.get(goods.getGoodsId());
                            item.put("treasure_price", defaultSku != null ? defaultSku.getPrice() : null); // 宝贝价格

                            item.put("treasure_url", "/addon/mall/pages/goods/detail?goods_id=" + goods.getGoodsId()); // 宝贝跳转链接
                            item.put("relate_id", goods.getGoodsId()); // 关联业务id
                            item.put("relate_type", "mall"); // 关联业务类型
                            treasureList.add(item);
                        }

                        CommonEventDefiner.CommonEventResult eventResult = new CommonEventDefiner.CommonEventResult();
                        eventResult.setResult(treasureList);
                        return eventResult;
                    } else {
                        // 空数据情况
                        CommonEventDefiner.CommonEventResult eventResult = new CommonEventDefiner.CommonEventResult();
                        eventResult.setResult(new ArrayList<>());
                        return eventResult;
                    }
                }
            }
            return new CommonEventDefiner.CommonEventResult();
        }
        return null;
    }
}