package com.niu.mall.service.admin.web.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.entity.web.MallWebFloor;
import com.niu.mall.enums.mall.MallFloorStatusTypeEnum;
import com.niu.mall.mapper.web.MallWebFloorMapper;
import com.niu.mall.service.admin.web.IMallWebFloorService;
import com.niu.mall.service.admin.web.param.MallWebFloorParam;
import com.niu.mall.service.admin.web.param.MallWebFloorSearchParam;
import com.niu.mall.service.admin.web.param.MallWebFloorSortParam;
import com.niu.mall.service.admin.web.vo.MallWebFloorInfoVo;
import com.niu.mall.service.admin.web.vo.MallWebFloorListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;


/**
 * 首页楼层实现
 */
@Service
public class MallWebFloorServiceImpl implements IMallWebFloorService {

    @Resource
    MallWebFloorMapper mallWebFloorMapper;


    @Override
    public PageResult<MallWebFloorListVo> page(PageParam pageParam, MallWebFloorSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<MallWebFloor> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("sort");
        IPage<MallWebFloor> iPage = mallWebFloorMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallWebFloorListVo> list = new LinkedList<>();
        for (MallWebFloor item : iPage.getRecords()) {
            MallWebFloorListVo vo = new MallWebFloorListVo();
            BeanUtils.copyProperties(item, vo);
            if (vo.getStatus() == 1) {
                vo.setStatusName(MallFloorStatusTypeEnum.HIDE.getName());
            }

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public MallWebFloorInfoVo info(Integer id) {
        MallWebFloor model = mallWebFloorMapper.selectOne(
                new QueryWrapper<MallWebFloor>()
                        .eq("id", id));

        Assert.notNull(model, "首页楼层不存在");

        MallWebFloorInfoVo vo = new MallWebFloorInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public Integer add(MallWebFloorParam addParam) {

        MallWebFloor model = new MallWebFloor();

        model.setFloorName(addParam.getFloorName());
        model.setSort(addParam.getSort());
        model.setTemp(addParam.getTemp());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("title", addParam.getTitle());
        configMap.put("sub_title", addParam.getSubTitle());
        configMap.put("goods_ids", ObjectUtil.isNotEmpty(addParam.getGoodsIds()) ? addParam.getGoodsIds() : Collections.emptyList());
        configMap.put("adv_img1", addParam.getAdvImg1());
        configMap.put("adv_img2", addParam.getAdvImg2());
        configMap.put("url", ObjectUtil.isNotEmpty(addParam.getUrl()) ? addParam.getUrl() : new HashMap<>());
        JSONObject config = new JSONObject();
        config.putAll(configMap);
        model.setConfig(config.toString());
        mallWebFloorMapper.insert(model);
        return model.getId();
    }

    @Override
    public void edit(Integer id, MallWebFloorParam editParam) {

        MallWebFloor model = mallWebFloorMapper.selectOne(
                new QueryWrapper<MallWebFloor>()
                        .eq("id", id));
        model.setFloorName(editParam.getFloorName());
        model.setSort(editParam.getSort());
        model.setTemp(editParam.getTemp());
        model.setStatus(editParam.getStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("title", editParam.getTitle());
        configMap.put("sub_title", editParam.getSubTitle());
        configMap.put("goods_ids", ObjectUtil.isNotEmpty(editParam.getGoodsIds()) ? editParam.getGoodsIds() : Collections.emptyList());
        configMap.put("adv_img1", editParam.getAdvImg1());
        configMap.put("adv_img2", editParam.getAdvImg2());
        configMap.put("url", ObjectUtil.isNotEmpty(editParam.getUrl()) ? editParam.getUrl() : new HashMap<>());

        JSONObject config = new JSONObject();
        config.putAll(configMap);
        model.setConfig(config.toString());
        mallWebFloorMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        mallWebFloorMapper.delete(new QueryWrapper<MallWebFloor>().eq("id", id));
    }

    @Override
    public void editSort(MallWebFloorSortParam sortParam) {
        mallWebFloorMapper.update(null, new UpdateWrapper<MallWebFloor>().eq("id", sortParam.getId()).set("sort", sortParam.getSort()));
    }


}
