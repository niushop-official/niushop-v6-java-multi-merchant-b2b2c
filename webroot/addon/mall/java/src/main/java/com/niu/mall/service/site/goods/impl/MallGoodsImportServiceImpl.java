package com.niu.mall.service.site.goods.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.entity.goods.MallGoodsImportRecord;
import com.niu.mall.entity.goods.MallGoodsImportRows;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.enums.goods.GoodsImportStatusEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.goods.MallGoodsSourceTypeEnum;
import com.niu.mall.mapper.goods.MallGoodsImportMapper;
import com.niu.mall.mapper.goods.MallGoodsImportRowMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.core.goods.ICoreGoodsPartyService;
import com.niu.mall.service.core.goods.impl.CoreGoodsPartyServiceImpl;
import com.niu.mall.service.site.goods.IMallGoodsImportService;
import com.niu.mall.service.site.goods.param.MallGoodsImportParam;
import com.niu.mall.service.site.goods.vo.MallGoodsImportRowVo;
import com.niu.mall.service.site.goods.vo.MallGoodsImportVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallGoodsImportServiceImpl implements IMallGoodsImportService {

    @Resource
    private MallGoodsImportMapper goodsImportMapper;
    @Resource
    private MallGoodsImportRowMapper importRowMapper;

    @Resource
    private ICoreGoodsPartyService coreGoodsPartyService;

    @Resource
    private MallGoodsSkuMapper skuMapper;

    @Override
    public Map<String, String> getGoodsTypeAll() {
        return GoodsTypeEnum.getSimpleMap();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> add(MallGoodsImportParam params) {
        String filePath = params.getFilePath();
        String goodsType = params.getGoodsType();
        Integer siteId = RequestUtils.siteId();   // 需从上下文中获取
        Integer mainId = RequestUtils.uid(); // 当前管理员ID

        MallGoodsImportRecord record = new MallGoodsImportRecord();
        record.setSiteId(siteId);
        record.setMainId(mainId);
        record.setFilePath(filePath);
        record.setGoodsType(goodsType);
        record.setCreateTime(System.currentTimeMillis() / 1000);
        record.setUpdateTime(System.currentTimeMillis() / 1000);
        goodsImportMapper.insert(record);
        Integer recordId = record.getRecordId();

        // 解析文件并保存行数据
        addRow(filePath, goodsType, recordId);

        return Map.of("record_id", recordId);
    }

    private void addRow(String filePath, String goodsType, Integer recordId) {
        File file = new File(WebAppEnvs.get().webRootDownResource + filePath);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        // 使用 EasyExcel 读取
        List<Map<Integer, String>> excelData = new ArrayList<>();
        EasyExcel.read(file, new ReadListener<Map<Integer, String>>() {
            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                excelData.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
            }
        }).sheet().doRead();

        if (excelData.isEmpty()) {
            throw new RuntimeException("文件无数据");
        }
        // 获取字段映射
        List<Map<String, String>> fieldMapping = GoodsTypeEnum.REAL.getType().equals(goodsType) ?
                CoreGoodsPartyServiceImpl.ImportCollectDict.importRealMap() : CoreGoodsPartyServiceImpl.ImportCollectDict.importVirtualMap();
        int expectedCols = fieldMapping.size();
        // 校验列数
        Map<Integer, String> firstRow = excelData.get(0);
        if (firstRow.size() != expectedCols) {
            throw new RuntimeException("上传文件和模版提供的的数据列数不对称");
        }
        // 从第3行开始处理（索引1）
        List<MallGoodsImportRows> rowList = new ArrayList<>();
        for (int i = 1; i < excelData.size(); i++) {
            Map<Integer, String> row = excelData.get(i);
            Map<String, String> itemMap = new HashMap<>();
            for (int j = 0; j < expectedCols; j++) {
                String fieldKey = fieldMapping.get(j).get("key");
                String value = row.getOrDefault(j, "");
                if (value != null) {
                    value = value.trim();
                }
                itemMap.put(fieldKey, value);
            }
            if (StrUtil.isEmpty(itemMap.get("goods_id"))) {
                continue;
            }
            MallGoodsImportRows rows = new MallGoodsImportRows();
            rows.setRecordId(recordId);
            rows.setGoodsId(itemMap.get("goods_id"));
            rows.setSkuId(itemMap.get("sku_id"));
            rows.setGoodsType(goodsType);
            rows.setData(JSONUtil.toJsonStr(itemMap));
            rows.setStatus(GoodsImportStatusEnum.WAIT.getStatus());
            rows.setCreateTime(System.currentTimeMillis() / 1000);
            rows.setUpdateTime(System.currentTimeMillis() / 1000);
            rowList.add(rows);
        }
        if (!rowList.isEmpty()) {
            importRowMapper.insert(rowList); // 需实现批量插入
        }
        // 更新总数量
        goodsImportMapper.update(new LambdaUpdateWrapper<MallGoodsImportRecord>()
                .eq(MallGoodsImportRecord::getRecordId, recordId)
                .set(MallGoodsImportRecord::getTotalNum, rowList.size())
                .set(MallGoodsImportRecord::getUpdateTime, System.currentTimeMillis() / 1000));
    }

    @Override
    public PageResult<MallGoodsImportVo> getPage(PageParam pageParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        Integer siteId = RequestUtils.siteId();

        LambdaQueryWrapper<MallGoodsImportRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallGoodsImportRecord::getSiteId, siteId);
        wrapper.orderByDesc(MallGoodsImportRecord::getRecordId);
        // 附加字段（status_name, goods_type_name）可在 VO 中处理，此处返回实体
        IPage<MallGoodsImportRecord> iPage = goodsImportMapper.selectPage(new Page<>(page, limit), wrapper);
        List<MallGoodsImportVo> list = BeanUtil.copyToList(iPage.getRecords(), MallGoodsImportVo.class);


        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public PageResult<MallGoodsImportRowVo> getDetail(Integer recordId, PageParam pageParam, Map<String, Object> params) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        Page<MallGoodsImportRows> pageObj = new Page<>(page, limit);
        LambdaQueryWrapper<MallGoodsImportRows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallGoodsImportRows::getRecordId, recordId);
        String status = (String) params.get("status");
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(MallGoodsImportRows::getStatus, Integer.parseInt(status));
        }
        String searchText = (String) params.get("search_text");
        if (StrUtil.isNotBlank(searchText)) {
            wrapper.like(MallGoodsImportRows::getData, searchText);
        }
        wrapper.orderByAsc(MallGoodsImportRows::getId);
        IPage<MallGoodsImportRows> rowsPage = importRowMapper.selectPage(pageObj, wrapper);
        IPage<Map<String, Object>> resultPage = new Page<>(rowsPage.getCurrent(), rowsPage.getSize(), rowsPage.getTotal());
        List<MallGoodsImportRowVo> list = BeanUtil.copyToList(resultPage.getRecords(), MallGoodsImportRowVo.class);

        return PageResult.build(page, limit, resultPage.getTotal()).setData(list);
    }

    @Override
    public MallGoodsImportVo info(Integer recordId) {
        MallGoodsImportRecord record = goodsImportMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }
        MallGoodsImportVo vo = BeanUtil.copyProperties(record, MallGoodsImportVo.class);
        return vo;
    }

//    @Override
//    public Boolean update(Map<String, Object> where, Map<String, Object> data) {
//        LambdaUpdateWrapper<ImportRecord> wrapper = new LambdaUpdateWrapper<>();
//        // 简单处理，实际按需实现
//        return this.update(wrapper);
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> execute(Integer recordId) {
        MallGoodsImportRecord record = goodsImportMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }
        if (record.getStatus().equals(GoodsImportStatusEnum.SUCCESS.getStatus())) {
            return Map.of();
        }
        if (record.getStatus().equals(GoodsImportStatusEnum.FAIL.getStatus())) {
            throw new RuntimeException("导入执行失败");
        }
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = importData(record);
        String failRemark = (String) result.getOrDefault("fail_remark", "");
        int status = StrUtil.isBlank(failRemark) ? GoodsImportStatusEnum.SUCCESS.getStatus() : GoodsImportStatusEnum.FAIL.getStatus();

        // 更新记录
        LambdaUpdateWrapper<MallGoodsImportRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MallGoodsImportRecord::getRecordId, recordId)
                .set(MallGoodsImportRecord::getStatus, status)
                .set(MallGoodsImportRecord::getSuccessNum, result.get("success_num"))
                .set(MallGoodsImportRecord::getFailNum, result.get("fail_num"))
                .set(MallGoodsImportRecord::getFailRemark, failRemark)
                .set(MallGoodsImportRecord::getUpdateTime, System.currentTimeMillis() / 1000);
        // 生成输出文件
        List<List<String>> totalList = (List<List<String>>) result.get("total_list");
        if (totalList != null && !totalList.isEmpty()) {
            String outputPath = writeCsv(totalList, record.getSiteId());
            updateWrapper.set(MallGoodsImportRecord::getOutput, outputPath);
        }
        List<List<String>> failList = (List<List<String>>) result.get("fail_list");
        if (failList != null && failList.size() > 1) {
            String failOutputPath = writeCsvWithTitle(failList, record.getSiteId(), record.getGoodsType());
            updateWrapper.set(MallGoodsImportRecord::getFailOutput, failOutputPath);
        }
        long duration = (System.currentTimeMillis() - startTime) / 1000;
        if (duration < 1) duration = 1;
        updateWrapper.set(MallGoodsImportRecord::getDuration, (int) duration);
        goodsImportMapper.update(updateWrapper);
        return result;
    }

    private Map<String, Object> importData(MallGoodsImportRecord record) {
        Integer recordId = record.getRecordId();
        Integer siteId = record.getSiteId();
        String goodsType = record.getGoodsType();
        // 查询待处理的行数据
        List<MallGoodsImportRows> rows = importRowMapper.selectList(new LambdaQueryWrapper<MallGoodsImportRows>()
                .eq(MallGoodsImportRows::getRecordId, recordId)
                .in(MallGoodsImportRows::getStatus, List.of(GoodsImportStatusEnum.WAIT.getStatus(), GoodsImportStatusEnum.PROCESSING.getStatus()))
                .orderByAsc(MallGoodsImportRows::getId));
        if (rows.isEmpty()) {
            return Map.of("success_num", 0, "fail_num", 0, "total_list", List.of(), "fail_list", List.of(), "fail_remark", "");
        }
        // 解析每条数据的 JSON
        List<Map<String, Object>> excelData = rows.stream()
                .map(row -> JSONUtil.parseObj(row.getData()))
                .collect(Collectors.toList());
        // 获取表头（字段名）
        List<Map<String, String>> fields = goodsType.equals(GoodsTypeEnum.REAL.getType()) ?
                CoreGoodsPartyServiceImpl.ImportCollectDict.importRealMap() :
                CoreGoodsPartyServiceImpl.ImportCollectDict.importVirtualMap();
        List<String> rowKeys = new ArrayList<>();
        List<String> rowTitle = new ArrayList<>();
        for (Map<String, String> field : fields) {
            rowKeys.add(field.get("key"));
            rowTitle.add(field.get("name"));
        }
        List<List<String>> totalList = new ArrayList<>();
        totalList.add(new ArrayList<>(rowTitle) {{
            add("结果");
            add("描述");
        }});
        List<List<String>> failList = new ArrayList<>();
        failList.add(new ArrayList<>(rowTitle) {{
            add("结果");
            add("描述");
        }});

        int successNum = 0;
        int failCount = 0;
        // 按 goods_id 分组
        Map<Long, List<Map<String, Object>>> groupData = excelData.stream()
                .collect(Collectors.groupingBy(m -> Long.parseLong(m.get("goods_id").toString())));
        for (Map.Entry<Long, List<Map<String, Object>>> entry : groupData.entrySet()) {
            Long goodsId = entry.getKey();
            List<Map<String, Object>> goodsData = entry.getValue();
            // 更新状态为处理中
            importRowMapper.update(null, new LambdaUpdateWrapper<MallGoodsImportRows>()
                    .eq(MallGoodsImportRows::getGoodsId, goodsId)
                    .eq(MallGoodsImportRows::getRecordId, recordId)
                    .set(MallGoodsImportRows::getStatus, GoodsImportStatusEnum.PROCESSING.getStatus())
                    .set(MallGoodsImportRows::getUpdateTime, System.currentTimeMillis() / 1000));
            String error = null;
            try {
                Map<String, Object> resp = coreGoodsPartyService.importGoods(goodsData, goodsType, siteId);
                if (resp.containsKey("fail")) {
                    error = (String) ((Map) resp.get("fail")).get("msg");
                }
            } catch (Exception e) {
                error = e.getMessage();
            }
            if (StrUtil.isBlank(error)) {
                // 成功：记录到 totalList，并更新关联的商品ID/SKU ID
                for (Map<String, Object> skuInfo : goodsData) {
//                    List<String> row = skuInfo.values().stream().map(Object::toString).collect(Collectors.toList());
                    List<String> row = new ArrayList<>();
                    for (String s : rowKeys) {
                        row.add((String) skuInfo.getOrDefault(s, ""));
                    }

                    row.add("成功");
                    row.add("");
                    totalList.add(row);
                }
                successNum += goodsData.size();
                // 查询关联的实际商品SKU
                List<Long> sourceSkuIds = goodsData.stream()
                        .map(m -> Long.parseLong(m.get("sku_id").toString()))
                        .toList();

                List<MallGoodsSku> goodsSkuList = skuMapper.selectList(new LambdaQueryWrapper<MallGoodsSku>()
                        .eq(MallGoodsSku::getSourceType, MallGoodsSourceTypeEnum.PART_IMPORT.getType())
                        .eq(MallGoodsSku::getSourceGoodsId, goodsId)
                        .in(MallGoodsSku::getSourceSkuId, sourceSkuIds)
                        .orderByDesc(MallGoodsSku::getSkuId));

                for (MallGoodsSku sku : goodsSkuList) {
                    LambdaUpdateWrapper<MallGoodsImportRows> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(MallGoodsImportRows::getGoodsId, goodsId)
                            .eq(MallGoodsImportRows::getSkuId, sku.getSourceSkuId())
                            .eq(MallGoodsImportRows::getRecordId, recordId)
                            .set(MallGoodsImportRows::getRelateGoodsId, sku.getGoodsId())
                            .set(MallGoodsImportRows::getRelateSkuId, sku.getSkuId())
                            .set(MallGoodsImportRows::getStatus, GoodsImportStatusEnum.SUCCESS.getStatus())
                            .set(MallGoodsImportRows::getUpdateTime, System.currentTimeMillis() / 1000);
                    importRowMapper.update(updateWrapper);
                }
            } else {
                for (Map<String, Object> skuInfo : goodsData) {
//                    List<String> row = skuInfo.values().stream().map(Object::toString).collect(Collectors.toList());
                    List<String> row = new ArrayList<>();
                    for (String s : rowKeys) {
                        row.add((String) skuInfo.getOrDefault(s, ""));
                    }
                    row.add("失败");
                    row.add(error);
                    totalList.add(row);
                    failList.add(row);
                }
                failCount += goodsData.size();
                importRowMapper.update(null, new LambdaUpdateWrapper<MallGoodsImportRows>()
                        .eq(MallGoodsImportRows::getGoodsId, goodsId)
                        .eq(MallGoodsImportRows::getRecordId, recordId)
                        .set(MallGoodsImportRows::getStatus, GoodsImportStatusEnum.FAIL.getStatus())
                        .set(MallGoodsImportRows::getUpdateTime, System.currentTimeMillis() / 1000));
            }
        }
        return Map.of(
                "success_num", successNum,
                "fail_num", failCount,
                "total_list", totalList,
                "fail_list", failList,
                "fail_remark", ""
        );
    }

    private String writeCsv(List<List<String>> rows, Integer siteId) {
        String dir = WebAppEnvs.get().webRootDownResource + "upload/goods/import/" + siteId + "/" + "output" + "/";
        File dirFile = new File(dir);
        if (!dirFile.exists()) dirFile.mkdirs();
        String fileName = IdUtil.fastSimpleUUID() + ".csv";
        String fullPath = dir + fileName;
        try (FileWriter fw = new FileWriter(fullPath, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(fw)) {
            for (List<String> row : rows) {
                writer.write(row.stream().map(s -> "\"" + escapeCsv(s) + "\"").collect(Collectors.joining(",")));
                writer.newLine();
            }
        } catch (IOException e) {
            log.error("写入CSV失败", e);
            return null;
        }
        return fullPath.replace(WebAppEnvs.get().webRootDownResource, "");
    }

    private String writeCsvWithTitle(List<List<String>> rows, Integer siteId, String goodsType) {
        String dir = WebAppEnvs.get().webRootDownResource + "upload/goods/import/" + siteId + "/" + "fail_output" + "/";
        File dirFile = new File(dir);
        if (!dirFile.exists()) dirFile.mkdirs();
        String fileName = IdUtil.fastSimpleUUID() + ".csv";
        String fullPath = dir + fileName;
        try (FileWriter fw = new FileWriter(fullPath, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(fw)) {
            // 获取合并标题（模板第一行）
            String mergeTitle = getDefaultGoodsImportTemplateRemark(goodsType);
            mergeTitle = mergeTitle.replace("\r", " ").replace("\n", " ");
            int totalColumns = rows.get(0).size();
            List<String> mergeRow = new ArrayList<>(Collections.nCopies(totalColumns, ""));
            mergeRow.set(0, mergeTitle);
            writer.write(mergeRow.stream().map(s -> "\"" + escapeCsv(s) + "\"").collect(Collectors.joining(",")));
            writer.newLine();
            for (List<String> row : rows) {
                writer.write(row.stream().map(s -> "\"" + escapeCsv(s) + "\"").collect(Collectors.joining(",")));
                writer.newLine();
            }
        } catch (IOException e) {
            log.error("写入失败CSV失败", e);
            return null;
        }
        return fullPath.replace(WebAppEnvs.get().webRootDownResource, "");
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }

    private String getDefaultGoodsImportTemplateRemark(String goodsType) {
        String templatePath;
        if (GoodsTypeEnum.REAL.getType().equals(goodsType)) {
            templatePath = WebAppEnvs.get().webRootDownResource + "/addon/mall/import/mall_real_goods_import_template.xlsx";
        } else {
            templatePath = WebAppEnvs.get().webRootDownResource + "/addon/mall/import/mall_virtual_goods_import_template.xlsx";
        }
        File file = new File(templatePath);
        if (!file.exists()) return "";

        try (InputStream is = new FileInputStream(file)) {
            // 读取第一个 sheet 的第一行第一列
            List<Map<Integer, String>> data = EasyExcel.read(is)
                    .sheet(0)
                    .headRowNumber(0) // 无表头
                    .doReadSync();
            if (!data.isEmpty()) {
                return data.get(0).getOrDefault(0, "");
            }
        } catch (IOException e) {
            log.error("读取模板失败", e);
        }
        return "";
    }

    @Override
    public Map<String, Object> demo() {
//        return coreGoodsImportService.demo(getSiteId());
        return Map.of();
    }


}
