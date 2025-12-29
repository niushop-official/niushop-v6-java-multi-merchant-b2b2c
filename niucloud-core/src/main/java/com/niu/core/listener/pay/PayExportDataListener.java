package com.niu.core.listener.pay;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.pay.PayChannelEnum;
import com.niu.core.enums.pay.PayStatusEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.vo.MemberInfoVo;
import com.niu.core.service.admin.sys.ISysUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EventCallback("mall")
public class PayExportDataListener extends ExportDataEventDefiner {

    @Resource
    PayMapper payMapper;

    @Resource
    IMemberService memberService;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if(!"pay".equals(event.getType())){
            return new ExportDataEventResult();
        }
        QueryWrapper<Pay> queryWrapper = new QueryWrapper<>();
        JSONObject where = event.getWhere();
        if(where != null){
            Object outTradeNo = where.get("out_trade_no");
            if (ObjectUtil.isNotEmpty(outTradeNo)) {
                queryWrapper.eq("out_trade_no", outTradeNo);
            }
            Object tradeType = where.get("trade_type");
            if (ObjectUtil.isNotEmpty(tradeType)) {
                queryWrapper.eq("trade_type", tradeType);
            }
            Object status = where.get("status");
            if (ObjectUtil.isNotEmpty(status)) {
                queryWrapper.eq("status", status);
            }
            if ((where.get("create_time") instanceof JSONArray createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
                QueryMapperUtils.buildByTime(queryWrapper, "create_time", StringUtils.jsonArrayToStringArray(createTimes));
            }
            Object type = where.get("type");
            if (ObjectUtil.isNotEmpty(type)) {
                queryWrapper.eq("type", type);
            }
            Object channel = where.get("channel");
            if (ObjectUtil.isNotEmpty(channel)) {
                queryWrapper.eq("channel", channel);
            }
        }

        List<Pay> list = new ArrayList<>();
        PageParam pageParam = event.getPageParam();
        if(pageParam.getPage() > 0 && pageParam.getLimit() > 0){
            list = payMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper).getRecords();
        }else{
            list = payMapper.selectList(queryWrapper);
        }
        return exportData(list);
    }

    private ExportDataEventResult exportData(List<Pay> list){
        ExportDataEventResult result = new ExportDataEventResult();
        if (ObjectUtil.isEmpty(result)) {
            return result;
        }
        JSONArray jsonArray = new JSONArray();
        for(Pay pay : list){
            JSONObject jsonObject = new JSONObject();

            MemberInfoVo memberInfoVo = memberService.info(pay.getMainId());
            JSONObject payType = PayTypeEnum.getType();
            jsonObject.set("out_trade_no",pay.getOutTradeNo())
                      .set("trade_type",pay.getTradeType())
                      .set("real_name",memberInfoVo.getNickname())
                      .set("mobile",memberInfoVo.getMobile())
                      .set("body",pay.getBody())
                      .set("money",pay.getMoney())
                      .set("status", PayStatusEnum.getNameByStatus(pay.getStatus()))
                      .set("type",payType.getByPath(pay.getType() + ".name", String.class))
                      .set("channel", PayChannelEnum.getNameByStatus(pay.getChannel()))
                      .set("create_time", DateUtils.timestampToString(pay.getCreateTime()))
                      .set("pay_time",DateUtils.timestampToString(pay.getPayTime()))
                      .set("cancel_time",DateUtils.timestampToString(pay.getCancelTime()))
                      .set("fail_reason",pay.getFailReason());
            jsonArray.add(jsonObject);
        }
        result.setResultData(jsonArray);
        return result;
    }
}
