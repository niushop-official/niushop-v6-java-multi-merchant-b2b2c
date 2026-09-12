package com.niu.mall.service.admin.site_shop.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.site.SiteApply;
import com.niu.core.enums.shop.ShopApplyEnum;
import com.niu.core.event.shop.SiteShopApplyVerifyAfterEvent;
import com.niu.core.mapper.site.SiteApplyMapper;
import com.niu.core.service.admin.diy_form.IDiyFormService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.service.admin.site_shop.IAdminShopApplyService;
import com.niu.mall.service.admin.site_shop.param.AdminShopApplyPageQueryParam;
import com.niu.mall.service.admin.site_shop.param.AdminShopApplyVerifyParam;
import com.niu.mall.service.admin.site_shop.vo.AdminShopApplyInfoVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopApplyPageVo;
import com.niu.mall.service.core.shop.ICoreShopApplyService;
import com.niu.mall.service.core.shop.vo.ShopApplyConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AdminShopApplyServiceImpl implements IAdminShopApplyService {

    @Autowired
    private SiteApplyMapper siteApplyMapper;
    @Autowired
    private ICoreShopApplyService coreShopApplyService;
    @Autowired
    private IDiyFormService diyFormService;

    private ApplicationContext applicationContext;

    private String getAppType() {
        return RequestUtils.appType();
    }

    private List<Integer> getCircleIds() {
        return RequestUtils.circleIds();
    }

    @Override
    public PageResult<AdminShopApplyPageVo> getPage(PageParam pageParam, AdminShopApplyPageQueryParam queryParam) {
        MPJQueryWrapper<SiteApply> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("s")
                .leftJoin("?_member m ON s.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_sys_user u ON s.uid = u.uid".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category c ON s.category_id = c.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category cc ON s.child_category_id = cc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .select("m.nickname, u.username as login_username, u.head_img, " +
                        "s.apply_id, s.site_id, s.member_id, s.site_name, s.user_name, s.user_mobile, " +
                        "s.category_id, s.child_category_id, s.business_license, s.status, s.create_time, " +
                        "s.update_time, s.audit_time, s.open_status, s.uid, s.apply_type, s.circle_id, " +
                        "s.account, s.password, " +
                        "c.name as category_name, cc.name as child_category_name")
                .eq("s.addon", "mall");

        if ("circle".equals(getAppType())) {
            List<Integer> circleIds = getCircleIds();
            if (CollUtil.isEmpty(circleIds)) {
                return PageResult.build(pageParam.getPage(), pageParam.getLimit(), 0L);
            }
            wrapper.in("s.circle_id", circleIds);
        }

        // 搜索条件
        if (queryParam.getStatus() != null) {
            wrapper.eq("s.status", queryParam.getStatus());
        }
        if (queryParam.getCategoryId() != null) {
            wrapper.eq("s.category_id", queryParam.getCategoryId());
        }
        if (StrUtil.isNotBlank(queryParam.getKeywords())) {
            wrapper.and(w -> w.like("s.site_name", queryParam.getKeywords())
                    .or().like("s.user_name", queryParam.getKeywords())
                    .or().like("s.user_mobile", queryParam.getKeywords()));
        }
        if (ObjectUtil.isNotEmpty(queryParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(wrapper, "s.create_time", queryParam.getCreateTime());
        }

        wrapper.orderByDesc("s.create_time");

        IPage<AdminShopApplyPageVo> page = siteApplyMapper.selectJoinPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()),
                AdminShopApplyPageVo.class, wrapper);

        for (AdminShopApplyPageVo vo : page.getRecords()) {
            vo.setStatusName(ShopApplyEnum.getNameByStatus(vo.getStatus()));
            vo.setOpenStatusName(ShopApplyEnum.getNameByOpenStatus(vo.getOpenStatus()));
            if (StrUtil.isBlank(vo.getApplyType())) {
                vo.setApplyType("member");
            }
            if (StrUtil.isBlank(vo.getUserName()) && StrUtil.isNotBlank(vo.getNickname())) {
                vo.setUserName(vo.getNickname());
            }
            Map<String, Object> user = new HashMap<>();
            user.put("uid", vo.getUid());
            user.put("username", vo.getLoginUsername());
            user.put("head_img", vo.getHeadImg());
            vo.setUser(user);
        }

        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal()).setData(page.getRecords());
    }

    @Override
    public AdminShopApplyInfoVo getInfo(Integer id) {
        MPJQueryWrapper<SiteApply> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("s")
                .leftJoin("?_member m ON s.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_sys_user u ON s.uid = u.uid".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category c ON s.category_id = c.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category cc ON s.child_category_id = cc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .select("m.nickname, u.username as login_username, " +
                        "s.apply_id, s.member_id, s.site_name, s.user_name, s.user_mobile, " +
                        "s.category_id, s.child_category_id, s.business_license, s.status, s.create_time, " +
                        "s.update_time, s.audit_time, s.refuse_reason, s.open_status, s.uid, s.form_record_id, " +
                        "s.apply_type, s.circle_id, " +
                        "c.name as category_name, cc.name as child_category_name")
                .eq("s.apply_id", id);

        if ("circle".equals(getAppType())) {
            wrapper.in("s.circle_id", getCircleIds());
        }

        AdminShopApplyInfoVo vo = siteApplyMapper.selectJoinOne(AdminShopApplyInfoVo.class, wrapper);
        if (vo == null) {
            throw new CommonException("申请不存在");
        }
        vo.setStatusName(ShopApplyEnum.getNameByStatus(vo.getStatus()));
        vo.setOpenStatusName(ShopApplyEnum.getNameByOpenStatus(vo.getOpenStatus()));

        // 查询表单信息（模拟，实际需注入 DiyFormService）
        if (vo.getFormRecordId() != null && vo.getFormRecordId() > 0) {
            vo.setFormInfo(diyFormService.getRecordInfo(vo.getFormRecordId()));
        } else {
            vo.setFormInfo(new HashMap<>());
        }
        return vo;
    }

    @Override
    @Transactional
    public void verify(Integer id, AdminShopApplyVerifyParam param) {
        SiteApply apply = loadApplyWithScope(id);
        if (apply == null) {
            return;
        }

        long now = System.currentTimeMillis() / 1000;
        apply.setStatus(param.getStatus());
        apply.setUpdateTime(now);
        if (ShopApplyEnum.PASS.getValue().equals(param.getStatus())) {
            apply.setAuditTime(now);
            apply.setRefuseReason("");
        } else if (ShopApplyEnum.REFUSE.getValue().equals(param.getStatus())) {
            apply.setRefuseReason(param.getRefuseReason() != null ? param.getRefuseReason() : "");
        }
        siteApplyMapper.updateById(apply);

        SiteShopApplyVerifyAfterEvent event = new SiteShopApplyVerifyAfterEvent(
                this,
                "mall_shop_verify",
                "system",
                RequestUtils.uid(),
                apply,
                param.getStatus(),
                param.getRefuseReason()
        );
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 按 addon/circle 权限范围加载申请
     */
    private SiteApply loadApplyWithScope(Integer id) {
        LambdaQueryWrapper<SiteApply> wrapper = new LambdaQueryWrapper<SiteApply>()
                .eq(SiteApply::getApplyId, id)
                .eq(SiteApply::getAddon, "mall");
        if ("circle".equals(getAppType())) {
            List<Integer> circleIds = getCircleIds();
            if (CollUtil.isEmpty(circleIds)) {
                return null;
            }
            wrapper.in(SiteApply::getCircleId, circleIds);
        }
        return siteApplyMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public void del(Integer id) {
        // 只允许删除未通过审核的申请（状态不等于 PASS）
        SiteApply apply = siteApplyMapper.selectOne(
                new LambdaQueryWrapper<SiteApply>()
                        .eq(SiteApply::getApplyId, id)
                        .ne(SiteApply::getStatus, ShopApplyEnum.PASS.getValue())
        );
        if (apply != null) {
            siteApplyMapper.deleteById(id);
        } else {
            throw new AdminException("申请不存在或已通过，无法删除");
        }
    }

    @Override
    public Map<String, String> getStatusList() {
        return Stream.of(ShopApplyEnum.WAIT_AUDIT, ShopApplyEnum.PASS, ShopApplyEnum.REFUSE)
                .collect(Collectors.toMap(e -> String.valueOf(e.getValue()), ShopApplyEnum::getName));
    }

    @Override
    public ShopApplyConfigVo getShopApplyConfig() {
        // 委托给核心服务
        return coreShopApplyService.getShopApplyConfig();
    }

    @Override
    public void setShopApplyConfig(ShopApplyConfigVo param) {
        coreShopApplyService.setShopApplyConfig(param);
    }

    @Override
    @Transactional
    public void modifyOpenStatus(Integer applyId, Integer siteId) {
        SiteApply apply = siteApplyMapper.selectById(applyId);
        if (apply == null) {
            return;
        }
        apply.setOpenStatus(ShopApplyEnum.OPENED.getValue());
        apply.setSiteId(siteId);
        apply.setUpdateTime(System.currentTimeMillis() / 1000);
        siteApplyMapper.updateById(apply);
    }
}