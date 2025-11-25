package com.niu.mall.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.entity.goods.MallGoodsLabel;
import com.niu.mall.mapper.goods.MallGoodsLabelMapper;
import com.niu.mall.service.admin.goods.param.EditGoodsLabelSortParam;
import com.niu.mall.service.admin.goods.param.EditGoodsLabelStatusParam;
import com.niu.mall.service.admin.goods.param.MallGoodsLabelParam;
import com.niu.mall.service.admin.goods.param.MallGoodsLabelSearchParam;
import com.niu.mall.service.admin.goods.IMallGoodsLabelService;
import com.niu.mall.service.admin.goods.vo.MallGoodsLabelInfoVo;
import com.niu.mall.service.admin.goods.vo.MallGoodsLabelListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品标签实现
 */
@Service
public class MallGoodsLabelServiceImpl implements IMallGoodsLabelService {
        
    @Resource
    MallGoodsLabelMapper mallGoodsLabelMapper;

    /**
     * 商品标签列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsLabelListVo>
     */
    @Override
    public PageResult<MallGoodsLabelListVo> page(PageParam pageParam, MallGoodsLabelSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<MallGoodsLabel> queryWrapper = new MPJQueryWrapper<>();
        /*queryWrapper.setAlias("l")
                .leftJoin("?_mall_goods_label_group lg ON l.group_id = lg.group_id".replace("?_", GlobalConfig.tablePrefix));*/
        queryWrapper.select("label_id,label_name,group_id,style_type, color_json, icon, status, memo, sort, create_time, update_time");
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getLabelName())) queryWrapper.like("label_name", searchParam.getLabelName());
        queryWrapper.orderByAsc("sort");
        queryWrapper.orderByDesc("label_id");

        IPage<MallGoodsLabelListVo> iPage = mallGoodsLabelMapper.selectJoinPage(new Page<>(page, limit), MallGoodsLabelListVo.class, queryWrapper);
        for (MallGoodsLabelListVo vo : iPage.getRecords()) {
            MallGoodsLabelListVo.Group group = new MallGoodsLabelListVo.Group();
            BeanUtils.copyProperties(vo, group);
            vo.setGroup(group);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    /**
     * 商品标签列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsLabelListVo>
     */
    @Override
    public List<MallGoodsLabelListVo> list(MallGoodsLabelSearchParam searchParam) {
        QueryWrapper<MallGoodsLabel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getLabelName())) queryWrapper.like("label_name", searchParam.getLabelName());
        queryWrapper.orderByDesc("label_id");

        List<MallGoodsLabel> record = mallGoodsLabelMapper.selectList(queryWrapper);
        List<MallGoodsLabelListVo> list = new LinkedList<>();
        for (MallGoodsLabel item : record) {
            MallGoodsLabelListVo vo = new MallGoodsLabelListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 商品标签详情
     * @param id 主键
     * @return ShopGoodsLabelInfoVo
     */
    @Override
    public MallGoodsLabelInfoVo info(Integer id) {
        MallGoodsLabel model = mallGoodsLabelMapper.selectOne(
                new QueryWrapper<MallGoodsLabel>()
                    .eq("label_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "标签不存在");

        MallGoodsLabelInfoVo vo = new MallGoodsLabelInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 商品标签添加
     * @param addParam 参数
     */
    @Override
    public void add(MallGoodsLabelParam addParam) {
        Long count = mallGoodsLabelMapper.selectCount(new QueryWrapper<MallGoodsLabel>()
                .eq("label_name", addParam.getLabelName())
                .eq("site_id", RequestUtils.siteId()));
        if (count > 0) throw new CommonException("标签已存在");

        MallGoodsLabel model = new MallGoodsLabel();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setSort(ObjectUtil.isNotEmpty(addParam.getSort()) ? Integer.parseInt(addParam.getSort()) : 0);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setColorJson(addParam.getColorJson().toString());

        mallGoodsLabelMapper.insert(model);
    }

    /**
     * 商品标签编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MallGoodsLabelParam editParam) {
        Long count = mallGoodsLabelMapper.selectCount(new QueryWrapper<MallGoodsLabel>()
                .eq("label_name", editParam.getLabelName())
                .ne("label_id", id)
                .eq("site_id", RequestUtils.siteId()));
        if (count > 0) throw new CommonException("标签已存在");

        MallGoodsLabel model = mallGoodsLabelMapper.selectOne(
                new QueryWrapper<MallGoodsLabel>()
                        .eq("label_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "标签不存在");

        BeanUtils.copyProperties(editParam, model);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setColorJson(editParam.getColorJson().toString());

        mallGoodsLabelMapper.updateById(model);
    }

    /**
     * 商品标签删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        mallGoodsLabelMapper.delete(new QueryWrapper<MallGoodsLabel>().eq("label_id", id).eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 商品标签排序编辑
     *
     * @param editParam 参数
     */
    @Override
    public void modifySort(EditGoodsLabelSortParam editParam) {
        MallGoodsLabel model = mallGoodsLabelMapper.selectOne(
                new QueryWrapper<MallGoodsLabel>()
                        .eq("label_id",  editParam.getLabelId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "标签不存在!");

        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        mallGoodsLabelMapper.updateById(model);
    }

    @Override
    public void modifyStatus(EditGoodsLabelStatusParam editParam) {
        MallGoodsLabel model = mallGoodsLabelMapper.selectOne(
            new QueryWrapper<MallGoodsLabel>()
                .eq("label_id",  editParam.getLabelId())
                .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "标签不存在!");

        model.setStatus(editParam.getStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        mallGoodsLabelMapper.updateById(model);
    }

    @Override
    public void copy(Integer id) {
        MallGoodsLabel model = mallGoodsLabelMapper.selectOne(
                new QueryWrapper<MallGoodsLabel>()
                        .eq("label_id",  id)
                        .eq("site_id", RequestUtils.siteId()));
        Assert.notNull(model, "标签不存在!");

        model.setLabelName(model.getLabelName()+"_副本");

        Long count = mallGoodsLabelMapper.selectCount(new QueryWrapper<MallGoodsLabel>()
                .eq("label_name", model.getLabelName())
                .eq("site_id", RequestUtils.siteId()));
        if (count > 0) throw new CommonException("标签已存在");

        MallGoodsLabel newModel = new MallGoodsLabel();
        newModel.setSiteId(RequestUtils.siteId());
        newModel.setLabelName(model.getLabelName());
        newModel.setStyleType(model.getStyleType());
        newModel.setColorJson(model.getColorJson());
        newModel.setIcon(model.getIcon());
        newModel.setStatus(model.getStatus());
        newModel.setMemo(model.getMemo());
        newModel.setSort(model.getSort());
        newModel.setCreateTime(System.currentTimeMillis() / 1000);

        mallGoodsLabelMapper.insert(newModel);
    }
}
