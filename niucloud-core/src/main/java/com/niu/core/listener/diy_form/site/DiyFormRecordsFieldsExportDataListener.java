package com.niu.core.listener.diy_form.site;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.service.site.diy_form.IDiyFormRecordsService;
import com.niu.core.service.site.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.site.diy_form.vo.DiyFormFieldsListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 表单填写记录导出数据监听器
 */
@Component
@EventCallback("core")
public class DiyFormRecordsFieldsExportDataListener extends ExportDataEventDefiner {

    @Resource
    private IDiyFormRecordsService diyFormRecordsService;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent exportDataEvent) {
        JSONArray result = new JSONArray();
        ExportDataEventResult exportDataEventResult = new ExportDataEventResult();

        if (!"site_diy_form_records_fields".equals(exportDataEvent.getType())) {
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
