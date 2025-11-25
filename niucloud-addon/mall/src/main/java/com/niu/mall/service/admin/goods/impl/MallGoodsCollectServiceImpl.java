package com.niu.mall.service.admin.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.mall.entity.goods.MallGoodsCollect;
import com.niu.mall.mapper.goods.MallGoodsCollectMapper;
import com.niu.mall.service.admin.goods.param.MallGoodsCollectParam;
import com.niu.mall.service.admin.goods.param.MallGoodsCollectSearchParam;
import com.niu.mall.service.admin.goods.IMallGoodsCollectService;
import com.niu.mall.service.admin.goods.vo.MallGoodsCollectInfoVo;
import com.niu.mall.service.admin.goods.vo.MallGoodsCollectListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品收藏记录实现
 */
@Service
public class MallGoodsCollectServiceImpl implements IMallGoodsCollectService {
        
    @Resource
    MallGoodsCollectMapper mallGoodsCollectMapper;

    /**
     * 商品收藏记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsCollectListVo>
     */
    @Override
    public PageResult<MallGoodsCollectListVo> list(PageParam pageParam, MallGoodsCollectSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MallGoodsCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<MallGoodsCollect> iPage = mallGoodsCollectMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallGoodsCollectListVo> list = new LinkedList<>();
        for (MallGoodsCollect item : iPage.getRecords()) {
            MallGoodsCollectListVo vo = new MallGoodsCollectListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品收藏记录详情
     * @param id 主键
     * @return ShopGoodsCollectInfoVo
     */
    @Override
    public MallGoodsCollectInfoVo info(Integer id) {
        MallGoodsCollect model = mallGoodsCollectMapper.selectOne(
                new QueryWrapper<MallGoodsCollect>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        MallGoodsCollectInfoVo vo = new MallGoodsCollectInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 商品收藏记录添加
     * @param addParam 参数
     */
    @Override
    public void add(MallGoodsCollectParam addParam) {
        MallGoodsCollect model = new MallGoodsCollect();
        model.setSiteId(addParam.getSiteId());
        model.setMemberId(addParam.getMemberId());
        model.setGoodsId(addParam.getGoodsId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        mallGoodsCollectMapper.insert(model);
    }

    /**
     * 商品收藏记录编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MallGoodsCollectParam editParam) {
        MallGoodsCollect model = mallGoodsCollectMapper.selectOne(
                new QueryWrapper<MallGoodsCollect>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        
        model.setSiteId(editParam.getSiteId());
        model.setMemberId(editParam.getMemberId());
        model.setGoodsId(editParam.getGoodsId());
        mallGoodsCollectMapper.updateById(model);
    }

    /**
     * 商品收藏记录删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        MallGoodsCollect model = mallGoodsCollectMapper.selectOne(
                new QueryWrapper<MallGoodsCollect>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        mallGoodsCollectMapper.delete(new QueryWrapper<MallGoodsCollect>().eq("id", id));
    }

}
