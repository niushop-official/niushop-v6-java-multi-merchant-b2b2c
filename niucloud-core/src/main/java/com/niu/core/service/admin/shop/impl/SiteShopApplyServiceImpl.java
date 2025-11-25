package com.niu.core.service.admin.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.shop.SiteShopApply;
import com.niu.core.mapper.shop.SiteShopApplyMapper;
import com.niu.core.service.admin.shop.ISiteShopApplyService;
import com.niu.core.service.admin.shop.param.SiteShopApplyParam;
import com.niu.core.service.admin.shop.param.SiteShopApplySearchParam;
import com.niu.core.service.admin.shop.param.SiteShopApplyVerifyParam;
import com.niu.core.service.admin.shop.vo.SiteShopApplyInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopApplyListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.util.LinkedList;
import java.util.List;


/**
 * 商户入驻申请实现
 */
@Service
public class SiteShopApplyServiceImpl implements ISiteShopApplyService {
        
    @Resource
    SiteShopApplyMapper siteShopApplyMapper;


    @Override
    public PageResult<SiteShopApplyListVo> page(PageParam pageParam, SiteShopApplySearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();


        MPJQueryWrapper<SiteShopApply> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("nm.nickname, nsg.group_name, nssa.apply_id, nssa.site_id, nssa.member_id, nssa.site_name, nssa.user_name, nssa.user_mobile, nssa.category_id, nssa.group_id, nssa.business_license, nssa.status, nssa.create_time, nssa.update_time, nssa.audit_time, nssa.open_status, nssc.category_name, nssa.account, nssa.password, nssa.uid")
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
        queryWrapper.select("nm.nickname, nsg.group_name, nssa.apply_id, nssa.member_id, nssa.site_name, nssa.user_name, nssa.user_mobile, nssa.category_id, nssa.group_id, nssa.business_license, nssa.status, nssa.create_time, nssa.update_time, nssa.audit_time, nssa.refuse_reason, nssa.open_status, nssc.category_name, nssa.account, nssa.password")
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
        if (model ==null) {
            Assert.notNull(model, "商户入驻申请不存在");
        }else {
            model.setStatus(verifyParam.getStatus());
            model.setRefuseReason(verifyParam.getRefuseReason());
            if (verifyParam.getStatus().equals(2)) {
                model.setAuditTime(System.currentTimeMillis() / 1000);
            }
            model.setUpdateTime(System.currentTimeMillis() / 1000);
            siteShopApplyMapper.updateById(model);
        }
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

        model.setSiteId(RequestUtils.siteId());

        siteShopApplyMapper.insert(model);
    }

    @Override
    public void edit(Integer id, SiteShopApplyParam editParam) {

        SiteShopApply model = siteShopApplyMapper.selectOne(
                new QueryWrapper<SiteShopApply>()
                        .eq("apply_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商户入驻申请不存在");
        model.setAuditTime(editParam.getAuditTime());
        model.setBusinessLicense(editParam.getBusinessLicense());
        model.setCategoryId(editParam.getCategoryId());
        model.setGroupId(editParam.getGroupId());
        model.setMemberId(editParam.getMemberId());
        model.setOpenStatus(editParam.getOpenStatus());
        model.setRefuseReason(editParam.getRefuseReason());
        model.setSiteName(editParam.getSiteName());
        model.setStatus(editParam.getStatus());
        model.setUserMobile(editParam.getUserMobile());
        model.setUserName(editParam.getUserName());
        siteShopApplyMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        siteShopApplyMapper.delete(new QueryWrapper<SiteShopApply>().eq("apply_id", id).eq("site_id", RequestUtils.siteId()));
    }

}
