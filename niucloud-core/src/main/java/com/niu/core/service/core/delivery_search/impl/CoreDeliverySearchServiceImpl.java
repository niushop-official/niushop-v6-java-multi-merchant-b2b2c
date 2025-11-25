package com.niu.core.service.core.delivery_search.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.AdminException;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.core.service.core.delivery_search.ICoreDeliverySearchService;
import com.niu.core.service.core.delivery_search.param.DeliverySearchParam;
import com.niu.core.service.core.delivery_search.vo.DeliveryTraceVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 物流查询服务层
 */
@Service
public class CoreDeliverySearchServiceImpl implements ICoreDeliverySearchService {

    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;
    @Resource
    DeliveryCompanyMapper deliveryCompanyMapper;

    @Override
    public DeliveryTraceVo deliverySearch(DeliverySearchParam param) {
        DeliverySearchConfigVo config = coreDeliveryConfigService.getDeliverySearchConfig();

        // 判断调用的是哪个快递，暂时现在有快递鸟和快递100俩种类型（快递鸟==1，快递100==2）
        if(config.getInterfaceType()==1){
            // 快递鸟调用
            return this.orderTracesKuaidiniao(config,param);
        } else if (config.getInterfaceType() == 2) {
            // 快递100调用
            return this.orderTraces100(config, param);
        } else {
            //没有其他的配置返回空值
            return new DeliveryTraceVo();
        }
    }

    /**
     * 快递鸟查询物流信息
     * @param config
     * @param param
     * @return
     */
    public DeliveryTraceVo orderTracesKuaidiniao(DeliverySearchConfigVo config, DeliverySearchParam param) {
        Map<String, String> RequestData = new HashMap<>();
        RequestData.put("ShipperCode", param.getExpressNo());
        RequestData.put("LogisticCode", param.getLogisticNo());
        RequestData.put("CustomerName", param.getMobile().substring(param.getMobile().length() - 4));

        Map<String, String> data = new HashMap<>();
        data.put("EBusinessID", config.getKdniaoId());
        data.put("RequestType", config.getKdniaoIsPay().equals(2) ? "8001" : "1002");
        data.put("RequestData", JSONUtil.parseObj(RequestData).toString());
        data.put("DataType", "2");
        data.put("DataSign", Base64.encode(SecureUtil.md5((String) data.get("RequestData") + config.getKdniaoAppKey()).getBytes()));

        String result = HttpRequest
                .post("https://api.kdniao.com/api/dist")
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


    /**
     * 快递100查询物流信息
     * @param config
     * @param deliverySearchParam
     * @return
     */
    public DeliveryTraceVo orderTraces100(DeliverySearchConfigVo config, DeliverySearchParam deliverySearchParam) {

        if(ObjectUtil.isNotEmpty(deliverySearchParam.getCompanyId()) && ObjectUtil.isNotNull(deliverySearchParam.getExpressNo())){
            QueryWrapper<DeliveryCompany> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("company_id", deliverySearchParam.getCompanyId());
            queryWrapper.select("kd100_express_no");
            DeliveryCompany shopDeliveryCompany = deliveryCompanyMapper.selectOne(queryWrapper);
            deliverySearchParam.setExpressNo(shopDeliveryCompany.getKd100ExpressNo());
        }

        // 构建请求参数
        Map<String, Object> param = new HashMap<>();
        param.put("com", deliverySearchParam.getExpressNo());  // 快递公司编码
        param.put("num", deliverySearchParam.getLogisticNo()); // 快递单号

        if (deliverySearchParam.getMobile() != null && !deliverySearchParam.getMobile().isEmpty()) {
            param.put("phone", deliverySearchParam.getMobile()); // 手机号
        }

        // 构建POST数据
        Map<String, String> postData = new HashMap<>();
        postData.put("customer", config.getKd100Customer().toString());
        try {
            // 将参数转换为JSON字符串
            String paramJson = JSONUtil.toJsonStr(param);
            postData.put("param", paramJson);

            // 生成签名
            String sign = SecureUtil.md5(paramJson+config.getKd100AppKey()+config.getKd100Customer().toString());
            postData.put("sign", sign.toUpperCase());

            // 发送POST请求
            String result = HttpRequest
                    .post("https://poll.kuaidi100.com/poll/query.do")
                    .body(JSONUtil.toJsonStr(postData))
                    .execute().body();

            DeliveryTraceVo vo = new DeliveryTraceVo();
            // 解析返回结果 处理返回结果
            if (result != null) {
                JSONObject resultData = JSONUtil.parseObj(result);
                if (Boolean.FALSE.equals(resultData.get("result"))) {
                    vo.setSuccess(false);
                    vo.setReason((String) resultData.get("message"));
                }

                if (resultData.containsKey("status")) {
                    int status = (Integer) resultData.get("status");
                    if (status != 200) {
                        vo.setSuccess(false);
                        vo.setReason((String) resultData.get("message"));
                    } else if (status == 200) {
                        List<DeliveryTraceVo.Trace> list = new ArrayList<>();
                        if (resultData.containsKey("data")) {
                            List<Map<String, String>> data = (List<Map<String, String>>) resultData.get("data");
                            for (Map<String, String> trace : data) {
                                DeliveryTraceVo.Trace item = new DeliveryTraceVo.Trace();
                                item.setDatetime(trace.get("time"));
                                item.setRemark(trace.get("context"));
                                list.add(item);
                            }
                            Collections.reverse(list); // 反转列表，最新的物流信息在前
                        }

                        vo.setSuccess(true);
                        vo.setStatus(status);
                        vo.setList(list);
                    }
                }
            }else{
                vo.setSuccess(false);
                vo.setReason("物流查询失败，请检查快递100配置");
            }
            return vo;
        } catch (Exception e) {
            throw new AdminException("物流查询失败，请检查快递100配置");
        }
    }
}
