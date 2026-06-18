package com.niu.core.service.core.pay.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.pay.BasePay;
import com.niu.core.common.loader.pay.PayLoader;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.OnliepayStatusEnum;
import com.niu.core.enums.pay.PayStatusEnum;
import com.niu.core.event.pay.PayCloseEvent;
import com.niu.core.event.pay.PayCreateEventDefiner;
import com.niu.core.event.pay.PaySuccessEvent;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayChannelService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.vo.GetInfoByTradeVo;
import com.niu.core.service.core.pay.vo.PayInfoVo;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.service.core.sys.ICorePrinterService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 支付记录实现
 */
@Service
@Slf4j
public class CorePayServiceImpl implements ICorePayService {

    @Resource
    PayMapper payMapper;

    @Resource
    ICorePayChannelService corePayChannelService;

    @Resource
    ICorePrinterService corePrinterService;

    @Resource
    Cached cached;
    @Resource
    private MemberMapper memberMapper;

    /**
     * 支付
     *
     * @param param
     * @return
     */
    @Override
    @Transactional
    public Object pay(PayParam param) {
        Pay pay = this.checkOrCreate(param.getTradeType(), param.getTradeId());
        if (pay.getBody().length() > 15) {
            pay.setBody(pay.getBody().substring(0, 15) + "...");
        }
        param.setPay(pay);
        param.setNotifyUrl(buildNotifyUrl(param.getChannel(), param.getType(), "pay"));

        BasePay driver = this.driver(param.getChannel(), param.getType());

        Map<String, Object> json = new HashMap<>();
        json.put("openid", param.getOpenid());
        pay.setJson(JSONUtil.toJsonStr(json));
        this.payMapper.updateById(pay);

        return driver.pay(param);
    }

    /**
     * 异步回调
     */
    public Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        return this.driver(param.getChannel(), param.getType()).asyncNotify(param, request, response);
    }

    /**
     * 支付回调处理
     *
     * @param param
     */
    public void payNotify(PayNotifyParam param) {
        Pay pay = this.findPayInfoByOutTradeNo(param.getOutTradeNo());
        if (pay == null) throw new CommonException("未获取到支付单据");
        if (pay.getStatus().equals(PayStatusEnum.STATUS_FINISH.getCode())) throw new CommonException("单据已支付");
        if (pay.getStatus().equals(PayStatusEnum.STATUS_CANCLE.getCode())) throw new CommonException("当前支付已取消");

        // 支付成功
        if (param.getPayStatus().getStatus().equals(OnliepayStatusEnum.SUCCESS.getStatus())) {
            this.paySuccess(pay, param);
        }
        // 支付关闭
        if (param.getPayStatus().getStatus().equals(OnliepayStatusEnum.CLOSED.getStatus())) {
            this.payClose(pay);
        }

        // 未支付
        if (param.getPayStatus().getStatus().equals(OnliepayStatusEnum.NOTPAY.getStatus())) {
            this.check(pay, param);
        }
    }

    public String buildNotifyUrl(String channel, String type, String action) {
        String[] param = {channel, type, action};
        String baseUrl = "";
        if (RequestUtils.handler() != null) {
            baseUrl = RequestUtils.getDomain(true);
            cached.put("payBaseurl", baseUrl);
        } else if (cached.get("payBaseurl") != null) {
            baseUrl = (String) cached.get("payBaseurl");
        } else {
            throw new CommonException("build notify url error");
        }
        return baseUrl + "/api/pay/notify/" + String.join("/", param);
    }

    /**
     * 检测并创建支付单据
     *
     * @param tradeType
     * @param tradeId
     */
    public Pay checkOrCreate(String tradeType, Integer tradeId) {
        Pay pay = this.findPayInfoByTrade(tradeType, tradeId);
        if (pay == null) {
            return this.createByTrade(tradeType, tradeId);
        }
        if (pay.getStatus().equals(PayStatusEnum.STATUS_FINISH.getCode())) throw new CommonException("当前支付已完成");
        if (pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) || pay.getStatus().equals(PayStatusEnum.STATUS_CANCLE.getCode())) {
            if (pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode())) {
                this.close(pay);
            }
            return this.createByTrade(tradeType, tradeId);
        }
        return pay;
    }

    /**
     * 支付驱动
     *
     * @param type
     * @param channel
     * @return
     */
    public BasePay driver(String channel, String type) {
        JSONObject config = ObjectUtil.defaultIfNull(corePayChannelService.getConfigByChannelAndType(channel, type), new JSONObject());
        config.set("channel", channel);
        return PayLoader.getDriver(type).init(config);
    }

    @Override
    public GetInfoByTradeVo getInfoByTrade(String tradeType, Integer tradeId, String channel, String scene) {
        Pay pay = findPayInfoByTrade(tradeType, tradeId);

        if (pay == null) {
            pay = createByTrade(tradeType, tradeId);
        }

        GetInfoByTradeVo vo = new GetInfoByTradeVo();
        BeanUtil.copyProperties(pay, vo);

        List<PayTypeVo> payTypeList = corePayChannelService.getAllowPayTypeByChannel(channel, tradeType);
        vo.setPayTypeList(payTypeList);

        if (ObjectUtil.isNotEmpty(payTypeList) && ObjectUtil.equals(scene, "friendspay")) {
            payTypeList.stream()
                    .filter(pt -> pt.getKey().equals("friendspay"))
                    .findFirst()
                    .ifPresent(payTypeVo -> vo.setConfig(JSONUtil.parseObj(payTypeVo.getConfig())));
            vo.setPayTypeList(payTypeList.stream().filter(pt -> !pt.getKey().equals("friendspay")).toList());
        }

        return vo;
    }

    /**
     * 通过业务类型和id查询有效的支付单据
     *
     * @param tradeType
     * @param tradeId
     */
    public Pay findPayInfoByTrade(String tradeType, Integer tradeId) {
        return payMapper.selectOne(new QueryWrapper<Pay>()
                .eq("trade_type", tradeType)
                .eq("trade_id", tradeId)
                .ne("status", PayStatusEnum.STATUS_CANCLE.getCode())
        );
    }

    /**
     * 通过交易流水号查询支付单据
     *
     * @param outTradeNo
     */
    public Pay findPayInfoByOutTradeNo(String outTradeNo) {
        return payMapper.selectOne(new QueryWrapper<Pay>()
                .eq("out_trade_no", outTradeNo)
        );
    }

    /**
     * 通过业务信息创建支付单据
     *
     * @param tradeType
     * @param tradeId
     * @return
     */
    public Pay createByTrade(String tradeType, Integer tradeId) {
        PayCreateEventDefiner.PayCreateEvent event = new PayCreateEventDefiner.PayCreateEvent();
        event.addAppSign("core");
        event.setName("PayCreateEvent");
        event.setTradeType(tradeType);
        event.setTradeId(tradeId);
        List<PayCreateEventDefiner.PayCreateEventResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);

        if (eventData.size() == 0) throw new CommonException("无效的交易类型");
        PayCreateEventDefiner.PayCreateEventResult data = eventData.get(0);

        Pay model = new Pay();
        BeanUtil.copyProperties(data, model);
        model.setTradeType(tradeType);
        model.setTradeId(tradeId);
        model.setOutTradeNo(createOutTradeNo());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setChannel(RequestUtils.channel());
        payMapper.insert(model);

        return this.findPayInfoByOutTradeNo(model.getOutTradeNo());
    }

    /**
     * 创建交易号
     *
     * @return
     */
    private String createOutTradeNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }

    /**
     * 校验支付订单的状态
     *
     * @return bool
     */
    public void check(Pay pay, PayNotifyParam param) {

        //查询第三方支付单据
        Object obj = this.driver(pay.getChannel(), pay.getType()).getOrder(param.getOutTradeNo());
        String status = "";
        if (ObjectUtil.isNotEmpty(obj) && obj.toString().contains("status")) {
            status = JSONUtil.parseObj(obj).getStr("status");
            if (status.equals(OnliepayStatusEnum.SUCCESS.getStatus())) {
                this.paySuccess(pay, param);
            }
            // 支付关闭
            if (status.equals(OnliepayStatusEnum.CLOSED.getStatus())) {
                this.payClose(pay);
            }

            // 未支付
            if (status.equals(OnliepayStatusEnum.NOTPAY.getStatus())) {
                this.check(pay, param);
            }
        }
    }

    /**
     * 支付成功
     *
     * @param pay
     * @param param
     */
    public void paySuccess(Pay pay, PayNotifyParam param) {

        BeanUtil.copyProperties(param, pay);
        pay.setStatus(PayStatusEnum.STATUS_FINISH.getCode());
        pay.setPayTime(System.currentTimeMillis() / 1000);

        payMapper.updateById(pay);

        ThreadPoolManager.runTask(() -> {
            PaySuccessEvent event = new PaySuccessEvent();
            event.setSiteId(param.getSiteId());
            event.addAppSign("core");
            event.setName("PaySuccessEvent");
            event.setTradeType(pay.getTradeType());
            event.setTradeId(pay.getTradeId());
            event.setPay(pay);
            EventAndSubscribeOfPublisher.publishAll(event);
        });
    }

    /**
     * 关闭支付
     *
     * @param pay
     */
    public void payClose(Pay pay) {
        PayCloseEvent event = new PayCloseEvent();
        event.addAppSign("core");
        event.setName("PayCloseEvent");
        event.setTradeType(pay.getTradeType());
        event.setTradeId(pay.getTradeId());
        EventAndSubscribeOfPublisher.publishAll(event);

        pay.setStatus(PayStatusEnum.STATUS_CANCLE.getCode());
        pay.setCancelTime(System.currentTimeMillis() / 1000);
        payMapper.updateById(pay);
    }

    /**
     * 关闭支付
     *
     * @param outTradeNo
     */
    public void close(String outTradeNo) {
        Pay pay = this.findPayInfoByOutTradeNo(outTradeNo);
        if (pay == null) throw new CommonException("无效的支付交易号");
        if (pay.getStatus().equals(PayStatusEnum.STATUS_CANCLE.getCode())) return;
        if (!pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) && !pay.getStatus().equals(PayStatusEnum.STATUS_WAIT.getCode())) {
            throw new CommonException("只有待支付时可以关闭");
        }

        if (pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) && !pay.getType().isEmpty()) {
            this.driver(pay.getChannel(), pay.getType()).close(pay);
        }

        this.payClose(pay);
    }

    /**
     * 关闭支付
     *
     * @param pay
     */
    public void close(Pay pay) {
        if (pay == null) throw new CommonException("无效的支付交易号");
        if (pay.getStatus().equals(PayStatusEnum.STATUS_CANCLE.getCode())) return;
        if (!pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) && !pay.getStatus().equals(PayStatusEnum.STATUS_WAIT.getCode())) {
            throw new CommonException("只有待支付时可以关闭");
        }

        if (pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) && !pay.getType().isEmpty()) {
            Boolean result = this.driver(pay.getChannel(), pay.getType()).close(pay);
            if (!result) {
                Object obj = this.driver(pay.getChannel(), pay.getType()).getOrder(pay.getOutTradeNo());
                String status = "";
                if (ObjectUtil.isNotEmpty(obj) && obj.toString().contains("status")) {
                    status = JSONUtil.parseObj(obj).getStr("status");
                    if (status.equals(OnliepayStatusEnum.SUCCESS.getStatus())) {
                        PayNotifyParam param = new PayNotifyParam();
                        BeanUtils.copyProperties(pay, param);
                        param.setPayStatus(OnliepayStatusEnum.SUCCESS);
                        this.paySuccess(pay, param);
                    }
                }
            }
        }

        this.payClose(pay);
    }

    /**
     * 关闭支付
     *
     * @param tradeType
     * @param tradeId
     */
    public void closeByTrade(String tradeType, Integer tradeId) {
        Pay pay = findPayInfoByTrade(tradeType, tradeId);
        if (pay == null) return;

        if (pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) || pay.getStatus().equals(PayStatusEnum.STATUS_WAIT.getCode())) {
            this.close(pay);
        }
    }

    @Override
    public List<PayTypeVo> getPayTypeByTrade(String tradeType, ChannelEnum channel) {
        return corePayChannelService.getAllowPayTypeByChannel(channel.getCode(), tradeType);
    }

    /**
     * 修改支付扩展信息
     *
     * @param json
     * @param id
     */
    @Override
    public void payEdit(String json, Integer id) {
        payMapper.update(new Pay(), new UpdateWrapper<Pay>().eq("id", id).set("json", json));
    }

    @Override
    public void payEditHuifu(BigDecimal feeAmt, BigDecimal confirmedAmt, BigDecimal unconfirmAmt, Integer id) {
        payMapper.update(new Pay(), new UpdateWrapper<Pay>().eq("id", id)
                .set("fee_amt", feeAmt)
                .set("confirmed_amt", ObjectUtil.isNotEmpty(confirmedAmt) ? confirmedAmt : new BigDecimal("0.00"))
                .set("unconfirm_amt", ObjectUtil.isNotEmpty(unconfirmAmt) ? unconfirmAmt : new BigDecimal("0.00")));
    }

    @Override
    public JSONObject getPayCashierUrl(String payType, String channel, Integer tradeId) {
        JSONObject json = new JSONObject();
        if (StringUtils.isNotEmpty(payType)) {
            json.set("payType", payType);
        }
        if (ObjectUtil.isNotEmpty(tradeId)) {
            json.set("trade_id", tradeId);
        }
        JSONObject result = new JSONObject();
        result.set("url", "/app/pages/pay/cashier");
        result.set("query", json);
        result.set("channel", channel);
        result.set("to_type", "cashier");
        return result;
    }

    @Override
    public String create(Integer siteId, String mainType, Integer mainId, BigDecimal money, String tradeType, Integer tradeId, String body) {
        String outTradeNo = createOutTradeNo();
        Pay pay = new Pay();
        pay.setSiteId(siteId);
        pay.setMoney(money);
        pay.setTradeType(tradeType);
        pay.setTradeId(tradeId);
        pay.setBody(body);
        pay.setOutTradeNo(outTradeNo);
        pay.setMainId(mainId);
        pay.setMainType(mainType);
        pay.setFromMainId(mainId);
        payMapper.insert(pay);
        return outTradeNo;
    }

    @Override
    public PayInfoVo getPayInfoByOutTradeNo(String outTradeNo) {
        // 1. 查询支付主表（指定字段）
        LambdaQueryWrapper<Pay> payWrapper = new LambdaQueryWrapper<Pay>()
                .eq(Pay::getOutTradeNo, outTradeNo)
                .select(Pay::getSiteId, Pay::getMainId, Pay::getOutTradeNo, Pay::getTradeType,
                        Pay::getTradeNo, Pay::getBody, Pay::getMoney, Pay::getVoucher,
                        Pay::getStatus, Pay::getJson, Pay::getCreateTime, Pay::getPayTime,
                        Pay::getType, Pay::getChannel);
        Pay pay = payMapper.selectOne(payWrapper);
        if (pay == null) {
            throw new CommonException("TRADE_INFO_NOT_EXIST");
        }

        // 2. 查询关联的会员信息（仅需要的字段）
        Member member = null;
        if (pay.getMainId() != null) {
            LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<Member>()
                    .eq(Member::getMemberId, pay.getMainId())
                    .select(Member::getMemberId, Member::getNickname, Member::getHeadimg);
            member = memberMapper.selectOne(memberWrapper);
        }

        // 3. 组装 VO
        PayInfoVo vo = new PayInfoVo();
        BeanUtils.copyProperties(pay, vo);

        if (member != null) {
            PayInfoVo.MemberInfo memberInfo = new PayInfoVo.MemberInfo();
            BeanUtils.copyProperties(member, memberInfo);
            vo.setMember(memberInfo);
        }

        return vo;
    }

}
