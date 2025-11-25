package com.niu.core.service.admin.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy.DiyAdv;
import com.niu.core.enums.diy.AdvPositionEnum;
import com.niu.core.mapper.diy.DiyAdvMapper;
import com.niu.core.service.admin.diy.IDiyAdvService;
import com.niu.core.service.admin.diy.param.DiyAdvParam;
import com.niu.core.service.admin.diy.param.DiyAdvSearchParam;
import com.niu.core.service.admin.diy.param.DiyAdvSortParam;
import com.niu.core.service.admin.diy.vo.DiyAdvInfoVo;
import com.niu.core.service.admin.diy.vo.DiyAdvListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 广告实现
 */
@Service
public class DiyAdvServiceImpl implements IDiyAdvService {

    @Resource
    DiyAdvMapper diyAdvMapper;


    @Override
    public PageResult<DiyAdvListVo> page(PageParam pageParam, DiyAdvSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<DiyAdv> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotEmpty(searchParam.getApKey())) {
            queryWrapper.eq("ap_key", searchParam.getApKey());
        }
        queryWrapper.orderByDesc("adv_id");

        IPage<DiyAdv> iPage = diyAdvMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<DiyAdvListVo> list = new LinkedList<>();

        JSONArray advPosition = AdvPositionEnum.getPosition();

        for (DiyAdv item : iPage.getRecords()) {
            DiyAdvListVo vo = new DiyAdvListVo();
            BeanUtils.copyProperties(item, vo);

            // 设置 apName，默认为“未知位置”
            String apName = "";
            if (advPosition != null && !advPosition.isEmpty()) {
                for (Object obj : advPosition) {
                    JSONObject positionItem = (JSONObject) obj;
                    if (positionItem.getStr("keywords").equals(item.getApKey())) {
                        apName = positionItem.getStr("ap_name");
                        break;
                    }
                }
            }
            vo.setApName(apName);
            list.add(vo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }


    @Override
    public List<DiyAdvListVo> list(DiyAdvSearchParam searchParam) {

        QueryWrapper<DiyAdv> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotEmpty(searchParam.getApKey())) {
            queryWrapper.eq("ap_key", searchParam.getApKey());
        }
        queryWrapper.orderByDesc("adv_id");
        List<DiyAdv> list = diyAdvMapper.selectList(queryWrapper);
        List<DiyAdvListVo> listVo = new LinkedList<>();
        for (DiyAdv item : list) {
            DiyAdvListVo vo = new DiyAdvListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;

    }

    @Override
    public DiyAdvInfoVo info(Integer id) {
        // 只查询指定字段
        QueryWrapper<DiyAdv> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("adv_id", "site_id", "ap_key", "adv_title", "adv_url", "adv_image", "slide_sort", "background")
                .eq("adv_id", id)
                .eq("site_id", RequestUtils.siteId());

        DiyAdv model = diyAdvMapper.selectOne(queryWrapper);
        Assert.notNull(model, "广告不存在");
        DiyAdvInfoVo vo = new DiyAdvInfoVo();
        BeanUtils.copyProperties(model, vo);
        // 设置ap_name
        JSONArray advPosition = AdvPositionEnum.getPosition();
        if (advPosition != null && !advPosition.isEmpty()) {
            for (Object obj : advPosition) {
                JSONObject item = (JSONObject) obj;
                if (item.getStr("keywords").equals(model.getApKey())) {
                    vo.setApName(item.getStr("ap_name"));
                    break;
                }
            }
        }

        return vo;
    }

    @Override
    public void add(DiyAdvParam addParam) {

        DiyAdv model = new DiyAdv();
        JSONArray advPosition = AdvPositionEnum.getPosition();
        List<String> advKeys = advPosition.stream()
                .map(obj -> ((JSONObject) obj).getStr("keywords"))
                .collect(Collectors.toList());
        if (!advKeys.contains(addParam.getApKey())) throw new AdminException("广告位置不存在");
//        model.setAdvKey(addParam.getAdvKey());
        model.setApKey(addParam.getApKey());
        model.setAdvTitle(addParam.getAdvTitle());
        model.setAdvUrl(addParam.getAdvUrl().toString());
        model.setAdvImage(addParam.getAdvImage());
//
        model.setSlideSort(addParam.getSlideSort());
        model.setBackground(addParam.getBackground());


        diyAdvMapper.insert(model);
    }

    @Override
    public void edit(Integer id, DiyAdvParam editParam) {

        DiyAdv model = diyAdvMapper.selectOne(
                new QueryWrapper<DiyAdv>()
                        .eq("adv_id", id));
        JSONArray advPosition = AdvPositionEnum.getPosition();
        List<String> advKeys = advPosition.stream()
                .map(obj -> ((JSONObject) obj).getStr("keywords"))
                .collect(Collectors.toList());
        if (!advKeys.contains(editParam.getApKey())) throw new AdminException("广告位置不存在");
        Assert.notNull(model, "广告不存在");
//        model.setAdvKey(editParam.getAdvKey());
        model.setApKey(editParam.getAdvKey());
        model.setAdvTitle(editParam.getAdvTitle());
        model.setAdvUrl(editParam.getAdvUrl().toString());
        model.setAdvImage(editParam.getAdvImage());
//        model.setSort(editParam.getSort());
        model.setSlideSort(editParam.getSlideSort());

        model.setBackground(editParam.getBackground());
        diyAdvMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        diyAdvMapper.delete(new QueryWrapper<DiyAdv>().eq("adv_id", id));
    }

    @Override
    public void editSort(DiyAdvSortParam sortParam) {
        diyAdvMapper.update(null, new UpdateWrapper<DiyAdv>().set("sort", sortParam.getSort()).eq("adv_id", sortParam.getId()));
    }

}
