package com.niu.mall.listener.sys;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.event.sys.GetPosterDataEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@EventCallback("mall")
@Component
public class GoodsPosterDataListener extends GetPosterDataEventDefiner {

    @Resource
    private ICoreSysConfigService coreSysConfigService;

    @Resource
    private MallGoodsSkuMapper skuMapper;

    @Resource
    private MallGoodsMapper goodsMapper;

    @Resource
    private MemberMapper memberMapper;


    public GetPosterDataResult handleCallback(GetPosterDataEvent event) {
        if (event.getType().equals("shop_goods")) {
            String mode = (String) event.getParam().getOrDefault("mode", "");
            Integer skuId = Integer.valueOf((String) event.getParam().getOrDefault("sku_id", "0"));
            Integer memberId = Integer.valueOf((String) event.getParam().getOrDefault("member_id", "0"));

            Map<String, Object> data = new HashMap<>();

            Map<String, Object> urlData = new HashMap<>();
            urlData.put("sku_id", skuId);

            if (mode.equals("preview")) {
                data.put("nickname", "会员昵称");
                data.put("headimg", "static/resource/images/default_headimg.png");
                data.put("goods_name", "商品名称");
                data.put("goods_price", "￥369.00");
                data.put("goods_market_price", "￥369.00");
                data.put("goods_img", "addon/mall/goods_template.png");
            } else {
                MallGoodsSku sku = skuMapper.selectOne(new QueryWrapper<MallGoodsSku>().select("goods_id,sku_name,sku_image,sale_price,market_price").eq("sku_id", skuId));
                if (sku == null) return null;

                MallGoods goods = goodsMapper.selectOne(new QueryWrapper<MallGoods>().select("goods_name,goods_cover").eq("goods_id", sku.getGoodsId()));
                if (goods == null) return null;

                String goodsImg = ObjectUtil.defaultIfEmpty(sku.getSkuImage(), goods.getGoodsCover());

                data.put("goods_name", goods.getGoodsName() + sku.getSkuName());
                data.put("goods_img", ObjectUtil.defaultIfEmpty(goodsImg, "addon/mall/goods_template.png"));
                data.put("goods_price", "￥" + sku.getSalePrice());
                data.put("goods_market_price", sku.getMarketPrice().doubleValue() > 0 ? "￥" + sku.getMarketPrice().toString() : "");

                if (memberId > 0) {
                    urlData.put("mid", memberId);
                    Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("nickname,headimg").eq("member_id", memberId));
                    data.put("nickname", member.getNickname().length() > 10 ? member.getNickname().substring(0, 7) + "..." : member.getNickname());
                    data.put("headimg", ObjectUtil.defaultIfEmpty(member.getHeadimg(), "static/resource/images/default_headimg.png"));
                }
            }

            // 生成二维码
            String url = QrcodeUtils.qrcodeToFile(
                    event.getSiteId(),
                    event.getChannel(),
                    coreSysConfigService.getSceneDomain().getWapUrl(),
                    "addon/mall/pages/goods/detail",
                    urlData,
                    ""
            );
            data.put("url", url);

            GetPosterDataResult result = new GetPosterDataResult();
            result.setData(data);
            return result;
        }
        return null;
    }
}
