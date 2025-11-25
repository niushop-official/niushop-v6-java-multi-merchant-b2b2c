package com.niu.core.service.core.diy_form.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.diy_form.*;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLabel;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.mapper.diy_form.*;
import com.niu.core.mapper.member.MemberLabelMapper;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsFieldsListVo;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsInfoVo;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 万能表单填写记录实现
 */
@Service
public class CoreDiyFormRecordsServiceImpl implements ICoreDiyFormRecordsService {

    @Resource
    DiyFormRecordsMapper diyFormRecordsMapper;

    @Resource
    DiyFormRecordsFieldsMapper diyFormRecordsFieldsMapper;

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    DiyFormFieldsMapper diyFormFieldsMapper;
    @Resource
    DiyFormWriteConfigMapper diyFormWriteConfigMapper;
    @Resource
    MemberMapper memberMapper;
    @Resource
    MemberLevelMapper memberLevelMapper;
    @Resource
    SiteShopMemberMapper shopMemberMapper;
    @Resource
    MemberLabelMapper memberLabelMapper;

    @Override
    public PageResult<DiyFormRecordsListVo> page(PageParam pageParam, DiyFormRecordsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<DiyFormRecords> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("ndfr.record_id, ndfr.site_id, ndfr.form_id, ndfr.value, ndfr.member_id, ndfr.relate_id, ndfr.create_time, nm.username, nm.member_no, nm.mobile, nm.nickname, nm.headimg")
                .setAlias("ndfr")
                .leftJoin("?_member nm ON ndfr.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix));
        if (ObjectUtil.isNotEmpty(searchParam.getFormId())) {
            queryWrapper.eq("ndfr.form_id", searchParam.getFormId());
        }
        if(ObjectUtil.isNotEmpty(searchParam.getCreateTime())){
            QueryMapperUtils.buildByTime(queryWrapper, "ndfr.create_time", searchParam.getCreateTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("nm.nickname", searchParam.getKeyword()))
                    .or(j -> j.like("nm.username", searchParam.getKeyword()))
                    .or(j -> j.like("nm.member_no", searchParam.getKeyword()))
                    .or(j -> j.like("nm.mobile", searchParam.getKeyword()))
            );
        }

        queryWrapper.orderByDesc("ndfr.create_time");
        IPage<DiyFormRecordsListVo> iPage = diyFormRecordsMapper.selectJoinPage(new Page<>(page, limit), DiyFormRecordsListVo.class, queryWrapper);

        for (DiyFormRecordsListVo vo : iPage.getRecords()) {
            List<DiyFormRecordsFields> list = diyFormRecordsFieldsMapper.selectList(new QueryWrapper<DiyFormRecordsFields>().eq("record_id", vo.getRecordId()));

            Member memberVo = new Member();
            BeanUtils.copyProperties(vo, memberVo);
            vo.setMember(memberVo);
            if (ObjectUtil.isNotEmpty(list)) {
                Map<String, DiyFormRecordsFieldsListVo> map = new HashMap<>();
                for (DiyFormRecordsFields item : list) {
                    DiyFormRecordsFieldsListVo diyFormRecordsFieldsListVo = new DiyFormRecordsFieldsListVo();
                    BeanUtils.copyProperties(item, diyFormRecordsFieldsListVo);
                    map.put(item.getFieldKey(), diyFormRecordsFieldsListVo);
                }
                vo.setRecordsFieldList(map);
            }
        }
        return PageResult.build(iPage);
    }

    @Override
    public DiyFormRecordsInfoVo info(Integer id) {
        DiyFormRecords model = diyFormRecordsMapper.selectOne(
                new QueryWrapper<DiyFormRecords>()
                        .eq("record_id", id));


        Assert.notNull(model, "万能表单填写记录不存在");

        DiyFormRecordsInfoVo vo = new DiyFormRecordsInfoVo();
        BeanUtils.copyProperties(model, vo);
        List<DiyFormRecordsFields> list = diyFormRecordsFieldsMapper.selectList(new QueryWrapper<DiyFormRecordsFields>().eq("record_id", vo.getRecordId()));
        if (ObjectUtil.isNotEmpty(list)) {
            Map<String, DiyFormRecordsFieldsListVo> map = new HashMap<>();
            for (DiyFormRecordsFields item : list) {
                DiyFormRecordsFieldsListVo diyFormRecordsFieldsListVo = new DiyFormRecordsFieldsListVo();
                BeanUtils.copyProperties(item, diyFormRecordsFieldsListVo);
                map.put(item.getFieldKey(), diyFormRecordsFieldsListVo);
            }
            vo.setRecordsFieldList(map);
        }
        return vo;
    }

    @Override
    @Transactional
    public Integer add(DiyFormRecordsParam addParam) {

        DiyForm diyForm = diyFormMapper.selectOne(new QueryWrapper<DiyForm>().eq("form_id", addParam.getFormId()));
        if (diyForm == null) return null;

        if (diyForm.getStatus() == 0) return null;

        DiyFormRecords model = new DiyFormRecords();
        BeanUtils.copyProperties(addParam, model);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        diyFormRecordsMapper.insert(model);
        List<DiyFormFields> list = diyFormFieldsMapper.selectList(new QueryWrapper<DiyFormFields>().eq("form_id", addParam.getFormId()));
        Map<String, DiyFormFields> fieldList = new HashMap<>();
        if (ObjectUtil.isNotEmpty(list)) {

            for (DiyFormFields item : list) {
                fieldList.put(item.getFieldKey(), item);
            }
        }
        List<DiyFormRecordsFields> recordsFieldsList = new ArrayList<>();

        if (ObjectUtil.isNotEmpty(addParam.getValue())) {
            JSONArray components = JSONUtil.parseArray(addParam.getValue());
            for (Object componentObj : components) {
                JSONObject component = JSONUtil.parseObj(componentObj);
                // 过滤非表单组件和表单提交按钮组件
                if (!"diy_form".equals(component.getStr("componentType")) || "FormSubmit".equals(component.getStr("componentName")) || component.getBool("isHidden")) {
                    continue;
                }

                JSONObject field = component.getJSONObject("field");
                Object fieldValue = field.get("value");
                Object checkFieldValue = fieldValue;
                if (fieldValue instanceof List) {
                    // 这里假设 DiyFormRecordsFieldsMapper 有对应的方法，需要根据实际情况修改
                    //  checkFieldValue = diyFormRecordsFieldsMapper.getRenderValueAttr("", (List<?>) fieldValue, (String) component.get("componentName"));
                    checkFieldValue = "";
                }

                DiyFormFields formFieldInfo = fieldList.get(component.getStr("id"));
                if (formFieldInfo != null) {
                    if (formFieldInfo.getFieldRequired() == 1 && fieldValue == null) {
                        throw new CommonException((String) field.getOrDefault("name", component.get("componentTitle")) + "不能为空");
                    } else if (checkFieldValue == null) {
                        // 过滤空数据
                        continue;
                    }

                    // 检测字段是否重复
                    QueryWrapper<DiyFormRecordsFields> uniqueQuery = new QueryWrapper<>();
                    uniqueQuery.eq("site_id", addParam.getSiteId())
                            .eq("form_id", addParam.getFormId())
                            .eq("field_key", component.get("id"))
                            .eq("field_type", component.get("componentName"));
                    List<Object> fieldValues = diyFormRecordsFieldsMapper.selectObjs(uniqueQuery);
                    if (formFieldInfo.getFieldUnique() == 1 && fieldValues.contains(fieldValue)) {
                        throw new CommonException((String) field.getOrDefault("name", component.get("componentTitle")) + "不能重复");
                    }
                } else if (checkFieldValue == null) {
                    // 过滤空数据
                    continue;
                }

                if (fieldValue instanceof List) {
                    // 这里假设使用 JSON 工具类，需要根据实际情况引入
                    fieldValue = JSONUtil.parseArray(fieldValue).toString();
                }

                DiyFormRecordsFields recordsField = new DiyFormRecordsFields();
                recordsField.setSiteId(addParam.getSiteId());
                recordsField.setFormId(addParam.getFormId());
                recordsField.setRecordId(model.getRecordId());
                recordsField.setMemberId(addParam.getMemberId());
                recordsField.setFieldKey((String) component.get("id"));
                recordsField.setFieldType((String) component.get("componentName"));
                recordsField.setFieldName((String) field.getOrDefault("name", ""));
                recordsField.setFieldValue(fieldValue.toString());
                recordsField.setFieldRequired((Boolean) field.get("required") ? 1 : 0);
                recordsField.setFieldHidden((Boolean) component.get("isHidden") ? 1 : 0);
                recordsField.setFieldUnique(component.containsKey("unique") && (Boolean) component.get("unique") ? 1 : 0);
                recordsField.setPrivacyProtection(component.containsKey("privacyProtection") && (Boolean) component.get("privacyProtection") ? 1 : 0);
                recordsField.setCreateTime(System.currentTimeMillis() / 1000);
                recordsField.setUpdateTime(System.currentTimeMillis() / 1000);
                recordsFieldsList.add(recordsField);
            }
        }

        if (!recordsFieldsList.isEmpty()) {
            diyFormRecordsFieldsMapper.insert(recordsFieldsList);

            // 累计填写数量
            diyFormMapper.update(new UpdateWrapper<DiyForm>().eq("form_id", addParam.getFormId()).setSql(" write_num = write_num + 1"));

            for (DiyFormRecordsFields field : recordsFieldsList) {
                // 字段累计填写数量
                diyFormFieldsMapper.update(new UpdateWrapper<DiyFormFields>().eq("form_id", addParam.getFormId()).eq("field_key", field.getFieldKey()).setSql(" write_num = write_num + 1"));
            }
        }
        return model.getRecordId();

    }

    @Override
    public void del(Integer siteId, Integer id) {
        diyFormRecordsMapper.delete(new QueryWrapper<DiyFormRecords>().eq("record_id", id).eq("site_id", siteId));
    }

    @Override
    public List<String> check(DiyFormRecordsParam checkParam) {
        Integer formId = checkParam.getFormId();
        Integer memberId = checkParam.getMemberId();

        // 查询表单填写配置
        DiyFormWriteConfig writeConfig = diyFormWriteConfigMapper.selectOne(
                new QueryWrapper<DiyFormWriteConfig>().eq("form_id", formId)
        );

        List<String> error = new ArrayList<>();

        if (ObjectUtil.isNotEmpty(writeConfig)) {
            // 检查会员是否可以参与
            String memberCanJoinError = checkMemberCanJoinOrNot(memberId, writeConfig);
            if (ObjectUtil.isNotEmpty(memberCanJoinError)) {
                error.add(memberCanJoinError);
            }

            // 检查表单填写时间
            String writeTimeError = checkFormWriteTime(writeConfig);
            if (ObjectUtil.isNotEmpty(writeTimeError)) {
                error.add(writeTimeError);
            }

            // 检查表单填写限制次数
            String writeLimitNumError = checkFormWriteLimitNum(formId, writeConfig);
            if (ObjectUtil.isNotEmpty(writeLimitNumError)) {
                error.add(writeLimitNumError);
            }

            // 检查会员填写限制次数
            String memberWriteLimitNumError = checkMemberWriteLimitNum(memberId, formId, writeConfig);
            if (ObjectUtil.isNotEmpty(memberWriteLimitNumError)) {
                error.add(memberWriteLimitNumError);
            }
        }

        return error;
    }

    private String checkMemberCanJoinOrNot(Integer memberId, DiyFormWriteConfig writeConfig) {
        try {
            Member member = memberMapper.selectOne(
                    new QueryWrapper<Member>()
                            .eq("member_id", memberId)
                            .select("member_level", "member_label")
            );
            if (ObjectUtil.isEmpty(member)) {
                throw new CommonException("MEMBER_NOT_EXIST");
            }

            switch (writeConfig.getJoinMemberType()) {
                case "all_member":
                    break;
                case "selected_member_level":
                    List<Integer> levelIds = this.parseIds(writeConfig.getLevelIds());
                    if (!levelIds.contains(member.getMemberLevel())) {
                        List<MemberLevel> levels = memberLevelMapper.selectList(
                                new QueryWrapper<MemberLevel>()
                                        .in("level_id", levelIds)
                                        .select("level_name")
                        );
                        String levelNames = levels.stream()
                                .map(MemberLevel::getLevelName)
                                .collect(Collectors.joining("、"));

                        return "该表单已设置仅限\"" + levelNames + "等级\"的用户填写";
                    }
                    break;
                case "selected_member_label":
                    SiteShopMember shopMember = shopMemberMapper.selectOne(
                            new QueryWrapper<SiteShopMember>()
                                    .eq("member_id", memberId)
                                    .select("shop_member_label")
                    );

                    if (ObjectUtil.isEmpty(shopMember)) {
                        throw new CommonException("MEMBER_NOT_EXIST");
                    }

                    List<String> memberLabels = new ArrayList<>();
                    if (ObjectUtil.isNotEmpty(member.getMemberLabel())) {
                        String[] memberLabelArray = member.getMemberLabel().split(",");
                        for (String label : memberLabelArray) {
                            if (ObjectUtil.isNotEmpty(label.trim())) {
                                memberLabels.add(label.trim());
                            }
                        }
                    }
                    if (ObjectUtil.isNotEmpty(shopMember.getShopMemberLabel())) {
                        String[] shopMemberLabelArray = shopMember.getShopMemberLabel().split(",");
                        for (String label : shopMemberLabelArray) {
                            if (ObjectUtil.isNotEmpty(label.trim())) {
                                memberLabels.add(label.trim());
                            }
                        }
                    }

                    List<Integer> labelIds = this.parseIds(writeConfig.getLabelIds());
                    boolean hasIntersection = memberLabels.stream()
                            .map(Integer::parseInt)
                            .anyMatch(labelIds::contains);

                    if (!hasIntersection) {
                        List<MemberLabel> labels = memberLabelMapper.selectList(
                                new QueryWrapper<MemberLabel>()
                                        .in("label_id", labelIds)
                                        .select("label_name")
                        );
                        String labelNames = labels.stream()
                                .map(MemberLabel::getLabelName)
                                .collect(Collectors.joining("、"));

                        return "该表单已设置仅限\"" + labelNames + "标签\"的用户填写";
                    }
                    break;
            }
        } catch (Exception e) {
            return "会员权限检查异常: " + e.getMessage();
        }

        return null;
    }

    private String checkFormWriteTime(DiyFormWriteConfig writeConfig) {
        try {
            switch (writeConfig.getTimeLimitType()) {
                case "no_limit":
                    // 无时间限制
                    break;
                    
                case "specify_time":
                    List<Long> specifyTime = this.parseTimeRule(writeConfig.getTimeLimitRule(), "specify_time");
                    if (ObjectUtil.isNotEmpty(specifyTime) && specifyTime.size() >= 2) {
                        long currentTime = System.currentTimeMillis() / 1000; // 当前时间戳（秒）
                        if (currentTime < specifyTime.get(0) || currentTime > specifyTime.get(1)) {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String startTimeStr = sdf.format(new java.util.Date(specifyTime.get(0) * 1000));
                            String endTimeStr = sdf.format(new java.util.Date(specifyTime.get(1) * 1000));
                            return "该表单已设置\"" + startTimeStr + "-" + endTimeStr + "\"可查看";
                        }
                    }
                    break;
                    
                case "open_day_time":
                    List<Integer> openDayTime = this.parseTimeRule(writeConfig.getTimeLimitRule(), "open_day_time");
                    if (ObjectUtil.isNotEmpty(openDayTime) && openDayTime.size() >= 2) {
                        java.util.Calendar calendar = java.util.Calendar.getInstance();
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        calendar.set(java.util.Calendar.MINUTE, 0);
                        calendar.set(java.util.Calendar.SECOND, 0);
                        calendar.set(java.util.Calendar.MILLISECOND, 0);
                        long todayStart = calendar.getTimeInMillis() / 1000;
                        
                        long startTime = todayStart + openDayTime.get(0);
                        long endTime = todayStart + openDayTime.get(1);
                        long currentTime = System.currentTimeMillis() / 1000;
                        
                        if (currentTime < startTime || currentTime > endTime) {
                            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
                            String startTimeStr = timeFormat.format(new java.util.Date(startTime * 1000));
                            String endTimeStr = timeFormat.format(new java.util.Date(endTime * 1000));
                            return "该表单已设置\"每天" + startTimeStr + "-" + endTimeStr + "\"可查看";
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            return "时间限制检查异常: " + e.getMessage();
        }
        
        return null; // 检查通过
    }
    
    private String checkFormWriteLimitNum(Integer formId, DiyFormWriteConfig writeConfig) {
        try {
            switch (writeConfig.getFormWriteType()) {
                case "no_limit":
                    // 无次数限制
                    break;
                    
                case "diy":
                    Map<String, Object> formWriteRule = this.parseWriteRule(writeConfig.getFormWriteRule());
                    if (ObjectUtil.isNotEmpty(formWriteRule)) {
                        String timeUnit = (String) formWriteRule.get("time_unit");
                        Integer timeValue = (Integer) formWriteRule.get("time_value");
                        Integer num = (Integer) formWriteRule.get("num");
                        
                        String timeText = "";
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
                        }
                        
                        java.time.LocalDateTime now = java.time.LocalDateTime.now();
                        java.time.LocalDateTime startTime = null;
                        switch (timeUnit) {
                            case "day":
                                startTime = now.minusDays(timeValue);
                                break;
                            case "week":
                                startTime = now.minusWeeks(timeValue);
                                break;
                            case "month":
                                startTime = now.minusMonths(timeValue);
                                break;
                            case "year":
                                startTime = now.minusYears(timeValue);
                                break;
                        }
                        
                        Long count = diyFormRecordsMapper.selectCount(
                            new QueryWrapper<DiyFormRecords>()
                                .eq("form_id", formId)
                                .ge("create_time", startTime)
                        );
                        
                        if (count >= num) {
                            return "该表单已设置\"每" + timeValue + timeText + "可填写" + num + "次\"";
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            return "表单次数限制检查异常: " + e.getMessage();
        }
        
        return null; // 检查通过
    }


    private String checkMemberWriteLimitNum(Integer memberId, Integer formId, DiyFormWriteConfig writeConfig) {
        try {
            switch (writeConfig.getMemberWriteType()) {
                case "no_limit":
                    break;
                    
                case "diy":
                    Map<String, Object> memberWriteRule = this.parseWriteRule(writeConfig.getMemberWriteRule());
                    if (ObjectUtil.isNotEmpty(memberWriteRule)) {
                        String timeUnit = (String) memberWriteRule.get("time_unit");
                        Integer timeValue = (Integer) memberWriteRule.get("time_value");
                        Integer num = (Integer) memberWriteRule.get("num");
                        
                        String timeText = "";
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
                        }
                        
                        java.time.LocalDateTime now = java.time.LocalDateTime.now();
                        java.time.LocalDateTime startTime = null;
                        switch (timeUnit) {
                            case "day":
                                startTime = now.minusDays(timeValue);
                                break;
                            case "week":
                                startTime = now.minusWeeks(timeValue);
                                break;
                            case "month":
                                startTime = now.minusMonths(timeValue);
                                break;
                            case "year":
                                startTime = now.minusYears(timeValue);
                                break;
                        }
                        
                        Long count = diyFormRecordsMapper.selectCount(
                            new QueryWrapper<DiyFormRecords>()
                                .eq("form_id", formId)
                                .eq("member_id", memberId)
                                .ge("create_time", startTime)
                        );
                        
                        if (count >= num) {
                            return "该表单已设置\"每人每" + timeValue + timeText + "可填写" + num + "次\"";
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            return "会员次数限制检查异常: " + e.getMessage();
        }
        
        return null;
    }


    private List<Integer> parseIds(String idsStr) {
        List<Integer> ids = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(idsStr)) {
            String[] idArray = idsStr.split(",");
            for (String id : idArray) {
                try {
                    ids.add(Integer.parseInt(id.trim()));
                } catch (NumberFormatException e) {
                    // 忽略无效的ID
                }
            }
        }
        return ids;
    }


    private List parseTimeRule(String timeRuleStr, String ruleType) {
        try {
            if (ObjectUtil.isEmpty(timeRuleStr)) {
                return new ArrayList();
            }
            
            JSONObject timeRule = JSONUtil.parseObj(timeRuleStr);
            Object rule = timeRule.get(ruleType);
            
            if (rule instanceof List) {
                return (List) rule;
            }
            
            return new ArrayList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
    
    private Map<String, Object> parseWriteRule(String writeRuleStr) {
        try {
            if (ObjectUtil.isEmpty(writeRuleStr)) {
                return new HashMap<>();
            }
            
            JSONObject writeRule = JSONUtil.parseObj(writeRuleStr);
            Map<String, Object> result = new HashMap<>();
            
            result.put("time_unit", writeRule.getStr("time_unit"));
            result.put("time_value", writeRule.getInt("time_value"));
            result.put("num", writeRule.getInt("num"));
            
            return result;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

}
