package com.niu.mall.service.site.marketing;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.entity.active.MallActive;
import com.niu.mall.service.site.marketing.param.MallActiveParam;
import com.niu.mall.service.site.marketing.param.MallDiscountSearchParam;
import com.niu.mall.service.site.marketing.vo.MallDiscountInfoVo;
import com.niu.mall.service.site.marketing.vo.MallDiscountListVo;

/**
 * 店铺营销活动（整体活动）服务接口
 */
public interface IMallActiveService extends IService<MallActive> {


    /**
     * 店铺营销活动（整体活动）列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopActiveListVo>
     */
    PageResult<MallDiscountListVo> list(PageParam pageParam, MallDiscountSearchParam searchParam);

    /**
     * 店铺营销活动（整体活动）详情
     *
     * @param id 主键ID
     * @return ShopActiveInfoVo
     */
    MallDiscountInfoVo info(Integer id);

    /**
     * 店铺营销活动（整体活动）添加
     *
     * @param addParam 添加参数
     */
    void add(MallActiveParam addParam);

    /**
     * 店铺营销活动（整体活动）编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MallActiveParam editParam);

    /**
     * 店铺营销活动（整体活动）删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

}
