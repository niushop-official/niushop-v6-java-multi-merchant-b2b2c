package com.niu.mall.controller.adminapi.goods;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.enums.goods.GoodsStatusTypeEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.service.admin.goods.IMallGoodsConfigService;
import com.niu.mall.service.admin.goods.IMallGoodsService;
import com.niu.mall.service.admin.goods.param.*;
import com.niu.mall.service.admin.goods.vo.MallGoodsInfoVo;
import com.niu.mall.service.admin.goods.vo.MallGoodsListVo;
import com.niu.mall.service.admin.goods.vo.MallGoodsSkuListVo;
import com.niu.mall.service.admin.goods.vo.MallSelectGoodsListVo;
import com.niu.mall.service.core.goods.vo.GoodsConfigVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */

@RestController
@RequestMapping("adminapi/mall/admin/goods")
public class GoodsController {

    @Resource
    IMallGoodsService goodsService;

    @Resource
    IMallGoodsConfigService goodsConfigService;

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
     * 获取商品状态-514
     *
     * @return Result<Object>
     */
    @GetMapping("/status_type")
    public Result<Object> statusType() {
        return Result.success(GoodsStatusTypeEnum.getMap());
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

    /**
     * 获取商品设置配置
     *
     * @return
     */
    @GetMapping("/config")
    public Result<GoodsConfigVo> getGoodsConfig() {
        return Result.success(goodsConfigService.getGoodsConfig());
    }

    /**
     * 设置商品设置配置
     *
     * @return
     */
    @PostMapping("/config")
    public Result<Object> setGoodsConfig(@RequestBody JSONObject data) {
        goodsConfigService.setGoodsConfig(data);
        return Result.success();
    }

    /**
     * 商品审核通过
     * 520
     *
     * @param param
     * @return
     */
    @PutMapping("/audit")
    public Result auditStatus(@Validated @RequestBody EditGoodsAuditStatusParam param) {
        return Result.success(goodsService.auditStatus(param));
    }

    /**
     * 商品审核拒绝
     * 520
     *
     * @param param
     * @return
     */
    @PutMapping("/refuse")
    public Result refuseStatus(@Validated @RequestBody EditGoodsRefuseStatusParam param) {
        return Result.success(goodsService.refuseStatus(param));
    }

    /**
     * 商品强制下架
     * 520
     *
     * @param param
     * @return
     */
    @PutMapping("/offline")
    public Result offlineStatus(@Validated @RequestBody EditGoodsRefuseStatusParam param) {
        return Result.success(goodsService.offlineStatus(param));
    }

    @GetMapping("/source")
    public Result source() {
        return Result.success(goodsService.getSource());
    }

    @PutMapping("/edit_label")
    public Result editLabel(@Validated @RequestBody EditGoodsLabelParam param) {
        goodsService.editLabel(param);
        return Result.success();
    }
}
