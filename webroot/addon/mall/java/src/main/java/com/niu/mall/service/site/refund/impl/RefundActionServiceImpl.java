package com.niu.mall.service.site.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.NoticeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayRefundMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.entity.order.MallOrderRefundLog;
import com.niu.core.entity.site.SiteAddress;
import com.niu.mall.enums.order.*;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundLogMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.mall.service.admin.order.vo.MallOrderInfoVo;
import com.niu.mall.service.site.refund.IRefundActionService;
import com.niu.mall.service.core.refund.ICoreRefundActionService;
import com.niu.mall.service.core.refund.param.ActiveRefundParam;
import com.niu.mall.service.site.refund.param.AuditApplyParam;
import com.niu.mall.service.site.refund.param.AuditRefundGoodsParam;
import com.niu.mall.service.site.refund.param.RefundSearchParam;
import com.niu.mall.service.site.refund.vo.RefundDetailVo;
import com.niu.mall.service.site.refund.vo.RefundListVo;
import com.niu.mall.service.core.refund.ICoreRefundService;
import com.niu.mall.service.core.refund.param.AddRefundLogParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class RefundActionServiceImpl implements IRefundActionService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallOrderRefundMapper mallOrderRefundMapper;

    @Resource
    ICoreRefundService coreRefundService;

    @Resource
    MallOrderRefundLogMapper mallOrderRefundLogMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    PayRefundMapper payRefundMapper;

    @Resource
    SiteAddressMapper shopAddressMapper;

    @Resource
    com.niu.core.service.core.pay.ICoreRefundService corePayRefundService;

    @Resource
    ICoreRefundActionService coreRefundActionService;
    @Resource
    ISiteService siteService;


    /**
     * 订单退款列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return
     */
    @Override
    public PageResult<RefundListVo> page(PageParam pageParam, RefundSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MallOrderRefund> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderRefundNo())) queryWrapper.like("order_refund_no", searchParam.getOrderRefundNo());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        queryWrapper.orderByDesc("create_time");

        IPage<MallOrderRefund> iPage = mallOrderRefundMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<RefundListVo> list = new LinkedList<>();
        for (MallOrderRefund item : iPage.getRecords()) {
            RefundListVo vo = new RefundListVo();
            BeanUtils.copyProperties(item, vo);

            RefundListVo.OrderGoods orderGoodsVo = new RefundListVo.OrderGoods();
            MallOrderGoods orderGoods = mallOrderGoodsMapper.selectById(vo.getOrderGoodsId());
            BeanUtil.copyProperties(orderGoods, orderGoodsVo);
            vo.setOrderGoods(orderGoodsVo);

            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
            BeanUtil.copyProperties(member, memberVo);
            vo.setMember(memberVo);

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 详情
     *
     * @param refundId
     * @return
     */
    @Override
    public RefundDetailVo detail(Integer refundId) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("refund_id", refundId)
                .eq("site_id", RequestUtils.siteId())
        );
        if (refund == null) throw new CommonException("退款已失效");

        RefundDetailVo vo = new RefundDetailVo();
        BeanUtil.copyProperties(refund, vo);

        RefundDetailVo.OrderGoods orderGoodsVo = new RefundDetailVo.OrderGoods();
        MallOrderGoods orderGoods = mallOrderGoodsMapper.selectById(vo.getOrderGoodsId());
        BeanUtil.copyProperties(orderGoods, orderGoodsVo);
        vo.setOrderGoods(orderGoodsVo);

        List<RefundDetailVo.RefundLog> refundLog = CollectUtils.convert(mallOrderRefundLogMapper.selectList(new QueryWrapper<MallOrderRefundLog>()
                .eq("order_refund_no", vo.getOrderRefundNo())
                .orderByDesc("create_time")
        ), RefundDetailVo.RefundLog.class);
        vo.setRefundLog(refundLog);

        MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
        BeanUtil.copyProperties(member, memberVo);
        vo.setMember(memberVo);

        RefundDetailVo.OrderMain orderVo = new RefundDetailVo.OrderMain();
        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("order_id", vo.getOrderId()));
        BeanUtil.copyProperties(order, orderVo);
        orderVo.setSupplyId(order.getSupplyId());
        vo.setOrderMain(orderVo);

        if (ObjectUtil.isNotEmpty(vo.getRefundNo())) {
            RefundDetailVo.PayRefund payRefundVo = new RefundDetailVo.PayRefund();
            PayRefund payRefund = payRefundMapper.selectOne(new QueryWrapper<PayRefund>().eq("refund_no", vo.getRefundNo()));
            payRefundVo.setRefundNo(payRefund.getRefundNo());
            payRefundVo.setType(payRefund.getType());
            payRefundVo.setTypeName(payRefundVo.getTypeName());
            payRefundVo.setMoney(payRefund.getMoney());
            payRefundVo.setCreateTime(payRefund.getCreateTime());
            payRefundVo.setRefundTime(payRefund.getRefundTime());
            vo.setPayRefund(payRefundVo);
        }

        if (!refund.getDelivery().isEmpty()) {
            RefundDetailVo.Delivery delivery = JSONUtil.toBean(JSONUtil.parseObj(refund.getDelivery()), RefundDetailVo.Delivery.class);
            vo.setDelivery(delivery);
        }

        //获取店铺信息
        if(ObjectUtil.isNotEmpty(order.getSupplyId()) && order.getSupplyId() >0){
            SiteInfoVo supplyInfo = siteService.info(order.getSupplyId());
            RefundDetailVo.Supply supply = new RefundDetailVo.Supply();
            supply.setSiteId(supplyInfo.getSiteId());
            supply.setSiteName(supplyInfo.getSiteName());
            vo.setSupply(supply);
        }
        return vo;
    }

    /**
     * 订单维权审核
     *
     * @param param
     */
    @Override
    public void auditApply(AuditApplyParam param) {
        param.setMainId(RequestUtils.uid());
        param.setMainType(OrderLogMainTypeEnum.STORE.getType());
        param.setSiteId(RequestUtils.siteId());
        coreRefundActionService.auditApply( param);
    }

    /**
     * 订单退货审核
     *
     * @param param
     */
    @Override
    public void auditRefundGoods(AuditRefundGoodsParam param) {
        param.setMainId(RequestUtils.uid());
        param.setMainType(OrderLogMainTypeEnum.STORE.getType());
        param.setSiteId(RequestUtils.siteId());
        coreRefundActionService.auditRefundGoods( param);
    }

    /**
     * 商家主动退款
     * @param param
     */
    @Override
    public void shopActiveRefund(ActiveRefundParam param) {
        param.setMainType(OrderLogMainTypeEnum.STORE.getType());
        param.setMainId(RequestUtils.uid());
        param.setSiteId(RequestUtils.siteId());
        param.setSource(OrderRefundSourceEnum.ACTIVE_REFUND.getType().toString());
        coreRefundActionService.shopActiveRefund(param);
    }
}
