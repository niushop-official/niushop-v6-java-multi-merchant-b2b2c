package com.niu.core.service.core.cash.order.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.cash.CashOrder;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.cash.CashOrderLogEnum;
import com.niu.core.enums.cash.CashOrderStatusEnum;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.mapper.cash.CashOrderMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.cash.order.ICoreCashOrderCreateService;
import com.niu.core.service.core.cash.order.ICoreCashOrderEventService;
import com.niu.core.service.core.cash.order.param.AfterCashOrderCreateParam;
import com.niu.core.service.core.cash.order.param.CoreCashOrderCalculateParam;
import com.niu.core.service.core.cash.order.param.CoreCashOrderCreateParam;
import com.niu.core.service.core.cash.order.vo.CashOrderBasicVo;
import com.niu.core.service.core.cash.order.vo.CashOrderCreateCacheVo;
import com.niu.core.service.core.cash.order.vo.CashOrderCreateResultVo;
import com.niu.core.service.core.pay.ICorePayService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CoreCashOrderCreateServiceImpl implements ICoreCashOrderCreateService {

    private static final String ORDER_CACHE_TAG = "order_cache";
    private static final long ORDER_CACHE_TIMEOUT = 3600L;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private SiteMapper siteMapper;

    @Resource
    private CashOrderMapper cashOrderMapper;

    @Resource
    private ICorePayService corePayService;

    @Resource
    private ICoreCashOrderEventService coreCashOrderEventService;

    @Override
    public CashOrderCreateCacheVo calculate(CoreCashOrderCalculateParam param) {
        BigDecimal totalMoney = moneyFormat(ObjectUtil.defaultIfNull(param.getTotalMoney(), BigDecimal.ZERO));
        BigDecimal noDiscountMoney = moneyFormat(ObjectUtil.defaultIfNull(param.getNoDiscountMoney(), BigDecimal.ZERO));
        BigDecimal discountableMoney = moneyFormat(totalMoney.subtract(noDiscountMoney).max(BigDecimal.ZERO));

        String orderKey = StrUtil.blankToDefault(param.getOrderKey(), "");
        if (StrUtil.isBlank(orderKey)) {
            orderKey = confirm(param);
        }

        CashOrderCreateCacheVo cacheVo = getOrderCache(orderKey + "_basic");
        cacheVo.setOrderKey(orderKey);
        setOrderCache(orderKey, cacheVo);

        BigDecimal orderMoney = moneyFormat(ObjectUtil.defaultIfNull(cacheVo.getBasic().getOrderMoney(), BigDecimal.ZERO));
        orderMoney = moneyFormat(moneyCalculate(orderMoney, totalMoney));

        CashOrderBasicVo basic = cacheVo.getBasic();
        basic.setTotalMoney(totalMoney);
        basic.setNoDiscountMoney(noDiscountMoney);
        basic.setDiscountableMoney(discountableMoney);
        basic.setOrderMoney(orderMoney.max(BigDecimal.ZERO));

        setOrderCache(orderKey, cacheVo);
        return cacheVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CashOrderCreateResultVo create(CoreCashOrderCreateParam param) {
        checkMember(param.getMemberId());

        String orderKey = StrUtil.blankToDefault(param.getOrderKey(), "");
        CashOrderCreateCacheVo cacheVo = getOrderCache(orderKey);
        if (!param.getMemberId().equals(cacheVo.getMemberId())) {
            throw new CommonException("订单已过期");
        }
        if (!cacheVo.getError().isEmpty()) {
            throw new CommonException(cacheVo.getError().get(0));
        }

        CashOrderBasicVo basic = cacheVo.getBasic();
        Site site = siteMapper.selectById(cacheVo.getSiteId());
        String siteName = site == null ? "" : ObjectUtil.defaultIfNull(site.getSiteName(), "");

        CashOrder order = new CashOrder();
        order.setSiteId(cacheVo.getSiteId());
        order.setSiteName(siteName);
        order.setStatus(CashOrderStatusEnum.WAIT_PAY.getStatus());
        order.setOrderFrom(StrUtil.blankToDefault(param.getOrderFrom(), "h5"));
        order.setMemberId(param.getMemberId());
        order.setOrderNo(CommonUtils.createNo());
        order.setIp(RequestUtils.ip());
        order.setTotalMoney(basic.getTotalMoney());
        order.setNoDiscountMoney(basic.getNoDiscountMoney());
        order.setDiscountableMoney(basic.getDiscountableMoney());
        order.setPlatformCouponMoney(basic.getPlatformCouponMoney());
        order.setPlatformCouponId(basic.getPlatformCouponId());
        order.setSiteCouponMoney(basic.getSiteCouponMoney());
        order.setSiteCouponId(basic.getSiteCouponId());
        order.setPointDeductMoney(basic.getPointDeductMoney());
        order.setPoint(basic.getPoint());
        order.setPointMoney(basic.getPointMoney());
        order.setDiscountMoney(basic.getDiscountMoney());
        order.setPlatDiscountMoney(basic.getPlatDiscountMoney());
        order.setOrderMoney(basic.getOrderMoney());
        order.setMemberRemark(StrUtil.blankToDefault(param.getMemberRemark(), ""));
        order.setCreateTime(System.currentTimeMillis() / 1000);

        cashOrderMapper.insert(order);

        corePayService.create(
                order.getSiteId(),
                CashOrderLogEnum.MEMBER.getCode(),
                order.getMemberId(),
                order.getOrderMoney(),
                "cash",
                order.getOrderId(),
                "买单订单"
        );

        delOrderCache(orderKey);

        AfterCashOrderCreateParam afterParam = new AfterCashOrderCreateParam();
        afterParam.setSiteId(order.getSiteId());
        afterParam.setOrderId(order.getOrderId());
        afterParam.setOrderData(order);
        afterParam.setBasic(basic);
        afterParam.setMainType(StrUtil.blankToDefault(param.getMainType(), CashOrderLogEnum.MEMBER.getCode()));
        afterParam.setMainId(ObjectUtil.defaultIfNull(param.getMainId(), order.getMemberId()));
        afterParam.setTime(System.currentTimeMillis() / 1000);
        coreCashOrderEventService.orderCreateAfter(afterParam);

        CashOrderCreateResultVo result = new CashOrderCreateResultVo();
        result.setTradeType("cash");
        result.setOrderId(order.getOrderId());
        return result;
    }

    private String confirm(CoreCashOrderCalculateParam param) {
        checkMember(param.getMemberId());

        CashOrderCreateCacheVo cacheVo = new CashOrderCreateCacheVo();
        cacheVo.setMemberId(param.getMemberId());
        cacheVo.setSiteId(param.getSiteId());

        String orderKey = CommonUtils.createOrderKey("", String.valueOf(param.getMemberId()));
        cacheVo.setOrderKey(orderKey);
        setOrderCache(orderKey, cacheVo);
        setOrderCache(orderKey + "_basic", cacheVo);
        return orderKey;
    }

    private void checkMember(Integer memberId) {
        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .eq("member_id", memberId)
                .select("member_id", "status"));
        if (member == null) {
            throw new CommonException("CASH_ORDER_BUYER_NOT_FOUND");
        }
        if (!StatusEnum.ON.getStatus().equals(member.getStatus())) {
            throw new CommonException("CASH_ORDER_BUYER_LOCKED");
        }
    }

    private CashOrderCreateCacheVo getOrderCache(String orderKey) {
        Cached cache = CacheFactory.getCacheOperator();
        Object data = cache.tag(ORDER_CACHE_TAG).get(orderKey);
        if (data == null) {
            throw new CommonException("订单已过期");
        }
        return JSONUtil.toBean(JSONUtil.parseObj(data), CashOrderCreateCacheVo.class);
    }

    private void setOrderCache(String orderKey, CashOrderCreateCacheVo cacheVo) {
        Cached cache = CacheFactory.getCacheOperator();
        cache.tag(ORDER_CACHE_TAG).put(orderKey, JSONUtil.toJsonStr(cacheVo), ORDER_CACHE_TIMEOUT);
    }

    private void delOrderCache(String orderKey) {
        if (StrUtil.isBlank(orderKey)) {
            return;
        }
        Cached cache = CacheFactory.getCacheOperator();
        cache.tag(ORDER_CACHE_TAG).remove(orderKey);
        cache.tag(ORDER_CACHE_TAG).remove(orderKey + "_basic");
    }

    private BigDecimal moneyFormat(BigDecimal money) {
        return money.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.FLOOR)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
    }

    private BigDecimal moneyCalculate(BigDecimal... amounts) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal amount : amounts) {
            total = total.add(ObjectUtil.defaultIfNull(amount, BigDecimal.ZERO));
        }
        return moneyFormat(total);
    }

    /**
     * 比例格式化（向下取整）
     * @param rate 比例值
     * @return 格式化后的比例
     */
    private BigDecimal rateFormat(BigDecimal rate) {
        if (rate == null) {
            return BigDecimal.ZERO;
        }
        return rate.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.FLOOR)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
    }

    /**
     * 获取订单数据（用于缓存或传递）
     * @param cacheVo 订单缓存数据
     * @return 订单基本数据
     */
    private CashOrderBasicVo getOrderData(CashOrderCreateCacheVo cacheVo) {
        if (cacheVo == null) {
            return new CashOrderBasicVo();
        }
        return cacheVo.getBasic();
    }
}
