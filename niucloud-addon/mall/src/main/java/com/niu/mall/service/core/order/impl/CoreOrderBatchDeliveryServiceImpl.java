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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderBatchDelivery;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderBatchDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.mall.mapper.order.MallOrderBatchDeliveryMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.site.order.param.MallOrderAddBatchDeliveryParam;
import com.niu.mall.service.core.order.ICoreOrderBatchDeliveryService;
import com.niu.mall.service.core.order.ICoreOrderDeliveryService;
import com.niu.mall.service.core.order.model.OrderBatchDeliveryData;
import com.niu.mall.service.core.order.model.OrderBatchDeliveryResultRow;
import com.niu.mall.service.core.order.model.OrderBatchDeliveryRow;
import com.niu.mall.service.core.order.param.OrderBatchDeliveryParam;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;
import com.niu.mall.service.core.order.vo.OrderBatchDeliveryVo;
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

    @Override
    public void add(MallOrderAddBatchDeliveryParam param) {
        MallOrderBatchDelivery delivery = new MallOrderBatchDelivery();
        BeanUtils.copyProperties(param, delivery);
        delivery.setData(JSONUtil.toJsonStr(param.getData()));
        delivery.setStatus(OrderBatchDeliveryStatusEnum.PROCESSING.getValue());
        delivery.setCreateTime(System.currentTimeMillis() / 1000);
        delivery.setType(param.getType().getValue());
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
        if (delivery.getType().equals("delivery")) {
            OrderBatchDeliveryParam param = new OrderBatchDeliveryParam();
            param.setSiteId(siteId);
            param.setMainId(delivery.getMainId());
            param.setData(JSONUtil.toBean(delivery.getData(), OrderBatchDeliveryData.class));
            result = delivery(param);
        }

        OrderBatchDeliveryStatusEnum status = ObjectUtil.isEmpty(result.getFailRemark()) ? OrderBatchDeliveryStatusEnum.FINISH : OrderBatchDeliveryStatusEnum.FAIL;
        delivery.setStatus(status.getValue());
        delivery.setTotalNum(result.getTotalNum());
        delivery.setSuccessNum(result.getSuccessNum());
        delivery.setFailNum(result.getFailNum());
        delivery.setFailRemark(result.getFailRemark());

        if (ObjectUtil.isNotEmpty(result.getTotalList())) {
            String outputDir = WebAppEnvs.get().webRootDownResource + "upload" + File.separator + "batch" + File.separator + siteId + File.separator + "output" + File.separator;
            if (FileUtil.isDirectory(outputDir) || FileUtil.mkdir(outputDir).isDirectory()) {
                String output = outputDir + RandomUtil.randomNumbers(10) + ".csv";
                writeCsv(output, result.getTotalList(), JSONUtil.parseObj(delivery.getData()).getStr("type"));
                delivery.setOutput(output.replace(WebAppEnvs.get().webRootDownResource, ""));
            }
        }

        List<OrderBatchDeliveryResultRow> failList = result.getFailList();
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

    private void writeCsv(String output, List<?> list, String type) {
        Map<String, String> header = Arrays.stream(OrderBatchDeliveryResultRow.class.getDeclaredFields())
                .peek(f -> f.setAccessible(true))
                .filter(f -> {
                    if (type.equals("order") && f.getName().equals("orderItemId")) {
                        return false;
                    }

                    String[] value = f.getAnnotation(ExcelProperty.class).value();
                    return value != null && value.length > 0;
                })
                .collect(Collectors.toMap(Field::getName, o -> o.getAnnotation(ExcelProperty.class).value()[0],
                        (a, b) -> a, LinkedHashMap::new));

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

}
