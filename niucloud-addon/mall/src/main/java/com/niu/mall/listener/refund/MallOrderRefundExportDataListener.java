package com.niu.mall.listener.refund;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.service.admin.delivery.IDeliveryCompanyService;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.vo.MemberInfoVo;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.enums.order.OrderRefundStatusEnum;
import com.niu.mall.enums.order.OrderRefundTypeEnum;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.admin.order.IMallOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@EventCallback("mall")
public class MallOrderRefundExportDataListener extends ExportDataEventDefiner {

    @Resource
    MallOrderRefundMapper mallOrderRefundMapper;

    @Resource
    IMemberService memberService;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    IMallOrderService mallOrderService;

    @Resource
    IDeliveryCompanyService deliveryCompanyService;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if (!"mall_order_refund".equals(event.getType())) {
            return new ExportDataEventResult();
        }
        QueryWrapper<MallOrderRefund> queryWrapper = new QueryWrapper<>();
        if (RequestUtils.siteId() != 0) {
            queryWrapper.eq("site_id", RequestUtils.siteId());
        }
        JSONObject where = event.getWhere();
        if (where != null) {
            Object orderRefundNo = where.get("order_refund_no");
            if (ObjectUtil.isNotEmpty(orderRefundNo)) {
                queryWrapper.like("order_refund_no", orderRefundNo);
            }
            if ((where.get("create_time") instanceof JSONArray createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
                QueryMapperUtils.buildByTime(queryWrapper, "create_time", StringUtils.jsonArrayToStringArray(createTimes));
            }
        }
        PageParam pageParam = event.getPageParam();
        List<MallOrderRefund> list = new ArrayList<>();
        if (pageParam.getPage() > 0 && pageParam.getLimit() > 0) {
            list = mallOrderRefundMapper.selectList(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper);
        } else {
            list = mallOrderRefundMapper.selectList(queryWrapper);
        }
        ExportDataEventResult result = new ExportDataEventResult();
        JSONArray jsonArray = new JSONArray();
        for (MallOrderRefund vo : list) {
            JSONObject jsonObject = new JSONObject();

            MemberInfoVo memberInfoVo = new MemberInfoVo();
            if (vo.getMemberId() != null) {
                memberInfoVo = memberService.info(vo.getMemberId());
            }
            MallOrderGoods orderGoods = mallOrderGoodsMapper.selectById(vo.getOrderGoodsId());

            Map<String, Object> refundAddress = JSONUtil.parseObj(vo.getRefundAddress() == null || vo.getRefundAddress().isEmpty() ?
                    "{}" : vo.getRefundAddress());
            Map<String, Object> delivery = JSONUtil.parseObj(vo.getDelivery() == null || vo.getDelivery().isEmpty() ?
                    "{}" : vo.getDelivery());
            jsonObject.set("order_refund_no", vo.getOrderRefundNo())
                    .set("nickname", memberInfoVo.getNickname())
                    .set("refund_type_name", OrderRefundTypeEnum.getNameByType(Integer.valueOf(vo.getRefundType())))
                    .set("apply_money", vo.getApplyMoney())
                    .set("goods_info", orderGoods.getGoodsName())
                    .set("refund_num", orderGoods.getNum())
                    .set("money", vo.getMoney())
                    .set("status_name", OrderRefundStatusEnum.getNameByStatus(Integer.valueOf(vo.getStatus())))
                    .set("create_time", DateUtils.timestampToString(vo.getCreateTime()))
                    .set("transfer_time", DateUtils.timestampToString(vo.getTransferTime()))
                    .set("refund_no", vo.getRefundNo())
                    .set("reason", vo.getReason())
                    .set("shop_reason", vo.getShopReason())
                    .set("contact_name", refundAddress.getOrDefault("contactName", ""))
                    .set("refund_address", refundAddress.getOrDefault("fullAddress", ""))
                    .set("express_number", delivery.getOrDefault("expressNumber", ""))
                    .set("express_company", delivery.getOrDefault("expressCompany", ""));

            jsonArray.add(jsonObject);
        }
        result.setResultData(jsonArray);
        return result;
    }
}
