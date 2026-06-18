package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.sys.SysExport;
import com.niu.core.enums.sys.ExportEnum;
import com.niu.core.mapper.sys.SysExportMapper;
import com.niu.core.service.admin.sys.ISysExportService;
import com.niu.core.service.admin.sys.param.SysExportSearchParam;
import com.niu.core.service.admin.sys.vo.SysExportListVo;
import com.niu.core.service.core.sys.ICoreExportService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;


/**
 * 导出报实现
 */
@Service
public class SysExportServiceImpl implements ISysExportService {

    @Resource
    SysExportMapper sysExportMapper;

    @Resource
    ICoreExportService coreExportService;

    /**
     * 导出报列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysExportListVo>
     */
    @Override
    public PageResult<SysExportListVo> list(PageParam pageParam, SysExportSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysExport> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (ObjectUtil.isNotEmpty(searchParam.getExportKey())) {
            queryWrapper.eq("export_key", searchParam.getExportKey());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getExportStatus())) {
            queryWrapper.eq("export_status", searchParam.getExportStatus());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            String[] createTime = searchParam.getCreateTime();
            long startTime = (createTime[0] == null) ? 0 : DateUtils.StringToTimestamp(createTime[0]);
            long endTime = (createTime[1] == null) ? 0 : DateUtils.StringToTimestamp(createTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("create_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("create_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le("create_time", startTime);
            }
        }

        JSONArray results = JsonModuleLoader.build().mergeResultSet("export/ExportType.json");

        IPage<SysExport> iPage = sysExportMapper.selectPage(new Page<>(page, limit), queryWrapper);
        IPage<SysExportListVo> converted = iPage.convert(export -> {
            SysExportListVo vo = new SysExportListVo();
            BeanUtils.copyProperties(export, vo);
            vo.setCreateTime(DateUtils.timestampToString(export.getCreateTime()));
            vo.setExportStatusName(ExportEnum.getNameByCode(export.getExportStatus()));
            results.stream()
                    .map(JSONUtil::parseObj)
                    .filter(o -> o.containsKey(export.getExportKey()))
                    .findFirst()
                    .map(o -> o.getJSONObject(export.getExportKey()))
                    .ifPresent(o -> vo.setExportKeyName(o.getStr("name")));

            return vo;
        });

        return PageResult.build(converted);
    }

    /**
     * 导出报删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysExport model = sysExportMapper.selectOne(
                new QueryWrapper<SysExport>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        sysExportMapper.delete(new QueryWrapper<SysExport>().eq("id", id));
    }

    @Override
    public Map<String, String> getExportDataType(String appType) {
        return coreExportService.getExportDataType(appType);
    }

    @Override
    public Boolean checkExportData(String type, Map<String, Object> whereMap, HttpServletRequest request) {


        PageParam pageParam = new PageParam();
        pageParam.setPage(Integer.parseInt(whereMap.getOrDefault("page", 0).toString()));
        pageParam.setLimit(Integer.parseInt(whereMap.getOrDefault("limit", 0).toString()));

        Map<String, Object> whereConditions = new java.util.HashMap<>(whereMap);

        //判断是否有数组数据
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            if (name.contains("[") && name.contains("]")) {
                String[] values = (String[]) requestParams.get(name);
                name = name.replaceAll("\\[", "").replaceAll("\\]", "");
                whereConditions.put(name, values);
            }
        }
        whereConditions.remove("page");
        whereConditions.remove("limit");

        JSONArray jsonArray = coreExportService.getExportData(RequestUtils.siteId(), type, JSONUtil.parseObj(whereConditions), pageParam);

        if (jsonArray == null) {
            throw new CommonException("列表为空");
        }
        if (jsonArray.isEmpty()) {
            if ("site_diy_form_records".equals(type) || "admin_diy_form_records".equals(type)) {
                throw new CommonException("暂无填写记录，无法导出");
            }
        }
        return !jsonArray.isEmpty();
    }

    @Override
    public Object getExportDataColumn(String type, Map<String, Object> whereMap, HttpServletRequest request) {
        Integer siteId = RequestUtils.siteId();
        Map<String, Object> whereConditions = new java.util.HashMap<>(whereMap);
        //判断是否有数组数据
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            if (name.contains("[") && name.contains("]")) {
                String[] values = (String[]) requestParams.get(name);
                name = name.replaceAll("\\[", "").replaceAll("\\]", "");
                whereConditions.put(name, values);
            }
        }
        JSONArray baseColumn = coreExportService.getExportDataColumn(siteId, type, JSONUtil.parseObj(whereConditions));
        JSONObject baseObj = new JSONObject();
        for (Object obj : baseColumn) {
            JSONObject jsonObject = (JSONObject) obj;
            for (String key : jsonObject.keySet()) {
                JSONObject value = jsonObject.getJSONObject(key);
                baseObj.set(key, value);
            }

        }
        return baseObj;
    }

    @Override
    public void exportData(String type, Map<String, Object> whereMap, HttpServletRequest request) {
        Integer siteId = RequestUtils.siteId();

        PageParam pageParam = new PageParam();
        pageParam.setPage(Integer.parseInt(whereMap.getOrDefault("page", 0).toString()));
        pageParam.setLimit(Integer.parseInt(whereMap.getOrDefault("limit", 0).toString()));
        whereMap.remove("page");
        whereMap.remove("limit");

        asyncExportData(siteId, type, JSONUtil.parseObj(whereMap), pageParam);

    }


    @Async
    public void asyncExportData(Integer siteId, String type, JSONObject whereConditions, PageParam pageParam) {
        // 复制whereConditions到searchCondition，移除export_column
        JSONObject searchCondition = new JSONObject();
        for (String key : whereConditions.keySet()) {
            if (!"export_column".equals(key)) {
                searchCondition.set(key, whereConditions.get(key));
            }
        }

        // 获取数据时使用包含export_column的whereConditions，确保导出字段过滤生效
        JSONArray dataArray = coreExportService.getExportData(siteId, type, whereConditions, pageParam);

        SysExport export = new SysExport();
        export.setSiteId(siteId);
        export.setExportKey(type);
        export.setExportNum(CollectionUtil.size(dataArray));
        export.setCreateTime(System.currentTimeMillis() / 1000);
        // 保存搜索条件（不包含export_column）
        export.setSearchCondition(searchCondition.toString());
        // 保存导出字段列
        JSONArray exportColumn = new JSONArray();
        if (whereConditions.containsKey("export_column")) {
            JSONObject exportColumnObj = whereConditions.getJSONObject("export_column");
            for (String key : exportColumnObj.keySet()) {
                JSONObject column = new JSONObject();
                column.set(key, exportColumnObj.get(key));
                exportColumn.add(column);
            }
            export.setDataColumn(exportColumn.toString());
        }
        coreExportService.add(export);

        coreExportService.export(siteId, export.getId(), type, exportColumn, dataArray);

    }

    @Override
    public void againExportData(Integer id) {
        SysExport exportInfo = sysExportMapper.selectOne(
                new QueryWrapper<SysExport>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(exportInfo, "任务不存在");

        // 检查任务状态是否为失败
        if (!ExportEnum.FAIL.getCode().equals(exportInfo.getExportStatus())) {
            throw new CommonException("导出失败任务才能重新执行");
        }

        // 更新任务状态为导出中
        exportInfo.setExportStatus(ExportEnum.EXPORTING.getCode());
        sysExportMapper.updateById(exportInfo);

        // 构建分页参数
        PageParam pageParam = new PageParam();
        pageParam.setPage(0);
        pageParam.setLimit(0);

        // 从searchCondition和dataColumn中恢复导出参数
        JSONObject whereConditions = JSONUtil.parseObj(exportInfo.getSearchCondition());
        if (ObjectUtil.isNotEmpty(exportInfo.getDataColumn())) {
            JSONArray exportColumn = JSONUtil.parseArray(exportInfo.getDataColumn());
            whereConditions.set("export_column", exportColumn);
        }

        // 重新执行导出
        asyncExportData(exportInfo.getSiteId(), exportInfo.getExportKey(), whereConditions, pageParam);
    }

}
