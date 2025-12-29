
package com.niu.core.service.site.delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.consts.local_delivery.LocalDeliveryConst;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.enums.local_delivery.LocalDeliveryStoreTypeEnum;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.delivery_store.ICoreDeliveryStoreService;
import com.niu.core.service.site.delivery.param.DeliveryStoreAddParam;
import com.niu.core.service.site.delivery.param.DeliveryStoreListSearchParam;
import com.niu.core.service.site.delivery.param.DeliveryStoreModifyParam;
import com.niu.core.service.site.delivery.param.DeliveryStoreSearchParam;
import com.niu.core.service.site.delivery.vo.*;
import com.niu.core.service.site.local_delivery.ILocalDeliveryService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.niu.core.service.site.delivery.IDeliveryStoreService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 配送门店服务实现
 */
@Log4j2
@Service
public class DeliveryStoreServiceImpl implements IDeliveryStoreService {

    @Resource
    private DeliveryStoreMapper deliveryStoreMapper;
    @Resource
    private ILocalDeliveryService localDeliveryService;
    @Resource
    private ICoreDeliveryConfigService coreDeliveryConfigService;
    @Resource
    private ICoreDeliveryStoreService coreDeliveryStoreService;

    @Override
    public PageResult<DeliveryStorePageVo> page(PageParam pageParam, DeliveryStoreSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<DeliveryStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());

        queryWrapper.like(StringUtils.isNotEmpty(searchParam.getStoreName()),"store_name", searchParam.getStoreName());
        if(StringUtils.isNotEmpty(searchParam.getPickUpType())){
            if(searchParam.getPickUpType().equals(LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType())){
                queryWrapper.eq("support_local_delivery", 1);
            }
            if(searchParam.getPickUpType().equals(LocalDeliveryStoreTypeEnum.STORE.getType())){
                queryWrapper.eq("support_store", 1);
            }
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        queryWrapper.orderByDesc("is_system","create_time");
        IPage<DeliveryStore> iPage = deliveryStoreMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<DeliveryStorePageVo> list = new ArrayList<>();
        for (DeliveryStore deliveryStore : iPage.getRecords()) {
            DeliveryStorePageVo deliveryStorePageVo = new DeliveryStorePageVo();
            BeanUtils.copyProperties(deliveryStore, deliveryStorePageVo);
            list.add(deliveryStorePageVo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public DeliveryStoreInfoVo info(Integer storeId) {
        DeliveryStoreInfoVo deliveryStoreInfoVo = new DeliveryStoreInfoVo();
        DeliveryStore info = deliveryStoreMapper.selectOne(new QueryWrapper<DeliveryStore>().eq("store_id", storeId)
                .eq("site_id", RequestUtils.siteId()));
        if(ObjectUtil.isNotEmpty( info)){
            BeanUtils.copyProperties(info, deliveryStoreInfoVo);
        }
        if(ObjectUtil.isEmpty(deliveryStoreInfoVo.getTimeInterval())){
            deliveryStoreInfoVo.setTimeInterval(LocalDeliveryConst.TIME_INTERVAL_30);
        }
        return deliveryStoreInfoVo;
    }

    @Override
    public DeliveryStoreInitInfoVo getInitInfo() {
        DeliveryStoreInitInfoVo deliveryStoreInitInfoVo = new DeliveryStoreInitInfoVo();
        deliveryStoreInitInfoVo.setWeekList(JSONUtil.toJsonStr(LocalDeliveryConst.getWeekList(null)));
        deliveryStoreInitInfoVo.setTimeIntervalList(JSONUtil.toJsonStr(LocalDeliveryConst.getTimeIntervalList(null)));
        deliveryStoreInitInfoVo.setLocalDeliveryConfig(localDeliveryService.getLocal(RequestUtils.siteId()));
        return deliveryStoreInitInfoVo;
    }

    @Override
    @Transactional
    public Integer add(DeliveryStoreAddParam param) {
        DeliveryStore deliveryStore = new DeliveryStore();
        BeanUtil.copyProperties(param, deliveryStore);
        deliveryStore.setSiteId(RequestUtils.siteId());
        deliveryStore.setCreateTime(System.currentTimeMillis()/1000);
        deliveryStore.setUpdateTime(System.currentTimeMillis()/1000);
        deliveryStore.setStoreNo(CommonUtils.createNo("store",""));
        if(StringUtils.isNotEmpty(param.getPickUpType())){
            if(param.getPickUpType().contains(LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType())){
                deliveryStore.setSupportLocalDelivery(1);
            }else{
                deliveryStore.setSupportLocalDelivery(0);
            }
            if(param.getPickUpType().contains(LocalDeliveryStoreTypeEnum.STORE.getType())){
                deliveryStore.setSupportStore(1);
            }else{
                deliveryStore.setSupportStore(0);
            }
        }else{
            deliveryStore.setSupportLocalDelivery(0);
            deliveryStore.setSupportStore(0);
        }
        deliveryStore.setArea(ObjectUtil.isNotEmpty(param.getArea()) ? param.getArea().toString() : "");
        deliveryStore.setTimeWeek(ObjectUtil.isNotEmpty(param.getTimeWeek()) ? param.getTimeWeek().toString() : "");
        deliveryStore.setTradeTimeJson(ObjectUtil.isNotEmpty(param.getTradeTimeJson()) ? param.getTradeTimeJson().toString() : "");
        deliveryStoreMapper.insert(deliveryStore);
        return deliveryStore.getStoreId();
    }

    @Override
    @Transactional
    public void edit(Integer storeId, DeliveryStoreAddParam param) {
        DeliveryStore deliveryStore = new DeliveryStore();
        deliveryStore.setStoreId(null);
        BeanUtil.copyProperties(param, deliveryStore);
        deliveryStore.setUpdateTime(System.currentTimeMillis()/1000);
        DeliveryStoreCheckVo deliveryStoreCheckVo = storeCheck(storeId);
        if(deliveryStoreCheckVo.getLocalDeliveryStoreCount() == 1 && deliveryStoreCheckVo.getCurrentLocalDeliveryStoreCount() == 1 &&
                !param.getPickUpType().contains(LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType()) && ObjectUtil.isNotEmpty(deliveryStoreCheckVo.getLocalDeliveryConfig()) &&
                1 == deliveryStoreCheckVo.getLocalDeliveryConfig().getStatus()){
            throw new AdminException("同城配送已开启，至少需保留一个同城配送门店");
        }

        if(deliveryStoreCheckVo.getPickUpStoreCount() == 1 && deliveryStoreCheckVo.getCurrentPickUpStoreCount() == 1 &&
                !param.getPickUpType().contains(LocalDeliveryStoreTypeEnum.STORE.getType()) && ObjectUtil.isNotEmpty(deliveryStoreCheckVo.getPickUpConfig()) &&
                1 == deliveryStoreCheckVo.getPickUpConfig().getStatus()){
            throw new AdminException("门店自提已开启，至少需保留一个自提门店");
        }
        if(StringUtils.isNotEmpty(param.getPickUpType())){
            if(param.getPickUpType().contains(LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType())){
                deliveryStore.setSupportLocalDelivery(1);
            }else{
                deliveryStore.setSupportLocalDelivery(0);
            }
            if(param.getPickUpType().contains(LocalDeliveryStoreTypeEnum.STORE.getType())){
                deliveryStore.setSupportStore(1);
            }else{
                deliveryStore.setSupportStore(0);
            }
        }else{
            deliveryStore.setSupportLocalDelivery(0);
            deliveryStore.setSupportStore(0);
        }

        deliveryStore.setArea(ObjectUtil.isNotEmpty(param.getArea()) ? param.getArea().toString() : "");
        deliveryStore.setTimeWeek(ObjectUtil.isNotEmpty(param.getTimeWeek()) ? param.getTimeWeek().toString() : "");
        deliveryStore.setTradeTimeJson(ObjectUtil.isNotEmpty(param.getTradeTimeJson()) ? param.getTradeTimeJson().toString() : "");
        deliveryStoreMapper.update(deliveryStore, new QueryWrapper<DeliveryStore>().eq("store_id", storeId).eq("site_id", RequestUtils.siteId()));
        DeliveryStore info = deliveryStoreMapper.selectById(storeId);
        ThreadPoolManager.runTask(() -> {
            //批量更新服务商门店信息
            coreDeliveryStoreService.batchDeliveryShopEdit(info);
        });
    }

    @Override
    public void del(Integer storeId) {
        DeliveryStoreCheckVo deliveryStoreCheckVo = storeCheck(storeId);
        if(deliveryStoreCheckVo.getLocalDeliveryStoreCount() == 1 && deliveryStoreCheckVo.getCurrentLocalDeliveryStoreCount() == 1 &&
                 ObjectUtil.isNotEmpty(deliveryStoreCheckVo.getLocalDeliveryConfig()) && 1 == deliveryStoreCheckVo.getLocalDeliveryConfig().getStatus()){
            throw new AdminException("同城配送已开启，至少需保留一个同城配送门店");
        }

        if(deliveryStoreCheckVo.getPickUpStoreCount() == 1 && deliveryStoreCheckVo.getCurrentPickUpStoreCount() == 1 &&
                 ObjectUtil.isNotEmpty(deliveryStoreCheckVo.getPickUpConfig()) && 1 == deliveryStoreCheckVo.getPickUpConfig().getStatus()){
            throw new AdminException("门店自提已开启，至少需保留一个自提门店");
        }
        deliveryStoreMapper.delete(new QueryWrapper<DeliveryStore>().eq("store_id", storeId).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public List<DeliveryStoreVo> getList(DeliveryStoreListSearchParam searchParam) {
        QueryWrapper<DeliveryStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())){
            queryWrapper.and(item -> item.like("store_name", searchParam.getKeyword()).or()
                    .like("mobile", searchParam.getKeyword()).or()
                    .like("full_address", searchParam.getKeyword()));
        }
        if(StringUtils.isNotEmpty(searchParam.getPickUpType())){
            if(searchParam.getPickUpType().equals(LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType())){
                queryWrapper.eq("support_local_delivery", 1);
            }
            if(searchParam.getPickUpType().equals(LocalDeliveryStoreTypeEnum.STORE.getType())){
                queryWrapper.eq("support_store", 1);
            }
        }
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("create_time");
        List<DeliveryStoreVo> list = CollectUtils.convert(deliveryStoreMapper.selectList(queryWrapper), DeliveryStoreVo.class);
        return list;
    }

    @Override
    public void modifyStatus(DeliveryStoreModifyParam param) {
        deliveryStoreMapper.update(null,new UpdateWrapper<DeliveryStore>().eq("store_id", param.getStoreId())
                .eq("site_id", RequestUtils.siteId()).set("status", param.getStatus()));
    }


    /**
     * 配送门店检测
     * @param storeId $id
     * @return array
     */
    private DeliveryStoreCheckVo storeCheck(Integer storeId){
        DeliveryStoreCheckVo deliveryStoreCheckVo = new DeliveryStoreCheckVo();
        Long localDeliveryStoreCount = deliveryStoreMapper.selectCount(new QueryWrapper<DeliveryStore>().eq("site_id", RequestUtils.siteId()).eq("support_local_delivery", 1));
        Long currentLocalDeliveryStoreCount = deliveryStoreMapper.selectCount(new QueryWrapper<DeliveryStore>().eq("store_id", storeId).eq("site_id", RequestUtils.siteId()).eq("support_local_delivery", 1));
        List<DeliveryConfigVo> localDeliveryConfig = coreDeliveryConfigService.getDeliveryConfig(RequestUtils.siteId());

        Long pickUpStoreCount = deliveryStoreMapper.selectCount(new QueryWrapper<DeliveryStore>().eq("site_id", RequestUtils.siteId()).eq("support_store", 1));
        Long currentPickUpStoreCount = deliveryStoreMapper.selectCount(new QueryWrapper<DeliveryStore>().eq("store_id", storeId).eq("site_id", RequestUtils.siteId()).eq("support_store", 1));
        List<DeliveryConfigVo> pickUpConfig = coreDeliveryConfigService.getDeliveryConfig(RequestUtils.siteId());

        deliveryStoreCheckVo.setLocalDeliveryStoreCount(localDeliveryStoreCount.intValue());
        deliveryStoreCheckVo.setCurrentLocalDeliveryStoreCount(currentLocalDeliveryStoreCount.intValue());
        deliveryStoreCheckVo.setLocalDeliveryConfig(localDeliveryConfig.isEmpty() ? null : localDeliveryConfig.stream().filter(info ->LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType().equals(info.getKey())).toList().get(0));

        deliveryStoreCheckVo.setPickUpStoreCount(pickUpStoreCount.intValue());
        deliveryStoreCheckVo.setCurrentPickUpStoreCount(currentPickUpStoreCount.intValue());
        deliveryStoreCheckVo.setPickUpConfig(pickUpConfig.isEmpty() ? null : pickUpConfig.stream().filter(info ->LocalDeliveryStoreTypeEnum.STORE.getType().equals(info.getKey())).toList().get(0));
        return deliveryStoreCheckVo;
    }
}
