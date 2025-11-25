package com.niu.core.service.admin.member.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.member.IMemberConfigService;
import com.niu.core.service.admin.member.param.*;
import com.niu.core.service.admin.member.vo.*;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;


/**
 * 会员相关配置
 */
@Service
public class MemberConfigServiceImpl implements IMemberConfigService {

    @Resource
    ICoreMemberConfigService iCoreMemberConfigService;

    /**
     * 获取注册与登录设置
     */
    @Override
    public LoginConfigVo getLoginConfig() {
        return iCoreMemberConfigService.getLoginConfig(RequestUtils.defaultSiteId());
    }

    /**
     * 设置注册与登录设置
     * @param configParam
     */
    @Override
    public void setLoginConfig(LoginConfigParam configParam) {
        iCoreMemberConfigService.setLoginConfig(RequestUtils.defaultSiteId(), configParam);
    }

    /**
     * 获取会员提现设置
     */
    @Override
    public CashOutConfigVo getCashOutConfig() {
        return iCoreMemberConfigService.getCashOutConfig(RequestUtils.defaultSiteId());
    }

    /**
     * 设置注册与登录设置
     * @param configParam
     */
    @Override
    public void setCashOutConfig(CashOutConfigParam configParam) {
        iCoreMemberConfigService.setCashOutConfig(RequestUtils.defaultSiteId(), configParam);
    }

    /**
     * 获取会员设置
     */
    @Override
    public MemberConfigVo getMemberConfig() {
        return iCoreMemberConfigService.getMemberConfig(RequestUtils.defaultSiteId());
    }

    /**
     * 会员设置
     * @param configParam
     */
    @Override
    public void setMemberConfig(MemberConfigParam configParam) {
        iCoreMemberConfigService.setMemberConfig(RequestUtils.defaultSiteId(), configParam);
    }

    /**
     * 获取成长值规则
     */
    @Override
    public JSONObject getGrowthRuleConfig() {
        return iCoreMemberConfigService.getGrowthRuleConfig(RequestUtils.defaultSiteId());
    }

    /**
     * 设置成长值规则
     * @param configParam
     */
    @Override
    public void setGrowthRuleConfig(JSONObject configParam) {
        iCoreMemberConfigService.setGrowthRuleConfig(RequestUtils.defaultSiteId(), configParam);
    }

    /**
     * 获取积分规则
     */
    @Override
    public JSONObject getPointRuleConfig() {
        return iCoreMemberConfigService.getPointRuleConfig(RequestUtils.defaultSiteId());
    }

    /**
     * 设置积分规则
     * @param configParam
     */
    @Override
    public Boolean setPointRuleConfig(JSONObject configParam) {
        iCoreMemberConfigService.setPointRuleConfig(RequestUtils.defaultSiteId(), configParam);
        return  true;
    }


}
