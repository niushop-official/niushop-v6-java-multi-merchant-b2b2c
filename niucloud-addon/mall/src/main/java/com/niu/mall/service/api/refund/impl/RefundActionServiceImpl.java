package com.niu.mall.service.api.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.event.common.CommonEvent;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.entity.order.MallOrderRefundLog;
import com.niu.mall.enums.order.*;
import com.niu.mall.event.refund.SyncSupplyRefundDeliveryEvent;
import com.niu.mall.event.refund.SyncSupplyRefundEditDeliveryEvent;
import com.niu.mall.event.refund.SyncSupplyRefundEditEvent;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundLogMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.api.refund.IRefundActionService;
import com.niu.mall.service.api.refund.param.*;
import com.niu.mall.service.api.refund.vo.RefundDataVo;
import com.niu.mall.service.api.refund.vo.RefundDetailVo;
import com.niu.mall.service.api.refund.vo.RefundListVo;
import com.niu.mall.service.core.refund.ICoreRefundActionService;
import com.niu.mall.service.core.refund.ICoreRefundService;
import com.niu.mall.service.core.refund.param.AddRefundLogParam;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
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
    ICoreRefundService coreRefundService;

    @Resource
    MallOrderRefundLogMapper mallOrderRefundLogMapper;

    @Resource
    ISiteService siteService;

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    ICoreRefundActionService coreRefundActionService;

    /**
     * 订单退款列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return
     */
    @Override
    public PageResult<RefundListVo> page(PageParam pageParam, RefundSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallOrderRefund> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
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
            RefundListVo.Site site = new RefundListVo.Site();
            SiteInfoVo infoVo = siteService.info(item.getSiteId());
            site.setLogo(infoVo.getLogo());
            site.setSiteName(infoVo.getSiteName());
            site.setSiteId(infoVo.getSiteId());
            vo.setSite(site);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 详情
     *
     * @param orderRefundNo
     * @return
     */
    @Override
    public RefundDetailVo detail(String orderRefundNo) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", orderRefundNo)
                .eq("member_id", RequestUtils.memberId())
        );
        if (refund == null) throw new CommonException("退款已失效");

        RefundDetailVo vo = new RefundDetailVo();
        BeanUtil.copyProperties(refund, vo);

        RefundDetailVo.OrderGoods orderGoodsVo = new RefundDetailVo.OrderGoods();
        MallOrderGoods orderGoods = mallOrderGoodsMapper.selectById(vo.getOrderGoodsId());
        BeanUtil.copyProperties(orderGoods, orderGoodsVo);
        vo.setOrderGoods(orderGoodsVo);

        if (!refund.getRefundAddress().isEmpty()) {
            SiteAddress refundAddress = JSONUtil.toBean(JSONUtil.parseObj(refund.getRefundAddress()), SiteAddress.class);
            vo.setRefundAddress(refundAddress);
        }

        List<RefundDetailVo.RefundLog> refundLog = CollectUtils.convert(mallOrderRefundLogMapper.selectList(new QueryWrapper<MallOrderRefundLog>()
                .eq("order_refund_no", vo.getOrderRefundNo())
                .orderByDesc("create_time")
        ), RefundDetailVo.RefundLog.class);
        vo.setRefundLog(refundLog);
        RefundDetailVo.Site site = new RefundDetailVo.Site();
        SiteInfoVo infoVo = siteService.info(refund.getSiteId());
        site.setLogo(infoVo.getLogo());
        site.setSiteName(infoVo.getSiteName());
        site.setSiteId(infoVo.getSiteId());
        vo.setSite(site);

        return vo;
    }

    /**
     * 退款申请
     *
     * @param param
     */
    @Override
    public void apply(ApplyRefundParam param) {
        param.setMainId(RequestUtils.memberId());
        param.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        coreRefundActionService.apply(param);
    }

    /**
     * 修改退款申请
     *
     * @param param
     */
    @Override
    public void edit(EditRefundParam param) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("member_id", RequestUtils.memberId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus().toString())
                && !refund.getStatus().equals(OrderRefundStatusEnum.STORE_REFUSE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString())) {
            throw new CommonException("退款已失效");
        }

        MallOrderGoods orderGoods = mallOrderGoodsMapper.selectOne(new QueryWrapper<MallOrderGoods>()
                .eq("order_goods_id", refund.getOrderGoodsId()));
        if (orderGoods == null) throw new CommonException("订单已失效");

        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>()
                .select("delivery_money")
                .eq("order_id", orderGoods.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");

        BigDecimal maxRefundMoney = orderGoods.getGoodsMoney().subtract(orderGoods.getDiscountMoney());
        if (refund.getIsRefundDelivery().equals(1)) maxRefundMoney = maxRefundMoney.add(order.getDeliveryMoney());

        if (param.getApplyMoney().compareTo(maxRefundMoney) == 1)
            throw new CommonException("退款金额不能大于可退款总额");

        MallOrderRefund model = new MallOrderRefund();
        BeanUtil.copyProperties(param, model);
        model.setRefundId(refund.getRefundId());
        model.setShopReason("");
        model.setVoucher(JSONUtil.parseArray(param.getVoucher()).toString());
        model.setStatus(OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus().toString());
        mallOrderRefundMapper.updateById(model);

        // 添加日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        logParam.setMainId(RequestUtils.memberId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setType(OrderRefundActionTypeEnum.EDIT_APPLY_ACTION.getType());
        coreRefundService.addLog(logParam);
        //同步供应商退款修改
        if(orderGoods.getSupplyGoodsId() != null && orderGoods.getSupplyGoodsId() > 0){
            MallOrderRefund refundMode = mallOrderRefundMapper.selectById(model.getRefundId());
            //同步供应商退款信息
            // 触发事件 退款同步供货商的退款单据号
            SyncSupplyRefundEditEvent eventRefund = new SyncSupplyRefundEditEvent();
            eventRefund.addAppSign("supply");
            eventRefund.setRefundData(refundMode);
            eventRefund.setOrderGoodsData(orderGoods);
            eventRefund.setRefundType(param.getRefundType());
            eventRefund.setReason(param.getReason());
            eventRefund.setRemark(param.getRemark());
            eventRefund.setVoucher(JSONUtil.parseArray(param.getVoucher()).toString());
            eventRefund.setSiteId(RequestUtils.siteId());
            EventAndSubscribeOfPublisher.publishAll(eventRefund);
        }
    }

    /**
     * 买家退货
     *
     * @param param
     */
    @Override
    public void delivery(RefundDeliveryParam param) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("member_id", RequestUtils.memberId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString())) {
            throw new CommonException("退款已失效");
        }

        MallOrderRefund model = new MallOrderRefund();
        model.setRefundId(refund.getRefundId());
        model.setDelivery(JSONUtil.parse(param.getDelivery()).toString());
        model.setStatus(OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus().toString());
        mallOrderRefundMapper.updateById(model);

        // 添加日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        logParam.setMainId(RequestUtils.memberId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setType(OrderRefundActionTypeEnum.DELIVERY_ACTION.getType());
        coreRefundService.addLog(logParam);

        //同步供应商退款修改
        if(refund.getSupplyRefundId() != null && refund.getSupplyRefundId() > 0){
            MallOrderRefund refundMode = mallOrderRefundMapper.selectById(model.getRefundId());
            //同步供应商退款信息
            // 触发事件 退款同步供货商的退款单据号
            SyncSupplyRefundDeliveryEvent eventRefund = new SyncSupplyRefundDeliveryEvent();
            eventRefund.setRefundData(refundMode);
            eventRefund.addAppSign("supply");
            eventRefund.setDelivery(JSONUtil.parse(param.getDelivery()).toString());
            eventRefund.setSiteId(RequestUtils.siteId());
            EventAndSubscribeOfPublisher.publishAll(eventRefund);
        }
    }

    /**
     * 修改买家退货
     *
     * @param param
     */
    @Override
    public void editDelivery(RefundDeliveryParam param) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("member_id", RequestUtils.memberId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.STORE_REFUSE_TAKE_REFUND_GOODS_WAIT_BUYER.getStatus().toString())
                && !refund.getStatus().equals(OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus().toString())) {
            throw new CommonException("退款已失效");
        }

        MallOrderRefund model = new MallOrderRefund();
        model.setRefundId(refund.getRefundId());
        model.setDelivery(JSONUtil.parse(param.getDelivery()).toString());
        model.setStatus(OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus().toString());
        mallOrderRefundMapper.updateById(model);

        // 添加日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        logParam.setMainId(RequestUtils.memberId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setType(OrderRefundActionTypeEnum.EDIT_DELIVERY_ACTION.getType());
        coreRefundService.addLog(logParam);

        //同步供应商退款修改
        if(refund.getSupplyRefundId() != null && refund.getSupplyRefundId() > 0){
            MallOrderRefund refundMode = mallOrderRefundMapper.selectById(model.getRefundId());
            //同步供应商退款信息
            // 触发事件 退款同步供货商的退款单据号
            SyncSupplyRefundEditDeliveryEvent eventRefund = new SyncSupplyRefundEditDeliveryEvent();
            eventRefund.setRefundData(refundMode);
            eventRefund.addAppSign("supply");
            eventRefund.setDelivery(JSONUtil.parse(param.getDelivery()).toString());
            eventRefund.setSiteId(RequestUtils.siteId());
            EventAndSubscribeOfPublisher.publishAll(eventRefund);
        }
    }

    /**
     * 创建交易号
     *
     * @return
     */
    private String createRefundNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }

    /**
     * 查询订单项退款信息
     *
     * @param orderGoodsId
     * @return
     */
    @Override
    public RefundDataVo getRefundData(Integer orderGoodsId) {
        MallOrderGoods orderGoods = mallOrderGoodsMapper.selectOne(new QueryWrapper<MallOrderGoods>()
                .select("order_id,order_goods_money")
                .eq("order_goods_id", orderGoodsId)
                .eq("member_id", RequestUtils.memberId()));
        if (orderGoods == null) throw new CommonException("订单已失效");

        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>()
                .select("delivery_money")
                .eq("order_id", orderGoods.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");

        Integer isRefundDelivery = 0;

        // 订单项数量
        Long orderGoodsCount = mallOrderGoodsMapper.selectCount(new QueryWrapper<MallOrderGoods>().eq("order_id", orderGoods.getOrderId()));
        // 已申请退款的订单项数量
        Long refundCount = mallOrderRefundMapper.selectCount(new QueryWrapper<MallOrderRefund>().eq("order_id", orderGoods.getOrderId()).ne("status", OrderRefundStatusEnum.CLOSE.getStatus()));

        if (orderGoodsCount.equals(refundCount + 1)) {
            Long refundDeliveryCount = mallOrderRefundMapper.selectCount(new QueryWrapper<MallOrderRefund>().eq("order_id", orderGoods.getOrderId()).eq("is_refund_delivery", 1).ne("status", OrderRefundStatusEnum.CLOSE.getStatus()));
            if (refundDeliveryCount == 0) isRefundDelivery = 1;
        }

        RefundDataVo vo = new RefundDataVo();
        vo.setRefundMoney(orderGoods.getOrderGoodsMoney());
        vo.setRefundOrderGoodsMoney(orderGoods.getOrderGoodsMoney());
        vo.setIsRefundDelivery(isRefundDelivery);

        if (isRefundDelivery.equals(1)) {
            vo.setRefundMoney(vo.getRefundMoney().add(order.getDeliveryMoney()));
            vo.setRefundDeliveryMoney(order.getDeliveryMoney());
        }

        return vo;
    }

    /**
     * 查询订单项退款信息
     *
     * @param orderRefundNo
     * @return
     */
    @Override
    public RefundDataVo getRefundData(String orderRefundNo) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", orderRefundNo)
                .eq("member_id", RequestUtils.memberId()));
        if (refund == null) throw new CommonException("退款已失效");

        MallOrderGoods orderGoods = mallOrderGoodsMapper.selectOne(new QueryWrapper<MallOrderGoods>()
                .select("order_id,order_goods_money")
                .eq("order_goods_id", refund.getOrderGoodsId())
                .eq("member_id", RequestUtils.memberId()));
        if (orderGoods == null) throw new CommonException("订单已失效");

        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>()
                .select("delivery_money")
                .eq("order_id", orderGoods.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");

        RefundDataVo vo = new RefundDataVo();
        vo.setRefundMoney(orderGoods.getOrderGoodsMoney());
        vo.setRefundOrderGoodsMoney(orderGoods.getOrderGoodsMoney());
        vo.setIsRefundDelivery(refund.getIsRefundDelivery());
        if (refund.getIsRefundDelivery().equals(1)) {
            vo.setRefundMoney(vo.getRefundMoney().add(order.getDeliveryMoney()));
            vo.setRefundDeliveryMoney(order.getDeliveryMoney());
        }

        return vo;
    }

    /**
     * 取消退款
     *
     * @param param
     */
    @Override
    public void close(CloseRefundParam param) {
        param.setMainId(RequestUtils.memberId());
        param.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        coreRefundActionService.close(param);
    }
}
