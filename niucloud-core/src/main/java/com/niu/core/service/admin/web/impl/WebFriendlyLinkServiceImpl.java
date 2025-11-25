package com.niu.core.service.admin.web.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.web.WebFriendlyLink;
import com.niu.core.mapper.web.WebFriendlyLinkMapper;
import com.niu.core.service.admin.web.IWebFriendlyLinkService;
import com.niu.core.service.admin.web.param.WebFriendlyLinkParam;
import com.niu.core.service.admin.web.param.WebFriendlyLinkSearchParam;
import com.niu.core.service.admin.web.param.WebFriendlyLinkSortParam;
import com.niu.core.service.admin.web.vo.WebFriendlyLinkInfoVo;
import com.niu.core.service.admin.web.vo.WebFriendlyLinkListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;


/**
 * 友情链接实现
 */
@Service
public class WebFriendlyLinkServiceImpl implements IWebFriendlyLinkService {
        
    @Resource
    WebFriendlyLinkMapper webFriendlyLinkMapper;


    @Override
    public PageResult<WebFriendlyLinkListVo> page(PageParam pageParam, WebFriendlyLinkSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<WebFriendlyLink> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotEmpty(searchParam.getLinkTitle())) {
        queryWrapper.eq("link_title", searchParam.getLinkTitle());
        }
        queryWrapper.orderByDesc("id");

        IPage<WebFriendlyLink> iPage = webFriendlyLinkMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<WebFriendlyLinkListVo> list = new LinkedList<>();
        for (WebFriendlyLink item : iPage.getRecords()) {
            WebFriendlyLinkListVo vo = new WebFriendlyLinkListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<WebFriendlyLinkListVo> list(WebFriendlyLinkSearchParam searchParam) {
        QueryWrapper<WebFriendlyLink> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getLinkTitle())) {
            queryWrapper.eq("link_title", searchParam.getLinkTitle());
        }
        queryWrapper.orderByDesc("id");
        List<WebFriendlyLink> list = webFriendlyLinkMapper.selectList(queryWrapper);
        List<WebFriendlyLinkListVo> listVo = new LinkedList<>();
        for (WebFriendlyLink item : list) {
            WebFriendlyLinkListVo vo = new WebFriendlyLinkListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }

    @Override
    public WebFriendlyLinkInfoVo info(Integer id) {
        WebFriendlyLink model = webFriendlyLinkMapper.selectOne(
        new QueryWrapper<WebFriendlyLink>()
        .eq("id", id));

        Assert.notNull(model, "友情链接不存在");

        WebFriendlyLinkInfoVo vo = new WebFriendlyLinkInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public void add(WebFriendlyLinkParam addParam) {

        WebFriendlyLink model = new WebFriendlyLink();

        model.setLinkTitle(addParam.getLinkTitle());
        model.setLinkUrl(addParam.getLinkUrl().toString());
        model.setLinkPic(addParam.getLinkPic());
        model.setLinkSort(addParam.getLinkSort());
        model.setIsShow(addParam.getIsShow());


        webFriendlyLinkMapper.insert(model);
    }

    @Override
    public void edit(Integer id, WebFriendlyLinkParam editParam) {

        WebFriendlyLink model = webFriendlyLinkMapper.selectOne(
                new QueryWrapper<WebFriendlyLink>()
                        .eq("id", id));

        Assert.notNull(model, "友情链接不存在");
        model.setLinkTitle(editParam.getLinkTitle());
        model.setLinkUrl(editParam.getLinkUrl().toString());
        model.setLinkPic(editParam.getLinkPic());
        model.setLinkSort(editParam.getLinkSort());
        model.setIsShow(editParam.getIsShow());
        webFriendlyLinkMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        webFriendlyLinkMapper.delete(new QueryWrapper<WebFriendlyLink>().eq("id", id));
    }

    @Override
    public void editSort(WebFriendlyLinkSortParam sortParam) {
        webFriendlyLinkMapper.update(null, new UpdateWrapper<WebFriendlyLink>().eq("id", sortParam.getId()).set("link_sort", sortParam.getLinkSort()));

    }

}
