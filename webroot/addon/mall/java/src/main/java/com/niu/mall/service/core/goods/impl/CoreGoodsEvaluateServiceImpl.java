package com.niu.mall.service.core.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsEvaluate;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.goods.GoodsEvaluateEnum;
import com.niu.mall.mapper.goods.MallGoodsEvaluateMapper;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.goods.ICoreGoodsEvaluateService;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import com.niu.mall.service.core.goods.param.ShopScoreCalcuParam;
import com.niu.mall.service.core.goods.vo.GoodsTotalEvaluateVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class CoreGoodsEvaluateServiceImpl implements ICoreGoodsEvaluateService {

    @Resource
    private MallGoodsEvaluateMapper mallGoodsEvaluateMapper;

    @Resource
    private MallOrderMapper mallOrderMapper;

    @Resource
    private MallGoodsMapper mallGoodsMapper;

    @Resource
    private SiteShopMapper siteShopMapper;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Override
    public Integer addEvaluate(MallGoodsEvaluate mallGoodsEvaluate) {
        mallGoodsEvaluateMapper.insert(mallGoodsEvaluate);

        if (mallGoodsEvaluate.getOrderId() > 0) {
            MallOrder mallOrder = new MallOrder();
            mallOrder.setIsEvaluate(1);
            mallOrder.setEvaluateTimer(0L);
            mallOrderMapper.update(mallOrder, new QueryWrapper<MallOrder>().eq("order_id", mallGoodsEvaluate.getOrderId()));
        }

        if (mallGoodsEvaluate.getIsAudit() == GoodsEvaluateEnum.AUDIT_NO.getStatus()) {
            GoodsAddStatParam addStatParam = new GoodsAddStatParam();
            addStatParam.setGoodsId(mallGoodsEvaluate.getGoodsId());
            addStatParam.setSiteId(mallGoodsEvaluate.getSiteId());
            addStatParam.setEvaluateNum(1);
            coreGoodsStatService.addStat(addStatParam);

            //查询商品信息
            MallGoods mallGoods = mallGoodsMapper.selectById(mallGoodsEvaluate.getGoodsId());
            mallGoods.setEvaluateNum(mallGoods.getEvaluateNum()+1);
            mallGoodsMapper.updateById(mallGoods);

            //计算店铺分数
            ShopScoreCalcuParam shopScoreCalcuParam = new ShopScoreCalcuParam();
            shopScoreCalcuParam.setSiteId(mallGoodsEvaluate.getSiteId());
            shopScoreCalcuParam.setQualityScore(mallGoodsEvaluate.getQualityScore());
            shopScoreCalcuParam.setServiceScore(mallGoodsEvaluate.getServiceScore());
            shopScoreCalcuParam.setLogisticsScore(mallGoodsEvaluate.getLogisticsScore());
            shopScoreCalculate(shopScoreCalcuParam);
        }

        return mallGoodsEvaluate.getEvaluateId();
    }

    @Override
    public void shopScoreCalculate(ShopScoreCalcuParam shopScoreCalcuParam) {
        //查询店铺
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", shopScoreCalcuParam.getSiteId()));
        if(ObjectUtil.isNotEmpty( shop)){
            if(ObjectUtil.isEmpty( shop.getEvaluateNum()) || (ObjectUtil.isNotEmpty( shop.getEvaluateNum()) && shop.getEvaluateNum() == 0)){
                MPJQueryWrapper<MallGoodsEvaluate> queryWrapper = new MPJQueryWrapper<>();
                queryWrapper.eq("site_id", shopScoreCalcuParam.getSiteId());
                queryWrapper.in("is_audit", GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus());
                queryWrapper.select("sum(quality_score) as total_quality_score,sum(service_score) as total_service_score,sum(logistics_score) as total_logistics_score, count(evaluate_id) as evaluate_num");
                GoodsTotalEvaluateVo totalEvaluate = mallGoodsEvaluateMapper.selectJoinOne(GoodsTotalEvaluateVo.class,queryWrapper);
                if(ObjectUtil.isNotEmpty(totalEvaluate)){
                    BigDecimal qualityScore = BigDecimal.ZERO;
                    BigDecimal serviceScore = BigDecimal.ZERO;
                    BigDecimal logisticsScore = BigDecimal.ZERO;
                    if(totalEvaluate.getEvaluateNum() <= 0){
                        totalEvaluate.setTotalServiceScore(new BigDecimal("5"));
                        totalEvaluate.setTotalQualityScore(new BigDecimal("5"));
                        totalEvaluate.setTotalLogisticsScore(new BigDecimal("5"));
                    }else{
                        qualityScore = totalEvaluate.getTotalQualityScore().divide(new BigDecimal(totalEvaluate.getEvaluateNum()), 1, RoundingMode.HALF_UP);
                        serviceScore = totalEvaluate.getTotalServiceScore().divide(new BigDecimal(totalEvaluate.getEvaluateNum()), 1, RoundingMode.HALF_UP);
                        logisticsScore = totalEvaluate.getTotalLogisticsScore().divide(new BigDecimal(totalEvaluate.getEvaluateNum()), 1, RoundingMode.HALF_UP);
                    }
                    shop.setServiceScore(serviceScore);
                    shop.setQualityScore(qualityScore);
                    shop.setLogisticsScore(logisticsScore);
                    shop.setEvaluateNum(totalEvaluate.getEvaluateNum());
                }
            }else{
                Integer evaluateNum = shop.getEvaluateNum()+1;
                BigDecimal qualityScore = shop.getQualityScore().multiply(new BigDecimal(shop.getEvaluateNum()));
                qualityScore = qualityScore.add(shopScoreCalcuParam.getQualityScore());
                qualityScore = qualityScore.divide(new BigDecimal(evaluateNum), 1, RoundingMode.HALF_UP);

                BigDecimal serviceScore = shop.getServiceScore().multiply(new BigDecimal(shop.getEvaluateNum()));
                serviceScore = serviceScore.add(shopScoreCalcuParam.getServiceScore());
                serviceScore = serviceScore.divide(new BigDecimal(evaluateNum), 1, RoundingMode.HALF_UP);

                BigDecimal logisticsScore = shop.getLogisticsScore().multiply(new BigDecimal(shop.getEvaluateNum()));
                logisticsScore = logisticsScore.add(shopScoreCalcuParam.getLogisticsScore());
                logisticsScore = logisticsScore.divide(new BigDecimal(evaluateNum), 1, RoundingMode.HALF_UP);

                shop.setServiceScore(serviceScore);
                shop.setQualityScore(qualityScore);
                shop.setLogisticsScore(logisticsScore);
                shop.setEvaluateNum(evaluateNum);
            }
            siteShopMapper.updateById( shop);
        }
    }
}
