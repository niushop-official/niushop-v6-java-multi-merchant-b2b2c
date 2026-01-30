package com.niu.core.service.admin.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONPath;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.enums.diy.ComponentEnum;
import com.niu.core.enums.diy.LinkEnum;
import com.niu.core.enums.diy.PagesEnum;
import com.niu.core.enums.diy.TemplateEnum;
import com.niu.core.mapper.diy.DiyPageMapper;
import com.niu.core.service.admin.diy.IDiyRouteService;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy.param.*;
import com.niu.core.service.admin.diy.vo.DiyPageInfoVo;
import com.niu.core.service.admin.diy.vo.DiyPageListVo;
import com.niu.core.service.admin.diy.vo.DiyRouteListVo;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;


/**
 * 自定义页面实现类
 */
@Service
public class DiyServiceImpl implements IDiyService {

    @Resource
    DiyPageMapper diyPageMapper;

    @Resource
    ICoreDiyConfigService coreDiyConfigService;

    @Resource
    ICoreSysConfigService coreSysConfigService;

    @Resource
    IDiyRouteService diyRouteService;

    /**
     * 自定义页面分页列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<DiyPageListVo>
     */
    @Override
    public PageResult<DiyPageListVo> list(PageParam pageParam, DiyPageSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<DiyPage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("id");

        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) queryWrapper.like("title", searchParam.getTitle());
        if (ObjectUtil.isNotEmpty(searchParam.getMode())) queryWrapper.eq("mode", searchParam.getMode());
        if (ObjectUtil.isNotEmpty(searchParam.getType())) queryWrapper.eq("type", searchParam.getType());

        JSONObject template = TemplateEnum.getTemplate();

        IPage<DiyPage> iPage = diyPageMapper.selectPage(new Page<DiyPage>(page, limit), queryWrapper);
        List<DiyPageListVo> list = new LinkedList<>();
        for (DiyPage item : iPage.getRecords()) {
            DiyPageListVo vo = new DiyPageListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setTypeName(ObjectUtil.defaultIfNull(template.getByPath(vo.getType() + ".title", String.class), ""));
            vo.setTypePage(ObjectUtil.defaultIfNull(template.getByPath(vo.getType() + ".page", String.class), ""));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 自定义页面列表
     *
     * @param searchParam
     * @return
     */
    public List<DiyPageListVo> allList(DiyPageSearchParam searchParam) {
        QueryWrapper<DiyPage> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) queryWrapper.like("title", searchParam.getTitle());
        if (ObjectUtil.isNotEmpty(searchParam.getMode())) queryWrapper.eq("mode", searchParam.getMode());
        if (ObjectUtil.isNotEmpty(searchParam.getType())) {
            if(searchParam.getType().contains(",")){
                queryWrapper.in("type", searchParam.getType().split( ","));
            }else{
                queryWrapper.eq("type", searchParam.getType());
            }

        }

        List<DiyPage> pages = diyPageMapper.selectList(queryWrapper);
        List<DiyPageListVo> list = new LinkedList<>();

        for (DiyPage item : pages) {
            DiyPageListVo vo = new DiyPageListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 自定义页面详情
     *
     * @param id 主键参数
     * @return DiyPage
     */
    @Override
    public DiyPageInfoVo info(Integer id) {
        DiyPage model = diyPageMapper.selectOne(
                new QueryWrapper<DiyPage>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        if (model == null) return null;

        DiyPageInfoVo vo = new DiyPageInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    @Override
    public DiyPageInfoVo infoByName(String name) {
        DiyPage model = diyPageMapper.selectOne(
                new QueryWrapper<DiyPage>()
                        .eq("name", name)
                        .eq("is_default", 1)
                        .eq("site_id", RequestUtils.siteId()).last("limit 1"));

        if (model == null) return null;

        DiyPageInfoVo vo = new DiyPageInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    /**
     * 自定义页面新增
     *
     * @param addParam 参数
     */
    @Override
    public void add(DiyPageParam addParam) {
        DiyPage model = new DiyPage();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        diyPageMapper.insert(model);
    }

    /**
     * 自定义页面编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, DiyPageParam editParam) {
        DiyPage model = diyPageMapper.selectOne(
                new QueryWrapper<DiyPage>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "数据不存在!");

        BeanUtils.copyProperties(editParam, model);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        diyPageMapper.updateById(model);
    }

    /**
     * 自定义页面删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        diyPageMapper.delete(new QueryWrapper<DiyPage>().eq("id", id).eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 设为使用
     *
     * @param id
     */
    public void setUse(Integer id) {
        DiyPage model = diyPageMapper.selectOne(new QueryWrapper<DiyPage>().eq("id", id).eq("site_id", RequestUtils.siteId()));
        Assert.notNull(model, "页面不存在!");

        DiyPage update = new DiyPage();
        update.setIsDefault(0);
        diyPageMapper.update(update, new QueryWrapper<DiyPage>().eq("name", model.getName()).eq("site_id", RequestUtils.siteId()));

        update.setId(id);
        update.setIsDefault(1);
        update.setUpdateTime(System.currentTimeMillis() / 1000);
        diyPageMapper.updateById(update);
    }


    /**
     * 获取自定义链接
     * @param params 参数Map，可包含type等过滤条件
     * @return JSONObject
     */
    @Override
    public JSONObject getLink(DiyLinkParam params) {

        JSONObject linkEnum = LinkEnum.getLink();
        Integer siteId = RequestUtils.siteId();

        // 使用迭代器安全地遍历和删除
        Iterator<Map.Entry<String, Object>> iterator = linkEnum.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            JSONObject linkGroup = (JSONObject) entry.getValue();

            // 设置name属性
            linkGroup.put("name", key);

            // 处理child_list
            if (linkGroup.containsKey("child_list")) {
                JSONArray childList = linkGroup.getJSONArray("child_list");
                if (childList != null && childList.size() > 0) {
                    // 过滤child_list中的项目
                    filterChildList(childList, key, siteId, params);

                    // 如果过滤后child_list为空，则移除整个分组
                    if (childList.size() == 0) {
                        iterator.remove();
                        continue;
                    }
                }
            }

            // 处理DIY_PAGE特殊逻辑
            if (key.equals("DIY_PAGE")) {
                handleDiyPageLinks(linkGroup, siteId);
            }

            // 设置DIY_LINK的parent属性
            if (key.equals("DIY_LINK")) {
                linkGroup.put("parent", "DIY_LINK");
            }
        }

        return linkEnum;
    }

    /**
     * 过滤child_list中的项目
     */
    private void filterChildList(JSONArray childList, String parentKey, Integer siteId, DiyLinkParam params) {
        Iterator<Object> childIterator = childList.iterator();
        while (childIterator.hasNext()) {
            JSONObject childItem = (JSONObject) childIterator.next();

            // 设置parent属性
            childItem.put("parent", parentKey);

            // 处理嵌套的child_list
            if (childItem.containsKey("child_list")) {
                JSONArray nestedChildList = childItem.getJSONArray("child_list");
                if (nestedChildList != null && nestedChildList.size() > 0) {
                    // 递归过滤嵌套的child_list
                    filterNestedChildList(nestedChildList, siteId, params);

                    // 如果嵌套child_list为空，则移除该项
                    if (nestedChildList.size() == 0) {
                        childIterator.remove();
                        continue;
                    }
                }
            } else {
                // 根据link_type过滤
                if (!shouldIncludeByLinkType(childItem, siteId)) {
                    childIterator.remove();
                    continue;
                }

                // 根据url_type过滤
                if (!shouldIncludeByUrlType(childItem, params)) {
                    childIterator.remove();
                    continue;
                }
            }
        }
    }

    /**
     * 过滤嵌套的child_list
     */
    private void filterNestedChildList(JSONArray nestedChildList, Integer siteId, DiyLinkParam params) {
        Iterator<Object> nestedIterator = nestedChildList.iterator();
        while (nestedIterator.hasNext()) {
            JSONObject nestedItem = (JSONObject) nestedIterator.next();

            // 根据link_type过滤
            if (!shouldIncludeByLinkType(nestedItem, siteId)) {
                nestedIterator.remove();
                continue;
            }

            // 根据url_type过滤
            if (!shouldIncludeByUrlType(nestedItem, params)) {
                nestedIterator.remove();
                continue;
            }
        }
    }

    /**
     * 根据link_type判断是否应该包含该项
     */
    private boolean shouldIncludeByLinkType(JSONObject item, Integer siteId) {
        String linkType = item.getStr("link_type");

        if (siteId > 0) {
            // 站点模式：只显示site和all类型的链接
            return linkType == null || "site".equals(linkType) || "all".equals(linkType);
        } else {
            // 管理员模式：不显示site类型的链接
            return !"site".equals(linkType);
        }
    }

    /**
     * 根据url_type判断是否应该包含该项
     */
    private boolean shouldIncludeByUrlType(JSONObject item, DiyLinkParam params) {
        String urlType = item.getStr("url_type");
        String typeParam = params.getType();

        // 如果没有指定type参数，则过滤掉tab类型的链接
        if (typeParam == null || typeParam.isEmpty()) {
            return !"tab".equals(urlType);
        }

        return true;
    }

    /**
     * 处理DIY_PAGE的特殊链接逻辑
     */
    private void handleDiyPageLinks(JSONObject linkGroup, Integer siteId) {
        // 查询自定义页面
        QueryWrapper<DiyPage> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
                wrapper.eq("type", "DIY_PAGE")
                        .eq("site_id", siteId)
        ).or(wrapper ->
                wrapper.ne("type", "DIY_PAGE")
                        .eq("site_id", siteId)
                        .eq("is_default", 0)
        );
        queryWrapper.select("id", "title", "page_title", "name", "template", "type", "mode", "is_default", "share", "visit_count", "create_time", "update_time");
        queryWrapper.orderByDesc("update_time");

        List<DiyPage> pageList = diyPageMapper.selectList(queryWrapper);

        JSONArray childList = linkGroup.getJSONArray("child_list");
        if (childList == null) {
            childList = new JSONArray();
            linkGroup.put("child_list", childList);
        }

        for (DiyPage item : pageList) {
            JSONObject page = new JSONObject();
            page.put("name", item.getName());
            page.put("title", item.getPageTitle()); // 使用page_title字段
            page.put("url", "/app/pages/index/diy?id=" + item.getId());
            childList.add(page);
        }
    }

    /**
     * 获取自定义链接列表
     *
     * @param param
     */
    public DiyPageInfoVo getPageInit(DiyPageInitParam param) {
        JSONObject template = getTemplate(new TemplateParam());

        DiyPageInfoVo info = null;
        if (param.getId() > 0) {
            info = this.info(param.getId());
        } else if (!param.getName().isEmpty()) {
            info = this.infoByName(param.getName());
        }

        if (!param.getName().isEmpty()) {
            StartUpPageConfigVo startConfig = coreDiyConfigService.getStartUpPageConfig(RequestUtils.siteId(), param.getName());
            if (startConfig != null) {
                if (startConfig.getParent().equals("DIY_PAGE")) {
                    Integer id = Integer.parseInt(startConfig.getPage().replace("/app/pages/index/diy?id=", ""));
                    info = this.info(id);
                    if (info != null) {
                        param.setType(info.getType());
                        param.setName(info.getName());
                    }
                } else {
                    for (String key : template.keySet()) {
                        if (ObjectUtil.defaultIfNull(template.getByPath(key + ".page", String.class), "").equals(startConfig.getPage())) {
                            info = this.infoByName(key);
                            if (info != null) {
                                param.setType(key);
                                param.setName(key);
                            }
                            break;
                        }
                    }
                }
            }
        }

        if (info != null) {
            if (template.getJSONObject(info.getType()) != null) {
                JSONObject page = template.getJSONObject(info.getType());
                info.setTypeName(page.getStr("title"));
                info.setPage(page.getStr("page"));
            }
        } else {
            Long time = System.currentTimeMillis() / 1000;
            String pageTitle = ObjectUtil.defaultIfBlank(param.getTitle(), "页面" + time);
            String type = ObjectUtil.defaultIfBlank(param.getType(), "DIY_PAGE");
            String name = type.equals("DIY_PAGE") ? "DIY_PAGE_RANDOM_" + time : type;
            String typeName = "";
            String templateName = "";
            String pageRoute = "";
            String mode = "diy";
            Integer isDefault = 0;
            String value = "";

            JSONObject page = null;
            if (template.getJSONObject(param.getName()) != null) {
                page = template.getJSONObject(param.getName());
                type = name = param.getName();
                pageTitle = typeName = page.getStr("title");
                pageRoute = page.getStr("page");
                JSONObject pageData = this.getFirstPageData(type, "");
//                if (pageData != null) {
//                    mode = pageData.getByPath("template.mode", String.class);
//                    value = pageData.getJSONObject("template").toString();
//                    isDefault = 1;
//                }

                if (pageData != null) {
                    mode = JSONPath.eval(pageData, "$.mode") + "";
                    value = JSONPath.eval(pageData, "$.data") + "";
                    isDefault = 1;
                }
            } else if (template.getJSONObject(param.getType()) != null) {
                page = template.getJSONObject(param.getType());
                typeName = page.getStr("title");
                pageRoute = page.getStr("page");
            }

            info = new DiyPageInfoVo();
            info.setName(name);
            info.setPageTitle(pageTitle);
            info.setTitle(pageTitle);
            info.setType(type);
            info.setTypeName(typeName);
            info.setTemplate(templateName);
            info.setPage(pageRoute);
            info.setMode(mode);
            info.setValue(value);
            info.setIsDefault(isDefault);
        }

        info.setComponent(getComponentList(info.getType()));
        info.setDomainUrl(coreSysConfigService.getSceneDomain());

        return info;
    }

    public JSONObject getComponentList(String type) {

        JSONObject ComponentList = ComponentEnum.getComponent();
        JSONObject res = new JSONObject();
        for (String formComponentObj : ComponentList.keySet()) {
            JSONObject formComponent = ComponentList.getJSONObject(formComponentObj);
            JSONObject list = formComponent.getJSONObject("list");
            if (list == null || list.isEmpty()) {
                continue;
            }
            Iterator<Map.Entry<String, Object>> iterator = list.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                String componentKey = entry.getKey();
                JSONObject cv = JSONUtil.parseObj(entry.getValue());
                if (cv.containsKey("support_page")) {
                    JSONArray support = cv.getJSONArray("support_page");
                    if (ObjectUtil.isNotEmpty(type)) {
                        if (support != null && support.size() > 0 && !support.contains(type)) {
                            iterator.remove();
                            continue;
                        }
                    }
                    if (RequestUtils.siteId() > 0) {
                        if ("MallGoodsRanking".equals(componentKey) || "MallShopList".equals(componentKey)) {
                            iterator.remove();
                            continue;
                        }
                    }

                }
                cv.remove("sort");
                cv.remove("support_page");
            }
            // 根据 sort 排序
            iterator = list.entrySet().iterator();
            if(iterator.hasNext())
            {
                formComponent.put("list", sortJSONObjectBySortField(list));
                res.put(formComponentObj, formComponent);
            }

        }

        return res;
    }

    private JSONObject sortJSONObjectBySortField(JSONObject originalJsonObject) {
        // 提取所有内部 JSONObject 到一个 List 中
        List<Map.Entry<String, Object>> entryList = new ArrayList<>(originalJsonObject.entrySet());

        // 对 List 进行排序
        Collections.sort(entryList, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                JSONObject jsonObj1 = (JSONObject) o1.getValue();
                JSONObject jsonObj2 = (JSONObject) o2.getValue();
                int sort1 = jsonObj1.getInt("sort");
                int sort2 = jsonObj2.getInt("sort");
                return Integer.compare(sort1, sort2);
            }
        });

        // 创建一个新的 JSONObject 来存放排序后的结果
        JSONObject sortedJsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : entryList) {
            sortedJsonObject.put(entry.getKey(), entry.getValue());
        }

        return sortedJsonObject;
    }

    /**
     * 获取默认页面数据
     *
     * @param type
     * @param addon
     * @return
     */
    public JSONObject getFirstPageData(String type, String addon) {
        JSONObject pages = PagesEnum.getPagesByAddon(type, addon);
        if (pages == null || pages.keySet().size() == 0) return null;

        String template = pages.keySet().iterator().next();
        JSONObject data = pages.getJSONObject(template);
        data.set("type", type);
        data.set("template", template);

        return data;
    }

    /**
     * 获取页面模板
     *
     * @return
     */
    public JSONObject getTemplate(TemplateParam param) {

        if (RequestUtils.siteId().equals(RequestUtils.defaultSiteId())) {
            param.setAppType("admin");
        } else {
            param.setAppType("site");
        }

        JSONObject template = TemplateEnum.getTemplate(param);

        for (String key : template.keySet()) {
            JSONObject pages = ObjectUtil.defaultIfNull(PagesEnum.getPages(key, param.getMode()), new JSONObject());
            template.putByPath(key + ".template", pages);
        }

        return template;
    }

    /**
     * 设置启动页
     *
     * @param value
     */
    public void changeTemplate(StartUpPageConfigParam value) {
        this.coreDiyConfigService.setStartUpPageConfig(RequestUtils.siteId(), value, value.getType());
    }

    /**
     * 获取装修页面列表
     *
     * @param searchParam
     */
    public JSONObject getDecoratePage(DiyPageSearchParam searchParam) {
        TemplateParam templateParam = new TemplateParam();
        String[] key = {searchParam.getType()};
        templateParam.setKey(key);
        JSONObject template = this.getTemplate(templateParam).getJSONObject(searchParam.getType());
        if (template == null) throw new CommonException("模板不存在");

        JSONObject defaultPage = getFirstPageData(searchParam.getType(), "");

        JSONObject useTemplate = new JSONObject();
        useTemplate.put("type", searchParam.getType());
        useTemplate.put("title", defaultPage == null ? "" : defaultPage.getStr("title", ""));
        useTemplate.put("name", "");
        useTemplate.put("cover", defaultPage == null ? "" : defaultPage.getStr("cover", ""));
        useTemplate.put("page", template.getStr("page"));
        useTemplate.put("action", template.getStr("action"));
        useTemplate.put("url", "");
        useTemplate.put("parent", "");

        DiyPageInfoVo info = infoByName(searchParam.getType());

        StartUpPageConfigVo startConfig = coreDiyConfigService.getStartUpPageConfig(RequestUtils.siteId(), searchParam.getType());
        if (startConfig != null) {
            useTemplate.set("title", startConfig.getTitle());
            useTemplate.set("name", startConfig.getName());
            useTemplate.set("page", startConfig.getPage());
            useTemplate.set("action", startConfig.getAction());
            useTemplate.set("url", startConfig.getPage());
            useTemplate.set("parent", startConfig.getParent());
        } else if (info != null) {
            useTemplate.set("id", info.getId());
            useTemplate.set("title", info.getTitle());
        }

        if (useTemplate.getStr("cover").isEmpty() && useTemplate.getStr("url").isEmpty()) {
            useTemplate.set("url", template.getStr("page"));
        } else if (useTemplate.getStr("url").isEmpty()) {
            useTemplate.set("url", template.getStr("page"));
        }

        DiyRouteSearchParam diyRouteSearchParam = new DiyRouteSearchParam();
        diyRouteSearchParam.setUrl(useTemplate.getStr("page"));
        List<DiyRouteListVo> otherPage = diyRouteService.list(diyRouteSearchParam);
        if (otherPage.size() > 0) {
            DiyRouteListVo route = otherPage.get(0);
            useTemplate.set("title", route.getTitle());
            useTemplate.set("name", route.getName());
            useTemplate.set("parent", route.getParent());
            useTemplate.set("action", route.getAction());
        }

        template.put("use_template", useTemplate);

        SceneDomainVo sceneDomain = coreSysConfigService.getSceneDomain();
        JSONObject domainUrl = new JSONObject();
        domainUrl.put("wap_domain", sceneDomain.getWapDomain());
        domainUrl.put("wap_url", sceneDomain.getWapUrl());
        domainUrl.put("web_url", sceneDomain.getWebUrl());
        template.put("domain_url", domainUrl);

        return template;
    }

    /**
     * 获取自定义页面分页列表，轮播搜索组件用
     */
    public PageResult<DiyPageListVo> getPageByCarouselSearch(PageParam pageParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<DiyPage> queryWrapper = new QueryWrapper<DiyPage>()
                .eq("site_id", RequestUtils.siteId())
                .eq("type", "DIY_PAGE")
                .notIn("value", Arrays.asList("top_fixed", "right_fixed", "bottom_fixed", "left_fixed", "fixed"))
                .or()
                .eq("site_id", RequestUtils.siteId())
                .ne("type", "DIY_PAGE")
                .eq("is_default", 0)
                .notIn("value", Arrays.asList("top_fixed", "right_fixed", "bottom_fixed", "left_fixed", "fixed"))
                .orderByDesc("id");

        IPage<DiyPage> iPage = diyPageMapper.selectPage(new Page<DiyPage>(page, limit), queryWrapper);
        List<DiyPageListVo> list = new LinkedList<>();
        for (DiyPage item : iPage.getRecords()) {
            DiyPageListVo vo = new DiyPageListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 设置 首页/个人中心 的第一个模板 设置为启动页
     *
     * @param param
     */
    public void setDiyData(SetDiyDataParam param) {
        String addonFlag = param.getKey();

        TemplateParam templateParam = new TemplateParam();
        String[] key = {param.getKey()};
        templateParam.setKey(key);
        JSONObject template = getTemplate(templateParam).getJSONObject(param.getKey());
        if (template == null) return;

        if (!param.getAddon().isEmpty()) {
            TemplateParam addonTemplateParam = new TemplateParam();
            addonTemplateParam.setAddon(param.getAddon());
            addonTemplateParam.setType(param.getType());
            JSONObject addonTemplate = getTemplate(addonTemplateParam);
            if (addonTemplate != null) {
                addonFlag = addonTemplate.keySet().iterator().next();
                template = addonTemplate.getJSONObject(addonFlag);
            }
        }

        JSONObject pages = template.getJSONObject("template");
        if (pages.isEmpty()) return;

        String pageKey = pages.keySet().iterator().next();
        JSONObject page = pages.getJSONObject(pageKey);

        DiyPage info = diyPageMapper.selectOne(new QueryWrapper<DiyPage>()
                .eq("name", addonFlag)
                .eq("site_id", param.getSiteId())
                .eq("is_default", 1)
        );

        if (info == null) {
            RequestUtils.setSiteId(param.getSiteId());
            DiyPageParam addParam = new DiyPageParam();
            addParam.setTitle(page.getStr("title", ""));
            addParam.setPageTitle(page.getStr("title", ""));
            addParam.setName(addonFlag);
            addParam.setType(addonFlag);
            addParam.setTemplate(pageKey);
            addParam.setValue(page.getJSONObject("data").toString());
            addParam.setMode(page.getStr("mode", ""));
            addParam.setIsDefault(1);
            addParam.setIsChange(0);
            this.add(addParam);
        } else {
            if (info.getName().equals("DIY_INDEX") && info.getType().equals("DIY_INDEX")) {
                DiyPage update = new DiyPage();
                update.setId(info.getId());
                update.setValue(page.getJSONObject("data").toString());
                diyPageMapper.updateById(update);
            }
        }

        if (param.getIsStart().equals(1)) {
            StartUpPageConfigParam startPage = new StartUpPageConfigParam();
            startPage.setType(param.getKey());
            startPage.setMode(page.getStr("mode", ""));
            startPage.setTitle(page.getStr("title", ""));
            startPage.setAction(template.getStr("action", ""));
            startPage.setPage(template.getStr("page"));
            this.changeTemplate(startPage);
        }
    }

    /**
     * 复制
     * @param id
     */
    @Override
    public void copy(Integer id) {
        DiyPage page = diyPageMapper.selectOne(new QueryWrapper<DiyPage>().eq("id", id).eq("site_id", RequestUtils.siteId()));
        if (page == null) throw new CommonException("页面不存在");

        page.setId(null);
        page.setPageTitle(page.getPageTitle() + "_副本");
        page.setIsChange(0);
        page.setIsDefault(0);
        page.setShare("");
        page.setCreateTime(System.currentTimeMillis() / 1000);
        diyPageMapper.insert(page);
    }

    @Override
    public int selectCount(QueryWrapper queryWrapper) {
        Long count = diyPageMapper.selectCount(queryWrapper);
        return count.intValue();
    }

    @Override
    public int initDefaultPage(Integer siteId, String flag, String addon) {
        JSONObject template = getFirstPageData(flag, addon);
        Long count = diyPageMapper.selectCount(new QueryWrapper<DiyPage>()
        .eq("site_id", siteId).eq("name", flag).eq("is_default", 1).eq("template", template.getStr("template")));
        if(count.intValue() == 0){
            DiyPage  page = new DiyPage();
            page.setSiteId(siteId);
            page.setPageTitle(template.getStr("title"));
            page.setTitle(template.getStr("title"));
            page.setType(flag);
            page.setName(flag);
            page.setTemplate(template.getStr("template"));
            page.setMode(template.getStr("mode"));
            page.setValue(template.getJSONObject("data").toString());
            page.setIsDefault(1);
            page.setIsChange(0);
            page.setCreateTime(System.currentTimeMillis() / 1000);
            diyPageMapper.insert( page);
            setUse(page.getId());
        }
        return 0;
    }
}
