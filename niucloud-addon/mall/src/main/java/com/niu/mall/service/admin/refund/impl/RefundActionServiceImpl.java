package com.niu.mall.service.admin.refund.impl;

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
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayRefundMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.entity.order.MallOrderRefundLog;
import com.niu.mall.enums.order.*;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundLogMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.admin.refund.IRefundActionService;
import com.niu.mall.service.admin.refund.param.AuditApplyParam;
import com.niu.mall.service.admin.refund.param.AuditRefundGoodsParam;
import com.niu.mall.service.admin.refund.param.RefundSearchParam;
import com.niu.mall.service.admin.refund.vo.RefundDetailVo;
import com.niu.mall.service.admin.refund.vo.RefundListVo;
import com.niu.mall.service.core.refund.ICoreRefundActionService;
import com.niu.mall.service.core.refund.ICoreRefundService;
import com.niu.mall.service.core.refund.param.ActiveRefundParam;
import com.niu.mall.service.core.refund.param.AddRefundLogParam;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
    @Lazy
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
        if(RequestUtils.siteId() != 0){
            queryWrapper.eq("site_id", RequestUtils.siteId());
        }
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

            /**
             * 获取商家信息
             *
             */
            SiteInfoVo siteInfoVo = siteService.info(vo.getSiteId());
            RefundListVo.Site site = new RefundListVo.Site();
            site.setSiteId(siteInfoVo.getSiteId());
            site.setSiteName(siteInfoVo.getSiteName());
            site.setLogo(siteInfoVo.getLogo());
            vo.setSite(site);

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
        vo.setOrderMain(orderVo);

        if (ObjectUtil.isNotEmpty(vo.getRefundNo())) {
            RefundDetailVo.PayRefund payRefundVo = new RefundDetailVo.PayRefund();
            PayRefund payRefund = payRefundMapper.selectOne(new QueryWrapper<PayRefund>().eq("refund_no", vo.getRefundNo()));
            BeanUtil.copyProperties(payRefund, payRefundVo);
            vo.setPayRefund(payRefundVo);
        }

        if (!refund.getDelivery().isEmpty()) {
            RefundDetailVo.Delivery delivery = JSONUtil.toBean(JSONUtil.parseObj(refund.getDelivery()), RefundDetailVo.Delivery.class);
            vo.setDelivery(delivery);
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
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus().toString())) {
            throw new CommonException("退款已失效(只有申请中的退款才可以审核)");
        }

        MallOrderRefund model = new MallOrderRefund();
        model.setRefundId(refund.getRefundId());

        // 操作日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        logParam.setMainId(RequestUtils.uid());

        String messageKey = "";

        // 同意退款
        MallOrderGoods orderGoods = mallOrderGoodsMapper.selectOne(new QueryWrapper<MallOrderGoods>()
                .select("goods_money,discount_money")
                .eq("order_goods_id", refund.getOrderGoodsId()));
        if (orderGoods == null) throw new CommonException("订单已失效");

        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>()
                .select("order_no,delivery_money")
                .eq("order_id", refund.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");

        if (param.getIsAgree().equals(1)) {
            BigDecimal maxRefundMoney = coreRefundService.getItemRefundableMoney(refund.getOrderGoodsId());
            if (refund.getIsRefundDelivery().equals(1)) maxRefundMoney = maxRefundMoney.add(order.getDeliveryMoney());

            if (param.getMoney().compareTo(maxRefundMoney) == 1) throw new CommonException("退款金额不能大于可退款总额");

            model.setMoney(param.getMoney());
            // 如果是退款退货
            if (refund.getRefundType().equals(OrderRefundTypeEnum.RETURN_AND_REFUND_GOODS.getType().toString())) {
                model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString());
                SiteAddress address = shopAddressMapper.selectOne(new QueryWrapper<SiteAddress>()
                        .eq("site_id", RequestUtils.siteId())
                        .eq("id", param.getRefundAddressId())
                );
                if (address == null) throw new CommonException("请选择退货地址");
                model.setRefundAddress(JSONUtil.parseObj(address).toString());
            } else {
                model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString());
            }

            logParam.setType(OrderRefundActionTypeEnum.AGREE_AUDIT_APPLY_ACTION.getType());

            messageKey = "mall_refund_agree";
        } else {
            // 拒绝退款
            model.setShopReason(param.getShopReason());
            model.setStatus(OrderRefundStatusEnum.STORE_REFUSE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString());
            logParam.setType(OrderRefundActionTypeEnum.REFUSE_AUDIT_APPLY_ACTION.getType());

            messageKey = "mall_refund_refuse";
        }

        mallOrderRefundMapper.updateById(model);

        logParam.setStatus(Integer.valueOf(model.getStatus()));
        coreRefundService.addLog(logParam);

        if (model.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString())) {
            refund = mallOrderRefundMapper.selectById(refund.getRefundId());
            coreRefundService.toTransfer(refund);
        }

        // 发送消息
        Map<String, Object> noticeParam = new HashMap<String, Object>();
        noticeParam.put("mallRefundId", refund.getRefundId());
        NoticeUtils.send(refund.getSiteId(), messageKey, noticeParam);
    }

    /**
     * 订单退货审核
     *
     * @param param
     */
    @Override
    public void auditRefundGoods(AuditRefundGoodsParam param) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus().toString())) {
            throw new CommonException("退款已失效(只有待确认收货的的退款才可以审核)");
        }

        MallOrderRefund model = new MallOrderRefund();
        model.setRefundId(refund.getRefundId());

        // 操作日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        logParam.setMainId(RequestUtils.uid());

        if (param.getIsAgree().equals(1)) {
            model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString());
            logParam.setType(OrderRefundActionTypeEnum.AGREE_AUDIT_REFUND_GOODS_ACTION.getType());
        } else {
            // 拒绝退货
            model.setShopReason(param.getShopReason());
            model.setStatus(OrderRefundStatusEnum.STORE_REFUSE_TAKE_REFUND_GOODS_WAIT_BUYER.getStatus().toString());
            logParam.setType(OrderRefundActionTypeEnum.REFUSE_AUDIT_REFUND_GOODS_ACTION.getType());
        }

        mallOrderRefundMapper.updateById(model);

        logParam.setStatus(Integer.valueOf(model.getStatus()));
        coreRefundService.addLog(logParam);

        if (model.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString())) {
            refund = mallOrderRefundMapper.selectById(refund.getRefundId());
            coreRefundService.toTransfer(refund);
        }
    }

    /**
     * 商家主动退款
     * @param param
     */
    @Override
    public void shopActiveRefund(ActiveRefundParam param) {
        param.setMainType(OrderLogMainTypeEnum.STORE.getType());
        param.setMainId(RequestUtils.uid());
        param.setSource(OrderRefundSourceEnum.ACTIVE_REFUND.getType().toString());
        coreRefundActionService.shopActiveRefund(param);
    }
}
