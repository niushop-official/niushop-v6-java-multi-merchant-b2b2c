package com.niu.mall.service.api.web.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.api.goods.param.MallGoodsComponentsSearchParam;
import com.niu.mall.entity.web.MallWebFloor;
import com.niu.mall.mapper.web.MallWebFloorMapper;
import com.niu.mall.service.api.web.IMallWebFloorService;
import com.niu.mall.service.api.web.vo.MallWebFloorListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 首页楼层实现
 */
@Service
public class MallWebFloorServiceImpl implements IMallWebFloorService {
        
    @Resource
    MallWebFloorMapper mallWebFloorMapper;

    @Resource
    IGoodsService goodsService;


    @Override
    public PageResult<MallWebFloorListVo> list(PageParam pageParam) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<MallWebFloor> queryWrapper = new QueryWrapper<>();

        queryWrapper.orderByAsc("sort");
        queryWrapper.orderByDesc("update_time");

        IPage<MallWebFloor> iPage = mallWebFloorMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallWebFloorListVo> listVo = new LinkedList<>();
        for (MallWebFloor item : iPage.getRecords()) {
            MallWebFloorListVo vo = new MallWebFloorListVo();
            BeanUtils.copyProperties(item, vo);

            JSONObject config = JSONUtil.parseObj(item.getConfig());
            if (ObjectUtil.isNotEmpty(config.get("goods_ids"))) {
                MallGoodsComponentsSearchParam param = new MallGoodsComponentsSearchParam();
                Integer[] goodsIds = new Integer[config.getJSONArray("goods_ids").size()];
                for (int i = 0; i < config.getJSONArray("goods_ids").size(); i++) {
                    goodsIds[i] = Integer.parseInt(config.getJSONArray("goods_ids").get(i).toString());
                }
                param.setGoodsIds(goodsIds);
                vo.setGoodsList(goodsService.getGoodsComponents(param));
            }
            listVo.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(listVo);
    }

}
