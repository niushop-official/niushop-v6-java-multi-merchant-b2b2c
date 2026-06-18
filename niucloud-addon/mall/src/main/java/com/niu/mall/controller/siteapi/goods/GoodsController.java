package com.niu.mall.controller.siteapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.service.site.goods.IMallGoodsService;
import com.niu.mall.service.site.goods.param.*;
import com.niu.mall.service.site.goods.vo.MallGoodsInfoVo;
import com.niu.mall.service.site.goods.vo.MallGoodsListVo;
import com.niu.mall.service.site.goods.vo.MallGoodsSkuListVo;
import com.niu.mall.service.site.goods.vo.MallSelectGoodsListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */

@RestController
@RequestMapping("adminapi/mall/site/goods")
public class GoodsController {

    @Resource
    IMallGoodsService goodsService;

    /**
     * 商品列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < MallGoodsListVo>>
     */
    @GetMapping("")
    public Result<PageResult<MallGoodsListVo>> list(@Validated PageParam pageParam, @Validated MallGoodsSearchParam searchParam) {
        PageResult<MallGoodsListVo> list = goodsService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品详情
     *
     * @param id 主键ID
     * @return Result<MallGoodsInfoVo>
     */
    @GetMapping("/{id}")
    public Result<MallGoodsInfoVo> info(@PathVariable("id") Integer id) {
        MallGoodsInfoVo info = goodsService.info(id);
        return Result.success(info);
    }

    /**
     * 商品添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody MallGoodsParam addParam) {
        goodsService.add(addParam);
        return Result.success();
    }

    /**
     * 商品编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody MallGoodsParam editParam) {
        goodsService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品删除
     *
     * @return Result<Object>
     */
    @PutMapping("/delete")
    public Result<Object> del(@Validated @RequestBody GetGoodsIdsParam param) {
        goodsService.del(param);
        return Result.success();
    }

    /**
     * 恢复商品
     *
     * @return Result<Object>
     */
    @PutMapping("/recycle")
    public Result<Object> recycle(@Validated @RequestBody GetGoodsIdsParam param) {
        goodsService.recycle(param);
        return Result.success();
    }

    /**
     * 商品回收站
     *
     * @return Result<Object>
     */
    @GetMapping("/recycle")
    public Result<PageResult<MallGoodsListVo>> recyclePage(@Validated PageParam pageParam, @Validated MallGoodsSearchParam searchParam) {
        return Result.success(goodsService.recyclePage(pageParam, searchParam));
    }

    /**
     * 修改商品上下架状态
     *
     * @return Result<Object>
     */
    @PutMapping("/status")
    public Result<Object> editStatus(@Validated @RequestBody EditGoodsStatusParam param) {
        goodsService.editStatus(param);
        return Result.success();
    }

    /**
     * 修改商品上下架状态
     *
     * @return Result<Object>
     */
    @GetMapping("/status")
    public Result<Object> status() {
        return Result.success(goodsService.getStatus());
    }

    /**
     * 修改商品排序号
     *
     * @return Result<Object>
     */
    @PutMapping("/sort")
    public Result<Object> editSort(@Validated @RequestBody EditGoodsSortParam param) {
        goodsService.editSort(param);
        return Result.success();
    }

    /**
     * 获取商品类型
     *
     * @return
     */
    @GetMapping("/type")
    public Result<Map<String, Map<String, Object>>> getGoodsType() {
        return Result.success(GoodsTypeEnum.getMap());
    }

    /**
     * 商品添加/编辑数据
     *
     * @return
     */
    @GetMapping("/init")
    public Result<?> init(@RequestParam(value = "goods_id", defaultValue = "0") Integer goodsId) {
        return Result.success(goodsService.getInit(goodsId));
    }

    /**
     * 复制商品
     *
     * @param goodsId
     * @return
     */
    @PutMapping("/copy/{goods_id}")
    public Result<Object> copy(@PathVariable("goods_id") Integer goodsId) {
        goodsService.copy(goodsId);
        return Result.success();
    }

    /**
     * 查询商品参与营销活动的数量
     *
     * @return
     */
    @GetMapping("/active/count")
    public Result<?> getActiveGoodsCount(@RequestParam(name = "goods_id") Integer goodsIds) {
        return Result.success(goodsService.getActiveGoodsCount(goodsIds));
    }

    /**
     * 获取商品选择分页列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < MallSelectGoodsListVo>>
     */
    @GetMapping("/select")
    public Result<MallSelectGoodsListVo> getSelectPage(
            @Validated PageParam pageParam,
            @Validated MallGoodsSelectSearchParam searchParam,
            @RequestParam(name = "goods_ids[]", required = false) List<Integer> goodsIds,
            @RequestParam(name = "sku_ids[]", required = false) List<Integer> skuIds,
            @RequestParam(name = "verify_goods_ids[]", required = false) List<Integer> verifyGoodsIds,
            @RequestParam(name = "verify_sku_ids[]", required = false) List<Integer> verifySkuIds) {
        searchParam.setGoodsIds(goodsIds);
        searchParam.setSkuIds(skuIds);
        searchParam.setVerifyGoodsIds(verifyGoodsIds);
        searchParam.setVerifySkuIds(verifySkuIds);
        MallSelectGoodsListVo list = goodsService.getSelectPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取商品选择列表带ku
     *
     * @return
     */
    @GetMapping("/selectgoodssku")
    public Result getSelectGoodsSku(@Validated MallGoodsSelectSearchParam searchParam,
                                    @RequestParam(name = "goods_ids[]", required = false) List<Integer> goodsIds,
                                    @RequestParam(name = "sku_ids[]", required = false) List<Integer> skuIds,
                                    @RequestParam(name = "verify_goods_ids[]", required = false) List<Integer> verifyGoodsIds,
                                    @RequestParam(name = "verify_sku_ids[]", required = false) List<Integer> verifySkuIds) {
        searchParam.setGoodsIds(goodsIds);
        searchParam.setSkuIds(skuIds);
        searchParam.setVerifyGoodsIds(verifyGoodsIds);
        searchParam.setVerifySkuIds(verifySkuIds);
        return Result.success(goodsService.getSelectGoodsSku(searchParam));
    }

    /**
     * 查询商品SKU规格列表
     *
     * @return Result<List < MallGoodsSkuListVo>>
     */
    @GetMapping("/sku")
    public Result<List<MallGoodsSkuListVo>> getSkuList(@RequestParam("goods_id") Integer goodsId) {
        return Result.success(goodsService.getSkuList(goodsId));
    }

    /**
     * 编辑商品规格列表库存
     *
     * @return
     */
    @PutMapping("/sku/stock")
    public Result editGoodsListStock(@Validated @RequestBody EditGoodsListStockParam param) {
        goodsService.editGoodsListStock(param);
        return Result.success();
    }

    /**
     * 编辑商品规格列表价格
     *
     * @return
     */
    @PutMapping("/sku/price")
    public Result editGoodsListPrice(@Validated @RequestBody EditGoodsListPriceParam param) {
        goodsService.editGoodsListPrice(param);
        return Result.success();
    }

    /**
     * 编辑商品规格列表会员价
     *
     * @return
     */
    @PutMapping("/sku/member_price")
    public Result editGoodsListMemberPrice(@Validated @RequestBody EditGoodsListMemberPriceParam param) {
        goodsService.editGoodsListMemberPrice(param);
        return Result.success();
    }

    @GetMapping("/buy/goods/select")
    public Result<?> buyGoodsSelect(PageParam pageParam, BuyGoodsSelectParam param) {
        return Result.success(goodsService.getBuyGoodsSelect(pageParam, param));
    }

    @GetMapping("/buy/sku/select")
    public Result<?> buySkuSelect(BuySkuSelectParam param) {
        return Result.success(goodsService.getBuySkuSelect(param));
    }

    @PostMapping("/buy/goods/selected")
    public Result<?> buyGoodsSelected(BuyGoodsSelectedParam param) {
        return Result.success(goodsService.getBuyGoodsSelected(param));
    }

    @GetMapping("/source")
    public Result<?> source() {
        return Result.success(goodsService.getSource());
    }

    /**
     * 获取批量设置类型
     *
     * @return Result<Map < String, String>>
     * @description 获取批量设置类型
     */
    @GetMapping("/batchSet/dict")
    public Result<Map<String, String>> getBatchSetDict() {
        return Result.success(goodsService.getBatchSetDict());
    }

    /**
     * 批量设置商品
     *
     * @param param 批量设置参数
     * @return Result<Object>
     * @description 批量设置商品
     */
    @PutMapping("/batchSet")
    public Result<Object> batchSet(@Validated @RequestBody GoodsBatchSetParam param) {
        goodsService.batchSet(param);
        return Result.success();
    }

    /**
     * 查询商品同步数据
     */
    @GetMapping("/sync")
    public Result sync(@RequestParam("goods_id") Integer goodsId, @RequestParam("supply_sync") Integer supplySync) {
        return Result.success(goodsService.sync(goodsId, supplySync));
    }

    @GetMapping("/status_type")
    public Result<?> getStatusType() {
        return Result.success(goodsService.getStatusType());
    }

    /**
     * 订单支付方式
     *
     * @return
     */
    @GetMapping("/payTypeList")
    public Result getPayType() {
        return Result.success(PayTypeEnum.getType());
    }
}
