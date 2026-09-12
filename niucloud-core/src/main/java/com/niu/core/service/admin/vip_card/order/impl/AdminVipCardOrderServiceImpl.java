package com.niu.core.service.admin.vip_card.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.entity.vip_card.MemberVipCardBuy;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.entity.vip_card.MemberVipCardOrderItem;
import com.niu.core.entity.vip_card.MemberVipCardOrderLog;
import com.niu.core.enums.pay.PayChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.vip_card.VipCardOrderActionEnum;
import com.niu.core.enums.vip_card.VipCardOrderLogEnum;
import com.niu.core.enums.vip_card.VipCardOrderStatusEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.vip_card.VipCardBuyMapper;
import com.niu.core.mapper.vip_card.VipCardOrderItemMapper;
import com.niu.core.mapper.vip_card.VipCardOrderLogMapper;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.admin.vip_card.card.IAdminVipCardBenefitsService;
import com.niu.core.service.admin.vip_card.order.IAdminVipCardOrderService;
import com.niu.core.service.admin.vip_card.order.param.AdminVipCardOrderPageParam;
import com.niu.core.service.admin.vip_card.order.vo.AdminVipCardOrderDetailVo;
import com.niu.core.service.admin.vip_card.order.vo.AdminVipCardOrderPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminVipCardOrderServiceImpl implements IAdminVipCardOrderService {

    @Autowired
    private VipCardOrderMapper orderMapper;

    @Autowired
    private VipCardOrderItemMapper orderItemMapper;

    @Autowired
    private VipCardOrderLogMapper orderLogMapper;

    @Autowired
    private VipCardBuyMapper vipCardBuyMapper;

    @Autowired
    private PayMapper payMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private IAdminVipCardBenefitsService vipCardBenefitsService;

    // ========== 分页列表 ==========
    @Override
    public PageResult<AdminVipCardOrderPageVo> getPage(PageParam pageParam, AdminVipCardOrderPageParam param) {
        Integer siteId = RequestUtils.siteId(); // 当前站点ID

        // 使用 MPJQueryWrapper 多表关联
        MPJQueryWrapper<MemberVipCardOrder> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("o")
                .leftJoin("ns_member m ON o.member_id = m.member_id")
                .leftJoin("ns_pay p ON o.out_trade_no = p.out_trade_no")
                .select("o.order_id, o.member_id, o.order_no, o.body, o.order_from, o.out_trade_no, " +
                        "o.status, o.order_money, o.create_time, o.pay_time, o.remark, " +
                        "m.member_id as member_member_id, m.headimg, m.nickname, m.mobile, m.member_no, " +
                        "p.type as pay_type, p.status as pay_status, p.out_trade_no as pay_out_trade_no")
                .eq("o.site_id", siteId);

        // 条件搜索
        if (StrUtil.isNotBlank(param.getKeyword())) {
            wrapper.and(w -> w.like("m.nickname", param.getKeyword())
                    .or().like("m.mobile", param.getKeyword())
                    .or().like("m.member_no", param.getKeyword()));
        }
        if (StrUtil.isNotBlank(param.getPayType()) && !"all".equals(param.getPayType())) {
            wrapper.eq("p.type", param.getPayType());
        }
        if (StrUtil.isNotBlank(param.getSearchType()) && StrUtil.isNotBlank(param.getSearchName())) {
            switch (param.getSearchType()) {
                case "card_name" -> wrapper.like("o.body", param.getSearchName());
                case "order_no" -> wrapper.like("o.order_no", param.getSearchName());
                case "out_trade_no" -> wrapper.like("o.out_trade_no", param.getSearchName());
                default -> {
                }
            }
        }
        if (StrUtil.isNotBlank(param.getOrderFrom()) && !"all".equals(param.getOrderFrom())) {
            wrapper.eq("o.order_from", param.getOrderFrom());
        }
        if (StrUtil.isNotBlank(param.getStatus()) && !"all".equals(param.getStatus())) {
            wrapper.eq("o.status", param.getStatus());
        }
        // 时间区间
        if (CollUtil.isNotEmpty(param.getCreateTime()) && param.getCreateTime().size() == 2) {
            wrapper.between("o.create_time", param.getCreateTime().get(0), param.getCreateTime().get(1));
        }
        if (CollUtil.isNotEmpty(param.getPayTime()) && param.getPayTime().size() == 2) {
            wrapper.between("o.pay_time", param.getPayTime().get(0), param.getPayTime().get(1));
        }

        wrapper.orderByDesc("o.create_time");

        IPage<AdminVipCardOrderPageVo> page = orderMapper.selectJoinPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()),
                AdminVipCardOrderPageVo.class, wrapper
        );

        List<AdminVipCardOrderPageVo> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal()).setData(records);
        }

        Map<Integer, Member> memberMap = loadMemberMap(records);

        // 查询订单项（批量）
        List<Integer> orderIds = records.stream().map(AdminVipCardOrderPageVo::getOrderId).collect(Collectors.toList());
        Map<Integer, List<MemberVipCardOrderItem>> itemMap = Collections.emptyMap();
        if (CollUtil.isNotEmpty(orderIds)) {
            List<MemberVipCardOrderItem> allItems = orderItemMapper.selectList(
                    new LambdaQueryWrapper<MemberVipCardOrderItem>()
                            .in(MemberVipCardOrderItem::getOrderId, orderIds)
                            .select(MemberVipCardOrderItem::getOrderItemId, MemberVipCardOrderItem::getOrderId,
                                    MemberVipCardOrderItem::getItemId, MemberVipCardOrderItem::getItemName,
                                    MemberVipCardOrderItem::getPrice, MemberVipCardOrderItem::getNum,
                                    MemberVipCardOrderItem::getItemMoney)
            );
            itemMap = allItems.stream()
                    .collect(Collectors.groupingBy(MemberVipCardOrderItem::getOrderId));
        }

        for (AdminVipCardOrderPageVo vo : records) {
            fillMember(vo, memberMap);
            vo.setPayTypeName(getPayTypeName(vo.getPayType()));
            vo.setOrderFromName(PayChannelEnum.getNameByStatus(vo.getOrderFrom()));
            vo.setStatusName(VipCardOrderStatusEnum.getNameByCode(vo.getStatus()));

            List<MemberVipCardOrderItem> items = itemMap.get(vo.getOrderId());
            if (CollUtil.isNotEmpty(items)) {
                List<AdminVipCardOrderPageVo.OrderItemInfo> itemVos = items.stream().map(item -> {
                    AdminVipCardOrderPageVo.OrderItemInfo info = new AdminVipCardOrderPageVo.OrderItemInfo();
                    BeanUtil.copyProperties(item, info);
                    return info;
                }).collect(Collectors.toList());
                vo.setOrderItem(itemVos);
            }
        }

        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal()).setData(records);
    }

    private Map<Integer, Member> loadMemberMap(List<AdminVipCardOrderPageVo> records) {
        List<Integer> memberIds = records.stream()
                .map(AdminVipCardOrderPageVo::getMemberId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(memberIds)) {
            return Collections.emptyMap();
        }
        return memberMapper.selectList(
                        new LambdaQueryWrapper<Member>()
                                .in(Member::getMemberId, memberIds)
                                .select(Member::getMemberId, Member::getHeadimg, Member::getNickname,
                                        Member::getMobile, Member::getMemberNo))
                .stream()
                .collect(Collectors.toMap(Member::getMemberId, member -> member, (a, b) -> a));
    }

    private void fillMember(AdminVipCardOrderPageVo vo, Map<Integer, Member> memberMap) {
        if (vo.getMemberId() == null) {
            return;
        }
        Member member = memberMap.get(vo.getMemberId());
        if (member == null) {
            return;
        }
        AdminVipCardOrderPageVo.MemberInfo memberInfo = new AdminVipCardOrderPageVo.MemberInfo();
        BeanUtil.copyProperties(member, memberInfo);
        vo.setMember(memberInfo);
    }

    // ========== 详情 ==========
    @Override
    public AdminVipCardOrderDetailVo getInfo(Integer orderId) {
        MemberVipCardOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardOrder>()
                        .eq(MemberVipCardOrder::getOrderId, orderId)
        );
        if (order == null) return null;

        AdminVipCardOrderDetailVo vo = new AdminVipCardOrderDetailVo();
        BeanUtil.copyProperties(order, vo);
        vo.setOrderFromName(PayChannelEnum.getNameByStatus(order.getOrderFrom()));
        vo.setStatusName(VipCardOrderStatusEnum.getNameByCode(order.getStatus()));

        // 会员信息
        if (order.getMemberId() != null) {
            Member member = memberMapper.selectOne(
                    new LambdaQueryWrapper<Member>()
                            .eq(Member::getMemberId, order.getMemberId())
                            .select(Member::getMemberId, Member::getHeadimg, Member::getNickname,
                                    Member::getMobile, Member::getMemberNo)
            );
            vo.setMember(member);
        }

        // 订单项
        List<MemberVipCardOrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<MemberVipCardOrderItem>()
                        .eq(MemberVipCardOrderItem::getOrderId, orderId)
        );
        if (CollUtil.isNotEmpty(items)) {
            List<AdminVipCardOrderDetailVo.OrderItemInfo> itemVos = items.stream().map(item -> {
                AdminVipCardOrderDetailVo.OrderItemInfo info = new AdminVipCardOrderDetailVo.OrderItemInfo();
                BeanUtil.copyProperties(item, info);
                return info;
            }).collect(Collectors.toList());
            vo.setOrderItem(itemVos);
        }

        // 订单日志
        List<MemberVipCardOrderLog> logs = orderLogMapper.selectList(
                new LambdaQueryWrapper<MemberVipCardOrderLog>()
                        .eq(MemberVipCardOrderLog::getOrderId, orderId)
                        .orderByDesc(MemberVipCardOrderLog::getId)
        );
        if (CollUtil.isNotEmpty(logs)) {
            List<AdminVipCardOrderDetailVo.OrderLogInfo> logVos = logs.stream().map(log -> {
                AdminVipCardOrderDetailVo.OrderLogInfo info = new AdminVipCardOrderDetailVo.OrderLogInfo();
                BeanUtil.copyProperties(log, info);
                info.setMainTypeName(VipCardOrderLogEnum.getNameByCode(log.getMainType()));
                info.setTypeName(VipCardOrderActionEnum.getNameByCode(log.getType()));
                info.setMainName(getMainName(log.getMainType(), log.getMainId()));
                return info;
            }).collect(Collectors.toList());
            vo.setOrderLog(logVos);
        } else {
            vo.setOrderLog(Collections.emptyList());
        }

        // 购卡记录（card_buy）
        MemberVipCardBuy cardBuy = vipCardBuyMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardBuy>()
                        .eq(MemberVipCardBuy::getOrderId, orderId)
        );
        if (cardBuy != null) {
            AdminVipCardOrderDetailVo.CardBuyInfo buyInfo = buildCardBuyInfo(cardBuy, order.getSiteId());
            vo.setCardBuy(buyInfo);
        }

        // 支付信息
        if (StrUtil.isNotBlank(order.getOutTradeNo())) {
            Pay pay = payMapper.selectOne(
                    new LambdaQueryWrapper<Pay>()
                            .eq(Pay::getOutTradeNo, order.getOutTradeNo())
            );
            if (pay != null) {
                AdminVipCardOrderDetailVo.PayInfo payInfo = new AdminVipCardOrderDetailVo.PayInfo();
                BeanUtil.copyProperties(pay, payInfo);
                String payTypeName = getPayTypeName(pay.getType());
                payInfo.setTypeName(payTypeName);
                vo.setPayTypeName(payTypeName);
                vo.setPay(payInfo);
            }
        }

        return vo;
    }

    private AdminVipCardOrderDetailVo.CardBuyInfo buildCardBuyInfo(MemberVipCardBuy cardBuy, Integer siteId) {
        AdminVipCardOrderDetailVo.CardBuyInfo buyInfo = new AdminVipCardOrderDetailVo.CardBuyInfo();
        buyInfo.setId(cardBuy.getId());
        buyInfo.setSiteId(cardBuy.getSiteId());
        buyInfo.setOrderId(cardBuy.getOrderId());
        buyInfo.setMemberId(cardBuy.getMemberId());
        buyInfo.setCardId(cardBuy.getCardId());
        buyInfo.setItemId(cardBuy.getItemId());
        buyInfo.setIsExpire(cardBuy.getIsExpire());
        buyInfo.setIsTrial(cardBuy.getIsTrial());
        buyInfo.setTrialDay(cardBuy.getTrialDay());
        buyInfo.setExpireTime(cardBuy.getExpireTime());
        buyInfo.setCreateTime(cardBuy.getCreateTime());

        Map<String, Object> benefitsConfig = parseJsonMap(cardBuy.getBenefitsConfig());
        buyInfo.setBenefitsConfig(benefitsConfig);
        buyInfo.setItemConfig(parseJsonMap(cardBuy.getItemConfig()));
        buyInfo.setBenefitsConfigContent(buildBenefitsConfigContentList(siteId, benefitsConfig));
        return buyInfo;
    }

    private List<AdminVipCardOrderDetailVo.BenefitsConfigContentItem> buildBenefitsConfigContentList(
            Integer siteId, Map<String, Object> benefitsConfig) {
        if (CollUtil.isEmpty(benefitsConfig)) {
            return new ArrayList<>();
        }
        JSONObject benefitsJson = new JSONObject(benefitsConfig);
        Map<String, Object> contentMap = vipCardBenefitsService.getBenefitsContent(siteId, benefitsJson, "admin");
        if (CollUtil.isEmpty(contentMap)) {
            return new ArrayList<>();
        }
        List<AdminVipCardOrderDetailVo.BenefitsConfigContentItem> list = new ArrayList<>();
        for (Object value : contentMap.values()) {
            if (!(value instanceof Map<?, ?> map)) {
                continue;
            }
            Object content = map.get("content");
            if (content == null || StrUtil.isBlank(content.toString())) {
                continue;
            }
            AdminVipCardOrderDetailVo.BenefitsConfigContentItem item =
                    new AdminVipCardOrderDetailVo.BenefitsConfigContentItem();
            item.setContent(content.toString());
            list.add(item);
        }
        return list;
    }

    private Map<String, Object> parseJsonMap(String json) {
        if (StrUtil.isBlank(json)) {
            return new LinkedHashMap<>();
        }
        try {
            Object parsed = JSONUtil.parse(json);
            if (parsed instanceof JSONObject jo) {
                return new LinkedHashMap<>(jo);
            }
            if (parsed instanceof Map<?, ?> map) {
                Map<String, Object> result = new LinkedHashMap<>();
                map.forEach((k, v) -> result.put(String.valueOf(k), v));
                return result;
            }
        } catch (Exception ignored) {
        }
        return new LinkedHashMap<>();
    }

    private String getPayTypeName(String type) {
        if (StrUtil.isBlank(type)) {
            return "";
        }
        JSONObject payTypes = PayTypeEnum.getType();
        JSONObject item = payTypes.getJSONObject(type);
        return item == null ? "" : ObjectUtil.defaultIfNull(item.getStr("name"), "");
    }

    private String getMainName(String mainType, Integer mainId) {
        if (mainId == null || StrUtil.isBlank(mainType)) {
            return "";
        }
        if (VipCardOrderLogEnum.MEMBER.getCode().equals(mainType)) {
            Member m = memberMapper.selectById(mainId);
            return m != null ? ObjectUtil.defaultIfNull(m.getNickname(), "") : "";
        }
        if (VipCardOrderLogEnum.ADMIN.getCode().equals(mainType)) {
            SysUser user = sysUserMapper.selectById(mainId);
            return user != null ? ObjectUtil.defaultIfNull(user.getUsername(), "") : "";
        }
        return "";
    }

}