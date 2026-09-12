package com.niu.core.service.admin.delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.component.context.event.CallbackPublisher;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryTradePackage;
import com.niu.core.enums.order.OrderDeliveryEnum;
import com.niu.core.mapper.delivery.DeliveryTradePackageMapper;
import com.niu.core.service.admin.delivery.IDeliveryPackageService;
import com.niu.core.service.admin.delivery.param.TradePackageSearchParam;
import com.niu.core.service.admin.delivery.vo.DeliveryPackageInfoVo;
import com.niu.core.service.admin.delivery.vo.DeliveryPackageListVo;
import com.niu.core.service.core.delivery_search.ICoreDeliverySearchService;
import com.niu.core.service.core.delivery_search.param.DeliverySearchParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 配送包裹服务层
 */
@Service
public class DeliveryPackageServiceImpl implements IDeliveryPackageService {

    @Resource
    DeliveryTradePackageMapper deliveryTradePackageMapper;
    @Resource
    ICoreDeliverySearchService coreDeliverySearchService;

    @Override
    public List<DeliveryPackageListVo> getDeliveryPackageList(String tradeNo, String tradeType) {
        MPJQueryWrapper<DeliveryTradePackage> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("d")
                .innerJoin("?_delivery_company dm ON dm.company_id = d.express_company_id ".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("d.id, d.trade_no, d.code,d.name, d.delivery_type, d.sub_delivery_type,d.express_company_id, d.express_number, d.create_time");
        queryWrapper.select("dm.company_id, dm.company_name, dm.express_no");
        queryWrapper.eq("d.site_id", RequestUtils.siteId());
        queryWrapper.eq("d.trade_no", tradeNo);
        List<DeliveryPackageListVo> list = deliveryTradePackageMapper.selectJoinList(DeliveryPackageListVo.class, queryWrapper);
        return list;
    }

    @Override
    public DeliveryPackageInfoVo getDeliveryPackage(TradePackageSearchParam data, String tradeType) {
        MPJQueryWrapper<DeliveryTradePackage> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("d")
                .innerJoin("?_delivery_company dm ON dm.company_id = d.express_company_id ".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("d.id, d.trade_no,d.site_id, d.code,d.name, d.delivery_type, d.sub_delivery_type,d.express_company_id, d.express_number, d.local_deliver_id, d.status, d.create_time as createTime");
        queryWrapper.select("dm.company_id, dm.company_name, dm.express_no");
        queryWrapper.eq("d.code", data.getCode());
        queryWrapper.eq("d.trade_type", tradeType);
        queryWrapper.last("LIMIT 1");
        DeliveryPackageInfoVo info = deliveryTradePackageMapper.selectJoinOne(DeliveryPackageInfoVo.class, queryWrapper);
        
        if (info != null) {

            DeliveryPackageInfoVo.CompanyInfo companyInfo = new DeliveryPackageInfoVo.CompanyInfo();
            companyInfo.setCompanyId(info.getCompanyId());
            companyInfo.setCompanyName(info.getCompanyName() != null ? info.getCompanyName() : "");
            companyInfo.setExpressNo(info.getExpressNo() != null ? info.getExpressNo() : "");
            info.setCompany(companyInfo);
            Long createTime = info.getCreateTime();
            if (createTime == null || createTime == 0) {
                DeliveryTradePackage packageEntity = deliveryTradePackageMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<DeliveryTradePackage>()
                        .eq("code", data.getCode())
                        .eq("trade_type", tradeType)
                        .select("create_time")
                        .last("LIMIT 1")
                );
                if (packageEntity != null && packageEntity.getCreateTime() != null && packageEntity.getCreateTime() > 0) {
                    createTime = packageEntity.getCreateTime().longValue();
                    info.setCreateTime(createTime);
                }
            } else {
            
                info.setCreateTime(createTime);
            }
            info.setDeliveryTime(createTime);

            try {
                Class<?> eventClass = Class.forName("com.niu.points_mall.event.delivery_package.GetTradeItemByPackageEvent");
                Object eventObj = eventClass.getDeclaredConstructor().newInstance();
                
                // 设置事件属性
                BeanUtil.setProperty(eventObj, "tradeType", tradeType);
                BeanUtil.setProperty(eventObj, "codes", CollUtil.newArrayList(data.getCode()));
                BeanUtil.setProperty(eventObj, "name", "GetTradeItemByPackage");
                BeanUtil.setProperty(eventObj, "siteId", RequestUtils.siteId());

                // 发布事件并获取结果
                @SuppressWarnings("unchecked")
                Map<String, EventResult> eventResults = CallbackPublisher.publish((com.niu.core.common.component.context.event.Event) eventObj);

                List<Map<String, Object>> tradeItemList = new ArrayList<>();
                if (CollUtil.isNotEmpty(eventResults)) {
                    for (EventResult eventResult : eventResults.values()) {
                        if (eventResult != null) {
                            try {
                    
                                Object tradeItemMapObj = BeanUtil.getProperty(eventResult, "tradeItemMap");
                                if (tradeItemMapObj instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, List<?>> tradeItemMap = (Map<String, List<?>>) tradeItemMapObj;
                                    List<?> items = tradeItemMap.get(data.getCode());
                                    if (CollUtil.isNotEmpty(items)) {
                                        for (Object item : items) {
                                            // 将实体对象转换为Map，字段名使用下划线命名，与PHP保持一致
                                            Map<String, Object> itemMap = convertToSnakeCaseMap(item);
                                            tradeItemList.add(itemMap);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // 忽略异常
                            }
                        }
                    }
                }
                
                info.setTradeItemList(tradeItemList);
            } catch (Exception e) {
                // 如果事件调用失败，设置为空列表
                info.setTradeItemList(new ArrayList<>());
            }
            
            if (OrderDeliveryEnum.EXPRESS.getValue().equals(info.getDeliveryType()) && !OrderDeliveryEnum.NONE_EXPRESS.getValue().equals(info.getSubDeliveryType())) {
                info.setMobile(data.getMobile());
                //查询物流跟踪
                DeliverySearchParam deliverySearchParam = new DeliverySearchParam();
                deliverySearchParam.setExpressNo(info.getExpressNo());
                deliverySearchParam.setLogisticNo(info.getExpressNumber());
                deliverySearchParam.setMobile(info.getMobile());
                deliverySearchParam.setCompanyId(info.getCompanyId());
                info.setTraces(coreDeliverySearchService.deliverySearch(deliverySearchParam));
            }
        }
        return info;
    }


    private Map<String, Object> convertToSnakeCaseMap(Object item) {
        Map<String, Object> itemMap = BeanUtil.beanToMap(item, false, true);
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            // 将驼峰命名转换为下划线命名
            String snakeKey = camelToSnake(key);
            
            // 确保图片字段始终有值，避免前端img函数报错
            if (("goods_image".equals(snakeKey) || "sku_image".equals(snakeKey)) && value == null) {
                value = "";
            }
            
            resultMap.put(snakeKey, value);
        }
        
        return resultMap;
    }

    /**
     * 驼峰命名转下划线命名
     * 例如：orderGoodsId -> order_goods_id
     */
    private String camelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                // 如果前面已经有字符，添加下划线
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
