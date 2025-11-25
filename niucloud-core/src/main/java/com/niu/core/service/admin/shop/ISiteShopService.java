package com.niu.core.service.admin.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.shop.param.*;
import com.niu.core.service.admin.shop.vo.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 店铺扩展服务接口
 */
public interface ISiteShopService {

    /**
     * 店铺扩展列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteShopListVo>
     */
    PageResult<SiteShopListVo> page(PageParam pageParam, SiteShopSearchParam searchParam);

    List<SiteShopListVo> list(SiteShopSearchParam searchParam);

    /**
     * 店铺扩展详情
     * @param id 主键ID
     * @return  SiteShopInfoVo
     */
    SiteShopInfoVo info(Integer id);



    /**
     * 店铺扩展添加
     * @param addParam 添加参数
     */
    void add(SiteShopParam addParam);

    /**
     * 店铺扩展编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteShopInfoParam editParam);

    /**
     * 店铺扩展编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void editBasicInfo(Integer id, SiteShopBasicInfoParam editParam);



    /**
     * 店铺扩展删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 店铺开启
     * @param siteId
     */
    void openShop(Integer siteId);

    /**
     * 店铺关闭
     * @param siteId
     */
    void closeShop(Integer siteId);

    /**
     * 获取店铺账户流水
     * @param siteId
     * @return
     */
    PageResult<SiteShopAccountLogListVo> getShopAccountLog(PageParam pageParam, Integer siteId);

    /**
     * 获取店铺提现流水
     * @param siteId
     * @return
     */
    PageResult<SiteShopCashOutListVo> getShopCashOutlog(PageParam pageParam, Integer siteId);

    /**
     * 获取店铺账户信息
     * @param siteId
     * @return
     */
    SiteShopInShopVo getShopAccountInfo(Integer siteId);

    /**
     * 编辑店铺账户信息
     * @param siteId
     * @param siteShopAccountParam
     */
    void editShopAccount(Integer siteId, SiteShopAccountInfoParam siteShopAccountParam);

    /**
     * 获取店铺下拉列表
     * @param siteShopSelectParam
     * @return
     */
    SiteShopSelectPage getSelectPage(PageParam pageParam, SiteShopSelectParam siteShopSelectParam);

    /**
     * 获取店铺统计金额
     * @return
     */
    Map<String, BigDecimal> getShopStatMoney();

    /**
     * 获取管理员统计金额
     * @return
     */
    Map<String, BigDecimal> getAdminStatMoney();
    
    /**
     * 检查店铺收款账户信息是否已经完成配置
     * @return boolean true：已完成配置，false：未完成配置
     */
    boolean check();
}
