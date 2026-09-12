package com.niu.mall.job.marketing;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.mall.entity.fullactive.MallFullGift;
import com.niu.core.entity.fullactive.MallFullReduction;
import com.niu.mall.enums.fullactive.FullGiftStatusEnum;
import com.niu.core.enums.fullactive.FullReductionStatusEnum;
import com.niu.mall.mapper.fullactive.FullGiftMapper;
import com.niu.core.mapper.fullactive.FullReductionMapper;
import com.niu.mall.service.site.marketing.ISiteFullGiftService;
import com.niu.core.service.site.marketing.ISiteFullReductionService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;

import java.util.List;

@JobProvider(key = "FullActiveStartJob", name = "满减满赠活动到时自动开启", describe = "满减满赠活动到时自动开启", source = "mall")
@Slf4j
public class FullActiveStartJob extends AbstractJobProvider implements IJobProvider {

    private FullReductionMapper fullReductionMapper() {
        return SpringContext.bean(FullReductionMapper.class);
    }

    private ISiteFullReductionService fullReductionService() {
        return SpringContext.bean(ISiteFullReductionService.class);
    }

    private FullGiftMapper fullGiftMapper() {
        return SpringContext.bean(FullGiftMapper.class);
    }

    private ISiteFullGiftService siteFullGiftService() {
        return SpringContext.bean(ISiteFullGiftService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        Long now = System.currentTimeMillis() / 1000;
        List<MallFullReduction> fullReductionList = fullReductionMapper().selectList(new LambdaQueryWrapper<MallFullReduction>()
                .eq(MallFullReduction::getStatus, FullReductionStatusEnum.WAIT.getStatus())
                .le(MallFullReduction::getStartTime, now)
                .select(MallFullReduction::getId)
        );
        if (ObjectUtil.isNotEmpty(fullReductionList)) {
            ISiteFullReductionService iSiteFullReductionService = fullReductionService();
            for (var item : fullReductionList) {
                try {
                    iSiteFullReductionService.setStatus(item.getId(), FullReductionStatusEnum.PROCESS.getStatus());
                } catch (Exception e) {
                    log.error("开启满减活动状态失败, activityId: {}", item.getId(), e);
                }
            }
        }
        List<MallFullGift> fullGiftList = fullGiftMapper().selectList(new LambdaQueryWrapper<MallFullGift>()
                .eq(MallFullGift::getStatus, FullGiftStatusEnum.WAIT.getStatus())
                .le(MallFullGift::getStartTime, now)
                .select(MallFullGift::getId)
        );
        if (ObjectUtil.isNotEmpty(fullGiftList)) {
            ISiteFullGiftService iSiteFullGiftService = siteFullGiftService();
            for (var item : fullGiftList) {
                try {
                    iSiteFullGiftService.setStatus(item.getId(), FullGiftStatusEnum.PROCESS.getStatus());
                } catch (Exception e) {
                    log.error("开启满赠活动状态失败, activityId: {}", item.getId(), e);
                }
            }
        }
    }
}
