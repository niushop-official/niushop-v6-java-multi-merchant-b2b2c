package com.niu.core.service.admin.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.service.admin.delivery.param.DeliveryCompanyParam;
import com.niu.core.service.admin.delivery.param.DeliveryCompanySearchParam;
import com.niu.core.service.admin.delivery.IDeliveryCompanyService;
import com.niu.core.service.admin.delivery.vo.DeliveryCompanyInfoVo;
import com.niu.core.service.admin.delivery.vo.DeliveryCompanyListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 物流公司实现
 */
@Service
public class DeliveryCompanyServiceImpl extends ServiceImpl<DeliveryCompanyMapper, DeliveryCompany> implements IDeliveryCompanyService {
        
    @Resource
    DeliveryCompanyMapper deliveryCompanyMapper;

    /**
     * 物流公司分页列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryCompanyListVo>
     */
    @Override
    public PageResult<DeliveryCompanyListVo> page(PageParam pageParam, DeliveryCompanySearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<DeliveryCompany> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getCompanyName())) queryWrapper.like("company_name", searchParam.getCompanyName());
        if (ObjectUtil.isNotEmpty(searchParam.getElectronicSheetSwitch())) queryWrapper.eq("electronic_sheet_switch", searchParam.getElectronicSheetSwitch());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("company_id");

        IPage<DeliveryCompany> iPage = deliveryCompanyMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<DeliveryCompanyListVo> list = new LinkedList<>();
        for (DeliveryCompany item : iPage.getRecords()) {
            DeliveryCompanyListVo vo = new DeliveryCompanyListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

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
        queryWrapper.eq("site_id", RequestUtils.siteId());
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
                    .eq("company_id", id).last("limit 1"));

        Assert.notNull(model, "物流公司不存在");

        DeliveryCompanyInfoVo vo = new DeliveryCompanyInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 物流公司添加
     * @param addParam 参数
     */
    @Override
    public void add(DeliveryCompanyParam addParam) {
        DeliveryCompany model = new DeliveryCompany();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setExpType(addParam.getExpType().toString());
        model.setPrintStyle(addParam.getPrintStyle().toString());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        deliveryCompanyMapper.insert(model);
    }

    /**
     * 物流公司编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, DeliveryCompanyParam editParam) {
        DeliveryCompany model = deliveryCompanyMapper.selectOne(
                new QueryWrapper<DeliveryCompany>()
                        .eq("company_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "物流公司不存在");

        BeanUtils.copyProperties(editParam, model);
        model.setExpType(editParam.getExpType().toString());
        model.setPrintStyle(editParam.getPrintStyle().toString());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        deliveryCompanyMapper.updateById(model);
    }

    /**
     * 物流公司删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        deliveryCompanyMapper.delete(new QueryWrapper<DeliveryCompany>().eq("company_id", id).eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 批量添加物流公司
     *
     * @param list
     */
    @Override
    public void insertAll(List<DeliveryCompany> list) {
        super.saveOrUpdateBatch(list);
    }

}
