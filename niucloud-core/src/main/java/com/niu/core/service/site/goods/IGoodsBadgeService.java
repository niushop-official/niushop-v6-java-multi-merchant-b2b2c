package com.niu.core.service.site.goods;

import com.niu.core.service.admin.goods.param.GoodsBadgeSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsBadgeListVo;

import java.util.List;

/**
 * 商品角标服务接口（站点端）
 * 对应 PHP: app\service\admin\basic\site\BadgeService
 */
public interface IGoodsBadgeService {

    List<GoodsBadgeListVo> list(GoodsBadgeSearchParam searchParam);
}
