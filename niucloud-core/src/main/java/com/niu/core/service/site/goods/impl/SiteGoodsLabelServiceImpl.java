package com.niu.core.service.site.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.goods.GoodsLabel;
import com.niu.core.mapper.goods.GoodsLabelMapper;
import com.niu.core.service.admin.goods.param.GoodsLabelSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsLabelListVo;
import com.niu.core.service.site.goods.IGoodsLabelService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * 商品标签服务实现（站点端）
 * 对应 PHP: app\service\admin\basic\site\LabelService
 */
@Service("siteGoodsLabelService")
public class SiteGoodsLabelServiceImpl implements IGoodsLabelService {

    private static final int STATUS_ENABLE = 1;

    @Resource
    private GoodsLabelMapper goodsLabelMapper;

    @Override
    public List<GoodsLabelListVo> list(GoodsLabelSearchParam searchParam) {
        QueryWrapper<GoodsLabel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", STATUS_ENABLE);
        if (searchParam != null && ObjectUtil.isNotEmpty(searchParam.getLabelName())) {
            queryWrapper.like("label_name", searchParam.getLabelName());
        }
        queryWrapper.orderByAsc("sort").orderByDesc("create_time");

        List<GoodsLabel> records = goodsLabelMapper.selectList(queryWrapper);
        List<GoodsLabelListVo> list = new LinkedList<>();
        for (GoodsLabel item : records) {
            GoodsLabelListVo vo = new GoodsLabelListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }
}
