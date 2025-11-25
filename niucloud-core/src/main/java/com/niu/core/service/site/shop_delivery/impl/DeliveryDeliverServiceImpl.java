package com.niu.core.service.site.shop_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.site.shop_delivery.IDeliveryDeliverService;
import com.niu.core.entity.delivery.DeliveryDeliver;
import com.niu.core.mapper.delivery.DeliveryDeliverMapper;
import com.niu.core.service.site.shop_delivery.param.DeliveryDeliverParam;
import com.niu.core.service.site.shop_delivery.param.DeliveryDeliverSearchParam;
import com.niu.core.service.site.shop_delivery.vo.DeliveryDeliverInfoVo;
import com.niu.core.service.site.shop_delivery.vo.DeliveryDeliverListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;


/**
 * 配送员实现
 */
@Service
public class DeliveryDeliverServiceImpl implements IDeliveryDeliverService {
        
    @Resource
    DeliveryDeliverMapper deliveryDeliverMapper;

    /**
     * 配送员列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<DeliveryDeliverListVo>
     */
    @Override
    public PageResult<DeliveryDeliverListVo> list(PageParam pageParam, DeliveryDeliverSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<DeliveryDeliver> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getDeliverMobile())) queryWrapper.like("deliver_mobile", searchParam.getDeliverMobile());
        if (ObjectUtil.isNotEmpty(searchParam.getDeliverName())) queryWrapper.like("deliver_name", searchParam.getDeliverName());
        queryWrapper.orderByDesc("deliver_id");

        IPage<DeliveryDeliver> iPage = deliveryDeliverMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<DeliveryDeliverListVo> list = new LinkedList<>();
        for (DeliveryDeliver item : iPage.getRecords()) {
            DeliveryDeliverListVo vo = new DeliveryDeliverListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<DeliveryDeliverListVo> getList(DeliveryDeliverSearchParam searchParam) {
        QueryWrapper<DeliveryDeliver> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getDeliverMobile())) queryWrapper.like("deliver_mobile", searchParam.getDeliverMobile());
        if (ObjectUtil.isNotEmpty(searchParam.getDeliverName())) queryWrapper.like("deliver_name", searchParam.getDeliverName());
        queryWrapper.orderByDesc("deliver_id");

        List<DeliveryDeliver> query = deliveryDeliverMapper.selectList(queryWrapper);
        List<DeliveryDeliverListVo> list = new LinkedList<>();
        for (DeliveryDeliver item : query) {
            DeliveryDeliverListVo vo = new DeliveryDeliverListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 配送员详情
     * @param id 主键
     * @return ShopDeliveryDeliverInfoVo
     */
    @Override
    public DeliveryDeliverInfoVo info(Integer id) {
        DeliveryDeliver model = deliveryDeliverMapper.selectOne(
                new QueryWrapper<DeliveryDeliver>()
                    .eq("deliver_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "配送员不存在");

        DeliveryDeliverInfoVo vo = new DeliveryDeliverInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 配送员添加
     * @param addParam 参数
     */
    @Override
    public void add(DeliveryDeliverParam addParam) {
        DeliveryDeliver model = new DeliveryDeliver();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        deliveryDeliverMapper.insert(model);
    }

    /**
     * 配送员编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, DeliveryDeliverParam editParam) {
        DeliveryDeliver model = deliveryDeliverMapper.selectOne(
                new QueryWrapper<DeliveryDeliver>()
                        .eq("deliver_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "配送员不存在");
        BeanUtils.copyProperties(editParam, model);
        model.setModifyTime(System.currentTimeMillis() / 1000);
        deliveryDeliverMapper.updateById(model);
    }

    /**
     * 配送员删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        deliveryDeliverMapper.delete(new QueryWrapper<DeliveryDeliver>().eq("deliver_id", id).eq("site_id", RequestUtils.siteId()));
    }

}
