package com.niu.mall.service.admin.points_mall;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.order.vo.MallOrderDeliveryInfoVo;
import com.niu.mall.service.admin.order.vo.MallOrderDeliveryListVo;
import com.niu.mall.service.admin.points_mall.param.PointsMallGoodsSelectDetailParam;
import com.niu.mall.service.admin.points_mall.param.PointsMallGoodsSelectParam;
import com.niu.mall.service.admin.points_mall.vo.PointsMallGoodsSelectVo;
import com.niu.mall.service.admin.points_mall.vo.PointsMallGoodsSelectDetailVo;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;

import java.util.List;

/**
 * 订单发货服务接口
 */
public interface IGoodsService {
    PageResult<PointsMallGoodsSelectVo> getMallGoodsSelectPage(PointsMallGoodsSelectParam param, PageParam pageParam);
    PointsMallGoodsSelectDetailVo getMallGoodsSelectDetail(PointsMallGoodsSelectDetailParam param);

}
