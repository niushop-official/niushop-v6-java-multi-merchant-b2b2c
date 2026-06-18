package com.niu.core.service.core.verify.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.entity.verify.Verifier;
import com.niu.core.entity.verify.Verify;
import com.niu.core.enums.sys.VerifierTypeEnum;
import com.niu.core.enums.sys.VerifyTypeEnum;
import com.niu.core.event.sys.VerifyCreateEventDefiner;
import com.niu.core.event.sys.VerifyEventDefiner;
import com.niu.core.event.verify.VerifierCheckEventDefiner;
import com.niu.core.event.verify.VerifyCheckEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.mapper.verify.VerifierMapper;
import com.niu.core.mapper.verify.VerifyMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.verify.ICoreVerifyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoreVerifyServiceImpl implements ICoreVerifyService {

    @Resource
    SysUserMapper sysUserMapper;
    @Resource
    VerifyMapper verifyMapper;
    @Resource
    VerifierMapper verifierMapper;
    @Resource
    MemberMapper memberMapper;
    @Resource
    Cached cached;
    private static final String CACHE_VERIFY = "verify";

    @Override
    public Boolean adminVerify(Integer siteId, Integer uid, String code) {
        //用户信息
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.select("username", "head_img");
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        // 1、获取核销码数据
        Map<String, Object> verifyMap = this.getCodeData(code);
        // 检测站点数据
        if (verifyMap.get("site_id") == null || !verifyMap.get("site_id").equals(siteId)) {
            // 核销码已过期
            throw new RuntimeException("当前核销码已核销或已失效");
        }
        // 核销操作
        Map<String, Object> verifyDataMap = new HashMap<>();
        verifyDataMap.put("site_id", verifyMap.get("site_id"));
        verifyDataMap.put("code", code);
        verifyDataMap.put("data", verifyMap.get("data"));
        verifyDataMap.put("value", JSONUtil.toJsonStr(verifyMap.get("value")));
        verifyDataMap.put("type", verifyMap.get("type"));
        verifyDataMap.put("body", verifyMap.get("body"));
        verifyDataMap.put("relate_tag", verifyMap.get("relate_tag") != null ? String.valueOf(verifyMap.get("relate_tag")) : "0");
        verifyDataMap.put("create_time", DateUtils.currTime());
        verifyDataMap.put("verifier_member_id", uid);
        verifyDataMap.put("verifier_name", sysUser.getUsername());
        verifyDataMap.put("verifier_headimg", sysUser.getHeadImg());
        verifyDataMap.put("verifier_type", VerifierTypeEnum.SITE.getType());
        // 核销
        VerifyEventDefiner.VerifyEvent verifyEvent = new VerifyEventDefiner.VerifyEvent();
        verifyEvent.setSiteId(siteId);
        verifyEvent.build(verifyDataMap);
        EventAndSubscribeOfPublisher.publishAndCallback(verifyEvent);

        // 添加核销记录
        Verify model = new Verify();
        model.setSiteId(verifyEvent.getSiteId());
        model.setCode(verifyEvent.getCode());
        model.setData(JSONUtil.toJsonStr(verifyEvent.getData()));
        model.setValue(verifyEvent.getValue());
        model.setType(verifyEvent.getType());
        model.setBody(verifyEvent.getBody());
        model.setRelateTag(verifyEvent.getRelateTag());
        model.setCreateTime(verifyEvent.getCreateTime());
        model.setVerifierMemberId(verifyEvent.getVerifierMemberId());
        model.setVerifierName(verifyEvent.getVerifierName());
        model.setVerifierHeadimg(verifyEvent.getVerifierHeadimg());
        model.setVerifierType(verifyEvent.getVerifierType());
        model.setIsAdmin(1); // 后台核销
        verifyMapper.insert(model);

        // 核销码失效
        this.clearCode(code);
        return true;
    }

    @Override
    public Map<String, Object> adminGetInfoByCode(Integer siteId, String code) {
        // 获取核销码数据
        Map<String, Object> value = this.getCodeData(code);
        if (value.get("site_id") == null || !value.get("site_id").equals(siteId)) {
            throw new RuntimeException("当前核销码已核销或已失效");
        }
        VerifyCheckEventDefiner.VerifyCheckEvent event = new VerifyCheckEventDefiner.VerifyCheckEvent();
        event.setVerifyData(value);
        List<VerifyCheckEventDefiner.VerifyCheckResult> data = EventAndSubscribeOfPublisher.publishAndCallback(event);

        // 判断
        if (data != null && !data.isEmpty()) {
            for (VerifyCheckEventDefiner.VerifyCheckResult datum : data) {
                if (datum != null && datum.getProcessedData() != null) {
                    value = datum.getProcessedData();
                    break;
                }
            }
        }
        return value;
    }

    @Override
    public Map<String, Object> getInfoByCode(String code, Integer memberId) {
        // 获取核销码数据
        Map<String, Object> value = this.getCodeData(code);
        // 触发核销检查事件
        VerifyCheckEventDefiner.VerifyCheckEvent event = new VerifyCheckEventDefiner.VerifyCheckEvent();
        event.setVerifyData(value);
        List<VerifyCheckEventDefiner.VerifyCheckResult> data = EventAndSubscribeOfPublisher.publishAndCallback(event);
        // 处理事件回调结果
        if (data != null && !data.isEmpty()) {
            for (VerifyCheckEventDefiner.VerifyCheckResult datum : data) {
                if (datum != null && datum.getProcessedData() != null) {
                    value = datum.getProcessedData();
                    break;
                }
            }
        }
        // 检测核销员身份，是否有核销权限
        Integer siteId = (Integer) value.get("site_id");
        Map<String, Object> dataMap = (Map<String, Object>) value.get("data");

        //检查和校园是否存在
        QueryWrapper<Verifier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId);
        List<Verifier> merberList = verifierMapper.selectList(queryWrapper);
        if (merberList.isEmpty()) throw new CommonException("核销员不存在");
        // 检查当前核销员是否有权限
        if (!merberList.stream().map(Verifier::getSiteId).toList().contains(siteId)) {
            throw new RuntimeException("您没有对应店铺核销权限，请联系管理员");
        }
        // 触发核销员检查事件
        VerifierCheckEventDefiner.VerifierCheckEvent verifierEvent = new VerifierCheckEventDefiner.VerifierCheckEvent();
        verifierEvent.setVerifyMemberId(memberId);
        verifierEvent.setType((String) value.get("type"));
        verifierEvent.setData(dataMap);
        verifierEvent.setSiteId(siteId);
        List<VerifierCheckEventDefiner.VerifierCheckResult> verifierResults = EventAndSubscribeOfPublisher.publishAndCallback(verifierEvent);

        // 获取核销员ID列表
        List<Integer> verifyMemberIds = new ArrayList<>();
        if (verifierResults != null && !verifierResults.isEmpty()) {
            VerifierCheckEventDefiner.VerifierCheckResult firstResult = verifierResults.get(0);
            if (firstResult != null && firstResult.getVerifyMemberIds() != null) {
                verifyMemberIds = firstResult.getVerifyMemberIds();
            }
        }

        // 检查当前核销员是否有权限
        if (!verifyMemberIds.contains(memberId)) {
            throw new RuntimeException("您没有订单核销权限，请联系管理员");
        }

        return value;
    }

    @Override
    public Boolean verify(Integer memberId, String code) {
        // 会员信息
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId);
        queryWrapper.select("nickname", "headimg");
        Member memberInfo = memberMapper.selectOne(queryWrapper);
        // 获取核销码数据
        Map<String, Object> value = this.getCodeData(code);
        // 检测核销员身份
        Integer siteId = (Integer) value.get("site_id");
        //检查和校园是否存在
        QueryWrapper<Verifier> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("member_id", memberId);
        List<Verifier> merberList = verifierMapper.selectList(queryWrapper1);
        if (merberList.isEmpty()) throw new CommonException("核销员不存在");
        // 检查当前核销员是否有权限
        if (!merberList.stream().map(Verifier::getSiteId).toList().contains(siteId)) {
            throw new RuntimeException("您没有对应店铺核销权限，请联系管理员");
        }

        VerifyCheckEventDefiner.VerifyCheckEvent event = new VerifyCheckEventDefiner.VerifyCheckEvent();
        event.setVerifyData(value);
        List<VerifyCheckEventDefiner.VerifyCheckResult> data = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if (!data.isEmpty()) {
            for (var item : data) {
                if (!data.isEmpty()) {
                    value = item.getProcessedData();
                    break;
                }
            }
        }


        Map<String, Object> dataMap = (Map<String, Object>) value.get("data");
        VerifierCheckEventDefiner.VerifierCheckEvent verifierEvent = new VerifierCheckEventDefiner.VerifierCheckEvent();
        verifierEvent.setVerifyMemberId(memberId);
        verifierEvent.setType((String) value.get("type"));
        verifierEvent.setData(dataMap);
        verifierEvent.setSiteId(siteId);
        List<VerifierCheckEventDefiner.VerifierCheckResult> verifierResults = EventAndSubscribeOfPublisher.publishAndCallback(verifierEvent);
        // 获取核销员ID列表
        List<Integer> verifyMemberIds = new ArrayList<>();
        if (!verifierResults.isEmpty()) {
            VerifierCheckEventDefiner.VerifierCheckResult firstResult = verifierResults.get(0);
            if (firstResult != null && firstResult.getVerifyMemberIds() != null) {
                verifyMemberIds = firstResult.getVerifyMemberIds();
            }
        }

        // 检查当前核销员是否有权限
        if (!verifyMemberIds.contains(memberId)) {
            throw new RuntimeException("您没有订单核销权限，请联系管理员");
        }
        // 核销操作
        Map<String, Object> verifyDataMap = new HashMap<>();
        verifyDataMap.put("site_id", value.get("site_id"));
        verifyDataMap.put("code", code);
        verifyDataMap.put("data", value.get("data"));
        verifyDataMap.put("value", JSONUtil.toJsonStr(value.get("value")));
        verifyDataMap.put("type", value.get("type"));
        verifyDataMap.put("body", value.get("body"));
        verifyDataMap.put("relate_tag", value.get("relate_tag") != null ? String.valueOf(value.get("relate_tag")) : "0");
        verifyDataMap.put("create_time", DateUtils.currTime());
        verifyDataMap.put("verifier_member_id", memberId);
        verifyDataMap.put("verifier_name", memberInfo != null ? memberInfo.getNickname() : "");
        verifyDataMap.put("verifier_headimg", memberInfo != null ? memberInfo.getHeadimg() : "");
        verifyDataMap.put("verifier_type", VerifierTypeEnum.MEMBER.getType());
        // 核销
        VerifyEventDefiner.VerifyEvent verifyEvent = new VerifyEventDefiner.VerifyEvent();
        verifyEvent.setSiteId(siteId);
        verifyEvent.build(verifyDataMap);
        EventAndSubscribeOfPublisher.publishAndCallback(verifyEvent);
        // 添加核销记录
        Verify model = new Verify();
        model.setSiteId(verifyEvent.getSiteId());
        model.setCode(verifyEvent.getCode());
        model.setData(JSONUtil.toJsonStr(verifyEvent.getData()));
        model.setValue(verifyEvent.getValue());
        model.setType(verifyEvent.getType());
        model.setBody(verifyEvent.getBody());
        model.setRelateTag(verifyEvent.getRelateTag());
        model.setCreateTime(verifyEvent.getCreateTime());
        model.setVerifierMemberId(verifyEvent.getVerifierMemberId());
        model.setVerifierName(verifyEvent.getVerifierName());
        model.setVerifierHeadimg(verifyEvent.getVerifierHeadimg());
        model.setVerifierType(verifyEvent.getVerifierType());
        model.setIsAdmin(0); // 非后台核销
        verifyMapper.insert(model);
        // 核销码失效
        this.clearCode(code);
        return true;
    }

    public Boolean adminVerify(Integer siteId, String code) {

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUid, RequestUtils.uid()));
        // 获取核销码数据
        Map<String, Object> value = this.getCodeData(code);
        // 检测核销员身份
        Integer vSiteId = (Integer) value.get("site_id");
        // 检查当前核销员是否有权限
        if (!vSiteId.equals(siteId)) {
            throw new RuntimeException("该店铺无核销权限");
        }

        VerifyCheckEventDefiner.VerifyCheckEvent event = new VerifyCheckEventDefiner.VerifyCheckEvent();
        event.setVerifyData(value);
        List<VerifyCheckEventDefiner.VerifyCheckResult> data = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if (!data.isEmpty()) {
            for (var item : data) {
                if (!data.isEmpty()) {
                    value = item.getProcessedData();
                    break;
                }
            }
        }


        Map<String, Object> dataMap = (Map<String, Object>) value.get("data");
        VerifierCheckEventDefiner.VerifierCheckEvent verifierEvent = new VerifierCheckEventDefiner.VerifierCheckEvent();
        verifierEvent.setVerifyMemberId(RequestUtils.uid());
        verifierEvent.setType((String) value.get("type"));
        verifierEvent.setData(dataMap);
        verifierEvent.setSiteId(siteId);
        List<VerifierCheckEventDefiner.VerifierCheckResult> verifierResults = EventAndSubscribeOfPublisher.publishAndCallback(verifierEvent);
        // 获取核销员ID列表
        List<Integer> verifyMemberIds = new ArrayList<>();
        if (!verifierResults.isEmpty()) {
            VerifierCheckEventDefiner.VerifierCheckResult firstResult = verifierResults.get(0);
            if (firstResult != null && firstResult.getVerifyMemberIds() != null) {
                verifyMemberIds = firstResult.getVerifyMemberIds();
            }
        }

        // 核销操作
        Map<String, Object> verifyDataMap = new HashMap<>();
        verifyDataMap.put("site_id", value.get("site_id"));
        verifyDataMap.put("code", code);
        verifyDataMap.put("data", value.get("data"));
        verifyDataMap.put("value", JSONUtil.toJsonStr(value.get("value")));
        verifyDataMap.put("type", value.get("type"));
        verifyDataMap.put("body", value.get("body"));
        verifyDataMap.put("relate_tag", value.get("relate_tag") != null ? String.valueOf(value.get("relate_tag")) : "0");
        verifyDataMap.put("create_time", DateUtils.currTime());
        verifyDataMap.put("verifier_member_id", RequestUtils.uid());
        verifyDataMap.put("verifier_name", user != null ? user.getUsername() : "");
        verifyDataMap.put("verifier_headimg", user != null ? user.getHeadImg() : "");
        verifyDataMap.put("verifier_type", VerifierTypeEnum.SITE.getType());
        // 核销
        VerifyEventDefiner.VerifyEvent verifyEvent = new VerifyEventDefiner.VerifyEvent();
        verifyEvent.setSiteId(siteId);
        verifyEvent.build(verifyDataMap);
        EventAndSubscribeOfPublisher.publishAndCallback(verifyEvent);
        // 添加核销记录
        Verify model = new Verify();
        model.setSiteId(verifyEvent.getSiteId());
        model.setCode(verifyEvent.getCode());
        model.setData(JSONUtil.toJsonStr(verifyEvent.getData()));
        model.setValue(verifyEvent.getValue());
        model.setType(verifyEvent.getType());
        model.setBody(verifyEvent.getBody());
        model.setRelateTag(verifyEvent.getRelateTag());
        model.setCreateTime(verifyEvent.getCreateTime());
        model.setVerifierMemberId(verifyEvent.getVerifierMemberId());
        model.setVerifierName(verifyEvent.getVerifierName());
        model.setVerifierHeadimg(verifyEvent.getVerifierHeadimg());
        model.setVerifierType(verifyEvent.getVerifierType());
        model.setIsAdmin(0); // 非后台核销
        verifyMapper.insert(model);
        // 核销码失效
        this.clearCode(code);
        return true;
    }


    private Map<String, Object> getCodeData(String verifyCode) {
        Map<String, Object> codeMap = (Map<String, Object>) cached.tag(CACHE_VERIFY).get(verifyCode);
        if (codeMap == null) {
            // 核销码已过期
            throw new RuntimeException("当前核销码已核销或已失效");

        }
        return codeMap;
    }

    /**
     * 清除核销
     *
     * @param verifyCode
     */
    private void clearCode(String verifyCode) {
        cached.tag(CACHE_VERIFY).remove(verifyCode);
    }

    @Override
    public List<String> create(Integer siteId, Integer memberId, String type, Map<String, Object> data) {
        if (ObjectUtil.isEmpty(VerifyTypeEnum.getTypeByName(type))) {
            throw new RuntimeException("核销类型错误");
        }
        // 通过事件获取核销创建
        VerifyCreateEventDefiner.VerifyCreateEvent verifyCreateEvent = new VerifyCreateEventDefiner.VerifyCreateEvent();
        verifyCreateEvent.setSiteId(siteId);
        verifyCreateEvent.setType(type);
        verifyCreateEvent.setMemberId(memberId);
        verifyCreateEvent.setData(data);
        List<VerifyCreateEventDefiner.VerifyCreateEventResult> verifyCreateJsonObjectList = EventAndSubscribeOfPublisher.publishAndCallback(verifyCreateEvent);
        VerifyCreateEventDefiner.VerifyCreateEventResult result = null;
        if (verifyCreateJsonObjectList != null && !verifyCreateJsonObjectList.isEmpty()) {
            result = verifyCreateJsonObjectList.get(0);
        }
        // 组装核销码数据
        int count = 1;
        Object valueData = new HashMap<>();
        String body = "";
        Object relateTag = 0;
        Long expireTime = null;
        if (result != null) {
            count = result.getCount() != null ? result.getCount() : 1;
            if (result.getData() != null) {
                valueData = StrUtil.isNotEmpty(result.getData()) ? JSONUtil.parseObj(result.getData()) : new HashMap<>();
            }
            body = result.getBody() != null ? result.getBody() : "";
            if (result.getRelateTag() != null) {
                relateTag = StrUtil.isNotEmpty(result.getRelateTag()) ? Integer.parseInt(result.getRelateTag()) : 0;
            }
            expireTime = result.getExpireTime();
        }
        String strData = JSONUtil.toJsonStr(data);
        Map<String, Object> value = new HashMap<>();
        value.put("site_id", siteId);
        value.put("type", type);
        value.put("type_name", VerifyTypeEnum.getTypeByName(type));
        value.put("data", data);
        value.put("value", valueData);
        value.put("body", body);
        value.put("relate_tag", relateTag);
        // 生成核销码
        List<String> verifyCodeList = new ArrayList<>();
        int temp = 0;
        while (temp < count) {
            String salt = IdUtil.randomUUID();
            String verifyCode = DigestUtil.md5Hex(salt + strData);
            this.createCode(verifyCode, value, expireTime);
            verifyCodeList.add(verifyCode);
            temp++;
        }
        return verifyCodeList;
    }

    private void createCode(String verifyCode, Map<String, Object> valueMap, Long timeoutSeconds) {
        if (timeoutSeconds != null && timeoutSeconds > 0) {
            cached.tag(CACHE_VERIFY).put(verifyCode, valueMap, timeoutSeconds);
        } else {
            cached.tag(CACHE_VERIFY).put(verifyCode, valueMap);
        }
    }
}

