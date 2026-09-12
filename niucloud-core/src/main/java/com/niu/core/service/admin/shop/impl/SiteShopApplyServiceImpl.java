package com.niu.core.service.admin.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.site.SiteApply;
import com.niu.core.event.shop.SiteShopApplyVerifyAfterEvent;
import com.niu.core.mapper.site.SiteApplyMapper;
import com.niu.core.service.admin.shop.ISiteShopApplyService;
import com.niu.core.service.admin.shop.param.SiteShopApplyParam;
import com.niu.core.service.admin.shop.param.SiteShopApplySearchParam;
import com.niu.core.service.admin.shop.param.SiteShopApplyVerifyParam;
import com.niu.core.service.admin.shop.vo.SiteShopApplyInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopApplyListVo;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsInfoVo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商户入驻申请实现 (Admin端)
 * v120迁移：基于 site_apply 表
 */
@Service
public class SiteShopApplyServiceImpl implements ISiteShopApplyService, ApplicationContextAware {

    @Resource
    private SiteApplyMapper siteApplyMapper;

    private ApplicationContext applicationContext;

    @Resource
    private ICoreDiyFormRecordsService coreDiyFormRecordsService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public PageResult<SiteShopApplyListVo> page(PageParam pageParam, SiteShopApplySearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<SiteApply> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("nm.nickname, nssa.apply_id, nssa.site_id, nssa.member_id, nssa.site_name, nssa.user_name, nssa.user_mobile, nssa.category_id, nssa.child_category_id, nssa.business_license, nssa.status, nssa.create_time, nssa.update_time, nssa.audit_time, nssa.open_status, nssc.name as category_name, nsscc.name as child_category_name, nssa.account, nssa.password, nssa.uid")
                .setAlias("nssa")
                .leftJoin("?_site_category nssc ON nssa.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nsscc ON nssa.child_category_id = nsscc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_member nm ON nssa.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix));

        // 处理区域代理业务 - circle_id过滤
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

        if (ObjectUtil.isNotEmpty(searchParam.getStatus()) && !"all".equals(String.valueOf(searchParam.getStatus()))) {
            queryWrapper.eq("nssa.status", searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId()) && !"all".equals(String.valueOf(searchParam.getCategoryId()))) {
            queryWrapper.eq("nssa.category_id", searchParam.getCategoryId());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "nssa.create_time", searchParam.getCreateTime());
        }

        queryWrapper.orderByDesc("nssa.create_time");

        IPage<SiteShopApplyListVo> iPage = siteApplyMapper.selectJoinPage(new Page<>(page, limit), SiteShopApplyListVo.class, queryWrapper);

        return PageResult.build(iPage);
    }

    @Override
    public SiteShopApplyInfoVo info(Integer id) {

        MPJQueryWrapper<SiteApply> queryWrapper = new MPJQueryWrapper<>();

        //sql语句 - 与PHP保持一致的查询字段
        queryWrapper.select("nm.nickname, nu.username, nssa.apply_id, nssa.member_id, nssa.site_name, nssa.user_name, nssa.user_mobile, nssa.category_id, nssa.child_category_id, nssa.business_license, nssa.status, nssa.create_time, nssa.update_time, nssa.audit_time, nssa.open_status, nssa.refuse_reason, nssa.uid, nssa.apply_type, nssa.circle_id, nssa.form_record_id, nssc.name as category_name, nsscc.name as child_category_name")
                .setAlias("nssa")
                .leftJoin("?_site_category nssc ON nssa.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nsscc ON nssa.child_category_id = nsscc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_member nm ON nssa.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_sys_user nu ON nssa.uid = nu.uid".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.eq("nssa.apply_id", id);

        // 处理区域代理业务 - circle_id过滤
        List<Integer> circleIds = RequestUtils.circleIds();
        if (ObjectUtil.isNotEmpty(circleIds)) {
            queryWrapper.in("nssa.circle_id", circleIds);
        }

        SiteShopApplyInfoVo vo = siteApplyMapper.selectJoinOne(SiteShopApplyInfoVo.class, queryWrapper);
        
        // 查询自定义表单信息
        if (vo != null) {
            if (vo.getFormRecordId() != null && vo.getFormRecordId() > 0) {
                try {
                    // 调用表单记录服务获取表单详情
                    DiyFormRecordsInfoVo formInfo = coreDiyFormRecordsService.info(vo.getFormRecordId());
                   Map<String, Object> formMap = new HashMap<>();
                   if(formInfo != null) {
                    // 组装表单信息，供前端渲染使用
                    formMap.put("record_id",formInfo.getRecordId());
                    formMap.put("form_id",formInfo.getFormId());
                    // 将 value 转换为对象格式（以数字为键），与 PHP 格式保持一致
                    if(formInfo.getValue() != null) {
                        try {
                            JSONArray jsonArray = JSONUtil.parseArray(formInfo.getValue().toString());
                            Map<String, Object> valueMap = new HashMap<>();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                valueMap.put(String.valueOf(i), jsonArray.get(i));
                            }
                            formMap.put("value", valueMap);
                        } catch (Exception e) {
                            formMap.put("value", formInfo.getValue());
                        }
                    }
                    // 使用 recordsFieldList（与前端保持一致）
                    formMap.put("recordsFieldList",formInfo.getRecordsFieldList());
                }
                    vo.setFormInfo(formMap);
                } catch (Exception e) {
                    // 忽略获取表单信息时的异常，返回空对象
                    vo.setFormInfo(new HashMap<>());
                }
            } else {
                vo.setFormInfo(new HashMap<>());
            }
        }
        
        return vo;
    }

    @Override
    public void verify(Integer id, SiteShopApplyVerifyParam verifyParam) {
        SiteApply model = siteApplyMapper.selectOne(
                new QueryWrapper<SiteApply>()
                        .eq("apply_id", id));
        if (model == null) {
            Assert.notNull(model, "商户入驻申请不存在");
        } else {
            model.setStatus(verifyParam.getStatus());
            model.setRefuseReason(verifyParam.getRefuseReason());
            if (verifyParam.getStatus().equals(2)) {
                model.setAuditTime(System.currentTimeMillis() / 1000);
            }
            model.setUpdateTime(System.currentTimeMillis() / 1000);
            siteApplyMapper.updateById(model);

            // 发布审核后事件，记录操作日志
            SiteShopApplyVerifyAfterEvent event = new SiteShopApplyVerifyAfterEvent(
                    this,
                    "mall_shop_verify",
                    "system",
                    RequestUtils.uid(),
                    model,
                    verifyParam.getStatus(),
                    verifyParam.getRefuseReason()
            );
            applicationContext.publishEvent(event);
        }
    }

    @Override
    public void add(SiteShopApplyParam addParam) {

        SiteApply model = new SiteApply();

        model.setAuditTime(addParam.getAuditTime());
        model.setBusinessLicense(addParam.getBusinessLicense());
        model.setCategoryId(addParam.getCategoryId());
        model.setChildCategoryId(addParam.getChildCategoryId());
        model.setMemberId(addParam.getMemberId());
        model.setOpenStatus(addParam.getOpenStatus());
        model.setRefuseReason(addParam.getRefuseReason());
        model.setSiteName(addParam.getSiteName());
        model.setStatus(addParam.getStatus());
        model.setUserMobile(addParam.getUserMobile());
        model.setUserName(addParam.getUserName());

        model.setSiteId(RequestUtils.siteId());

        siteApplyMapper.insert(model);
    }

    @Override
    public void edit(Integer id, SiteShopApplyParam editParam) {

        SiteApply model = siteApplyMapper.selectOne(
                new QueryWrapper<SiteApply>()
                        .eq("apply_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商户入驻申请不存在");
        model.setAuditTime(editParam.getAuditTime());
        model.setBusinessLicense(editParam.getBusinessLicense());
        model.setCategoryId(editParam.getCategoryId());
        model.setChildCategoryId(editParam.getChildCategoryId());
        model.setMemberId(editParam.getMemberId());
        model.setOpenStatus(editParam.getOpenStatus());
        model.setRefuseReason(editParam.getRefuseReason());
        model.setSiteName(editParam.getSiteName());
        model.setStatus(editParam.getStatus());
        model.setUserMobile(editParam.getUserMobile());
        model.setUserName(editParam.getUserName());
        siteApplyMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        siteApplyMapper.delete(new QueryWrapper<SiteApply>().eq("apply_id", id).eq("site_id", RequestUtils.siteId()));
    }
}
