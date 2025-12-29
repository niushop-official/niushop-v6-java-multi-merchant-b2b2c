package com.niu.core.listener.diy_form.site;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.entity.diy_form.DiyFormFields;
import com.niu.core.entity.diy_form.DiyFormRecords;
import com.niu.core.entity.diy_form.DiyFormRecordsFields;
import com.niu.core.entity.member.Member;
import com.niu.core.enums.diy_form.DiyFormTypeEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.diy_form.DiyFormFieldsMapper;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.diy_form.DiyFormRecordsFieldsMapper;
import com.niu.core.mapper.diy_form.DiyFormRecordsMapper;
import com.niu.core.mapper.member.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表单填写记录导出数据监听器
 */
@Component
@EventCallback("core")
public class DiyFormRecordsExportDataListener extends ExportDataEventDefiner {

    @Resource
    private DiyFormRecordsMapper diyFormRecordsMapper;

    @Resource
    private DiyFormFieldsMapper diyFormFieldsMapper;

    @Resource
    private DiyFormRecordsFieldsMapper diyFormRecordsFieldsMapper;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private DiyFormMapper diyFormMapper;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent exportDataEvent) {
        JSONArray result = new JSONArray();
        ExportDataEventResult exportDataEventResult = new ExportDataEventResult();

        if (!"site_diy_form_records".equals(exportDataEvent.getType())) {
            exportDataEventResult.setResultData(result);
            return exportDataEventResult;
        }
        JSONObject whereJson = exportDataEvent.getWhere();
        Integer siteId = exportDataEvent.getSiteId();

        if (whereJson == null || !whereJson.containsKey("form_id")) {
            exportDataEventResult.setResultData(result);
            return exportDataEventResult;
        }
        Integer formId = whereJson.getInt("form_id");

        QueryWrapper<DiyFormRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("form_id", formId);
        if ((whereJson.get("create_time") instanceof JSONArray createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", StringUtils.jsonArrayToStringArray(createTimes));
        }
        queryWrapper.orderByDesc("create_time");
        PageParam pageParam = exportDataEvent.getPageParam();
        List<DiyFormRecords> recordsList = new ArrayList<>();
        if (pageParam.getPage() > 0 && pageParam.getLimit() > 0) {
            Page<DiyFormRecords> page = diyFormRecordsMapper.selectPage(
                    new Page<>(pageParam.getPage(), pageParam.getLimit()), 
                    queryWrapper
            );
            recordsList = page.getRecords();
        } else {
            recordsList = diyFormRecordsMapper.selectList(queryWrapper);
        }

        if (recordsList.isEmpty()) {
            throw new CommonException("表单为空");
        }

        DiyForm diyForm = diyFormMapper.selectById(formId);
        List<String> fieldKeyList = diyFormFieldsMapper.selectList(
                new QueryWrapper<DiyFormFields>()
                        .eq("form_id", formId)
                        .eq("site_id", siteId)
                        .orderByAsc("field_id")
        ).stream()
                .map(DiyFormFields::getFieldKey)
                .collect(Collectors.toList());
        
        List<Integer> recordIds = recordsList.stream()
                .map(DiyFormRecords::getRecordId)
                .collect(Collectors.toList());

        List<Integer> memberIds = recordsList.stream()
                .map(DiyFormRecords::getMemberId)
                .filter(ObjectUtil::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, Member> memberMap = new HashMap<>();
        if (!memberIds.isEmpty()) {
            List<Member> members = memberMapper.selectBatchIds(memberIds);
            memberMap = members.stream()
                    .collect(Collectors.toMap(Member::getMemberId, m -> m));
        }

        Map<Integer, Map<String, String>> recordFieldsMap = new HashMap<>();
        if (!recordIds.isEmpty()) {
            List<DiyFormRecordsFields> recordsFieldsList = diyFormRecordsFieldsMapper.selectList(
                    new QueryWrapper<DiyFormRecordsFields>()
                            .in("record_id", recordIds)
                            .eq("form_id", formId)
            );

            for (DiyFormRecordsFields field : recordsFieldsList) {
                recordFieldsMap.computeIfAbsent(field.getRecordId(), k -> new HashMap<>())
                        .put(field.getFieldKey(), field.getFieldValue());
            }
        }

        DiyFormTypeEnum diyFormTypeEnum = new DiyFormTypeEnum();
        String typeName = "";
        if (diyForm != null && ObjectUtil.isNotEmpty(diyForm.getType())) {
            JSONObject typeObj = diyFormTypeEnum.getType(diyForm.getType());
            if (typeObj != null && typeObj.containsKey("name")) {
                typeName = typeObj.getStr("name");
            }
        }

        Object keyword = whereJson.get("keyword");
        final Map<Integer, Member> finalMemberMap = memberMap;
        if (ObjectUtil.isNotEmpty(keyword)) {
            String keywordStr = keyword.toString();
            recordsList = recordsList.stream()
                    .filter(record -> {
                        Member member = finalMemberMap.get(record.getMemberId());
                        if (member == null) return false;
                        return (ObjectUtil.isNotEmpty(member.getNickname()) && member.getNickname().contains(keywordStr))
                                || (ObjectUtil.isNotEmpty(member.getMobile()) && member.getMobile().contains(keywordStr));
                    })
                    .collect(Collectors.toList());
        }

        final String finalTypeName = typeName;
        for (DiyFormRecords record : recordsList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("record_id", record.getRecordId());
            jsonObject.set("create_time", DateUtils.timestampToString(record.getCreateTime()));
            jsonObject.set("page_title", diyForm != null ? diyForm.getPageTitle() : "");
            jsonObject.set("type_name", finalTypeName);
            jsonObject.set("form_id", record.getFormId());
            Member member = memberMap.get(record.getMemberId());
            if (member != null) {
                jsonObject.set("nickname", ObjectUtil.isNotEmpty(member.getNickname()) ? member.getNickname() : "");
                jsonObject.set("mobile", ObjectUtil.isNotEmpty(member.getMobile()) ? member.getMobile() + "\t" : "");
            } else {
                jsonObject.set("nickname", "");
                jsonObject.set("mobile", "");
            }
            Map<String, String> fieldsData = recordFieldsMap.get(record.getRecordId());
            for (String fieldKey : fieldKeyList) {
                String fieldValue = "";
                if (fieldsData != null && fieldsData.containsKey(fieldKey)) {
                    fieldValue = fieldsData.get(fieldKey);
                    if (ObjectUtil.isNotEmpty(fieldValue)) {
                        fieldValue = fieldValue + "\t";
                    }
                }
                jsonObject.set(fieldKey, fieldValue);
            }

            result.add(jsonObject);
        }

        exportDataEventResult.setResultData(result);
        return exportDataEventResult;
    }
}
