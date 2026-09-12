package com.niu.core.service.core.sms.driver;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.loader.sms.BaseSms;
import com.niu.core.common.loader.sms.SendResultVo;
import com.niu.core.entity.sys.NiuSmsTemplate;
import com.niu.core.enums.notice.TemplateParamsTypeEnum;
import com.niu.core.enums.sys.SmsStatusEnum;
import com.niu.core.mapper.sys.NiuSmsTemplateMapper;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Log4j2
public class Nuiyun extends BaseSms {

    private static final String SEND_URL = "https://api-shss.zthysms.com/v2/sendSmsTp";

    private final String username;
    private final String password;
    private final String signature;

    public Nuiyun(JSONObject config) throws Exception {
        super(config);
        this.username = config.getStr("username");
        this.password = config.getStr("password");
        this.signature = config.getStr("signature");
        log.info("牛云短信 init {}", JSONUtil.toJsonStr(config));
    }

    private NiuSmsTemplateMapper niuSmsTemplateMapper() {
        return SpringContext.bean(NiuSmsTemplateMapper.class);
    }

    @Override
    public SendResultVo send(NoticeInfoVo notice, NoticeDataVo noticeData) {
        log.info("牛云短信开始发送, key={}, vars={}", notice.getKey(), JSONUtil.toJsonStr(noticeData.getVars()));

        if (!StringUtils.hasText(signature)) {
            throw new CommonException("短信签名未配置");
        }

        NiuSmsTemplate templateInfo = niuSmsTemplateMapper().selectOne(
                new LambdaQueryWrapper<NiuSmsTemplate>()
                        .eq(NiuSmsTemplate::getTemplateKey, notice.getKey())
        );

        if (ObjectUtil.isEmpty(templateInfo)) {
            log.warn("牛云短信模板不存在, templateKey={}", notice.getKey());
            throw new CommonException("短信模板未配置或未报备: " + notice.getKey());
        }

        log.info("牛云短信模板 {}", templateInfo);

        String auditStatus = templateInfo.getAuditStatus();
        if (StringUtil.isEmpty(auditStatus) || !"2".equals(auditStatus)) {
            throw new CommonException("短信模板审核未通过: " + notice.getKey());
        }

        Map<String, Object> formattedData = formatParams(noticeData.getVars(), templateInfo);

        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> records = new ArrayList<>();
        Map<String, Object> record = new HashMap<>();
        record.put("mobile", noticeData.getMobile());
        record.put("tpContent", formattedData);
        records.add(record);

        params.put("records", records);
        params.put("tpId", templateInfo.getTemplateId());
        params.put("username", username);

        long tKey = System.currentTimeMillis() / 1000;
        params.put("tKey", tKey);
        params.put("password", DigestUtils.md5Hex(DigestUtils.md5Hex(password) + tKey));
        params.put("signature", signature);

        log.info("牛云短信发送参数 {}", JSONUtil.toJsonStr(params));

        SendResultVo sendResult = new SendResultVo();
        try {
            String response = HttpRequest.post(SEND_URL)
                    .body(JSONUtil.toJsonStr(params))
                    .execute()
                    .body();

            log.info("牛云短信返回 {}", response);

            JSONObject result = JSONUtil.parseObj(response);
            Integer code = result.getInt("code");
            if (code != 200) {
                sendResult.setStatus(SmsStatusEnum.FAIL);
                sendResult.setFailReason(result.getStr("msg"));
            } else {
                sendResult.setStatus(SmsStatusEnum.SUCCESS);
            }
        } catch (Exception e) {
            log.error("牛云短信发送失败", e);
            sendResult.setStatus(SmsStatusEnum.FAIL);
            sendResult.setFailReason(e.getMessage());
        }
        return sendResult;
    }

    private Map<String, Object> formatParams(Map<String, Object> data, NiuSmsTemplate templateInfo) {
        String paramJson = templateInfo.getParamJson();
        JSONObject paramsJson = JSONUtil.parseObj(paramJson);
        List<Map<String, Object>> paramsTypeArr = TemplateParamsTypeEnum.getApiParamsType();

        Map<String, Map<String, Object>> typeMap = new HashMap<>();
        for (Map<String, Object> type : paramsTypeArr) {
            typeMap.put(type.get("type").toString(), type);
        }

        Map<String, Object> result = new HashMap<>();
        for (String param : paramsJson.keySet()) {
            String validateType = paramsJson.getStr(param);
            Object valueObj = data.get(param);

            if (valueObj == null) {
                log.error("牛云短信缺少参数, param={}", param);
                throw new CommonException("短信参数异常，无法发送");
            }

            String value = valueObj.toString();
            Map<String, Object> typeConfig = typeMap.get(validateType);
            String pattern = typeConfig != null ? (String) typeConfig.get("rule") : "";
            int max = typeConfig != null ? Integer.parseInt(typeConfig.get("max").toString()) : 35;
            int min = typeConfig != null ? Integer.parseInt(typeConfig.get("min").toString()) : 1;

            if (StringUtils.hasText(pattern)
                    && (TemplateParamsTypeEnum.CHINESE.getType().equals(validateType)
                    || TemplateParamsTypeEnum.OTHERS.getType().equals(validateType))) {
                value = value.replace(" ", "").replace(".", "");
                value = filterByValidateType(value, validateType);

                int length = value.length();
                if (length > max) {
                    value = value.substring(0, max);
                } else if (length < min) {
                    value = "";
                }
            }

            if (!StringUtils.hasText(value)) {
                log.error("牛云短信参数异常, param={}", param);
                throw new CommonException("短信参数异常，无法发送");
            }

            result.put(param, value);
        }
        return result;
    }

    private String filterByValidateType(String value, String validateType) {
        if (!StringUtils.hasText(value)) {
            return "";
        }

        String invalidPattern;
        if (TemplateParamsTypeEnum.CHINESE.getType().equals(validateType)) {
            invalidPattern = "[^\\p{IsHan}()\\uFF08\\uFF09]";
        } else if (TemplateParamsTypeEnum.OTHERS.getType().equals(validateType)) {
            invalidPattern = "[^\\p{IsHan}\\p{N}\\p{L}\\p{P}\\p{S}\\s]";
        } else {
            return value;
        }
        return Pattern.compile(invalidPattern).matcher(value).replaceAll("");
    }
}
