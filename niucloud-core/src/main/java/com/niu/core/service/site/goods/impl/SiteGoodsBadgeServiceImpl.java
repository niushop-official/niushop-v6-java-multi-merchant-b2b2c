package com.niu.core.service.site.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.goods.GoodsBadge;
import com.niu.core.mapper.goods.GoodsBadgeMapper;
import com.niu.core.service.admin.goods.param.GoodsBadgeSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsBadgeListVo;
import com.niu.core.service.site.goods.IGoodsBadgeService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * 商品角标服务实现（站点端）
 * 对应 PHP: app\service\admin\basic\site\BadgeService
 */
@Service
public class SiteGoodsBadgeServiceImpl implements IGoodsBadgeService {

    private static final int STATUS_ENABLE = 1;

    @Resource
    private GoodsBadgeMapper goodsBadgeMapper;

    @Override
    public List<GoodsBadgeListVo> list(GoodsBadgeSearchParam searchParam) {
        QueryWrapper<GoodsBadge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", STATUS_ENABLE);
        if (searchParam != null && ObjectUtil.isNotEmpty(searchParam.getBadgeName())) {
            queryWrapper.like("badge_name", searchParam.getBadgeName());
        }
        queryWrapper.orderByAsc("sort").orderByDesc("create_time");

        List<GoodsBadge> records = goodsBadgeMapper.selectList(queryWrapper);
        List<GoodsBadgeListVo> list = new LinkedList<>();
        for (GoodsBadge item : records) {
            GoodsBadgeListVo vo = new GoodsBadgeListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }
}
