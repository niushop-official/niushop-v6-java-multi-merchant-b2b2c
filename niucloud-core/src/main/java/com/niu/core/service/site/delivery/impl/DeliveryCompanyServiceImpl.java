package com.niu.core.service.site.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.service.site.delivery.IDeliveryCompanyService;
import com.niu.core.service.site.delivery.param.DeliveryCompanySearchParam;
import com.niu.core.service.site.delivery.vo.DeliveryCompanyInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryCompanyListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;


/**
 * 物流公司实现
 */
@Service
public class DeliveryCompanyServiceImpl implements IDeliveryCompanyService {
        
    @Resource
    DeliveryCompanyMapper deliveryCompanyMapper;

    /**
     * 物流公司列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryCompanyListVo>
     */
    @Override
    public List<DeliveryCompanyListVo> list(DeliveryCompanySearchParam searchParam) {
        QueryWrapper<DeliveryCompany> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getCompanyName())) queryWrapper.like("company_name", searchParam.getCompanyName());
        if (ObjectUtil.isNotEmpty(searchParam.getElectronicSheetSwitch())) queryWrapper.eq("electronic_sheet_switch", searchParam.getElectronicSheetSwitch());
        queryWrapper.orderByDesc("company_id");

        List<DeliveryCompany> record = deliveryCompanyMapper.selectList(queryWrapper);
        List<DeliveryCompanyListVo> list = new LinkedList<>();
        for (DeliveryCompany item : record) {
            DeliveryCompanyListVo vo = new DeliveryCompanyListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 物流公司详情
     * @param id 主键
     * @return ShopDeliveryCompanyInfoVo
     */
    @Override
    public DeliveryCompanyInfoVo info(Integer id) {
        DeliveryCompany model = deliveryCompanyMapper.selectOne(
                new QueryWrapper<DeliveryCompany>()
                    .eq("company_id", id));

        Assert.notNull(model, "物流公司不存在");

        DeliveryCompanyInfoVo vo = new DeliveryCompanyInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

}
