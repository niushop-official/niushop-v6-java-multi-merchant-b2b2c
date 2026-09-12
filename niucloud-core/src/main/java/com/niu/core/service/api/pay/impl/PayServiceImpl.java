package com.niu.core.service.api.pay.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.sys.SysPoster;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.event.pay.PayTradeInfoDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.SysPosterMapper;
import com.niu.core.service.api.pay.IPayService;
import com.niu.core.service.api.pay.vo.FriendspayInfoVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayChannelService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.vo.GetInfoByTradeVo;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PayServiceImpl implements IPayService {

    @Resource
    ICorePayService corePayService;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICorePayChannelService corePayChannelService;

    @Resource
    SysPosterMapper sysPosterMapper;

    /**
     * 支付
     *
     * @param param
     * @return
     */
    public Object pay(PayParam param) {
        param.setChannel(RequestUtils.channel());
        param.setBuyerId(RequestUtils.memberId());

        if (RequestUtils.channel().equals(ChannelEnum.WECHAT.getCode()) && param.getOpenid().isEmpty()) {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("wx_openid").eq("member_id", RequestUtils.memberId()));
            param.setOpenid(ObjectUtil.defaultIfNull(member.getWxOpenid(), ""));
        }
        if (RequestUtils.channel().equals(ChannelEnum.WEAPP.getCode()) && param.getOpenid().isEmpty()) {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("weapp_openid").eq("member_id", RequestUtils.memberId()));
            param.setOpenid(ObjectUtil.defaultIfNull(member.getWeappOpenid(), ""));
        }

        return corePayService.pay(param);
    }

    /**
     * 支付异步回调
     *
     * @param param
     * @param request
     * @param response
     * @return
     */
    public Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        return corePayService.asyncNotify(param, request, response);
    }

    public GetInfoByTradeVo getInfoByTrade(String tradeType, Integer tradeId, String scene) {
        return corePayService.getInfoByTrade(tradeType, tradeId, RequestUtils.channel(), scene);
    }

    @Override
    public FriendspayInfoVo friendspayInfo(String tradeType, Integer tradeId) {
        // 对齐 PHP getFriendspayInfoByTrade：不按 siteId 强校验（平台 H5 site_id=0，支付单为店铺 site_id）
        GetInfoByTradeVo payInfo = getInfoByTrade(tradeType, tradeId, "friendspay");
        if (ObjectUtil.isEmpty(payInfo)) {
            return new FriendspayInfoVo();
        }

        FriendspayInfoVo vo = new FriendspayInfoVo();
        BeanUtil.copyProperties(payInfo, vo);
        vo.setConfig(payInfo.getConfig());
        vo.setPayTypeList(payInfo.getPayTypeList());
        vo.setCreateTime(DateUtils.timestampToString(payInfo.getCreateTime()));
        if (payInfo.getPayTime() != 0) {
            vo.setPayTime(DateUtils.timestampToString(payInfo.getPayTime()));
        }

        if (payInfo.getCancelTime() != 0) {
            vo.setCancelTime(DateUtils.timestampToString(payInfo.getCancelTime()));
        }

        // 对齐 PHP array_filter：多插件回调时过滤空结果，取首个有效 trade_info
        PayTradeInfoDefiner.PayTradeInfoEvent event = new PayTradeInfoDefiner.PayTradeInfoEvent(tradeType, tradeId);
        List<PayTradeInfoDefiner.PayTradeInfoEventResult> trades = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if (ObjectUtil.isNotEmpty(trades)) {
            trades.stream()
                    .filter(t -> t != null && ObjectUtil.isNotEmpty(t.getTradeInfo())
                            && ObjectUtil.isNotEmpty(t.getTradeInfo().getItemList()))
                    .findFirst()
                    .ifPresent(t -> vo.setTradeInfo(t.getTradeInfo()));
        }

        // 用 from_main_id 判断是否本人发起
        Integer fromMainId = Objects.isNull(payInfo.getFromMainId()) || payInfo.getFromMainId() == 0 ? payInfo.getMainId() : payInfo.getFromMainId();
        vo.setSelf(fromMainId != null && fromMainId.equals(RequestUtils.memberId()));

        QueryWrapper<SysPoster> posterQueryWrapper = new QueryWrapper<>();
        posterQueryWrapper.eq("type", "friendspay")
                .eq("status", 1)
                .eq("is_default", 1);
        SysPoster sysPoster = sysPosterMapper.selectOne(posterQueryWrapper);
        if (ObjectUtil.isNotEmpty(sysPoster)) {
            vo.setPosterId(sysPoster.getId());
        }

        Member member = memberMapper.selectById(fromMainId);
        vo.setMember(member);
        return vo;
    }

    /**
     * 获取某笔交易当前渠道下可用的支付方式
     *
     * @param tradeType 业务类型
     * @return 可用支付方式列表
     */
    @Override
    public List<PayTypeVo> getPayTypeByTrade(String tradeType) {
        ChannelEnum channel = Arrays.stream(ChannelEnum.values())
                .filter(item -> item.getCode().equals(RequestUtils.channel()))
                .findFirst()
                .orElse(ChannelEnum.H5);
        return corePayService.getPayTypeByTrade(tradeType, channel);
    }

    /**
     * 关闭支付单（仅允许订单所属会员操作，且仅待支付状态可关闭）
     *
     * @param outTradeNo 支付交易号
     */
    @Override
    public void close(String outTradeNo) {
        Pay pay = corePayService.findPayInfoByOutTradeNo(outTradeNo);
        if (pay == null) {
            throw new CommonException("无效的支付交易号");
        }
        // 归属校验：只允许订单所属会员关闭
        if (pay.getMainId() == null || !pay.getMainId().equals(RequestUtils.memberId())) {
            throw new CommonException("无权操作该支付单");
        }
        corePayService.close(outTradeNo);
    }

}
