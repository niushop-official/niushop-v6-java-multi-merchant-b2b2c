package com.niu.core.service.admin.site;

import com.niu.core.entity.site.SiteSettAccount;
import com.niu.core.service.admin.site.param.SiteSettAccountParam;
import com.niu.core.service.admin.site.param.SiteSettAccountSearchParam;
import com.niu.core.service.admin.site.vo.SiteSettAccountVo;

import java.util.List;
import java.util.Map;

/**
 * 站点结算账户服务接口
 */
public interface ISiteSettAccountService {

    /**
     * 获取结算账户列表
     * @param searchParam
     * @return
     */
    List<SiteSettAccountVo> getList(SiteSettAccountSearchParam searchParam);

    /**
     * 获取结算账户详情
     * @param id
     * @return
     */
    SiteSettAccountVo getInfo(Integer id);

    /**
     * 根据ID获取结算账户实体
     * @param id
     * @return
     */
    SiteSettAccount getSiteSettAccountById(Integer id);

    /**
     * 根据结算账户获取转账数据格式
     * @param siteSettAccount
     * @return
     */
    Map<String, Object> getTransferDataBySettAccount(SiteSettAccount siteSettAccount);

    /**
     * 添加结算账户
     * @param param
     * @return
     */
    Integer add(SiteSettAccountParam param);

    /**
     * 编辑结算账户
     * @param id
     * @param param
     * @return
     */
    Integer edit(Integer id, SiteSettAccountParam param);

    /**
     * 删除结算账户
     * @param id
     * @return
     */
    Integer del(Integer id);
}