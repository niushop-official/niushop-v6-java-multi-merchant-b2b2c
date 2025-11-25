package com.niu.mall.controller.siteapi.supplygoods;


import com.niu.core.common.domain.Result;
import com.niu.mall.service.site.supply_goods.IMallSupplyGoodsService;
import com.niu.mall.service.site.supply_goods.vo.MallSupplyGoodsInfoVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 站点 选品平台
 * @author gjl
 * @date 2025/09/25
 */
@RestController
@RequestMapping("adminapi/mall/site/supply_goods")
public class MallSupplyGoodsController {

    @Resource
    IMallSupplyGoodsService mallSupplyGoodsService;

    /**
     * 供货商商品详情
     */
    @GetMapping("/{id}")
    public Result<MallSupplyGoodsInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(mallSupplyGoodsService.getInfo( id));
    }
}
