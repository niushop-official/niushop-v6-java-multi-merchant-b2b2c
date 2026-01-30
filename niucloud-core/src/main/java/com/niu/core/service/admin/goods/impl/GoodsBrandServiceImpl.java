package com.niu.core.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsBrand;
import com.niu.core.enums.goods.BrandAuditEnum;
import com.niu.core.enums.goods.BrandPopularEnum;
import com.niu.core.enums.goods.BrandStatusEnum;
import com.niu.core.enums.goods.BrandTypeEnum;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.service.admin.goods.param.*;
import com.niu.core.service.admin.goods.IGoodsBrandService;
import com.niu.core.service.admin.goods.vo.GoodsBrandInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsBrandInitVo;
import com.niu.core.service.admin.goods.vo.GoodsBrandListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;

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
    public GoodsBrandInitVo init() {
        GoodsBrandInitVo vo = new GoodsBrandInitVo();
        vo.setStatusList(JSONUtil.parseObj(BrandStatusEnum.getMap()));
        vo.setIsHotList(JSONUtil.parseObj(BrandPopularEnum.getMap()));
        vo.setAuditStatusList(JSONUtil.parseObj(BrandAuditEnum.getMap()));
        return vo;
    }

    /**
     * 商品品牌分页列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsBrandListVo>
     */
    @Override
    public PageResult<GoodsBrandListVo> page(PageParam pageParam, GoodsBrandSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<GoodsBrand> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ngb")
                .leftJoin("?_site ns ON ngb.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .select("ngb.brand_id, ngb.site_id, ngb.brand_name, ngb.logo, ngb.color_json, ngb.`desc`, ngb.sort, ngb.create_time, ngb.update_time, ngb.delete_time, ngb.brand_type, ns.site_name, ns.front_end_logo,ngb.status,ngb.is_hot");

        queryWrapper.eq("ngb.audit_status", BrandAuditEnum.AUDIT_PASS.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus()))   queryWrapper.eq("ngb.status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getIsHot()))   queryWrapper.eq("ngb.is_hot", searchParam.getIsHot());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandName())) queryWrapper.like("ngb.brand_name", searchParam.getBrandName());
        queryWrapper.orderByDesc("ngb.brand_id");
        if (ObjectUtil.isNotEmpty(searchParam.getOrder())){
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        }
        IPage<GoodsBrandListVo> iPage = goodsBrandMapper.selectJoinPage(new Page<>(page, limit), GoodsBrandListVo.class, queryWrapper);
        for (GoodsBrandListVo item : iPage.getRecords()) {
            GoodsBrandListVo.SiteInfoVO siteInfoVO = new GoodsBrandListVo.SiteInfoVO();
            siteInfoVO.setSiteId(item.getSiteId());
            siteInfoVO.setSiteName(item.getSiteName());
            siteInfoVO.setFrontEndLogo(item.getFrontEndLogo());
            item.setSite(siteInfoVO);
        }
        return PageResult.build(iPage);
    }

    @Override
    public PageResult<GoodsBrandListVo> getAuditPage(PageParam pageParam, GoodsBrandSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<GoodsBrand> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ngb")
                .leftJoin("?_site ns ON ngb.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .select("ngb.brand_id, ngb.site_id, ngb.brand_name, ngb.logo, ngb.color_json, ngb.`desc`, ngb.sort, ngb.create_time, ngb.update_time, ngb.delete_time, ngb.brand_type, ns.site_name, ns.front_end_logo,ngb.status,ngb.is_hot,ngb.audit_status,ngb.audit_reason,ngb.audit_time");
        queryWrapper.gt("ngb.site_id", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getStatus()))   queryWrapper.eq("ngb.status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getAuditStatus()))   queryWrapper.eq("ngb.audit_status", searchParam.getAuditStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getIsHot()))   queryWrapper.eq("ngb.is_hot", searchParam.getIsHot());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandName())) queryWrapper.like("ngb.brand_name", searchParam.getBrandName());
        queryWrapper.orderByDesc("ngb.brand_id");
        if (ObjectUtil.isNotEmpty(searchParam.getOrder())){
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        }
        IPage<GoodsBrandListVo> iPage = goodsBrandMapper.selectJoinPage(new Page<>(page, limit), GoodsBrandListVo.class, queryWrapper);
        for (GoodsBrandListVo item : iPage.getRecords()) {
            GoodsBrandListVo.SiteInfoVO siteInfoVO = new GoodsBrandListVo.SiteInfoVO();
            siteInfoVO.setSiteId(item.getSiteId());
            siteInfoVO.setSiteName(item.getSiteName());
            siteInfoVO.setFrontEndLogo(item.getFrontEndLogo());
            item.setSite(siteInfoVO);
        }
        return PageResult.build(iPage);
    }

    /**
     * 商品品牌列表
     *
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsBrandListVo>
     */
    @Override
    public List<GoodsBrandListVo> list(GoodsBrandSearchParam searchParam) {
        QueryWrapper<GoodsBrand> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getBrandName())) queryWrapper.like("brand_name", searchParam.getBrandName());
        queryWrapper.orderByDesc("create_time");

        List<GoodsBrand> record = goodsBrandMapper.selectList(queryWrapper);
        List<GoodsBrandListVo> list = new LinkedList<>();
        for (GoodsBrand item : record) {
            GoodsBrandListVo vo = new GoodsBrandListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 商品品牌详情
     *
     * @param id 主键
     * @return ShopGoodsBrandInfoVo
     */
    @Override
    public GoodsBrandInfoVo info(Integer id) {
        GoodsBrand model = goodsBrandMapper.selectOne(
                new QueryWrapper<GoodsBrand>()
                    .eq("brand_id", id));

        Assert.notNull(model, "品牌不存在");

        GoodsBrandInfoVo vo = new GoodsBrandInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 商品品牌添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(GoodsBrandParam addParam) {
        Long count = goodsBrandMapper.selectCount(new QueryWrapper<GoodsBrand>()
                .eq("brand_name", addParam.getBrandName()));
        if (count > 0) throw new CommonException("品牌已存在");

        GoodsBrand model = new GoodsBrand();

        model.setSiteId(RequestUtils.siteId());
        model.setBrandName(addParam.getBrandName());
        model.setBrandType(BrandTypeEnum.ADMIN.getType());
        model.setAuditStatus(BrandAuditEnum.AUDIT_PASS.getStatus());
        model.setLogo(addParam.getLogo());
        model.setDesc(addParam.getDesc());
        model.setSort(ObjectUtil.isNotEmpty(addParam.getSort()) ? Integer.parseInt(addParam.getSort()) : 0);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setColorJson(JSONUtil.toJsonStr(ObjectUtil.defaultIfNull(addParam.getColorJson(), new JSONObject())));
        goodsBrandMapper.insert(model);
    }

    /**
     * 商品品牌编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, GoodsBrandParam editParam) {
        Long count = goodsBrandMapper.selectCount(new QueryWrapper<GoodsBrand>()
                .eq("brand_name", editParam.getBrandName())
                .ne("brand_id", id));
        if (count > 0) throw new CommonException("品牌已存在");

        GoodsBrand model = goodsBrandMapper.selectOne(
                new QueryWrapper<GoodsBrand>()
                    .eq("brand_id",  id));

        Assert.notNull(model, "品牌不存在!");

        model.setBrandName(editParam.getBrandName());
        model.setLogo(editParam.getLogo());
        model.setDesc(editParam.getDesc());
        model.setSort(ObjectUtil.isNotEmpty(editParam.getSort()) ? Integer.parseInt(editParam.getSort()) : 0);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setAuditStatus(BrandAuditEnum.AUDIT_PASS.getStatus());
        model.setColorJson(JSONUtil.toJsonStr(ObjectUtil.defaultIfNull(editParam.getColorJson(), new JSONObject())));
        goodsBrandMapper.updateById(model);
    }

    /**
     * 商品品牌删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        goodsBrandMapper.delete(new QueryWrapper<GoodsBrand>().eq("brand_id", id));
    }

    /**
     * 商品品牌编辑
     *
     * @param editParam 参数
     */
    @Override
    public void modifySort(EditGoodsBrandSortParam editParam) {
        GoodsBrand model = goodsBrandMapper.selectOne(
                new QueryWrapper<GoodsBrand>()
                        .eq("brand_id",  editParam.getBrandId()));

        Assert.notNull(model, "品牌不存在!");

        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        goodsBrandMapper.updateById(model);
    }

    @Override
    public void move(Integer brandId) {
        GoodsBrand model = new GoodsBrand();
        model.setBrandType(BrandTypeEnum.ADMIN.getType());
        model.setAuditStatus(BrandAuditEnum.AUDIT_PASS.getStatus());
        goodsBrandMapper.update(model, new QueryWrapper<GoodsBrand>().eq("brand_id", brandId));
    }

    @Override
    public void audit(GoodsBrandAuditParam auditParam) {
        GoodsBrand brand = goodsBrandMapper.selectOne(
                new QueryWrapper<GoodsBrand>()
                        .eq("brand_id",  auditParam.getBrandId()));

        Assert.notNull(brand, "品牌不存在!");
        GoodsBrand model = new GoodsBrand();
        model.setBrandId(brand.getBrandId());
        if (auditParam.getAuditStatus().equals(BrandAuditEnum.AUDIT_PASS.getStatus())){
            model.setAuditReason("");
        }else{
            model.setAuditReason(auditParam.getAuditReason());
        }
        model.setAuditStatus(auditParam.getAuditStatus());
        model.setAuditTime(System.currentTimeMillis() / 1000);
        goodsBrandMapper.updateById( model);
    }

    @Override
    public void modifyStatus(GoodsBrandStatusParam editParam) {
        GoodsBrand model = goodsBrandMapper.selectOne(
                new QueryWrapper<GoodsBrand>()
                        .eq("brand_id",  editParam.getBrandId()));

        Assert.notNull(model, "品牌不存在!");

        model.setStatus(editParam.getStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        goodsBrandMapper.updateById(model);
    }

    @Override
    public void modifyHot(GoodsBrandIsHotParam isHotParam) {
        GoodsBrand model = goodsBrandMapper.selectOne(
                new QueryWrapper<GoodsBrand>()
                        .eq("brand_id",  isHotParam.getBrandId()));

        Assert.notNull(model, "品牌不存在!");

        model.setIsHot(isHotParam.getIsHot());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        goodsBrandMapper.updateById(model);
    }
}
