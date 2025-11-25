package com.niu.mall.service.core.goods;

import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import com.niu.mall.service.core.goods.param.GoodsDelStatParam;
import com.niu.mall.service.core.goods.vo.MallGoodsStatVo;

public interface ICoreGoodsStatService {
    void addStat(GoodsAddStatParam param);

    void delStat(GoodsDelStatParam param);

    MallGoodsStatVo getGoodsStat(Integer siteId, String startDate, String endDate);

    void saveGoodsPayNumAndMoneyByOrderId(Integer orderId);

    void saveGoodsRefundNumAndMoneyByOrderId(MallOrderRefund refund);
}
