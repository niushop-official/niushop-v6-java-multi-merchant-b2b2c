package com.niu.mall.service.site.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderCreateService;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCreateParam;
import com.niu.core.service.core.verify.ICoreVerifyService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDelivery;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderDeliveryMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.vo.MallOrderGoodsDeliveryVo;
import com.niu.mall.service.site.order.IMallOrderDeliveryService;
import com.niu.mall.service.site.order.param.OrderPickUpParam;
import com.niu.mall.service.site.order.param.lOrderDeliveryFeeParam;
import com.niu.mall.service.site.order.vo.MallOrderDeliveryInfoVo;
import com.niu.mall.service.site.order.vo.MallOrderDeliveryListVo;
import com.niu.mall.service.core.order.ICoreOrderDeliveryService;
import com.niu.mall.service.core.order.ICoreOrderService;
import com.niu.mall.service.core.order.param.DeliverySearchParam;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;

import static com.niu.core.common.utils.CommonUtils.createNo;


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

    @Resource
    ICoreLocalDeliveryOrderCreateService coreLocalDeliveryOrderCreateService;
    @Resource
    private MallOrderMapper mallOrderMapper;
    @Resource
    private ICoreVerifyService coreVerifyService;


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
        DeliveryCompany company = deliveryCompanyMapper.selectById(mallOrderDelivery.getExpressCompanyId());
        if(ObjectUtil.isNotEmpty(company)){
            MallOrderDeliveryInfoVo.DeliveryCompanyInfo companyInfo = new MallOrderDeliveryInfoVo.DeliveryCompanyInfo();
            BeanUtils.copyProperties(company, companyInfo);
            vo.setCompany(companyInfo);
        }
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

    @Override
    public BigDecimal getDeliveryFee(lOrderDeliveryFeeParam params) {
        if (!(params.getDeliveryType().equals("local_delivery") && params.getDeliveryWay().equals("third_party_delivery") )) {
            return BigDecimal.ZERO;
        }
        //同城配送订单创建
        MallOrder orderData = mallOrderMapper.selectById(params.getOrderId());
        if(ObjectUtil.isEmpty(orderData)) throw new CommonException("订单信息不存在");
        List<MallOrderGoods> orderGoodsList = new ArrayList<>();
        List<LocalDeliveryOrderCreateParam.OrderGoodsInfo> orderGoodsListParam = new ArrayList<>();
        if(params.getOrderGoodsIds() != null && params.getOrderGoodsIds().length > 0){
            orderGoodsList = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().in("order_goods_id", Arrays.asList(params.getOrderGoodsIds())));
            for (MallOrderGoods orderGoods : orderGoodsList) {
                LocalDeliveryOrderCreateParam.OrderGoodsInfo goodsInfo = new LocalDeliveryOrderCreateParam.OrderGoodsInfo();
                BeanUtils.copyProperties(orderGoods, goodsInfo);
                orderGoodsListParam.add(goodsInfo);
            }
        }


        String localDeliveryNo = createNo();
        LocalDeliveryOrderCreateParam param = new LocalDeliveryOrderCreateParam();
        LocalDeliveryOrderCreateParam.OrderDataInfo orderInfo = new LocalDeliveryOrderCreateParam.OrderDataInfo();
        BeanUtils.copyProperties(orderData, orderInfo);
        orderInfo.setTakerCityId(orderData.getTakerCity());
        orderInfo.setTakerDistrictId(orderData.getTakerDistrict());
        orderInfo.setTakerProvinceId(orderData.getTakerProvince());
        param.setDeliveryNo(localDeliveryNo);
        param.setBody(orderData.getBody());
        param.setLocalDeliveryNo(localDeliveryNo);
        param.setLocalDeliveryType(params.getLocalDeliveryType());
        param.setOrderData(orderInfo);
        param.setGoodsWeight(param.getGoodsWeight());
        param.setOrderGoodsData(orderGoodsListParam);
        param.setStoreId(params.getStoreId());
        return coreLocalDeliveryOrderCreateService.getDeliveryFee( param);
    }

    @Override
    public void orderPickUp(OrderPickUpParam param) {
        //查询订单
        MallOrder order = mallOrderMapper.selectById(param.getOrderId());
        Assert.notNull(order, "订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString())){
            throw new CommonException("订单不是待提货状态");
        }
        Map<String, Object> data = BeanUtil.beanToMap(param);
        coreVerifyService.create(order.getSiteId(), order.getMemberId(), "shopPickUpOrder", data);
    }
}
