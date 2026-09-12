package com.niu.mall.service.site.stat;

import com.niu.core.service.admin.addon.vo.AddonListVo;
import com.niu.core.service.admin.article.vo.ArticleListVo;
import com.niu.mall.service.core.shop.vo.StatDataVo;
import com.niu.mall.service.core.shop.vo.StatVo;
import com.niu.mall.service.site.stat.vo.*;

import java.util.List;

/**
 * 【请填写功能名称】服务接口
 */
public interface IMallStatService {

    /**
     * 查询统计总和
     *
     * @return
     */
    StatVo getStat();

    /**
     * 获取时间区间内天统计数据
     *
     * @param startTime
     * @param endtTime
     * @return
     */
    StatDataVo getStatData(String startTime, String endtTime);

    /**
     * 获取时间区间内天统计数据
     *
     * @param startTime
     * @param endTime
     * @return
     */
    StatVo getStatDate(Long startTime, Long endTime);

    /**
     * 获取使用指南
     * @return
     */
    List<SuggestionVo> getSuggestionList();

    /**
     * 获取插件
     * @return
     */
    List<AddonListVo> getAddonList();

    /**
     * 获取公告通知
     * @return
     */
    List<ArticleListVo> getArticleList();

    /**
     * 资讯详情
     */
    StatArticleInfoVo articleInfo(Integer id);

    /**
     * 获取店铺信息
     * @return
     */
    ShopStatVo getShopInfo();

    /**
     * 获取交易概况
     * @param startTime
     * @param endtTime
     * @return
     */
    StatDataVo getSaleList(String startTime, String endtTime);

    /**
     * 获取商品排行榜
     * @param startTime
     * @param endTime
     * @return
     */
    List<GoodsRankVo> getGoodsRank(Long startTime, Long endTime);

    /**
     * 获取商品排行榜（支持排序，对齐 PHP StatService::getRank）
     */
    List<GoodsRankVo> getGoodsRank(Long startTime, Long endTime, String order, String sort);
}
