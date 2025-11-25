package com.niu.core.service.site.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.shop.MemberLabel;
import com.niu.core.mapper.shop.SiteShopMemberLabelMapper;
import com.niu.core.service.site.shop.ISiteShopMemberLabelService;
import com.niu.core.service.site.shop.param.SiteShopMemberLabelEditParam;
import com.niu.core.service.site.shop.param.SiteShopMemberLabelSearchParam;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelAllListVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelInfoVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;


/**
 * 店铺会员标签实现
 */
@Service
public class SiteShopMemberLabelServiceImpl implements ISiteShopMemberLabelService {
        
    @Resource
    SiteShopMemberLabelMapper siteShopMemberLabelMapper;

    /**
     * 店铺会员标签列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberLabelListVo>
     */
    @Override
    public PageResult<SiteShopMemberLabelListVo> list(PageParam pageParam, SiteShopMemberLabelSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();

        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MemberLabel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.orderByAsc ("sort");
        queryWrapper.orderByDesc("label_id");
        if (ObjectUtil.isNotEmpty(searchParam.getLabelName())) queryWrapper.like("label_name", searchParam.getLabelName());

        IPage<MemberLabel> iPage = siteShopMemberLabelMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SiteShopMemberLabelListVo> list = new LinkedList<>();
        for (MemberLabel item : iPage.getRecords()) {
            SiteShopMemberLabelListVo vo = new SiteShopMemberLabelListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 店铺会员标签详情
     * @param id 主键
     * @return MemberLabelInfoVo
     */
    @Override
    public SiteShopMemberLabelInfoVo info(Integer id) {
        Integer siteId = RequestUtils.siteId();

        MemberLabel model = siteShopMemberLabelMapper.selectOne(
                new QueryWrapper<MemberLabel>()
                    .eq("site_id", siteId)
                    .eq("label_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "标签不存在");

        SiteShopMemberLabelInfoVo vo = new SiteShopMemberLabelInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 店铺会员标签添加
     * @param addParam 参数
     */
    @Override
    public void add(SiteShopMemberLabelEditParam addParam) {
        Integer siteId = RequestUtils.siteId();

        MemberLabel model = new MemberLabel();
        model.setSiteId(siteId);
        model.setLabelName(addParam.getLabelName());
        model.setMemo(addParam.getMemo());
        model.setSort(addParam.getSort());
        model.setCreateTime(System.currentTimeMillis() / 1000);

        siteShopMemberLabelMapper.insert(model);
    }

    /**
     * 店铺会员标签编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, SiteShopMemberLabelEditParam editParam) {
        Integer siteId = RequestUtils.siteId();

        UpdateWrapper<MemberLabel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId)
                .eq("label_id",  id);

        MemberLabel model = new MemberLabel();
        model.setLabelName(editParam.getLabelName());
        model.setMemo(editParam.getMemo());
        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        siteShopMemberLabelMapper.update(model, updateWrapper);
    }

    /**
     * 店铺会员标签删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Integer siteId = RequestUtils.siteId();

        QueryWrapper<MemberLabel> queryWrapper = new QueryWrapper<MemberLabel>()
                .eq("site_id", siteId)
                .eq("label_id", id);

        siteShopMemberLabelMapper.delete(queryWrapper);
    }

    /**
     * 查询全部店铺会员标签
     * @return
     */
    @Override
    public List<SiteShopMemberLabelAllListVo> all() {
        Integer siteId = RequestUtils.siteId();

        MPJQueryWrapper<MemberLabel> queryWrapper = new MPJQueryWrapper<MemberLabel>();
        queryWrapper.select("label_id,label_name").eq("site_id", siteId);

        List<MemberLabel> labels = siteShopMemberLabelMapper.selectList(queryWrapper); // 调用 selectList 方法

        List<SiteShopMemberLabelAllListVo> list = new LinkedList<>();
        for (MemberLabel item : labels) {
            SiteShopMemberLabelAllListVo vo = new SiteShopMemberLabelAllListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }
}
