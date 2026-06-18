package com.niu.mall.service.site.marketing.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.entity.fullactive.MallFullGift;
import com.niu.mall.entity.fullactive.MallFullGiftGoods;
import com.niu.mall.enums.fullactive.FullGiftGoodsTypeEnum;
import com.niu.mall.enums.fullactive.FullGiftStatusEnum;
import com.niu.mall.mapper.fullactive.FullGiftGoodsMapper;
import com.niu.mall.mapper.fullactive.FullGiftMapper;
import com.niu.mall.service.admin.marketing.param.FullGiftParam;
import com.niu.mall.service.admin.marketing.param.FullGiftSearchParam;
import com.niu.mall.service.admin.marketing.vo.FullGiftInfoVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftInitVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftListVo;
import com.niu.mall.service.core.marketing.ICoreFullGiftService;
import com.niu.mall.service.site.marketing.ISiteFullGiftService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SiteFullGiftServiceImpl implements ISiteFullGiftService {

    @Resource
    private FullGiftMapper fullGiftMapper;

    @Resource
    private FullGiftGoodsMapper fullGiftGoodsMapper;

    @Resource
    private ICoreFullGiftService coreFullGiftService;

    @Override
    public PageResult<FullGiftListVo> list(PageParam pageParam, FullGiftSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        Integer siteId = RequestUtils.siteId();

        MPJLambdaWrapper<MallFullGift> queryWrapper = new MPJLambdaWrapper<MallFullGift>()
                .selectAll(MallFullGift.class)
                .eq(MallFullGift::getSiteId, siteId)
                .orderByDesc(MallFullGift::getCreateTime);

        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) {
            queryWrapper.like(MallFullGift::getTitle, searchParam.getTitle());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq(MallFullGift::getStatus, searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType())) {
            queryWrapper.eq(MallFullGift::getGoodsType, searchParam.getGoodsType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStartTime())) {
            queryWrapper.ge(MallFullGift::getEndTime, searchParam.getStartTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getEndTime())) {
            queryWrapper.le(MallFullGift::getStartTime, searchParam.getEndTime());
        }

        IPage<MallFullGift> iPage = fullGiftMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<FullGiftListVo> list = BeanUtil.copyToList(iPage.getRecords(), FullGiftListVo.class);

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public FullGiftInitVo init(Integer id) {
        FullGiftInitVo initVo = coreFullGiftService.init();
        if (id != null && id > 0) {
            initVo.setInfo(info(id));
        }
        return initVo;
    }

    @Override
    public FullGiftInfoVo info(Integer id) {
        return coreFullGiftService.info(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(FullGiftParam addParam) {
        // 1. 校验商品冲突
        checkGoods(addParam, true);

        // 2. 格式化活动数据
        var mallFullGift = formatActiveData(addParam);

        // 3. 设置创建人和创建时间
        mallFullGift.setSiteId(RequestUtils.siteId());
        //todo
//        mallFullGift.setCreator(RequestUtils.getCurrentUser().getUsername());
        mallFullGift.setCreator("测试");
        mallFullGift.setCreateTime(System.currentTimeMillis() / 1000);
        mallFullGift.setUpdateTime(System.currentTimeMillis() / 1000);

        // 4. 保存活动主表
        fullGiftMapper.insert(mallFullGift);

        // 5. 处理商品关联（批量插入）
        upsertGoods(addParam.getGoodsData(), mallFullGift);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Integer id, FullGiftParam modifyParam) {
        MallFullGift fullGift = fullGiftMapper.selectById(id);
        if (ObjectUtil.isNull(fullGift)) {
            throw new CommonException("满赠活动不存在");
        }

        // 1. 校验商品冲突
        checkGoods(modifyParam, true);

        // 2. 格式化活动数据
        var mallFullReduction = formatActiveData(modifyParam);

        // 3. 保存修改的数据, 记录修改时间
        BeanUtil.copyProperties(mallFullReduction, fullGift, CopyOptions.create().setIgnoreNullValue(true));
        fullGift.setUpdateTime(System.currentTimeMillis() / 1000);

        // 4. 修改数据
        fullGiftMapper.updateById(fullGift);

        // 5. 修改关联商品信息
        upsertGoods(modifyParam.getGoodsData(), fullGift);
    }

    /**
     * 处理商品关联（增量更新）
     */
    private void upsertGoods(List<FullGiftParam.GoodsData> goodsData, MallFullGift fullGift) {
        if (goodsData == null) goodsData = new ArrayList<>();
        // 1. 将 goodsData 转换为 Map<goodsId, List<skuId>>
        Map<Integer, List<Integer>> inputGoodsMap = new HashMap<>();
        for (var item : goodsData) {
            Integer goodsId = item.getGoodsId();
            List<Integer> skuIds = item.getSkuIds();
            if (skuIds != null && !skuIds.isEmpty()) {
                List<Integer> filteredSkuIds = skuIds.stream()
                        .filter(id -> id != null && id > 0)
                        .collect(Collectors.toList());
                if (!filteredSkuIds.isEmpty())
                    inputGoodsMap.put(goodsId, filteredSkuIds);
            }
        }

        // 2. 查询数据库中已存在的关联记录
        List<MallFullGiftGoods> dbList = fullGiftGoodsMapper.selectList(new LambdaQueryWrapper<MallFullGiftGoods>()
                .eq(MallFullGiftGoods::getSiteId, fullGift.getSiteId())
                .eq(MallFullGiftGoods::getFullGiftId, fullGift.getId()));
        Map<String, MallFullGiftGoods> dbSkuMap = new HashMap<>();
        for (MallFullGiftGoods item : dbList) {
            String key = item.getGoodsId() + "_" + item.getSkuId();
            dbSkuMap.put(key, item);
        }

        // 3. 记录传入的所有 key
        Set<String> inputKeys = new HashSet<>();
        List<MallFullGiftGoods> toAdd = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : inputGoodsMap.entrySet()) {
            Integer goodsId = entry.getKey();
            for (Integer skuId : entry.getValue()) {
                String key = goodsId + "_" + skuId;
                inputKeys.add(key);
                if (!dbSkuMap.containsKey(key)) {
                    MallFullGiftGoods newGoods = new MallFullGiftGoods();
                    newGoods.setSiteId(fullGift.getSiteId());
                    newGoods.setFullGiftId(fullGift.getId());
                    newGoods.setGoodsId(goodsId);
                    newGoods.setSkuId(skuId);
                    newGoods.setStatus(fullGift.getStatus());
                    newGoods.setGoodsType(fullGift.getGoodsType());
                    newGoods.setCreateTime(fullGift.getUpdateTime());
                    toAdd.add(newGoods);
                }
            }
        }

        // 4. 收集需要删除的记录ID
        List<Integer> toDelIds = new ArrayList<>();
        for (Map.Entry<String, MallFullGiftGoods> entry : dbSkuMap.entrySet()) {
            if (!inputKeys.contains(entry.getKey())) {
                toDelIds.add(entry.getValue().getId());
            }
        }

        if (!toAdd.isEmpty()) {
            fullGiftGoodsMapper.insert(toAdd);
        }
        if (!toDelIds.isEmpty()) {
            fullGiftGoodsMapper.deleteByIds(toDelIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(Integer id) {
        MallFullGift fullGift = fullGiftMapper.selectById(id);
        if (ObjectUtil.isNull(fullGift)) {
            throw new CommonException("满赠活动不存在");
        }

        fullGiftMapper.deleteById(fullGift);

        LambdaQueryWrapper<MallFullGiftGoods> goodsQueryWrapper = new LambdaQueryWrapper<>();
        goodsQueryWrapper.eq(MallFullGiftGoods::getFullGiftId, id);
        fullGiftGoodsMapper.delete(goodsQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setStatus(Integer id, String status) {
        if (FullGiftStatusEnum.getEnumByStatus(status) == null) {
            throw new CommonException("状态错误");
        }
        MallFullGift fullGift = fullGiftMapper.selectById(id);
        if (ObjectUtil.isNull(fullGift)) {
            throw new CommonException("满赠活动不存在");
        }

        fullGift.setStatus(status);
        fullGift.setUpdateTime(System.currentTimeMillis() / 1000);

        fullGiftGoodsMapper.update(new LambdaUpdateWrapper<MallFullGiftGoods>()
                .set(MallFullGiftGoods::getStatus, status)
                .eq(MallFullGiftGoods::getFullGiftId, fullGift.getId()));

        fullGiftMapper.updateById(fullGift);
    }


    private String getStatus(Long startTime, Long endTime) {
        Long now = System.currentTimeMillis() / 1000;
        if (startTime > now) return FullGiftStatusEnum.WAIT.getStatus();
        else if (now < endTime) return FullGiftStatusEnum.PROCESS.getStatus();
        else return FullGiftStatusEnum.END.getStatus();
    }

    /**
     * 格式化活动数据（含赠品规则重组）
     */
    private MallFullGift formatActiveData(FullGiftParam data) {
        MallFullGift activeData = BeanUtil.copyProperties(data, MallFullGift.class);

        activeData.setMemberTypeJson(JSONUtil.toJsonStr(data.getMemberTypeJson()));

        // 处理 rule_json：将赠品SKU按商品ID聚合
        var paramsRuleJson = data.getRuleJson();
        for (var item : paramsRuleJson) {
            List<Map<String, Object>> giftSkuData = item.getGiftSkuData();
            Map<Integer, Map<String, Object>> numMap = new HashMap<>();
            for (Map<String, Object> datum : giftSkuData) {
                Integer goodsId = (Integer) datum.get("goods_id");
                Integer skuId = (Integer) datum.get("sku_id");
                Integer num = (Integer) datum.get("num");
                numMap.computeIfAbsent(goodsId, k -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("goods_id", goodsId);
                    map.put("sku_list", new ArrayList<Map<String, Object>>());
                    return map;
                });
                List<Map<String, Object>> skuList = (List<Map<String, Object>>) numMap.get(goodsId).get("sku_list");
                Map<String, Object> skuItem = new HashMap<>();
                skuItem.put("sku_id", skuId);
                skuItem.put("num", num);
                skuList.add(skuItem);
            }
            item.setGiftSkuData(new ArrayList<>(numMap.values()));
        }
        activeData.setRuleJson(JSONUtil.toJsonStr(paramsRuleJson));
        activeData.setStatus(getStatus(activeData.getStartTime(), activeData.getEndTime()));
        return activeData;
    }

    @Override
    public Map<String, Object> checkGoods(FullGiftParam data) {
        return checkGoods(data, false);
    }

    public Map<String, Object> checkGoods(FullGiftParam data, Boolean isThrow) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", 1);
        error.put("data", new ArrayList<>());

        Long startTime = data.getStartTime();
        Long endTime = data.getEndTime();
        String goodsType = data.getGoodsType();
        Integer id = data.getId();
        var siteId = RequestUtils.siteId();

        // 时间重叠条件
        LambdaQueryWrapper<MallFullGift> timeWrapper = new LambdaQueryWrapper<>();
        timeWrapper.le(MallFullGift::getStartTime, endTime)
                .ge(MallFullGift::getEndTime, startTime);

        // 规则1：全部商品活动只能有一个
        if (FullGiftGoodsTypeEnum.ALL.getType().equals(goodsType)) {
            long allGoodsCount = fullGiftMapper.selectCount(new LambdaQueryWrapper<MallFullGift>()
                    .eq(MallFullGift::getSiteId, siteId)
                    .eq(MallFullGift::getGoodsType, FullGiftGoodsTypeEnum.ALL.getType())
                    .in(MallFullGift::getStatus, Arrays.asList(FullGiftStatusEnum.WAIT.getStatus(), FullGiftStatusEnum.PROCESS.getStatus()))
                    .ne(id != null, MallFullGift::getId, id)
                    .le(MallFullGift::getStartTime, endTime)
                    .ge(MallFullGift::getEndTime, startTime));
            if (allGoodsCount > 0 && isThrow) {
                throw new AdminException("同一时间段内只能存在一个全部商品类型的满赠活动");
            }
        } else {
            var goodsData = data.getGoodsData();
            List<Integer> addGoodsIds = goodsData.stream().map(FullGiftParam.GoodsData::getGoodsId).toList();

            // 规则2：指定商品不参与活动
            if (FullGiftGoodsTypeEnum.SELECTED_NOT.getType().equals(goodsType)) {
                long excludeCount = fullGiftMapper.selectCount(new LambdaQueryWrapper<MallFullGift>()
                        .eq(MallFullGift::getSiteId, siteId)
                        .eq(MallFullGift::getGoodsType, FullGiftGoodsTypeEnum.SELECTED_NOT.getType())
                        .in(MallFullGift::getStatus, Arrays.asList(FullGiftStatusEnum.WAIT.getStatus(), FullGiftStatusEnum.PROCESS.getStatus()))
                        .ne(id != null, MallFullGift::getId, id)
                        .le(MallFullGift::getStartTime, endTime)
                        .ge(MallFullGift::getEndTime, startTime));
                if (excludeCount > 0) {
                    if (isThrow) {
                        throw new AdminException("同一时间段内只能存在一个指定商品不参与类型的满赠活动");
                    } else {
                        for (Integer goodsId : addGoodsIds) {
                            ((List<Map<String, Object>>) error.get("data")).add(Map.of(
                                    "goods_id", goodsId,
                                    "error_msg", "同一时间段内只能存在一个指定商品不参与类型的满赠活动"
                            ));
                        }
                    }
                }

                List<Integer> selectedActiveIds = fullGiftMapper.selectList(new LambdaQueryWrapper<MallFullGift>()
                                .eq(MallFullGift::getSiteId, siteId)
                                .eq(MallFullGift::getGoodsType, FullGiftGoodsTypeEnum.SELECTED.getType())
                                .in(MallFullGift::getStatus, Arrays.asList(FullGiftStatusEnum.WAIT.getStatus(), FullGiftStatusEnum.PROCESS.getStatus()))
                                .ne(id != null, MallFullGift::getId, id)
                                .le(MallFullGift::getStartTime, endTime)
                                .ge(MallFullGift::getEndTime, startTime))
                        .stream().map(MallFullGift::getId).collect(Collectors.toList());

                if (!selectedActiveIds.isEmpty()) {
                    List<Integer> selectedGoodsList = fullGiftGoodsMapper.selectList(new LambdaQueryWrapper<MallFullGiftGoods>()
                                    .in(MallFullGiftGoods::getFullGiftId, selectedActiveIds)
                                    .select(MallFullGiftGoods::getGoodsId))
                            .stream().map(MallFullGiftGoods::getGoodsId).distinct().toList();
                    if (!selectedGoodsList.isEmpty() && !new HashSet<>(addGoodsIds).containsAll(selectedGoodsList)) {
                        //通过当前正在参加活动的ID和排除的Id得到重复参加活动的商品ID
                        //selectedGoodsList中 不被 addGoodsIds 包含的即为重复参加活动
//                        List<Integer> repeatIds = selectedGoodsList.stream().filter(repeatId -> !addGoodsIds.contains(repeatId)).toList();
//                        if (!repeatIds.isEmpty()) {
                        for (Integer goodsId : addGoodsIds) {
                            if (isThrow) {
                                throw new AdminException("同一商品在一个时间段内只能参加一个满赠活动");
                            } else {
                                ((List<Map<String, Object>>) error.get("data")).add(Map.of(
                                        "goods_id", goodsId,
                                        "error_msg", "当前活动设置会导致部分商品参与多个满赠活动"
                                ));
                            }
                        }
//                        }
                    }
                }
            }

            // 规则3：指定商品参与活动（SKU不重复）
            if (FullGiftGoodsTypeEnum.SELECTED.getType().equals(goodsType) && !goodsData.isEmpty()) {
                if (!addGoodsIds.isEmpty()) {
                    // 冲突商品：其他指定商品参与活动中已包含的商品
                    var sql1 = "SELECT id FROM ?_mall_full_gift WHERE site_id = " + siteId +
                            " AND goods_type = '" + FullGiftGoodsTypeEnum.SELECTED.getType() + "'" +
                            " AND status IN ('" + FullGiftStatusEnum.WAIT.getStatus() + "','" + FullGiftStatusEnum.PROCESS.getStatus() + "')" +
                            (id != null ? " AND id <> " + id : "") +
                            " AND start_time <= " + endTime + " AND end_time >= " + startTime;
                    List<Integer> conflictGoodsIds = fullGiftGoodsMapper.selectList(new LambdaQueryWrapper<MallFullGiftGoods>()
                                    .eq(MallFullGiftGoods::getSiteId, siteId)
                                    .inSql(MallFullGiftGoods::getFullGiftId, sql1.replace("?_", GlobalConfig.tablePrefix))
                                    .select(MallFullGiftGoods::getGoodsId))
                            .stream().map(MallFullGiftGoods::getGoodsId).toList();

                    List<Integer> conflictIntersect = conflictGoodsIds.stream()
                            .filter(addGoodsIds::contains)
                            .collect(Collectors.toList());

                    if (!conflictIntersect.isEmpty()) {
                        // 查询冲突活动标题
                        var sql = "SELECT id FROM ?_mall_full_gift WHERE site_id = " + siteId +
                                " AND status IN ('" + FullGiftStatusEnum.WAIT.getStatus() + "','" + FullGiftStatusEnum.PROCESS.getStatus() + "')" +
                                " AND start_time <= " + endTime + " AND end_time >= " + startTime;
                        List<Map<String, Object>> conflictGoodsInfo = fullGiftGoodsMapper.selectList(new LambdaQueryWrapper<MallFullGiftGoods>()
                                        .eq(MallFullGiftGoods::getSiteId, siteId)
                                        .in(MallFullGiftGoods::getGoodsId, conflictIntersect)
                                        .inSql(MallFullGiftGoods::getFullGiftId, sql.replace("?_", GlobalConfig.tablePrefix)))
                                .stream()
                                .map(g -> {
                                    // 实际需联查 full_gift 表，此处简化
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("goods_id", g.getGoodsId());
                                    map.put("title", "未知活动");
                                    return map;
                                }).toList();

                        Map<Integer, String> goodsTitleMap = new HashMap<>();
                        for (Map<String, Object> item : conflictGoodsInfo) {
                            goodsTitleMap.put((Integer) item.get("goods_id"), (String) item.get("title"));
                        }
                        for (Integer goodsId : conflictIntersect) {
                            String activityTitle = goodsTitleMap.getOrDefault(goodsId, "未知");
                            String errorMsg = String.format("该商品已参加【%s】满赠活动，无法重复参与", activityTitle);
                            ((List<Map<String, Object>>) error.get("data")).add(Map.of(
                                    "goods_id", goodsId,
                                    "error_msg", errorMsg
                            ));
                        }
                    }

                    // 检查是否与“指定商品不参与”活动冲突（必须所有商品都在不参与列表中）
                    List<Integer> selectedNotActiveIds = fullGiftMapper.selectList(new LambdaQueryWrapper<MallFullGift>()
                                    .eq(MallFullGift::getSiteId, siteId)
                                    .eq(MallFullGift::getGoodsType, FullGiftGoodsTypeEnum.SELECTED_NOT.getType())
                                    .in(MallFullGift::getStatus, Arrays.asList(FullGiftStatusEnum.WAIT.getStatus(), FullGiftStatusEnum.PROCESS.getStatus()))
                                    .ne(id != null, MallFullGift::getId, id)
                                    .le(MallFullGift::getStartTime, endTime)
                                    .ge(MallFullGift::getEndTime, startTime))
                            .stream().map(MallFullGift::getId).collect(Collectors.toList());

                    if (!selectedNotActiveIds.isEmpty()) {
                        List<Integer> notGoodsIds = fullGiftGoodsMapper.selectList(new LambdaQueryWrapper<MallFullGiftGoods>()
                                        .in(MallFullGiftGoods::getFullGiftId, selectedNotActiveIds)
                                        .select(MallFullGiftGoods::getGoodsId))
                                .stream().map(MallFullGiftGoods::getGoodsId).distinct().toList();
                        for (Integer goodsId : addGoodsIds) {
                            if (!notGoodsIds.contains(goodsId)) {
                                if (isThrow) {
                                    throw new AdminException("同一商品在一个时间段内只能参加一个满赠活动");
                                } else {
                                    ((List<Map<String, Object>>) error.get("data")).add(Map.of(
                                            "goods_id", goodsId,
                                            "error_msg", "当前活动设置会导致部分商品参与多个满赠活动"
                                    ));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!((List<?>) error.get("data")).isEmpty()) {
            error.put("code", -1);
        }
        if (error.get("code").equals(-1) && isThrow) {
            throw new AdminException("同一商品在一个时间段内只能参加一个满赠活动");
        }
        return error;
    }

}