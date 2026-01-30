package com.niu.core.service.site.site.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.site.site.param.SiteAddressParam;
import com.niu.core.service.site.site.param.SiteAddressSearchParam;
import com.niu.core.service.site.site.ISiteAddressService;
import com.niu.core.service.site.site.vo.SiteAddressInfoVo;
import com.niu.core.service.site.site.vo.SiteAddressListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;

import java.util.LinkedList;
import java.util.List;


/**
 * 商家地址库实现
 */
@Service
public class SiteAddressServiceImpl implements ISiteAddressService {
        
    @Resource
    SiteAddressMapper siteAddressMapper;

    /**
     * 商家地址库列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopAddressListVo>
     */
    @Override
    public PageResult<SiteAddressListVo> page(PageParam pageParam, SiteAddressSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<SiteAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getMobile())) queryWrapper.like("mobile", searchParam.getMobile());
        if (ObjectUtil.isNotEmpty(searchParam.getFullAddress())) queryWrapper.like("full_address", searchParam.getFullAddress());
        queryWrapper.orderByDesc("id");

        IPage<SiteAddress> iPage = siteAddressMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SiteAddressListVo> list = new LinkedList<>();
        for (SiteAddress item : iPage.getRecords()) {
            SiteAddressListVo vo = new SiteAddressListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<SiteAddressListVo> list(SiteAddressSearchParam searchParam) {
        QueryWrapper<SiteAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("is_refund_address", 1);
        queryWrapper.orderByDesc("id");
        List<SiteAddressListVo> list = CollectUtils.convert(siteAddressMapper.selectList(queryWrapper),SiteAddressListVo.class);
        return list;
    }

    /**
     * 商家地址库详情
     *
     * @param id 主键
     * @return ShopAddressInfoVo
     */
    @Override
    public SiteAddressInfoVo info(Integer id) {
        SiteAddress model = siteAddressMapper.selectOne(
                new QueryWrapper<SiteAddress>()
                    .eq("id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "地址不存在");

        SiteAddressInfoVo vo = new SiteAddressInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 获取默认发货地址
     *
     * @return ShopAddressInfoVo
     */
    @Override
    public SiteAddressInfoVo defaultDelivery() {
        SiteAddress model = siteAddressMapper.selectOne(
                new QueryWrapper<SiteAddress>()
                        .eq("is_delivery_address", 1)
                        .eq("is_default_delivery", 1)
                        .eq("site_id", RequestUtils.siteId()));

        if (model == null) return null;

        SiteAddressInfoVo vo = new SiteAddressInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 商家地址库添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(SiteAddressParam addParam) {
        // 处理布尔值转整数（确保数据正确）
        normalizeBooleanFields(addParam);
        
        SiteAddress model = new SiteAddress();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        
        // 手动设置布尔字段，确保BeanUtils.copyProperties无法复制Object类型时也能正确设置
        model.setIsDeliveryAddress((Integer) addParam.getIsDeliveryAddress());
        model.setIsRefundAddress((Integer) addParam.getIsRefundAddress());
        model.setIsDefaultDelivery((Integer) addParam.getIsDefaultDelivery());
        model.setIsDefaultRefund((Integer) addParam.getIsDefaultRefund());

        Integer isDefaultDelivery = (Integer) addParam.getIsDefaultDelivery();
        if (isDefaultDelivery != null && isDefaultDelivery.equals(1)) {
            SiteAddress updateModel = new SiteAddress();
            updateModel.setIsDefaultDelivery(0);
            siteAddressMapper.update(updateModel, new QueryWrapper<SiteAddress>().eq("site_id", RequestUtils.siteId()).ne("is_default_delivery", 0));
        }
        Integer isDefaultRefund = (Integer) addParam.getIsDefaultRefund();
        if (isDefaultRefund != null && isDefaultRefund.equals(1)) {
            SiteAddress updateModel = new SiteAddress();
            updateModel.setIsDefaultRefund(0);
            siteAddressMapper.update(updateModel, new QueryWrapper<SiteAddress>().eq("site_id", RequestUtils.siteId()).ne("is_default_refund", 0));
        }

        siteAddressMapper.insert(model);
    }

    /**
     * 商家地址库编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, SiteAddressParam editParam) {
        // 处理布尔值转整数（确保数据正确）
        normalizeBooleanFields(editParam);
        
        SiteAddress model = siteAddressMapper.selectOne(
                new QueryWrapper<SiteAddress>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "地址不存在");
        BeanUtils.copyProperties(editParam, model);
        model.setIsDeliveryAddress((Integer) editParam.getIsDeliveryAddress());
        model.setIsRefundAddress((Integer) editParam.getIsRefundAddress());
        model.setIsDefaultDelivery((Integer) editParam.getIsDefaultDelivery());
        model.setIsDefaultRefund((Integer) editParam.getIsDefaultRefund());

        Integer isDefaultDelivery = (Integer) editParam.getIsDefaultDelivery();
        if (isDefaultDelivery != null && isDefaultDelivery.equals(1)) {
            SiteAddress updateModel = new SiteAddress();
            updateModel.setIsDefaultDelivery(0);
            siteAddressMapper.update(updateModel, new QueryWrapper<SiteAddress>().eq("site_id", RequestUtils.siteId()).ne("is_default_delivery", 0));
        }
        Integer isDefaultRefund = (Integer) editParam.getIsDefaultRefund();
        if (isDefaultRefund != null && isDefaultRefund.equals(1)) {
            SiteAddress updateModel = new SiteAddress();
            updateModel.setIsDefaultRefund(0);
            siteAddressMapper.update(updateModel, new QueryWrapper<SiteAddress>().eq("site_id", RequestUtils.siteId()).ne("is_default_refund", 0));
        }
        siteAddressMapper.updateById(model);
    }

    /**
     * 商家地址库删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        siteAddressMapper.delete(new QueryWrapper<SiteAddress>().eq("id", id).eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 退款地址列表
     *
     * @return
     */
    @Override
    public List<SiteAddressListVo> refundAddress() {
        return CollectUtils.convert(siteAddressMapper.selectList(new QueryWrapper<SiteAddress>().eq("is_refund_address", 1).eq("site_id", RequestUtils.siteId())), SiteAddressListVo.class);
    }

    /**
     * 规范化布尔字段：处理布尔值转整数，确保Integer字段不为null，默认值为0
     */
    private void normalizeBooleanFields(SiteAddressParam param) {
        // 处理 isDeliveryAddress
        if (param.getIsDeliveryAddress() == null) {
            param.setIsDeliveryAddress(0);
        } else if (param.getIsDeliveryAddress() instanceof Boolean) {
            param.setIsDeliveryAddress(((Boolean) param.getIsDeliveryAddress()) ? 1 : 0);
        } else if (param.getIsDeliveryAddress() instanceof Number) {
            param.setIsDeliveryAddress(((Number) param.getIsDeliveryAddress()).intValue());
        } else {
            param.setIsDeliveryAddress(0);
        }
        
        // 处理 isRefundAddress
        if (param.getIsRefundAddress() == null) {
            param.setIsRefundAddress(0);
        } else if (param.getIsRefundAddress() instanceof Boolean) {
            param.setIsRefundAddress(((Boolean) param.getIsRefundAddress()) ? 1 : 0);
        } else if (param.getIsRefundAddress() instanceof Number) {
            param.setIsRefundAddress(((Number) param.getIsRefundAddress()).intValue());
        } else {
            param.setIsRefundAddress(0);
        }
        
        // 处理 isDefaultDelivery
        if (param.getIsDefaultDelivery() == null) {
            param.setIsDefaultDelivery(0);
        } else if (param.getIsDefaultDelivery() instanceof Boolean) {
            param.setIsDefaultDelivery(((Boolean) param.getIsDefaultDelivery()) ? 1 : 0);
        } else if (param.getIsDefaultDelivery() instanceof Number) {
            param.setIsDefaultDelivery(((Number) param.getIsDefaultDelivery()).intValue());
        } else {
            param.setIsDefaultDelivery(0);
        }
        
        // 处理 isDefaultRefund
        if (param.getIsDefaultRefund() == null) {
            param.setIsDefaultRefund(0);
        } else if (param.getIsDefaultRefund() instanceof Boolean) {
            param.setIsDefaultRefund(((Boolean) param.getIsDefaultRefund()) ? 1 : 0);
        } else if (param.getIsDefaultRefund() instanceof Number) {
            param.setIsDefaultRefund(((Number) param.getIsDefaultRefund()).intValue());
        } else {
            param.setIsDefaultRefund(0);
        }
    }

}
