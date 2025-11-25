package com.niu.mall.listener.verify;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.entity.verify.Verifier;
import com.niu.core.event.verify.VerifierCheckEventDefiner;
import com.niu.core.mapper.verify.VerifierMapper;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventCallback("mall")
@Component
public class VerifierCheckListener extends VerifierCheckEventDefiner {

    @Resource
    private MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    private VerifierMapper verifierMapper;

    @Override
    @SuppressWarnings("unchecked")
    public VerifierCheckResult handleCallback(VerifierCheckEvent event) {
        Map<String, Object> data = event.getData();
        Integer siteId = event.getSiteId();
        String type = event.getType();

        List<Integer> verifyMemberIds = new ArrayList<>();

        if ("shopVirtualGoods".equals(type)) {
            // 虚拟商品核销
            Object orderGoodsIdObj = data.get("order_goods_id");
            Integer orderGoodsId = 0;
            if (orderGoodsIdObj instanceof Integer) {
                orderGoodsId = (Integer) orderGoodsIdObj;
            } else if (orderGoodsIdObj instanceof String) {
                orderGoodsId = Integer.parseInt((String) orderGoodsIdObj);
            } else if (orderGoodsIdObj != null) {
                orderGoodsId = Integer.parseInt(orderGoodsIdObj.toString());
            }

            MallOrderGoods orderGoodsInfo = mallOrderGoodsMapper.selectOne(
                    new QueryWrapper<MallOrderGoods>()
                            .eq("order_goods_id", orderGoodsId)
            );

            if (orderGoodsInfo == null) {
                throw new RuntimeException("订单商品不存在");
            }

            // 查询核销员：供应商ID或站点ID下的核销员，且核销类型包含当前type
            verifyMemberIds = verifierMapper.selectList(
                    new QueryWrapper<Verifier>()
                            .select("member_id")
                            .in("site_id", orderGoodsInfo.getSupplyId(), orderGoodsInfo.getSiteId())
                            .like("verify_type", "\"" + type + "\"")
            ).stream().map(Verifier::getMemberId).toList();

        } else if ("shopPickUpOrder".equals(type)) {
            // 自提订单核销
            verifyMemberIds = verifierMapper.selectList(
                    new QueryWrapper<Verifier>()
                            .select("member_id")
                            .eq("site_id", siteId)
                            .like("verify_type", "\"" + type + "\"")
            ).stream().map(Verifier::getMemberId).toList();
        }

        return new VerifierCheckResult().setVerifyMemberIds(verifyMemberIds);
    }
}

