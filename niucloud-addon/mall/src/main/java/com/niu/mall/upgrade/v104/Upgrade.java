package com.niu.mall.upgrade.v104;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.mapper.diy.DiyPageMapper;
import com.niu.core.mapper.sys.SysConfigMapper;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.goods.GoodsAuditStatusEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderEventService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UpgradeProvider(addon = "mall", version = "1.0.4")
public class Upgrade extends IUpgradeProvider {

    private static final String DIY_BOTTOM_MALL_KEY = ConfigKeyEnum.DIY_BOTTOM.getName() + "_mall";

    @Override
    public void handle() {
        handleGoodsAuditData();
        handleDiyBottomData();
        handleDiyPageData();
        handleVirtualGoodsData();
        handleOrderGoodsData();
        handleVirtualVerifyOrderData();
    }

    // 处理商品审核状态迁移
    public void handleGoodsAuditData() {
        MallGoodsMapper mallGoodsMapper = SpringContext.bean(MallGoodsMapper.class);
        mallGoodsMapper.update(new MallGoods(), new LambdaUpdateWrapper<MallGoods>()
                .in(MallGoods::getStatus, GoodsStatusEnum.ONLINE.getStatus(), GoodsStatusEnum.OFFLINE.getStatus())
                .set(MallGoods::getAuditStatus, GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus())
        );
        mallGoodsMapper.update(new MallGoods(), new LambdaUpdateWrapper<MallGoods>()
                .eq(MallGoods::getStatus, GoodsStatusEnum.AUDI_TO_FINE.getStatus())
                .set(MallGoods::getAuditStatus, GoodsAuditStatusEnum.AUDIT_REFUSE.getStatus())
        );
        mallGoodsMapper.update(new MallGoods(), new LambdaUpdateWrapper<MallGoods>()
                .eq(MallGoods::getStatus, GoodsStatusEnum.WAIT_AUDIT.getStatus())
                .set(MallGoods::getStatus, GoodsStatusEnum.OFFLINE.getStatus())
                .set(MallGoods::getAuditStatus, GoodsAuditStatusEnum.WAIT_AUDIT.getStatus())
        );
        mallGoodsMapper.update(new MallGoods(), new LambdaUpdateWrapper<MallGoods>()
                .eq(MallGoods::getStatus, GoodsStatusEnum.AUDIT_REFUSE.getStatus())
                .set(MallGoods::getStatus, GoodsStatusEnum.OFFLINE.getStatus())
                .set(MallGoods::getAuditStatus, GoodsAuditStatusEnum.AUDIT_REFUSE.getStatus())
        );
    }

    // 处理自定义底部菜单数据
    public void handleDiyBottomData() {
        SysConfigMapper sysConfigMapper = SpringContext.bean(SysConfigMapper.class);
        Cached cached = SpringContext.getBean("redisCacheOperator", Cached.class);
        List<SysConfig> configList = sysConfigMapper.selectList(new LambdaQueryWrapper<SysConfig>()
                .gt(SysConfig::getSiteId, 0)
                .eq(SysConfig::getConfigKey, DIY_BOTTOM_MALL_KEY)
        );
        if (configList.isEmpty()) {
            return;
        }
        for (SysConfig config : configList) {
            if (StrUtil.isBlank(config.getValue())) {
                continue;
            }
            JSONObject value = JSONUtil.parseObj(config.getValue());
            JSONArray list = value.getJSONArray("list");
            if (list == null || list.isEmpty()) {
                continue;
            }
            Integer siteId = config.getSiteId();
            for (int i = 0; i < list.size(); i++) {
                JSONObject item = list.getJSONObject(i);
                if (item == null) {
                    continue;
                }
                JSONObject link = item.getJSONObject("link");
                if (link == null) {
                    continue;
                }
                String name = link.getStr("name", "");
                switch (name) {
                    case "MALL_SHOP_INDEX" -> item.set("link", buildShopLink("MALL_SHOP_INDEX", "店铺首页",
                            "/addon/mall/pages/index?site_id=" + siteId));
                    case "MALL_SHOP_GOODS_LIST" ->
                            item.set("link", buildShopLink("MALL_SHOP_GOODS_LIST", "店铺商品列表",
                                    "/addon/mall/pages/home/goods?site_id=" + siteId));
                    case "MALL_SHOP_GOODS_CATEGORY" ->
                            item.set("link", buildShopLink("MALL_SHOP_GOODS_CATEGORY", "店铺商品分类",
                                    "/addon/mall/pages/home/category?site_id=" + siteId));
                    case "MALL_SHOP_COUPON_LIST" ->
                            item.set("link", buildShopLink("MALL_SHOP_COUPON_LIST", "店铺优惠券列表",
                                    "/addon/mall/pages/home/coupon?site_id=" + siteId));
                    default -> {
                        String url = link.getStr("url", "");
                        if (StrUtil.isNotBlank(url) && !url.contains("site_id")) {
                            link.set("url", appendSiteIdToUrl(url, siteId));
                        }
                    }
                }
            }
            SysConfig updateConfig = new SysConfig();
            updateConfig.setId(config.getId());
            updateConfig.setValue(value.toString());
            sysConfigMapper.updateById(updateConfig);
            cached.tag("sys_config" + siteId).clear();
        }
    }

    // 处理自定义页面数据
    public void handleDiyPageData() {
        DiyPageMapper diyPageMapper = SpringContext.bean(DiyPageMapper.class);
        Cached cached = SpringContext.getBean("redisCacheOperator", Cached.class);
        List<DiyPage> pageList = diyPageMapper.selectList(new LambdaQueryWrapper<DiyPage>()
                .ne(DiyPage::getSiteId, 0)
                .ne(DiyPage::getValue, "")
                .eq(DiyPage::getName, "DIY_SITE_INDEX")
                .like(DiyPage::getTemplate, "mall_site_index_style")
        );
        if (pageList.isEmpty()) {
            return;
        }
        for (DiyPage page : pageList) {
            JSONObject diyData = JSONUtil.parseObj(page.getValue());
            JSONArray components = diyData.getJSONArray("value");
            if (components == null || components.isEmpty()) {
                continue;
            }
            boolean changed = false;
            for (int i = 0; i < components.size(); i++) {
                JSONObject item = components.getJSONObject(i);
                if (item == null) {
                    continue;
                }
                String componentName = item.getStr("componentName", "");
                if ("MallGoodsList".equals(componentName)) {
                    if (!item.containsKey("searchCondition")) {
                        JSONObject searchCondition = new JSONObject();
                        searchCondition.set("isShow", false);
                        item.set("searchCondition", searchCondition);
                        changed = true;
                    }
                    if (!item.containsKey("sortType")) {
                        item.set("sortType", "desc");
                        changed = true;
                    }
                } else if ("MallShopInfo".equals(componentName) && !item.containsKey("style")) {
                    item.set("style", "style-2");
                    changed = true;
                }
            }
            if (!changed) {
                continue;
            }
            DiyPage updatePage = new DiyPage();
            updatePage.setId(page.getId());
            updatePage.setValue(diyData.toString());
            diyPageMapper.updateById(updatePage);
            cached.tag("sys_config" + page.getSiteId()).clear();
        }
    }

    // 处理虚拟商品发货方式
    public void handleVirtualGoodsData() {
        MallGoodsMapper mallGoodsMapper = SpringContext.bean(MallGoodsMapper.class);
        mallGoodsMapper.update(new MallGoods(), new LambdaUpdateWrapper<MallGoods>()
                .eq(MallGoods::getGoodsType, GoodsTypeEnum.VIRTUAL.getType())
                .eq(MallGoods::getVirtualAutoDelivery, 0)
                .ne(MallGoods::getVirtualReceiveType, "verify")
                .set(MallGoods::getVirtualDeliveryType, "manual")
        );
        mallGoodsMapper.update(new MallGoods(), new LambdaUpdateWrapper<MallGoods>()
                .eq(MallGoods::getGoodsType, GoodsTypeEnum.VIRTUAL.getType())
                .eq(MallGoods::getVirtualAutoDelivery, 1)
                .ne(MallGoods::getVirtualReceiveType, "verify")
                .set(MallGoods::getVirtualDeliveryType, "auto")
        );
        mallGoodsMapper.update(new MallGoods(), new LambdaUpdateWrapper<MallGoods>()
                .eq(MallGoods::getGoodsType, GoodsTypeEnum.VIRTUAL.getType())
                .eq(MallGoods::getVirtualReceiveType, "verify")
                .set(MallGoods::getVirtualDeliveryType, "verify")
        );
    }

    // 处理虚拟商品订单数据
    public void handleOrderGoodsData() {
        MallOrderMapper mallOrderMapper = SpringContext.bean(MallOrderMapper.class);
        MallOrderGoodsMapper mallOrderGoodsMapper = SpringContext.bean(MallOrderGoodsMapper.class);
        MallGoodsMapper mallGoodsMapper = SpringContext.bean(MallGoodsMapper.class);

        List<MallOrder> orders = mallOrderMapper.selectList(new LambdaQueryWrapper<MallOrder>()
                .notIn(MallOrder::getStatus,
                        String.valueOf(OrderStatusEnum.FINISH.getStatus()),
                        String.valueOf(OrderStatusEnum.CLOSE.getStatus()))
                .eq(MallOrder::getDeliveryType, OrderDeliveryTypeEnum.VIRTUAL.getType())
        );
        if (orders.isEmpty()) {
            return;
        }
        List<Integer> orderIds = orders.stream().map(MallOrder::getOrderId).collect(Collectors.toList());
        List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(new LambdaQueryWrapper<MallOrderGoods>()
                .in(MallOrderGoods::getOrderId, orderIds)
                .eq(MallOrderGoods::getGoodsType, GoodsTypeEnum.VIRTUAL.getType())
        );
        if (orderGoodsList.isEmpty()) {
            return;
        }
        Set<Integer> goodsIds = orderGoodsList.stream().map(MallOrderGoods::getGoodsId).collect(Collectors.toSet());
        List<MallGoods> goodsList = mallGoodsMapper.selectList(new LambdaQueryWrapper<MallGoods>()
                .in(MallGoods::getGoodsId, goodsIds)
        );
        for (MallGoods goods : goodsList) {
            String goodsConfig = buildGoodsConfig(goods);
            String subDeliveryType = "verify".equals(goods.getVirtualDeliveryType())
                    ? "verify" : OrderDeliveryTypeEnum.VIRTUAL.getType();
            mallOrderGoodsMapper.update(new MallOrderGoods(), new LambdaUpdateWrapper<MallOrderGoods>()
                    .in(MallOrderGoods::getOrderId, orderIds)
                    .eq(MallOrderGoods::getGoodsId, goods.getGoodsId())
                    .set(MallOrderGoods::getGoodsConfig, goodsConfig)
                    .set(MallOrderGoods::getVirtualRefundType, goods.getVirtualRefund())
            );
            List<Integer> itemOrderIds = mallOrderGoodsMapper.selectList(new LambdaQueryWrapper<MallOrderGoods>()
                            .in(MallOrderGoods::getOrderId, orderIds)
                            .eq(MallOrderGoods::getGoodsId, goods.getGoodsId())
                            .select(MallOrderGoods::getOrderId)
                    ).stream()
                    .map(MallOrderGoods::getOrderId)
                    .distinct()
                    .collect(Collectors.toList());
            if (ObjectUtil.isEmpty(itemOrderIds)) {
                continue;
            }
            mallOrderMapper.update(new MallOrder(), new LambdaUpdateWrapper<MallOrder>()
                    .in(MallOrder::getOrderId, itemOrderIds)
                    .set(MallOrder::getSubDeliveryType, subDeliveryType)
            );
        }
    }

    // 处理虚拟核销商品主动发货
    public void handleVirtualVerifyOrderData() {
        MallOrderMapper mallOrderMapper = SpringContext.bean(MallOrderMapper.class);
        ICoreOrderEventService coreOrderEventService = SpringContext.bean(ICoreOrderEventService.class);
        List<MallOrder> orderList = mallOrderMapper.selectList(new LambdaQueryWrapper<MallOrder>()
                .eq(MallOrder::getStatus, String.valueOf(OrderStatusEnum.WAIT_DELIVERY.getStatus()))
                .eq(MallOrder::getDeliveryType, OrderDeliveryTypeEnum.VIRTUAL.getType())
                .eq(MallOrder::getSubDeliveryType, "verify")
        );
        for (MallOrder order : orderList) {
            coreOrderEventService.handleVirtualOrderDelivery(order);
        }
    }

    private JSONObject buildShopLink(String name, String title, String url) {
        JSONObject link = new JSONObject();
        link.set("name", name);
        link.set("parent", "MALL_SHOP_LINK");
        link.set("title", title);
        link.set("url", url);
        return link;
    }

    private String appendSiteIdToUrl(String url, Integer siteId) {
        return url + (url.contains("?") ? "&" : "?") + "site_id=" + siteId;
    }

    private String buildGoodsConfig(MallGoods goods) {
        JSONObject config = new JSONObject();
        config.set("virtual_delivery_type", goods.getVirtualDeliveryType());
        config.set("virtual_delivery_content", goods.getVirtualDeliveryContent());
        config.set("virtual_receive_type", goods.getVirtualReceiveType());
        config.set("virtual_verify_type", goods.getVirtualVerifyType());
        config.set("virtual_indate", goods.getVirtualIndate());
        config.set("virtual_refund", goods.getVirtualRefund());
        config.set("virtual_refund_day", goods.getVirtualRefundDay());
        config.set("virtual_verify_mobile", goods.getVirtualVerifyMobile());
        return config.toString();
    }

}
