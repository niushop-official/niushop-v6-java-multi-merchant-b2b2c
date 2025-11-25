package com.niu.mall.service.site.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.mall.entity.order.MallOrderDiscount;
import com.niu.mall.mapper.order.MallOrderDiscountMapper;
import com.niu.mall.service.site.order.param.MallOrderDiscountParam;
import com.niu.mall.service.site.order.param.MallOrderDiscountSearchParam;
import com.niu.mall.service.site.order.IMallOrderDiscountService;
import com.niu.mall.service.site.order.vo.MallOrderDiscountInfoVo;
import com.niu.mall.service.site.order.vo.MallOrderDiscountListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 订单优惠实现
 */
@Service
public class MallOrderDiscountServiceImpl implements IMallOrderDiscountService {
        
    @Resource
    MallOrderDiscountMapper mallOrderDiscountMapper;

    /**
     * 订单优惠列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderDiscountListVo>
     */
    @Override
    public PageResult<MallOrderDiscountListVo> list(PageParam pageParam, MallOrderDiscountSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MallOrderDiscount> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<MallOrderDiscount> iPage = mallOrderDiscountMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallOrderDiscountListVo> list = new LinkedList<>();
        for (MallOrderDiscount item : iPage.getRecords()) {
            MallOrderDiscountListVo vo = new MallOrderDiscountListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 订单优惠详情
     * @param id 主键
     * @return ShopOrderDiscountInfoVo
     */
    @Override
    public MallOrderDiscountInfoVo info(Integer id) {
        MallOrderDiscount model = mallOrderDiscountMapper.selectOne(
                new QueryWrapper<MallOrderDiscount>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        MallOrderDiscountInfoVo vo = new MallOrderDiscountInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 订单优惠添加
     * @param addParam 参数
     */
    @Override
    public void add(MallOrderDiscountParam addParam) {
        MallOrderDiscount model = new MallOrderDiscount();
        model.setSiteId(addParam.getSiteId());
        model.setOrderId(addParam.getOrderId());
        model.setOrderGoodsIds(addParam.getOrderGoodsIds());
        model.setType(addParam.getType());
        model.setNum(addParam.getNum());
        model.setMoney(addParam.getMoney());
        model.setDiscountType(addParam.getDiscountType());
        model.setDiscountTypeId(addParam.getDiscountTypeId());
        model.setContent(addParam.getContent());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setStatus(addParam.getStatus());
        mallOrderDiscountMapper.insert(model);
    }

    /**
     * 订单优惠编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MallOrderDiscountParam editParam) {
        MallOrderDiscount model = mallOrderDiscountMapper.selectOne(
                new QueryWrapper<MallOrderDiscount>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        
        model.setSiteId(editParam.getSiteId());
        model.setOrderId(editParam.getOrderId());
        model.setOrderGoodsIds(editParam.getOrderGoodsIds());
        model.setType(editParam.getType());
        model.setNum(editParam.getNum());
        model.setMoney(editParam.getMoney());
        model.setDiscountType(editParam.getDiscountType());
        model.setDiscountTypeId(editParam.getDiscountTypeId());
        model.setContent(editParam.getContent());
        model.setStatus(editParam.getStatus());
        mallOrderDiscountMapper.updateById(model);
    }

    /**
     * 订单优惠删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        MallOrderDiscount model = mallOrderDiscountMapper.selectOne(
                new QueryWrapper<MallOrderDiscount>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        mallOrderDiscountMapper.delete(new QueryWrapper<MallOrderDiscount>().eq("id", id));
    }

}
