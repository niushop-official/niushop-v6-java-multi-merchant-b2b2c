package com.niu.core.service.site.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.marketing.param.FullReductionParam;
import com.niu.core.service.admin.marketing.param.FullReductionSearchParam;
import com.niu.core.service.admin.marketing.vo.FullReductionInfoVo;
import com.niu.core.service.admin.marketing.vo.FullReductionInitVo;
import com.niu.core.service.admin.marketing.vo.FullReductionListVo;

import java.util.Map;

public interface ISiteFullReductionService {

    PageResult<FullReductionListVo> list(PageParam pageParam, FullReductionSearchParam searchParam);

    FullReductionInfoVo info(Integer id);

    FullReductionInitVo init(Integer id);

    void add(FullReductionParam addParam);

    void edit(Integer id, FullReductionParam modifyParam);

    void del(Integer id);

    void setStatus(Integer id, String status);

    Map<String, Object> checkGoods(FullReductionParam goodsIds);

}