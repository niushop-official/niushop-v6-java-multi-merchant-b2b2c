package com.niu.core.controller.api.goods;

import com.niu.core.common.domain.PageParam;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.goods.IGoodsServiceService;
import com.niu.core.service.api.goods.vo.GoodsServiceListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品服务控制器
 */

@RestController
@RequestMapping("api/basic/goods/service")
public class ServiceController {

    @Resource
    IGoodsServiceService goodsServiceService;

    /**
     * 商品服务列表
     *
     * @return Result<PageResult<ShopGoodsServiceListVo>>
     */
    @GetMapping("")
    public Result<List<GoodsServiceListVo>> list() {
        List<GoodsServiceListVo> list = goodsServiceService.getAll();
        return Result.success(list);
    }




}
