package com.niu.core.service.api.diy.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.diy_form.*;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLabel;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.enums.diy_form.DiyFormTypeEnum;
import com.niu.core.mapper.diy_form.*;
import com.niu.core.mapper.member.MemberLabelMapper;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.api.diy.IDiyFormService;
import com.niu.core.service.api.diy.param.DiyFormParam;
import com.niu.core.service.api.diy.vo.DiyFormInfoVo;
import com.niu.core.service.api.diy.vo.DiyFormRecordsDetailVo;
import com.niu.core.service.api.diy.vo.DiyFormRecordsInfoVo;
import com.niu.core.service.core.diy_form.ICoreDiyFormConfigService;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.core.service.api.diy.vo.DiyFormRecordsFieldsListVo;
import com.niu.core.service.core.diy_form.vo.DiyFormSubmitConfigInfoVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiyFormServiceImpl implements IDiyFormService {

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    DiyFormFieldsMapper diyFormFieldsMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    MemberLevelMapper memberLevelMapper;

    @Resource
    MemberLabelMapper memberLabelMapper;

    @Resource
    DiyFormRecordsMapper diyFormRecordsMapper;

    @Resource
    DiyFormRecordsFieldsMapper diyFormRecordsFieldsMapper;

    @Resource
    DiyFormSubmitConfigMapper diyFormSubmitConfigMapper;

    @Resource
    DiyFormWriteConfigMapper diyFormWriteConfigMapper;

    @Resource
    ICoreDiyFormConfigService coreDiyFormConfigService;

    @Resource
    ICoreDiyFormRecordsService coreDiyFormRecordsService;

    @Resource
    IDiyService diyService;

    @Resource
    ICoreSysConfigService systemConfigService;

    @Override
    public DiyFormInfoVo getInfo(Integer formId) {
        // 查询 DiyFormWriteConfig
        QueryWrapper<DiyFormWriteConfig> writeConfigWrapper = new QueryWrapper<>();
        writeConfigWrapper.eq("form_id", formId);
        DiyFormWriteConfig writeConfig = diyFormWriteConfigMapper.selectOne(writeConfigWrapper);

        // 查询表单信息
        QueryWrapper<DiyForm> formWrapper = new QueryWrapper<>();
        formWrapper.eq("form_id", formId)
                .eq("status", 1);
        DiyForm formInfo = diyFormMapper.selectOne(formWrapper);

        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, String>> error = new ArrayList<>();
        
        if (formInfo != null) {
            result.put("form_id", formInfo.getFormId());
            result.put("site_id", formInfo.getSiteId());
            result.put("page_title", formInfo.getPageTitle());
            result.put("title", formInfo.getTitle());
            result.put("type", formInfo.getType());
            result.put("status", formInfo.getStatus());
            result.put("template", formInfo.getTemplate());
            result.put("addon", formInfo.getAddon());
            result.put("remark", formInfo.getRemark());
            result.put("write_num", formInfo.getWriteNum());
            result.put("create_time", formInfo.getCreateTime() != null ? DateUtils.timestampToString(formInfo.getCreateTime()) : "");
            result.put("update_time", formInfo.getUpdateTime() != null ? DateUtils.timestampToString(formInfo.getUpdateTime()) : "");
            
            DiyFormTypeEnum diyFormTypeEnum = new DiyFormTypeEnum();
            JSONObject typeObj = diyFormTypeEnum.getType(formInfo.getType());
            result.put("type_name", (typeObj != null && typeObj.containsKey("title")) ? typeObj.getStr("title") : "");
            
            if (ObjectUtil.isNotEmpty(formInfo.getShare())) {
                result.put("share", formInfo.getShare());
            } else {
                String defaultShare = "{\"wechat\": {\"title\": \"" + (formInfo.getTitle() != null ? formInfo.getTitle() : "") + "\", \"desc\": \"\", \"url\": \"\"}, \"weapp\": {\"title\": \"" + (formInfo.getTitle() != null ? formInfo.getTitle() : "") + "\", \"desc\": \"\", \"url\": \"\"}}";
                result.put("share", defaultShare);
            }
            
            if (ObjectUtil.isNotEmpty(formInfo.getValue())) {
                try {
                    JSONObject valueObj = JSONUtil.parseObj(formInfo.getValue());
                    
                    if (valueObj.containsKey("value")) {
                        Object valueArrayObj = valueObj.get("value");
                        if (valueArrayObj instanceof JSONArray) {
                            JSONArray valueArray = (JSONArray) valueArrayObj;
                            for (int i = 0; i < valueArray.size(); i++) {
                                Object item = valueArray.get(i);
                                if (item instanceof JSONObject) {
                                    JSONObject component = (JSONObject) item;
                                    normalizeComponentTypes(component);
                                }
                            }
                        }
                    }
                    
                    result.put("value", valueObj.toString());
                } catch (Exception e) {
                    result.put("value", formInfo.getValue());
                }
            } else {
                result.put("value", "{\"global\":{\"topStatusBar\":{\"isShow\":false},\"bottomTabBar\":{\"isShow\":false}},\"value\":[]}");
            }

            // 检查会员相关限制
            Integer memberId = RequestUtils.memberId();
            if (writeConfig != null && memberId != null) {
                Map<String, String> errorMsg;
                errorMsg = checkMemberCanJoinOrNot(memberId, writeConfig);
                if (ObjectUtil.isNotEmpty(errorMsg)) {
                    error.add(errorMsg);
                }
                errorMsg = checkFormWriteTime(writeConfig);
                if (ObjectUtil.isNotEmpty(errorMsg)) {
                    error.add(errorMsg);
                }
                errorMsg = checkFormWriteLimitNum(formId, writeConfig);
                if (ObjectUtil.isNotEmpty(errorMsg)) {
                    error.add(errorMsg);
                }
                errorMsg = checkMemberWriteLimitNum(memberId, formId, writeConfig);
                if (ObjectUtil.isNotEmpty(errorMsg)) {
                    error.add(errorMsg);
                }
            }
        } else {
            result.put("value", createDefaultValueStructure());
            
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("title", "当前表单无法查看");
            errorMap.put("type", "表单状态");
            errorMap.put("desc", "该表单已关闭");
            error.add(errorMap);
        }
        
        result.put("error", error);
        
        DiyFormInfoVo vo = new DiyFormInfoVo();
        vo.setFormId((Integer) result.get("form_id"));
        vo.setSiteId((Integer) result.get("site_id"));
        vo.setPageTitle((String) result.get("page_title"));
        vo.setTitle((String) result.get("title"));
        vo.setType((String) result.get("type"));
        vo.setStatus((Integer) result.get("status"));
        vo.setTemplate((String) result.get("template"));
        vo.setValue((String) result.get("value"));  // value 现在是字符串
        vo.setShare((String) result.get("share"));   // share 现在是字符串
        vo.setAddon((String) result.get("addon"));
        vo.setRemark((String) result.get("remark"));
        vo.setWriteNum((Integer) result.get("write_num"));
        vo.setError(error);
        
        return vo;
    }
    
    /**
     * 创建默认的 value 结构
     */
    private Map<String, Object> createDefaultValueStructure() {
        Map<String, Object> defaultValue = new LinkedHashMap<>();
        Map<String, Object> globalObj = new LinkedHashMap<>();
        Map<String, Object> topStatusBar = new LinkedHashMap<>();
        topStatusBar.put("isShow", false);
        Map<String, Object> bottomTabBar = new LinkedHashMap<>();
        bottomTabBar.put("isShow", false);
        globalObj.put("topStatusBar", topStatusBar);
        globalObj.put("bottomTabBar", bottomTabBar);
        defaultValue.put("global", globalObj);
        defaultValue.put("value", new ArrayList<>());
        return defaultValue;
    }

    /**
     * 规范化组件字段类型
     * 
     * @param component 组件JSON对象
     */
    private void normalizeComponentTypes(JSONObject component) {
        // 将 isOpen 字段从数字转为布尔值
        if (component.containsKey("isOpen")) {
            Object isOpenObj = component.get("isOpen");
            if (isOpenObj instanceof Number) {
                component.set("isOpen", ((Number) isOpenObj).intValue() == 1);
            } else if (isOpenObj instanceof String) {
                component.set("isOpen", "1".equals(isOpenObj) || "true".equalsIgnoreCase((String) isOpenObj));
            }
        }
        
        // 为 field.cache 设置默认值 true
        if (component.containsKey("field")) {
            Object fieldObj = component.get("field");
            if (fieldObj instanceof JSONObject) {
                JSONObject field = (JSONObject) fieldObj;
                if (!field.containsKey("cache")) {
                    field.set("cache", true);
                }
            }
        }
        
        // 递归处理嵌套对象
        for (String key : component.keySet()) {
            Object value = component.get(key);
            if (value instanceof JSONObject) {
                normalizeComponentTypes((JSONObject) value);
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (int i = 0; i < array.size(); i++) {
                    Object item = array.get(i);
                    if (item instanceof JSONObject) {
                        normalizeComponentTypes((JSONObject) item);
                    }
                }
            }
        }
    }

    @Override
    public Map<String, String> checkMemberCanJoinOrNot(Integer memberId, DiyFormWriteConfig diyFormWriteConfig) {
        Map<String, String> error = new HashMap<>();
        // 查询会员信息
        QueryWrapper<Member> memberWrapper = new QueryWrapper<>();
        memberWrapper.eq("member_id", memberId);
        Member memberInfo = memberMapper.selectOne(memberWrapper);
        if (memberInfo == null) {
            throw new ApiException("MEMBER_NOT_EXIST");
        }

        String joinMemberType = diyFormWriteConfig.getJoinMemberType();
        switch (joinMemberType) {
            case "all_member":
                break;
            case "selected_member_level":
                List<Integer> levelIds = JSONUtil.toList(JSONUtil.parseArray(diyFormWriteConfig.getLevelIds()), Integer.class);
                if (!levelIds.contains(memberInfo.getMemberLevel())) {
                    QueryWrapper<MemberLevel> levelWrapper = new QueryWrapper<>();
                    levelWrapper.in("level_id", levelIds);
                    List<String> levelNames = memberLevelMapper.selectObjs(levelWrapper.select("level_name")).stream()
                            .map(Object::toString)
                            .toList();
                    error.put("title", "当前表单无法查看");
                    error.put("type", "允许填写用户");
                    error.put("desc", "该表单已设置仅限“" + String.join("、", levelNames) + "等级”的用户填写");
                }
                break;
            case "selected_member_label":
                JSONArray memberLabels = JSONUtil.parseArray(memberInfo.getMemberLabel());
                if (memberLabels == null) {
                    memberLabels = new JSONArray();
                }
                List<Integer> labelIds = JSONUtil.toList(JSONUtil.parseArray(diyFormWriteConfig.getLabelIds()), Integer.class);
                boolean hasIntersection = false;
                for (Integer labelId : labelIds) {
                    if (memberLabels.contains(labelId)) {
                        hasIntersection = true;
                        break;
                    }
                }
                if (!hasIntersection) {
                    QueryWrapper<MemberLabel> labelWrapper = new QueryWrapper<>();
                    labelWrapper.in("label_id", labelIds);
                    List<String> labelNames = memberLabelMapper.selectObjs(labelWrapper.select("label_name")).stream()
                            .map(Object::toString)
                            .toList();
                    error.put("title", "当前表单无法查看");
                    error.put("type", "允许填写用户");
                    error.put("desc", "该表单已设置仅限“" + String.join("、", labelNames) + "标签”的用户填写");
                }
                break;
            default:
                throw new CommonException("未知的会员类型: " + joinMemberType);
        }
        return error;
    }

    @Override
    public Map<String, String> checkMemberWriteLimitNum(Integer memberId, Integer formId, DiyFormWriteConfig diyFormWriteConfig) {

        Map<String, String> error = new HashMap<>();
        String memberWriteType = diyFormWriteConfig.getMemberWriteType();

        switch (memberWriteType) {
            case "no_limit":
                break;
            case "diy":
                JSONObject memberWriteRule = JSONUtil.parseObj(diyFormWriteConfig.getMemberWriteRule());
                String timeUnit =  memberWriteRule.getStr("time_unit");
                String timeText;
                switch (timeUnit) {
                    case "day":
                        timeText = "天";
                        break;
                    case "week":
                        timeText = "周";
                        break;
                    case "month":
                        timeText = "月";
                        break;
                    case "year":
                        timeText = "年";
                        break;
                    default:
                        throw new CommonException("未知的时间单位: " + timeUnit);
                }

                int timeValue = (int) memberWriteRule.get("time_value");
                int numLimit = (int) memberWriteRule.get("num");

                LocalDateTime startTime = LocalDateTime.now();
                switch (timeUnit) {
                    case "day":
                        startTime = startTime.minus(timeValue, ChronoUnit.DAYS);
                        break;
                    case "week":
                        startTime = startTime.minus(timeValue, ChronoUnit.WEEKS);
                        break;
                    case "month":
                        startTime = startTime.minus(timeValue, ChronoUnit.MONTHS);
                        break;
                    case "year":
                        startTime = startTime.minus(timeValue, ChronoUnit.YEARS);
                        break;
                }

                QueryWrapper<DiyFormRecords> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("form_id", formId)
                        .eq("member_id", memberId)
                        .ge("create_time", startTime);
                Long count = diyFormRecordsMapper.selectCount(queryWrapper);

                if (count >= numLimit) {
                    error.put("title", "您的填写次数已达上限");
                    error.put("type", "允许填写次数(每人)");
                    error.put("desc", "该表单已设置“每人每" + timeValue + timeText + "可填写" + numLimit + "次”");
                }
                break;
            default:
                throw new CommonException("未知的会员填写类型: " + memberWriteType);
        }
        return error;
    }

    @Override
    public Map<String, String> checkFormWriteLimitNum(Integer formId, DiyFormWriteConfig diyFormWriteConfig) {
        Map<String, String> error = new HashMap<>();
        String formWriteType = diyFormWriteConfig.getFormWriteType();

        switch (formWriteType) {
            case "no_limit":
                break;
            case "diy":
                JSONObject formWriteRule = JSONUtil.parseObj(diyFormWriteConfig.getFormWriteRule());
                String timeUnit = formWriteRule.getStr("time_unit");
                String timeText;
                switch (timeUnit) {
                    case "day":
                        timeText = "天";
                        break;
                    case "week":
                        timeText = "周";
                        break;
                    case "month":
                        timeText = "月";
                        break;
                    case "year":
                        timeText = "年";
                        break;
                    default:
                        throw new CommonException("未知的时间单位: " + timeUnit);
                }

                int timeValue = (int) formWriteRule.get("time_value");
                int numLimit = (int) formWriteRule.get("num");

                LocalDateTime startTime = LocalDateTime.now();
                switch (timeUnit) {
                    case "day":
                        startTime = startTime.minus(timeValue, ChronoUnit.DAYS);
                        break;
                    case "week":
                        startTime = startTime.minus(timeValue, ChronoUnit.WEEKS);
                        break;
                    case "month":
                        startTime = startTime.minus(timeValue, ChronoUnit.MONTHS);
                        break;
                    case "year":
                        startTime = startTime.minus(timeValue, ChronoUnit.YEARS);
                        break;
                }

                QueryWrapper<DiyFormRecords> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("form_id", formId)
                        .ge("create_time", startTime);
                Long count = diyFormRecordsMapper.selectCount(queryWrapper);

                if (count >= numLimit) {
                    error.put("title", "表单总填写次数已达上限");
                    error.put("type", "允许填写次数(总)");
                    error.put("desc", "该表单已设置“每" + timeValue + timeText + "可填写" + numLimit + "次”");
                }
                break;
            default:
                throw new CommonException("未知的表单填写类型: " + formWriteType);
        }
        return error;
    }

    @Override
    public Map<String, String> checkFormWriteTime(DiyFormWriteConfig diyFormWriteConfig) {
        Map<String, String> error = new HashMap<>();
        String timeLimitType = diyFormWriteConfig.getTimeLimitType();

        switch (timeLimitType) {
            case "no_limit":
                break;
            case "specify_time":
                JSONObject timeLimitRule = JSONUtil.parseObj(diyFormWriteConfig.getTimeLimitRule());
                JSONArray specifyTime = timeLimitRule.getJSONArray("specify_time");
                if (specifyTime != null && specifyTime.size() == 2) {
                    long currentTime = DateUtils.currTime();
                    if (currentTime < specifyTime.getLong(0) || currentTime > specifyTime.getLong(1)) {
                        String start = DateUtils.timestampToString(specifyTime.getLong(0));
                        String end = DateUtils.timestampToString(specifyTime.getLong(1));
                        error.put("title", "当前时间无法查看");
                        error.put("type", "允许查看时间");
                        error.put("desc", "该表单已设置“" + start + "-" + end + "”可查看");
                    }
                }
                break;
            case "open_day_time":
                JSONObject timeLimitRuleOpenDay = JSONUtil.parseObj(diyFormWriteConfig.getTimeLimitRule());
                JSONArray openDayTime = timeLimitRuleOpenDay.getJSONArray("open_day_time");
                if (openDayTime != null && openDayTime.size() == 2) {
                    LocalDate today = LocalDate.now();
                    LocalDateTime startTime = today.atStartOfDay().plusSeconds(openDayTime.getLong(0));
                    LocalDateTime endTime = today.atStartOfDay().plusSeconds(openDayTime.getLong(1));
                    LocalDateTime now = LocalDateTime.now();
                    if (now.isBefore(startTime) || now.isAfter(endTime)) {
                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                        String startStr = startTime.format(timeFormatter);
                        String endStr = endTime.format(timeFormatter);
                        error.put("title", "当前时间无法查看");
                        error.put("type", "允许查看时间");
                        error.put("desc", "该表单已设置“每天" + startStr + "-" + endStr + "”可查看");
                    }
                }
                break;
            default:
                throw new CommonException("未知的时间限制类型: " + timeLimitType);
        }
        return error;
    }

    @Override
    public Integer addRecord(DiyFormRecordsParam diyFormRecordsParam) {
        diyFormRecordsParam.setSiteId(RequestUtils.siteId());
        diyFormRecordsParam.setMemberId(RequestUtils.memberId());

        // 检查表单是否存在且已开启
        QueryWrapper<DiyForm> formQueryWrapper = new QueryWrapper<>();
        formQueryWrapper.eq("form_id", diyFormRecordsParam.getFormId());
        DiyForm formInfo = diyFormMapper.selectOne(formQueryWrapper);
        if (formInfo == null) {
            throw new ApiException("DIY_FORM_NOT_EXIST");
        }
        if (formInfo.getStatus() == 0) {
            throw new ApiException("DIY_FORM_NOT_OPEN");
        }

        // 查询表单填写配置
        QueryWrapper<DiyFormWriteConfig> writeConfigQueryWrapper = new QueryWrapper<>();
        writeConfigQueryWrapper.eq("form_id", diyFormRecordsParam.getFormId());
        DiyFormWriteConfig writeConfig = diyFormWriteConfigMapper.selectOne(writeConfigQueryWrapper);

        if (writeConfig != null) {
            // 检查会员是否可以参与
            Map<String, String> canJoinError = checkMemberCanJoinOrNot(RequestUtils.memberId(), writeConfig);
            if (!canJoinError.isEmpty()) {
                throw new ApiException(canJoinError.get("desc"));
            }

            // 检查表单填写时间
            Map<String, String> timeError = checkFormWriteTime(writeConfig);
            if (!timeError.isEmpty()) {
                throw new ApiException(timeError.get("desc"));
            }

            // 检查表单总填写次数限制
            Map<String, String> formLimitError = checkFormWriteLimitNum(diyFormRecordsParam.getFormId(), writeConfig);
            if (!formLimitError.isEmpty()) {
                throw new ApiException(formLimitError.get("desc"));
            }

            // 检查会员填写次数限制
            Map<String, String> memberLimitError = checkMemberWriteLimitNum(RequestUtils.memberId(), diyFormRecordsParam.getFormId(), writeConfig);
            if (!memberLimitError.isEmpty()) {
                throw new ApiException(memberLimitError.get("desc"));
            }
        }

        // 调用核心服务添加记录
        return coreDiyFormRecordsService.add(diyFormRecordsParam);
    }

    @Override
    public DiyFormRecordsInfoVo getResult(Integer recordId) {
        DiyFormRecords diyFormRecords = diyFormRecordsMapper.selectOne(new QueryWrapper<DiyFormRecords>().eq("record_id", recordId).eq("site_id", RequestUtils.siteId()).eq("member_id", RequestUtils.memberId()));
        if(ObjectUtil.isEmpty(diyFormRecords)) throw new ApiException("表单记录不存在");
        DiyFormRecordsInfoVo vo = new DiyFormRecordsInfoVo();
        BeanUtil.copyProperties(diyFormRecords, vo);
        DiyFormSubmitConfig diyFormSubmitConfig = diyFormSubmitConfigMapper.selectOne(new QueryWrapper<DiyFormSubmitConfig>().eq("form_id", vo.getFormId()));
        DiyFormSubmitConfigInfoVo configInfoVo = new DiyFormSubmitConfigInfoVo();
        if(ObjectUtil.isNotEmpty(diyFormSubmitConfig))
        {
            BeanUtil.copyProperties(diyFormSubmitConfig, configInfoVo);
        }
        vo.setDiyFormSubmitConfig(configInfoVo);
        return vo;
    }

    @Override
    public DiyFormRecordsDetailVo getFormRecordInfo(Integer recordId) {
        DiyFormRecords diyFormRecords = diyFormRecordsMapper.selectOne(
            new QueryWrapper<DiyFormRecords>()
                .select("record_id", "form_id", "create_time")
                .eq("record_id", recordId)
                .eq("member_id", RequestUtils.memberId())
        );
        
        if (ObjectUtil.isEmpty(diyFormRecords)) {
            throw new ApiException("表单记录不存在");
        }
        
        DiyFormRecordsDetailVo vo = new DiyFormRecordsDetailVo();
        BeanUtil.copyProperties(diyFormRecords, vo);

        List<DiyFormRecordsFields> list = diyFormRecordsFieldsMapper.selectList(
            new QueryWrapper<DiyFormRecordsFields>()
                .select("id", "form_id", "form_field_id", "record_id", "field_key", 
                       "field_type", "field_name", "field_value", "field_required", 
                       "field_unique", "privacy_protection", "update_num", "update_time")
                .eq("record_id", vo.getRecordId())
        );
        
        if (ObjectUtil.isNotEmpty(list)) {
            List<DiyFormRecordsFieldsListVo> volist = new ArrayList<>();
            for (DiyFormRecordsFields item : list) {
                DiyFormRecordsFieldsListVo diyFormRecordsFieldsListVo = new DiyFormRecordsFieldsListVo();
                BeanUtils.copyProperties(item, diyFormRecordsFieldsListVo);
                volist.add(diyFormRecordsFieldsListVo);
            }
            vo.setRecordsFieldList(volist);
        }
        
        // 表单组件配置
        if (ObjectUtil.isNotEmpty(diyFormRecords.getFormId())) {
            DiyForm formInfo = diyFormMapper.selectOne(
                new QueryWrapper<DiyForm>()
                    .select("value")
                    .eq("form_id", diyFormRecords.getFormId())
            );
            
            if (ObjectUtil.isNotEmpty(formInfo) && ObjectUtil.isNotEmpty(formInfo.getValue())) {
                try {
                    JSONObject formValueJson = JSONUtil.parseObj(formInfo.getValue());
                    JSONArray diyComponentArray = formValueJson.getJSONArray("value");
                    if (ObjectUtil.isNotEmpty(diyComponentArray)) {
                        vo.setDiyComponentList(diyComponentArray.toString());
                    } else {
                        vo.setDiyComponentList("[]");
                    }
                } catch (Exception e) {
                    vo.setDiyComponentList("[]");
                }
            }
        }
        
        return vo;
    }
}
