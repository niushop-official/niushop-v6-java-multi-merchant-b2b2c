package com.niu.mall.service.core.goods.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.service.core.sys.vo.SysStatVo;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsStat;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsStatMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import com.niu.mall.service.core.goods.param.GoodsDelStatParam;
import com.niu.mall.service.core.goods.vo.MallGoodsStatVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoreGoodsStatServiceImpl implements ICoreGoodsStatService {
    @Resource
    private MallGoodsStatMapper mallGoodsStatMapper;
    @Resource
    private MallOrderGoodsMapper mallOrderGoodsMapper;
    @Resource
    private MallGoodsMapper mallGoodsMapper;

    @Override
    public void addStat(GoodsAddStatParam param) {
        if (param.getGoodsId() <= 0) {
            return;
        }

        QueryWrapper<MallGoodsStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date", DateUtils.currInitDate())
                .eq("date_time", DateUtils.StringToTimestamp(new DateTime().toString("yyyy-MM-dd")))
                .eq("site_id", param.getSiteId())
                .eq("goods_id", param.getGoodsId());

        MallGoodsStat stat = mallGoodsStatMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(stat)) {
            stat = new MallGoodsStat();
            BeanUtils.copyProperties(param, stat);
            stat.setDate(DateUtils.currInitDate());
            stat.setDateTime(DateUtils.StringToTimestamp(new DateTime().toString("yyyy-MM-dd")));
            stat.setSiteId(param.getSiteId());
            stat.setGoodsId(param.getGoodsId());
            mallGoodsStatMapper.insert(stat);
            return;
        }

        stat.setSaleNum(param.getSaleNum() + stat.getSaleNum());
        stat.setCartNum(param.getCartNum() + stat.getCartNum());
        stat.setPayNum(param.getPayNum() + stat.getPayNum());
        stat.setRefundNum(param.getRefundNum() + stat.getRefundNum());
        stat.setEvaluateNum(param.getEvaluateNum() + stat.getEvaluateNum());
        stat.setGoodsVisitMemberCount(param.getGoodsVisitMemberCount() + stat.getGoodsVisitMemberCount());
        stat.setCollectNum(param.getCollectNum() + stat.getCollectNum());
        stat.setAccessNum(param.getAccessNum() + stat.getAccessNum());
        stat.setPayMoney(param.getPayMoney() == null ? stat.getPayMoney() : param.getPayMoney().add(stat.getPayMoney()));
        stat.setRefundMoney(param.getRefundMoney() == null ? stat.getRefundMoney() : param.getRefundMoney().add(stat.getRefundMoney()));
        mallGoodsStatMapper.updateById(stat);
    }

    @Override
    public void delStat(GoodsDelStatParam param) {
        MallGoodsStat model = mallGoodsStatMapper.selectOne(new QueryWrapper<MallGoodsStat>().eq("date", param.getDate())
                .eq("site_id", param.getSiteId()).eq("site_id", param.getGoodsId()));
        Boolean er = false;
        if (ObjectUtil.isNotEmpty( model)){
            if (ObjectUtil.isNotEmpty(param.getSaleNum())){
                int saleNum = model.getSaleNum() - param.getSaleNum();
                model.setSaleNum(Math.max(saleNum, 0));
                er = true;
            }
        }
        if(er){
            mallGoodsStatMapper.updateById(model);
        }
    }

    @Override
    public MallGoodsStatVo getGoodsStat(Integer siteId, String startDate, String endDate) {
        long startTime = DateUtils.StringToTimestamp(startDate);
        long endTime = DateUtils.StringToTimestamp(endDate);
        QueryWrapper<MallGoodsStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        
        if(ObjectUtil.isNotEmpty(startDate) && ObjectUtil.isNotEmpty(endDate))
        {
            queryWrapper.ge("date_time", startTime);
            queryWrapper.le("date_time", endTime);
        }
        else if(ObjectUtil.isNotEmpty(startDate))
        {
            queryWrapper.eq("date_time", startTime);
        }
        else
        {
            queryWrapper.gt("date_time", 0);
        }

        // 按照 PHP STAT_FIELD 的顺序排列字段
        queryWrapper.select("IFNULL(sum(sale_num), 0) AS sale_num, " +
                            "IFNULL(sum(pay_num), 0) AS pay_num, " +
                            "IFNULL(sum(pay_money), 0) AS pay_money, " +
                            "IFNULL(sum(refund_num), 0) AS refund_num, " +
                            "IFNULL(sum(refund_money), 0) AS refund_money, " +
                            "IFNULL(sum(access_num), 0) AS access_num, " +
                            "IFNULL(sum(collect_num), 0) AS collect_num, " +
                            "IFNULL(sum(cart_num), 0) AS cart_num, " +
                            "IFNULL(sum(evaluate_num), 0) AS evaluate_num, " +
                            "IFNULL(sum(goods_visit_member_count), 0) AS goods_visit_member_count");
        MallGoodsStat mallGoodsStat = mallGoodsStatMapper.selectOne(queryWrapper);
        MallGoodsStatVo vo = new MallGoodsStatVo();
        if (mallGoodsStat != null) {
            BeanUtil.copyProperties(mallGoodsStat, vo);
        }
        vo.setSiteId(siteId);
        return vo;
    }

    @Override
    public void saveGoodsPayNumAndMoneyByOrderId(Integer orderId) {
        QueryWrapper<MallOrderGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        List<MallOrderGoods> shopOrderGoods =mallOrderGoodsMapper.selectList(queryWrapper);
        if (!shopOrderGoods.isEmpty()){
            for (MallOrderGoods shopOrderGood : shopOrderGoods){
                GoodsAddStatParam param = new GoodsAddStatParam();
                param.setSiteId(shopOrderGood.getSiteId());
                param.setGoodsId(shopOrderGood.getGoodsId());
                param.setPayNum(shopOrderGood.getNum());
                param.setPayMoney(shopOrderGood.getOrderGoodsMoney());
                addStat(param);

                // 更新支付件数
                UpdateWrapper<MallGoods> updateWrapper1 = new UpdateWrapper<>();
                updateWrapper1.eq("goods_id", shopOrderGood.getGoodsId())
                        .setSql("pay_num = pay_num + " + shopOrderGood.getNum());
                mallGoodsMapper.update(new MallGoods(), updateWrapper1);

                // 更新支付金额
                UpdateWrapper<MallGoods> updateWrapper2 = new UpdateWrapper<>();
                updateWrapper2.eq("goods_id", shopOrderGood.getGoodsId())
                        .setSql("pay_money = pay_money + " + shopOrderGood.getOrderGoodsMoney());
                mallGoodsMapper.update(new MallGoods(), updateWrapper2);
            }
        }
    }

    @Override
    public void saveGoodsRefundNumAndMoneyByOrderId(MallOrderRefund refund) {
        MallOrderGoods goods = mallOrderGoodsMapper.selectById(refund.getOrderGoodsId());
        if(ObjectUtil.isNotEmpty(goods)){
            GoodsAddStatParam param = new GoodsAddStatParam();
            param.setSiteId(goods.getSiteId());
            param.setGoodsId(goods.getGoodsId());
            param.setRefundNum(goods.getNum());
            param.setRefundMoney(refund.getMoney());
            addStat(param);

            // 更新支付件数
            UpdateWrapper<MallGoods> updateWrapper1 = new UpdateWrapper<>();
            updateWrapper1.eq("goods_id", goods.getGoodsId())
                    .setSql("refund_num = refund_num + " + goods.getNum());
            mallGoodsMapper.update(new MallGoods(), updateWrapper1);

            // 更新支付金额
            UpdateWrapper<MallGoods> updateWrapper2 = new UpdateWrapper<>();
            updateWrapper2.eq("goods_id", goods.getGoodsId())
                    .setSql("refund_money = refund_money + " + refund.getMoney());
            mallGoodsMapper.update(new MallGoods(), updateWrapper2);
        }
    }

}
