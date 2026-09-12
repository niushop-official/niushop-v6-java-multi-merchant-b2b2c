package com.niu.core.service.site.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.shop.vo.SiteShopFollowVo;
import com.niu.core.service.site.shop.param.SiteShopMemberBatchModifyParam;
import com.niu.core.service.site.shop.param.SiteShopMemberModifyParam;
import com.niu.core.service.site.shop.param.SiteShopMemberParam;
import com.niu.core.service.site.shop.param.SiteShopMemberSearchParam;
import com.niu.core.service.site.shop.vo.SiteShopMemberInfoVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberListVo;
import com.niu.core.service.site.shop.vo.PlatMemberSelectVo;
import com.niu.core.service.site.shop.param.PlatMemberSelectParam;

import java.util.List;

/**
 * 店铺会员服务接口
 */
public interface ISiteShopMemberService {

    /**
     * 店铺会员列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteShopMemberListVo>
     */
    PageResult<SiteShopMemberListVo> page(PageParam pageParam, SiteShopMemberSearchParam searchParam);

    /**
     * 店铺会员详情
     * @param memberId 会员ID
     * @param addon 所属插件
     * @return  SiteShopMemberInfoVo
     */
    SiteShopMemberInfoVo info(Integer memberId, String addon);

    /**
     * 店铺会员添加
     *
     * @param addParam 添加参数
     */
    void add(SiteShopMemberParam addParam);

    /**
     * 店铺会员编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteShopMemberParam editParam);

    /**
     * 店铺会员删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 修改部分字段
     *
     * @param editParam
     */
    void modify(SiteShopMemberModifyParam editParam);

    /**
     * 批量修改部分字段
     * @param editParam
     */
    void batchModify(SiteShopMemberBatchModifyParam editParam);

    /**
     * 平台会员选择列表
     *
     * @param searchParam 搜索参数
     * @return List<PlatMemberSelectVo>
     */
    List<PlatMemberSelectVo> platMemberSelect(PlatMemberSelectParam searchParam);
}
