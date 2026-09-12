package com.niu.core.controller.siteapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.goods.param.GoodsServiceParam;
import com.niu.core.service.site.goods.param.GoodsServiceSearchParam;
import com.niu.core.service.site.goods.IGoodsServiceService;
import com.niu.core.service.site.goods.vo.GoodsServiceInfoVo;
import com.niu.core.service.site.goods.vo.GoodsServiceListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品服务控制器
 */

@RestController
@RequestMapping("adminapi/basic/site/goods/service")
public class ServiceController {

    @Resource
    IGoodsServiceService goodsServiceService;

    /**
     * 商品服务分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<GoodsServiceListVo>>
     */
    @GetMapping("")
    public Result<PageResult<GoodsServiceListVo>> page(@Validated PageParam pageParam, @Validated GoodsServiceSearchParam searchParam) {
        PageResult<GoodsServiceListVo> list = goodsServiceService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品服务列表
     *
     * @param searchParam 搜索条件
     * @return Result<PageResult<GoodsServiceListVo>>
     */
    @GetMapping("/list")
    public Result<List<GoodsServiceListVo>> list(@Validated PageParam pageParam, @Validated GoodsServiceSearchParam searchParam) {
        List<GoodsServiceListVo> list = goodsServiceService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品服务详情
     *
     * @param id 主键ID
     * @return Result<GoodsServiceInfoVo>
     */
    @GetMapping("/{id}")
    public Result<GoodsServiceInfoVo> info(@PathVariable("id") Integer id) {
        GoodsServiceInfoVo info = goodsServiceService.info(id);
        return Result.success(info);
    }

    /**
     * 获取符合条件的插件多个数据
     *
     * @param addon 插件标识
     * @return Result<List<GoodsServiceListVo>>
     */
    @GetMapping("/addon/{addon}")
    public Result<List<GoodsServiceListVo>> getAddonServiceList(@PathVariable("addon") String addon) {
        return Result.success(goodsServiceService.getAddonServiceList(addon));
    }

}
