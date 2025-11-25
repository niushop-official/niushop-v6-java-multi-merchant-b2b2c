package com.niu.core.service.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.shop.param.SiteShopCashOutAuditParam;
import com.niu.core.service.api.shop.param.SiteShopCashOutParam;
import com.niu.core.service.api.shop.param.SiteShopCashOutSearchParam;
import com.niu.core.service.api.shop.param.SiteShopCashOutTransferParam;
import com.niu.core.service.api.shop.vo.SiteShopCashOutInfoVo;
import com.niu.core.service.api.shop.vo.SiteShopCashOutListVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商家站点提现服务接口
 */
public interface ISiteShopCashOutService {

    /**
     * 商家站点提现列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteShopCashOutListVo>
     */
    PageResult<SiteShopCashOutListVo> page(PageParam pageParam, SiteShopCashOutSearchParam searchParam);

    /**
     * 商家站点提现详情
     * @param id 主键ID
     * @return  SiteShopCashOutInfoVo
     */
    SiteShopCashOutInfoVo info(Integer id);

    /**
     * 商家站点提现审核
     * @param id
     * @param auditParam
     */
    void audit(Integer id, SiteShopCashOutAuditParam auditParam);

    /**
     * 商家站点提现转账
     * @param id
     * @param transferParam
     */
    void transfer(Integer id, SiteShopCashOutTransferParam transferParam);

    /**
     * 商家站点提现添加
     * @param addParam 添加参数
     */
    void add(SiteShopCashOutParam addParam);

    /**
     * 商家站点提现编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteShopCashOutParam editParam);

    /**
     * 商家站点提现删除
     * @param id 主键ID
     */
    void del(Integer id);

    Map<String, BigDecimal> getWithdrawData();
}
