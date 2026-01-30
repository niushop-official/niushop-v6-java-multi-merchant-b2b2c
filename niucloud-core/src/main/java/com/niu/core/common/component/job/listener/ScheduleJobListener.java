package com.niu.core.common.component.job.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.sys.SysSchedule;
import com.niu.core.entity.sys.SysScheduleLog;
import com.niu.core.mapper.sys.SysScheduleLogMapper;
import com.niu.core.mapper.sys.SysScheduleMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

/**
 * Quartz任务执行监听器 - 记录任务执行日志
 */
@Slf4j
@Component
public class ScheduleJobListener implements JobListener {

    private SysScheduleMapper sysScheduleMapper() {
        return SpringContext.bean(SysScheduleMapper.class);
    }
    
    private SysScheduleLogMapper sysScheduleLogMapper() {
        return SpringContext.bean(SysScheduleLogMapper.class);
    }

    @Override
    public String getName() {
        return "ScheduleJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        // 执行前处理
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // 异常处理
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        try {
            // 获取Job实例
            Object jobInstance = context.getJobInstance();
            if (!(jobInstance instanceof IJobProvider)) {
                return;
            }

            IJobProvider jobProvider = (IJobProvider) jobInstance;
            JobProvider annotation = jobProvider.getClass().getAnnotation(JobProvider.class);
            
            if (annotation == null) {
                return;
            }

            String jobKey = annotation.key();
            String jobName = annotation.name();
            String addon = annotation.source();
            String className = jobProvider.getClass().getName();

            // 查询计划任务配置
            SysSchedule sysSchedule = sysScheduleMapper().selectOne(
                new QueryWrapper<SysSchedule>()
                    .eq("`key`", jobKey)
                    .eq("addon", addon)
                    .last("limit 1")
            );

            // 构建日志对象
            SysScheduleLog.SysScheduleLogBuilder logBuilder = SysScheduleLog.builder()
                .key(jobKey)
                .name(jobName)
                .addon(addon)
                .className(className)
                .job(className)
                .executeTime(DateUtils.currTime());

            if (sysSchedule != null) {
                logBuilder.scheduleId(sysSchedule.getId());
            }

            // 判断执行结果
            if (jobException == null) {
                // 执行成功
                String result = "计划任务：" + jobName + "执行成功";
                logBuilder.status("success").executeResult(result);
            } else {
                // 执行失败
                String errorMsg = "计划任务：" + jobName + "发生错误, 错误原因：" + jobException.getMessage();
                if (jobException.getStackTrace() != null && jobException.getStackTrace().length > 0) {
                    errorMsg += " at " + jobException.getStackTrace()[0].toString();
                }
                logBuilder.status("error").executeResult(errorMsg);
                log.error("定时任务执行失败: {}", errorMsg, jobException);
            }

            // 保存日志
            SysScheduleLog scheduleLog = logBuilder.build();
            sysScheduleLogMapper().insert(scheduleLog);

        } catch (Exception e) {
            log.error("记录定时任务日志失败: {}", e.getMessage(), e);
        }
    }
}
