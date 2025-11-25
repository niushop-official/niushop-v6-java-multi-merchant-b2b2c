package com.niu.core.service.core.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.core.shop.param.ShopStatParam;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import com.niu.core.service.core.sys.vo.SysDataVo;
import com.niu.core.service.core.sys.vo.SysStatListVo;
import com.niu.core.service.core.sys.vo.SysStatVo;

public interface ICoreShopStatService {

    void addStat(ShopStatParam shopStatParam);

    void setStat(ShopStatParam shopStatParam);
}
