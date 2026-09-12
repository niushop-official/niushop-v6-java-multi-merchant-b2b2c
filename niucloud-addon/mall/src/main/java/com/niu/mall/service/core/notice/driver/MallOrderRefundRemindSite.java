package com.niu.mall.service.core.notice.driver;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.entity.sys.NiuSmsTemplate;
import com.niu.core.mapper.sys.NiuSmsTemplateMapper;
import com.niu.core.enums.notice.TemplateParamsTypeEnum;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.enums.order.OrderRefundTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家端退款申请通知
 */
public class MallOrderRefundRemindSite extends BaseNotice {

    @Override
    public NoticeDataVo noticeData(Map<String, Object> vars) {
        MallNoticeSupport support = SpringContext.bean(MallNoticeSupport.class);
        MallOrderRefund refund = support.resolveRefund(vars.get("refund"), vars.get("mallRefundId"), vars.get("order_refund_no"));
        if (ObjectUtil.isEmpty(refund)) {
            return new NoticeDataVo();
        }

        Map<String, Object> param = new HashMap<>();
        param.put("order_refund_no", StrUtil.subSufByLength(refund.getOrderRefundNo(), 4));
        Integer refundType = null;
        try {
            refundType = Integer.parseInt(refund.getRefundType());
        } catch (NumberFormatException ignored) {
            // ignore
        }
        param.put("refund_type", resolveRefundTypeValue(refund, refundType));

        NoticeDataVo vo = new NoticeDataVo();
        vo.setUid(support.getNoticeUid(refund.getSiteId()));
        vo.setVars(param);
        return vo;
    }

    private String resolveRefundTypeValue(MallOrderRefund refund, Integer refundType) {
        String refundTypeName = OrderRefundTypeEnum.getNameByType(refundType);
        NiuSmsTemplate template = SpringContext.bean(NiuSmsTemplateMapper.class).selectOne(
                new QueryWrapper<NiuSmsTemplate>()
                        .eq("site_id", refund.getSiteId())
                        .eq("template_key", "mall_order_refund_remind_site")
                        .last("limit 1")
        );
        if (template == null || StrUtil.isBlank(template.getParamJson())) {
            return refundTypeName;
        }
        try {
            JSONObject paramJson = JSONUtil.parseObj(template.getParamJson());
            String refundTypeParam = paramJson.getStr("refund_type", "");
            if (TemplateParamsTypeEnum.CHINESE.getType().equals(refundTypeParam)
                    || TemplateParamsTypeEnum.OTHERS.getType().equals(refundTypeParam)
                    || TemplateParamsTypeEnum.DATE.getType().equals(refundTypeParam)
                    || TemplateParamsTypeEnum.AMOUNT.getType().equals(refundTypeParam)) {
                return refundTypeName;
            }
        } catch (Exception ignored) {
            return refundTypeName;
        }
        return refund.getRefundType();
    }
}
