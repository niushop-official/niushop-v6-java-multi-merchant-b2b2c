package com.niu.core.service.api.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.marketing.NewcomerGiftRecords;
import com.niu.core.mapper.marketing.NewcomerGiftRecordsMapper;
import com.niu.core.service.api.marketing.INewcomerGiftRecordsService;
import com.niu.core.service.core.member.ICoreMemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class NewcomerGiftRecordsServiceImpl implements INewcomerGiftRecordsService {

    @Resource
    private NewcomerGiftRecordsMapper newcomerGiftRecordsMapper;

    @Resource
    private ICoreMemberService coreMemberService;

    @Override
    public JSONObject getMyRecords() {
        NewcomerGiftRecords info = newcomerGiftRecordsMapper.selectOne(
                new QueryWrapper<NewcomerGiftRecords>()
                        .select("record_id", "gifts", "create_time")
                        .eq("member_id", RequestUtils.memberId())
                        .last("limit 1")
        );

        if (info == null) return new JSONObject();

        JSONObject result = new JSONObject();
        result.set("record_id", info.getRecordId());
        result.set("gifts", ObjectUtil.isEmpty(info.getGifts()) ? new JSONObject() : JSONUtil.parseObj(info.getGifts()));
        result.set("create_time", info.getCreateTime());
        if (ObjectUtil.isNotEmpty(info.getGifts())) {
            result.set("gifts_content", coreMemberService.getGiftContent(RequestUtils.siteId(), JSONUtil.parseObj(info.getGifts()), "wap"));
        }
        return result;
    }
}
