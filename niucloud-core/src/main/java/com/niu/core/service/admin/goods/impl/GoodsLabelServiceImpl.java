package com.niu.core.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsLabel;
import com.niu.core.mapper.goods.GoodsLabelMapper;
import com.niu.core.service.admin.goods.IGoodsLabelService;
import com.niu.core.service.admin.goods.param.EditGoodsLabelSortParam;
import com.niu.core.service.admin.goods.param.EditGoodsLabelStatusParam;
import com.niu.core.service.admin.goods.param.GoodsLabelParam;
import com.niu.core.service.admin.goods.param.GoodsLabelSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsLabelInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsLabelListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

/**
 * 商品标签服务实现
 * 对应 PHP: app\service\admin\basic\admin\LabelService
 */
@Service
public class GoodsLabelServiceImpl implements IGoodsLabelService {

    private static final int STATUS_ENABLE = 1;
    private static final int STATUS_CLOSE = 0;

    @Resource
    private GoodsLabelMapper goodsLabelMapper;

    @Override
    public PageResult<GoodsLabelListVo> page(PageParam pageParam, GoodsLabelSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<GoodsLabel> queryWrapper = baseQuery();
        if (searchParam != null) {
            if (ObjectUtil.isNotEmpty(searchParam.getLabelName())) {
                queryWrapper.like("label_name", searchParam.getLabelName());
            }
            if (searchParam.getGroupId() != null && searchParam.getGroupId() > 0) {
                queryWrapper.eq("group_id", searchParam.getGroupId());
            }
            if (ObjectUtil.isNotEmpty(searchParam.getOrder()) && ObjectUtil.isNotEmpty(searchParam.getSort())) {
                boolean isAsc = "asc".equalsIgnoreCase(searchParam.getSort());
                queryWrapper.orderBy(true, isAsc, searchParam.getOrder());
            } else {
                queryWrapper.orderByDesc("sort").orderByDesc("label_id");
            }
        } else {
            queryWrapper.orderByDesc("sort").orderByDesc("label_id");
        }

        IPage<GoodsLabel> iPage = goodsLabelMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<GoodsLabelListVo> list = new LinkedList<>();
        for (GoodsLabel item : iPage.getRecords()) {
            GoodsLabelListVo vo = new GoodsLabelListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<GoodsLabelListVo> list(GoodsLabelSearchParam searchParam) {
        QueryWrapper<GoodsLabel> queryWrapper = baseQuery();
        queryWrapper.eq("status", STATUS_ENABLE);
        if (searchParam != null && ObjectUtil.isNotEmpty(searchParam.getLabelName())) {
            queryWrapper.like("label_name", searchParam.getLabelName());
        }
        queryWrapper.orderByDesc("sort").orderByDesc("label_id");

        List<GoodsLabel> records = goodsLabelMapper.selectList(queryWrapper);
        List<GoodsLabelListVo> list = new LinkedList<>();
        for (GoodsLabel item : records) {
            GoodsLabelListVo vo = new GoodsLabelListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    @Override
    public GoodsLabelInfoVo info(Integer id) {
        GoodsLabel model = goodsLabelMapper.selectOne(baseQuery().eq("label_id", id));
        Assert.notNull(model, "标签不存在");

        GoodsLabelInfoVo vo = new GoodsLabelInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public Integer add(GoodsLabelParam addParam) {
        Long count = goodsLabelMapper.selectCount(baseQuery().eq("label_name", addParam.getLabelName()));
        if (count != null && count > 0) {
            throw new CommonException("标签名称重复，请检查");
        }

        GoodsLabel model = new GoodsLabel();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.defaultSiteId());
        model.setGroupId(ObjectUtil.defaultIfNull(addParam.getGroupId(), 0));
        model.setStyleType(StrUtil.blankToDefault(addParam.getStyleType(), ""));
        model.setColorJson(addParam.getColorJson() == null ? "{}" : addParam.getColorJson().toString());
        model.setIcon(StrUtil.blankToDefault(addParam.getIcon(), ""));
        model.setStatus(ObjectUtil.defaultIfNull(addParam.getStatus(), STATUS_ENABLE));
        model.setMemo(StrUtil.blankToDefault(addParam.getMemo(), ""));
        model.setSort(ObjectUtil.defaultIfNull(addParam.getSort(), 0));
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(0L);
        goodsLabelMapper.insert(model);
        return model.getLabelId();
    }

    @Override
    public void edit(Integer id, GoodsLabelParam editParam) {
        GoodsLabel exist = goodsLabelMapper.selectOne(baseQuery()
                .eq("label_name", editParam.getLabelName())
                .ne("label_id", id)
                .last("limit 1"));
        if (exist != null) {
            throw new CommonException("标签名称重复，请检查");
        }

        GoodsLabel model = goodsLabelMapper.selectOne(baseQuery().eq("label_id", id));
        Assert.notNull(model, "标签不存在");

        model.setLabelName(editParam.getLabelName());
        model.setGroupId(ObjectUtil.defaultIfNull(editParam.getGroupId(), 0));
        model.setStyleType(StrUtil.blankToDefault(editParam.getStyleType(), ""));
        model.setColorJson(editParam.getColorJson() == null ? "{}" : editParam.getColorJson().toString());
        model.setIcon(StrUtil.blankToDefault(editParam.getIcon(), ""));
        model.setStatus(ObjectUtil.defaultIfNull(editParam.getStatus(), model.getStatus()));
        model.setMemo(StrUtil.blankToDefault(editParam.getMemo(), ""));
        model.setSort(ObjectUtil.defaultIfNull(editParam.getSort(), 0));
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        goodsLabelMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        goodsLabelMapper.delete(baseQuery().eq("label_id", id));
    }

    @Override
    public void modifySort(EditGoodsLabelSortParam editParam) {
        GoodsLabel model = goodsLabelMapper.selectOne(baseQuery().eq("label_id", editParam.getLabelId()));
        Assert.notNull(model, "标签不存在");
        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        goodsLabelMapper.updateById(model);
    }

    @Override
    public void modifyStatus(EditGoodsLabelStatusParam editParam) {
        GoodsLabel model = goodsLabelMapper.selectOne(baseQuery().eq("label_id", editParam.getLabelId()));
        Assert.notNull(model, "标签不存在");
        model.setStatus(editParam.getStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        goodsLabelMapper.updateById(model);
    }

    @Override
    public void copy(Integer id) {
        GoodsLabel model = goodsLabelMapper.selectOne(baseQuery().eq("label_id", id));
        Assert.notNull(model, "标签不存在");

        GoodsLabel copy = new GoodsLabel();
        copy.setSiteId(RequestUtils.defaultSiteId());
        copy.setLabelName(model.getLabelName() + "_副本");
        copy.setGroupId(ObjectUtil.defaultIfNull(model.getGroupId(), 0));
        copy.setStyleType(StrUtil.blankToDefault(model.getStyleType(), ""));
        copy.setColorJson(StrUtil.blankToDefault(model.getColorJson(), "{}"));
        copy.setIcon(StrUtil.blankToDefault(model.getIcon(), ""));
        copy.setStatus(STATUS_CLOSE);
        copy.setMemo(StrUtil.blankToDefault(model.getMemo(), ""));
        copy.setSort(ObjectUtil.defaultIfNull(model.getSort(), 0));
        copy.setCreateTime(System.currentTimeMillis() / 1000);
        copy.setUpdateTime(0L);
        goodsLabelMapper.insert(copy);
    }

    private QueryWrapper<GoodsLabel> baseQuery() {
        return new QueryWrapper<GoodsLabel>().eq("site_id", RequestUtils.defaultSiteId());
    }
}
