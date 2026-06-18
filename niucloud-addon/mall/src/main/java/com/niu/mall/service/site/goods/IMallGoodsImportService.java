package com.niu.mall.service.site.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.goods.param.MallGoodsImportParam;
import com.niu.mall.service.site.goods.vo.MallGoodsImportRowVo;
import com.niu.mall.service.site.goods.vo.MallGoodsImportVo;

import java.util.Map;

public interface IMallGoodsImportService {

    Map<String, String> getGoodsTypeAll();

    Map<String, Object> add(MallGoodsImportParam params);

    PageResult<MallGoodsImportVo> getPage(PageParam pageParam);

    PageResult<MallGoodsImportRowVo> getDetail(Integer recordId, PageParam pageParam, Map<String, Object> params);

    MallGoodsImportVo info(Integer recordId);

//    Boolean update(Map<String, Object> where, Map<String, Object> data);

    Map<String, Object> execute(Integer recordId);

    Map<String, Object> demo();
}
