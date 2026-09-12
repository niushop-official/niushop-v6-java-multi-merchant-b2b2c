package com.niu.mall.service.core.order.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriteConfig;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.entity.delivery.DeliveryElectronicSheet;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.mapper.delivery.DeliveryElectronicSheetMapper;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderBatchDelivery;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderBatchDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderBatchDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.mall.mapper.order.MallOrderBatchDeliveryMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderBatchDeliveryService;
import com.niu.mall.service.core.order.ICoreOrderDeliveryService;
import com.niu.mall.service.core.order.model.*;
import com.niu.mall.service.core.order.param.OrderBatchDeliveryParam;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;
import com.niu.mall.service.core.order.vo.OrderBatchDeliveryVo;
import com.niu.mall.service.site.order.param.MallOrderAddBatchDeliveryParam;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreOrderBatchDeliveryServiceImpl implements ICoreOrderBatchDeliveryService {
    private final MallOrderMapper mallOrderMapper;
    private final MallOrderGoodsMapper mallOrderGoodsMapper;
    private final ICoreOrderDeliveryService coreOrderDeliveryService;
    private final DeliveryCompanyMapper deliveryCompanyMapper;
    private final MallOrderBatchDeliveryMapper mallOrderBatchDeliveryMapper;
    private final DeliveryElectronicSheetMapper deliveryElectronicSheetMapper;
    private final SiteAddressMapper siteAddressMapper;

    @Override
    public void add(MallOrderAddBatchDeliveryParam param) {
        MallOrderBatchDelivery delivery = new MallOrderBatchDelivery();
        BeanUtils.copyProperties(param, delivery);
        delivery.setData(JSONUtil.toJsonStr(param.getData()));
        delivery.setStatus(OrderBatchDeliveryStatusEnum.PROCESSING.getValue());
        delivery.setCreateTime(System.currentTimeMillis() / 1000);
//        delivery.setType(param.getType().getValue());
        delivery.setType(param.getType());
        mallOrderBatchDeliveryMapper.insert(delivery);

        ThreadPoolManager.runTask(() -> execute(delivery.getId(), delivery.getSiteId()));
    }

    @Override
    public void execute(Integer id, Integer siteId) {
        QueryWrapper<MallOrderBatchDelivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        if (ObjectUtil.isNotEmpty(siteId) && siteId > 0) {
            queryWrapper.eq("site_id", siteId);
        }

        MallOrderBatchDelivery delivery = mallOrderBatchDeliveryMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(delivery)) {
            throw new RuntimeException("任务不存在");
        }

        if (!Objects.equals(delivery.getStatus(), OrderBatchDeliveryStatusEnum.PROCESSING.getValue())) {
            throw new RuntimeException("任务已经完成");
        }

        OrderBatchDeliveryVo result = new OrderBatchDeliveryVo();
        if (delivery.getType().equals(OrderBatchDeliveryTypeEnum.DELIVERY.getValue())) {
            //导入
            OrderBatchDeliveryParam param = new OrderBatchDeliveryParam();
            param.setSiteId(siteId);
            param.setMainId(delivery.getMainId());
            param.setData(JSONUtil.toBean(delivery.getData(), OrderBatchDeliveryData.class));
            result = delivery(param);
        } else if (delivery.getType().equals(OrderBatchDeliveryTypeEnum.SELECT_DELIVERY.getValue())) {
            //选择
            OrderBatchDeliveryParam param = new OrderBatchDeliveryParam();
            param.setSiteId(siteId);
            param.setMainId(delivery.getMainId());
            param.setData(JSONUtil.toBean(delivery.getData(), OrderBatchDeliveryData.class));
            result = batchSelectDelivery(param);
        } else if (delivery.getType().equals(OrderBatchDeliveryTypeEnum.PRINT_DELIVERY.getValue())) {
            //打单
            OrderBatchDeliveryParam param = new OrderBatchDeliveryParam();
            param.setSiteId(siteId);
            param.setMainId(delivery.getMainId());
            param.setData(JSONUtil.toBean(delivery.getData(), OrderBatchDeliveryData.class));
            result = batchPrintDelivery(param);
        }

        OrderBatchDeliveryStatusEnum status = ObjectUtil.isEmpty(result.getFailRemark()) ? OrderBatchDeliveryStatusEnum.FINISH : OrderBatchDeliveryStatusEnum.FAIL;
        delivery.setStatus(status.getValue());
        delivery.setTotalNum(result.getTotalNum());
        delivery.setSuccessNum(result.getSuccessNum());
        delivery.setFailNum(result.getFailNum());
        delivery.setFailRemark(result.getFailRemark());

        List<?> totalList = result.getTotalList();
        List<?> failList = result.getFailList();
        if (ObjectUtil.isNotEmpty(totalList)) {
            String outputDir = WebAppEnvs.get().webRootDownResource + "upload" + File.separator + "batch" + File.separator + siteId + File.separator + "output" + File.separator;
            if (FileUtil.isDirectory(outputDir) || FileUtil.mkdir(outputDir).isDirectory()) {
                String output = outputDir + RandomUtil.randomNumbers(10) + ".csv";
                writeCsv(output, totalList, JSONUtil.parseObj(delivery.getData()).getStr("type"));
                delivery.setOutput(output.replace(WebAppEnvs.get().webRootDownResource, ""));
            }
        }

        if (ObjectUtil.isNotEmpty(failList)) {
            String outputDir = WebAppEnvs.get().webRootDownResource + "upload" + File.separator + "batch" + File.separator + siteId + File.separator + "fail_output" + File.separator;
            if (FileUtil.isDirectory(outputDir) || FileUtil.mkdir(outputDir).isDirectory()) {
                String output = outputDir + RandomUtil.randomNumbers(10) + ".csv";
                writeCsv(output, failList, JSONUtil.parseObj(delivery.getData()).getStr("type"));
                delivery.setFailOutput(output.replace(WebAppEnvs.get().webRootDownResource, ""));
            }
        }

        mallOrderBatchDeliveryMapper.updateById(delivery);
    }

    @Override
    public OrderBatchDeliveryVo delivery(OrderBatchDeliveryParam param) {
        int totalNum = 0, successNum = 0, failNum = 0;
        OrderBatchDeliveryVo vo = new OrderBatchDeliveryVo();
        if (ObjectUtil.isNotEmpty(param.getOrderIds())) {
            vo.setSuccessNum(successNum);
            vo.setFailNum(failNum);
            vo.setTotalNum(totalNum);
            vo.setTotalList(Collections.emptyList());
            vo.setFailList(Collections.emptyList());
            vo.setFailRemark("");
            return vo;
        }

        OrderBatchDeliveryData data = param.getData();
        String path = WebAppEnvs.get().webRootDownResource + data.getPath();
        if (!FileUtil.isFile(new File(path))) {
            vo.setFailRemark("文件不存在");
            return vo;
        }

        DeliveryBatchReadListener listener = new DeliveryBatchReadListener(param.getSiteId(), param.getMainId());
        EasyExcel.read(path, OrderBatchDeliveryRow.class, listener).sheet().doRead();
        vo.setSuccessNum(listener.getSuccessNum().get());
        vo.setFailNum(listener.getFailNum().get());
        vo.setTotalNum(listener.getTotalNum().get());
        vo.setTotalList(listener.getTotalList());
        vo.setFailList(listener.getFailList());
        return vo;
    }

    /**
     * 批量选择发货（针对无需物流）
     *
     * @param params 参数包含 main_id, site_id, data.list
     * @return 处理结果统计
     */
    public OrderBatchDeliveryVo batchSelectDelivery(OrderBatchDeliveryParam params) {
        Integer mainId = params.getMainId();
        Integer siteId = params.getSiteId();
        List<OrderBatchDeliveryData.Orders> deliveryData = params.getData().getList();

        int totalNum = 0;
        int successNum = 0;
        int failCount = 0;
        List<OrderBatchSelectDeliveryResultRow> totalList = new ArrayList<>();
        List<OrderBatchSelectDeliveryResultRow> failList = new ArrayList<>();
        String failRemark = null;

        // 表头
        String deliveryType = OrderDeliveryTypeEnum.NONE_EXPRESS.getType();
        int expressCompanyId = 0;
        String expressNumber = "";

        try {
            for (OrderBatchDeliveryData.Orders item : deliveryData) {
                String error = null;
                totalNum++;

                Integer orderId = item.getOrderId();
                List<Integer> orderGoodsIds = item.getOrderGoodsIds();
                MallOrder order = null;
                if (orderId != null && orderGoodsIds != null && !orderGoodsIds.isEmpty()) {
                    // 查询订单
                    order = mallOrderMapper.selectOne(new LambdaQueryWrapper<MallOrder>()
                            .eq(MallOrder::getSiteId, siteId)
                            .eq(MallOrder::getOrderId, orderId));
                    if (order == null) {
                        error = "订单不存在";
                    } else {
                        // 调用发货服务
                        OrderDeliveryParam deliveryParam = new OrderDeliveryParam();
                        deliveryParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                        deliveryParam.setMainId(mainId);
                        deliveryParam.setSiteId(siteId);
                        deliveryParam.setOrderId(orderId);
                        deliveryParam.setOrderGoodsIds(orderGoodsIds.toArray(Integer[]::new));
                        deliveryParam.setDeliveryType(deliveryType);
                        deliveryParam.setExpressCompanyId(expressCompanyId);
                        deliveryParam.setExpressNumber(expressNumber);
                        deliveryParam.setRemark("");
                        try {
                            coreOrderDeliveryService.delivery(deliveryParam);
                        } catch (CommonException e) {
                            log.info(e.getMessage());
                            log.info(e.toString());
                            error = e.getMessage();
                        }
                    }
                } else {
                    error = "订单不存在";
                }

                // 构建记录行
                OrderBatchSelectDeliveryResultRow tempRow = new OrderBatchSelectDeliveryResultRow();
                tempRow.setOrderNo(order != null ? order.getOrderNo() + "\t" : "");
                tempRow.setOrderId(String.valueOf(orderId));
                tempRow.setOrderItemId(orderGoodsIds != null ? String.join(",", orderGoodsIds.stream().map(String::valueOf).collect(Collectors.toList())) : "");
                tempRow.setDeliveryType(OrderDeliveryTypeEnum.NONE_EXPRESS.getName());

                if (error == null) {
                    tempRow.setResult("成功");
                    tempRow.setRemark("");
                    totalList.add(tempRow);
                    successNum++;
                } else {
                    tempRow.setResult("失败");
                    tempRow.setRemark(error);
                    totalList.add(tempRow);
                    failList.add(tempRow);
                    failCount++;
                }
            }
        } catch (Exception e) {
            failRemark = e.getMessage();
        }

        OrderBatchDeliveryVo result = new OrderBatchDeliveryVo();
        result.setSuccessNum(successNum);
        result.setFailNum(failCount);
        result.setTotalNum(totalNum);
        result.setTotalList(totalList);
        result.setFailList(failList);
        result.setFailRemark(failRemark != null ? failRemark : "");
        return result;
    }

    /**
     * 批量打单发货
     *
     * @param params 参数包含 main_id, site_id, data（包含 delivery_type, express_company_id, electronic_sheet_id, address_id, list）
     * @return 处理结果统计
     */
    public OrderBatchDeliveryVo batchPrintDelivery(OrderBatchDeliveryParam params) {
        Integer mainId = params.getMainId();
        Integer siteId = params.getSiteId();
        OrderBatchDeliveryData deliveryData = params.getData();
        List<OrderBatchDeliveryData.Orders> orderList = deliveryData.getList();

        int totalNum = 0;
        int successNum = 0;
        int failCount = 0;
        List<OrderBatchPrintDeliveryResultRow> totalList = new ArrayList<>();
        List<OrderBatchPrintDeliveryResultRow> failList = new ArrayList<>();
        String failRemark = null;

        // 提取公共参数
        String deliveryType = deliveryData.getDeliveryType() != null ? deliveryData.getDeliveryType() : OrderDeliveryTypeEnum.EXPRESS.getType();
        Integer expressCompanyId = deliveryData.getExpressCompanyId();
        Integer electronicSheetId = deliveryData.getElectronicSheetId();
//        String deliveryWay = deliveryData.getDeliveryWay() != null ? deliveryData.getDeliveryWay() : "electronic_sheet";
        String deliveryWay = "electronic_sheet";
        Integer addressId = deliveryData.getAddressId();
        String expressNumber = "";

        // 查询关联字典数据
        String deliveryTypeName = OrderDeliveryTypeEnum.getNameByType(deliveryType);
        DeliveryCompany expressCompany = deliveryCompanyMapper.selectOne(
                new LambdaQueryWrapper<DeliveryCompany>().eq(DeliveryCompany::getCompanyId, expressCompanyId)
        );
        String expressCompanyName = expressCompany.getCompanyName();
        String electronicSheetName = deliveryElectronicSheetMapper.selectOne(
                new LambdaQueryWrapper<DeliveryElectronicSheet>().eq(DeliveryElectronicSheet::getId, electronicSheetId)
        ).getTemplateName();
        SiteAddress address = siteAddressMapper.selectById(addressId);

        try {
            for (OrderBatchDeliveryData.Orders item : orderList) {
                String error = null;
                totalNum++;

                Integer orderId = item.getOrderId();
                List<Integer> orderGoodsIds = item.getOrderGoodsIds();
                MallOrder order = null;
                if (orderId != null && orderGoodsIds != null && !orderGoodsIds.isEmpty()) {
                    order = mallOrderMapper.selectOne(new LambdaQueryWrapper<MallOrder>()
                            .eq(MallOrder::getSiteId, siteId)
                            .eq(MallOrder::getOrderId, orderId));
                    if (order == null) {
                        error = "订单不存在";
                    } else {
                        OrderDeliveryParam deliveryParam = new OrderDeliveryParam();


                        deliveryParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                        deliveryParam.setMainId(mainId);
                        deliveryParam.setSiteId(siteId);
                        deliveryParam.setOrderId(orderId);
                        deliveryParam.setOrderGoodsIds(orderGoodsIds.toArray(Integer[]::new));
                        deliveryParam.setDeliveryType(deliveryType);
                        deliveryParam.setExpressCompanyId(expressCompanyId);
                        deliveryParam.setExpressNumber(expressNumber);
                        deliveryParam.setDeliveryWay(deliveryWay);
                        deliveryParam.setElectronicSheetId(electronicSheetId);
                        deliveryParam.setAddress(toSenderAddress(address));
                        deliveryParam.setRemark("");
                        coreOrderDeliveryService.delivery(deliveryParam);
                    }
                } else {
                    error = "订单不存在";
                }

                OrderBatchPrintDeliveryResultRow tempRow = new OrderBatchPrintDeliveryResultRow();
                tempRow.setOrderNo(order != null ? order.getOrderNo() + "\t" : "");
                tempRow.setOrderId(String.valueOf(orderId));
                tempRow.setOrderItemId(orderGoodsIds != null ? String.join(",", orderGoodsIds.stream().map(String::valueOf).collect(Collectors.toList())) : "");
                tempRow.setDeliveryType(deliveryTypeName);
                tempRow.setExpressCompanyName(expressCompanyName);
                tempRow.setElectronicSheetName(electronicSheetName);
                tempRow.setAddress(address != null ? address.getFullAddress() : "");

                if (error == null) {
                    tempRow.setResult("成功");
                    tempRow.setRemark("");
                    totalList.add(tempRow);
                    successNum++;
                } else {
                    tempRow.setResult("失败");
                    tempRow.setRemark(error);
                    totalList.add(tempRow);
                    failList.add(tempRow);
                    failCount++;
                }
            }
        } catch (Exception e) {
            failRemark = e.getMessage();
        }

        OrderBatchDeliveryVo result = new OrderBatchDeliveryVo();
        result.setSuccessNum(successNum);
        result.setFailNum(failCount);
        result.setTotalNum(totalNum);
        result.setTotalList(totalList);
        result.setFailList(failList);
        result.setFailRemark(failRemark != null ? failRemark : "");
        return result;
    }

    private void writeCsv(String output, List<?> list, String type) {
        Map<String, String> header = Arrays.stream(OrderBatchDeliveryResultRow.class.getDeclaredFields())
                .peek(f -> f.setAccessible(true))
                .filter(f -> {
                    // 跳过 order 类型下的 orderItemId 字段
                    if ("order".equals(type) && "orderItemId".equals(f.getName())) {
                        return false;
                    }
                    ExcelProperty excelProperty = f.getAnnotation(ExcelProperty.class);
                    if (excelProperty == null) return false;
                    String[] values = excelProperty.value();
                    return values != null && values.length > 0;
                })
                .collect(Collectors.toMap(
                        Field::getName,
                        f -> {
                            ExcelProperty excelProperty = f.getAnnotation(ExcelProperty.class);
                            // 此处不会为 null，因为 filter 已保证
                            return excelProperty.value()[0]; // 如果 value()[0] 可能为 null，需额外处理
                        },
                        (a, b) -> a,  // 重复 key 保留第一个
                        LinkedHashMap::new
                ));

        CsvWriteConfig config = new CsvWriteConfig().setHeaderAlias(header);

        try (FileWriter fileWriter = new FileWriter(output)) {
            CsvWriter writer = CsvUtil.getWriter(fileWriter, config);
            writer.writeBeans(list);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class DeliveryBatchReadListener implements ReadListener<OrderBatchDeliveryRow> {
        @Getter
        private final AtomicInteger totalNum = new AtomicInteger(0);
        @Getter
        private final AtomicInteger successNum = new AtomicInteger(0);
        @Getter
        private final AtomicInteger failNum = new AtomicInteger(0);
        @Getter
        private final List<OrderBatchDeliveryResultRow> totalList = Collections.synchronizedList(new LinkedList<>());
        @Getter
        private final List<OrderBatchDeliveryResultRow> failList = Collections.synchronizedList(new LinkedList<>());

        private final Integer mainId;
        private final Integer siteId;
        private final Map<String, Integer> orderIdListMap = new ConcurrentHashMap<>(32, 0.9F);
        private final Map<String, DeliveryCompany> companyMap = new ConcurrentHashMap<>(32, 0.9F);

        public DeliveryBatchReadListener(Integer siteId, Integer mainId) {
            this.siteId = siteId;
            this.mainId = mainId;

            QueryWrapper<DeliveryCompany> queryWrapper = new QueryWrapper<>();
            List<DeliveryCompany> companies = deliveryCompanyMapper.selectList(queryWrapper);
            companyMap.putAll(companies.stream().collect(Collectors.toMap(DeliveryCompany::getCompanyName, o -> o)));
        }

        @Override
        public void invoke(OrderBatchDeliveryRow row, AnalysisContext context) {
            String orderNo = row.getOrderNo();
            if (ObjectUtil.isEmpty(orderNo)) {
                return;
            }

            OrderBatchDeliveryResultRow result = new OrderBatchDeliveryResultRow();
            BeanUtils.copyProperties(row, result);
            result.setOrderNo(orderNo + "\t");

            try {
                Integer itemOrderId = orderIdListMap.getOrDefault(orderNo, 0);
                if (itemOrderId == 0) {
                    QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("site_id", siteId)
                            .eq("order_no", orderNo);
                    MallOrder order = mallOrderMapper.selectOne(queryWrapper);
                    if (order == null) {
                        result.setResult("失败");
                        failNum.addAndGet(1);
                        result.setDesc("订单不存在");
                        failList.add(result);
                        totalList.add(result);
                        return;
                    }

                    itemOrderId = order.getOrderId();
                    orderIdListMap.put(orderNo, order.getOrderId());
                }

                Integer itemExpressCompanyId = 0;
                OrderDeliveryTypeEnum deliveryType = OrderDeliveryTypeEnum.NONE_EXPRESS;
                if (!row.getCompanyName().equals("无需物流")) {
                    deliveryType = OrderDeliveryTypeEnum.EXPRESS;
                    if (!companyMap.containsKey(row.getCompanyName())) {
                        throw new RuntimeException("物流公司不存在");
                    }
                    itemExpressCompanyId = companyMap.getOrDefault(row.getCompanyName(), new DeliveryCompany()).getCompanyId();
                }

                QueryWrapper<MallOrderGoods> goodsQueryWrapper = new QueryWrapper<>();
                goodsQueryWrapper.eq("order_id", itemOrderId);
                List<Integer> orderGoodsIds = mallOrderGoodsMapper.selectList(goodsQueryWrapper).stream().map(MallOrderGoods::getOrderGoodsId).toList();

                OrderDeliveryParam param = new OrderDeliveryParam();
                param.setOrderId(itemOrderId);
                param.setSiteId(siteId);
                param.setOrderGoodsIds(orderGoodsIds.toArray(new Integer[0]));
                param.setDeliveryType(deliveryType.getType());
                param.setDeliveryWay("manual_write");
                param.setExpressCompanyId(itemExpressCompanyId);
                param.setExpressNumber(row.getExpressNo());
                param.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                param.setMainId(mainId);
                coreOrderDeliveryService.delivery(param);

                result.setResult("成功");
                successNum.addAndGet(1);
            } catch (Exception e) {
                result.setDesc(e.getMessage());
                result.setResult("失败");
                failList.add(result);
                failNum.addAndGet(1);
            } finally {
                totalNum.addAndGet(1);
                totalList.add(result);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        }
    }

    private ShopDeliveryPrintElectronicSheetParam.SenderAddress toSenderAddress(SiteAddress siteAddress) {
        if (siteAddress == null) {
            return null;
        }
        ShopDeliveryPrintElectronicSheetParam.SenderAddress address = new ShopDeliveryPrintElectronicSheetParam.SenderAddress();
        address.setContactName(siteAddress.getContactName());
        address.setMobile(siteAddress.getMobile());
        address.setProvinceId(siteAddress.getProvinceId());
        address.setCityId(siteAddress.getCityId());
        address.setDistrictId(siteAddress.getDistrictId());
        address.setAddress(siteAddress.getAddress());
        return address;
    }

}
