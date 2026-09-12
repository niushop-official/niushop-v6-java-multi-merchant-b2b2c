package com.niu.mall.job.goods;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.enums.goods.GoodsStatusEnum;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.core.goods.ICoreGoodsStatusService;
import com.niu.core.service.core.goods.param.GoodsStatusEditParam;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.List;


@JobProvider(key = "GoodsExpiredJob", name = "虚拟商品过期自动下架", describe = "虚拟商品过期自动下架", source = "mall")
public class GoodsExpiredJob extends AbstractJobProvider implements IJobProvider {
    private ICoreGoodsStatusService coreGoodsStatusService() {
        return SpringContext.bean(ICoreGoodsStatusService.class);
    }

    private MallGoodsMapper mallGoodsMapper() {
        return SpringContext.bean(MallGoodsMapper.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_type", GoodsTypeEnum.VIRTUAL.getType());
        queryWrapper.eq("virtual_delivery_type", "verify");
        queryWrapper.eq("virtual_verify_type", 2);
        queryWrapper.gt("virtual_indate", 0);
        queryWrapper.le("virtual_indate", System.currentTimeMillis() / 1000);
        queryWrapper.in("status", GoodsStatusEnum.ONLINE.getStatus(), GoodsStatusEnum.WAIT_AUDIT.getStatus());
        queryWrapper.select("goods_id", "site_id");
        List<MallGoods> goodsList = mallGoodsMapper().selectList(queryWrapper);
        if (!goodsList.isEmpty()) {
            List<GoodsStatusEditParam> params = new ArrayList<>();
            goodsList.forEach(goods -> {
                GoodsStatusEditParam param = new GoodsStatusEditParam();
                param.setGoodsId(goods.getGoodsId());
                param.setSiteId(goods.getSiteId());
                params.add(param);
            });
            coreGoodsStatusService().editStatus(params);
        }
    }

}
