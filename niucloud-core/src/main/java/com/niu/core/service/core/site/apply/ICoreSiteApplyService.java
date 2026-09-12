package com.niu.core.service.core.site.apply;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.shop.param.SiteApplyParam;
import com.niu.core.service.api.shop.param.SiteApplySearchParam;
import com.niu.core.service.api.shop.param.SiteShopApplyVerifyParam;
import com.niu.core.service.api.shop.vo.SiteApplyInfoVo;
import com.niu.core.service.api.shop.vo.SiteApplyListVo;
import com.niu.core.service.core.site.apply.vo.AccountCheckVo;

/**
 * 商户入驻申请服务接口
 * v120迁移：基于 site_apply 表
 */
public interface ICoreSiteApplyService {

    /**
     * 商户入驻申请分页列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return 分页结果
     */
    PageResult<SiteApplyListVo> page(PageParam pageParam, SiteApplySearchParam searchParam);

    /**
     * 商户入驻申请详情
     *
     * @param id 申请id
     * @return 申请详情
     */
    SiteApplyInfoVo info(Integer id);

    /**
     * 审核商户入驻申请
     *
     * @param id          申请id
     * @param verifyParam 审核参数
     */
    void verify(Integer id, SiteShopApplyVerifyParam verifyParam);

    /**
     * 添加商户入驻申请
     *
     * @param addParam 申请参数
     * @return 申请id
     */
    Long add(SiteApplyParam addParam);

    /**
     * 编辑商户入驻申请
     *
     * @param id        申请id
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteApplyParam editParam);

    /**
     * 删除商户入驻申请
     *
     * @param id 申请id
     */
    void del(Integer id);

    /**
     * 店铺账号检测
     *
     * @param account 账号
     * @return 账号信息
     */
    java.util.Map<String, Object> checkAccount(String account);

    /**
     * 申请状态列表
     *
     * @return 状态列表
     */
    java.util.List<java.util.Map<String, Object>> statusList();

    /**
     * 检查是否已有申请
     *
     * @return 是否有申请
     */
    Boolean applyCheck();

    /**
     * 店铺账号检测
     *
     * @param account 账号参数
     * @return 用户信息VO（脱敏手机号）
     */
    AccountCheckVo accountCheck(String account);
}
