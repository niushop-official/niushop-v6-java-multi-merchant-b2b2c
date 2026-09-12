package com.niu.core.service.admin.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.shop.SiteShopAccountLog;
import com.niu.core.mapper.shop.SiteShopAccountLogMapper;
import com.niu.core.service.admin.shop.ISiteShopAccountLogService;
import com.niu.core.service.admin.shop.param.SiteShopAccountLogParam;
import com.niu.core.service.admin.shop.param.SiteShopAccountLogSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopAccountLogInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopAccountLogListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.util.LinkedList;
import java.util.List;


/**
 * 商家结算账单记录实现
 */
@Service
public class SiteShopAccountLogServiceImpl implements ISiteShopAccountLogService {

    @Resource
    SiteShopAccountLogMapper siteShopAccountLogMapper;


    @Override
    public PageResult<SiteShopAccountLogListVo> page(PageParam pageParam, SiteShopAccountLogSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<SiteShopAccountLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getFromType()) && !"all".equals(searchParam.getFromType())) {
        queryWrapper.eq("from_type", searchParam.getFromType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getId())) {
        queryWrapper.eq("id", searchParam.getId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getMemberId())) {
        queryWrapper.eq("member_id", searchParam.getMemberId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getMemo())) {
        queryWrapper.eq("memo", searchParam.getMemo());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getMoney())) {
        queryWrapper.eq("money", searchParam.getMoney());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getMoneySum())) {
        queryWrapper.eq("money_sum", searchParam.getMoneySum());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getRelatedId())) {
        queryWrapper.eq("related_id", searchParam.getRelatedId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getSiteId())) {
        queryWrapper.eq("site_id", searchParam.getSiteId());
        }
        queryWrapper.orderByDesc("id");

        IPage<SiteShopAccountLog> iPage = siteShopAccountLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SiteShopAccountLogListVo> list = new LinkedList<>();
        for (SiteShopAccountLog item : iPage.getRecords()) {
            SiteShopAccountLogListVo vo = new SiteShopAccountLogListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public SiteShopAccountLogInfoVo info(Integer id) {
        SiteShopAccountLog model = siteShopAccountLogMapper.selectOne(
                new QueryWrapper<SiteShopAccountLog>()
                        .eq("related_id", id)
                        .or()
                        .eq("id",id)
                        .eq("site_id", RequestUtils.siteId()));


        Assert.notNull(model, "商家结算账单记录不存在");

        SiteShopAccountLogInfoVo vo = new SiteShopAccountLogInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public void add(SiteShopAccountLogParam addParam) {

        SiteShopAccountLog model = new SiteShopAccountLog();

        model.setFromType(addParam.getFromType());
        model.setMemberId(addParam.getMemberId());
        model.setMemo(addParam.getMemo());
        model.setMoney(addParam.getMoney());
        model.setMoneySum(addParam.getMoneySum());
        model.setRelatedId(addParam.getRelatedId());

        model.setSiteId(RequestUtils.siteId());

        siteShopAccountLogMapper.insert(model);
    }

    @Override
    public void edit(Integer id, SiteShopAccountLogParam editParam) {

        SiteShopAccountLog model = siteShopAccountLogMapper.selectOne(
                new QueryWrapper<SiteShopAccountLog>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商家结算账单记录不存在");
        model.setFromType(editParam.getFromType());
        model.setMemberId(editParam.getMemberId());
        model.setMemo(editParam.getMemo());
        model.setMoney(editParam.getMoney());
        model.setMoneySum(editParam.getMoneySum());
        model.setRelatedId(editParam.getRelatedId());
        siteShopAccountLogMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        siteShopAccountLogMapper.delete(new QueryWrapper<SiteShopAccountLog>().eq("id", id).eq("site_id", RequestUtils.siteId()));
    }

}
