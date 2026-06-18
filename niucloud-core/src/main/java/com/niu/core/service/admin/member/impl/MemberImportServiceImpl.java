package com.niu.core.service.admin.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.MemberImport;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.member.MemberImportStatusEnum;
import com.niu.core.mapper.member.MemberImportMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.service.admin.member.IMemberImportService;
import com.niu.core.service.admin.member.param.MemberImportAddParam;
import com.niu.core.service.admin.member.param.MemberImportSearchParam;
import com.niu.core.service.admin.member.vo.MemberImportListVo;
import com.niu.core.service.core.member.ICoreMemberImportService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemberImportServiceImpl implements IMemberImportService {

    @Resource
    private MemberImportMapper memberImportMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private ICoreMemberImportService coreMemberImportService;

    @Override
    public PageResult<MemberImportListVo> getPage(PageParam pageParam, MemberImportSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        Integer siteId = RequestUtils.defaultSiteId();

        QueryWrapper<MemberImport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);

        if (ObjectUtil.isNotNull(searchParam.getStatus())) {
            queryWrapper.eq("status", searchParam.getStatus());
        }

        if (StrUtil.isNotEmpty(searchParam.getStartTime())) {
            queryWrapper.ge("create_time", searchParam.getStartTime());
        }

        if (StrUtil.isNotEmpty(searchParam.getEndTime())) {
            queryWrapper.le("create_time", searchParam.getEndTime());
        }

        queryWrapper.orderByDesc("create_time");

        IPage<MemberImport> iPage = memberImportMapper.selectPage(new Page<>(page, limit), queryWrapper);

        List<MemberImportListVo> list = new LinkedList<>();
        List<Integer> userIds = iPage.getRecords().stream()
                .map(MemberImport::getMainId)
                .filter(ObjectUtil::isNotNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, SysUser> userMap = new java.util.HashMap<>();
        if (!userIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(SysUser::getUid, u -> u));
        }

        for (MemberImport item : iPage.getRecords()) {
            MemberImportListVo vo = new MemberImportListVo();
            BeanUtils.copyProperties(item, vo);

            if (ObjectUtil.isNotNull(item.getStatus())) {
                vo.setStatusName(MemberImportStatusEnum.getNameByValue(item.getStatus()));
            }

            if (ObjectUtil.isNotNull(item.getMainId())) {
                SysUser user = userMap.get(item.getMainId());
                if (user != null) {
                    MemberImportListVo.SysUserVo userVo = new MemberImportListVo.SysUserVo();
                    userVo.setUid(user.getUid());
                    userVo.setUsername(user.getUsername());
                    userVo.setHeadImg(user.getHeadImg());
                    userVo.setRealName(user.getRealName());
                    vo.setUser(userVo);
                }
            }

            list.add(vo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer memberImport(MemberImportAddParam.MemberImportData addParam) {
        Integer siteId = RequestUtils.defaultSiteId();
        Integer mainId = RequestUtils.uid();
        addParam.getMyMemberLabel().put("site_id", siteId);
        addParam.getMyMemberLabel().put("main_id", mainId);

        return coreMemberImportService.add(addParam);
    }
}
