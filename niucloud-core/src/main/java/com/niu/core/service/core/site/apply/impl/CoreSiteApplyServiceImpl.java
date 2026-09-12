package com.niu.core.service.core.site.apply.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.site.SiteApply;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.shop.ShopApplyEnum;
import com.niu.core.event.AddSiteApplyEvent;
import com.niu.core.event.EditSiteApplyEvent;
import com.niu.core.mapper.site.SiteApplyMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.shop.param.SiteApplyParam;
import com.niu.core.service.api.shop.param.SiteApplySearchParam;
import com.niu.core.service.api.shop.param.SiteShopApplyVerifyParam;
import com.niu.core.service.api.shop.vo.SiteApplyInfoVo;
import com.niu.core.service.api.shop.vo.SiteApplyListVo;
import com.niu.core.service.core.site.apply.ICoreSiteApplyService;
import com.niu.core.service.core.site.apply.vo.AccountCheckVo;
import com.niu.core.service.core.user.ICoreUserService;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 商户入驻申请服务实现
 * v120迁移：基于 site_apply 表
 */
@Service
public class CoreSiteApplyServiceImpl implements ICoreSiteApplyService {

    @Resource
    private SiteApplyMapper siteApplyMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private IRegisterService registerService;
    @Resource
    private ICoreUserService coreUserService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public PageResult<SiteApplyListVo> page(PageParam pageParam, SiteApplySearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        MPJQueryWrapper<SiteApply> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("nm.nickname, nssa.apply_id, nssa.site_id, nssa.member_id, nssa.site_name, nssa.user_name, nssa.user_mobile, nssa.category_id, nssa.child_category_id, nssa.business_license, nssa.status, nssa.create_time, nssa.update_time, nssa.audit_time, nssa.open_status, nssc.name as category_name, nsscc.name as child_category_name")
                .setAlias("nssa")
                .leftJoin("?_member nm ON nssa.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nssc ON nssa.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nsscc ON nssa.child_category_id = nsscc.category_id".replace("?_", GlobalConfig.tablePrefix));

        // 区域代理过滤
        List<Integer> circleIds = RequestUtils.circleIds();
        if (ObjectUtil.isNotEmpty(circleIds)) {
            queryWrapper.in("nssa.circle_id", circleIds);
        }

        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.and(i -> i.or(j -> j.like("nssa.site_name", searchParam.getKeywords()))
                    .or(j -> j.like("nm.username", searchParam.getKeywords()))
                    .or(j -> j.like("nm.mobile", searchParam.getKeywords()))
            );
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("nssa.status", searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId())) {
            queryWrapper.eq("nssa.category_id", searchParam.getCategoryId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getMemberId())) {
            queryWrapper.eq("nssa.member_id", searchParam.getMemberId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "nssa.create_time", searchParam.getCreateTime());
        }
        queryWrapper.orderByDesc("nssa.create_time");

        IPage<SiteApplyListVo> iPage = siteApplyMapper.selectJoinPage(new Page<>(page, limit), SiteApplyListVo.class, queryWrapper);
        return PageResult.build(iPage);
    }

    @Override
    public SiteApplyInfoVo info(Integer id) {
        MPJQueryWrapper<SiteApply> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("nm.nickname, nssa.*, nssc.name as category_name, nsscc.name as child_category_name")
                .setAlias("nssa")
                .leftJoin("?_member nm ON nssa.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nssc ON nssa.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nsscc ON nssa.child_category_id = nsscc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("nssa.apply_id", id);

        return siteApplyMapper.selectJoinOne(SiteApplyInfoVo.class, queryWrapper);
    }

    @Override
    public void verify(Integer id, SiteShopApplyVerifyParam verifyParam) {
        SiteApply model = siteApplyMapper.selectOne(
                new QueryWrapper<SiteApply>()
                        .eq("apply_id", id));
        Assert.notNull(model, "商户入驻申请不存在");

        model.setStatus(verifyParam.getStatus());
        model.setRefuseReason(verifyParam.getRefuseReason());
        if (verifyParam.getStatus().equals(2)) {
            model.setAuditTime(System.currentTimeMillis() / 1000);
        }
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        siteApplyMapper.updateById(model);
    }

    @Override
    public Long add(SiteApplyParam addParam) {
        String applyType = addParam.getApplyType();

        // 校验手机验证码并创建账号
        checkMobileAndCreateAccount(addParam);

        SiteApply model = new SiteApply();
        model.setSiteName(addParam.getSiteName());
        model.setCategoryId(addParam.getCategoryId());
        model.setChildCategoryId(addParam.getChildCategoryId());
        model.setCircleId(addParam.getCircleId());
        model.setBusinessLicense(addParam.getBusinessLicense());
        model.setStatus(addParam.getStatus());
        model.setUid(addParam.getUid());
        model.setMemberId(addParam.getMemberId());
        model.setCreateTime(DateUtils.currTime());
        model.setApplyType(applyType);
        model.setAddon(addParam.getAddon());

        if ("member".equals(applyType)) {
            model.setUserName(addParam.getUserName());
            model.setUserMobile(addParam.getUserMobile());
        }

        siteApplyMapper.insert(model);

        // 发布申请添加事件（携带 form_data，由监听器保存万能表单）
        applicationEventPublisher.publishEvent(new AddSiteApplyEvent(this, model, addParam.getFormData()));

        return model.getApplyId().longValue();
    }

    @Override
    public void edit(Integer id, SiteApplyParam editParam) {
        // 校验手机验证码并创建账号
        checkMobileAndCreateAccount(editParam);

        SiteApply model = siteApplyMapper.selectOne(
                new QueryWrapper<SiteApply>()
                        .eq("apply_id", id));
        Assert.notNull(model, "商户入驻申请不存在");

        model.setSiteName(editParam.getSiteName());
        model.setUserName(editParam.getUserName());
        model.setUserMobile(editParam.getUserMobile());
        model.setCategoryId(editParam.getCategoryId());
        model.setChildCategoryId(editParam.getChildCategoryId());
        model.setCircleId(editParam.getCircleId());
        model.setBusinessLicense(editParam.getBusinessLicense());
        model.setStatus(editParam.getStatus());
        model.setUid(editParam.getUid());
        model.setRefuseReason("");
        model.setUpdateTime(DateUtils.currTime());

        siteApplyMapper.updateById(model);

        // 发布申请编辑事件（携带 form_data，由监听器更新万能表单）
        applicationEventPublisher.publishEvent(new EditSiteApplyEvent(this, model, editParam.getFormData()));
    }

    @Override
    public void del(Integer id) {
        siteApplyMapper.delete(new QueryWrapper<SiteApply>().eq("apply_id", id).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public Map<String, Object> checkAccount(String account) {
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq("username", account).select("uid", "username", "head_img", "mobile");
        SysUser sysUser = sysUserMapper.selectOne(qw);

        Map<String, Object> info = new HashMap<>();
        if (sysUser != null) {
            info.put("uid", sysUser.getUid());
            info.put("username", sysUser.getUsername());
            info.put("head_img", sysUser.getHeadImg());

            String mobile = sysUser.getMobile();
            if (mobile != null && mobile.matches("^1\\d{10}$")) {
                mobile = mobile.substring(0, 3) + "****" + mobile.substring(7);
            }
            info.put("mobile", mobile);
        }
        return info;
    }

    @Override
    public List<Map<String, Object>> statusList() {
        Map<Integer, String> stringMap = ShopApplyEnum.getStatus();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : stringMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("label", entry.getValue());
            map.put("value", entry.getKey());
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public Boolean applyCheck() {
        QueryWrapper<SiteApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.in("status", Arrays.asList(ShopApplyEnum.WAIT_AUDIT.getValue(), ShopApplyEnum.PASS.getValue()));
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 1");
        SiteApply siteApply = siteApplyMapper.selectOne(queryWrapper);
        return siteApply != null;
    }

    @Override
    public AccountCheckVo accountCheck(String account) {
        AccountCheckVo vo = new AccountCheckVo();

        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, account)
                        .select(SysUser::getUid, SysUser::getUsername, SysUser::getHeadImg, SysUser::getMobile)
        );

        if (user != null) {
            vo.setUid(user.getUid());
            vo.setUsername(user.getUsername());
            vo.setHeadImg(user.getHeadImg());

            String mobile = user.getMobile();
            if (StringUtils.hasText(mobile) && mobile.matches("^1\\d{10}$")) {
                // 脱敏：保留前3位和后4位，中间4位用 **** 代替
                mobile = mobile.substring(0, 3) + "****" + mobile.substring(7);
            }
            vo.setMobile(mobile);
        }

        return vo;
    }

    /**
     * 校验手机号是否存在账号，不存在则创建账号
     */
    private void checkMobileAndCreateAccount(SiteApplyParam data) {
        // 创建用户并返回uid
        Integer uid = coreUserService.createUserByMobile(data.getUserMobile());
        data.setUid(uid);
    }
}
