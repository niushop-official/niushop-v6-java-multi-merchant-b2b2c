package com.niu.core.service.core.point_deduct;

import com.niu.core.entity.point_deduct.PointDeduct;
import com.niu.core.service.core.point_deduct.vo.DeductMoneyVo;

import java.math.BigDecimal;

public interface ICorePointDeductService {

    /**
     * 设置全局积分抵扣配置（site_id = 0）
     *
     * @param param 折扣规则配置
     */
    void setPointDeductConfig(PointDeduct param);

    /**
     * 获取全局积分抵扣配置
     *
     * @return 配置参数（含默认值）
     */
    PointDeduct getPointDeductConfig();

    /**
     * 设置站点积分抵扣比例配置
     *
     * @param param 包含 site_id 的配置参数
     */
    void setSitePointDeductConfig(PointDeduct param);

    /**
     * 获取站点积分抵扣比例配置
     *
     * @param siteId 站点ID
     * @return 配置参数（含默认值）
     */
    PointDeduct getSitePointDeductConfig(Integer siteId);

    /**
     * 计算积分可抵扣金额
     *
     * @param memberPoint  用户可用积分
     * @param goodsMoney   商品金额
     * @param config       抵扣配置（可选，为空则使用全局配置）
     * @param isOrderGoods 是否为订单商品（影响计算方式）
     * @param maxUsePoint  最大可使用积分（仅在 isOrderGoods=true 时有效）
     * @return DeductMoneyVo
     */
    DeductMoneyVo calculateDeductMoney(Integer memberPoint,
                                       BigDecimal goodsMoney,
                                       PointDeduct config,
                                       Boolean isOrderGoods,
                                       Integer maxUsePoint);

    /**
     * 简单计算给定积分可抵扣金额
     *
     * @param point  积分数量
     * @param config 抵扣配置（可选，为空则使用全局配置）
     * @return 可抵扣金额
     */
    BigDecimal calculatePointMoney(Integer point, PointDeduct config);

    /**
     * 购物抵扣积分
     *
     * @param siteId   站点ID
     * @param memberId 会员ID
     * @param point    消耗积分
     * @param orderNo  订单号
     */
    void joinPointDeduct(Integer siteId, Integer memberId, Integer point, String orderNo);

    /**
     * 退款返还积分
     *
     * @param siteId   站点ID
     * @param memberId 会员ID
     * @param point    返还积分
     * @param orderNo  订单号
     */
    void refundPointDeduct(Integer siteId, Integer memberId, Integer point, String orderNo);
}
