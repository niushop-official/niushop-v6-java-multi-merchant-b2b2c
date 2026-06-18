package com.niu.mall.service.admin.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.marketing.param.FullGiftSearchParam;
import com.niu.mall.service.admin.marketing.vo.FullGiftInfoVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftInitVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftListVo;

/**
 * 满赠活动服务接口
 * <p>
 * 提供满赠活动的管理功能，包括活动的增删改查、状态设置、商品检测等
 */
public interface IFullGiftService {

    /**
     * 获取满赠活动分页列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索条件（标题、状态、类型、商品类型、时间范围等）
     * @return PageResult<FullGiftListVo> 满赠活动分页列表
     */
    PageResult<FullGiftListVo> list(PageParam pageParam, FullGiftSearchParam searchParam);

    /**
     * 获取满赠活动详情
     *
     * @param id 满赠活动ID
     * @return FullGiftInfoVo 满赠活动详情信息
     */
    FullGiftInfoVo info(Integer id);

    /**
     * 获取满赠活动初始化数据
     * <p>
     * 返回满赠活动所需的枚举数据，包括状态、商品类型、规则类型、会员类型等
     *
     * @return FullGiftInitVo 初始化数据
     */
    FullGiftInitVo init();

}