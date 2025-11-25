package com.niu.mall.service.site.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.mall.entity.order.MallOrderDiscountGoods;
import com.niu.mall.mapper.order.MallOrderDiscountGoodsMapper;
import com.niu.mall.service.site.order.param.MallOrderDiscountGoodsParam;
import com.niu.mall.service.site.order.param.MallOrderDiscountGoodsSearchParam;
import com.niu.mall.service.site.order.IMallOrderDiscountGoodsService;
import com.niu.mall.service.site.order.vo.MallOrderDiscountGoodsInfoVo;
import com.niu.mall.service.site.order.vo.MallOrderDiscountGoodsListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 订单项优惠实现
 */
@Service
public class MallOrderDiscountGoodsServiceImpl implements IMallOrderDiscountGoodsService {
        
    @Resource
    MallOrderDiscountGoodsMapper mallOrderDiscountGoodsMapper;

    /**
     * 订单项优惠列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderDiscountGoodsListVo>
     */
    @Override
    public PageResult<MallOrderDiscountGoodsListVo> list(PageParam pageParam, MallOrderDiscountGoodsSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MallOrderDiscountGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<MallOrderDiscountGoods> iPage = mallOrderDiscountGoodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallOrderDiscountGoodsListVo> list = new LinkedList<>();
        for (MallOrderDiscountGoods item : iPage.getRecords()) {
            MallOrderDiscountGoodsListVo vo = new MallOrderDiscountGoodsListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 订单项优惠详情
     * @param id 主键
     * @return ShopOrderDiscountGoodsInfoVo
     */
    @Override
    public MallOrderDiscountGoodsInfoVo info(Integer id) {
        MallOrderDiscountGoods model = mallOrderDiscountGoodsMapper.selectOne(
                new QueryWrapper<MallOrderDiscountGoods>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        MallOrderDiscountGoodsInfoVo vo = new MallOrderDiscountGoodsInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 订单项优惠添加
     * @param addParam 参数
     */
    @Override
    public void add(MallOrderDiscountGoodsParam addParam) {
        MallOrderDiscountGoods model = new MallOrderDiscountGoods();
        model.setSiteId(addParam.getSiteId());
        model.setOrderDiscountId(addParam.getOrderDiscountId());
        model.setOrderId(addParam.getOrderId());
        model.setOrderGoodsId(addParam.getOrderGoodsId());
        model.setType(addParam.getType());
        model.setNum(addParam.getNum());
        model.setMoney(addParam.getMoney());
        mallOrderDiscountGoodsMapper.insert(model);
    }

    /**
     * 订单项优惠编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MallOrderDiscountGoodsParam editParam) {
        MallOrderDiscountGoods model = mallOrderDiscountGoodsMapper.selectOne(
                new QueryWrapper<MallOrderDiscountGoods>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        
        model.setSiteId(editParam.getSiteId());
        model.setOrderDiscountId(editParam.getOrderDiscountId());
        model.setOrderId(editParam.getOrderId());
        model.setOrderGoodsId(editParam.getOrderGoodsId());
        model.setType(editParam.getType());
        model.setNum(editParam.getNum());
        model.setMoney(editParam.getMoney());
        mallOrderDiscountGoodsMapper.updateById(model);
    }

    /**
     * 订单项优惠删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        MallOrderDiscountGoods model = mallOrderDiscountGoodsMapper.selectOne(
                new QueryWrapper<MallOrderDiscountGoods>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        mallOrderDiscountGoodsMapper.delete(new QueryWrapper<MallOrderDiscountGoods>().eq("id", id));
    }

}
