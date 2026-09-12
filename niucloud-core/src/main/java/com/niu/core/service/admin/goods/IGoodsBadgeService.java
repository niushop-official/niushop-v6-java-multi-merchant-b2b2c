package com.niu.core.service.admin.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.goods.param.EditGoodsBadgeSortParam;
import com.niu.core.service.admin.goods.param.EditGoodsBadgeStatusParam;
import com.niu.core.service.admin.goods.param.GoodsBadgeParam;
import com.niu.core.service.admin.goods.param.GoodsBadgeSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsBadgeInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsBadgeListVo;

import java.util.List;

/**
 * 商品角标服务接口
 * 对应 PHP: app\service\admin\basic\admin\BadgeService
 */
public interface IGoodsBadgeService {

    PageResult<GoodsBadgeListVo> page(PageParam pageParam, GoodsBadgeSearchParam searchParam);

    List<GoodsBadgeListVo> list(GoodsBadgeSearchParam searchParam);

    GoodsBadgeInfoVo info(Integer id);

    Integer add(GoodsBadgeParam addParam);

    void edit(Integer id, GoodsBadgeParam editParam);

    void del(Integer id);

    void modifySort(EditGoodsBadgeSortParam editParam);

    void modifyStatus(EditGoodsBadgeStatusParam editParam);

    void copy(Integer id);
}
