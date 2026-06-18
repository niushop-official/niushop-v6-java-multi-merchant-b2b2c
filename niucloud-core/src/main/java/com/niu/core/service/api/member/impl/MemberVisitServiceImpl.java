package com.niu.core.service.api.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.MemberVisit;
import com.niu.core.mapper.member.MemberVisitMapper;
import com.niu.core.service.api.member.IMemberVisitService;
import com.niu.core.service.api.member.param.MemberVisitParam;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Service
public class MemberVisitServiceImpl implements IMemberVisitService {
    @Resource
    private MemberVisitMapper memberVisitMapper;

    @Resource
    private ICoreSysStatService sysStatService;

    @Override
    public void addVisit(MemberVisitParam param) {
        // 未登录用户直接返回
        Integer memberId = RequestUtils.memberId(); // 获取当前登录会员ID，需自行实现
        if (memberId == null || memberId == 0) {
            return;
        }

        Integer siteId = param.getSiteId() != null ? param.getSiteId() : 0;

        // 获取今天的开始和结束时间戳（秒）
        LocalDate today = LocalDate.now();
        long startTime = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        long endTime = today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toEpochSecond();

        // 查询今天是否已有该会员该站点的访问记录
        LambdaQueryWrapper<MemberVisit> wrapper = new LambdaQueryWrapper<MemberVisit>()
                .eq(MemberVisit::getSiteId, siteId)
                .eq(MemberVisit::getMemberId, memberId)
                .between(MemberVisit::getVisitTime, startTime, endTime);
        Long existVisit = memberVisitMapper.selectCount(wrapper);
        boolean isFirstVisitToday = (existVisit <= 0);

        // 准备统计增量数据
        SysDataAddParam statParam = new SysDataAddParam();
        statParam.setSiteId(siteId);
        statParam.setAccessSum(1);
        if (isFirstVisitToday) statParam.setVisitNum(1);

        // 插入访问明细记录
        MemberVisit newVisit = new MemberVisit();
        newVisit.setMemberId(memberId);
        newVisit.setSiteId(siteId);
        newVisit.setVisitTime(System.currentTimeMillis() / 1000);
        memberVisitMapper.insert(newVisit);

        // 更新统计
        sysStatService.addStat(statParam);
    }
}
