package com.niu.core.service.admin.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.marketing.param.FullReductionSearchParam;
import com.niu.core.service.admin.marketing.vo.FullReductionInfoVo;
import com.niu.core.service.admin.marketing.vo.FullReductionInitVo;
import com.niu.core.service.admin.marketing.vo.FullReductionListVo;

/**
 * 满减活动服务接口
 * <p>
 * 提供满减活动的管理功能，包括活动的增删改查、状态设置、商品检测等
 */
public interface IFullReductionService {

    /**
     * 获取满减活动分页列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索条件（标题、状态、类型、商品类型、时间范围等）
     * @return PageResult<FullReductionListVo> 满减活动分页列表
     */
    PageResult<FullReductionListVo> list(PageParam pageParam, FullReductionSearchParam searchParam);

    /**
     * 获取满减活动详情
     *
     * @param id 满减活动ID
     * @return FullReductionInfoVo 满减活动详情信息
     */
    FullReductionInfoVo info(Integer id);

    /**
     * 获取满减活动初始化数据
     * <p>
     * 返回满减活动所需的枚举数据，包括状态、类型、商品类型、规则类型、会员类型、优惠类型等
     *
     * @return FullReductionInitVo 初始化数据
     */
    FullReductionInitVo init();

}