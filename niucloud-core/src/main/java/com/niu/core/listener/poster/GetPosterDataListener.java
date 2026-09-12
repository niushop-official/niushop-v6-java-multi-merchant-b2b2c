package com.niu.core.listener.poster;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.pay.PayStatusEnum;
import com.niu.core.event.sys.GetPosterDataEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.pay.ICorePayChannelService;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventCallback("core")
@Component
public class GetPosterDataListener extends GetPosterDataEventDefiner {
    private PayMapper payMapper;
    private MemberMapper memberMapper;
    private ICoreSysConfigService coreSysConfigService;
    private SiteMapper siteMapper;
    @Resource
    private ICorePayChannelService corePayChannelService;

    public GetPosterDataEventDefiner.GetPosterDataResult handleCallback(GetPosterDataEventDefiner.GetPosterDataEvent event) {
        if (event.getType().equals("friendspay")) {
            final Map<String, Object> param = event.getParam();
            GetPosterDataEventDefiner.GetPosterDataResult result = new GetPosterDataEventDefiner.GetPosterDataResult();
            Object tradeId = param.get("id");
            Object tradeType = param.get("type");
            String wapUrl = coreSysConfigService.getSceneDomain().getWapUrl();
            if (ObjectUtil.equal(param.get("mode"), "preview")) {
                Map<String, Object> data = new HashMap<>(8, 1);
                data.put("nickname", "会员昵称的帮付");
                data.put("headimg", "static/resource/images/default_headimg.png");
                data.put("friendspay_message", "帮我付一下这笔订单吧，谢谢啦~");
                data.put("friendspay_money", "￥369.00");


                String url = QrcodeUtils.qrcodeToFile(
                        event.getSiteId(),
                        event.getChannel(),
                        wapUrl,
                        "app/pages/friendspay/money",
                        Map.of("id", tradeId, "type", param.get("type")),
                        ""
                );
                data.put("url", url);

                result.setData(data);
                return result;
            }

            QueryWrapper<Pay> wrapper = new QueryWrapper<>();
            wrapper.eq("trade_id", tradeId)
                    .eq("trade_type", tradeType)
                    .ne("status", PayStatusEnum.STATUS_CANCLE.getCode());
            List<Pay> pays = payMapper.selectList(wrapper);
            if (ObjectUtil.isEmpty(pays)) {
                return result;
            }
            JSONObject payConfig = new JSONObject();
            List<PayTypeVo> payTypeVos = corePayChannelService.getAllowPayTypeByChannel(event.getChannel(), tradeType.toString());
            if(!payTypeVos.isEmpty()){
                List<PayTypeVo> payTypeVosThis = payTypeVos.stream().filter(payTypeVo -> payTypeVo.getKey().equals(tradeType.toString())).toList();
                if(!payTypeVosThis.isEmpty()){
                    payConfig = JSONUtil.parseObj(payTypeVosThis.get(0).getConfig());
                }
            }

            Map<String, Object> data = new HashMap<>(8, 1);
            data.put("friendspay_message", StringUtils.isNotEmpty(payConfig.getStr("pay_leave_message")) ? payConfig.getStr("pay_leave_message") :  "帮我付一下这笔订单吧，谢谢啦~");
            data.put("friendspay_money", !pays.isEmpty() && ObjectUtil.isNotEmpty(pays.get(0).getMoney())  ? "￥" + pays.get(0).getMoney() :  "￥369.00");

            // 生成二维码
            String url = QrcodeUtils.qrcodeToFile(
                    event.getSiteId(),
                    event.getChannel(),
                    coreSysConfigService.getSceneDomain().getWapUrl(),
                    "app/pages/friendspay/money",
                    Map.of("id", tradeId, "type", param.get("type")),
                    ""
            );
            data.put("url", url);

            String memberId = event.getParam().getOrDefault("member_id", 0).toString();
            if (ObjectUtil.isNotEmpty(memberId)) {
                Member member = memberMapper.selectById(memberId);
                if (ObjectUtil.isNotEmpty(member)) {
                    if (ObjectUtil.isEmpty(member.getHeadimg())) {
                        member.setHeadimg("static/resource/images/default_headimg.png");
                    }

                    data.put("nickname", member.getNickname().length() > 12 ? member.getNickname().substring(12) + "...的帮付" : member.getNickname() + "的帮付");
                    data.put("headimg", member.getHeadimg());
                }
            }

            result.setData(data);
            return result;
        }

        // 买单海报
        if (event.getType().equals("cash")) {
            GetPosterDataEventDefiner.GetPosterDataResult result = new GetPosterDataEventDefiner.GetPosterDataResult();
            String wapUrl = coreSysConfigService.getSceneDomain().getWapUrl();
            Map<String, Object> param = event.getParam() != null ? event.getParam() : Map.of();
            String mode = String.valueOf(param.getOrDefault("mode", ""));
            Integer siteId = event.getSiteId();
            Object siteIdParam = param.getOrDefault("site_id", null);
            if (siteIdParam != null) {
                if (siteIdParam instanceof Integer) {
                    siteId = (Integer) siteIdParam;
                } else {
                    try {
                        siteId = Integer.valueOf(siteIdParam.toString());
                    } catch (Exception e) {
                    }
                }
            }

            Map<String, Object> data = new HashMap<>(8, 1);
            if ("preview".equals(mode)) {
                data.put("site_name", "店铺名称");
                data.put("site_logo", "static/resource/images/default_headimg.jpg");
            } else {
                Site site = siteMapper.selectOne(new LambdaQueryWrapper<Site>().eq(Site::getSiteId, siteId));
                if (site != null) {
                    String siteLogo = ObjectUtil.defaultIfBlank(site.getFrontEndLogo(), "static/resource/images/default_headimg.jpg");
                    data.put("site_name", site.getSiteName());
                    data.put("site_logo", siteLogo);
                } else {
                    data.put("site_name", "店铺名称");
                    data.put("site_logo", "static/resource/images/default_headimg.jpg");
                }
            }

            Map<String, Object> urlInfo = new HashMap<>(4, 1);
            urlInfo.put("url", wapUrl);
            urlInfo.put("page", "app/pages/dopay/dopay");
            urlInfo.put("data", List.of(Map.of("key", "site_id", "value", siteId)));
            data.put("url", urlInfo);

            result.setData(data);
            return result;
        }

        return null;
    }

    @Autowired
    public void setPayMapper(PayMapper payMapper) {
        this.payMapper = payMapper;
    }

    @Autowired
    public void setMemberMapper(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Autowired
    public void setCoreSysConfigService(ICoreSysConfigService coreSysConfigService) {
        this.coreSysConfigService = coreSysConfigService;
    }

    @Autowired
    public void setSiteMapper(SiteMapper siteMapper) {
        this.siteMapper = siteMapper;
    }

}
