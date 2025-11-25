package com.niu.core.service.site.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsBrand;
import com.niu.core.enums.goods.BrandTypeEnum;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.service.site.goods.vo.GoodsBrandInfoVo;
import com.niu.core.service.site.goods.param.EditGoodsBrandSortParam;
import com.niu.core.service.site.goods.param.GoodsBrandParam;
import com.niu.core.service.site.goods.param.GoodsBrandSearchParam;
import com.niu.core.service.site.goods.IGoodsBrandService;
import com.niu.core.service.site.goods.vo.GoodsBrandListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;


/**
 * 商品品牌实现
 */
@Service
public class GoodsBrandServiceImpl implements IGoodsBrandService {
        
    @Resource
    GoodsBrandMapper goodsBrandMapper;


    @Override
    public PageResult<GoodsBrandListVo> page(PageParam pageParam, GoodsBrandSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();


        MPJQueryWrapper<GoodsBrand> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ngb")
                .leftJoin("?_site ns ON ngb.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .select("ngb.brand_id, ngb.site_id, ngb.brand_name, ngb.logo, ngb.color_json, ngb.`desc`, ngb.sort, ngb.create_time, ngb.update_time, ngb.delete_time, ngb.brand_type, ns.site_name, ns.front_end_logo");
        queryWrapper.eq("ngb.site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandName())) queryWrapper.like("ngb.brand_name", searchParam.getBrandName());
        queryWrapper.orderByDesc("ngb.brand_id");

        IPage<GoodsBrandListVo> iPage = goodsBrandMapper.selectJoinPage(new Page<>(page, limit), GoodsBrandListVo.class, queryWrapper);
        for (GoodsBrandListVo item : iPage.getRecords()) {
            GoodsBrandListVo.SiteInfoVO siteInfoVO = new GoodsBrandListVo.SiteInfoVO();
            siteInfoVO.setSiteId(item.getSiteId());
            siteInfoVO.setSiteName(item.getSiteName());
            siteInfoVO.setFrontEndLogo(item.getFrontEndLogo());
            item.setSite(siteInfoVO);
            item.setBrandTypeName(BrandTypeEnum.getNameByType(item.getBrandType()));
        }
        return PageResult.build(iPage);
    }

    @Override
    public List<GoodsBrandListVo> list(GoodsBrandSearchParam searchParam) {
        QueryWrapper<GoodsBrand> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(
                i -> i.eq("site_id", RequestUtils.siteId()).or()
                        .eq("brand_type", BrandTypeEnum.ADMIN.getType())
        );
        if (ObjectUtil.isNotEmpty(searchParam.getBrandName())) queryWrapper.like("brand_name", searchParam.getBrandName());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandIds())) queryWrapper.in("brand_id", searchParam.getBrandIds());

        queryWrapper.orderByDesc("brand_id");

        List<GoodsBrand> record = goodsBrandMapper.selectList(queryWrapper);
        List<GoodsBrandListVo> list = new LinkedList<>();
        for (GoodsBrand item : record) {
            GoodsBrandListVo vo = new GoodsBrandListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    @Override
    public GoodsBrandInfoVo info(Integer id) {
        GoodsBrand model = goodsBrandMapper.selectOne(
                new QueryWrapper<GoodsBrand>()
                        .eq("brand_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "品牌不存在");

       GoodsBrandInfoVo vo = new GoodsBrandInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public void add(GoodsBrandParam addParam) {
        Long count = goodsBrandMapper.selectCount(new QueryWrapper<GoodsBrand>()
                .eq("brand_name", addParam.getBrandName())
                .eq("site_id", RequestUtils.siteId()));
        if (count > 0) throw new CommonException("品牌已存在");

        GoodsBrand model = new GoodsBrand();

        model.setSiteId(RequestUtils.siteId());
        model.setBrandName(addParam.getBrandName());
        model.setBrandType(BrandTypeEnum.SITE.getType());
        model.setLogo(addParam.getLogo());
        model.setDesc(addParam.getDesc());
        model.setSort(ObjectUtil.isNotEmpty(addParam.getSort()) ? Integer.parseInt(addParam.getSort()) : 0);
        model.setCreateTime(System.currentTimeMillis() / 1000);

        goodsBrandMapper.insert(model);
    }

    @Override
    public void edit(Integer id, GoodsBrandParam editParam) {
        Long count = goodsBrandMapper.selectCount(new QueryWrapper<GoodsBrand>()
                .eq("brand_name", editParam.getBrandName())
                .ne("brand_id", id)
                .eq("site_id", RequestUtils.siteId()));
        if (count > 0) throw new CommonException("品牌已存在");

        GoodsBrand model = goodsBrandMapper.selectOne(
                new QueryWrapper<GoodsBrand>()
                        .eq("brand_id",  id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "品牌不存在!");

        model.setBrandName(editParam.getBrandName());
        model.setLogo(editParam.getLogo());
        model.setDesc(editParam.getDesc());
        model.setSort(ObjectUtil.isNotEmpty(editParam.getSort()) ? Integer.parseInt(editParam.getSort()) : 0);
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        goodsBrandMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        goodsBrandMapper.delete(new QueryWrapper<GoodsBrand>().eq("brand_id", id).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public void modifySort(EditGoodsBrandSortParam editParam) {
        GoodsBrand model = goodsBrandMapper.selectOne(
                new QueryWrapper<GoodsBrand>()
                        .eq("brand_id",  editParam.getBrandId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "品牌不存在!");

        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        goodsBrandMapper.updateById(model);
    }
}
