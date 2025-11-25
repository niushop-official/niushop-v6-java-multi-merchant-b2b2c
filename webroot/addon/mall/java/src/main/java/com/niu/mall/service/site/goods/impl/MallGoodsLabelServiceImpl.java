package com.niu.mall.service.site.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.entity.goods.MallGoodsLabel;
import com.niu.mall.enums.goods.GoodsLabelTypeEnum;
import com.niu.mall.mapper.goods.MallGoodsLabelMapper;
import com.niu.mall.service.site.goods.param.MallGoodsLabelSearchParam;
import com.niu.mall.service.site.goods.IMallGoodsLabelService;
import com.niu.mall.service.site.goods.vo.MallGoodsLabelListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品标签实现
 */
@Service
public class MallGoodsLabelServiceImpl implements IMallGoodsLabelService {
        
    @Resource
    MallGoodsLabelMapper mallGoodsLabelMapper;

    /**
     * 商品标签列表
     * @param searchParam 搜索参数
     * @return PageResult<MallGoodsLabelListVo>
     */
    public List<MallGoodsLabelListVo> list(MallGoodsLabelSearchParam searchParam) {
        QueryWrapper<MallGoodsLabel> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq("site_id", RequestUtils.siteId()); //站点不维护
        queryWrapper.eq("status", GoodsLabelTypeEnum.ENABLE.getStatus());

        if (ObjectUtil.isNotEmpty(searchParam.getLabelName())) {
            queryWrapper.like("label_name", searchParam.getLabelName());
        }

        // 按sort升序，create_time降序排序
        queryWrapper.orderByAsc("sort");
        queryWrapper.orderByDesc("create_time");

        List<MallGoodsLabel> record = mallGoodsLabelMapper.selectList(queryWrapper);
        List<MallGoodsLabelListVo> list = new LinkedList<>();

        for (MallGoodsLabel item : record) {
            MallGoodsLabelListVo vo = new MallGoodsLabelListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }

        return list;
    }

}
