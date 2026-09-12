package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateFormatUtils;
import com.niu.core.entity.sys.SysAttachment;
import com.niu.core.entity.sys.SysAttachmentCategory;
import com.niu.core.mapper.sys.SysAttachmentCategoryMapper;
import com.niu.core.mapper.sys.SysAttachmentMapper;
import com.niu.core.service.admin.sys.ISysAttachmentService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;
import com.niu.core.service.admin.sys.vo.IconCategoryVo;
import com.niu.core.service.admin.sys.vo.IconGlyphVo;
import com.niu.core.service.admin.sys.vo.SysAttachmentCategoryListVo;
import com.niu.core.service.admin.sys.vo.SysAttachmentConfigVo;
import com.niu.core.service.admin.sys.vo.SysAttachmentListVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.upload.ICoreStorageService;
import com.niu.core.common.component.upload.IUploadProvider;
import com.niu.core.common.component.upload.factory.UploadProviderFactory;
import com.niu.core.common.component.upload.model.Base64Model;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 附件管理实现
 */
@Service
public class SysAttachmentServiceImpl implements ISysAttachmentService {

    @Resource
    SysAttachmentMapper sysAttachmentMapper;

    @Resource
    SysAttachmentCategoryMapper sysAttachmentCategoryMapper;

    @Resource
    ICoreUploadService coreUploadService;

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    ICoreStorageService coreStorageService;

    /**
     * 附件管理列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysAttachmentListVo>
     */
    @Override
    public PageResult<SysAttachmentListVo> list(PageParam pageParam, SysAttachmentSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysAttachment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("att_id");
        if (ObjectUtil.isNotEmpty(searchParam.getAttType())) queryWrapper.eq("att_type", searchParam.getAttType());
        if (ObjectUtil.isNotEmpty(searchParam.getCateId()) && searchParam.getCateId() > 0) queryWrapper.eq("cate_id", searchParam.getCateId());
        if (ObjectUtil.isNotEmpty(searchParam.getRealName())) queryWrapper.like("real_name", searchParam.getRealName());

        IPage<SysAttachment> iPage = sysAttachmentMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysAttachmentListVo> list = new LinkedList<>();
        for (SysAttachment item : iPage.getRecords()) {
            SysAttachmentListVo vo = new SysAttachmentListVo();
            BeanUtils.copyProperties(item, vo);

            try {
                String thumb = CommonUtils.thumbImageSmall(RequestUtils.siteId(), item.getUrl());
                vo.setThumb(ObjectUtil.isEmpty(thumb) ? item.getUrl() : thumb);
            } catch (Exception e) {
                // 缩略图生成失败，使用原图
                vo.setThumb(item.getUrl());
            }
            
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 上传图片
     *
     * @param param
     */
    @Override
    public AttachmentUploadVo image(AttachmentUploadParam param) {
        param.setSiteId(RequestUtils.siteId());
        param.setAttType("image");
        param.setDir("attachment/image/" + param.getSiteId() + "/" + DateFormatUtils.getUploadFormat() + "/");
        return coreUploadService.upload(param);
    }

    /**
     * 上传视频
     *
     * @param param
     */
    @Override
    public AttachmentUploadVo video(AttachmentUploadParam param) {
        param.setSiteId(RequestUtils.siteId());
        param.setAttType("video");
        param.setDir("attachment/video/" + param.getSiteId() + "/" + DateFormatUtils.getUploadFormat() + "/");
        return coreUploadService.upload(param);
    }

    /**
     * 上传文档
     *
     * @param param
     */
    @Override
    public AttachmentUploadVo document(AttachmentUploadParam param) {
        param.setSiteId(RequestUtils.siteId());
        param.setIsAttachment(0);
        param.setAttType("document");
        param.setStorageType("local");
        param.setDir("attachment/document/" + param.getDocumentType() + "/" + param.getSiteId() + "/" + DateFormatUtils.getUploadFormat() + "/");
        return coreUploadService.upload(param);
    }

    /**
     * base64图片上传
     */
    @Override
    public AttachmentUploadVo base64(AttachmentUploadParam param) {
        if (StrUtil.isBlank(param.getContent())) {
            throw new CommonException("base图片资源不能为空");
        }
        param.setSiteId(RequestUtils.siteId());
        param.setIsAttachment(1);
        param.setAttType("image");
        param.setStorageType("local");
        param.setDir("attachment/image/" + param.getSiteId() + "/" + DateFormatUtils.getUploadFormat() + "/");
        return coreUploadService.uploadBase64(param);
    }

    /**
     * 移动图片分组
     */
    @Override
    public void batchMoveCategory(SysAttachmentMoveParam param) {
        SysAttachment model = new SysAttachment();
        model.setCateId(param.getCateId());
        sysAttachmentMapper.update(model, new QueryWrapper<SysAttachment>()
                .eq("site_id", RequestUtils.siteId())
                .in("att_id", param.getAttIds()));
    }

    /**
     * 附件删除
     */
    @Override
    public void del(SysAttachmentDelParam param) {
        List<SysAttachment> sysAttachmentList = sysAttachmentMapper.selectList(
                new QueryWrapper<SysAttachment>()
                        .eq("site_id", RequestUtils.siteId())
                        .in("att_id", param.getAttIds()));
        if (sysAttachmentList.isEmpty()) {
            throw new CommonException("请选择要删除的附件");
        }
        for (SysAttachment sysAttachment : sysAttachmentList) {
            coreUploadService.delete(sysAttachment.getSiteId(), sysAttachment.getStorageType(), sysAttachment.getPath());
        }
        sysAttachmentMapper.delete(new QueryWrapper<SysAttachment>().eq("site_id", RequestUtils.siteId()).in("att_id", param.getAttIds()));
    }

    /**
     * 获取分组列表
     *
     * @param searchParam
     * @return
     */
    public List<SysAttachmentCategoryListVo> getCategoryList(SysAttachmentCategorySearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();

        MPJQueryWrapper<SysAttachmentCategory> queryWrapper = new MPJQueryWrapper<SysAttachmentCategory>();
        queryWrapper.select("id,name,type").eq("site_id", siteId);
        if (ObjectUtil.isNotEmpty(searchParam.getName())) queryWrapper.like("name", searchParam.getName());
        if (ObjectUtil.isNotEmpty(searchParam.getType())) queryWrapper.eq("type", searchParam.getType());

        List<SysAttachmentCategory> categorys = sysAttachmentCategoryMapper.selectList(queryWrapper); // 调用 selectList 方法

        List<SysAttachmentCategoryListVo> list = new LinkedList<>();
        for (SysAttachmentCategory item : categorys) {
            SysAttachmentCategoryListVo vo = new SysAttachmentCategoryListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 添加分组
     *
     * @param addParam
     */
    public void addCategory(SysAttachmentCategoryParam addParam) {
        if (ObjectUtil.isEmpty(addParam.getType())) throw new CommonException("type参数不能为空");

        SysAttachmentCategory model = new SysAttachmentCategory();
        model.setSiteId(RequestUtils.siteId());
        model.setType(addParam.getType());
        model.setName(addParam.getName());
        sysAttachmentCategoryMapper.insert(model);
    }

    /**
     * 编辑分组
     *
     * @param id
     * @param editParam
     */
    public void editCategory(Integer id, SysAttachmentCategoryParam editParam) {
        Integer siteId = RequestUtils.siteId();

        UpdateWrapper<SysAttachmentCategory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId).eq("id", id);

        SysAttachmentCategory model = new SysAttachmentCategory();
        model.setName(editParam.getName());

        sysAttachmentCategoryMapper.update(model, updateWrapper);
    }

    /**
     * 删除分组
     *
     * @param id
     */
    public void delCategory(Integer id) {
        Integer siteId = RequestUtils.siteId();

        QueryWrapper<SysAttachmentCategory> queryWrapper = new QueryWrapper<SysAttachmentCategory>()
                .eq("site_id", siteId)
                .eq("id", id);

        sysAttachmentCategoryMapper.delete(queryWrapper);
    }

    /**
     * 获取相册设置
     *
     * @return 相册配置
     */
    @Override
    public SysAttachmentConfigVo getConfig() {
        Integer siteId = RequestUtils.siteId();
        JSONObject config = coreConfigService.getConfigValue(siteId, "ALBUM_CONFIG");
        
        SysAttachmentConfigVo vo = new SysAttachmentConfigVo();
        if (config == null || config.isEmpty()) {
            vo.setIsCropper(0);
        } else {
            vo.setIsCropper(config.getInt("is_cropper", 0));
        }
        return vo;
    }

    /**
     * 设置相册设置
     *
     * @param param 配置参数
     */
    @Override
    public void setConfig(SysAttachmentConfigParam param) {
        Integer siteId = RequestUtils.siteId();
        JSONObject configObject = new JSONObject();
        configObject.set("is_cropper", param.getIsCropper() != null ? param.getIsCropper() : 0);
        coreConfigService.setConfig(siteId, "ALBUM_CONFIG", configObject);
    }

    /**
     * 加载 iconfont.json 资源
     */
    private List<IconCategoryVo> loadIconData() {
        try {
            ClassPathResource resource = new ClassPathResource("core/icon/iconfont.json");
            InputStream inputStream = resource.getInputStream();
            String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
            List<IconCategoryVo> list = JSONUtil.toList(json, IconCategoryVo.class);
            // PHP 逻辑：只有一个分类时直接返回；多个时将最后一个移到第一位
            if (list.size() > 1) {
                IconCategoryVo last = list.remove(list.size() - 1);
                list.add(0, last);
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取图标分类列表
     */
    @Override
    public List<IconCategoryVo> getIconCategoryList(String name) {
        List<IconCategoryVo> list = loadIconData();
        // 不返回 glyphs 字段（与 PHP 一致）
        list.forEach(cat -> cat.setGlyphs(null));
        // 名称筛选
        if (StrUtil.isNotEmpty(name)) {
            list = list.stream()
                    .filter(cat -> cat.getName() != null && cat.getName().contains(name))
                    .collect(Collectors.toList());
        }
        return list;
    }

    /**
     * 获取图标列表（分页）
     */
    @Override
    public Map<String, Object> getIconList(Integer page, Integer limit, String cateId, String realName) {
        List<IconCategoryVo> categories = loadIconData();
        List<IconGlyphVo> allIcons = new ArrayList<>();

        for (IconCategoryVo cat : categories) {
            if (cat.getGlyphs() == null) continue;

            List<IconGlyphVo> glyphs = cat.getGlyphs();
            for (IconGlyphVo glyph : glyphs) {
                // 组装字段（与 PHP 一致）
                glyph.setAttId(glyph.getIconId());
                glyph.setUrl(cat.getFontFamily() + "-" + cat.getCssPrefixText() + glyph.getFontClass());
                glyph.setRealName(glyph.getName());

                // 名称筛选
                if (StrUtil.isNotEmpty(realName) && (glyph.getName() == null || !glyph.getName().contains(realName))) {
                    continue;
                }
                allIcons.add(glyph);
            }

            // 指定分类时只取该分类
            if (StrUtil.isNotEmpty(cateId) && cateId.equals(cat.getId())) {
                break;
            }
        }

        // 手动分页（与 PHP 一致）
        int total = allIcons.size();
        int currentPage = page != null && page > 0 ? page : 1;
        int perPage = limit != null && limit > 0 ? limit : 20;
        int start = (currentPage - 1) * perPage;
        int end = Math.min(start + perPage, total);

        List<IconGlyphVo> pageData = start < total ? allIcons.subList(start, end) : new ArrayList<>();

        Map<String, Object> result = new HashMap<>();
        result.put("current_page", currentPage);
        result.put("per_page", perPage);
        result.put("data", pageData);
        result.put("total", total);
        return result;
    }

}
