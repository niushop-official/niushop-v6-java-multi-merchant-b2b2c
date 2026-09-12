package com.niu.core.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsBadge;
import com.niu.core.event.goods.CheckBadgeInUseEventDefiner;
import com.niu.core.mapper.goods.GoodsBadgeMapper;
import com.niu.core.service.admin.goods.IGoodsBadgeService;
import com.niu.core.service.admin.goods.param.EditGoodsBadgeSortParam;
import com.niu.core.service.admin.goods.param.EditGoodsBadgeStatusParam;
import com.niu.core.service.admin.goods.param.GoodsBadgeParam;
import com.niu.core.service.admin.goods.param.GoodsBadgeSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsBadgeInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsBadgeListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

/**
 * 商品角标服务实现
 * 对应 PHP: app\service\admin\basic\admin\BadgeService
 */
@Service
public class GoodsBadgeServiceImpl implements IGoodsBadgeService {

    private static final int STATUS_ENABLE = 1;
    private static final int STATUS_CLOSE = 0;

    @Resource
    private GoodsBadgeMapper goodsBadgeMapper;

    @Override
    public PageResult<GoodsBadgeListVo> page(PageParam pageParam, GoodsBadgeSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<GoodsBadge> queryWrapper = baseQuery();
        if (searchParam != null) {
            if (ObjectUtil.isNotEmpty(searchParam.getBadgeName())) {
                queryWrapper.like("badge_name", searchParam.getBadgeName());
            }
            if (searchParam.getGroupId() != null && searchParam.getGroupId() > 0) {
                queryWrapper.eq("group_id", searchParam.getGroupId());
            }
            if (ObjectUtil.isNotEmpty(searchParam.getOrder()) && ObjectUtil.isNotEmpty(searchParam.getSort())) {
                boolean isAsc = "asc".equalsIgnoreCase(searchParam.getSort());
                queryWrapper.orderBy(true, isAsc, searchParam.getOrder());
            } else {
                queryWrapper.orderByDesc("sort").orderByDesc("badge_id");
            }
        } else {
            queryWrapper.orderByDesc("sort").orderByDesc("badge_id");
        }

        IPage<GoodsBadge> iPage = goodsBadgeMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<GoodsBadgeListVo> list = new LinkedList<>();
        for (GoodsBadge item : iPage.getRecords()) {
            GoodsBadgeListVo vo = new GoodsBadgeListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<GoodsBadgeListVo> list(GoodsBadgeSearchParam searchParam) {
        QueryWrapper<GoodsBadge> queryWrapper = baseQuery();
        queryWrapper.eq("status", STATUS_ENABLE);
        if (searchParam != null && ObjectUtil.isNotEmpty(searchParam.getBadgeName())) {
            queryWrapper.like("badge_name", searchParam.getBadgeName());
        }
        queryWrapper.orderByDesc("sort").orderByDesc("badge_id");

        List<GoodsBadge> records = goodsBadgeMapper.selectList(queryWrapper);
        List<GoodsBadgeListVo> list = new LinkedList<>();
        for (GoodsBadge item : records) {
            GoodsBadgeListVo vo = new GoodsBadgeListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    @Override
    public GoodsBadgeInfoVo info(Integer id) {
        GoodsBadge model = goodsBadgeMapper.selectOne(baseQuery().eq("badge_id", id));
        Assert.notNull(model, "角标不存在");

        GoodsBadgeInfoVo vo = new GoodsBadgeInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public Integer add(GoodsBadgeParam addParam) {
        Long count = goodsBadgeMapper.selectCount(baseQuery().eq("badge_name", addParam.getBadgeName()));
        if (count != null && count > 0) {
            throw new CommonException("角标名称重复，请检查");
        }

        GoodsBadge model = new GoodsBadge();
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
        goodsBadgeMapper.insert(model);
        return model.getBadgeId();
    }

    @Override
    public void edit(Integer id, GoodsBadgeParam editParam) {
        GoodsBadge exist = goodsBadgeMapper.selectOne(baseQuery()
                .eq("badge_name", editParam.getBadgeName())
                .ne("badge_id", id)
                .last("limit 1"));
        if (exist != null) {
            throw new CommonException("角标名称重复，请检查");
        }

        GoodsBadge model = goodsBadgeMapper.selectOne(baseQuery().eq("badge_id", id));
        Assert.notNull(model, "角标不存在");

        model.setBadgeName(editParam.getBadgeName());
        model.setGroupId(ObjectUtil.defaultIfNull(editParam.getGroupId(), 0));
        model.setStyleType(StrUtil.blankToDefault(editParam.getStyleType(), ""));
        model.setColorJson(editParam.getColorJson() == null ? "{}" : editParam.getColorJson().toString());
        model.setIcon(StrUtil.blankToDefault(editParam.getIcon(), ""));
        model.setStatus(ObjectUtil.defaultIfNull(editParam.getStatus(), model.getStatus()));
        model.setMemo(StrUtil.blankToDefault(editParam.getMemo(), ""));
        model.setSort(ObjectUtil.defaultIfNull(editParam.getSort(), 0));
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        goodsBadgeMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        assertBadgeNotInUse(id);
        goodsBadgeMapper.delete(baseQuery().eq("badge_id", id));
    }

    /**
     * 删除前通过插件回调校验是否被商品引用（如 fds 的 ns_fds_goods.badge_id）
     */
    private void assertBadgeNotInUse(Integer badgeId) {
        CheckBadgeInUseEventDefiner.CheckBadgeInUseEvent event = new CheckBadgeInUseEventDefiner.CheckBadgeInUseEvent();
        event.setBadgeId(badgeId);
        event.setSiteId(RequestUtils.defaultSiteId());
        List<CheckBadgeInUseEventDefiner.CheckBadgeInUseResult> results =
                EventAndSubscribeOfPublisher.publishAndCallback(event);
        for (CheckBadgeInUseEventDefiner.CheckBadgeInUseResult result : results) {
            if (result != null && result.getCount() != null && result.getCount() > 0) {
                throw new AdminException(StrUtil.blankToDefault(result.getMessage(), "该角标存在关联商品，无法删除"));
            }
        }
    }

    @Override
    public void modifySort(EditGoodsBadgeSortParam editParam) {
        GoodsBadge model = goodsBadgeMapper.selectOne(baseQuery().eq("badge_id", editParam.getBadgeId()));
        Assert.notNull(model, "角标不存在");
        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        goodsBadgeMapper.updateById(model);
    }

    @Override
    public void modifyStatus(EditGoodsBadgeStatusParam editParam) {
        GoodsBadge model = goodsBadgeMapper.selectOne(baseQuery().eq("badge_id", editParam.getBadgeId()));
        Assert.notNull(model, "角标不存在");
        model.setStatus(editParam.getStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        goodsBadgeMapper.updateById(model);
    }

    @Override
    public void copy(Integer id) {
        GoodsBadge model = goodsBadgeMapper.selectOne(baseQuery().eq("badge_id", id));
        Assert.notNull(model, "角标不存在");

        GoodsBadge copy = new GoodsBadge();
        copy.setSiteId(RequestUtils.defaultSiteId());
        copy.setBadgeName(model.getBadgeName() + "_副本");
        copy.setGroupId(ObjectUtil.defaultIfNull(model.getGroupId(), 0));
        copy.setStyleType(StrUtil.blankToDefault(model.getStyleType(), ""));
        copy.setColorJson(StrUtil.blankToDefault(model.getColorJson(), "{}"));
        copy.setIcon(StrUtil.blankToDefault(model.getIcon(), ""));
        copy.setStatus(STATUS_CLOSE);
        copy.setMemo(StrUtil.blankToDefault(model.getMemo(), ""));
        copy.setSort(ObjectUtil.defaultIfNull(model.getSort(), 0));
        copy.setCreateTime(System.currentTimeMillis() / 1000);
        copy.setUpdateTime(0L);
        goodsBadgeMapper.insert(copy);
    }

    private QueryWrapper<GoodsBadge> baseQuery() {
        return new QueryWrapper<GoodsBadge>().eq("site_id", RequestUtils.defaultSiteId());
    }
}
