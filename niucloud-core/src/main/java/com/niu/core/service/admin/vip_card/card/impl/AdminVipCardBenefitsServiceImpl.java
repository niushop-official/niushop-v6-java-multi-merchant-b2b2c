package com.niu.core.service.admin.vip_card.card.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.vip_card.MemberVipCard;
import com.niu.core.entity.vip_card.MemberVipCardBenefits;
import com.niu.core.entity.vip_card.MemberVipCardBenefitsItem;
import com.niu.core.enums.vip_card.VipCardBenefitsStatusEnum;
import com.niu.core.mapper.vip_card.VipCardBenefitsItemMapper;
import com.niu.core.mapper.vip_card.VipCardBenefitsMapper;
import com.niu.core.mapper.vip_card.VipCardMapper;
import com.niu.core.service.admin.vip_card.card.IAdminVipCardBenefitsService;
import com.niu.core.service.admin.vip_card.card.param.AdminBenefitsListParam;
import com.niu.core.service.admin.vip_card.card.param.AdminModifySortParam;
import com.niu.core.service.admin.vip_card.card.param.AdminModifyStatusParam;
import com.niu.core.service.admin.vip_card.card.param.AdminSetConfigParam;
import com.niu.core.service.admin.vip_card.card.vo.AdminBenefitsConfigVo;
import com.niu.core.service.admin.vip_card.card.vo.AdminBenefitsListVo;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.vip_card.card.benefits.BenefitsConfig;
import com.niu.core.service.core.vip_card.card.benefits.BenefitsContentHandler;
import com.niu.core.service.core.vip_card.card.benefits.BenefitsDictLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminVipCardBenefitsServiceImpl implements IAdminVipCardBenefitsService {

    @Autowired
    private VipCardBenefitsMapper benefitsMapper;

    @Autowired
    private VipCardBenefitsItemMapper benefitsItemMapper;

    @Autowired
    private VipCardMapper vipCardMapper;

    @Autowired
    private ICoreMemberService coreMemberService;

    @Autowired
    private BenefitsDictLoader benefitsDictLoader;

    // ========== 会员卡权益分页列表 ==========
    @Override
    public PageResult<AdminBenefitsListVo> getPage(PageParam pageParam, AdminBenefitsListParam param) {
        LambdaQueryWrapper<MemberVipCardBenefits> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(param.getName())) {
            wrapper.like(MemberVipCardBenefits::getName, param.getName());
        }
        if (StrUtil.isNotBlank(param.getType())) {
            wrapper.eq(MemberVipCardBenefits::getType, param.getType());
        }
        wrapper.orderByDesc(MemberVipCardBenefits::getCreateTime);

        IPage<MemberVipCardBenefits> page = benefitsMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()), wrapper);

        List<AdminBenefitsListVo> voList = new ArrayList<>();
        for (MemberVipCardBenefits benefits : page.getRecords()) {
            AdminBenefitsListVo vo = new AdminBenefitsListVo();
            BeanUtil.copyProperties(benefits, vo);
            vo.setStatusName(VipCardBenefitsStatusEnum.getNameByCode(benefits.getStatus()));
            // 查询权益项
            List<MemberVipCardBenefitsItem> items = benefitsItemMapper.selectList(
                    new LambdaQueryWrapper<MemberVipCardBenefitsItem>()
                            .eq(MemberVipCardBenefitsItem::getBenefitsId, benefits.getBenefitsId()));
            if (CollUtil.isNotEmpty(items)) {
                vo.setItemList(items.stream().map(this::toBenefitsListItemVo).collect(Collectors.toList()));
            } else {
                vo.setItemList(new ArrayList<>());
            }
            voList.add(vo);
        }
        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal(), voList);
    }

    // ========== 列表 ==========
    @Override
    public List<AdminBenefitsListVo> getList(AdminBenefitsListParam param) {
        LambdaQueryWrapper<MemberVipCardBenefits> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberVipCardBenefits::getStatus, 1)
                .orderByDesc(MemberVipCardBenefits::getCreateTime);

        if (StrUtil.isNotBlank(param.getName())) {
            wrapper.like(MemberVipCardBenefits::getName, param.getName());
        }
        if (StrUtil.isNotBlank(param.getType())) {
            wrapper.eq(MemberVipCardBenefits::getType, param.getType());
        }

        List<MemberVipCardBenefits> benefits = benefitsMapper.selectList(wrapper);
        if (CollUtil.isEmpty(benefits)) {
            return Collections.emptyList();
        }

        // 批量查询权益项
        List<Long> benefitsIds = benefits.stream().map(MemberVipCardBenefits::getBenefitsId).collect(Collectors.toList());
        List<MemberVipCardBenefitsItem> items = benefitsItemMapper.selectList(
                new LambdaQueryWrapper<MemberVipCardBenefitsItem>().in(MemberVipCardBenefitsItem::getBenefitsId, benefitsIds)
        );
        Map<Long, List<MemberVipCardBenefitsItem>> itemMap = items.stream()
                .collect(Collectors.groupingBy(MemberVipCardBenefitsItem::getBenefitsId));

        return benefits.stream().map(b -> {
            AdminBenefitsListVo vo = new AdminBenefitsListVo();
            BeanUtil.copyProperties(b, vo);
            vo.setStatusName(VipCardBenefitsStatusEnum.getNameByCode(b.getStatus()));
            List<MemberVipCardBenefitsItem> itemList = itemMap.get(b.getBenefitsId());
            if (CollUtil.isNotEmpty(itemList)) {
                vo.setItemList(itemList.stream().map(this::toBenefitsListItemVo).collect(Collectors.toList()));
            } else {
                vo.setItemList(new ArrayList<>());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    // ========== 获取配置 ==========
    @Override
    public AdminBenefitsConfigVo getConfig(String type) {
        // 先从枚举获取基础配置
        BenefitsConfig benefitsConfig = benefitsDictLoader.getConfig(type);
        AdminBenefitsConfigVo vo = new AdminBenefitsConfigVo();
        if (benefitsConfig != null) {
            vo.setType(benefitsConfig.getType());
            vo.setName(benefitsConfig.getName());
            vo.setIcon(normalizeIconPath(benefitsConfig.getIcon()));
            vo.setDesc(benefitsConfig.getDesc());
            vo.setAddon(benefitsConfig.getAddon());
        }

        // 从数据库查询已有配置
        LambdaQueryWrapper<MemberVipCardBenefits> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberVipCardBenefits::getType, type);
        MemberVipCardBenefits benefits = benefitsMapper.selectOne(wrapper);

        if (benefits != null) {
            BeanUtil.copyProperties(benefits, vo);
            vo.setIcon(normalizeIconPath(vo.getIcon()));
            List<MemberVipCardBenefitsItem> items = benefitsItemMapper.selectList(
                    new LambdaQueryWrapper<MemberVipCardBenefitsItem>()
                            .eq(MemberVipCardBenefitsItem::getBenefitsId, benefits.getBenefitsId())
            );
            if (CollUtil.isNotEmpty(items)) {
                vo.setItemList(items.stream().map(this::toBenefitsConfigItemVo).collect(Collectors.toList()));
            } else {
                vo.setItemList(new ArrayList<>());
            }
        } else {
            vo.setBenefitsId(null);
            vo.setExplain("");
            vo.setStatus(0);
            vo.setSort(0);
            vo.setItemList(new ArrayList<>());
        }

        return vo;
    }

    // ========== 编辑配置 ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setConfig(String type, AdminSetConfigParam param) {
        LambdaQueryWrapper<MemberVipCardBenefits> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberVipCardBenefits::getType, type);
        MemberVipCardBenefits benefits = benefitsMapper.selectOne(wrapper);

        MemberVipCardBenefits updateData = new MemberVipCardBenefits();
        updateData.setName(param.getName());
        updateData.setIcon(normalizeIconPath(param.getIcon()));
        updateData.setType(param.getType());
        updateData.setDesc(param.getDesc());
        updateData.setExplain(param.getExplain());
        updateData.setAddon(param.getAddon());
        updateData.setStatus(param.getStatus());
        updateData.setIsSystem(1);

        if (param.getItemList() == null) param.setItemList(new ArrayList<>());

        if (benefits == null) {
            // 新增
            updateData.setCreateTime(System.currentTimeMillis() / 1000);
            benefitsMapper.insert(updateData);
            // 获取主键
            Long benefitsId = updateData.getBenefitsId();
            // 插入权益项
            if (CollUtil.isNotEmpty(param.getItemList())) {
                List<MemberVipCardBenefitsItem> items = param.getItemList().stream()
                        .map(itemParam -> toBenefitsItemEntity(itemParam, benefitsId))
                        .collect(Collectors.toList());
                benefitsItemMapper.insert(items);
            }
        } else {
            // 更新
            updateData.setUpdateTime(System.currentTimeMillis() / 1000);
            // 检查如果状态变为关闭且被卡使用则报错
            if (VipCardBenefitsStatusEnum.OFF.getCode().equals(param.getStatus())) {
                long count = vipCardMapper.selectCount(
                        new LambdaQueryWrapper<MemberVipCard>().apply("FIND_IN_SET({0}, benefits_id)", benefits.getBenefitsId())
                );
                if (count > 0) {
                    throw new AdminException("VIP_CARD_BENEFITS_USED");
                }
            }
            benefitsMapper.update(updateData, new LambdaUpdateWrapper<MemberVipCardBenefits>()
                    .eq(MemberVipCardBenefits::getType, type));

            // 处理权益项
            List<Integer> existItemIds = benefitsItemMapper.selectList(
                    new LambdaQueryWrapper<MemberVipCardBenefitsItem>()
                            .eq(MemberVipCardBenefitsItem::getBenefitsId, benefits.getBenefitsId())
                            .select(MemberVipCardBenefitsItem::getBenefitsItemId)
            ).stream().map(u -> Math.toIntExact(u.getBenefitsItemId())).toList();

            List<Integer> inputItemIds = param.getItemList().stream()
                    .map(AdminSetConfigParam.BenefitsItem::getBenefitsItemId)
                    .filter(Objects::nonNull)
                    .toList();

            // 新增项（无id）
            List<AdminSetConfigParam.BenefitsItem> toAdd = param.getItemList().stream()
                    .filter(item -> item.getBenefitsItemId() == null)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(toAdd)) {
                List<MemberVipCardBenefitsItem> addItems = toAdd.stream()
                        .map(itemParam -> toBenefitsItemEntity(itemParam, benefits.getBenefitsId()))
                        .collect(Collectors.toList());
                benefitsItemMapper.insert(addItems);
            }

            // 更新项（有id）
            List<AdminSetConfigParam.BenefitsItem> toUpdate = param.getItemList().stream()
                    .filter(item -> item.getBenefitsItemId() != null)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(toUpdate)) {
                for (AdminSetConfigParam.BenefitsItem itemParam : toUpdate) {
                    MemberVipCardBenefitsItem updateItem = toBenefitsItemEntity(itemParam, benefits.getBenefitsId());
                    benefitsItemMapper.updateById(updateItem);
                }
            }

            // 删除项（不在输入中）
            List<Integer> delIds = existItemIds.stream()
                    .filter(id -> !inputItemIds.contains(id))
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(delIds)) {
                // 检查是否被卡使用
                long usedCount = vipCardMapper.selectCount(
                        new LambdaQueryWrapper<MemberVipCard>().apply("FIND_IN_SET({0}, benefits_item_id)", delIds)
                );
                if (usedCount > 0) {
                    throw new AdminException("VIP_CARD_BENEFITS_ITEM_USED");
                }
                benefitsItemMapper.deleteByIds(delIds);
            }
        }
    }

    // ========== 修改状态 ==========
    @Override
    public void modifyStatus(AdminModifyStatusParam param) {
        if (param.getBenefitsId() == null) {
            throw new AdminException("VIP_CARD_BENEFITS_NOT_SET");
        }
        long count = vipCardMapper.selectCount(
                new LambdaQueryWrapper<MemberVipCard>().apply("FIND_IN_SET({0}, benefits_id)", param.getBenefitsId())
        );
        if (Objects.equals(param.getStatus(), VipCardBenefitsStatusEnum.OFF.getCode()) && count > 0) {
            throw new AdminException("VIP_CARD_BENEFITS_USED");
        }
        benefitsMapper.update(null,
                new LambdaUpdateWrapper<MemberVipCardBenefits>()
                        .eq(MemberVipCardBenefits::getBenefitsId, param.getBenefitsId())
                        .set(MemberVipCardBenefits::getStatus, param.getStatus())
        );
    }

    // ========== 修改排序 ==========
    @Override
    public void modifySort(AdminModifySortParam param) {
        if (param.getBenefitsId() == null) {
            throw new AdminException("VIP_CARD_BENEFITS_NOT_SET");
        }
        benefitsMapper.update(null,
                new LambdaUpdateWrapper<MemberVipCardBenefits>()
                        .eq(MemberVipCardBenefits::getBenefitsId, param.getBenefitsId())
                        .set(MemberVipCardBenefits::getSort, param.getSort())
        );
    }

    // ========== 获取字典 ==========
    @Override
    public Map<String, Map<String, Object>> getBenefitsDict() {
        // 从枚举加载所有权益类型
        Map<String, Map<String, Object>> dict = new LinkedHashMap<>();
        Map<String, BenefitsConfig> benefitsConfigMap = benefitsDictLoader.getDict();
        for (String key : benefitsConfigMap.keySet()) {
            BenefitsConfig benefitsConfig = benefitsConfigMap.get(key);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("type", benefitsConfig.getType());
            item.put("name", benefitsConfig.getName());
            item.put("icon", normalizeIconPath(benefitsConfig.getIcon()));
            item.put("desc", benefitsConfig.getDesc());
            item.put("addon", benefitsConfig.getAddon());
            item.put("component", benefitsConfig.getComponent());
            // 注意：content 这里不直接存储函数，而是存储一个标识或空，因为函数无法序列化到JSON
            // 但字典通常用于前端展示，这里可以留空，或者存一个描述
            item.put("content", Map.of("admin", new HashMap())); // 或者保留Map结构供前端使用

            // 设置未进行配置时的默认值
            item.put("benefits_id", 0);
            item.put("status", 0);
            item.put("explain", "");
            item.put("sort", 0);
            item.put("itemList", new ArrayList<>());
            item.put("item_list", new ArrayList<>());
            dict.put(key, item);
        }

        // 合并数据库中的配置（如 status、explain 等）
        List<MemberVipCardBenefits> benefitsList = benefitsMapper.selectList(null);
        List<Long> benefitsIds = benefitsList.stream()
                .map(MemberVipCardBenefits::getBenefitsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, List<Map<String, Object>>> itemMap = new HashMap<>();
        if (CollUtil.isNotEmpty(benefitsIds)) {
            List<MemberVipCardBenefitsItem> benefitsItems = benefitsItemMapper.selectList(
                    new LambdaQueryWrapper<MemberVipCardBenefitsItem>()
                            .in(MemberVipCardBenefitsItem::getBenefitsId, benefitsIds)
            );
            itemMap = benefitsItems.stream()
                    .collect(Collectors.groupingBy(
                            MemberVipCardBenefitsItem::getBenefitsId,
                            Collectors.mapping(this::toBenefitsDictItem, Collectors.toList())
                    ));
        }
        for (MemberVipCardBenefits benefit : benefitsList) {
            String key = benefit.getType();
            if (dict.containsKey(key)) {
                Map<String, Object> item = dict.get(key);
                // 数据库配置覆盖字典默认值
                item.put("benefits_id", benefit.getBenefitsId());
                item.put("status", benefit.getStatus());
                item.put("explain", benefit.getExplain());
                item.put("sort", benefit.getSort());
                // 其他字段若需要也可覆盖
                if (StringUtils.isNotEmpty(benefit.getIcon())) item.put("icon", normalizeIconPath(benefit.getIcon()));
                if (StringUtils.isNotEmpty(benefit.getName())) item.put("name", benefit.getName());
                if (StringUtils.isNotEmpty(benefit.getDesc())) item.put("desc", benefit.getDesc());
                List<Map<String, Object>> benefitsItemList = itemMap.getOrDefault(benefit.getBenefitsId(), new ArrayList<>());
                item.put("itemList", benefitsItemList);
                item.put("item_list", benefitsItemList);
            }
        }

        return dict;
    }

    // ========== 获取权益内容 ==========
    @Override
    public Map<String, Object> getBenefitsContent(Integer siteId, JSONObject benefits, String scene) {
        if (benefits == null) return new HashMap<>();
        Map<String, Object> result = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : benefits.entrySet()) {
            String type = entry.getKey();
            Map<String, Object> config = toConfigMap(entry.getValue());
            Map<String, Object> enriched = new LinkedHashMap<>(config);

            // 处理 gifts，统一转为 JSONObject
            if (config.containsKey("gifts")) {
                Object giftsObj = config.get("gifts");
                JSONObject giftsJson = null;
                if (giftsObj instanceof JSONObject) {
                    giftsJson = (JSONObject) giftsObj;
                } else if (giftsObj instanceof Map) {
                    giftsJson = new JSONObject(giftsObj);
                } else {
                    // 如果是字符串或其他类型，尝试解析
                    try {
                        giftsJson = JSONUtil.parseObj(giftsObj);
                    } catch (Exception e) {
                        // 解析失败则忽略
                    }
                }
                if (giftsJson != null) {
                    Map<String, Object> giftContent = coreMemberService.getGiftContent(siteId, giftsJson, "admin");
                    enriched.put("gifts_content", giftContent);
                }
            }

            // 获取对应枚举的处理器
            BenefitsConfig benefitsConfig = benefitsDictLoader.getConfig(type);
            if (benefitsConfig != null) {
                Map<String, BenefitsContentHandler> contentHandlers = benefitsConfig.getContent();
                if (contentHandlers != null && contentHandlers.containsKey(scene)) {
                    BenefitsContentHandler handler = contentHandlers.get(scene);
                    if (handler != null) {
                        // 先准备好已包含 gifts_content 的 config，再传给 handler
                        try {
                            String content = handler.handle(siteId, enriched);
                            if (content != null) {
                                enriched.put("content", content);
                            }
                        } catch (Exception ignored) {
                            // 单个权益 content 失败不影响整单详情
                            log.error("获取权益内容出错", ignored);
                        }
                    }
                }
            }

            result.put(type, enriched);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toConfigMap(Object value) {
        if (value == null) {
            return new HashMap<>();
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new LinkedHashMap<>();
            map.forEach((k, v) -> result.put(String.valueOf(k), v));
            return result;
        }
        if (value instanceof JSONObject jsonObject) {
            return new LinkedHashMap<>(jsonObject);
        }
        if (value instanceof CharSequence str) {
            if (StrUtil.isBlank(str.toString())) {
                return new HashMap<>();
            }
            try {
                Object parsed = JSONUtil.parse(str.toString());
                if (parsed instanceof JSONObject jo) {
                    return new LinkedHashMap<>(jo);
                }
                if (parsed instanceof Map<?, ?> parsedMap) {
                    Map<String, Object> result = new LinkedHashMap<>();
                    parsedMap.forEach((k, v) -> result.put(String.valueOf(k), v));
                    return result;
                }
            } catch (Exception ignored) {
            }
        }
        return new HashMap<>();
    }

    private AdminBenefitsListVo.BenefitsItemVo toBenefitsListItemVo(MemberVipCardBenefitsItem item) {
        AdminBenefitsListVo.BenefitsItemVo itemVo = new AdminBenefitsListVo.BenefitsItemVo();
        if (item.getBenefitsItemId() != null) {
            itemVo.setBenefitsItemId(item.getBenefitsItemId().intValue());
        }
        itemVo.setName(item.getName());
        itemVo.setBenefitsConfig(parseBenefitsConfig(item.getBenefitsConfig()));
        return itemVo;
    }

    private AdminBenefitsConfigVo.BenefitsItemVo toBenefitsConfigItemVo(MemberVipCardBenefitsItem item) {
        AdminBenefitsConfigVo.BenefitsItemVo itemVo = new AdminBenefitsConfigVo.BenefitsItemVo();
        if (item.getBenefitsItemId() != null) {
            itemVo.setBenefitsItemId(item.getBenefitsItemId().intValue());
        }
        itemVo.setName(item.getName());
        itemVo.setBenefitsConfig(parseBenefitsConfig(item.getBenefitsConfig()));
        return itemVo;
    }

    private MemberVipCardBenefitsItem toBenefitsItemEntity(AdminSetConfigParam.BenefitsItem itemParam, Long benefitsId) {
        MemberVipCardBenefitsItem item = new MemberVipCardBenefitsItem();
        if (itemParam.getBenefitsItemId() != null) {
            item.setBenefitsItemId(itemParam.getBenefitsItemId().longValue());
        }
        item.setBenefitsId(benefitsId);
        item.setName(itemParam.getName());
        item.setBenefitsConfig(serializeBenefitsConfig(itemParam.getBenefitsConfig()));
        return item;
    }

    private Map<String, Object> toBenefitsDictItem(MemberVipCardBenefitsItem item) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("benefits_item_id", item.getBenefitsItemId());
        result.put("benefitsItemId", item.getBenefitsItemId());
        result.put("name", item.getName());
        Object benefitsConfig = parseBenefitsConfig(item.getBenefitsConfig());
        result.put("benefits_config", benefitsConfig);
        result.put("benefitsConfig", benefitsConfig);
        return result;
    }

    private Object parseBenefitsConfig(String benefitsConfig) {
        if (StrUtil.isBlank(benefitsConfig)) {
            return defaultGiftBenefitsConfig();
        }
        try {
            return JSONUtil.parse(benefitsConfig);
        } catch (Exception e) {
            return defaultGiftBenefitsConfig();
        }
    }

    private String serializeBenefitsConfig(Object benefitsConfig) {
        if (benefitsConfig == null) {
            return JSONUtil.toJsonStr(defaultGiftBenefitsConfig());
        }
        if (benefitsConfig instanceof String configStr) {
            return StrUtil.isBlank(configStr) ? JSONUtil.toJsonStr(defaultGiftBenefitsConfig()) : configStr;
        }
        return JSONUtil.toJsonStr(benefitsConfig);
    }

    private JSONObject defaultGiftBenefitsConfig() {
        JSONObject balance = new JSONObject();
        balance.set("is_use", 0);
        balance.set("money", "");
        JSONObject point = new JSONObject();
        point.set("is_use", 0);
        point.set("num", "");
        JSONObject shopCoupon = new JSONObject();
        shopCoupon.set("is_use", 0);
        shopCoupon.set("coupon_id", new ArrayList<>());
        shopCoupon.set("coupon_list", new JSONObject());
        JSONObject gifts = new JSONObject();
        gifts.set("balance", balance);
        gifts.set("point", point);
        gifts.set("shop_coupon", shopCoupon);
        JSONObject config = new JSONObject();
        config.set("gifts", gifts);
        return config;
    }

    private String normalizeIconPath(String icon) {
        if (StringUtils.isEmpty(icon)) {
            return icon;
        }
        if (icon.startsWith("http://") || icon.startsWith("https://") || icon.startsWith("data:")) {
            return icon;
        }
        if (icon.startsWith("/static/") || icon.startsWith("/addon/")) {
            return icon.substring(1);
        }
        return icon;
    }

}
