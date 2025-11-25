package com.niu.mall.service.admin.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.entity.order.MallOrderDelivery;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.mall.mapper.order.MallOrderDeliveryMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.service.admin.order.IMallOrderDeliveryService;
import com.niu.mall.service.admin.order.vo.MallOrderDeliveryInfoVo;
import com.niu.mall.service.admin.order.vo.MallOrderDeliveryListVo;
import com.niu.mall.service.core.order.ICoreOrderDeliveryService;
import com.niu.mall.service.core.order.ICoreOrderService;
import com.niu.mall.service.core.order.param.DeliverySearchParam;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 订单发货实现
 */
@Service
public class MallOrderDeliveryServiceImpl implements IMallOrderDeliveryService {
        
    @Resource
    MallOrderDeliveryMapper mallOrderDeliveryMapper;

    @Resource
    DeliveryCompanyMapper deliveryCompanyMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    ICoreOrderDeliveryService coreOrderDeliveryService;

    @Resource
    ICoreOrderService coreOrderService;


    @Override
    public void delivery(OrderDeliveryParam orderDeliveryParam) {
        orderDeliveryParam.setSiteId(RequestUtils.siteId());
        coreOrderDeliveryService.delivery(orderDeliveryParam);
    }

    @Override
    public List<MallOrderDeliveryListVo> getDeliveryPackageList(Integer orderId) {
        List<MallOrderDelivery> list = mallOrderDeliveryMapper.selectList(new QueryWrapper<MallOrderDelivery>().eq("order_id", orderId).eq("site_id", RequestUtils.siteId()));

        List<MallOrderDeliveryListVo> voList = new LinkedList<>();
        for (MallOrderDelivery mallOrderDelivery : list) {
            MallOrderDeliveryListVo vo = new MallOrderDeliveryListVo();
            BeanUtil.copyProperties(mallOrderDelivery, vo);
            vo.setCompany(deliveryCompanyMapper.selectById(mallOrderDelivery.getExpressCompanyId()));
            List<MallOrderDeliveryListVo.OrderGoods> orderGoods = CollectUtils.convert(mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("delivery_id", mallOrderDelivery.getId())), MallOrderDeliveryListVo.OrderGoods.class);
            vo.setOrderGoods(orderGoods);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public MallOrderDeliveryInfoVo getDeliveryPackage(Integer id, String mobile) {

        MallOrderDelivery mallOrderDelivery = mallOrderDeliveryMapper.selectById(id);
        if(ObjectUtil.isEmpty(mallOrderDelivery)) throw new CommonException("物流信息不存在");
        MallOrderDeliveryInfoVo vo = new MallOrderDeliveryInfoVo();
        BeanUtil.copyProperties(mallOrderDelivery, vo);
        vo.setCompany(deliveryCompanyMapper.selectById(mallOrderDelivery.getExpressCompanyId()));
        List<MallOrderDeliveryListVo.OrderGoods> orderGoods = CollectUtils.convert(mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("delivery_id", mallOrderDelivery.getId())), MallOrderDeliveryListVo.OrderGoods.class);
        vo.setOrderGoods(orderGoods);
        if(vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType()) && !vo.getSubDeliveryType().equals(OrderDeliveryTypeEnum.NONE_EXPRESS.getType())){
            vo.setMobile(mobile);
            //查询物流信息
            DeliverySearchParam deliverySearchParam = new DeliverySearchParam();
            deliverySearchParam.setExpressNo(vo.getCompany().getExpressNo());
            deliverySearchParam.setLogisticNo(vo.getExpressNumber());
            deliverySearchParam.setMobile(mobile);
            vo.setTraces(coreOrderService.deliverySearch(deliverySearchParam));
        }
        return vo;

    }
}
