package com.niu.mall.job.marketing;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.entity.coupon.MallCoupon;
import com.niu.core.enums.coupon.CouponStatusEnum;
import com.niu.core.mapper.coupon.MallCouponMapper;
import com.niu.mall.service.core.marketing.ICoreCouponService;
import org.quartz.JobExecutionContext;

import java.util.List;
import java.util.stream.Collectors;

@JobProvider(key = "CouponEndJob", name = "优惠券到时自动关闭", describe = "优惠券到时自动关闭", source = "mall")
public class CouponEndJob extends AbstractJobProvider implements IJobProvider {

    private MallCouponMapper mallCouponMapper() {
        return SpringContext.bean(MallCouponMapper.class);
    }

    private ICoreCouponService coreCouponService() {
        return SpringContext.bean(ICoreCouponService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<MallCoupon> list = mallCouponMapper().selectList(new QueryWrapper<MallCoupon>()
                .in("status", CouponStatusEnum.NORMAL.getStatus(),CouponStatusEnum.WAIT_START.getStatus())
         //       .gt("start_time", 0)
                .gt("end_time", 0)
                .le("end_time", System.currentTimeMillis() / 1000)
                .select("id")
        );
        if (ObjectUtil.isNotEmpty(list)) {
            List<Integer> ids = list.stream().map(i -> i.getId()).collect(Collectors.toList());
            coreCouponService().couponExpire(ids);
        }
    }
}

