package com.niu.mall.job.marketing;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.entity.coupon.MallCouponMember;
import com.niu.core.enums.coupon.CouponMemberStatusEnum;
import com.niu.core.mapper.coupon.MallCouponMemberMapper;
import com.niu.core.service.core.marketing.ICoreCouponMemberService;
import org.quartz.JobExecutionContext;

import java.util.List;

@JobProvider(key = "CouponMemberExpireJob", name = "优惠券到期自动过期", describe = "优惠券到期自动过期", source = "mall")
public class CouponMemberExpireJob extends AbstractJobProvider implements IJobProvider {

    private ICoreCouponMemberService coreCouponMemberService() {
        return SpringContext.bean(ICoreCouponMemberService.class);
    }

    private MallCouponMemberMapper mallCouponMemberMapper() {
        return SpringContext.bean(MallCouponMemberMapper.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<MallCouponMember> list = mallCouponMemberMapper().selectList(new QueryWrapper<MallCouponMember>()
                        .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                .le("expire_time", System.currentTimeMillis() / 1000).select("id"));
        if(!list.isEmpty()){
            List<Integer> ids = list.stream().map(MallCouponMember::getId).toList();
            coreCouponMemberService().expire(ids);
        }

    }
}
