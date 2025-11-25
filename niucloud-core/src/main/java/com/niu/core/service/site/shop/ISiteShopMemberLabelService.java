package com.niu.core.service.site.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.shop.param.SiteShopMemberLabelEditParam;
import com.niu.core.service.site.shop.param.SiteShopMemberLabelSearchParam;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelAllListVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelInfoVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelListVo;

import java.util.List;

/**
 * 店铺会员标签服务接口
 */
public interface ISiteShopMemberLabelService {


    /**
     * 店铺会员标签列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteShopMemberLabelListVo>
     */
    PageResult<SiteShopMemberLabelListVo> list(PageParam pageParam, SiteShopMemberLabelSearchParam searchParam);

    /**
     * 店铺会员标签详情
     * @param id 主键ID
     * @return SiteShopMemberLabelInfoVo
     */
    SiteShopMemberLabelInfoVo info(Integer id);

    /**
     * 店铺会员标签添加
     * @param addParam 添加参数
     */
    void add(SiteShopMemberLabelEditParam addParam);

    /**
     * 店铺会员标签编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteShopMemberLabelEditParam editParam);

    /**
     * 店铺会员标签删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 全部店铺会员标签
     * @return
     */
    List<SiteShopMemberLabelAllListVo> all();
}
