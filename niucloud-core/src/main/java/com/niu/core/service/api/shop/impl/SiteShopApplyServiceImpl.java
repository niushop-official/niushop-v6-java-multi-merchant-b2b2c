package com.niu.core.service.api.shop.impl;

import cn.hutool.core.util.ObjectUtil;
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
import com.niu.core.entity.shop.SiteShopApply;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.shop.ShopApplyEnum;
import com.niu.core.mapper.shop.SiteShopApplyMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.service.api.shop.ISiteShopApplyService;
import com.niu.core.service.api.shop.param.SiteShopApplyParam;
import com.niu.core.service.api.shop.param.SiteShopApplySearchParam;
import com.niu.core.service.api.shop.param.SiteShopApplyVerifyParam;
import com.niu.core.service.api.shop.vo.SiteShopApplyInfoVo;
import com.niu.core.service.api.shop.vo.SiteShopApplyListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商户入驻申请实现
 */
@Service
public class SiteShopApplyServiceImpl implements ISiteShopApplyService {
        
    @Resource
    SiteShopApplyMapper siteShopApplyMapper;
    @Resource
    private SysUserMapper  sysUserMapper;

    @Override
    public PageResult<SiteShopApplyListVo> page(PageParam pageParam, SiteShopApplySearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();
        MPJQueryWrapper<SiteShopApply> queryWrapper = new MPJQueryWrapper<>();
        //sql语句
        queryWrapper.select("nm.nickname, nsg.group_name, nssa.apply_id,nssa.site_id," +
                        " nssa.member_id, nssa.site_name, nssa.user_name, nssa.user_mobile, nssa.category_id," +
                        " nssa.group_id, nssa.business_license, nssa.status, nssa.create_time, " +
                        "nssa.update_time, nssa.audit_time, nssa.refuse_reason, nssa.open_status, nssc.category_name")
                .setAlias("nssa")
                .leftJoin("?_site_group nsg ON nssa.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop_category nssc ON nssa.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_member nm ON nssa.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix));

        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.and(i -> i.or(j -> j.like("nssa.site_name", searchParam.getKeywords()))
                    .or(j-> j.like("nm.username", searchParam.getKeywords()))
                    .or(j-> j.like("nm.mobile", searchParam.getKeywords()))
            );
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("nssa.status", searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId())) {
            queryWrapper.eq("nssa.category_id", searchParam.getCategoryId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGroupId())) {
            queryWrapper.eq("nssa.group_id", searchParam.getGroupId());
        }
        queryWrapper.eq("nm.member_id", RequestUtils.memberId());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "nssa.create_time", searchParam.getCreateTime());
        }
        queryWrapper.orderByDesc("nssa.create_time");

        IPage<SiteShopApplyListVo> iPage = siteShopApplyMapper.selectJoinPage(new Page<>(page, limit),SiteShopApplyListVo.class, queryWrapper);

        return PageResult.build(iPage);
    }

    @Override
    public SiteShopApplyInfoVo info(Integer id) {

        MPJQueryWrapper<SiteShopApply> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("nm.nickname, nsg.group_name, nssa.apply_id, nssa.site_id, nssa.member_id, nssa.site_name, nssa.user_name, nssa.user_mobile, nssa.category_id, nssa.group_id, nssa.business_license, nssa.status, nssa.create_time, nssa.update_time, nssa.audit_time, nssa.refuse_reason, nssa.open_status, nssa.account, nssa.password, nssc.category_name")
                .setAlias("nssa")
                .leftJoin("?_site_group nsg ON nssa.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop_category nssc ON nssa.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_member nm ON nssa.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.eq("nssa.apply_id", id);

        return siteShopApplyMapper.selectJoinOne(SiteShopApplyInfoVo.class, queryWrapper);

    }

    @Override
    public void verify(Integer id, SiteShopApplyVerifyParam verifyParam) {
        SiteShopApply model = siteShopApplyMapper.selectOne(
                new QueryWrapper<SiteShopApply>()
                        .eq("apply_id", id));

        Assert.notNull(model, "商户入驻申请不存在");
        model.setStatus(verifyParam.getStatus());
        model.setRefuseReason(verifyParam.getRefuseReason());
        if (verifyParam.getStatus().equals(2)) {
            model.setAuditTime(System.currentTimeMillis() / 1000);
        }
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        siteShopApplyMapper.updateById(model);

    }

    @Override
    public void add(SiteShopApplyParam addParam) {

        SiteShopApply model = new SiteShopApply();

        model.setAuditTime(addParam.getAuditTime());
        model.setBusinessLicense(addParam.getBusinessLicense());
        model.setCategoryId(addParam.getCategoryId());
        model.setGroupId(addParam.getGroupId());
        model.setMemberId(addParam.getMemberId());
        model.setOpenStatus(addParam.getOpenStatus());
        model.setRefuseReason(addParam.getRefuseReason());
        model.setSiteName(addParam.getSiteName());
        model.setStatus(addParam.getStatus());
        model.setUserMobile(addParam.getUserMobile());
        model.setUserName(addParam.getUserName());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setSiteId(RequestUtils.siteId());

        siteShopApplyMapper.insert(model);
    }

    @Override
    public void edit(Integer id, SiteShopApplyParam editParam) {
        // 检查是否有待审核或已通过的申请（同一会员）
        QueryWrapper<SiteShopApply> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("member_id", RequestUtils.memberId())
                .in("status", Arrays.asList(ShopApplyEnum.WAIT_AUDIT.getStatus(), ShopApplyEnum.PASS.getStatus()));
        SiteShopApply existingApply = siteShopApplyMapper.selectOne(checkWrapper);
        if (ObjectUtil.isNotEmpty(existingApply)) {
            throw new RuntimeException("SHOP_APPLY_PASS_OR_WAIT_AUDIT");
        }

        // 检查申请是否存在
        SiteShopApply model = siteShopApplyMapper.selectOne(
                new QueryWrapper<SiteShopApply>()
                        .eq("apply_id", id));

        Assert.notNull(model, "SHOP_APPLY_NOT_EXIT");

        // 更新字段
        model.setSiteName(editParam.getSiteName());
        model.setUserName(editParam.getUserName());
        model.setUserMobile(editParam.getUserMobile());
        model.setCategoryId(editParam.getCategoryId());
        model.setGroupId(editParam.getGroupId());
        model.setBusinessLicense(editParam.getBusinessLicense());
        model.setStatus(editParam.getStatus() != null ? editParam.getStatus() : 1);
        model.setUpdateTime(DateUtils.currTime());
        model.setRefuseReason(""); // 清空拒绝原因

        // 如果有 uid，则不设置 account 和 password
        if (ObjectUtil.isNotEmpty(editParam.getUid())) {
            model.setUid(editParam.getUid());
        } else {
            model.setAccount(editParam.getAccount());
            model.setPassword(editParam.getPassword());
        }

        siteShopApplyMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        siteShopApplyMapper.delete(new QueryWrapper<SiteShopApply>().eq("apply_id", id).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public Integer shopApply(SiteShopApplyParam addParam) {

        QueryWrapper<SiteShopApply> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("member_id", RequestUtils.memberId())
                .in("status", Arrays.asList(ShopApplyEnum.WAIT_AUDIT.getStatus(), ShopApplyEnum.PASS.getStatus()));
        SiteShopApply existingApply = siteShopApplyMapper.selectOne(checkWrapper);
        if (ObjectUtil.isNotEmpty(existingApply)) {
            throw new RuntimeException ("SHOP_APPLY_PASS_OR_WAIT_AUDIT");
        }

        SiteShopApply model = new SiteShopApply();
        model.setBusinessLicense(addParam.getBusinessLicense());
        model.setCategoryId(addParam.getCategoryId());
        model.setGroupId(addParam.getGroupId());
        model.setSiteName(addParam.getSiteName());
        model.setUserMobile(addParam.getUserMobile());
        model.setUserName(addParam.getUserName());
        model.setMemberId(RequestUtils.memberId());
        model.setCreateTime(DateUtils.currTime());
        model.setAccount(addParam.getAccount());
        model.setPassword(addParam.getPassword());
        model.setUpdateTime(DateUtils.currTime());
        //待审核
        model.setStatus(1);
        if (ObjectUtil.isNotEmpty(addParam.getUid())) {
            model.setUid(addParam.getUid());
        } else {
            model.setAccount(addParam.getAccount());
            model.setPassword(addParam.getPassword());
        }
        
        siteShopApplyMapper.insert(model);
        return model.getApplyId();
    }

    @Override
    public List<Map<String, Object>> statusList() {
        //店铺申请状态
        Map<Integer, String> stringMap = ShopApplyEnum.getStatus();
        List<Map<String, Object>> mapList = ShopApplyEnum.convertToLabelValueList(stringMap);
        return mapList;
    }

    @Override
    public Boolean applyCheck() {
        QueryWrapper<SiteShopApply> siteShopApplyQueryWrapper = new QueryWrapper<>();
        siteShopApplyQueryWrapper.eq("member_id", RequestUtils.memberId());
        siteShopApplyQueryWrapper.in("status", 1, 2);
        SiteShopApply siteShopApply = siteShopApplyMapper.selectOne(siteShopApplyQueryWrapper);
        if (siteShopApply != null){
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> checkAccount(String account) {
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq("username", account).select("uid","username","head_img","mobile");
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


}
