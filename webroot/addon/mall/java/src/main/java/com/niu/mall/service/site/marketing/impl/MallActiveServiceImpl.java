package com.niu.mall.service.site.marketing.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.entity.active.MallActive;
import com.niu.mall.mapper.active.MallActiveMapper;
import com.niu.mall.service.site.marketing.IMallActiveService;
import com.niu.mall.service.site.marketing.param.MallActiveParam;
import com.niu.mall.service.site.marketing.param.MallDiscountSearchParam;
import com.niu.mall.service.site.marketing.vo.MallDiscountInfoVo;
import com.niu.mall.service.site.marketing.vo.MallDiscountListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 店铺营销活动（整体活动）实现
 */
@Service
public class MallActiveServiceImpl extends ServiceImpl<MallActiveMapper, MallActive> implements IMallActiveService {

    @Resource
    MallActiveMapper mallActiveMapper;

    /**
     * 店铺营销活动（整体活动）列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopActiveListVo>
     */
    @Override
    public PageResult<MallDiscountListVo> list(PageParam pageParam, MallDiscountSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallActive> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("active_id");

        IPage<MallActive> iPage = mallActiveMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallDiscountListVo> list = new LinkedList<>();
        for (MallActive item : iPage.getRecords()) {
            MallDiscountListVo vo = new MallDiscountListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 店铺营销活动（整体活动）详情
     *
     * @param id 主键
     * @return ShopActiveInfoVo
     */
    @Override
    public MallDiscountInfoVo info(Integer id) {
        MallActive model = mallActiveMapper.selectOne(
                new QueryWrapper<MallActive>()
                        .eq("active_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        MallDiscountInfoVo vo = new MallDiscountInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 店铺营销活动（整体活动）添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(MallActiveParam addParam) {
        MallActive model = new MallActive();
        model.setSiteId(addParam.getSiteId());
        model.setActiveName(addParam.getActiveName());
        model.setActiveDesc(addParam.getActiveDesc());
        model.setActiveType(addParam.getActiveType());
        model.setActiveGoodsType(addParam.getActiveGoodsType());
        model.setActiveGoodsInfo(addParam.getActiveGoodsInfo());
        model.setActiveClass(addParam.getActiveClass());
        model.setActiveClassCategory(addParam.getActiveClassCategory());
        model.setRelateMember(addParam.getRelateMember());
        model.setActiveValue(addParam.getActiveValue());
        model.setStartTime(System.currentTimeMillis() / 1000);
        model.setEndTime(System.currentTimeMillis() / 1000);
        model.setActiveStatus(addParam.getActiveStatus());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setActiveOrderMoney(addParam.getActiveOrderMoney());
        model.setActiveOrderNum(addParam.getActiveOrderNum());
        model.setActiveMemberNum(addParam.getActiveMemberNum());
        model.setActiveSuccessNum(addParam.getActiveSuccessNum());
        mallActiveMapper.insert(model);
    }

    /**
     * 店铺营销活动（整体活动）编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MallActiveParam editParam) {
        MallActive model = mallActiveMapper.selectOne(
                new QueryWrapper<MallActive>()
                        .eq("active_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setActiveId(id);
        model.setActiveId(editParam.getActiveId());
        model.setSiteId(editParam.getSiteId());
        model.setActiveName(editParam.getActiveName());
        model.setActiveDesc(editParam.getActiveDesc());
        model.setActiveType(editParam.getActiveType());
        model.setActiveGoodsType(editParam.getActiveGoodsType());
        model.setActiveGoodsInfo(editParam.getActiveGoodsInfo());
        model.setActiveClass(editParam.getActiveClass());
        model.setActiveClassCategory(editParam.getActiveClassCategory());
        model.setRelateMember(editParam.getRelateMember());
        model.setActiveValue(editParam.getActiveValue());
        model.setActiveStatus(editParam.getActiveStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setActiveOrderMoney(editParam.getActiveOrderMoney());
        model.setActiveOrderNum(editParam.getActiveOrderNum());
        model.setActiveMemberNum(editParam.getActiveMemberNum());
        model.setActiveSuccessNum(editParam.getActiveSuccessNum());
        mallActiveMapper.updateById(model);
    }

    /**
     * 店铺营销活动（整体活动）删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        MallActive model = mallActiveMapper.selectOne(
                new QueryWrapper<MallActive>()
                        .eq("active_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        mallActiveMapper.delete(new QueryWrapper<MallActive>().eq("active_id", id));
    }

}
