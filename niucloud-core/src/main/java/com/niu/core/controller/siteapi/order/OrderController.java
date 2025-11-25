package com.niu.core.controller.siteapi.order;

import com.niu.core.common.domain.Result;
import com.niu.core.enums.order.TradeTypeEnum;
import com.niu.core.service.site.goods.IGoodsBrandService;
import com.niu.core.service.site.goods.param.GoodsBrandSearchParam;
import com.niu.core.service.site.goods.vo.GoodsBrandListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 交易类型
 */

@RestController
@RequestMapping("adminapi/basic")
public class OrderController {


    /**
     * 商品品牌列表
     *
     * @return Result<PageResult<GoodsBrandListVo>>
     */
    @GetMapping("/get_trade")
    public Result<Object> getTradeType() {
        return Result.success(TradeTypeEnum.getType());
    }

}
