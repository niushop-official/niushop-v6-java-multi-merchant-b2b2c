package com.niu.mall.service.site.goods;

import com.niu.mall.service.site.goods.param.MallGoodsLabelSearchParam;
import com.niu.mall.service.site.goods.vo.MallGoodsLabelListVo;

import java.util.List;

/**
 * 商品标签服务接口
 */
public interface IMallGoodsLabelService {


    /**
     * 商品标签列表
     * @param searchParam 搜索参数
     * @return PageResult<MallGoodsLabelListVo>
     */
    List<MallGoodsLabelListVo> list(MallGoodsLabelSearchParam searchParam);

}
