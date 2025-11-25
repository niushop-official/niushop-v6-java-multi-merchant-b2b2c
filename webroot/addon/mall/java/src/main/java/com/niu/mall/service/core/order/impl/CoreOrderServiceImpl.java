package com.niu.mall.service.core.order.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.mall.service.core.order.*;
import com.niu.mall.service.core.order.param.*;
import com.niu.mall.service.core.order.vo.DeliveryTraceVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class CoreOrderServiceImpl implements ICoreOrderService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;

    @Override
    public MallOrder orderInfo(Integer orderId) {
        return mallOrderMapper.selectById(orderId);
    }
    /**
     *
     * 查询物流信息
     * @param param
     */
    @Override
    public DeliveryTraceVo deliverySearch(DeliverySearchParam param) {
        DeliverySearchConfigVo config = coreDeliveryConfigService.getDeliverySearchConfig();

        // TODO:: 暂时先固定查询快递鸟接口 后期考虑扩展
        Map<String, String> RequestData = new HashMap<>();
        RequestData.put("ShipperCode", param.getExpressNo());
        RequestData.put("LogisticCode", param.getLogisticNo());
        RequestData.put("CustomerName", ObjectUtil.isNotEmpty( param.getMobile()) ? param.getMobile().substring(param.getMobile().length() - 4) : "");

        Map<String, String> data = new HashMap<>();
        data.put("EBusinessID", config.getKdniaoId());
        data.put("RequestType", config.getKdniaoIsPay().equals(2) ? "8001" : "1002");
        data.put("RequestData", JSONUtil.parseObj(RequestData).toString());
        data.put("DataType", "2");
        data.put("DataSign", Base64.encode(SecureUtil.md5((String) data.get("RequestData") + config.getKdniaoAppKey()).getBytes()));

        String result = HttpRequest
                .post("https://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx")
                .formStr(data)
                .execute()
                .body();

        DeliveryTraceVo vo = new DeliveryTraceVo();
        if (!JSONUtil.isJson(result)) return vo;

        JSONObject resultData = JSONUtil.parseObj(result);

        if (resultData.getBool("Success", false)) {
            vo.setSuccess(true);
            List<DeliveryTraceVo.Trace> traceList = new LinkedList<>();
            JSONArray traces = resultData.getJSONArray("Traces");
            for (int i = traces.size() - 1; i >= 0; i--) {
                JSONObject trace = traces.getJSONObject(i);
                DeliveryTraceVo.Trace traceVo = new DeliveryTraceVo.Trace();
                traceVo.setDatetime(trace.getStr("AcceptTime", ""));
                traceVo.setRemark(trace.getStr("AcceptStation", ""));
                traceList.add(traceVo);
            }
            vo.setList(traceList);
        } else {
            vo.setReason(resultData.getStr("Reason", "物流跟踪配置错误"));
        }

        return vo;
    }
}
