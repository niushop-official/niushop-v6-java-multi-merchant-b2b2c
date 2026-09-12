package com.niu.mall.service.admin.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.admin.goods.param.MallGoodsSkuSearchParam;
import com.niu.mall.service.admin.goods.IMallGoodsSkuService;
import com.niu.mall.service.api.goods.vo.MallGoodsSkuVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.util.LinkedList;
import java.util.List;


/**
 * 商品规格实现
 */
@Service
public class MallGoodsSkuServiceImpl extends ServiceImpl<MallGoodsSkuMapper, MallGoodsSku>  implements IMallGoodsSkuService {
        
    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    @Override
    public void insertAll(List<MallGoodsSku> list) {
        super.saveOrUpdateBatch(list);
    }

    @Override
    public List<MallGoodsSkuVo> list(MallGoodsSkuSearchParam searchParam) {
        QueryWrapper<MallGoodsSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("goods_id", searchParam.getGoodsId());

        List<MallGoodsSku> record = mallGoodsSkuMapper.selectList(queryWrapper);
        List<MallGoodsSkuVo> list = new LinkedList<>();
        for (MallGoodsSku item : record) {
            MallGoodsSkuVo vo = new MallGoodsSkuVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

}
