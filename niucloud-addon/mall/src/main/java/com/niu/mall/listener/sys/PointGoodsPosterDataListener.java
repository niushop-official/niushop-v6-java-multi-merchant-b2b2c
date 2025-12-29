package com.niu.mall.listener.sys;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.event.sys.GetPosterDataEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.mall.entity.exchange.MallPointExchange;
import com.niu.mall.mapper.exchange.MallPointExchangeMapper;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@EventCallback("mall")
@Component
public class PointGoodsPosterDataListener extends GetPosterDataEventDefiner {

    @Resource
    private ICoreSysConfigService coreSysConfigService;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private MallPointExchangeMapper pointExchangeMapper;

    public GetPosterDataResult handleCallback(GetPosterDataEvent event) {
        String type = event.getType();
        // 只处理mall_point_goods类型，points_mall_goods由points_mall模块的PointsGoodsPosterDataListener处理
        if (type.equals("mall_point_goods")) {
            String mode = (String) event.getParam().getOrDefault("mode", "");
            
            Object skuIdObj = event.getParam().getOrDefault("sku_id", event.getParam().getOrDefault("id", "0"));
            Integer skuId = skuIdObj instanceof Integer ? (Integer) skuIdObj : Integer.valueOf(skuIdObj.toString());
            
            Object memberIdObj = event.getParam().getOrDefault("member_id", "0");
            Integer memberId = memberIdObj instanceof Integer ? (Integer) memberIdObj : Integer.valueOf(memberIdObj.toString());

            Map<String, Object> data = new HashMap<>();

            Map<String, Object> urlData = new HashMap<>();
            urlData.put("sku_id", skuId);

            if (mode.equals("preview")) {
                data.put("nickname", "会员昵称");
                data.put("headimg", "static/resource/images/default_headimg.jpg");
                data.put("goods_name", "商品名称");
                data.put("goods_price", "1000积分+￥369.00");
                data.put("goods_market_price", "￥3690.00");
                data.put("goods_img", "addon/points_mall/goods_template.png");
                
                Map<String, Object> url = new HashMap<>();
                url.put("url", coreSysConfigService.getSceneDomain().getWapUrl());
                url.put("page", "addon/points_mall/pages/goods/detail");
                Map<String, Object> urlParam = new HashMap<>();
                urlParam.put("sku_id", skuId);
                url.put("data", urlParam);
                data.put("url", url);
                
                GetPosterDataResult result = new GetPosterDataResult();
                result.setData(data);
                return result;
            } else {
                MallPointExchange exchange = pointExchangeMapper.selectOne(new QueryWrapper<MallPointExchange>().eq("site_id", event.getSiteId()).eq("id", skuId));
                if (exchange == null) return null;

                String[] goodsImg = exchange.getImage().split(",");

                data.put("goods_name", exchange.getNames());
                data.put("goods_img", ObjectUtil.defaultIfEmpty(goodsImg[0], "addon/shop/goods_template.png"));
                data.put("goods_price", "积分" + exchange.getPoint() + (exchange.getPrice().doubleValue() > 0 ? "+￥" + exchange.getPrice().toString() : ""));

                if (memberId > 0) {
                    urlData.put("mid", memberId);
                    Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("nickname,headimg").eq("member_id", memberId));
                    if (member != null) {
                        String nickname = member.getNickname();
                        if (nickname != null && nickname.length() > 10) {
                            data.put("nickname", nickname.substring(0, 7) + "...");
                        } else {
                            data.put("nickname", nickname);
                        }
                        data.put("headimg", ObjectUtil.defaultIfEmpty(member.getHeadimg(), "static/resource/images/default_headimg.jpg"));
                    }
                }
            }

            Map<String, Object> url = new HashMap<>();
            url.put("url", coreSysConfigService.getSceneDomain().getWapUrl());
            url.put("page", "addon/points_mall/pages/goods/detail");
            url.put("data", urlData);
            data.put("url", url);

            GetPosterDataResult result = new GetPosterDataResult();
            result.setData(data);
            return result;
        }
        return null;
    }
}
