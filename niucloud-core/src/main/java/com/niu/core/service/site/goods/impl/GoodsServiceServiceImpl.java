package com.niu.core.service.site.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.service.site.goods.param.GoodsServiceParam;
import com.niu.core.service.site.goods.param.GoodsServiceSearchParam;
import com.niu.core.service.site.goods.IGoodsServiceService;
import com.niu.core.service.site.goods.vo.GoodsServiceInfoVo;
import com.niu.core.service.site.goods.vo.GoodsServiceListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品服务实现
 */
@Service
public class GoodsServiceServiceImpl implements IGoodsServiceService {
        
    @Resource
    GoodsServiceMapper mallGoodsServiceMapper;

    /**
     * 商品服务分页列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsServiceListVo>
     */
    @Override
    public PageResult<GoodsServiceListVo> page(PageParam pageParam, GoodsServiceSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<GoodsService> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        queryWrapper.orderByDesc("service_id");
        if (ObjectUtil.isNotNull(searchParam.getServiceName())) queryWrapper.like("service_name", searchParam.getServiceName());

        IPage<GoodsService> iPage = mallGoodsServiceMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<GoodsServiceListVo> list = new LinkedList<>();
        for (GoodsService item : iPage.getRecords()) {
            GoodsServiceListVo vo = new GoodsServiceListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品服务列表
     * @param searchParam 搜索参数
     * @return List<ShopGoodsServiceListVo>
     */
    @Override
    public List<GoodsServiceListVo> list(GoodsServiceSearchParam searchParam) {
        QueryWrapper<GoodsService> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        queryWrapper.orderByDesc("service_id");
        if (ObjectUtil.isNotNull(searchParam.getServiceName())) queryWrapper.like("service_name", searchParam.getServiceName());

        List<GoodsService> record = mallGoodsServiceMapper.selectList(queryWrapper);
        List<GoodsServiceListVo> list = new LinkedList<>();
        for (GoodsService item : record) {
            GoodsServiceListVo vo = new GoodsServiceListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 商品服务详情
     * @param id 主键
     * @return ShopGoodsServiceInfoVo
     */
    @Override
    public GoodsServiceInfoVo info(Integer id) {
        GoodsService model = mallGoodsServiceMapper.selectOne(
                new QueryWrapper<GoodsService>()
                    .eq("service_id", id));

        Assert.notNull(model, "服务不存在");

        GoodsServiceInfoVo vo = new GoodsServiceInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    /**
     * 商品服务添加
     * @param addParam 参数
     */
    @Override
    public void add(GoodsServiceParam addParam) {
        GoodsService model = new GoodsService();

        model.setSiteId(RequestUtils.defaultSiteId());
        model.setServiceName(addParam.getServiceName());
        model.setImage(addParam.getImage());
        model.setDesc(addParam.getDesc());
        model.setCreateTime(System.currentTimeMillis() / 1000);

        mallGoodsServiceMapper.insert(model);
    }

    /**
     * 商品服务编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, GoodsServiceParam editParam) {
        GoodsService model = mallGoodsServiceMapper.selectOne(
                new QueryWrapper<GoodsService>()
                        .eq("service_id", id)
                        .eq("site_id", RequestUtils.defaultSiteId()));

        Assert.notNull(model, "服务不存在!");

        model.setServiceName(editParam.getServiceName());
        model.setImage(editParam.getImage());
        model.setDesc(editParam.getDesc());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        mallGoodsServiceMapper.updateById(model);
    }

    /**
     * 商品服务删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        mallGoodsServiceMapper.delete(new QueryWrapper<GoodsService>().eq("service_id", id).eq("site_id", RequestUtils.defaultSiteId()));
    }

}
