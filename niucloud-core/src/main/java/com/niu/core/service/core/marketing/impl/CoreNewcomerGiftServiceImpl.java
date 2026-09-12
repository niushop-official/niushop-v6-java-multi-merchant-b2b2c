package com.niu.core.service.core.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.marketing.NewcomerGiftRecords;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.marketing.NewcomerGiftRecordsMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.marketing.ICoreNewcomerGiftService;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CoreNewcomerGiftServiceImpl implements ICoreNewcomerGiftService {

    private static final String CONFIG_KEY = "NEWCOMER_GIFT";

    @Resource
    private ICoreConfigService coreConfigService;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private NewcomerGiftRecordsMapper newcomerGiftRecordsMapper;

    @Resource
    private ICoreMemberService coreMemberService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNewcomerGift(Integer memberId) {
        if (memberId == null || memberId <= 0) return;

        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), CONFIG_KEY);
        if (ObjectUtil.isEmpty(config) || config.isEmpty() || config.getInt("is_open", 0) != 1) return;

        JSONObject gifts = config.getJSONObject("gifts");
        if (ObjectUtil.isEmpty(gifts) || gifts.isEmpty()) return;

        Member member = memberMapper.selectById(memberId);
        if (member == null || ObjectUtil.equal(member.getIsNewcomerGift(), 1)) return;

        Long recordCount = newcomerGiftRecordsMapper.selectCount(new QueryWrapper<NewcomerGiftRecords>().eq("member_id", memberId));
        if (recordCount != null && recordCount > 0) {
            Member updateMember = new Member();
            updateMember.setMemberId(memberId);
            updateMember.setIsNewcomerGift(1);
            memberMapper.updateById(updateMember);
            return;
        }

        Member updateMember = new Member();
        updateMember.setMemberId(memberId);
        updateMember.setIsNewcomerGift(1);
        memberMapper.updateById(updateMember);

        NewcomerGiftRecords records = new NewcomerGiftRecords();
        records.setMemberId(memberId);
        records.setGifts(gifts.toString());
        records.setCreateTime(DateUtils.currTime());
        newcomerGiftRecordsMapper.insert(records);

        Map<String, Object> vars = new HashMap<>();
        vars.put("from_type", "newcomer_gift");
        vars.put("memo", "新人礼");
        coreMemberService.asyncMemberGiftGrant(RequestUtils.defaultSiteId(), memberId, gifts, vars);
    }
}
