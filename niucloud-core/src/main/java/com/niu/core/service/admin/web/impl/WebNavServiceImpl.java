package com.niu.core.service.admin.web.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.web.WebNav;
import com.niu.core.mapper.web.WebNavMapper;
import com.niu.core.service.admin.web.IWebNavService;
import com.niu.core.service.admin.web.param.WebNavParam;
import com.niu.core.service.admin.web.param.WebNavSearchParam;
import com.niu.core.service.admin.web.param.WebNavSortParam;
import com.niu.core.service.admin.web.vo.WebNavInfoVo;
import com.niu.core.service.admin.web.vo.WebNavListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;


/**
 * PC导航管理实现
 */
@Service
public class WebNavServiceImpl implements IWebNavService {
        
    @Resource
    WebNavMapper webNavMapper;


    @Override
    public PageResult<WebNavListVo> page(PageParam pageParam, WebNavSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<WebNav> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotEmpty(searchParam.getNavTitle())) {
        queryWrapper.eq("nav_title", searchParam.getNavTitle());
        }

        queryWrapper.orderByDesc("id");

        IPage<WebNav> iPage = webNavMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<WebNavListVo> list = new LinkedList<>();
        for (WebNav item : iPage.getRecords()) {
            WebNavListVo vo = new WebNavListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<WebNavListVo> list(WebNavSearchParam searchParam) {
        QueryWrapper<WebNav> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotEmpty(searchParam.getNavTitle())) {
            queryWrapper.eq("nav_title", searchParam.getNavTitle());
        }
        queryWrapper.eq("is_show", 1);
        queryWrapper.orderByDesc("sort");

        List<WebNav> list = webNavMapper.selectList(queryWrapper);
        List<WebNavListVo> listVo = new LinkedList<>();
        for (WebNav item : list) {
            WebNavListVo vo = new WebNavListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }

    @Override
    public WebNavInfoVo info(Integer id) {
        WebNav model = webNavMapper.selectOne(
        new QueryWrapper<WebNav>()
        .eq("id", id));

        Assert.notNull(model, "PC导航管理不存在");

        WebNavInfoVo vo = new WebNavInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public void add(WebNavParam addParam) {

        WebNav model = new WebNav();

        model.setNavTitle(addParam.getNavTitle());
        model.setNavUrl(addParam.getNavUrl().toString());
        model.setSort(addParam.getSort());
        model.setIsBlank(addParam.getIsBlank());
        model.setIsShow(addParam.getIsShow());


        webNavMapper.insert(model);
    }

    @Override
    public void edit(Integer id, WebNavParam editParam) {

        WebNav model = webNavMapper.selectOne(
                new QueryWrapper<WebNav>()
                        .eq("id", id));

        Assert.notNull(model, "PC导航管理不存在");
        model.setNavTitle(editParam.getNavTitle());
        model.setNavUrl(editParam.getNavUrl().toString());
        model.setSort(editParam.getSort());
        model.setIsBlank(editParam.getIsBlank());
        model.setIsShow(editParam.getIsShow());
        webNavMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        webNavMapper.delete(new QueryWrapper<WebNav>().eq("id", id));
    }

    @Override
    public void editSort(WebNavSortParam sortParam) {
        webNavMapper.update(null, new UpdateWrapper<WebNav>()
                .set("sort", sortParam.getSort())
                .eq("id", sortParam.getId()));
    }

}
