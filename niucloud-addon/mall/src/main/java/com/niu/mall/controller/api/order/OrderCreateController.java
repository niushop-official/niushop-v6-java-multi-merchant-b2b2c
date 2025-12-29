package com.niu.mall.controller.api.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.service.api.order.IOrderCreateService;
import com.niu.mall.service.core.order.param.MallOrderCalculateParam;
import com.niu.mall.service.core.order.param.MallOrderCreateParam;
import com.niu.mall.service.core.order.vo.MallOrderCreateDataVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/mall")
@SaCheckLogin
public class OrderCreateController {

    @Resource
    IOrderCreateService orderCreateService;

    /**
     * 计算
     *
     * @return
     */
    @PostMapping("/order_create/calculate")
    public Result<MallOrderCreateDataVo>  calculate(@Validated @RequestBody MallOrderCalculateParam param) {
        return Result.success(orderCreateService.calculate(param));
    }

    /**
     * 创建订单
     *
     * @return
     */
    @PostMapping("/order_create/create")
    public Result create(@Validated @RequestBody MallOrderCreateParam param) {
        return Result.success(orderCreateService.create(param));
    }


    /**
     * 查询店铺优惠券
     * @param map
     * @return
     */
    @PostMapping("/order_create/coupon")
    public Result getCoupon(@Validated @RequestBody Map<String,Map<Integer,Map<String,Object>>> map) {

        Map<Integer,Map<String,Object>> body = map.get("body");
        return Result.success(orderCreateService.getCoupon(body));
    }

    /**
     * 查询平台优惠券
     * @param map
     * @return
     */
    @PostMapping("/order_create/platform_coupon")
    public Result getPlatformCoupon(@Validated @RequestBody Map<String,Map<Integer,Map<String,Object>>> map) {
        //从购物车结算或有多个店铺多个商品逻辑
        List<Object> orderKeys = new ArrayList<>();
        Map<Integer,Map<String,Object>> body = map.get("body");
        Set<Integer> siteObjs = body.keySet();
        for(int siteObj : siteObjs){
            Map<String,Object> site = body.get(siteObj);
            Object orderKey = site.get("order_key");
            orderKeys.add(orderKey);
        }
        return Result.success(orderCreateService.getPlatformCoupon(orderKeys));
    }

    /**
     * 查询店铺优惠券
     * @param map
     * @return
     */
    @PostMapping("/order_create/store")
    public Result getStore(@Validated @RequestBody Map<String,Map<Integer,Map<String,Object>>> map) {

        Map<Integer,Map<String,Object>> body = map.get("body");
        return Result.success(orderCreateService.getCoupon(body));
    }
}
