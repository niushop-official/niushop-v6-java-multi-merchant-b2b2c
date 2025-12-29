package com.niu.core.service.core.delivery_package.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.AdminException;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.entity.delivery.DeliveryTradePackage;
import com.niu.core.mapper.delivery.DeliveryTradePackageMapper;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.core.service.core.delivery_package.ICoreDeliveryPackageService;
import com.niu.core.service.core.delivery_search.ICoreDeliverySearchService;
import com.niu.core.service.core.delivery_search.param.DeliverySearchParam;
import com.niu.core.service.core.delivery_search.vo.DeliveryTraceVo;
import com.niu.core.service.core.delivery_package.vo.DeliveryPackageVo;
import com.niu.core.enums.delivery_package.PackageStatusEnum;
import org.springframework.beans.BeanUtils;
import com.niu.core.common.utils.CommonUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 物流查询服务层
 */
@Service
public class CoreDeliveryPackageServiceImpl implements ICoreDeliveryPackageService {

    @Resource
    private DeliveryTradePackageMapper deliveryTradePackageMapper;

    @Override
    public String create(Map<String, Object> data, String tradeType) {
        data.put("status", PackageStatusEnum.DELIVERYING.getStatus());
        data.put("trade_type", tradeType);
        String code = CommonUtils.createNo();
        data.put("code", code);
        DeliveryTradePackage deliveryTradePackage = new DeliveryTradePackage();
        deliveryTradePackage.setCode(code);
        deliveryTradePackage.setTradeType(tradeType);
        deliveryTradePackage.setStatus(PackageStatusEnum.DELIVERYING.getStatus());
        deliveryTradePackage.setTradeNo((String) data.get("trade_no"));
        deliveryTradePackage.setSiteId((Integer) data.get("site_id"));
        deliveryTradePackage.setDeliveryType((String) data.get("delivery_type"));
        deliveryTradePackage.setSubDeliveryType((String) data.get("sub_delivery_type"));
        // 安全处理express_company_id：如果为空字符串或null，设置为null
        Object expressCompanyIdObj = data.get("express_company_id");
        if (expressCompanyIdObj != null) {
            String expressCompanyIdStr = expressCompanyIdObj.toString();
            if (expressCompanyIdStr != null && !expressCompanyIdStr.trim().isEmpty()) {
                try {
                    deliveryTradePackage.setExpressCompanyId(Integer.valueOf(expressCompanyIdStr));
                } catch (NumberFormatException e) {
                    deliveryTradePackage.setExpressCompanyId(null);
                }
            } else {
                deliveryTradePackage.setExpressCompanyId(null);
            }
        } else {
            deliveryTradePackage.setExpressCompanyId(null);
        }
        deliveryTradePackage.setExpressNumber((String) data.get("express_number"));
        deliveryTradePackage.setRemark((String) data.get("remark"));
        
        deliveryTradePackageMapper.insert(deliveryTradePackage);
        
        return code;
    }

    @Override
    public List<DeliveryPackageVo> getDeliveryPackageByCodes(List<String> codes, String tradeType) {
        MPJQueryWrapper<DeliveryTradePackage> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("d")
                .leftJoin("?_delivery_company dm ON dm.company_id = d.express_company_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("d.id, d.trade_no, d.code, d.site_id, d.name, d.delivery_type, d.sub_delivery_type, d.express_company_id, d.express_number, d.local_deliver_id, d.status, d.create_time");
        queryWrapper.select("dm.company_id, dm.company_name, dm.express_no");
        queryWrapper.in("d.code", codes);
        queryWrapper.eq("d.trade_type", tradeType);
        // 直接查询为VO对象
        List<DeliveryPackageVo> resultList = deliveryTradePackageMapper.selectJoinList(DeliveryPackageVo.class, queryWrapper);

        
        return resultList;
    }
    
    @Override
    public List<DeliveryPackageVo> getPackageListOfTrade(Integer orderId, String tradeType) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        
        QueryWrapper<DeliveryTradePackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("trade_type", tradeType);
        queryWrapper.eq("trade_no", orderId.toString());
        
        List<DeliveryTradePackage> packages = deliveryTradePackageMapper.selectList(queryWrapper);
        
        List<DeliveryPackageVo> resultList = new ArrayList<>();
        for (DeliveryTradePackage pkg : packages) {
            DeliveryPackageVo vo = new DeliveryPackageVo();
            BeanUtils.copyProperties(pkg, vo);
            resultList.add(vo);
        }
        
        return resultList;
    }
}
