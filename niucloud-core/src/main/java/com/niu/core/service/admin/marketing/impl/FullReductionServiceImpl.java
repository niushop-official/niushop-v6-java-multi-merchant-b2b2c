package com.niu.core.service.admin.marketing.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.fullactive.MallFullReduction;
import com.niu.core.mapper.fullactive.FullReductionGoodsMapper;
import com.niu.core.mapper.fullactive.FullReductionMapper;
import com.niu.core.service.admin.marketing.IFullReductionService;
import com.niu.core.service.admin.marketing.param.FullReductionSearchParam;
import com.niu.core.service.admin.marketing.vo.FullReductionInfoVo;
import com.niu.core.service.admin.marketing.vo.FullReductionInitVo;
import com.niu.core.service.admin.marketing.vo.FullReductionListVo;
import com.niu.core.service.core.marketing.ICoreFullReductionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 满减活动服务实现类
 * <p>
 * 实现满减活动的管理功能，包括活动的增删改查、状态设置、商品检测等
 */
@Service
public class FullReductionServiceImpl implements IFullReductionService {

    @Resource
    private FullReductionMapper fullReductionMapper;

    @Resource
    private FullReductionGoodsMapper fullReductionGoodsMapper;

    @Resource
    private ICoreFullReductionService coreFullReductionService;

    @Override
    public PageResult<FullReductionListVo> list(PageParam pageParam, FullReductionSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        Integer siteId = RequestUtils.siteId();

        MPJLambdaWrapper<MallFullReduction> queryWrapper = new MPJLambdaWrapper<MallFullReduction>()
                .selectAll(MallFullReduction.class)
                .orderByDesc(MallFullReduction::getCreateTime);

        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) {
            queryWrapper.like(MallFullReduction::getTitle, searchParam.getTitle());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq(MallFullReduction::getStatus, searchParam.getStatus());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getType())) {
            queryWrapper.eq(MallFullReduction::getType, searchParam.getType());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType())) {
            queryWrapper.eq(MallFullReduction::getGoodsType, searchParam.getGoodsType());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStartTime())) {
            queryWrapper.ge(MallFullReduction::getEndTime, searchParam.getStartTime());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getEndTime())) {
            queryWrapper.le(MallFullReduction::getStartTime, searchParam.getEndTime());
        }

        IPage<MallFullReduction> iPage = fullReductionMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<FullReductionListVo> list = BeanUtil.copyToList(iPage.getRecords(), FullReductionListVo.class);

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public FullReductionInfoVo info(Integer id) {
        return coreFullReductionService.info(id);
    }

    @Override
    public FullReductionInitVo init() {
        return coreFullReductionService.init();
    }

}