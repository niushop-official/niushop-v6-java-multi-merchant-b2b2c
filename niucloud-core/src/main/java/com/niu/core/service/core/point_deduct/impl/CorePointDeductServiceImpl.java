package com.niu.core.service.core.point_deduct.impl;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.point_deduct.PointDeduct;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.point_deduct.ICorePointDeductService;
import com.niu.core.service.core.point_deduct.vo.DeductMoneyVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CorePointDeductServiceImpl implements ICorePointDeductService {

    //PHP文件路径
    //use app\service\core\point_deduct\CorePointDeductService;

    private static final String POINT_DEDUCT_KEY = "POINT_DEDUCT";
    private static final String SITE_KEY = "SITE_POINT_DEDUCT_RATE";
    @Resource
    private ICoreConfigService coreConfigService;

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    @Override
    public void setPointDeductConfig(PointDeduct param) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), POINT_DEDUCT_KEY, JSONUtil.parse(param));
    }

    @Override
    public PointDeduct getPointDeductConfig() {
        var config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), POINT_DEDUCT_KEY);
        if (config == null || config.isEmpty()) {
            var pd = new PointDeduct();
            pd.setIsOpen(0);
            pd.setDeductAmount(BigDecimal.ZERO);
            pd.setDesc("");
            return pd;
        }
        return JSONUtil.toBean(config, PointDeduct.class);
    }

    @Override
    public void setSitePointDeductConfig(PointDeduct param) {
        coreConfigService.setConfig(param.getSiteId(), SITE_KEY, JSONUtil.parse(param));
    }

    @Override
    public PointDeduct getSitePointDeductConfig(Integer siteId) {
        var config = coreConfigService.getConfigValue(siteId, SITE_KEY);
        if (config == null || config.isEmpty()) {
            var pd = new PointDeduct();
            pd.setDeductRate(BigDecimal.valueOf(100));
            return pd;
        }
        return JSONUtil.toBean(config, PointDeduct.class);
    }

    @Override
    public DeductMoneyVo calculateDeductMoney(Integer memberPoint,
                                              BigDecimal goodsMoney,
                                              PointDeduct config,
                                              Boolean isOrderGoods,
                                              Integer maxUsePoint) {
        if (maxUsePoint == null) maxUsePoint = 0;
        if (isOrderGoods == null) isOrderGoods = false;

        var defaultReturn = new DeductMoneyVo();
        defaultReturn.setUsePoint(0);
        defaultReturn.setDeductMoney(BigDecimal.ZERO);

        // 获取配置
        if (config == null) {
            config = getPointDeductConfig();
        }

        // 基础校验：功能未开启 或 抵扣金额配置无效
        Integer isOpen = config.getIsOpen();
        if (isOpen == null || isOpen == 0 || config.getDeductAmount() == null
                || config.getDeductAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return defaultReturn;
        }
        BigDecimal perPointDeductMoney = config.getDeductAmount();

        // 校验用户可用积分
        if (memberPoint == null || memberPoint <= 0) {
            return defaultReturn;
        }

        // 1.订单金额可支撑的最大积分（抵扣金额≤商品金额）
        BigDecimal maxPointByMoney;
        if (isOrderGoods) {
            // 订单商品：保留两位小数，向下取整（与 bcdiv 行为一致）
            maxPointByMoney = goodsMoney.divide(perPointDeductMoney, 2, RoundingMode.DOWN);
        } else {
            // 非订单商品：取整
            maxPointByMoney = goodsMoney.divide(perPointDeductMoney, 0, RoundingMode.FLOOR);
        }

        // 2. 实际可使用积分 = 取最小值（用户积分、订单支撑积分）
        BigDecimal usablePoint = BigDecimal.valueOf(memberPoint).min(maxPointByMoney);
        if (isOrderGoods) {
            usablePoint = usablePoint.min(BigDecimal.valueOf(maxUsePoint));
        }
        if (usablePoint.compareTo(BigDecimal.ZERO) <= 0) {
            return defaultReturn;
        }

        // 计算抵扣金额，不能超过商品金额
        BigDecimal deductMoney = usablePoint.multiply(perPointDeductMoney);
        deductMoney = deductMoney.min(goodsMoney);
        // 保留两位小数
        deductMoney = deductMoney.setScale(2, RoundingMode.HALF_UP);

        defaultReturn.setUsePoint(usablePoint.intValue());
        defaultReturn.setDeductMoney(deductMoney);
        return defaultReturn;
    }

    @Override
    public BigDecimal calculatePointMoney(Integer point, PointDeduct config) {
        if (config == null) {
            config = getPointDeductConfig();
        }
        Integer isOpen = config.getIsOpen();
        BigDecimal deductAmountObj = config.getDeductAmount();
        if (isOpen == null || isOpen == 0 ||
                deductAmountObj == null || deductAmountObj.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(point).multiply(deductAmountObj).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void joinPointDeduct(Integer siteId, Integer memberId, Integer point, String orderNo) {
        if (point == null || point <= 0) {
            return;
        }
        // 扣减积分，调用会员账户服务
        coreMemberAccountService.addLog(siteId, memberId, AccountTypeEnum.POINT.getType(), -point.doubleValue(),
                "order", "参与积分抵扣优惠活动", orderNo);
    }

    @Override
    public void refundPointDeduct(Integer siteId, Integer memberId, BigDecimal point, String orderNo) {
        if (point == null || point.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        // 返还积分
        coreMemberAccountService.addLog(siteId, memberId, AccountTypeEnum.POINT.getType(), point.doubleValue(),
                "order_refund", "订单退款返还积分", orderNo);
    }
}
