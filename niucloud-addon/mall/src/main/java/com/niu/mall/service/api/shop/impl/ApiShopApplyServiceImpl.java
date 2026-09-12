package com.niu.mall.service.api.shop.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.SiteApply;
import com.niu.core.enums.shop.ShopApplyEnum;
import com.niu.core.mapper.site.SiteApplyMapper;
import com.niu.core.service.api.diy.IDiyFormService;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.shop.param.SiteApplyParam;
import com.niu.core.service.core.site.apply.ICoreSiteApplyService;
import com.niu.core.service.core.site.apply.vo.AccountCheckVo;
import com.niu.mall.service.api.shop.IApiShopApplyService;
import com.niu.mall.service.api.shop.param.ShopApplyAddParam;
import com.niu.mall.service.api.shop.param.ShopApplyListParam;
import com.niu.mall.service.api.shop.vo.ShopApplyInfoVo;
import com.niu.mall.service.api.shop.vo.ShopApplyPageVo;
import com.niu.mall.service.core.shop.ICoreShopApplyService;
import com.niu.mall.service.core.shop.vo.ShopApplyConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class ApiShopApplyServiceImpl implements IApiShopApplyService {

    @Autowired
    private SiteApplyMapper siteApplyMapper;

    @Autowired
    private ICoreSiteApplyService siteSiteApplyService;

    @Autowired
    private ICoreShopApplyService coreShopApplyService;

    @Autowired
    private IDiyFormService diyFormService;

    @Autowired
    private IRegisterService registerService;

    private Integer getCurrentMemberId() {
        return RequestUtils.memberId();
    }

    @Override
    public PageResult<ShopApplyPageVo> getPage(PageParam pageParam, ShopApplyListParam param) {
        Integer memberId = getCurrentMemberId();
        MPJQueryWrapper<SiteApply> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("s")
                .leftJoin("?_member m ON s.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category c ON s.category_id = c.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category cc ON s.child_category_id = cc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .select("s.apply_id, s.member_id, s.site_name, s.user_name, s.user_mobile, " +
                        "s.category_id, s.child_category_id, s.circle_id, s.business_license, " +
                        "s.status, s.create_time, s.update_time, s.audit_time, s.refuse_reason, " +
                        "m.nickname as member_name, c.name as category_name, cc.name as child_category_name")
                .eq("s.member_id", memberId);

        if (param.getStatus() != null) {
            wrapper.eq("s.status", param.getStatus());
        }
        wrapper.orderByDesc("s.create_time");

        IPage<ShopApplyPageVo> page = siteApplyMapper.selectJoinPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()),
                ShopApplyPageVo.class, wrapper);

        // 填充状态名称（使用枚举）
        for (ShopApplyPageVo vo : page.getRecords()) {
            vo.setStatusName(ShopApplyEnum.getNameByStatus(vo.getStatus()));
        }

        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal()).setData(page.getRecords());
    }

    @Override
    public ShopApplyInfoVo getInfo(Integer id) {
        Integer memberId = getCurrentMemberId();
        MPJQueryWrapper<SiteApply> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("s")
                .leftJoin("?_member m ON s.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category c ON s.category_id = c.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category cc ON s.child_category_id = cc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .select("s.apply_id, s.member_id, s.site_name, s.user_name, s.user_mobile, " +
                        "s.category_id, s.child_category_id, s.circle_id, s.business_license, " +
                        "s.status, s.create_time, s.update_time, s.audit_time, s.refuse_reason, " +
                        "s.form_record_id, m.nickname as member_name, c.name as category_name, cc.name as child_category_name")
                .eq("s.apply_id", id)
                .eq("s.member_id", memberId);

        ShopApplyInfoVo vo = siteApplyMapper.selectJoinOne(ShopApplyInfoVo.class, wrapper);
        if (vo == null) {
            throw new CommonException("申请不存在");
        }
        vo.setStatusName(ShopApplyEnum.getNameByStatus(vo.getStatus()));
        // 表单信息需要通过 DiyFormService 查询，此处模拟，实际可注入 DiyFormService
        if (vo.getFormRecordId() != null && vo.getFormRecordId() > 0) {
            vo.setFormInfo(diyFormService.getFormRecordInfo(vo.getFormRecordId()));
        }
        return vo;
    }

    @Override
    public AccountCheckVo accountCheck(String account) {
        // 调用核心服务
        return siteSiteApplyService.accountCheck(account);
    }

    @Override
    @Transactional
    public Integer add(ShopApplyAddParam param) {
        registerService.checkMobileCode(param.getUserMobile(), param.getMobileKey(), param.getMobileCode());
        SiteApplyParam data = BeanUtil.copyProperties(param, SiteApplyParam.class, "formData");
        data.setFormData(param.getFormData());
        data.setAddon("mall");
        data.setApplyType("member");
        data.setMemberId(getCurrentMemberId());
        Long applyId = siteSiteApplyService.add(data);
        return applyId.intValue();
    }

    @Override
    @Transactional
    public void edit(Integer id, ShopApplyAddParam param) {
        registerService.checkMobileCode(param.getUserMobile(), param.getMobileKey(), param.getMobileCode());
        SiteApplyParam data = BeanUtil.copyProperties(param, SiteApplyParam.class, "formData");
        data.setFormData(param.getFormData());
        siteSiteApplyService.edit(id, data);
    }

    @Override
    public List<Map<String, String>> getStatusList() {
        return Stream.of(ShopApplyEnum.WAIT_AUDIT, ShopApplyEnum.PASS, ShopApplyEnum.REFUSE)
                .map(e -> Map.of("label", e.getName(), "value", String.valueOf(e.getValue())))
                .toList();
    }

    @Override
    public ShopApplyConfigVo getShopApplyConfig() {
        return coreShopApplyService.getShopApplyConfig();
    }
}
