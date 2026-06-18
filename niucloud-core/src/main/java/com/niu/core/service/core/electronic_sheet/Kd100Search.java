package com.niu.core.service.core.electronic_sheet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.service.core.electronic_sheet.param.Kd100Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Kd100Search {

    private static final Logger log = LoggerFactory.getLogger(Kd100Search.class);
    private static final String URL = "https://api.kuaidi100.com/label/order";

    private final String key;
    private final String secret;

    /**
     * 构造方法，必须传入 key 和 secret
     *
     * @param key    快递100 key
     * @param secret 快递100 secret
     * @throws CommonException 如果任一参数为空
     */
    public Kd100Search(String key, String secret) {
        if (key == null || key.trim().isEmpty() || secret == null || secret.trim().isEmpty()) {
            throw new CommonException("NOT_CONFIGURED_DELIVERY_KD100");
        }
        this.key = key;
        this.secret = secret;
    }


    /**
     * 电子面单下单
     *
     * @param data Kd100Param 请求参数，结构与原 PHP $data 一致
     * @return 结果 Map，包含 success, reason, result_code, print_template, order_info 等
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> electronicSheet(Kd100Param data) {
        // 获取配置信息
        Kd100Param.EsTemplateInfo esTemplateInfo = data.getEsTemplateInfo();
        Kd100Param.KD100Config kd100Config = esTemplateInfo.getKd100Config();

        // 构建 template_info
        Map<String, Object> templateInfo = new HashMap<>();
        templateInfo.put("printType", "HTML");   // 打印类型
        templateInfo.put("partnerId", kd100Config.getMonthCode());
        templateInfo.put("partnerKey", kd100Config.getCustomerPwd());
        templateInfo.put("partnerSecret", kd100Config.getMonthCode());
        templateInfo.put("partnerName", kd100Config.getCustomerName());
        templateInfo.put("code", kd100Config.getSendSite());
        templateInfo.put("checkMan", kd100Config.getSendStaff());
        templateInfo.put("payType", kd100Config.getPayType());
        templateInfo.put("expType", esTemplateInfo.getExpType());

        // 收件人信息
        Kd100Param.PersonnelInfo receiver = data.getReceiver();
        Map<String, String> recMan = new HashMap<>();
        recMan.put("name", receiver.getName());
        recMan.put("mobile", receiver.getMobile());
        recMan.put("printAddr", receiver.getFullAddr());

        // 寄件人信息
        Kd100Param.PersonnelInfo sender = data.getSender();
        Map<String, String> sendMan = new HashMap<>();
        sendMan.put("name", sender.getName());
        sendMan.put("mobile", sender.getMobile());
        sendMan.put("printAddr", sender.getFullAddr());

        // 构建 param 数据
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("kuaidicom", esTemplateInfo.getCompany().getKd100ExpressNoElectronicSheet());
        paramData.put("recMan", recMan);
        paramData.put("sendMan", sendMan);
        paramData.put("cargo", "商品");
        paramData.put("count", data.getQuantity());
        paramData.put("weight", data.getWeight());
        paramData.put("remark", "");
        paramData.put("tempId", esTemplateInfo.getTempId());
        paramData.put("childTempId", esTemplateInfo.getChildTempId());
        paramData.put("backTempId", esTemplateInfo.getBackTempId());

        // 合并 templateInfo 和 paramData
        Map<String, Object> allParams = new HashMap<>();
        allParams.putAll(templateInfo);
        allParams.putAll(paramData);

        // 构造 POST 请求参数
        Map<String, String> postData = new HashMap<>();
        postData.put("method", "order");
        try {
            String paramJson = new ObjectMapper().writeValueAsString(allParams);
            postData.put("param", paramJson);
            postData.put("key", this.key);
            long t = System.currentTimeMillis() / 1000;
            postData.put("t", String.valueOf(t));

            // 生成签名
            String signStr = paramJson + t + this.key + this.secret;
            String sign = md5(signStr).toUpperCase();
            postData.put("sign", sign);

            // 发送请求
            String responseBody = sendPost(URL, postData);
            log.info("快递100电子面单返回数据：{}", responseBody);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> result = mapper.readValue(responseBody, Map.class);

            // 组装返回结果
            Map<String, Object> res = new HashMap<>();
            if (result.containsKey("code") && Integer.valueOf(200).equals(result.get("code"))) {
                res.put("success", true);
                res.put("result_code", 200);
                Map<String, Object> dataResult = (Map<String, Object>) result.get("data");
                res.put("print_template", dataResult.get("label"));
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("LogisticCode", dataResult.get("kuaidinum"));
                res.put("order_info", orderInfo);
            } else {
                res.put("success", false);
                res.put("result_code", result.getOrDefault("code", 105));
                res.put("reason", result.getOrDefault("message", "未知错误"));
            }
            return res;
        } catch (Exception e) {
            log.error("电子面单请求失败", e);
            Map<String, Object> errorRes = new HashMap<>();
            errorRes.put("success", false);
            errorRes.put("result_code", 500);
            errorRes.put("reason", e.getMessage());
            return errorRes;
        }
    }

    /**
     * 电子面单下单
     *
     * @param data 请求参数，结构与原 PHP $data 一致
     * @return 结果 Map，包含 success, reason, result_code, print_template, order_info 等
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> electronicSheet(Map<String, Object> data) {
        // 获取配置信息
        Map<String, Object> esTemplateInfo = (Map<String, Object>) data.get("es_template_info");
        Map<String, Object> kd100Config = (Map<String, Object>) esTemplateInfo.get("kd100_config");

        // 构建 template_info
        Map<String, Object> templateInfo = new HashMap<>();
        templateInfo.put("printType", "HTML");   // 打印类型
        templateInfo.put("partnerId", kd100Config.get("month_code"));
        templateInfo.put("partnerKey", kd100Config.get("customer_pwd"));
        templateInfo.put("partnerSecret", kd100Config.get("month_code"));
        templateInfo.put("partnerName", kd100Config.get("customer_name"));
        templateInfo.put("code", kd100Config.get("send_site"));
        templateInfo.put("checkMan", kd100Config.get("send_staff"));
        templateInfo.put("payType", kd100Config.get("pay_type"));
        templateInfo.put("expType", esTemplateInfo.get("exp_type"));

        // 收件人信息
        Map<String, Object> receiver = (Map<String, Object>) data.get("Receiver");
        String receiverFullAddr = concatAddress(receiver);
        Map<String, String> recMan = new HashMap<>();
        recMan.put("name", (String) receiver.get("Name"));
        recMan.put("mobile", (String) receiver.get("Mobile"));
        recMan.put("printAddr", receiverFullAddr);

        // 寄件人信息
        Map<String, Object> sender = (Map<String, Object>) data.get("Sender");
        String senderFullAddr = concatAddress(sender);
        Map<String, String> sendMan = new HashMap<>();
        sendMan.put("name", (String) sender.get("Name"));
        sendMan.put("mobile", (String) sender.get("Mobile"));
        sendMan.put("printAddr", senderFullAddr);

        // 构建 param 数据
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("kuaidicom", ((Map<String, Object>) esTemplateInfo.get("company")).get("kd100_express_no_electronic_sheet"));
        paramData.put("recMan", recMan);
        paramData.put("sendMan", sendMan);
        paramData.put("cargo", "商品");
        paramData.put("count", data.get("Quantity"));
        paramData.put("weight", data.get("Weight"));
        paramData.put("remark", "");
        paramData.put("tempId", esTemplateInfo.get("temp_id"));
        paramData.put("childTempId", esTemplateInfo.get("child_temp_id"));
        paramData.put("backTempId", esTemplateInfo.get("back_temp_id"));

        // 合并 templateInfo 和 paramData
        Map<String, Object> allParams = new HashMap<>();
        allParams.putAll(templateInfo);
        allParams.putAll(paramData);

        // 构造 POST 请求参数
        Map<String, String> postData = new HashMap<>();
        postData.put("method", "order");
        try {
            String paramJson = new ObjectMapper().writeValueAsString(allParams);
            postData.put("param", paramJson);
            postData.put("key", this.key);
            long t = System.currentTimeMillis() / 1000;
            postData.put("t", String.valueOf(t));

            // 生成签名
            String signStr = paramJson + t + this.key + this.secret;
            String sign = md5(signStr).toUpperCase();
            postData.put("sign", sign);

            // 发送请求
            String responseBody = sendPost(URL, postData);
            log.info("快递100电子面单返回数据：{}", responseBody);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> result = mapper.readValue(responseBody, Map.class);

            // 组装返回结果
            Map<String, Object> res = new HashMap<>();
            if (result.containsKey("code") && Integer.valueOf(200).equals(result.get("code"))) {
                res.put("success", true);
                res.put("result_code", 200);
                Map<String, Object> dataResult = (Map<String, Object>) result.get("data");
                res.put("print_template", dataResult.get("label"));
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("LogisticCode", dataResult.get("kuaidinum"));
                res.put("order_info", orderInfo);
            } else {
                res.put("success", false);
                res.put("result_code", result.getOrDefault("code", 105));
                res.put("reason", result.getOrDefault("message", "未知错误"));
            }
            return res;
        } catch (Exception e) {
            log.error("电子面单请求失败", e);
            Map<String, Object> errorRes = new HashMap<>();
            errorRes.put("success", false);
            errorRes.put("result_code", 500);
            errorRes.put("reason", e.getMessage());
            return errorRes;
        }
    }

    /**
     * 拼接完整地址：省+市+区+详细地址
     */
    private String concatAddress(Map<String, Object> addrMap) {
        return (String) addrMap.getOrDefault("ProvinceName", "") +
                (String) addrMap.getOrDefault("CityName", "") +
                (String) addrMap.getOrDefault("ExpAreaName", "") +
                (String) addrMap.getOrDefault("Address", "");
    }

    /**
     * 发送 POST application/x-www-form-urlencoded 请求
     *
     * @param url  请求地址
     * @param data 表单数据
     * @return 响应体字符串
     * @throws IOException          网络异常
     * @throws InterruptedException 请求中断
     */
    private String sendPost(String url, Map<String, String> data) throws IOException, InterruptedException {
        String formBody = data.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                        URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * MD5 加密
     *
     * @param input 输入字符串
     * @return 32位小写MD5
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不存在", e);
        }
    }
}
