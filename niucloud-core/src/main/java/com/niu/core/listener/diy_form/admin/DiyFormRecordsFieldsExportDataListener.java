package com.niu.core.listener.diy_form.admin;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
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
import com.niu.core.service.admin.diy_form.IDiyFormRecordsService;
import com.niu.core.service.admin.diy_form.IDiyFormService;
import com.niu.core.service.admin.diy_form.param.DiyFormRecordsFieldsSearchParam;
import com.niu.core.service.admin.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.admin.diy_form.vo.DiyFormFieldsListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 表单填写记录导出数据监听器
 */
@Component
@EventCallback("core")
public class DiyFormRecordsFieldsExportDataListener extends ExportDataEventDefiner {

    @Resource
    private DiyFormRecordsMapper diyFormRecordsMapper;

    @Resource
    private IDiyFormRecordsService diyFormRecordsService;

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

        if (!"admin_diy_form_records_fields".equals(exportDataEvent.getType())) {
            exportDataEventResult.setResultData(result);
            return exportDataEventResult;
        }
        JSONObject whereJson = exportDataEvent.getWhere();
        if (whereJson == null || !whereJson.containsKey("form_id")) {
            exportDataEventResult.setResultData(result);
            return exportDataEventResult;
        }

        RequestUtils.setSiteId(exportDataEvent.getSiteId());

        DiyFormRecordsSearchParam searchParam = new DiyFormRecordsSearchParam();
        searchParam.setFormId(whereJson.getInt("form_id", 0));
        searchParam.setKeyword(whereJson.getStr("keyword", ""));

        List<DiyFormFieldsListVo> records = diyFormRecordsService.getFieldStatList(searchParam);
        if (ObjectUtil.isNotEmpty(records)) {
            for (DiyFormFieldsListVo vo : records) {
                vo.getValueList().stream().forEach(item -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.set("field_name", vo.getFieldName());
                    jsonObject.set("render_value", item.get("render_value"));
                    jsonObject.set("write_count", item.get("write_count"));
                    jsonObject.set("write_percent", item.get("write_percent"));
                    result.add(jsonObject);
                });
            }
        }

        exportDataEventResult.setResultData(result);
        return exportDataEventResult;
    }
}
