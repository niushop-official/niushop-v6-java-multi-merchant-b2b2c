package com.niu.mall.service.site.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.marketing.param.FullGiftParam;
import com.niu.mall.service.admin.marketing.param.FullGiftSearchParam;
import com.niu.mall.service.admin.marketing.vo.FullGiftInfoVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftInitVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftListVo;

import java.util.Map;

public interface ISiteFullGiftService {

    PageResult<FullGiftListVo> list(PageParam pageParam, FullGiftSearchParam searchParam);

    FullGiftInitVo init(Integer id);

    FullGiftInfoVo info(Integer id);

    void add(FullGiftParam addParam);

    void edit(Integer id, FullGiftParam modifyParam);

    void del(Integer id);

    void setStatus(Integer id, String status);

    Map<String, Object> checkGoods(FullGiftParam data);

}