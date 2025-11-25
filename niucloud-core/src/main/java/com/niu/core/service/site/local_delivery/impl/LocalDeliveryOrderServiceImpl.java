package com.niu.core.service.site.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrderLog;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.local_delivery.LocalDeliveryMainTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderLogMapper;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderCloseService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderFinishService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderService;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderFinishParam;
import com.niu.core.service.site.local_delivery.ILocalDeliveryOrderService;
import com.niu.core.service.site.local_delivery.param.LocalDeliveryOrderCloseParam;
import com.niu.core.service.site.local_delivery.param.LocalDeliveryOrderSearchParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryOrderInfoVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 同城配送服务接口
 * @author gjl
 * @date 2025/10/10
 */
@Service
public class LocalDeliveryOrderServiceImpl implements ILocalDeliveryOrderService {

    @Resource
    private DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;
    @Resource
    private DeliveryLocalDeliveryOrderLogMapper deliveryLocalDeliveryOrderLogMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private ICoreLocalDeliveryOrderCloseService coreLocalDeliveryOrderCloseService;
    @Resource
    private ICoreLocalDeliveryOrderFinishService coreLocalDeliveryOrderFinishService;
    @Resource
    private ICoreLocalDeliveryOrderService coreLocalDeliveryOrderService;

    @Override
    public PageResult<LocalDeliveryOrderListVo> page(PageParam pageParam, LocalDeliveryOrderSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<DeliveryLocalDeliveryOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id, delivery_service, delivery_no, trade_id, trade_no, trade_type, delivery_money, delivery_start, delivery_start_lng, delivery_start_lat, delivery_end, delivery_end_lng, delivery_end_lat, delivery_distance, status, create_time, remark, body");
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.like(StringUtils.isNotEmpty(searchParam.getDeliveryNo()),"delivery_no", searchParam.getDeliveryNo());
        queryWrapper.like(StringUtils.isNotEmpty(searchParam.getTradeNo()),"trade_no", searchParam.getTradeNo());
        queryWrapper.eq(StringUtils.isNotEmpty(searchParam.getTradeType()),"trade_type", searchParam.getTradeType());
        queryWrapper.eq(StringUtils.isNotEmpty(searchParam.getStatus()),"status", searchParam.getStatus());
        if(ObjectUtil.isNotEmpty(searchParam.getCreateTime())){
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        }
        queryWrapper.orderByDesc("create_time");
        IPage<DeliveryLocalDeliveryOrder> iPage = deliveryLocalDeliveryOrderMapper.selectPage(new Page<>(page, limit),queryWrapper);
        List<LocalDeliveryOrderListVo> list = new LinkedList<>();
        for (DeliveryLocalDeliveryOrder item : iPage.getRecords()) {
            LocalDeliveryOrderListVo vo = new LocalDeliveryOrderListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public LocalDeliveryOrderInfoVo getInfo(Integer id) {
        DeliveryLocalDeliveryOrder deliveryLocalDeliveryOrder = deliveryLocalDeliveryOrderMapper.selectById(id);
        LocalDeliveryOrderInfoVo result = new LocalDeliveryOrderInfoVo();
        BeanUtils.copyProperties(deliveryLocalDeliveryOrder, result);
        if(ObjectUtil.isNotEmpty(deliveryLocalDeliveryOrder)){
            QueryWrapper<DeliveryLocalDeliveryOrderLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", id);
            List<DeliveryLocalDeliveryOrderLog> orderLogList = deliveryLocalDeliveryOrderLogMapper.selectList(queryWrapper);
            if(!orderLogList.isEmpty()){
                List<LocalDeliveryOrderInfoVo.orderLogInfo> list = new LinkedList<>();
                for (DeliveryLocalDeliveryOrderLog item : orderLogList) {
                    LocalDeliveryOrderInfoVo.orderLogInfo vo = new LocalDeliveryOrderInfoVo.orderLogInfo();
                    BeanUtils.copyProperties(item, vo);
                    list.add(vo);
                }
                result.setOrderLog(list);
            }
        }
        return result;
    }

    @Override
    public void closeOrder(Integer id, LocalDeliveryOrderCloseParam param) {
        param.setMainId(RequestUtils.uid());
        param.setMainType(LocalDeliveryMainTypeEnum.STORE.getType());
        SysUser sysUser = sysUserMapper.selectById(RequestUtils.uid());
        param.setMainName(sysUser.getUsername());
        param.setId( id);

        com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCloseParam paramClose = new com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCloseParam();
        BeanUtils.copyProperties(param, paramClose);
        coreLocalDeliveryOrderCloseService.closeOrder( paramClose);
    }

    @Override
    public void finishOrder(Integer id) {
        LocalDeliveryOrderFinishParam param = new LocalDeliveryOrderFinishParam();
        param.setId( id);
        param.setMainId(RequestUtils.uid());
        param.setMainType(LocalDeliveryMainTypeEnum.STORE.getType());
        SysUser sysUser = sysUserMapper.selectById(RequestUtils.uid());
        param.setMainName(sysUser.getUsername());
        coreLocalDeliveryOrderFinishService.finishOrder(param );
    }

    @Override
    public void syncOrder(Integer id) {
        coreLocalDeliveryOrderService.syncOrder( id);
    }
}
