package com.niu.core.listener.sys;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.enums.common.SexEnum;
import com.niu.core.enums.member.MemberRegisterChannelEnum;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@EventCallback("core")
@Component
public class MemberExportDataListener extends ExportDataEventDefiner {

    @Resource
    MemberMapper memberMapper;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent exportDataEvent) {
        JSONArray result = new JSONArray();
        ExportDataEventResult exportDataEventResult = new ExportDataEventResult();
        if (exportDataEvent.getType().equals("member")) {
            JSONObject whereJson = exportDataEvent.getWhere();
            QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_del",0);
            if(whereJson != null){


                Object keyword = whereJson.get("keyword");
                if (ObjectUtil.isNotEmpty(keyword)) {
                    queryWrapper.and(i -> i.like("member_no", keyword).or()
                            .like("username", keyword).or()
                            .like("nickname", keyword).or()
                            .like("mobile", keyword));
                }

                Object member_level = whereJson.get("member_level");
                if (ObjectUtil.isNotEmpty(member_level)) {
                    queryWrapper.eq("member_level", member_level);
                }
                Object register_channel = whereJson.get("register_channel");
                if (ObjectUtil.isNotEmpty(register_channel)) {
                    queryWrapper.eq("register_channel", register_channel);
                }
                Object member_label = whereJson.get("member_label");
                if (ObjectUtil.isNotEmpty(member_label)) {
                    queryWrapper.like("member_label", member_label);
                }
                Object register_type = whereJson.get("register_type");
                if (ObjectUtil.isNotEmpty(register_type)) {
                    queryWrapper.eq("register_type", register_type);
                }

                if ((whereJson.get("create_time") instanceof String[] createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
                    QueryMapperUtils.buildByTime(queryWrapper, "create_time", createTimes);
                }
            }
            PageParam pageParam = exportDataEvent.getPageParam();
            List<Member> list = new ArrayList<>();
            if(pageParam.getPage() > 0 && pageParam.getLimit() > 0){
                list = memberMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper).getRecords();
            }else{
                list = memberMapper.selectList(queryWrapper);
            }
            for(Member vo : list){
                JSONObject jsonObject = new JSONObject();

                jsonObject.set("nickname",vo.getNickname())
                        .set("mobile",vo.getMobile())
                        .set("sex_name", SexEnum.getNameBySex(vo.getSex()))
                        .set("birthday",vo.getBirthday())
                        .set("point",vo.getPoint())
                        .set("balance",vo.getBalance())
                        .set("money", vo.getMoney())
                        .set("growth",vo.getGrowth())
                        .set("commission", vo.getCommission())
                        .set("register_channel_name", MemberRegisterChannelEnum.getNameByType(vo.getRegisterChannel()))
                        .set("status_name", StatusEnum.getNameByStatus(vo.getStatus()))
                        .set("create_time",DateUtils.timestampToString(vo.getCreateTime()))
                        .set("last_visit_time",DateUtils.timestampToString(vo.getLastConsumTime()));

                result.add(jsonObject);
            }
        }
        exportDataEventResult.setResultData(result);
        return exportDataEventResult;
    }
}
