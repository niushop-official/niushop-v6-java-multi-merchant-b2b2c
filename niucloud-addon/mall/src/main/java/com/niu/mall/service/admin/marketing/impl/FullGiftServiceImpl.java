package com.niu.mall.service.admin.marketing.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.entity.fullactive.MallFullGift;
import com.niu.mall.mapper.fullactive.FullGiftGoodsMapper;
import com.niu.mall.mapper.fullactive.FullGiftMapper;
import com.niu.mall.service.admin.marketing.IFullGiftService;
import com.niu.mall.service.admin.marketing.param.FullGiftSearchParam;
import com.niu.mall.service.admin.marketing.vo.FullGiftInfoVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftInitVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftListVo;
import com.niu.mall.service.core.marketing.ICoreFullGiftService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 满赠活动服务实现类
 * <p>
 * 实现满赠活动的管理功能，包括活动的增删改查、状态设置、商品检测等
 */
@Service
public class FullGiftServiceImpl implements IFullGiftService {

    @Resource
    private FullGiftMapper fullGiftMapper;

    @Resource
    private FullGiftGoodsMapper fullGiftGoodsMapper;

    @Resource
    private ICoreFullGiftService coreFullGiftService;

    @Override
    public PageResult<FullGiftListVo> list(PageParam pageParam, FullGiftSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        Integer siteId = RequestUtils.siteId();

        MPJLambdaWrapper<MallFullGift> queryWrapper = new MPJLambdaWrapper<MallFullGift>()
                .selectAll(MallFullGift.class)
                .orderByDesc(MallFullGift::getCreateTime);

        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) {
            queryWrapper.like(MallFullGift::getTitle, searchParam.getTitle());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq(MallFullGift::getStatus, searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType())) {
            queryWrapper.eq(MallFullGift::getGoodsType, searchParam.getGoodsType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStartTime())) {
            // 有开始时间：活动结束时间 ≥ 筛选开始时间
            queryWrapper.ge(MallFullGift::getEndTime, searchParam.getStartTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getEndTime())) {
            // 有结束时间：活动开始时间 ≤ 筛选结束时间
            queryWrapper.le(MallFullGift::getStartTime, searchParam.getEndTime());
        }

        IPage<MallFullGift> iPage = fullGiftMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<FullGiftListVo> list = BeanUtil.copyToList(iPage.getRecords(), FullGiftListVo.class);

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public FullGiftInitVo init() {
        return coreFullGiftService.init();
    }

    @Override
    public FullGiftInfoVo info(Integer id) {
        return coreFullGiftService.info(id);
    }

}