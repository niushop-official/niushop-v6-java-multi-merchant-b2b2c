package com.niu.core.service.site.marketing.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.component.context.event.CallbackPublisher;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.goods.FullReductionGoodsEventDefiner;
import com.niu.core.entity.fullactive.MallFullReduction;
import com.niu.core.entity.fullactive.MallFullReductionGoods;
import com.niu.core.enums.fullactive.FullReductionGoodsTypeEnum;
import com.niu.core.enums.fullactive.FullReductionStatusEnum;
import com.niu.core.mapper.fullactive.FullReductionGoodsMapper;
import com.niu.core.mapper.fullactive.FullReductionMapper;
import com.niu.core.service.admin.marketing.param.FullReductionParam;
import com.niu.core.service.admin.marketing.param.FullReductionSearchParam;
import com.niu.core.service.admin.marketing.vo.FullReductionInfoVo;
import com.niu.core.service.admin.marketing.vo.FullReductionInitVo;
import com.niu.core.service.admin.marketing.vo.FullReductionListVo;
import com.niu.core.service.core.marketing.ICoreFullReductionService;
import com.niu.core.service.site.marketing.ISiteFullReductionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SiteFullReductionServiceImpl implements ISiteFullReductionService {

    @Resource
    private FullReductionMapper fullReductionMapper;

    @Resource
    private FullReductionGoodsMapper fullReductionGoodsMapper;

    @Resource
    private ICoreFullReductionService coreFullReductionService;

    @Override
    public PageResult<FullReductionListVo> list(PageParam pageParam, FullReductionSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        Integer siteId = RequestUtils.siteId();

        MPJLambdaWrapper<MallFullReduction> queryWrapper = new MPJLambdaWrapper<MallFullReduction>()
                .selectAll(MallFullReduction.class)
                .eq(MallFullReduction::getSiteId, siteId)
                .orderByDesc(MallFullReduction::getCreateTime);

        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) {
            queryWrapper.like(MallFullReduction::getTitle, searchParam.getTitle());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq(MallFullReduction::getStatus, searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getMemberType())) {
            queryWrapper.eq(MallFullReduction::getMemberType, searchParam.getMemberType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType())) {
            queryWrapper.eq(MallFullReduction::getGoodsType, searchParam.getGoodsType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getType())) {
            queryWrapper.eq(MallFullReduction::getType, searchParam.getType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStartTime())) {
            queryWrapper.ge(MallFullReduction::getEndTime, searchParam.getStartTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getEndTime())) {
            queryWrapper.le(MallFullReduction::getStartTime, searchParam.getEndTime());
        }

        IPage<MallFullReduction> iPage = fullReductionMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<FullReductionListVo> list = BeanUtil.copyToList(iPage.getRecords(), FullReductionListVo.class);

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public FullReductionInitVo init(Integer id) {
        FullReductionInitVo initVo = coreFullReductionService.init();
        if (id != null && id > 0) {
            initVo.setInfo(info(id));
        }
        return initVo;
    }

    /**
     * 详情（含商品关联）
     */
    @Override
    public FullReductionInfoVo info(Integer id) {
        return coreFullReductionService.info(id);
    }

    /**
     * 添加满减活动
     *
     * @param addParam 请求参数
     * @return true 成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(FullReductionParam addParam) {
        // 1. 校验商品冲突
        checkGoods(addParam, true);

        // 2. 格式化活动数据
        var mallFullReduction = formatActiveData(addParam);

        // 3. 设置创建人和创建时间
        mallFullReduction.setSiteId(RequestUtils.siteId());
        mallFullReduction.setCreator(RequestUtils.getCurrentUser().getUsername());
        mallFullReduction.setCreateTime(System.currentTimeMillis() / 1000);
        mallFullReduction.setUpdateTime(System.currentTimeMillis() / 1000);

        // 4. 保存活动主表
        fullReductionMapper.insert(mallFullReduction);

        // 5. 处理商品关联（批量插入）
        upsertGoods(addParam.getGoodsData(), mallFullReduction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Integer id, FullReductionParam modifyParam) {
        MallFullReduction fullReduction = fullReductionMapper.selectById(id);
        if (ObjectUtil.isNull(fullReduction)) {
            throw new CommonException("满减活动不存在");
        }

        // 1. 校验商品冲突
        checkGoods(modifyParam, true);

        // 2. 格式化活动数据
        var mallFullReduction = formatActiveData(modifyParam);

        // 3. 保存修改的数据, 记录修改时间
        BeanUtil.copyProperties(mallFullReduction, fullReduction, CopyOptions.create().setIgnoreNullValue(true));
        fullReduction.setUpdateTime(System.currentTimeMillis() / 1000);

        // 4. 修改数据
        fullReductionMapper.updateById(fullReduction);

        // 5. 修改关联商品信息
        upsertGoods(modifyParam.getGoodsData(), fullReduction);
    }

    /**
     * 处理商品关联（增量更新）
     *
     * @param goodsData     前端传入的商品数据，格式：List<Map<String, Object>>，每个元素包含 goods_id 和 sku_ids
     * @param fullReduction 满减活动信息
     */
    private void upsertGoods(List<FullReductionParam.GoodsData> goodsData, MallFullReduction fullReduction) {
        // 1. 将 goodsData 转换为 Map<goodsId, List<skuId>>
        Map<Integer, List<Integer>> inputGoodsMap = new HashMap<>();
        for (FullReductionParam.GoodsData item : goodsData) {
            Integer goodsId = item.getGoodsId();
            List<Integer> skuIds = item.getSkuIds();
            if (goodsId == null || goodsId <= 0) {
                continue;
            }
            if (skuIds == null || skuIds.isEmpty()) {
                inputGoodsMap.put(goodsId, new ArrayList<>());
            } else {
                // 过滤掉 null 或 0 的 skuId
                List<Integer> filteredSkuIds = skuIds.stream()
                        .filter(id -> id != null && id > 0)
                        .collect(Collectors.toList());
                if (!filteredSkuIds.isEmpty()) {
                    inputGoodsMap.put(goodsId, filteredSkuIds);
                }
            }
        }

        // 2. 查询数据库中已存在的关联记录
        List<MallFullReductionGoods> dbList = fullReductionGoodsMapper.selectList(new LambdaQueryWrapper<MallFullReductionGoods>()
                .eq(MallFullReductionGoods::getSiteId, fullReduction.getSiteId())
                .eq(MallFullReductionGoods::getFullReductionId, fullReduction.getId()));
        Map<String, MallFullReductionGoods> dbSkuMap = new HashMap<>();
        for (MallFullReductionGoods item : dbList) {
            String key = item.getGoodsId() + "_" + item.getSkuId();
            dbSkuMap.put(key, item);
        }

        // 3. 记录传入的所有 key
        Set<String> inputSkuKeys = new HashSet<>();
        List<MallFullReductionGoods> toAdd = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : inputGoodsMap.entrySet()) {
            Integer goodsId = entry.getKey();
            List<Integer> skuIds = entry.getValue();
            if (skuIds.isEmpty()) {
                skuIds = getAllSkuIds(fullReduction, goodsId);
            }
            for (Integer skuId : skuIds) {
                String key = goodsId + "_" + skuId;
                inputSkuKeys.add(key);
                if (!dbSkuMap.containsKey(key)) {
                    MallFullReductionGoods newGoods = new MallFullReductionGoods();
                    newGoods.setSiteId(RequestUtils.siteId());
                    newGoods.setFullReductionId(fullReduction.getId());
                    newGoods.setGoodsId(goodsId);
                    newGoods.setSkuId(skuId);
                    newGoods.setStatus(fullReduction.getStatus());
                    newGoods.setGoodsType(fullReduction.getGoodsType());
                    newGoods.setTradeType(fullReduction.getAddon());
                    newGoods.setCreateTime(fullReduction.getUpdateTime());
                    toAdd.add(newGoods);
                }
            }
        }

        // 4. 收集需要删除的记录ID
        List<Integer> toDelIds = new ArrayList<>();
        for (Map.Entry<String, MallFullReductionGoods> entry : dbSkuMap.entrySet()) {
            if (!inputSkuKeys.contains(entry.getKey())) {
                toDelIds.add(entry.getValue().getId());
            }
        }

        // 5. 执行批量新增和删除
        if (!toAdd.isEmpty()) {
            fullReductionGoodsMapper.insert(toAdd);
        }
        if (!toDelIds.isEmpty()) {
            fullReductionGoodsMapper.deleteByIds(toDelIds);
        }
    }

    /**
     * A product-level front warehouse selection represents all of that goods'
     * SKUs. The selector sends an empty sku_ids array for this case.
     */
    private List<Integer> getAllSkuIds(MallFullReduction fullReduction, Integer goodsId) {
        if (!"front_warehouse".equals(fullReduction.getAddon())) {
            return Collections.emptyList();
        }
        FullReductionGoodsEventDefiner.FullReductionGoodsEvent event =
                new FullReductionGoodsEventDefiner.FullReductionGoodsEvent();
        event.setSiteId(fullReduction.getSiteId());
        event.setTradeType(fullReduction.getAddon());
        event.setGoodsId(goodsId);
        event.setAllSkus(true);
        event.setAuthority(true);
        event.addAppSign(fullReduction.getAddon());
        return CallbackPublisher.<FullReductionGoodsEventDefiner.FullReductionGoodsResult>publishEventReturnList(event)
                .stream()
                .filter(Objects::nonNull)
                .map(FullReductionGoodsEventDefiner.FullReductionGoodsResult::getSkuIds)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(Collections::emptyList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(Integer id) {
        MallFullReduction fullReduction = fullReductionMapper.selectById(id);
        if (ObjectUtil.isNull(fullReduction)) {
            throw new CommonException("满减活动不存在");
        }

        fullReductionMapper.deleteById(fullReduction);

        LambdaQueryWrapper<MallFullReductionGoods> goodsQueryWrapper = new LambdaQueryWrapper<>();
        goodsQueryWrapper.eq(MallFullReductionGoods::getFullReductionId, id);
        fullReductionGoodsMapper.delete(goodsQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setStatus(Integer id, String status) {
        if (FullReductionStatusEnum.getEnumByStatus(status) == null) {
            throw new CommonException("状态错误");
        }
        MallFullReduction fullReduction = fullReductionMapper.selectById(id);
        if (ObjectUtil.isNull(fullReduction)) {
            throw new CommonException("满减活动不存在");
        }

        fullReduction.setStatus(status);
        fullReduction.setUpdateTime(System.currentTimeMillis() / 1000);

        fullReductionGoodsMapper.update(new LambdaUpdateWrapper<MallFullReductionGoods>()
                .set(MallFullReductionGoods::getStatus, status)
                .eq(MallFullReductionGoods::getFullReductionId, fullReduction.getId()));

        fullReductionMapper.updateById(fullReduction);
    }

    private String getStatus(Long startTime, Long endTime) {
        var time = new Date().getTime() / 1000;
        if (startTime > time) {
            return FullReductionStatusEnum.WAIT.getStatus();
        } else if (time < endTime) {
            return FullReductionStatusEnum.PROCESS.getStatus();
        } else {
            return FullReductionStatusEnum.END.getStatus();
        }
    }

    /**
     * 格式化活动数据（将前端参数转换为实体对象）
     *
     * @param data 前端传入的参数
     * @return FullReduction 实体
     */
    private MallFullReduction formatActiveData(FullReductionParam data) {
        MallFullReduction activeData = BeanUtil.copyProperties(data, MallFullReduction.class);
        if (!org.springframework.util.StringUtils.hasText(activeData.getAddon())) {
            List<String> addonKeys = com.niu.core.common.component.context.SpringContext
                    .bean(com.niu.core.service.core.site.ICoreSiteService.class)
                    .getAddonKeysBySiteId(RequestUtils.siteId());
            if (addonKeys.contains("front_warehouse")) {
                activeData.setAddon("front_warehouse");
            }
        }
        activeData.setStatus(getStatus(activeData.getStartTime(), activeData.getEndTime()));
        activeData.setRuleJson(JSONUtil.toJsonStr(data.getRuleJson()));
        activeData.setMemberTypeJson(JSONUtil.toJsonStr(data.getMemberTypeJson()));
        return activeData;
    }

    @Override
    public Map<String, Object> checkGoods(FullReductionParam param) {
        return checkGoods(param, false);
    }

    /**
     * 满减送商品校验
     * 校验逻辑：
     * 1. 同一时间段内，只允许存在一个全部商品的活动
     * 2. 同一时间段内：
     * - 允许存在多个指定商品参与的活动
     * - 或一个指定商品不参与的活动（有了该类型则不允许添加指定商品参与的活动）
     *
     * @param data    请求参数（包含 start_time, end_time, goods_type, goods_data, id 等）
     * @param isThrow 是否抛出异常（true=直接抛异常，false=返回错误信息）
     * @return Map 包含 code(1=无冲突, -1=有冲突) 和 data(错误详情)
     */
    public Map<String, Object> checkGoods(FullReductionParam data, Boolean isThrow) {
        // 初始化返回结果
        Map<String, Object> error = new HashMap<>();
        error.put("code", 1);
        error.put("data", new ArrayList<>());

        Long startTime = data.getStartTime();
        Long endTime = data.getEndTime();
        String goodsType = data.getGoodsType();
        Integer id = data.getId();

        // 时间重叠条件（Lambda 表达式复用）
        LambdaQueryWrapper<MallFullReduction> timeWrapper = new LambdaQueryWrapper<>();
        timeWrapper.le(MallFullReduction::getStartTime, endTime)
                .ge(MallFullReduction::getEndTime, startTime);

        // ========== 规则1：同一时间段只能有一个“全部商品”活动 ==========
        if (FullReductionGoodsTypeEnum.ALL.getType().equals(goodsType)) {
            // 查询同时间段、同站点、未结束的全部商品活动数量
            long allGoodsCount = fullReductionMapper.selectCount(Wrappers.<MallFullReduction>lambdaQuery()
                    .eq(MallFullReduction::getSiteId, RequestUtils.siteId())
                    .eq(MallFullReduction::getGoodsType, FullReductionGoodsTypeEnum.ALL.getType())
                    .in(MallFullReduction::getStatus, Arrays.asList(FullReductionStatusEnum.WAIT.getStatus(), FullReductionStatusEnum.PROCESS.getStatus()))
                    .ne(id != null, MallFullReduction::getId, id)
                    .le(MallFullReduction::getStartTime, endTime)
                    .ge(MallFullReduction::getEndTime, startTime));
            if (allGoodsCount > 0) {
                if (isThrow) {
                    throw new AdminException("同一时间段内只能存在一个全部商品类型的满减活动");
                }
            }
        } else {
            // 获取传入的商品ID列表
            List<Integer> addGoodsIds = data.getGoodsData().stream()
                    .map(FullReductionParam.GoodsData::getGoodsId).toList();

            // ========== 规则2：指定商品不参与活动 ==========
            if (FullReductionGoodsTypeEnum.SELECTED_NOT.getType().equals(goodsType)) {
                // 检查是否已有其他“指定商品不参与”活动（只能有一个）
                long excludeCount = fullReductionMapper.selectCount(Wrappers.<MallFullReduction>lambdaQuery()
                        .eq(MallFullReduction::getSiteId, RequestUtils.siteId())
                        .eq(MallFullReduction::getGoodsType, FullReductionGoodsTypeEnum.SELECTED_NOT.getType())
                        .in(MallFullReduction::getStatus, Arrays.asList(FullReductionStatusEnum.WAIT.getStatus(), FullReductionStatusEnum.PROCESS.getStatus()))
                        .ne(id != null, MallFullReduction::getId, id)
                        .le(MallFullReduction::getStartTime, endTime)
                        .ge(MallFullReduction::getEndTime, startTime));
                if (excludeCount > 0) {
                    if (isThrow) {
                        throw new AdminException("已有主商品已参与其他活动");
                    } else {
                        for (Integer goodsId : addGoodsIds) {
                            ((List<Map<String, Object>>) error.get("data")).add(Map.of(
                                    "goods_id", goodsId,
                                    "error_msg", "同一时间段内只能存在一个指定商品不参与类型的满减活动"
                            ));
                        }
                    }
                }

                // 查询所有“指定商品参与”的活动ID（同一时间段内，排除自身）
                List<Integer> selectedActiveIds = fullReductionMapper.selectList(Wrappers.<MallFullReduction>lambdaQuery()
                                .eq(MallFullReduction::getSiteId, RequestUtils.siteId())
                                .eq(MallFullReduction::getGoodsType, FullReductionGoodsTypeEnum.SELECTED.getType())
                                .in(MallFullReduction::getStatus, Arrays.asList(FullReductionStatusEnum.WAIT.getStatus(), FullReductionStatusEnum.PROCESS.getStatus()))
                                .ne(id != null, MallFullReduction::getId, id)
                                .le(MallFullReduction::getStartTime, endTime)
                                .ge(MallFullReduction::getEndTime, startTime))
                        .stream().map(MallFullReduction::getId).collect(Collectors.toList());

                if (!selectedActiveIds.isEmpty()) {
                    // 获取这些活动中包含的商品ID（去重）
                    List<Integer> selectedGoodsList = fullReductionGoodsMapper.selectList(Wrappers.<MallFullReductionGoods>lambdaQuery()
                                    .in(MallFullReductionGoods::getFullReductionId, selectedActiveIds)
                                    .select(MallFullReductionGoods::getGoodsId))
                            .stream().map(MallFullReductionGoods::getGoodsId).distinct().toList();
                    //通过当前正在参加活动的ID和排除的Id得到重复参加活动的商品ID
                    //selectedGoodsList中 不被 addGoodsIds 包含的即为重复参加活动
                    if (!selectedGoodsList.isEmpty() && !new HashSet<>(addGoodsIds).containsAll(selectedGoodsList)) {
//                        List<Integer> repeatIds = selectedGoodsList.stream().filter(repeatId -> !addGoodsIds.contains(repeatId)).toList();
//                        if (!repeatIds.isEmpty()) {
                        for (Integer goodsId : addGoodsIds) {
                            if (isThrow) {
                                throw new AdminException("同一商品在一个时间段内只能参加一个满减活动");
                            } else {
                                ((List<Map<String, Object>>) error.get("data")).add(Map.of(
                                        "goods_id", goodsId,
                                        "error_msg", "当前活动设置会导致部分商品参与多个满减活动"
                                ));
                            }
                        }
//                        }
                    }
                }
            }

            // ========== 规则3：指定商品参与活动 ==========
            if (FullReductionGoodsTypeEnum.SELECTED.getType().equals(goodsType) && CollectionUtils.isNotEmpty(data.getGoodsData())) {
                if (!addGoodsIds.isEmpty()) {
                    // 查询冲突的商品ID（同一时间段内其他“指定商品参与”活动中已存在的商品）
                    var sql1 = "SELECT id FROM ?_mall_full_reduction WHERE site_id = " + RequestUtils.siteId() +
                            " AND goods_type = '" + FullReductionGoodsTypeEnum.SELECTED.getType() + "'" +
                            " AND status IN ('" + FullReductionStatusEnum.WAIT.getStatus() + "','" + FullReductionStatusEnum.PROCESS.getStatus() + "')" +
                            (id != null ? " AND id <> " + id : "") +
                            " AND start_time <= " + endTime + " AND end_time >= " + startTime;
                    List<Integer> conflictGoodsIds = fullReductionGoodsMapper.selectList(Wrappers.<MallFullReductionGoods>lambdaQuery()
                                    .eq(MallFullReductionGoods::getSiteId, RequestUtils.siteId())
                                    .inSql(MallFullReductionGoods::getFullReductionId, sql1.replace("?_", GlobalConfig.tablePrefix))
                                    .select(MallFullReductionGoods::getGoodsId))
                            .stream().map(MallFullReductionGoods::getGoodsId).toList();

                    List<Integer> conflictIntersect = conflictGoodsIds.stream()
                            .filter(addGoodsIds::contains)
                            .collect(Collectors.toList());

                    if (!conflictIntersect.isEmpty()) {
                        // 查询冲突活动的商品
                        var sql2 = "SELECT id FROM ?_mall_full_reduction WHERE site_id = " + RequestUtils.siteId() +
                                " AND status IN ('" + FullReductionStatusEnum.WAIT.getStatus() + "','" + FullReductionStatusEnum.PROCESS.getStatus() + "')" +
                                " AND start_time <= " + endTime + " AND end_time >= " + startTime;
                        var goods = fullReductionGoodsMapper.selectList(Wrappers.<MallFullReductionGoods>lambdaQuery()
                                .eq(MallFullReductionGoods::getSiteId, RequestUtils.siteId())
                                .in(MallFullReductionGoods::getGoodsId, conflictIntersect)
                                .inSql(MallFullReductionGoods::getFullReductionId, sql2.replace("?_", GlobalConfig.tablePrefix)));

                        //冲突的活动ID
                        List<Integer> fullReductionIds = goods.stream().map(MallFullReductionGoods::getFullReductionId).toList();
                        //冲突的活动标题
                        var fullReductionTitles = fullReductionMapper.selectList(new LambdaQueryWrapper<MallFullReduction>()
                                .in(MallFullReduction::getId, fullReductionIds)
                                .select(MallFullReduction::getId, MallFullReduction::getTitle)
                        ).stream().collect(Collectors.toMap(MallFullReduction::getId, MallFullReduction::getTitle));

                        List<Map<String, Object>> conflictGoodsInfo = goods.stream()
                                .map(g -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("goods_id", g.getGoodsId());
                                    map.put("title", fullReductionTitles.get(g.getFullReductionId()) != null ?
                                            fullReductionTitles.get(g.getFullReductionId()) : "未知活动 id: " + g.getFullReductionId());
                                    return map;
                                }).toList();
                        Map<Integer, String> goodsTitleMap = new HashMap<>();
                        for (Map<String, Object> item : conflictGoodsInfo) {
                            goodsTitleMap.put((Integer) item.get("goods_id"), (String) item.get("title"));
                        }

                        for (Integer goodsId : conflictIntersect) {
                            String activityTitle = goodsTitleMap.getOrDefault(goodsId, "未知");
                            String errorMsg = String.format("该商品已参加【%s】满减活动，无法重复参与", activityTitle);
                            ((List<Map<String, Object>>) error.get("data")).add(Map.of(
                                    "goods_id", goodsId,
                                    "error_msg", errorMsg
                            ));
                        }
                    }

                    // 查询“指定商品不参与”的活动ID
                    List<Integer> selectedNotActiveIds = fullReductionMapper.selectList(Wrappers.<MallFullReduction>lambdaQuery()
                                    .eq(MallFullReduction::getSiteId, RequestUtils.siteId())
                                    .eq(MallFullReduction::getGoodsType, FullReductionGoodsTypeEnum.SELECTED_NOT.getType())
                                    .in(MallFullReduction::getStatus, Arrays.asList(FullReductionStatusEnum.WAIT.getStatus(), FullReductionStatusEnum.PROCESS.getStatus()))
                                    .ne(id != null, MallFullReduction::getId, id)
                                    .le(MallFullReduction::getStartTime, endTime)
                                    .ge(MallFullReduction::getEndTime, startTime))
                            .stream().map(MallFullReduction::getId).collect(Collectors.toList());

                    if (!selectedNotActiveIds.isEmpty()) {
                        // 获取这些活动中包含的商品ID（去重）
                        List<Integer> selectedGoodsNotList = fullReductionGoodsMapper.selectList(Wrappers.<MallFullReductionGoods>lambdaQuery()
                                        .in(MallFullReductionGoods::getFullReductionId, selectedNotActiveIds)
                                        .select(MallFullReductionGoods::getGoodsId))
                                .stream().map(MallFullReductionGoods::getGoodsId).distinct().toList();
                        if (!selectedGoodsNotList.isEmpty()) {
                            for (Integer goodsId : addGoodsIds) {
                                if (!selectedGoodsNotList.contains(goodsId)) {
                                    if (isThrow) {
                                        throw new AdminException("同一商品在一个时间段内只能参加一个满减活动");
                                    } else {
                                        ((List<Map<String, Object>>) error.get("data")).add(Map.of(
                                                "goods_id", goodsId,
                                                "error_msg", "当前活动设置会导致部分商品参与多个满减活动"
                                        ));
                                    }
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
            throw new AdminException("同一商品在一个时间段内只能参加一个满减活动");
        }
        return error;
    }

}
