package com.niu.mall.controller.siteapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.goods.param.*;
import com.niu.mall.service.site.goods.IMallGoodsLabelService;
import com.niu.mall.service.site.goods.vo.MallGoodsLabelListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品标签控制器
 */

@RestController
@RequestMapping("adminapi/mall/site/goods/label")
public class LabelController {

    @Resource
    IMallGoodsLabelService goodsLabelService;


    /**
     * 商品标签列表
     *
     * @param searchParam 搜索条件
     * @return Result<PageResult<MallGoodsLabelListVo>>
     */
    @GetMapping("/list")
    public Result<List<MallGoodsLabelListVo>> list(@Validated MallGoodsLabelSearchParam searchParam) {
        List<MallGoodsLabelListVo> list = goodsLabelService.list(searchParam);
        return Result.success(list);
    }
}
