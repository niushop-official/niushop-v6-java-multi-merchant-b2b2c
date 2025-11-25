package com.niu.core.service.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.shop.param.SiteShopApplyParam;
import com.niu.core.service.api.shop.param.SiteShopApplySearchParam;
import com.niu.core.service.api.shop.param.SiteShopApplyVerifyParam;
import com.niu.core.service.api.shop.vo.SiteShopApplyInfoVo;
import com.niu.core.service.api.shop.vo.SiteShopApplyListVo;

import java.util.List;
import java.util.Map;

/**
 * 商户入驻申请服务接口
 */
public interface ISiteShopApplyService {

    /**
     * 商户入驻申请列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteShopApplyListVo>
     */
    PageResult<SiteShopApplyListVo> page(PageParam pageParam, SiteShopApplySearchParam searchParam);

    /**
     * 商户入驻申请详情
     * @param id 主键ID
     * @return  SiteShopApplyInfoVo
     */
    SiteShopApplyInfoVo info(Integer id);

    /**
     * 审核
     * @param id
     * @param verifyParam
     */
    void verify(Integer id, SiteShopApplyVerifyParam verifyParam);

    /**
     * 商户入驻申请添加
     * @param addParam 添加参数
     */
    void add(SiteShopApplyParam addParam);

    /**
     * 商户入驻申请编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteShopApplyParam editParam);

    /**
     * 商户入驻申请删除
     * @param id 主键ID
     */
    void del(Integer id);

    Integer shopApply(SiteShopApplyParam addParam);

    List<Map<String, Object>> statusList();

    Boolean applyCheck();


    Map<String, Object> checkAccount(String account);
}
