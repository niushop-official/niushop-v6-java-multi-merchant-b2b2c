package com.niu.core.service.core.finance.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.applyment.enums.SettlementVerifyStateEnum;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.enums.trade.SettlementEnum;
import com.niu.core.mapper.separ_account.SeparAccountMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.core.finance.ICoreFinanceCalculateService;
import com.niu.core.service.core.finance.param.OrderFinanceParam;
import com.niu.core.service.core.finance.param.OrderPayParam;
import com.niu.core.service.core.finance.param.OrderRefundParam;
import com.niu.core.service.core.finance.vo.CoreFinanceCalculateVo;
import com.niu.core.service.core.finance.vo.OrderPayCalcVo;
import com.niu.core.service.core.finance.vo.OrderRefundCalcVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CoreFinanceCalculateServiceImpl implements ICoreFinanceCalculateService {

    @Resource
    private SiteShopMapper siteShopMapper;

    @Resource
    private SeparAccountMapper separAccountMapper;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @Override
    public CoreFinanceCalculateVo orderPayCalc(OrderFinanceParam orderData) {
        CoreFinanceCalculateVo result = new CoreFinanceCalculateVo();

        BigDecimal mallMoney = BigDecimal.ZERO;
        BigDecimal shopSeparateMoney = BigDecimal.ZERO;
        BigDecimal shopIncomeMoney = BigDecimal.ZERO;
        // 只计算商城订单的平台手续费、支付分账金额
        if (!(ObjectUtil.isNotEmpty(orderData.getSupplyId())  && orderData.getSupplyId() > 0)) {
            BigDecimal orderMoney = orderData.getMallMoney();
            BigDecimal shopCommission = orderData.getShopCommission();
            BigDecimal deliveryMoney = orderData.getDeliveryMoney();

            // 支付分账金额 = 订单金额 - 支付佣金 - 平台手续费 - 运费
            BigDecimal subtract = ((orderMoney.subtract(shopCommission).setScale(2,RoundingMode.HALF_UP)).subtract(mallMoney)).setScale(2,RoundingMode.HALF_UP);
            shopSeparateMoney = (subtract.subtract(deliveryMoney)).setScale(2,RoundingMode.HALF_UP);
            //商户实际收入金额=订单金额-支付佣金-平台手续费
            shopIncomeMoney = subtract;
        }

        // 构建返回结果
        result.setMallMoney(mallMoney);
        result.setShopSeparateMoney(shopSeparateMoney);
        result.setShopIncomeMoney(shopIncomeMoney);
        result.setOrderReceivableMoney(orderData.getOrderMoney().add(orderData.getMallCouponMoney()));
        return result;
    }

    @Override
    public OrderRefundCalcVo orderRefundCalc(OrderRefundParam orderData) {
        OrderRefundCalcVo result = new OrderRefundCalcVo();

        BigDecimal refundMoney = orderData.getMoney();
        BigDecimal supplyRefundMoney = ObjectUtil.isNotEmpty(orderData.getSupplyRefundMoney()) ? orderData.getSupplyRefundMoney() : BigDecimal.ZERO;
        BigDecimal refundCommission = orderData.getRefundCommission();
        BigDecimal mallRate = orderData.getMallRate();
        BigDecimal orderMoney = orderData.getOrderMoney();
        BigDecimal refundDeliveryMoney =  BigDecimal.ZERO;
        if(!(ObjectUtil.isNotEmpty(orderData.getSupplyId()) && orderData.getSupplyId() > 0)){
            refundDeliveryMoney = orderData.getRefundDeliveryMoney();
        }


        // 退款比例 = 退款金额 / 订单金额（避免除以零）
        BigDecimal refundRate = orderMoney.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : refundMoney.divide(orderMoney, 2, RoundingMode.HALF_UP);

        BigDecimal mallCouponMoney = orderData.getMallCouponMoney();
        // 退还平台优惠券补贴 = 平台优惠券金额 × 退款比例
        BigDecimal mallRefundCouponMoney = mallCouponMoney.multiply(refundRate)
                .setScale(2, RoundingMode.HALF_UP);
        // 确保退还的优惠券金额不超过原优惠券金额
        if (mallRefundCouponMoney.compareTo(mallCouponMoney) > 0) {
            mallRefundCouponMoney = mallCouponMoney;
        }

        // 差价退款金额 = 退款金额 - 供应商退款金额
        BigDecimal diffRefundMoney = refundMoney.subtract(supplyRefundMoney);
        // 平台退款手续费 = 差价退款金额 × 平台费率/100
        BigDecimal mallRefundMoney = diffRefundMoney.multiply(mallRate.divide(HUNDRED, 2)).setScale(2, RoundingMode.HALF_UP);

        // 商户订单实退金额 = 差价退款金额 - 平台退款手续费 - 退款佣金
        BigDecimal shopRefundMoney = diffRefundMoney.subtract(mallRefundMoney).subtract(refundCommission);
        // 商户订单应退金额 = 商户订单实退金额 + 退还平台优惠券补贴
        BigDecimal orderNeedRefundMoney = refundMoney.add(mallRefundCouponMoney);

        BigDecimal shopRefundSeparateMoney = refundMoney.subtract(refundCommission).subtract(mallRefundMoney).subtract(orderData.getRefundDeliveryMoney());

        // 构建返回结果
        result.setMallRefundMoney(SettlementEnum.WAIT.getCode().equals(orderData.getSettlementStatus()) ? mallRefundMoney : BigDecimal.ZERO);          // 退还手续费
        result.setShopRefundMoney(shopRefundMoney);          // 商户订单实退金额
        result.setOrderNeedRefundMoney(orderNeedRefundMoney);  // 订单应退金额
        result.setMallRefundCouponMoney(mallRefundCouponMoney); // 退还平台优惠券补贴
        result.setShopRefundSeparateMoney(SettlementEnum.SETTLED.getCode().equals(orderData.getSettlementStatus()) ? shopRefundSeparateMoney : BigDecimal.ZERO); // 退款分账金额

        result.setRefundCommission(SettlementEnum.SETTLED.getCode().equals(orderData.getSettlementStatus()) ? orderData.getRefundCommission() : BigDecimal.ZERO); // 退还平台优惠券补贴
        result.setRefundDeliveryMoney(refundDeliveryMoney); // 退款分账金额
        return result;
    }
}