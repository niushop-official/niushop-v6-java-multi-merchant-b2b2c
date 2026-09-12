package com.niu.core.service.admin.cash.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.cash.CashOrder;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.cash.CashOrderStatusEnum;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.mapper.cash.CashOrderMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.admin.cash.ICashOrderService;
import com.niu.core.service.admin.cash.param.CashOrderSearchParam;
import com.niu.core.service.admin.cash.vo.CashOrderDetailVo;
import com.niu.core.service.admin.cash.vo.CashOrderListVo;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CashOrderServiceImpl implements ICashOrderService {

    @Resource
    CashOrderMapper cashOrderMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    PayMapper payMapper;

    @Override
    public PageResult<CashOrderListVo> getPage(PageParam pageParam, CashOrderSearchParam param, Integer siteId) {
        MPJQueryWrapper<CashOrder> wrapper = buildQueryWrapper(param, siteId);
        wrapper.orderByDesc("o.create_time");

        IPage<CashOrder> page = cashOrderMapper.selectJoinPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()),
                CashOrder.class,
                wrapper
        );

        List<CashOrderListVo> list = buildListVo(page.getRecords());
        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal()).setData(list);
    }

    @Override
    public CashOrderDetailVo getDetail(Integer orderId, Integer siteId) {
        QueryWrapper<CashOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        if (siteId != null && siteId > 0) {
            queryWrapper.eq("site_id", siteId);
        }
        CashOrder order = cashOrderMapper.selectOne(queryWrapper);
        if (order == null) {
            return null;
        }
        return buildDetailVo(order);
    }

    @Override
    public Map<Integer, Map<String, Object>> getStatus() {
        return CashOrderStatusEnum.getMap();
    }

    private MPJQueryWrapper<CashOrder> buildQueryWrapper(CashOrderSearchParam param, Integer siteId) {
        MPJQueryWrapper<CashOrder> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("o")
                .leftJoin("ns_member m ON o.member_id = m.member_id")
                .leftJoin("ns_pay p ON o.out_trade_no = p.out_trade_no")
                .select("o.order_id, o.site_id, o.site_name, o.order_no, o.order_from, o.out_trade_no, o.status, " +
                        "o.refund_status, o.member_id, o.total_money, o.no_discount_money, o.discountable_money, " +
                        "o.order_money, o.discount_money, o.plat_discount_money, o.create_time, o.pay_time, " +
                        "o.close_time, o.shop_remark, o.member_remark");

        if (siteId != null && siteId > 0) {
            wrapper.eq("o.site_id", siteId);
        }
        if (param == null) {
            return wrapper;
        }

        if (StrUtil.isNotBlank(param.getKeyword())) {
            wrapper.and(w -> w.like("m.nickname", param.getKeyword())
                    .or().like("m.mobile", param.getKeyword())
                    .or().like("m.member_no", param.getKeyword()));
        }
        if (StrUtil.isNotBlank(param.getPayType()) && !"all".equals(param.getPayType())) {
            wrapper.eq("p.type", param.getPayType());
        }
        if (StrUtil.isNotBlank(param.getSearchType()) && StrUtil.isNotBlank(param.getSearchName())) {
            if ("order_no".equals(param.getSearchType())) {
                wrapper.like("o.order_no", param.getSearchName());
            } else if ("out_trade_no".equals(param.getSearchType())) {
                wrapper.like("o.out_trade_no", param.getSearchName());
            }
        }
        if (StrUtil.isNotBlank(param.getOrderFrom()) && !"all".equals(param.getOrderFrom())) {
            wrapper.eq("o.order_from", param.getOrderFrom());
        }
        if (StrUtil.isNotBlank(param.getStatus()) && !"all".equals(param.getStatus())) {
            wrapper.eq("o.status", param.getStatus());
        }
        if (param.getCreateTime() != null) {
            QueryMapperUtils.buildByTime(wrapper, "o.create_time", param.getCreateTime());
        }
        if (param.getPayTime() != null) {
            QueryMapperUtils.buildByTime(wrapper, "o.pay_time", param.getPayTime());
        }
        buildOrderMoneyRange(wrapper, param.getStartMoney(), param.getEndMoney());
        return wrapper;
    }

    private void buildOrderMoneyRange(MPJQueryWrapper<CashOrder> wrapper, BigDecimal startMoney, BigDecimal endMoney) {
        BigDecimal start = startMoney == null ? BigDecimal.ZERO : startMoney;
        BigDecimal end = endMoney == null ? BigDecimal.ZERO : endMoney;
        if (start.compareTo(BigDecimal.ZERO) > 0 && end.compareTo(BigDecimal.ZERO) > 0) {
            if (start.compareTo(end) > 0) {
                BigDecimal temp = start;
                start = end;
                end = temp;
            }
            wrapper.between("o.order_money", start, end);
        } else if (start.compareTo(BigDecimal.ZERO) > 0) {
            wrapper.ge("o.order_money", start);
        } else if (end.compareTo(BigDecimal.ZERO) > 0) {
            wrapper.le("o.order_money", end);
        }
    }

    private List<CashOrderListVo> buildListVo(List<CashOrder> records) {
        if (records == null || records.isEmpty()) {
            return List.of();
        }
        List<Integer> memberIds = records.stream().map(CashOrder::getMemberId).filter(id -> id != null && id > 0).distinct().toList();
        Map<Integer, Member> memberMap = memberIds.isEmpty() ? Map.of() :
                memberMapper.selectList(new QueryWrapper<Member>().in("member_id", memberIds)).stream()
                        .collect(Collectors.toMap(Member::getMemberId, Function.identity(), (a, b) -> a));

        List<String> outTradeNos = records.stream().map(CashOrder::getOutTradeNo).filter(StrUtil::isNotBlank).distinct().toList();
        Map<String, Pay> payMap = outTradeNos.isEmpty() ? Map.of() :
                payMapper.selectList(new QueryWrapper<Pay>().in("out_trade_no", outTradeNos)).stream()
                        .collect(Collectors.toMap(Pay::getOutTradeNo, Function.identity(), (a, b) -> a));

        List<CashOrderListVo> list = new ArrayList<>();
        for (CashOrder order : records) {
            CashOrderListVo vo = new CashOrderListVo();
            BeanUtils.copyProperties(order, vo);
            fillCommonFields(vo, order, memberMap.get(order.getMemberId()), payMap.get(order.getOutTradeNo()));
            list.add(vo);
        }
        return list;
    }

    private CashOrderDetailVo buildDetailVo(CashOrder order) {
        CashOrderDetailVo vo = new CashOrderDetailVo();
        BeanUtils.copyProperties(order, vo);
        Member member = order.getMemberId() == null ? null :
                memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", order.getMemberId()));
        Pay pay = StrUtil.isBlank(order.getOutTradeNo()) ? null :
                payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", order.getOutTradeNo()));
        fillCommonFields(vo, order, member, pay);
        return vo;
    }

    private void fillCommonFields(CashOrderListVo vo, CashOrder order, Member member, Pay pay) {
        vo.setOrderFromName(ChannelEnum.getNameByCode(order.getOrderFrom()));
        vo.setStatusInfo(new LinkedHashMap<>(CashOrderStatusEnum.getStatusInfo(order.getStatus())));
        if (member != null) {
            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            memberVo.setMemberId(member.getMemberId());
            memberVo.setNickname(member.getNickname());
            memberVo.setMobile(member.getMobile());
            memberVo.setHeadimg(member.getHeadimg());
            vo.setMember(memberVo);
        }
        vo.setPayTypeName(getPayTypeName(pay == null ? null : pay.getType()));
    }

    private void fillCommonFields(CashOrderDetailVo vo, CashOrder order, Member member, Pay pay) {
        vo.setOrderFromName(ChannelEnum.getNameByCode(order.getOrderFrom()));
        vo.setStatusInfo(new LinkedHashMap<>(CashOrderStatusEnum.getStatusInfo(order.getStatus())));
        if (member != null) {
            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            memberVo.setMemberId(member.getMemberId());
            memberVo.setNickname(member.getNickname());
            memberVo.setMobile(member.getMobile());
            memberVo.setHeadimg(member.getHeadimg());
            vo.setMember(memberVo);
        }
        vo.setPayTypeName(getPayTypeName(pay == null ? null : pay.getType()));
    }

    private String getPayTypeName(String type) {
        if (StrUtil.isBlank(type)) {
            return "";
        }
        JSONObject payTypes = PayTypeEnum.getType();
        JSONObject item = payTypes.getJSONObject(type);
        return item == null ? "" : ObjectUtil.defaultIfNull(item.getStr("name"), "");
    }
}
