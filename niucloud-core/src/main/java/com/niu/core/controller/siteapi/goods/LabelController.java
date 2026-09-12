package com.niu.core.controller.siteapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.goods.param.GoodsLabelSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsLabelListVo;
import com.niu.core.service.site.goods.IGoodsLabelService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品标签控制器（站点端）
 * 对应 PHP: app\adminapi\controller\goods\site\Label
 */
@RestController("siteGoodsLabelController")
@RequestMapping("adminapi/basic/site/goods/label")
public class LabelController {

    @Resource
    private IGoodsLabelService goodsLabelService;

    /**
     * 商品标签列表（启用）
     */
    @GetMapping("/list")
    public Result<List<GoodsLabelListVo>> list(@Validated GoodsLabelSearchParam searchParam) {
        return Result.success(goodsLabelService.list(searchParam));
    }
}
