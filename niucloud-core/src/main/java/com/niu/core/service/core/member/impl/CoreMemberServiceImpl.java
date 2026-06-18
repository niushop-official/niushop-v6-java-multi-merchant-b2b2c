package com.niu.core.service.core.member.impl;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.member.*;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.member.IMemberLabelService;
import com.niu.core.service.admin.member.IMemberLevelService;
import com.niu.core.service.admin.member.vo.MemberConfigVo;
import com.niu.core.service.admin.member.vo.MemberLabelInfoVo;
import com.niu.core.service.admin.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.member.dto.MemberInfoDto;
import com.niu.core.service.core.member.param.MemberStatSearchParam;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@Log4j2
@Service
public class CoreMemberServiceImpl implements ICoreMemberService {

    @Resource
    SiteMapper siteMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreMemberConfigService coreMemberConfigService;

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    @Resource
    private IMemberLabelService memberLabelService;

    @Lazy
    @Resource
    private IMemberLevelService memberLevelService;

    @Override
    public Member getMember(Integer siteId, Integer memberId) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.eq("member_id", memberId);
        return memberMapper.selectOne(queryWrapper);
    }

    /**
     * 创建会员码
     *
     * @param siteId
     * @return
     */
    @Override
    public String createMemberNo(Integer siteId) {
        Site site = siteMapper.selectById(siteId);
        MemberConfigVo memberConfig = coreMemberConfigService.getMemberConfig(siteId);

        String no = (site.getMemberNo() + 1) + "";
        String memberNo = memberConfig.getPrefix() + (memberConfig.getPrefix().length() > memberConfig.getLength() ? no : StringUtils.leftPad(no, memberConfig.getLength(), "0"));

        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .eq("site_id", siteId).eq("member_no", memberNo).last("limit 1"));

        if (member == null) {
            return memberNo;
        }

        site.setMemberNo(Integer.parseInt(no));
        siteMapper.updateById(site);

        return this.createMemberNo(siteId);
    }

    /**
     * 通过条件统计会员
     *
     * @param memberStatSearchParam
     * @return
     */
    @Override
    public Integer getMemberCount(MemberStatSearchParam memberStatSearchParam) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(memberStatSearchParam.getSiteId())) {
            queryWrapper.eq("site_id", memberStatSearchParam.getSiteId());
        }
        if (ObjectUtil.isNotNull(memberStatSearchParam.getCreateTime())) {
            String[] createTime = memberStatSearchParam.getCreateTime();
            Long startTime = (createTime[0] == "") ? 0 : DateUtils.StringToTimestamp(createTime[0]);
            Long endTime = (createTime[1] == "") ? 0 : DateUtils.StringToTimestamp(createTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("create_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("create_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le("create_time", endTime);
            }
        }
        if (ObjectUtil.isNotNull(memberStatSearchParam.getSex())) {
            queryWrapper.eq("sex", memberStatSearchParam.getSex());
        }
        if (ObjectUtil.isNotNull(memberStatSearchParam.getLastVisitTime())) {
            String[] lastVisitTime = memberStatSearchParam.getLastVisitTime();
            Long startLastVisitTime = (lastVisitTime[0] == "") ? 0 : DateUtils.StringToTimestamp(lastVisitTime[0]);
            Long endLastVisitTime = (lastVisitTime[1] == "") ? 0 : DateUtils.StringToTimestamp(lastVisitTime[1]);
            if (startLastVisitTime > 0 && endLastVisitTime > 0) {
                queryWrapper.between("last_visit_time", startLastVisitTime, endLastVisitTime);
            } else if (startLastVisitTime > 0 && endLastVisitTime == 0) {
                queryWrapper.ge("last_visit_time", startLastVisitTime);
            } else if (startLastVisitTime == 0 && endLastVisitTime > 0) {
                queryWrapper.le("last_visit_time", endLastVisitTime);
            }
        }
        Long memberCount = memberMapper.selectCount(queryWrapper);
        return memberCount.intValue();
    }

    /**
     * 获取礼包内容
     *
     * @param siteId
     * @param gifts
     * @param scene
     * @return
     */
    public JSONObject getGiftContent(Integer siteId, JSONObject gifts, String scene) {
        JSONObject giftEnum = GiftEnum.getType();

        if (gifts != null) {
            for (String key : gifts.keySet()) {
                try {
                    JSONObject config = gifts.getJSONObject(key);
                    String driver = (String) giftEnum.getByPath(key + ".content." + scene + ".driver");
                    if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                        Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                        Object obj = clazz.getDeclaredConstructor().newInstance();
                        Method method = clazz.getMethod("content", Integer.class, JSONObject.class, String.class);
                        Object[] params = {siteId, config, scene};
                        Object content = method.invoke(obj, params);
                        gifts.putByPath(key + ".content", content);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return gifts;
    }

    /**
     * 获取权益内容
     *
     * @param siteId
     * @param benefits
     * @param scene
     * @return
     */
    public JSONObject getBenefitsContent(Integer siteId, JSONObject benefits, String scene) {
        JSONObject benefitsEnum = BenefitsEnum.getType();

        for (String key : benefits.keySet()) {
            try {
                JSONObject config = benefits.getJSONObject(key);
                String driver = (String) benefitsEnum.getByPath(key + ".content." + scene + ".driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("content", Integer.class, JSONObject.class, String.class);
                    Object[] params = {siteId, config, scene};
                    Object content = method.invoke(obj, params);
                    benefits.putByPath(key + ".content", content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return benefits;
    }

    /**
     * 获取成长值规则内容
     *
     * @param siteId
     * @param rules
     * @param scene
     * @return
     */
    public JSONObject getGrowthRuleContent(Integer siteId, JSONObject rules, String scene) {
        JSONObject growthRuleEnum = GrowthRuleEnum.getType();

        for (String key : rules.keySet()) {
            try {
                JSONObject config = rules.getJSONObject(key);
                String driver = (String) growthRuleEnum.getByPath(key + ".content." + scene + ".driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("content", Integer.class, JSONObject.class, String.class);
                    Object[] params = {siteId, config, scene};
                    Object content = method.invoke(obj, params);
                    rules.putByPath(key + ".content", content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rules;
    }

    /**
     * 获取积分发放规则内容
     *
     * @param siteId
     * @param rules
     * @param scene
     * @return
     */
    public JSONObject getPointGrantRuleContent(Integer siteId, JSONObject rules, String scene) {
        JSONObject pointRuleEnum = PointRuleEnum.getType().getJSONObject("grant");

        for (String key : rules.keySet()) {
            try {
                JSONObject config = rules.getJSONObject(key);
                String driver = (String) pointRuleEnum.getByPath(key + ".content." + scene + ".driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("content", Integer.class, JSONObject.class, String.class);
                    Object[] params = {siteId, config, scene};
                    Object content = method.invoke(obj, params);
                    rules.putByPath(key + ".content", content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rules;
    }

    /**
     * 获取积分消费规则内容
     *
     * @param siteId
     * @param rules
     * @param scene
     * @return
     */
    public JSONObject getPointConsumeRuleContent(Integer siteId, JSONObject rules, String scene) {
        JSONObject pointRuleEnum = PointRuleEnum.getType().getJSONObject("consume");

        for (String key : rules.keySet()) {
            try {
                JSONObject config = rules.getJSONObject(key);
                String driver = (String) pointRuleEnum.getByPath(key + ".content." + scene + ".driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("content", Integer.class, JSONObject.class, String.class);
                    Object[] params = {siteId, config, scene};
                    Object content = method.invoke(obj, params);
                    rules.putByPath(key + ".content", content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rules;
    }

    /**
     * 发放成长值
     *
     * @param siteId
     * @param memberId
     * @param key
     */
    public void sendGrowth(Integer siteId, Integer memberId, String key, Map<String, Object> data) {
        JSONObject ruleConfig = coreMemberConfigService.getGrowthRuleConfig(siteId);
        if (ruleConfig == null) return;

        JSONObject config = ruleConfig.getJSONObject(key);
        if (config == null || config.getInt("is_use", 0).equals(0)) return;

        JSONObject rule = GrowthRuleEnum.getType().getJSONObject(key);
        if (rule == null) return;

        String driver = ObjectUtil.defaultIfNull(rule.getByPath("calculate.driver", String.class), "");
        if (driver.isEmpty()) return;

        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(driver);
            Object obj = clazz.getDeclaredConstructor().newInstance();
            Method method = clazz.getMethod("calculate", Integer.class, String.class, JSONObject.class, Map.class);
            Object[] params = {siteId, key, config, data};
            Object growth = method.invoke(obj, params);

            if (growth instanceof Integer && (Integer) growth > 0) {
                Double accountData = Double.parseDouble(growth.toString());
                coreMemberAccountService.addLog(siteId, memberId, AccountTypeEnum.GROWTH.getType(), accountData, String.valueOf(ObjectUtil.defaultIfNull(data.get("from_type"), "")), String.valueOf(ObjectUtil.defaultIfNull(data.get("memo"), "")), String.valueOf(ObjectUtil.defaultIfNull(data.get("related_id"), "")));
            }
        } catch (Exception e) {
            System.out.println("成长值发放异常");
            e.printStackTrace();
        }
    }

    /**
     * 发放积分
     *
     * @param siteId
     * @param memberId
     * @param key
     */
    public void sendPoint(Integer siteId, Integer memberId, String key, Map<String, Object> data) {
        JSONObject ruleConfig = coreMemberConfigService.getPointRuleConfig(siteId).getJSONObject("grant");
        if (ruleConfig == null) return;

        JSONObject config = ruleConfig.getJSONObject(key);
        if (config == null || config.getInt("is_use", 0).equals(0)) return;

        JSONObject rule = PointRuleEnum.getType().getJSONObject("grant");
        if (rule == null) return;

        String driver = ObjectUtil.defaultIfNull(rule.getByPath(key + ".calculate.driver", String.class), "");
        if (driver.isEmpty()) return;

        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(driver);
            Object obj = clazz.getDeclaredConstructor().newInstance();
            Method method = clazz.getMethod("calculate", Integer.class, String.class, JSONObject.class, Map.class);
            Object[] params = {siteId, key, config, data};
            Object growth = method.invoke(obj, params);

            if (growth instanceof Integer && (Integer) growth > 0) {
                Double accountData = Double.parseDouble(growth.toString());
                coreMemberAccountService.addLog(siteId, memberId, AccountTypeEnum.POINT.getType(), accountData, String.valueOf(ObjectUtil.defaultIfNull(data.get("from_type"), "")), String.valueOf(ObjectUtil.defaultIfNull(data.get("memo"), "")), String.valueOf(ObjectUtil.defaultIfNull(data.get("related_id"), "")));
            }
        } catch (Exception e) {
            System.out.println("积分发放异常");
            e.printStackTrace();
        }
    }

    /**
     * 会员礼包发放
     *
     * @param siteId
     * @param memberId
     * @param gifts
     */
    public void memberGiftGrant(Integer siteId, Integer memberId, JSONObject gifts, Map<String, Object> vars) {
        if (gifts == null) return;

        JSONObject giftEnum = GiftEnum.getType();

        try {
            for (String key : gifts.keySet()) {
                JSONObject config = gifts.getJSONObject(key);
                String driver = (String) giftEnum.getByPath(key + ".grant.driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("grant", Integer.class, Integer.class, JSONObject.class, Map.class);
                    Object[] params = {siteId, memberId, config, vars};
                    method.invoke(obj, params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("会员礼包发放失败");
        }
    }

    /**
     * 获取权益字典
     *
     * @param siteId
     * @return
     */
    public JSONObject getBenefitsDict(Integer siteId) {
        return BenefitsEnum.getType();
    }

    /**
     * 获取礼包字典
     *
     * @param siteId
     * @return
     */
    public JSONObject getGiftDict(Integer siteId) {
        return GiftEnum.getType();
    }


    @Override
    public MemberInfoDto getMemberInfo(Integer memberId) {
        Member member = memberMapper.selectById(memberId);
        MemberInfoDto result = new MemberInfoDto();
        BeanUtils.copyProperties(member, result);
        if (StringUtils.isNotEmpty(member.getHeadimg())) {
            result.setHeadimgSmall(CommonUtils.thumbImageSmall(member.getSiteId(), member.getHeadimg()));
            result.setHeadimgMiddle(CommonUtils.thumbImageMiddle(member.getSiteId(), member.getHeadimg()));
            result.setHeadimgBig(CommonUtils.thumbImageBig(member.getSiteId(), member.getHeadimg()));
        }
        MemberLabelInfoVo label = new MemberLabelInfoVo();
        MemberLevelInfoVo level = new MemberLevelInfoVo();
        try {
            label = memberLabelService.info(memberId);
            level = memberLevelService.info(memberId);
        } catch (Exception e) {
            log.error("获取会员标签或会员等级失败:{}", e.getMessage());
        } finally {
            result.setLabelInfo(label);
            result.setLevelInfo(level);
        }
        return result;
    }

    @Override
    public Member getInfoByMemberId(Integer memberId, String field) {
        if (memberId == null) {
            return null;
        }
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId);
        if (StringUtils.isNotEmpty(field) && !"*".equals(field.trim())) {
            String[] columns = Arrays.stream(field.split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotEmpty)
                    .toArray(String[]::new);
            if (columns.length > 0) {
                queryWrapper.select(columns);
            }
        }
        Member member = memberMapper.selectOne(queryWrapper);
        if (member == null) {
            return null;
        }
        if (member.getMemberLevel() != null) {
            try {
                MemberLevelInfoVo levelInfoVo = memberLevelService.info(member.getMemberLevel());
                if (levelInfoVo != null) {
                    member.setMemberLevelName(levelInfoVo.getLevelName());
                }
            } catch (Exception e) {
            }
        }
        return member;
    }

    @Override
    public void modifyRecommender(Integer memberId, Integer pid) {
        // 1. 如果 pid 不为 0，进行校验
        if (pid != null && pid != 0) {
            if (memberId.equals(pid)) {
                throw new CommonException("MEMBER_RECOMMENDER_CAN_NOT_BE_ITSELF");
            }
            Member parent = memberMapper.selectOne(new LambdaQueryWrapper<Member>().eq(Member::getMemberId, pid));
            if (parent == null) {
                throw new CommonException("MEMBER_RECOMMENDER_NOT_EXIST");
            }
        } else {
            // pid 为 0 表示清除推荐人
            pid = 0;
        }

        // 2. 更新会员的 pid 字段
        LambdaUpdateWrapper<Member> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Member::getMemberId, memberId)
                .set(Member::getPid, pid);
        int updated = memberMapper.update(null, wrapper);
        if (updated == 0) {
            throw new CommonException("MEMBER_NOT_EXIST");
        }

        // 3. 发布事件（异步或同步）
//        eventPublisher.publishEvent(new AfterMemberRecommenderChangeEvent(this, memberId, pid));
    }

    @Override
    public void modifyPassword(Integer memberId, String password) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) throw new CommonException("MEMBER_NOT_EXIST");

        //todo  需要考虑一下,新的密码和原密码一样能否通过验证
        String passwordHash = PasswordEncipher.encode(password);
        memberMapper.update(new LambdaUpdateWrapper<Member>()
                .eq(Member::getMemberId, memberId)
                .set(Member::getPassword, passwordHash));


        //todo 清除登录状态
//        TokenAuth::clearToken($member_id, AppTypeDict::API);
    }
}
