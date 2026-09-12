package com.niu.mall.service.admin.site_shop.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.shop.ShopStatusEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteCategoryMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.SiteAddParam;
import com.niu.core.service.admin.site.param.SiteEditParam;
import com.niu.core.service.admin.site.vo.SiteAdminVo;
import com.niu.core.service.admin.site.vo.SiteCategoryListVo;
import com.niu.core.service.core.site.ICoreSiteCategoryService;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.mall.service.admin.site_shop.IAdminShopApplyService;
import com.niu.mall.service.admin.site_shop.IAdminShopService;
import com.niu.mall.service.admin.site_shop.param.AdminShopListQueryParam;
import com.niu.mall.service.admin.site_shop.param.AdminShopPageQueryParam;
import com.niu.mall.service.admin.site_shop.param.AdminShopSelectQueryParam;
import com.niu.mall.service.admin.site_shop.vo.AdminShopInfoVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopListVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopPageVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopSelectVo;
import com.niu.mall.service.admin.site_shop.vo.SiteAdminWithSiteIdVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminShopServiceImpl implements IAdminShopService {

    @Autowired
    private SiteShopMapper shopMapper;
    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    private SiteCategoryMapper siteCategoryMapper;
    @Autowired
    private SiteGroupMapper siteGroupMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private ICoreSiteService coreSiteService;
    @Autowired
    private ISiteService adminSiteService;
    @Autowired
    private AddonMapper addonMapper;
    @Autowired
    private ICoreSiteCategoryService coreSiteCategoryService;
    @Autowired
    private IAdminShopApplyService shopApplyService;

    // 获取当前用户的应用类型和商圈ID（用于权限过滤）
    private String getAppType() {
        return RequestUtils.appType(); // 如 "admin" 或 "circle"
    }

    private List<Integer> getCircleIds() {
        return RequestUtils.circleIds(); // 当前可管理的商圈ID列表
    }

    @Override
    public PageResult<AdminShopPageVo> getPage(PageParam pageParam, AdminShopPageQueryParam queryParam) {
        MPJQueryWrapper<SiteShop> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("sp")
                .leftJoin("?_site s ON sp.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category c ON sp.category_id = c.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category cc ON sp.child_category_id = cc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .select("s.site_id, sp.is_self, sp.business_status, s.group_id, sp.category_id, " +
                        "s.site_name, s.front_end_name, s.front_end_logo, s.logo, s.icon, s.status, s.create_time, " +
                        "sp.service_rate, sp.child_category_id, s.app, " +
                        "c.name as category_name, cc.name as child_category_name")
                .eq("s.app_type", "site"); // 只查站点类型，非管理员

        // 商圈过滤
        if ("circle".equals(getAppType())) {
            List<Integer> circleIds = getCircleIds();
            if (CollUtil.isEmpty(circleIds)) {
                return PageResult.build(pageParam.getPage(), pageParam.getLimit(), 0L);
            }
            wrapper.in("sp.circle_id", circleIds);
        }

        // 条件搜索
        if (StrUtil.isNotBlank(queryParam.getKeyword())) {
            wrapper.like("s.site_name", queryParam.getKeyword());
        }
        if (queryParam.getStatus() != null) {
            wrapper.eq("s.status", queryParam.getStatus());
        }
        if (queryParam.getGroupId() != null) {
            wrapper.eq("s.group_id", queryParam.getGroupId());
        }
        if (queryParam.getCategoryId() != null) {
            wrapper.eq("sp.category_id", queryParam.getCategoryId());
        }
        if (queryParam.getIsSelf() != null) {
            wrapper.eq("sp.is_self", queryParam.getIsSelf());
        }
        if (StrUtil.isNotBlank(queryParam.getApp())) {
            wrapper.like("s.app", queryParam.getApp());
        }
        // 时间范围（create_time, expire_time）需自行解析
        if (CollUtil.isNotEmpty(queryParam.getCreateTime()) && queryParam.getCreateTime().size() == 2) {
            wrapper.between("s.create_time", queryParam.getCreateTime().get(0), queryParam.getCreateTime().get(1));
        }
        if (CollUtil.isNotEmpty(queryParam.getExpireTime()) && queryParam.getExpireTime().size() == 2) {
            wrapper.between("s.expire_time", queryParam.getExpireTime().get(0), queryParam.getExpireTime().get(1));
        }

        wrapper.orderByDesc("s.create_time");

        IPage<AdminShopPageVo> page = shopMapper.selectJoinPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()),
                AdminShopPageVo.class, wrapper);

        // 查询管理员信息（每个站点的超级管理员）
        List<Integer> siteIds = page.getRecords().stream().map(AdminShopPageVo::getSiteId).collect(Collectors.toList());
        Map<Integer, SiteAdminVo> adminMap = Map.of();
        if (CollUtil.isNotEmpty(siteIds)) {
            MPJQueryWrapper<SysUserRole> adminWrapper = new MPJQueryWrapper<>();
            adminWrapper.setAlias("nsur")
                    .select("nsur.site_id, nsu.uid, nsu.username, nsu.head_img, nsu.real_name, nsu.last_ip, nsu.last_time, nsu.login_count")
                    .leftJoin("?_sys_user nsu ON nsur.uid = nsu.uid".replace("?_", GlobalConfig.tablePrefix))
                    .in("nsur.site_id", siteIds)
                    .eq("nsur.is_admin", 1);
            List<SiteAdminWithSiteIdVo> adminList = sysUserRoleMapper.selectJoinList(SiteAdminWithSiteIdVo.class, adminWrapper);
            adminMap = adminList.stream().collect(Collectors.toMap(
                    SiteAdminWithSiteIdVo::getSiteId,
                    admin -> admin,
                    (existing, replacement) -> existing
            ));
        }
        for (AdminShopPageVo vo : page.getRecords()) {
            SiteAdminVo admin = adminMap.get(vo.getSiteId());
            if (admin == null || ObjectUtil.isEmpty(admin.getUsername())) {
                admin = new SiteAdminVo();
                admin.setUsername("--");
            }
            vo.setAdmin(admin);
            vo.setStatusName(SiteStatusEnum.getNameByCode(vo.getStatus()));
            vo.setBusinessStatusName(ShopStatusEnum.getNameByKey(vo.getBusinessStatus()));
        }

        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal()).setData(page.getRecords());
    }

    @Override
    public List<AdminShopListVo> getList(AdminShopListQueryParam queryParam) {
        MPJQueryWrapper<SiteShop> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("sp")
                .leftJoin("?_site s ON sp.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category c ON sp.category_id = c.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group g ON s.group_id = g.group_id".replace("?_", GlobalConfig.tablePrefix))
                .select("s.site_id, sp.is_self, sp.business_status, s.group_id, sp.category_id, " +
                        "s.site_name, s.front_end_name, s.front_end_logo, s.logo, s.icon, s.status, s.create_time, " +
                        "c.name as categoryName, g.group_name")
                .eq("s.app_type", "site");

        // 商圈过滤
        if ("circle".equals(getAppType())) {
            List<Integer> circleIds = getCircleIds();
            if (CollUtil.isEmpty(circleIds)) {
                return new ArrayList<>();
            }
            wrapper.in("sp.circle_id", circleIds);
        }

        // 条件（与分页相似，但无分页）
        if (StrUtil.isNotBlank(queryParam.getKeyword())) {
            wrapper.like("s.site_name", queryParam.getKeyword());
        }
        if (queryParam.getStatus() != null) {
            wrapper.eq("s.status", queryParam.getStatus());
        }
        if (queryParam.getGroupId() != null) {
            wrapper.eq("s.group_id", queryParam.getGroupId());
        }
        if (queryParam.getCategoryId() != null) {
            wrapper.eq("sp.category_id", queryParam.getCategoryId());
        }
        if (queryParam.getIsSelf() != null) {
            wrapper.eq("sp.is_self", queryParam.getIsSelf());
        }
        if (CollUtil.isNotEmpty(queryParam.getCreateTime()) && queryParam.getCreateTime().size() == 2) {
            wrapper.between("s.create_time", queryParam.getCreateTime().get(0), queryParam.getCreateTime().get(1));
        }
        if (CollUtil.isNotEmpty(queryParam.getExpireTime()) && queryParam.getExpireTime().size() == 2) {
            wrapper.between("s.expire_time", queryParam.getExpireTime().get(0), queryParam.getExpireTime().get(1));
        }

        wrapper.orderByDesc("s.create_time");

        List<AdminShopListVo> list = shopMapper.selectJoinList(AdminShopListVo.class, wrapper);
        for (AdminShopListVo vo : list) {
            vo.setStatusName(SiteStatusEnum.getNameByCode(vo.getStatus()));
            vo.setBusinessStatusName(ShopStatusEnum.getNameByKey(vo.getBusinessStatus()));
        }
        return list;
    }

    @Override
    public AdminShopInfoVo getInfo(Integer siteId) {
        MPJQueryWrapper<SiteShop> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("sp")
                .leftJoin("?_site s ON sp.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category c ON sp.category_id = c.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category cc ON sp.child_category_id = cc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group g ON s.group_id = g.group_id".replace("?_", GlobalConfig.tablePrefix))
                .select("sp.*, s.*, c.name as categoryName, cc.name as child_category_name, g.group_name")
                .eq("sp.site_id", siteId);

        // 商圈权限过滤
        if ("circle".equals(getAppType())) {
            wrapper.in("sp.circle_id", getCircleIds());
        }

        AdminShopInfoVo vo = shopMapper.selectJoinOne(AdminShopInfoVo.class, wrapper);
        if (vo == null) {
            throw new AdminException("店铺不存在");
        }

        // 查询站点已安装插件
        List<String> addonKeys = coreSiteService.getAddonKeysBySiteId(siteId);
        if (CollUtil.isNotEmpty(addonKeys)) {
            // 假设有 AddonMapper 可查询
            vo.setSiteAddons(addonMapper.selectList(new LambdaQueryWrapper<Addon>()
                    .in(Addon::getKey, addonKeys)));
        }

        // 计算可提现余额
        BigDecimal moneyGet = vo.getMoneyGet() != null ? vo.getMoneyGet() : BigDecimal.ZERO;
        BigDecimal money = vo.getMoney() != null ? vo.getMoney() : BigDecimal.ZERO;
        BigDecimal moneyCashOuting = vo.getMoneyCashOuting() != null ? vo.getMoneyCashOuting() : BigDecimal.ZERO;
        vo.setWithdrawalMoney(moneyGet.subtract(money).subtract(moneyCashOuting));

        // 查询子分类列表
        List<SiteCategoryListVo> childCategories = coreSiteCategoryService.getChildList(vo.getCategoryId());
        vo.setChildCategoryList(childCategories);

        return vo;
    }

    @Override
    @Transactional
    public Integer add(SiteAddParam param) {
        // 获取分类佣金比率（需实现 CoreSiteCategoryService）
        BigDecimal serviceRate = coreSiteCategoryService.getServiceRateByCategoryId(param.getCategoryId());

        // 其他字段如 uid, username, password 等需处理
        Integer siteId = coreSiteService.add(param); // 需实现

        // 插入 shop 表
        SiteShop shop = new SiteShop();
        shop.setSiteId(siteId);
        shop.setIsSelf(param.getIsSelf());
        shop.setCategoryId(param.getCategoryId());
        shop.setChildCategoryId(param.getChildCategoryId());
        shop.setCircleId(param.getCircleId() != null ? param.getCircleId() : 0);
        shop.setServiceRate(serviceRate);
        shopMapper.insert(shop);

        // 如果有申请ID，更新申请状态
        if (param.getApplyId() != null && param.getApplyId() > 0) {
            shopApplyService.modifyOpenStatus(param.getApplyId(), siteId);
        }
        return siteId;
    }

    @Override
    @Transactional
    public void edit(Integer siteId, SiteEditParam param) {
        // 更新 shop 表
        SiteShop shop = shopMapper.selectOne(new LambdaQueryWrapper<SiteShop>().eq(SiteShop::getSiteId, siteId));
        if (shop == null) {
            throw new AdminException("店铺不存在");
        }
        shop.setIsSelf(param.getIsSelf());
        shop.setChildCategoryId(param.getChildCategoryId());
        shop.setCircleId(param.getCircleId());
        shop.setServiceRate(param.getServiceRate());
        shopMapper.updateById(shop);

        coreSiteService.edit(siteId, param);
    }

    @Override
    public PageResult<AdminShopSelectVo> getSelectPage(PageParam pageParam, AdminShopSelectQueryParam queryParam) {
        MPJQueryWrapper<SiteShop> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("sp")
                .leftJoin("?_site s ON sp.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category c ON sp.category_id = c.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group g ON s.group_id = g.group_id".replace("?_", GlobalConfig.tablePrefix))
                .select("s.site_id, s.site_name, s.logo, s.status, s.create_time, s.group_id, sp.is_recommend, sp.is_self, sp.category_id, s.contact_name, IFNULL(NULLIF(s.contact_mobile, ''), s.phone) as contact_mobile")
                .eq("s.app_type", "site")
                .like("s.app", "\"mall\"")
                .eq("sp.business_status", 1) // 营业中
                .eq("s.status", 1);           // 启用

        // 应用筛选
        if (StrUtil.isNotBlank(queryParam.getKeyword())) {
            wrapper.like("s.site_name", queryParam.getKeyword());
        }
        if (queryParam.getStatus() != null) {
            wrapper.eq("s.status", queryParam.getStatus());
        }
        if (queryParam.getGroupId() != null) {
            wrapper.eq("s.group_id", queryParam.getGroupId());
        }
        if (queryParam.getCategoryId() != null) {
            wrapper.eq("sp.category_id", queryParam.getCategoryId());
        }
        if (queryParam.getIsSelf() != null) {
            wrapper.eq("sp.is_self", queryParam.getIsSelf());
        }
        if (CollUtil.isNotEmpty(queryParam.getSiteIds())) {
            wrapper.in("sp.site_id", queryParam.getSiteIds());
        }
        if (CollUtil.isNotEmpty(queryParam.getCreateTime()) && queryParam.getCreateTime().size() == 2) {
            wrapper.between("s.create_time", queryParam.getCreateTime().get(0), queryParam.getCreateTime().get(1));
        }

        wrapper.orderByDesc("s.create_time");

        IPage<AdminShopSelectVo> page = shopMapper.selectJoinPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()),
                AdminShopSelectVo.class, wrapper);

        // 校验 verify_site_ids
        List<Integer> verifySiteIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(queryParam.getVerifySiteIds())) {
            List<Integer> existIds = shopMapper.selectJoinList(Integer.class,
                            new MPJQueryWrapper<SiteShop>()
                                    .setAlias("sp")
                                    .select("sp.site_id")
                                    .in("sp.site_id", queryParam.getVerifySiteIds())
                                    .eq("sp.business_status", 1))
                    .stream().collect(Collectors.toList());
            verifySiteIds = existIds;
        }

        PageResult<AdminShopSelectVo> result = PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal())
                .setData(page.getRecords());
        result.setExtra(Map.of("verify_site_ids", verifySiteIds));
        return result;
    }

}