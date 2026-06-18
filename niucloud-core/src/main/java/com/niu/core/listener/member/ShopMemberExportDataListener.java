package com.niu.core.listener.member;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.enums.shop.ShopMemberIsFollowEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.core.service.site.shop.vo.SiteShopMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EventCallback("shop")
public class ShopMemberExportDataListener extends ExportDataEventDefiner {

    @Resource
    SiteShopMemberMapper siteShopMemberMapper;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if (!"shop_member".equals(event.getType())) {
            return new ExportDataEventResult();
        }
        MPJQueryWrapper<SiteShopMember> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ssm");
        queryWrapper.select("ssm.id,ssm.site_id,ssm.member_id, ssm.is_follow, ssm.first_consum_time, ssm.last_consum_time, ssm.shop_member_label, m.member_no, m.mobile, m.nickname, m.username, m.headimg, m.register_channel");
        queryWrapper.leftJoin("?_member m ON m.`member_id` = ssm.member_id".replace("?_", "ns_"));

        queryWrapper.eq("ssm.site_id", RequestUtils.siteId());
        JSONObject where = event.getWhere();
        if (where != null) {
            Object keyword = where.get("keyword");
            // 查询条件
            if (ObjectUtil.isNotEmpty(keyword)) {
                queryWrapper.and(i -> i.like("m.member_no", keyword).or()
                        .like("m.username", keyword).or()
                        .like("m.nickname", keyword).or()
                        .like("m.mobile", keyword));
            }
            Object isFollow = where.get("is_follow");
            if (ObjectUtil.isNotEmpty(isFollow)) {
                queryWrapper.eq("ssm.is_follow", isFollow);
            }
            Object shopMemberLabel = where.get("shop_member_label");
            if (ObjectUtil.isNotEmpty(shopMemberLabel)) {
                queryWrapper.eq("ssm.shop_member_label", shopMemberLabel);
            }
            Object registerChannel = where.get("register_channel");
            if (ObjectUtil.isNotEmpty(registerChannel)) {
                queryWrapper.eq("m.register_channel", registerChannel);
            }
            if ((where.get("create_time") instanceof JSONArray createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
                QueryMapperUtils.buildByTime(queryWrapper, "ssm.create_time", StringUtils.jsonArrayToStringArray(createTimes));
            }
        }
        PageParam pageParam = event.getPageParam();
        List<SiteShopMemberListVo> list = new ArrayList<>();
        if (pageParam.getPage() > 0 && pageParam.getLimit() > 0) {
            list = siteShopMemberMapper.selectJoinList(SiteShopMemberListVo.class, queryWrapper);
        } else {
            list = siteShopMemberMapper.selectJoinList(SiteShopMemberListVo.class, queryWrapper);
        }

        ExportDataEventResult result = new ExportDataEventResult();

        JSONArray jsonArray = new JSONArray();

        for (SiteShopMemberListVo vo : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("nickname", vo.getNickname())
                    .set("mobile", vo.getMobile())
                    .set("comsum_num", vo.getComsumNum())
                    .set("comsum_money", vo.getComsumMoney())
                    .set("register_channel_name", vo.getRegisterChannelName())
                    .set("first_consum_time", DateUtils.timestampToString(vo.getFirstConsumTime()))
                    .set("last_consum_time", DateUtils.timestampToString(vo.getLastConsumTime()))
                    .set("is_follow_name", ShopMemberIsFollowEnum.getNameByStatus(vo.getIsFollow()));
            jsonArray.add(jsonObject);
        }
        result.setResultData(jsonArray);
        return result;
    }
}
