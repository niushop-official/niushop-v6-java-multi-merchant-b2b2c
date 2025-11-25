package com.niu.core.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsAttr;
import com.niu.core.mapper.goods.GoodsAttrMapper;
import com.niu.core.service.admin.goods.IGoodsAttrService;
import com.niu.core.service.admin.goods.param.*;
import com.niu.core.service.admin.goods.vo.GoodsAttrInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsAttrListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品参数实现
 */
@Service
public class GoodsAttrServiceImpl implements IGoodsAttrService {
        
    @Resource
    GoodsAttrMapper goodsAttrMapper;

    /**
     * 商品参数列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsAttrListVo>
     */
    @Override
    public PageResult<GoodsAttrListVo> page(PageParam pageParam, GoodsAttrSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<GoodsAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        if (ObjectUtil.isNotEmpty(searchParam.getAttrName())) queryWrapper.like("attr_name", searchParam.getAttrName());
        queryWrapper.orderByDesc(Arrays.asList("sort", "attr_id"));

        IPage<GoodsAttr> iPage = goodsAttrMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<GoodsAttrListVo> list = new LinkedList<>();
        for (GoodsAttr item : iPage.getRecords()) {
            GoodsAttrListVo vo = new GoodsAttrListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品参数列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsAttrListVo>
     */
    @Override
    public List<GoodsAttrListVo> list(GoodsAttrSearchParam searchParam) {
        QueryWrapper<GoodsAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        if (ObjectUtil.isNotEmpty(searchParam.getAttrName())) queryWrapper.like("attr_name", searchParam.getAttrName());
        queryWrapper.orderByDesc(Arrays.asList("sort", "attr_id"));

        List<GoodsAttr> record = goodsAttrMapper.selectList(queryWrapper);
        List<GoodsAttrListVo> list = new LinkedList<>();
        for (GoodsAttr item : record) {
            GoodsAttrListVo vo = new GoodsAttrListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 商品参数详情
     *
     * @param id 主键
     * @return ShopGoodsAttrInfoVo
     */
    @Override
    public GoodsAttrInfoVo info(Integer id) {
        GoodsAttr model = goodsAttrMapper.selectOne(
                new QueryWrapper<GoodsAttr>()
                    .eq("attr_id", id)
                    .eq("site_id", RequestUtils.defaultSiteId()));

        if (model == null) return null;

        GoodsAttrInfoVo vo = new GoodsAttrInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    /**
     * 商品参数添加
     * @param addParam 参数
     */
    @Override
    public void add(GoodsAttrParam addParam) {
        GoodsAttr model = new GoodsAttr();
        model.setSiteId(RequestUtils.defaultSiteId());
        model.setAttrName(addParam.getAttrName());
        model.setSort(addParam.getSort());
        goodsAttrMapper.insert(model);
    }

    /**
     * 商品参数编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, GoodsAttrParam editParam) {
        GoodsAttr model = goodsAttrMapper.selectOne(
                new QueryWrapper<GoodsAttr>()
                    .eq("attr_id",  id)
                    .eq("site_id", RequestUtils.defaultSiteId()));

        Assert.notNull(model, "商品参数不存在!");

        model.setAttrName(editParam.getAttrName());
        model.setSort(editParam.getSort());

        goodsAttrMapper.updateById(model);
    }

    /**
     * 商品参数删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        goodsAttrMapper.delete(new QueryWrapper<GoodsAttr>().eq("attr_id", id).eq("site_id", RequestUtils.defaultSiteId()));
    }

    /**
     * 修改商品参数排序号
     *
     * @param editParam
     */
    @Override
    public void modifySort(EditGoodsAttrSortParam editParam) {
        GoodsAttr model = goodsAttrMapper.selectOne(
                new QueryWrapper<GoodsAttr>()
                        .eq("attr_id",  editParam.getAttrId())
                        .eq("site_id", RequestUtils.defaultSiteId()));

        Assert.notNull(model, "商品参数不存在!");

        model.setSort(editParam.getSort());

        goodsAttrMapper.updateById(model);
    }

    /**
     * 修改商品参数名称
     *
     * @param editParam
     */
    @Override
    public void modifyAttrName(EditGoodsAttrNameParam editParam) {
        GoodsAttr model = goodsAttrMapper.selectOne(
                new QueryWrapper<GoodsAttr>()
                        .eq("attr_id",  editParam.getAttrId())
                        .eq("site_id", RequestUtils.defaultSiteId()));

        Assert.notNull(model, "商品参数不存在!");

        model.setAttrName(editParam.getAttrName());

        goodsAttrMapper.updateById(model);
    }

    /**
     * 修改商品参数值
     *
     * @param editParam
     */
    @Override
    public void modifyAttrValueFormat(EditGoodsAttrValueParam editParam) {
        GoodsAttr model = goodsAttrMapper.selectOne(
                new QueryWrapper<GoodsAttr>()
                        .eq("attr_id",  editParam.getAttrId())
                        .eq("site_id", RequestUtils.defaultSiteId()));

        Assert.notNull(model, "商品参数不存在!");

        model.setAttrValueFormat(editParam.getAttrValueFormat());

        goodsAttrMapper.updateById(model);
    }
}
