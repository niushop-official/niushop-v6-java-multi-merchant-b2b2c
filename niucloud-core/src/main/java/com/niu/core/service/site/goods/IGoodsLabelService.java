package com.niu.core.service.site.goods;

import com.niu.core.service.admin.goods.param.GoodsLabelSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsLabelListVo;

import java.util.List;

/**
 * 商品标签服务接口（站点端）
 * 对应 PHP: app\service\admin\basic\site\LabelService
 */
public interface IGoodsLabelService {

    /**
     * 商品标签列表（启用状态）
     */
    List<GoodsLabelListVo> list(GoodsLabelSearchParam searchParam);
}
