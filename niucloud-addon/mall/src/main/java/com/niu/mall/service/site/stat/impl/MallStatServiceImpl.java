package com.niu.mall.service.site.stat.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.article.Article;
import com.niu.core.entity.delivery.DeliveryShippingTemplate;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.enums.addon.AddonStatusEnum;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.article.ArticleMapper;
import com.niu.core.mapper.delivery.DeliveryShippingTemplateMapper;
import com.niu.core.mapper.diy.DiyPageMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.service.admin.addon.vo.AddonListVo;
import com.niu.core.service.admin.article.IArticleService;
import com.niu.core.service.admin.article.param.ArticeSearchParam;
import com.niu.core.service.admin.article.vo.ArticleInfoVo;
import com.niu.core.service.admin.article.vo.ArticleListVo;
import com.niu.core.service.admin.shop.ISiteShopCategoryService;
import com.niu.core.service.admin.shop.ISiteShopService;
import com.niu.core.service.admin.shop.vo.SiteShopCategoryInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopInShopVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.active.MallActive;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsStat;
import com.niu.mall.enums.active.ActiveClassEnum;
import com.niu.mall.mapper.active.MallActiveMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsStatMapper;
import com.niu.mall.service.core.shop.ICoreStatService;
import com.niu.mall.service.core.shop.vo.StatDataVo;
import com.niu.mall.service.core.shop.vo.StatVo;
import com.niu.mall.service.site.stat.IMallStatService;
import com.niu.mall.service.site.stat.vo.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 【请填写功能名称】实现
 */
@Service
public class MallStatServiceImpl implements IMallStatService {
        
    @Resource
    ICoreStatService coreStatService;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    IArticleService articleService;

    @Resource
    ISiteService siteService;

    @Resource
    ISiteShopService siteShopService;

    @Resource
    ISiteShopCategoryService siteShopCategoryService;
    @Resource
    ICoreUploadService coreUploadService;

    @Resource
    ArticleMapper articleMapper;
    @Resource
    DiyPageMapper diyPageMapper;
    @Resource
    private SiteAddressMapper siteAddressMapper;
    @Resource
    private DeliveryShippingTemplateMapper deliveryShippingTemplateMapper;
    @Resource
    private MallActiveMapper mallActiveMapper;
    @Resource
    private ICoreSiteService coreSiteService;
    @Resource
    private AddonMapper addonMapper;
    @Resource
    private SysMenuMapper sysMenuMapper;
    @Resource
    private MallGoodsStatMapper mallGoodsStatMapper;

    /**
     * 查询统计总和
     *
     * @return
     */
    @Override
    public StatVo getStat() {
        return coreStatService.getStat(RequestUtils.siteId());
    }

    /**
     * 获取时间区间内天统计数据
     *
     * @param startTime
     * @param endtTime
     * @return
     */
    @Override
    public StatDataVo getStatData(String startTime, String endtTime) {

        return coreStatService.getStatData(RequestUtils.siteId(), startTime, endtTime);
    }

    @Override
    public StatVo getStatDate(Long startTime, Long endtTime) {
        return coreStatService.getStatDate(RequestUtils.siteId(), startTime, endtTime);
    }

    /**
     * 获取使用指南
     * @return
     */
    @Override
    public List<SuggestionVo> getSuggestionList() {
        List<SuggestionVo> list = new ArrayList<>();
        SuggestionVo vo = new SuggestionVo();
        vo.setKey("add_shop_diy_page");
        vo.setTitle("店铺装修");
        vo.setDesc("搭建一个漂亮的店铺首页，吸引更多客户吧");
        vo.setButtonName("去装修");
        vo.setRouterPath("diy/index");
        vo.setIsComplete(0);
        list.add(vo);

        SuggestionVo vo1 = new SuggestionVo();
        vo1.setKey("add_goods");
        vo1.setTitle("添加一件商品");
        vo1.setDesc("让你的生意尽快运转起来");
        vo1.setButtonName("立即创建");
        vo1.setRouterPath("mall/goods/real_edit");
        vo1.setIsComplete(0);
        list.add(vo1);

        SuggestionVo vo2 = new SuggestionVo();
        vo2.setKey("add_site_address");
        vo2.setTitle("设置商家地址库");
        vo2.setDesc("方便商家发货和收货");
        vo2.setButtonName("去设置");
        vo2.setRouterPath("site_address");
        vo2.setIsComplete(0);
        list.add(vo2);

        SuggestionVo vo3 = new SuggestionVo();
        vo3.setKey("add_delivery_shipping_template");
        vo3.setTitle("设置运费模板");
        vo3.setDesc("灵活设置商品运费");
        vo3.setButtonName("去设置");
        vo3.setRouterPath("delivery/shipping/template_edit");
        vo3.setIsComplete(0);
        list.add(vo3);

        SuggestionVo vo4 = new SuggestionVo();
        vo4.setKey("add_discount");
        vo4.setTitle("创建一个限时折扣活动");
        vo4.setDesc("吸引客户尽快来下单");
        vo4.setButtonName("立即创建");
        vo4.setRouterPath("mall/marketing/discount/add");
        vo4.setIsComplete(0);
        list.add(vo4);

        Integer siteId = RequestUtils.siteId();
        Long diyCount = diyPageMapper.selectCount(new QueryWrapper<DiyPage>().eq("site_id", siteId).eq("name","DIY_SITE_INDEX").eq("is_default",1));
        Long goodsCount = mallGoodsMapper.selectCount(new QueryWrapper<MallGoods>().eq("site_id", siteId));
        Long shopAddressCount = siteAddressMapper.selectCount(new QueryWrapper<SiteAddress>().eq("site_id", siteId));
        Long templateCount = deliveryShippingTemplateMapper.selectCount(new QueryWrapper<DeliveryShippingTemplate>().eq("site_id", siteId));
        Long discountCount = mallActiveMapper.selectCount(new QueryWrapper<MallActive>().eq("site_id", siteId).eq("active_class", ActiveClassEnum.DISCOUNT.getType()));

        for(int i=0; i<list.size(); i++){
            if(list.get(i).getKey().equals("add_shop_diy_page") && diyCount.intValue() > 0){
                list.get(i).setIsComplete(1);
            }
            if(list.get(i).getKey().equals("add_goods") && goodsCount.intValue() > 0){
                list.get(i).setIsComplete(1);
            }
            if(list.get(i).getKey().equals("add_shop_address") && shopAddressCount.intValue() > 0){
                list.get(i).setIsComplete(1);
            }
            if(list.get(i).getKey().equals("add_delivery_shipping_template") && templateCount.intValue() > 0){
                list.get(i).setIsComplete(1);
            }
            if(list.get(i).getKey().equals("add_discount") && discountCount.intValue() > 0){
                list.get(i).setIsComplete(1);
            }
        }
        return list;
    }

    /**
     * 获取分销插件
     * @return
     */
    @Override
    public List<AddonListVo> getAddonList() {
        List<String> siteAddon = coreSiteService.getAddonKeysBySiteId(RequestUtils.siteId());
         List<Addon> addonList = addonMapper.selectList(new QueryWrapper<Addon>().eq("type", AddonTypeEnum.ADDON.getType())
                .eq("status", AddonStatusEnum.ON.getCode()).in("`KEY`",siteAddon));

        List<AddonListVo> list = new ArrayList<>();
        if(!addonList.isEmpty()){
            for(Addon vo : addonList){
                AddonListVo addonListVo = new AddonListVo();
                BeanUtils.copyProperties(vo, addonListVo);
                SysMenu sysMenuInfoVoList = sysMenuMapper.selectOne(new QueryWrapper<SysMenu>()
                        .eq("parent_key",vo.getKey()).eq("addon",vo.getKey()).eq("app_type","site").orderByDesc("sort").last("limit 1"));
                if(ObjectUtil.isNotEmpty(sysMenuInfoVoList) &&  StringUtils.isNotEmpty(sysMenuInfoVoList.getRouterPath())){
                    addonListVo.setRouterPath(sysMenuInfoVoList.getRouterPath());
                }
                list.add(addonListVo);
            }
        }
        return list;
    }

    /**
     * 获取公告通知
     * @return
     */
    @Override
    public List<ArticleListVo> getArticleList() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_show", 1);
        queryWrapper.orderByAsc("sort");
        queryWrapper.last("limit 6");
        List<Article> list = articleMapper.selectList(queryWrapper);
        List<ArticleListVo> listVo = new LinkedList<>();
        for (Article item : list) {
            ArticleListVo vo = new ArticleListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }

    @Override
    public StatArticleInfoVo articleInfo(Integer id) {
        StatArticleInfoVo statArticleInfoVo = new StatArticleInfoVo();
        Article article = articleMapper.selectById( id);
        if(article != null){
            BeanUtils.copyProperties(article, statArticleInfoVo);
            if(ObjectUtil.isNotEmpty(article.getImage())){
                statArticleInfoVo.setImageThumbBig(coreUploadService.thumb(RequestUtils.siteId(), article.getImage(), UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType()));
            }
        }
        return statArticleInfoVo;
    }

    /**
     * 获取店铺信息
     * @return
     */
    @Override
    public ShopStatVo getShopInfo() {
        //站点信息
        SiteInfoVo siteInfoVo = siteService.info(RequestUtils.siteId());
        //站点和分类关联信息
        SiteShopInShopVo siteShopInShopVo = siteShopService.getShopAccountInfo(siteInfoVo.getSiteId());
        //分类信息
        SiteShopCategoryInfoVo siteShopCategoryInfoVo = siteShopCategoryService.info(siteShopInShopVo.getCategoryId());


        ShopStatVo result = new ShopStatVo();
        result.setCreateTime(siteInfoVo.getCreateTime());
        result.setExpireTime(siteInfoVo.getExpireTime());
        result.setGroupId(siteInfoVo.getGroupId());
        result.setGroupName(siteInfoVo.getGroupName());
        result.setIsSelf(siteShopInShopVo.getIsSelf());
        result.setSiteId(siteInfoVo.getSiteId());
        result.setSiteName(siteInfoVo.getSiteName());
        result.setStatus(siteInfoVo.getStatus());
        result.setStatusName(siteInfoVo.getStatusName());

        ShopStatVo.categoryName categoryName = new ShopStatVo.categoryName();
        categoryName.setCategoryId(siteShopCategoryInfoVo.getCategoryId());
        categoryName.setCategoryName(siteShopCategoryInfoVo.getCategoryName());
        result.setCategoryName(categoryName);

        return result;
    }

    /**
     * 获取交易概况
     * @param startTime
     * @param endtTime
     * @return
     */
    @Override
    public StatDataVo getSaleList(String startTime, String endtTime) {

        return coreStatService.getStatData(RequestUtils.siteId(), startTime, endtTime);
    }

    /**
     * 获取商品排行榜
     * 516
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<GoodsRankVo> getGoodsRank( Long startTime, Long endTime) {
        MPJQueryWrapper<MallGoodsStat> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("gs")
                .innerJoin("?_mall_goods g ON gs.goods_id = g.goods_id ".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("gs.goods_id,sum(gs.sale_num) as sale_num,sum(gs.pay_money) as pay_money");
        queryWrapper.select("g.goods_name,g.goods_cover");
        queryWrapper.eq("gs.site_id", RequestUtils.siteId());
        if(startTime.intValue() > 0 && endTime.intValue() > 0){
            queryWrapper.ge("gs.date_time", startTime);
            queryWrapper.le("gs.date_time", startTime);
        }else if(startTime.intValue() > 0){
            queryWrapper.eq("gs.date_time", startTime);
        }
        if(startTime.intValue() <= 0 && endTime.intValue() <= 0){
            queryWrapper.gt("gs.date_time", 0);
        }
        queryWrapper.groupBy("gs.goods_id");
        queryWrapper.orderByDesc("sum(gs.sale_num)");
        queryWrapper.last("limit 5");
        List<GoodsRankVo> list = mallGoodsStatMapper.selectJoinList(GoodsRankVo.class,queryWrapper);
        for (GoodsRankVo item : list) {
            GoodsRankVo.goods goods = new GoodsRankVo.goods();
            BeanUtils.copyProperties(item, goods);
            item.setGoods(goods);
        }
        return list;
    }

}