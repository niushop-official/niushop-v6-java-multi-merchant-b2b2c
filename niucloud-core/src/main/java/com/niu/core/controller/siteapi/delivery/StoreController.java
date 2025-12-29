package com.niu.core.controller.siteapi.delivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.local_delivery.LocalDeliveryStoreTypeEnum;
import com.niu.core.service.core.delivery_store.param.DeliveryStoreShopEditParam;
import com.niu.core.service.site.delivery.IDeliveryStoreService;
import com.niu.core.service.site.delivery.param.DeliveryStoreAddParam;
import com.niu.core.service.site.delivery.param.DeliveryStoreListSearchParam;
import com.niu.core.service.site.delivery.param.DeliveryStoreModifyParam;
import com.niu.core.service.site.delivery.param.DeliveryStoreSearchParam;
import com.niu.core.service.site.delivery.vo.DeliveryStoreInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryStoreInitInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryStorePageVo;
import com.niu.core.service.site.delivery.vo.DeliveryStoreVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配送方式控制器
 */
@RestController
@RequestMapping("adminapi/delivery/site/delivery/store")
public class StoreController {

    @Resource
    private IDeliveryStoreService deliveryStoreService;

    /**
     * 配送门店列表（分页）
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopOrderListVo>>
     */
    @GetMapping("")
    public Result<PageResult<DeliveryStorePageVo>> list(@Validated PageParam pageParam, @Validated DeliveryStoreSearchParam searchParam) {
        PageResult<DeliveryStorePageVo> list = deliveryStoreService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 配送门店初始化
     * @return Result<PageResult<ShopOrderListVo>>
     */
    @GetMapping("/init")
    public Result<DeliveryStoreInitInfoVo> getInitInfo() {
        return Result.success(deliveryStoreService.getInitInfo());
    }

    /**
     * 配送门店列表（不分页）
     * @param searchParam 分页
     * @param searchParam 搜索条件
     */
    @GetMapping("/list")
    public Result<List<DeliveryStoreVo>> list(@Validated DeliveryStoreListSearchParam searchParam) {
        return Result.success(deliveryStoreService.getList(searchParam));
    }

    /**
     * 配送门店详情
     */
    @GetMapping("/{id}")
    public Result<DeliveryStoreInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(deliveryStoreService.info(id));
    }

    /**
     * 添加配送门店
     */
    @PostMapping("")
    public Result<?> add(@Validated @RequestBody DeliveryStoreAddParam param) {
        Integer id = deliveryStoreService.add(param);
        return Result.success("添加成功", id);
    }

    /**
     * 配送门店编辑
     */
    @PutMapping("/{id}")
    public Result<?> edit(@PathVariable("id") Integer storeId,@Validated @RequestBody DeliveryStoreAddParam param) {
        deliveryStoreService.edit(storeId,param);
        return Result.success(true);
    }

    /**
     * 删除配送门店
     */
    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable("id") Integer storeId) {
        deliveryStoreService.del(storeId);
        return Result.success("删除成功");
    }

    /**
     * 配送门店提货类型
     */
    @GetMapping("/pick_up_type")
    public Result<?> getPickUpType() {
        return Result.success(LocalDeliveryStoreTypeEnum.getMap());
    }

    /**
     * 修改配送门店状态
     */
    @PutMapping("/modify_status")
    public Result<?> modifyStatus(@Validated @RequestBody DeliveryStoreModifyParam param) {
        deliveryStoreService.modifyStatus(param);
        return Result.success(true);
    }

}
