package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.component.job.factory.JobProviderFactory;
import com.niu.core.common.component.job.model.JobInfo;
import com.niu.core.common.component.job.quartz.QuartzJobManager;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.sys.SysSchedule;
import com.niu.core.entity.sys.SysScheduleLog;
import com.niu.core.mapper.sys.SysScheduleLogMapper;
import com.niu.core.mapper.sys.SysScheduleMapper;
import com.niu.core.service.admin.sys.ISysScheduleService;
import com.niu.core.service.admin.sys.param.SysScheduleLogSearchParam;
import com.niu.core.service.admin.sys.param.SysScheduleParam;
import com.niu.core.service.admin.sys.param.SysScheduleSearchParam;
import com.niu.core.service.admin.sys.param.SysScheduleStatusParam;
import com.niu.core.service.admin.sys.vo.SysScheduleInfoVo;
import com.niu.core.service.admin.sys.vo.SysScheduleListVo;
import com.niu.core.service.admin.sys.vo.SysScheduleLogListVo;
import com.niu.core.service.admin.sys.vo.SysScheduleTemplateVo;
import com.niu.core.service.core.schedule.ICoreScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 系统任务实现
 */
@Service
@Slf4j
public class SysScheduleServiceImpl implements ISysScheduleService {

    @Resource
    private SysScheduleMapper sysScheduleMapper;
    @Resource
    private SysScheduleLogMapper sysScheduleLogMapper;
    @Resource
    private ICoreScheduleService coreScheduleService;

    @Override
    public List<SysSchedule> getSysEnableList() {
        QueryWrapper<SysSchedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("sort", "id");
        return sysScheduleMapper.selectList(queryWrapper);
    }

    /**
     * 系统任务列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysScheduleListVo>
     */
    @Override
    public PageResult<SysScheduleListVo> list(PageParam pageParam, SysScheduleSearchParam searchParam) {
        // 分页参数
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        // 构造查询条件执行查询
        QueryWrapper<SysSchedule> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getStatus()) && !"all".equals(searchParam.getStatus())){
            if ("0".equals(searchParam.getStatus())){
                searchParam.setStatus("2");
            }
            queryWrapper.eq("status", searchParam.getStatus());
        }
        queryWrapper.eq(ObjectUtil.isNotEmpty(searchParam.getKey()), "`key`", searchParam.getKey());
        queryWrapper.orderByDesc(Arrays.asList("sort", "id"));
        IPage<SysSchedule> sysSchedulePage = sysScheduleMapper.selectPage(new Page<>(page, limit), queryWrapper);
        // 返回结果集
        List<SysScheduleListVo> sysScheduleListVoList = new LinkedList<>();
        for (SysSchedule sysSchedule : sysSchedulePage.getRecords()) {
            SysScheduleListVo sysScheduleListVo = new SysScheduleListVo();
            BeanUtils.copyProperties(sysSchedule, sysScheduleListVo);
            String jobKey = sysScheduleListVo.getKey();
            JobInfo jobInfo = JobProviderFactory.getJobInfo(jobKey);
            if (jobInfo != null) {
                sysScheduleListVo.setName(jobInfo.getName());
            }
            sysScheduleListVo.setTimeObject(sysSchedule.getTime());
            sysScheduleListVo.setCrontabContent(QuartzJobManager.convertCronContent(sysSchedule.getTime()));
            sysScheduleListVoList.add(sysScheduleListVo);
        }
        return PageResult.build(page, limit, sysSchedulePage.getTotal()).setData(sysScheduleListVoList);
    }

    /**
     * @param id
     * @return
     */
    private SysSchedule findByIdAndCheck(Integer id) {
        SysSchedule sysSchedule = sysScheduleMapper.selectById(id);
        Assert.notNull(sysSchedule, "系统任务不存在");
        return sysSchedule;
    }

    /**
     * 系统任务详情
     *
     * @param id 主键
     * @return SysScheduleInfoVo
     */
    @Override
    public SysScheduleInfoVo info(Integer id) {
        SysSchedule sysSchedule = sysScheduleMapper.selectById(id);
        SysScheduleInfoVo sysScheduleInfoVo = new SysScheduleInfoVo();
        BeanUtils.copyProperties(sysSchedule, sysScheduleInfoVo);
        return sysScheduleInfoVo;
    }

    @Override
    public void modifyStatus(SysScheduleStatusParam sysScheduleStatusParam) {
        SysSchedule oldSysSchedule = this.findByIdAndCheck(sysScheduleStatusParam.getId());
        if (sysScheduleStatusParam.getStatus() == 2) {
            QuartzJobManager.stopJob(oldSysSchedule);
        } else {
            QuartzJobManager.stopJob(oldSysSchedule);
            QuartzJobManager.startJob(oldSysSchedule);
        }
        SysSchedule newSysSchedule = new SysSchedule();
        newSysSchedule.setId(oldSysSchedule.getId());
        newSysSchedule.setStatus(sysScheduleStatusParam.getStatus());
        newSysSchedule.setUpdateTime(DateUtils.currTime());
        sysScheduleMapper.updateById(newSysSchedule);
    }

    /**
     * 系统任务添加
     *
     * @param sysScheduleParam 参数
     */
    @Override
    public void add(SysScheduleParam sysScheduleParam) {
        String jobKey = sysScheduleParam.getKey();
        JobInfo jobInfo = JobProviderFactory.getJobInfo(jobKey);
        if (jobInfo == null) {
            throw new RuntimeException("任务不存在.");
        }
        // 校验任务是否存在
        QueryWrapper<SysSchedule> queryWrapper = new QueryWrapper();
        queryWrapper.eq("`key`", jobKey);
        List<SysSchedule> sysScheduleList = sysScheduleMapper.selectList(queryWrapper);
        if (sysScheduleList != null && sysScheduleList.size() > 0) {
            throw new RuntimeException("任务已经存在.");
        }
        // 新增一个任务
        SysSchedule newSysSchedule = new SysSchedule();
        BeanUtils.copyProperties(sysScheduleParam, newSysSchedule);
        newSysSchedule.setTime(JSONUtil.toJsonStr(sysScheduleParam.getTime()));
        newSysSchedule.setCreateTime(DateUtils.currTime());
        newSysSchedule.setUpdateTime(DateUtils.currTime());
        newSysSchedule.setSiteId(RequestUtils.adminSiteId());
        newSysSchedule.setAddon(jobInfo.getSource());
        int result = sysScheduleMapper.insert(newSysSchedule);
        if (result > 0) {
            if (newSysSchedule.getStatus() == 1) {
                // 如果是启动状态，就启动任务
                QuartzJobManager.startJob(newSysSchedule);
            }
        }
    }

    /**
     * 系统任务编辑
     *
     * @param sysScheduleParam 参数
     */
    @Override
    public void edit(Integer id, SysScheduleParam sysScheduleParam) {
        // 存在性检查
        this.findByIdAndCheck(id);
        // 构建更新对象
        SysSchedule newSysSchedule = new SysSchedule();
        BeanUtils.copyProperties(sysScheduleParam, newSysSchedule);
        newSysSchedule.setId(id);
        newSysSchedule.setSiteId(RequestUtils.adminSiteId());
        newSysSchedule.setUpdateTime(DateUtils.currTime());
        newSysSchedule.setTime(JSONUtil.toJsonStr(sysScheduleParam.getTime()));
        int result = sysScheduleMapper.updateById(newSysSchedule);
        if (result <= 0) {
            log.error("更新系统任务失败：id={}", id);
        }
        // 根据状态启停任务
        if (sysScheduleParam.getStatus() == 2) {
            QuartzJobManager.stopJob(newSysSchedule.getKey());
        } else {
            QuartzJobManager.stopJob(newSysSchedule.getKey());
            QuartzJobManager.startJob(newSysSchedule);
        }
    }

    /**
     * 系统任务删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysSchedule sysSchedule = this.findByIdAndCheck(id);
        if (sysSchedule.getStatus() == 1) {
            QuartzJobManager.stopJob(sysSchedule);
        }
        sysScheduleMapper.deleteById(id);
    }

    @Override
    public List<SysScheduleTemplateVo> template() {
        List<SysScheduleTemplateVo> sysScheduleTemplateVoList = new ArrayList<>();
        Set<String> jobKeys = JobProviderFactory.jobKeys();
        for (String jobKey : jobKeys) {
            JobInfo jobInfo = JobProviderFactory.getJobInfo(jobKey);
            SysScheduleTemplateVo sysScheduleTemplateVo = new SysScheduleTemplateVo();
            sysScheduleTemplateVo.setKey(jobKey);
            sysScheduleTemplateVo.setName(jobInfo.getName());
            sysScheduleTemplateVo.setDesc(jobInfo.getDescribe());
            sysScheduleTemplateVo.setClazz(jobInfo.getJobClass());
            sysScheduleTemplateVo.setFunction(jobInfo.getJobClass());
            sysScheduleTemplateVoList.add(sysScheduleTemplateVo);
        }
        return sysScheduleTemplateVoList;
    }

    @Override
    public Map<String, String> type() {
        Map<String, String> typeData = new HashMap<>();
        typeData.put("cron", "定时任务");
        typeData.put("crond", "周期任务");
        return typeData;
    }

    @Override
    public Map<String, String> dateType() {
        Map<String, String> dateTypeData = new HashMap<>();
        dateTypeData.put("min", "每隔几分钟");
        dateTypeData.put("hour", "每隔几小时");
        dateTypeData.put("day", "每隔几天");
        dateTypeData.put("week", "每周");
        dateTypeData.put("month", "每月");
        return dateTypeData;
    }

    @Override
    public void resetSchedule() {
        coreScheduleService.resetSchedule();

    }

    @Override
    public PageResult<SysScheduleLogListVo> logList(PageParam pageParam, SysScheduleLogSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        LambdaQueryWrapper<SysScheduleLog> qw = new LambdaQueryWrapper<>();
        qw.eq(ObjectUtil.isNotEmpty(searchParam.getScheduleId()), SysScheduleLog::getScheduleId, searchParam.getScheduleId());
        qw.eq(ObjectUtil.isNotEmpty(searchParam.getKey()), SysScheduleLog::getKey, searchParam.getKey());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus()) && !"all".equals(searchParam.getStatus())){
            qw.eq(SysScheduleLog::getStatus, searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getExecuteTime())) {
            QueryMapperUtils.buildByTime(qw, SysScheduleLog::getExecuteTime, searchParam.getExecuteTime());
        }
        Page<SysScheduleLog> sysScheduleLogPage = sysScheduleLogMapper.selectPage(new Page<>(page, limit), qw);
        List<SysScheduleLogListVo> result = new ArrayList<>();
        sysScheduleLogPage.getRecords().forEach(sysScheduleLog -> {
            SysScheduleLogListVo sysScheduleLogListVo = new SysScheduleLogListVo();
            BeanUtils.copyProperties(sysScheduleLog, sysScheduleLogListVo);
            result.add(sysScheduleLogListVo);
        });
        return PageResult.build(page, limit, sysScheduleLogPage.getTotal()).setData(result);
    }

    @Override
    public void addLog(SysScheduleLog log) {
        SysSchedule sysSchedule = sysScheduleMapper.selectOne(new LambdaQueryWrapper<SysSchedule>()
                .eq(SysSchedule::getKey, log.getKey()).eq(SysSchedule::getAddon, log.getAddon()));
        if (sysSchedule == null) return;
        log.setScheduleId(sysSchedule.getId());
        log.setExecuteTime(DateUtils.currTime());
        sysScheduleLogMapper.insert(log);
    }

    @Override
    public void doSchedule(Integer id) {
        SysSchedule sysSchedule = sysScheduleMapper.selectById(id);
        if (sysSchedule == null) {
            throw new CommonException("计划任务不存在");
        }
        String jobKey = sysSchedule.getKey();
        Class<? extends IJobProvider> jobClass = JobProviderFactory.getJob(jobKey);
        if (jobClass == null) {
            throw new CommonException("未找到计划任务JobProvider: " + jobKey);
        }
        String className = jobClass.getName();
        try {
            IJobProvider job = jobClass.getDeclaredConstructor().newInstance();
            job.exec(null);

            String result = "计划任务：" + sysSchedule.getName() + "执行成功";
            addLog(SysScheduleLog.builder()
                    .key(jobKey)
                    .name(sysSchedule.getName())
                    .addon(sysSchedule.getAddon())
                    .className(className)
                    .status("success")
                    .executeResult(result)
                    .job(className)
                    .build());

        } catch (Exception e) {
            String errorMsg = "计划任务：" + sysSchedule.getName() + "发生错误, 错误原因：" + e.getMessage() +
                    " at " + e.getStackTrace()[0].toString();
            addLog(SysScheduleLog.builder()
                    .key(jobKey)
                    .name(sysSchedule.getName())
                    .addon(sysSchedule.getAddon())
                    .className(className)
                    .status("error")
                    .executeResult(errorMsg)
                    .job(className)
                    .build());

            throw new CommonException("执行计划任务失败", e);
        }
    }

    @Override
    public void delLog(Object ids) {
        Integer[] logIds = null;
        if (ids instanceof Integer) {
            logIds = new Integer[]{(Integer) ids};
        } else if (ids instanceof ArrayList) {
            logIds = ((ArrayList<?>)ids).toArray(new Integer[0]);
        }
        sysScheduleLogMapper.deleteByIds(Arrays.asList(logIds));
    }

    @Override
    public void clearLog(Integer scheduleId) {
        sysScheduleLogMapper.delete(new QueryWrapper<SysScheduleLog>().eq("schedule_id", scheduleId));
    }
}
