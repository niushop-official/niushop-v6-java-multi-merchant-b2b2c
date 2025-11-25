package com.niu.core.service.site.shop_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.loader.local_delivery.param.CloseShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.FinishShopDeliveryOrderParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.delivery.*;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.local_delivery.LocalDeliveryMainTypeEnum;
import com.niu.core.mapper.delivery.DeliveryDeliverMapper;
import com.niu.core.mapper.delivery.DeliveryShopDeliveryOrderLogMapper;
import com.niu.core.mapper.delivery.DeliveryShopDeliveryOrderMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderCloseService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderFinishService;
import com.niu.core.service.site.shop_delivery.param.ShopDeliveryOrderCloseParam;
import com.niu.core.service.site.shop_delivery.param.ShopDeliveryOrderSearchParam;
import com.niu.core.service.site.shop_delivery.vo.ShopDeliveryOrderInfoVo;
import com.niu.core.service.site.shop_delivery.vo.ShopDeliveryOrderListVo;
import com.niu.core.service.site.shop_delivery.IShopDeliveryOrderService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;


/**
 * 同城配送服务接口
 * @author gjl
 * @date 2025/10/10
 */
@Service
public class ShopDeliveryOrderServiceImpl implements IShopDeliveryOrderService {

    @Resource
    private DeliveryShopDeliveryOrderMapper deliveryShopDeliveryOrderMapper;
    @Resource
    private DeliveryShopDeliveryOrderLogMapper deliveryShopDeliveryOrderLogMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private DeliveryDeliverMapper deliveryDeliverMapper;
    @Resource
    private ICoreShopDeliveryOrderCloseService coreShopDeliveryOrderCloseService;
    @Resource
    private ICoreShopDeliveryOrderFinishService coreShopDeliveryOrderFinishService;

    @Override
    public PageResult<ShopDeliveryOrderListVo> page(PageParam pageParam, ShopDeliveryOrderSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<DeliveryShopDeliveryOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id, delivery_no, out_delivery_no, trade_id, trade_no, trade_type, delivery_money, delivery_start, delivery_start_lng, delivery_start_lat, delivery_end, delivery_end_lng, delivery_end_lat, delivery_distance, status, create_time, remark, body");
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.like(StringUtils.isNotEmpty(searchParam.getDeliveryNo()),"delivery_no", searchParam.getDeliveryNo());
        queryWrapper.like(StringUtils.isNotEmpty(searchParam.getTradeNo()),"trade_no", searchParam.getTradeNo());
        queryWrapper.eq(StringUtils.isNotEmpty(searchParam.getTradeType()),"trade_type", searchParam.getTradeType());
        queryWrapper.eq(StringUtils.isNotEmpty(searchParam.getStatus()),"status", searchParam.getStatus());
        if(ObjectUtil.isNotEmpty(searchParam.getCreateTime())){
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        }
        queryWrapper.orderByDesc("create_time");
        IPage<DeliveryShopDeliveryOrder> iPage = deliveryShopDeliveryOrderMapper.selectPage(new Page<>(page, limit),queryWrapper);
        List<ShopDeliveryOrderListVo> list = new LinkedList<>();
        for (DeliveryShopDeliveryOrder item : iPage.getRecords()) {
            ShopDeliveryOrderListVo vo = new ShopDeliveryOrderListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public ShopDeliveryOrderInfoVo getInfo(Integer id) {
        DeliveryShopDeliveryOrder deliveryLocalDeliveryOrder = deliveryShopDeliveryOrderMapper.selectById(id);
        ShopDeliveryOrderInfoVo result = new ShopDeliveryOrderInfoVo();
        BeanUtils.copyProperties(deliveryLocalDeliveryOrder, result);
        if(ObjectUtil.isNotEmpty(deliveryLocalDeliveryOrder)){
            QueryWrapper<DeliveryShopDeliveryOrderLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", id);
            List<DeliveryShopDeliveryOrderLog> orderLogList = deliveryShopDeliveryOrderLogMapper.selectList(queryWrapper);
            if(!orderLogList.isEmpty()){
                List<ShopDeliveryOrderInfoVo.orderLogInfo> list = new LinkedList<>();
                for (DeliveryShopDeliveryOrderLog item : orderLogList) {
                    ShopDeliveryOrderInfoVo.orderLogInfo vo = new ShopDeliveryOrderInfoVo.orderLogInfo();
                    BeanUtils.copyProperties(item, vo);
                    list.add(vo);
                }
                result.setOrderLog(list);
            }

            //查询配送员
            DeliveryDeliver deliver = deliveryDeliverMapper.selectById(deliveryLocalDeliveryOrder.getDeliverId());
            ShopDeliveryOrderInfoVo.DeliverInfo vo = new ShopDeliveryOrderInfoVo.DeliverInfo();
            BeanUtils.copyProperties(deliver, vo);
            result.setDeliver(vo);
        }
        return result;
    }

    @Override
    public void closeOrder(Integer id, ShopDeliveryOrderCloseParam param) {
        param.setMainId(RequestUtils.uid());
        param.setMainType(LocalDeliveryMainTypeEnum.STORE.getType());
        SysUser sysUser = sysUserMapper.selectById(RequestUtils.uid());
        param.setMainName(sysUser.getUsername());
        param.setId( id);

        CloseShopDeliveryOrderParam paramClose = new CloseShopDeliveryOrderParam();
        BeanUtils.copyProperties(param, paramClose);
        coreShopDeliveryOrderCloseService.closeOrder( paramClose);
    }

    @Override
    public void finishOrder(Integer id) {
        FinishShopDeliveryOrderParam param = new FinishShopDeliveryOrderParam();
        param.setId( id);
        param.setMainId(RequestUtils.uid());
        param.setMainType(LocalDeliveryMainTypeEnum.STORE.getType());
        SysUser sysUser = sysUserMapper.selectById(RequestUtils.uid());
        param.setMainName(sysUser.getUsername());
        coreShopDeliveryOrderFinishService.finishOrder(param );
    }

}
