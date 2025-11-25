package com.niu.mall.service.admin.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.admin.diy.IMallDiyFormService;
import com.niu.mall.service.admin.order.IOrderConfigService;
import com.niu.mall.service.core.order.vo.OrderConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MallDiyFormServiceImpl implements IMallDiyFormService {

    @Resource
    IOrderConfigService orderConfigService;

    @Resource
    MallGoodsMapper mallGoodsMapper;


    @Override
    public Boolean diyFromDelBefore(Integer formId) {
        OrderConfigVo config = orderConfigService.getConfig();
        if (ObjectUtil.isNotEmpty(config) && ObjectUtil.isNotEmpty(config.getFormId())) {
            if (config.getFormId().equals(formId)) {
                return false;
            }
        }
        Long count = mallGoodsMapper.selectCount(new QueryWrapper<MallGoods>().eq("form_id", formId));
        if(count == null) count = 0L;
        return count <= 0;
    }
}
