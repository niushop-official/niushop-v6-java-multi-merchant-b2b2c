package com.niu.mall.job.marketing;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.mall.entity.active.MallActive;
import com.niu.mall.enums.active.ActiveStatusEnum;
import com.niu.mall.mapper.active.MallActiveMapper;
import com.niu.mall.service.core.marketing.ICoreActiveService;
import org.quartz.JobExecutionContext;

import java.util.List;

@JobProvider(key = "ActiveEndJob", name = "活动到时自动关闭", describe = "活动到时自动关闭", source = "mall")
public class ActiveEndJob extends AbstractJobProvider implements IJobProvider {

    private MallActiveMapper mallActiveMapper() {
        return SpringContext.bean(MallActiveMapper.class);
    }

    private ICoreActiveService coreActiveService() {
        return SpringContext.bean(ICoreActiveService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<MallActive> activeList = mallActiveMapper().selectList(new QueryWrapper<MallActive>()
                .eq("active_status", ActiveStatusEnum.ACTIVE.getStatus())
                .lt("end_time", System.currentTimeMillis() / 1000)
                .select("active_id")
        );
        if (ObjectUtil.isNotEmpty(activeList)) {
            for (MallActive item : activeList) {
                if (item.getEndTime() != null && item.getEndTime() != 0) {
                    coreActiveService().end(item.getActiveId());
                }
            }
        }
    }
}
