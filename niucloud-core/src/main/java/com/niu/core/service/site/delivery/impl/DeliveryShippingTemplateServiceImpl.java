package com.niu.core.service.site.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryShippingTemplate;
import com.niu.core.entity.delivery.DeliveryShippingTemplateItem;
import com.niu.core.event.delivery.CheckShippingTemplateInUseEventDefiner;
import com.niu.core.mapper.delivery.DeliveryShippingTemplateItemMapper;
import com.niu.core.mapper.delivery.DeliveryShippingTemplateMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.site.delivery.param.DeliveryShippingTemplateParam;
import com.niu.core.service.site.delivery.param.DeliveryShippingTemplateSearchParam;
import com.niu.core.service.site.delivery.IDeliveryShippingTemplateService;
import com.niu.core.service.site.delivery.vo.DeliveryShippingTemplateInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryShippingTemplateListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 运费模板实现
 */
@Service
public class DeliveryShippingTemplateServiceImpl extends ServiceImpl<DeliveryShippingTemplateItemMapper, DeliveryShippingTemplateItem> implements IDeliveryShippingTemplateService {
        
    @Resource
    DeliveryShippingTemplateMapper deliveryShippingTemplateMapper;

    @Resource
    DeliveryShippingTemplateItemMapper deliveryShippingTemplateItemMapper;

    /**
     * 运费模板分页列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryShippingTemplateListVo>
     */
    @Override
    public PageResult<DeliveryShippingTemplateListVo> page(PageParam pageParam, DeliveryShippingTemplateSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<DeliveryShippingTemplate> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getTemplateName())) queryWrapper.like("template_name", searchParam.getTemplateName());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("template_id");

        IPage<DeliveryShippingTemplate> iPage = deliveryShippingTemplateMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<DeliveryShippingTemplateListVo> list = new LinkedList<>();
        for (DeliveryShippingTemplate item : iPage.getRecords()) {
            DeliveryShippingTemplateListVo vo = new DeliveryShippingTemplateListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 运费模板列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryShippingTemplateListVo>
     */
    @Override
    public List<DeliveryShippingTemplateListVo> list(DeliveryShippingTemplateSearchParam searchParam) {
        QueryWrapper<DeliveryShippingTemplate> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getTemplateName())) queryWrapper.like("template_name", searchParam.getTemplateName());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("template_id");

        List<DeliveryShippingTemplate> record = deliveryShippingTemplateMapper.selectList(queryWrapper);
        List<DeliveryShippingTemplateListVo> list = new LinkedList<>();
        for (DeliveryShippingTemplate item : record) {
            DeliveryShippingTemplateListVo vo = new DeliveryShippingTemplateListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 运费模板详情
     * @param id 主键
     * @return ShopDeliveryShippingTemplateInfoVo
     */
    @Override
    public DeliveryShippingTemplateInfoVo info(Integer id) {
        DeliveryShippingTemplate model = deliveryShippingTemplateMapper.selectOne(
                new QueryWrapper<DeliveryShippingTemplate>()
                        .eq("template_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "运费模板不存在");

        DeliveryShippingTemplateInfoVo vo = new DeliveryShippingTemplateInfoVo();
        BeanUtils.copyProperties(model, vo);

        // 处理 supportTrade 字段转换 - 正确解析JSON数组
        if (model.getSupportTrade() != null && !model.getSupportTrade().isEmpty()) {
            try {
                // 如果数据库存储的是JSON格式的字符串，直接解析
                if (model.getSupportTrade().startsWith("[") && model.getSupportTrade().endsWith("]")) {
                    List<String> supportTradeList = JSONUtil.parseArray(model.getSupportTrade()).toList(String.class);
                    vo.setSupportTrade(supportTradeList);
                } else {
                    // 如果是逗号分隔的字符串
                    List<String> supportTradeList = Arrays.asList(model.getSupportTrade().split(","));
                    vo.setSupportTrade(supportTradeList);
                }
            } catch (Exception e) {
                // 解析失败时的备用方案
                List<String> supportTradeList = Arrays.asList(model.getSupportTrade().split(","));
                vo.setSupportTrade(supportTradeList);
            }
        }

        vo.setFeeData(CollectUtils.convert(deliveryShippingTemplateItemMapper.selectList(new QueryWrapper<DeliveryShippingTemplateItem>()
                .eq("template_id", model.getTemplateId()).ne("fee_area_ids", "").groupBy("fee_area_ids")
        ), DeliveryShippingTemplateInfoVo.FeeData.class));
        vo.setNoDeliveryData(CollectUtils.convert(deliveryShippingTemplateItemMapper.selectList(new QueryWrapper<DeliveryShippingTemplateItem>()
                .eq("template_id", model.getTemplateId()).ne("no_delivery_area_ids", "").groupBy("no_delivery_area_ids")
        ), DeliveryShippingTemplateInfoVo.NoDeliveryData.class));
        vo.setFreeShippingData(CollectUtils.convert(deliveryShippingTemplateItemMapper.selectList(new QueryWrapper<DeliveryShippingTemplateItem>()
                .eq("template_id", model.getTemplateId()).ne("free_shipping_area_ids", "").groupBy("free_shipping_area_ids")
        ), DeliveryShippingTemplateInfoVo.FreeShippingData.class));

        return vo;
    }

    /**
     * 运费模板添加
     * @param addParam 参数
     */
    @Override
    @Transactional
    public void add(DeliveryShippingTemplateParam addParam) {
        DeliveryShippingTemplate model = new DeliveryShippingTemplate();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setSupportTrade(addParam.getSupportTrade().toString());

        deliveryShippingTemplateMapper.insert(model);

        List<DeliveryShippingTemplateItem> list = new LinkedList<>();
        for (int i = 0; i < addParam.getArea().size(); i++) {
            JSONObject data = addParam.getArea().getJSONObject(i);
            DeliveryShippingTemplateItem item = JSONUtil.toBean(data, DeliveryShippingTemplateItem.class);
            item.setTemplateId(model.getTemplateId());
            item.setSiteId(model.getSiteId());
            item.setFeeType(model.getFeeType());
            item.setFeeAreaIds(data.getStr("fee_area_ids", ""));
            item.setFeeAreaNames(data.getStr("fee_area_names", ""));
            item.setNoDeliveryAreaIds(data.getStr("no_delivery_area_ids", ""));
            item.setNoDeliveryAreaNames(data.getStr("no_delivery_area_names", ""));
            item.setFreeShippingAreaIds(data.getStr("free_shipping_area_ids", ""));
            item.setFreeShippingAreaNames(data.getStr("free_shipping_area_names", ""));
            list.add(item);
        }
        super.saveBatch(list);
    }

    /**
     * 运费模板编辑
     *
     * @param editParam 参数
     */
    @Override
    @Transactional
    public void edit(Integer id, DeliveryShippingTemplateParam editParam) {
        DeliveryShippingTemplate model = deliveryShippingTemplateMapper.selectOne(
                new QueryWrapper<DeliveryShippingTemplate>()
                        .eq("template_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "运费模板不存在");
        BeanUtils.copyProperties(editParam, model);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setSupportTrade(editParam.getSupportTrade().toString());
        deliveryShippingTemplateMapper.updateById(model);

        deliveryShippingTemplateItemMapper.delete(new QueryWrapper<DeliveryShippingTemplateItem>().eq("template_id", id));

        List<DeliveryShippingTemplateItem> list = new LinkedList<>();
        for (int i = 0; i < editParam.getArea().size(); i++) {
            JSONObject data = editParam.getArea().getJSONObject(i);
            DeliveryShippingTemplateItem item = JSONUtil.toBean(data, DeliveryShippingTemplateItem.class);
            item.setTemplateId(model.getTemplateId());
            item.setSiteId(model.getSiteId());
            item.setFeeType(model.getFeeType());
            item.setFeeAreaIds(data.getStr("fee_area_ids", ""));
            item.setFeeAreaNames(data.getStr("fee_area_names", ""));
            item.setNoDeliveryAreaIds(data.getStr("no_delivery_area_ids", ""));
            item.setNoDeliveryAreaNames(data.getStr("no_delivery_area_names", ""));
            item.setFreeShippingAreaIds(data.getStr("free_shipping_area_ids", ""));
            item.setFreeShippingAreaNames(data.getStr("free_shipping_area_names", ""));
            list.add(item);
        }
        super.saveBatch(list);
    }

    /**
     * 运费模板删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        CheckShippingTemplateInUseEventDefiner.CheckShippingTemplateInUseEvent event =
                new CheckShippingTemplateInUseEventDefiner.CheckShippingTemplateInUseEvent();
        event.setSiteId(RequestUtils.siteId());
        event.setTemplateId(id);
        List<CheckShippingTemplateInUseEventDefiner.CheckShippingTemplateInUseResult> results =
                EventAndSubscribeOfPublisher.publishAndCallback(event);
        boolean inUse = results.stream()
                .anyMatch(r -> r != null && Boolean.TRUE.equals(r.getInUse()));
        if (inUse) {
            throw new CommonException("运费模板已被使用，不能删除");
        }

        DeliveryShippingTemplate model = deliveryShippingTemplateMapper.selectOne(
                new QueryWrapper<DeliveryShippingTemplate>()
                        .eq("template_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "运费模板数据不存在");

        deliveryShippingTemplateMapper.delete(new QueryWrapper<DeliveryShippingTemplate>().eq("template_id", id));
        deliveryShippingTemplateItemMapper.delete(new QueryWrapper<DeliveryShippingTemplateItem>().eq("template_id", id));
    }

}
