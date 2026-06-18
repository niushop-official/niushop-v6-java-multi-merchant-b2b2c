package com.niu.core.service.site.delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.consts.ShopDeliveryElectronicSheetConst;
import com.niu.core.entity.delivery.DeliveryElectronicSheet;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.enums.delivery.ElectronicSheetPayTypeEnum;
import com.niu.core.enums.delivery.SupportTradeEnum;
import com.niu.core.mapper.delivery.DeliveryElectronicSheetMapper;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.service.core.delivery.vo.ElectronicSheetConfigValueVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.site.delivery.IDeliveryCompanyService;
import com.niu.core.service.site.delivery.IDeliveryElectronicSheetService;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetListsParam;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetPagesParam;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetParam;
import com.niu.core.service.site.delivery.vo.DeliveryCompanyInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryElectronicSheetInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryElectronicSheetListVo;
import com.niu.core.service.site.delivery.vo.DeliveryElectronicSheetPageVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 电子面单实现
 */
@Service
public class DeliveryElectronicSheetServiceImpl implements IDeliveryElectronicSheetService {

    @Resource
    DeliveryElectronicSheetMapper deliveryElectronicSheetMapper;

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    DeliveryCompanyMapper deliveryCompanyMapper;

    @Resource
    IDeliveryCompanyService deliveryCompanyService;

    @Override
    public List<DeliveryElectronicSheetListVo> lists(PageParam pageParam, DeliveryElectronicSheetListsParam listsParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        LambdaQueryWrapper<DeliveryElectronicSheet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeliveryElectronicSheet::getSiteId, RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(listsParam.getTemplateName())) {
            queryWrapper.like(DeliveryElectronicSheet::getTemplateName, listsParam.getTemplateName());
        }

        if (ObjectUtil.isNotEmpty(listsParam.getExpressCompanyId())) {
            queryWrapper.eq(DeliveryElectronicSheet::getExpressCompanyId, listsParam.getExpressCompanyId());
        }

        if (ObjectUtil.isNotEmpty(listsParam.getIsNotice())) {
            queryWrapper.eq(DeliveryElectronicSheet::getIsNotice, listsParam.getIsNotice());
        }

        if (ObjectUtil.isNotEmpty(listsParam.getStatus())) {
            queryWrapper.eq(DeliveryElectronicSheet::getStatus, listsParam.getStatus());
        }

        queryWrapper.orderByDesc(DeliveryElectronicSheet::getId);

        return deliveryElectronicSheetMapper.selectPage(new Page<>(page, limit), queryWrapper)
                .getRecords()
                .stream()
                .map(item -> BeanUtil.toBean(item, DeliveryElectronicSheetListVo.class))
                .collect(Collectors.toList());
    }

    /**
     * 电子面单列表
     *
     * @param pageParam  分页参数
     * @param pagesParam 搜索参数
     * @return PageResult<ShopDeliveryElectronicSheetListVo>
     */
    @Override
    public PageResult<DeliveryElectronicSheetPageVo> pages(PageParam pageParam, DeliveryElectronicSheetPagesParam pagesParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        LambdaQueryWrapper<DeliveryElectronicSheet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeliveryElectronicSheet::getSiteId, RequestUtils.siteId())
                .orderByDesc(DeliveryElectronicSheet::getIsDefault)
                .orderByDesc(DeliveryElectronicSheet::getId);

        if (pagesParam.getExpressCompanyId() != null && pagesParam.getExpressCompanyId() > 0) {
            queryWrapper.eq(DeliveryElectronicSheet::getExpressCompanyId, pagesParam.getExpressCompanyId());
        }

        if (ObjectUtil.isNotEmpty(pagesParam.getTemplateName())) {
            queryWrapper.like(DeliveryElectronicSheet::getTemplateName, pagesParam.getTemplateName());
        }
        if (ObjectUtil.isNotEmpty(pagesParam.getSupportTrade())) {
            queryWrapper.eq(DeliveryElectronicSheet::getSupportTrade, pagesParam.getSupportTrade());
        }
        IPage<DeliveryElectronicSheet> iPage = deliveryElectronicSheetMapper.selectPage(new Page<>(page, limit), queryWrapper);

        Set<Integer> expressCompanyIds = iPage.getRecords().stream().map(DeliveryElectronicSheet::getExpressCompanyId).collect(Collectors.toSet());
        final Map<Integer, DeliveryCompany> companyMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(expressCompanyIds)) {
            LambdaQueryWrapper<DeliveryCompany> companyQueryWrapper = new LambdaQueryWrapper<>();
            companyQueryWrapper.eq(DeliveryCompany::getSiteId, RequestUtils.siteId());
            companyQueryWrapper.in(DeliveryCompany::getCompanyId, expressCompanyIds);
            companyMap.putAll(deliveryCompanyMapper.selectList(companyQueryWrapper)
                    .stream().collect(Collectors.toMap(DeliveryCompany::getCompanyId, e -> e)));
        }

        List<DeliveryElectronicSheetPageVo> list = iPage.getRecords().stream().map(item -> {
            DeliveryElectronicSheetPageVo vo = BeanUtil.toBean(item, DeliveryElectronicSheetPageVo.class);
            DeliveryCompany company = companyMap.getOrDefault(item.getExpressCompanyId(), new DeliveryCompany());
            vo.setCompany(new DeliveryElectronicSheetPageVo.Company(company.getCompanyName(), company.getExpressNoElectronicSheet()));
            vo.setPayTypeName(ElectronicSheetPayTypeEnum.getPayTypeByType(item.getPayType()));
            vo.setSupportTradeName(SupportTradeEnum.getNameByType(item.getSupportTrade()));
            return vo;
        }).collect(Collectors.toList());
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 电子面单详情
     *
     * @param id 主键
     * @return ShopDeliveryElectronicSheetInfoVo
     */
    @Override
    public DeliveryElectronicSheetInfoVo info(Integer id) {
        DeliveryElectronicSheet model = deliveryElectronicSheetMapper.selectOne(
                new QueryWrapper<DeliveryElectronicSheet>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        DeliveryElectronicSheetInfoVo vo = new DeliveryElectronicSheetInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 电子面单添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(DeliveryElectronicSheetParam addParam) {
        DeliveryElectronicSheet model = new DeliveryElectronicSheet();
        model.setSiteId(RequestUtils.siteId());
        model.setTemplateName(addParam.getTemplateName());
        model.setExpressCompanyId(addParam.getExpressCompanyId());
        model.setCustomerName(addParam.getCustomerName());
        model.setCustomerPwd(addParam.getCustomerPwd());
        model.setSendSite(addParam.getSendSite());
        model.setSendStaff(addParam.getSendStaff());
        model.setMonthCode(addParam.getMonthCode());
        model.setPayType(addParam.getPayType());
        model.setIsNotice(addParam.getIsNotice());
        model.setStatus(addParam.getStatus());
        model.setExpType(addParam.getExpType());
        model.setPrintStyle(addParam.getPrintStyle());
        model.setIsDefault(addParam.getIsDefault());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setSupportTrade(addParam.getSupportTrade());
        deliveryElectronicSheetMapper.insert(model);

        if (addParam.getIsDefault() == 1) {
            setDefault(model.getId());
        }
    }

    /**
     * 电子面单编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, DeliveryElectronicSheetParam editParam) {
        DeliveryElectronicSheet model = deliveryElectronicSheetMapper.selectOne(
                new QueryWrapper<DeliveryElectronicSheet>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);

        model.setSiteId(editParam.getSiteId());
        model.setTemplateName(editParam.getTemplateName());
        model.setExpressCompanyId(editParam.getExpressCompanyId());
        model.setCustomerName(editParam.getCustomerName());
        model.setCustomerPwd(editParam.getCustomerPwd());
        model.setSendSite(editParam.getSendSite());
        model.setSendStaff(editParam.getSendStaff());
        model.setMonthCode(editParam.getMonthCode());
        model.setPayType(editParam.getPayType());
        model.setIsNotice(editParam.getIsNotice());
        model.setStatus(editParam.getStatus());
        model.setExpType(editParam.getExpType());
        model.setPrintStyle(editParam.getPrintStyle());
        model.setIsDefault(editParam.getIsDefault());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setSupportTrade(editParam.getSupportTrade());
        deliveryElectronicSheetMapper.updateById(model);
    }

    /**
     * 电子面单删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        DeliveryElectronicSheet model = deliveryElectronicSheetMapper.selectOne(
                new QueryWrapper<DeliveryElectronicSheet>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        deliveryElectronicSheetMapper.delete(new QueryWrapper<DeliveryElectronicSheet>().eq("id", id));
    }

    @Override
    public Map<Integer, String> getPayType() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (int i = 0; i < ElectronicSheetPayTypeEnum.values().length; i++) {
            map.put(i + 1, ElectronicSheetPayTypeEnum.values()[i].getPayType());
        }
        return map;
    }

    @Override
    @Transactional
    public void setDefault(Integer id) {
        LambdaQueryWrapper<DeliveryElectronicSheet> selectOneQueryWrapper = new LambdaQueryWrapper<>();
        selectOneQueryWrapper.eq(DeliveryElectronicSheet::getId, id);
        selectOneQueryWrapper.eq(DeliveryElectronicSheet::getSiteId, RequestUtils.siteId());

        DeliveryElectronicSheet deliveryElectronicSheet;
        if (id == null || (deliveryElectronicSheet = deliveryElectronicSheetMapper.selectOne(selectOneQueryWrapper)) == null) {
            return;
        }

        LambdaQueryWrapper<DeliveryElectronicSheet> resetQueryWrapper = new LambdaQueryWrapper<>();
        resetQueryWrapper.eq(DeliveryElectronicSheet::getSiteId, RequestUtils.siteId());

        DeliveryElectronicSheet resetElectronicSheet = new DeliveryElectronicSheet();
        resetElectronicSheet.setIsDefault(0);
        resetElectronicSheet.setUpdateTime(System.currentTimeMillis() / 1000);

        deliveryElectronicSheetMapper.update(resetElectronicSheet, resetQueryWrapper);

        deliveryElectronicSheet.setIsDefault(1);
        deliveryElectronicSheet.setUpdateTime(System.currentTimeMillis() / 1000);
        deliveryElectronicSheetMapper.updateById(deliveryElectronicSheet);
    }

    @Override
    public ElectronicSheetConfigValueVo getElectronicSheetConfig() {
        CoreSysConfigVo config = coreConfigService.getConfig(RequestUtils.siteId(), ShopDeliveryElectronicSheetConst.ELECTRONIC_SHEET_CONFIG_KEY);
        if (ObjectUtil.hasEmpty(config, config.getValue())) {
            return ElectronicSheetConfigValueVo.newInstance();
        }
        ElectronicSheetConfigValueVo parsed = JacksonUtils.toSnakeCaseObject(config.getValue(), ElectronicSheetConfigValueVo.class);
        return parsed != null ? parsed : ElectronicSheetConfigValueVo.newInstance();
    }

    @Override
    public void setElectronicSheetConfig(Object config) {
        coreConfigService.setConfig(RequestUtils.siteId(), ShopDeliveryElectronicSheetConst.ELECTRONIC_SHEET_CONFIG_KEY, JSONUtil.parseObj(config));
    }

}
