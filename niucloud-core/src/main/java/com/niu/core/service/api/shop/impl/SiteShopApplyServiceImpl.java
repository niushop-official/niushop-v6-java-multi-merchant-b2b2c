package com.niu.core.service.api.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.site.SiteApply;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.shop.ShopApplyEnum;
import com.niu.core.mapper.site.SiteApplyMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.shop.IShopConfigService;
import com.niu.core.service.api.shop.ISiteShopApplyService;
import com.niu.core.service.api.shop.param.SiteShopApplyParam;
import com.niu.core.service.api.shop.param.SiteShopApplySearchParam;
import com.niu.core.service.api.shop.param.SiteShopApplyVerifyParam;
import com.niu.core.service.api.shop.vo.SiteShopApplyInfoVo;
import com.niu.core.service.api.shop.vo.SiteShopApplyListVo;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsInfoVo;
import com.niu.core.service.core.shop.vo.ShopApplyConfigInfoVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.user.ICoreUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 商户入驻申请实现
 * v120迁移：基于 site_apply 表
 */
@Service
@Slf4j
public class SiteShopApplyServiceImpl implements ISiteShopApplyService {

    @Resource
    private SiteApplyMapper siteApplyMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private IRegisterService registerService;
    @Resource
    private ICoreUserService coreUserService;
    @Resource
    private ICoreConfigService coreConfigService;
    @Resource
    private ICoreDiyFormRecordsService coreDiyFormRecordsService;
    @Resource
    private IShopConfigService shopConfigService;
    @Resource
    private DiyFormMapper diyFormMapper;
    @Resource
    private com.niu.core.service.api.diy.IDiyFormService diyFormService;
    

    @Override
    public PageResult<SiteShopApplyListVo> page(PageParam pageParam, SiteShopApplySearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        MPJQueryWrapper<SiteApply> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("nm.nickname, nssa.apply_id, nssa.site_id, nssa.member_id, nssa.site_name, nssa.user_name, nssa.user_mobile, nssa.category_id, nssa.child_category_id, nssa.circle_id, nssa.business_license, nssa.status, nssa.create_time, nssa.update_time, nssa.audit_time, nssa.refuse_reason")
                .setAlias("nssa")
                .leftJoin("?_member nm ON nssa.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nssc ON nssa.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nsscc ON nssa.child_category_id = nsscc.category_id".replace("?_", GlobalConfig.tablePrefix));

        // API端：按当前会员ID过滤
        queryWrapper.eq("nssa.member_id", RequestUtils.memberId());

        // 区域代理过滤
        List<Integer> circleIds = RequestUtils.circleIds();
        if (ObjectUtil.isNotEmpty(circleIds)) {
            queryWrapper.in("nssa.circle_id", circleIds);
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("nssa.status", searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "nssa.create_time", searchParam.getCreateTime());
        }
        queryWrapper.orderByDesc("nssa.create_time");

        IPage<SiteShopApplyListVo> iPage = siteApplyMapper.selectJoinPage(new Page<>(page, limit), SiteShopApplyListVo.class, queryWrapper);
        return PageResult.build(iPage);
    }

    @Override
    public SiteShopApplyInfoVo info(Integer id) {
        log.info("【商户入驻申请详情查询】开始，apply_id: {}", id);
        
        MPJQueryWrapper<SiteApply> queryWrapper = new MPJQueryWrapper<>();
        // PHP返回的字段：apply_id, member_id, site_name, user_name, user_mobile, category_id, child_category_id, circle_id, business_license, status, create_time, update_time, audit_time, refuse_reason, form_record_id
        queryWrapper.select("nm.nickname, nssc.name as category_name, nsscc.name as child_category_name, nssa.apply_id, nssa.member_id, nssa.site_name, nssa.user_name, nssa.user_mobile, nssa.category_id, nssa.child_category_id, nssa.circle_id, nssa.business_license, nssa.status, nssa.create_time, nssa.update_time, nssa.audit_time, nssa.refuse_reason, nssa.form_record_id")
                .setAlias("nssa")
                .leftJoin("?_member nm ON nssa.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nssc ON nssa.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nsscc ON nssa.child_category_id = nsscc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("nssa.apply_id", id);

        log.info("【商户入驻申请详情查询】SQL查询条件: apply_id={}", id);
        
        SiteShopApplyInfoVo infoVo = siteApplyMapper.selectJoinOne(SiteShopApplyInfoVo.class, queryWrapper);
        
        log.info("【商户入驻申请详情查询】SQL查询结果: {}", infoVo != null ? "查询成功" : "未找到记录");
        if (infoVo != null) {
            log.info("【商户入驻申请详情查询】店铺名称: {}, 状态: {}, 申请时间: {}", 
                    infoVo.getSiteName(), infoVo.getStatus(), infoVo.getCreateTime());
        }
        
        // 设置状态名称
        if (infoVo != null && infoVo.getStatus() != null) {
            String statusName = ShopApplyEnum.getNameByStatus(infoVo.getStatus());
            infoVo.setStatusName(statusName);
            log.info("【商户入驻申请详情查询】设置状态名称: status={}, status_name={}", infoVo.getStatus(), statusName);
        } else if (infoVo != null && infoVo.getStatus() == null) {
            log.warn("【商户入驻申请详情查询】状态值为null");
        }
        
        // 获取表单信息（与PHP DiyFormService::getFormRecordInfo保持一致）
        if (infoVo != null && infoVo.getFormRecordId() != null && infoVo.getFormRecordId() > 0) {
            log.info("【商户入驻申请详情查询】开始获取表单信息, form_record_id={}", infoVo.getFormRecordId());
            try {
                // 使用 IDiyFormService.getFormRecordInfo() 获取表单信息，与PHP代码保持一致
                Object formInfo = diyFormService.getFormRecordInfo(infoVo.getFormRecordId());
                log.info("【商户入驻申请详情查询】表单信息获取结果: {}", formInfo != null ? "成功" : "失败");
                
                Map<String, Object> formMap = new HashMap<>();
                if (formInfo != null) {
                    // 将返回的对象转换为Map
                    formMap = cn.hutool.core.bean.BeanUtil.beanToMap(formInfo);
                }
                infoVo.setFormInfo(formMap);
                log.info("【商户入驻申请详情查询】表单信息设置完成");
            } catch (Exception e) {
                // 忽略获取表单信息时的异常
                infoVo.setFormInfo(new HashMap<>());
                log.error("【商户入驻申请详情查询】获取表单信息异常: {}", e.getMessage(), e);
            }
        } else {
            if (infoVo != null) {
                infoVo.setFormInfo(new HashMap<>());
                log.info("【商户入驻申请详情查询】form_record_id为空或无效, 跳过表单信息获取");
            }
        }
        
        log.info("【商户入驻申请详情查询】结束, 返回结果: {}", infoVo != null ? "非空" : "空");
        return infoVo;
    }

    @Override
    public void verify(Integer id, SiteShopApplyVerifyParam verifyParam) {
        SiteApply model = siteApplyMapper.selectOne(
                new QueryWrapper<SiteApply>()
                        .eq("apply_id", id));
        Assert.notNull(model, "商户入驻申请不存在");

        model.setStatus(verifyParam.getStatus());
        model.setRefuseReason(verifyParam.getRefuseReason());
        if (verifyParam.getStatus().equals(2)) {
            model.setAuditTime(System.currentTimeMillis() / 1000);
        }
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        siteApplyMapper.updateById(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SiteShopApplyParam addParam) {
        // 校验手机验证码并创建账号
        checkMobileAndCreateAccount(addParam);

        SiteApply model = new SiteApply();
        model.setSiteName(addParam.getSiteName());
        model.setCategoryId(addParam.getCategoryId());
        model.setChildCategoryId(addParam.getChildCategoryId());
        model.setCircleId(addParam.getCircleId() != null ? addParam.getCircleId() : 0);
        model.setBusinessLicense(addParam.getBusinessLicense());
        model.setStatus(addParam.getStatus());
        model.setUid(addParam.getUid() != null ? addParam.getUid() : 0);
        model.setMemberId(RequestUtils.memberId());
        model.setCreateTime(DateUtils.currTime());
        model.setApplyType("member");
        model.setAddon("mall");
        model.setUserName(addParam.getUserName());
        model.setUserMobile(addParam.getUserMobile());
        
        // 保存表单数据
        Integer formRecordId = saveFormData(addParam.getFormData());
        if (formRecordId != null) {
            model.setFormRecordId(formRecordId);
        }

        siteApplyMapper.insert(model);
        return model.getApplyId().longValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Integer id, SiteShopApplyParam editParam) {
        // 校验手机验证码并创建账号
        checkMobileAndCreateAccount(editParam);

        SiteApply model = siteApplyMapper.selectOne(
                new QueryWrapper<SiteApply>()
                        .eq("apply_id", id));
        Assert.notNull(model, "商户入驻申请不存在");

        model.setSiteName(editParam.getSiteName());
        model.setUserName(editParam.getUserName());
        model.setUserMobile(editParam.getUserMobile());
        model.setCategoryId(editParam.getCategoryId());
        model.setChildCategoryId(editParam.getChildCategoryId());
        model.setCircleId(editParam.getCircleId() != null ? editParam.getCircleId() : 0);
        model.setBusinessLicense(editParam.getBusinessLicense());
        model.setStatus(editParam.getStatus());
        model.setUid(editParam.getUid());
        model.setRefuseReason("");
        model.setUpdateTime(DateUtils.currTime());
        
        // 更新表单数据
        Integer oldFormRecordId = model.getFormRecordId();
        Integer newFormRecordId = updateFormData(oldFormRecordId, editParam.getFormData());
        if (newFormRecordId != null) {
            model.setFormRecordId(newFormRecordId);
        }

        siteApplyMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        siteApplyMapper.delete(new QueryWrapper<SiteApply>().eq("apply_id", id).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer shopApply(SiteShopApplyParam addParam) {
        // 校验手机验证码 - 暂时注释，方便测试
        // registerService.checkMobileCode(addParam.getUserMobile(), addParam.getMobileKey(), addParam.getMobileCode());

        SiteApply model = new SiteApply();
        model.setBusinessLicense(addParam.getBusinessLicense());
        model.setCategoryId(addParam.getCategoryId());
        model.setChildCategoryId(addParam.getChildCategoryId());
        model.setSiteName(addParam.getSiteName());
        model.setUserMobile(addParam.getUserMobile());
        model.setUserName(addParam.getUserName());
        model.setMemberId(RequestUtils.memberId());
        model.setCreateTime(DateUtils.currTime());
        model.setUpdateTime(DateUtils.currTime());
        model.setUid(addParam.getUid());
        model.setStatus(1);
        model.setApplyType("member");
        model.setAddon("mall");
        model.setCircleId(addParam.getCircleId() != null ? addParam.getCircleId() : 0);
        
        // 打印日志：验证 form_data 是否被正确接收
        Object formData = addParam.getFormData();
        log.info("【商户入驻申请】formData 是否为 null: {}", formData == null);
        if (formData != null) {
            log.info("【商户入驻申请】formData 类型：{}", formData.getClass().getName());
            log.info("【商户入驻申请】formData 内容：{}", formData.toString());
            
            // 如果是 Map 类型，打印每个元素的详细信息
            if (formData instanceof Map) {
                Map<?, ?> mapData = (Map<?, ?>) formData;
                log.info("【商户入驻申请】formData Map 大小：{}", mapData.size());
                for (Map.Entry<?, ?> entry : mapData.entrySet()) {
                    log.info("【商户入驻申请】formData key={}, valueType={}, value={}", 
                        entry.getKey(), 
                        entry.getValue() != null ? entry.getValue().getClass().getName() : "null",
                        entry.getValue());
                }
            }
        }
        
        // 保存表单数据
        // 前端发送的 form_data 包含 form_id, value, relate_id，我们需要提取 value 字段（表单组件数组）
        Object formValue = formData;
        if (formData instanceof Map) {
            Map<?, ?> mapData = (Map<?, ?>) formData;
            if (mapData.containsKey("value")) {
                formValue = mapData.get("value");
                log.info("【商户入驻申请】提取 form_data.value 作为表单数据，类型：{}", formValue != null ? formValue.getClass().getName() : "null");
            }
        }
        Integer formRecordId = saveFormData(formValue);
        log.info("【商户入驻申请】保存表单后 formRecordId: {}", formRecordId);
        if (formRecordId != null) {
            model.setFormRecordId(formRecordId);
            log.info("【商户入驻申请】已设置 formRecordId 到 model: {}", model.getFormRecordId());
        } else {
            log.warn("【商户入驻申请】formRecordId 为 null，未设置到 model 中");
        }

        siteApplyMapper.insert(model);
        log.info("【商户入驻申请】插入数据库后 applyId: {}, formRecordId: {}", model.getApplyId(), model.getFormRecordId());
        return model.getApplyId();
    }

    @Override
    public List<Map<String, Object>> statusList() {
        Map<Integer, String> stringMap = ShopApplyEnum.getStatus();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : stringMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("label", entry.getValue());
            map.put("value", entry.getKey());
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public Boolean applyCheck() {
        QueryWrapper<SiteApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.in("status", Arrays.asList(ShopApplyEnum.WAIT_AUDIT.getValue(), ShopApplyEnum.PASS.getValue()));
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 1");
        SiteApply siteApply = siteApplyMapper.selectOne(queryWrapper);
        return siteApply != null;
    }

    @Override
    public Map<String, Object> checkAccount(String account) {
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq("username", account).select("uid", "username", "head_img", "mobile");
        SysUser sysUser = sysUserMapper.selectOne(qw);

        Map<String, Object> info = new HashMap<>();
        if (sysUser != null) {
            info.put("uid", sysUser.getUid());
            info.put("username", sysUser.getUsername());
            info.put("head_img", sysUser.getHeadImg());

            String mobile = sysUser.getMobile();
            if (mobile != null && mobile.matches("^1\\d{10}$")) {
                mobile = mobile.substring(0, 3) + "****" + mobile.substring(7);
            }
            info.put("mobile", mobile);
        }
        return info;
    }

    /**
     * 获取商户入驻设置
     * @return
     */
    public Map<String, Object> getShopApplyConfig() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.siteId(), "SHOP_APPLY");
        
        Map<String, Object> result = new HashMap<>();
        if (config != null) {
            result.put("apply_title", config.getStr("apply_title", ""));
            result.put("apply_banner", config.getStr("apply_banner", "addon/mall/member/merchant_bg.png"));
            result.put("form_id", config.getStr("form_id", ""));
        } else {
            result.put("apply_title", "");
            result.put("apply_banner", "addon/mall/member/merchant_bg.png");
            result.put("form_id", "");
        }
        return result;
    }

    /**
     * 校验手机号是否存在账号，不存在则创建账号
     */
    private void checkMobileAndCreateAccount(SiteShopApplyParam data) {
        // 创建用户并返回 uid
        Integer uid = coreUserService.createUserByMobile(data.getUserMobile());
        data.setUid(uid);
    }
    
    /**
     * 保存表单数据
     * @param formData 表单数据
     * @return 表单记录 ID
     */
    private Integer saveFormData(Object formData) {
        log.info("【saveFormData】开始保存表单数据，formData 是否为 null: {}", formData == null);
        
        if (formData == null) {
            log.warn("【saveFormData】formData 为 null，直接返回 null");
            return null;
        }
        
        try {
            // 获取表单配置
            ShopApplyConfigInfoVo config = shopConfigService.getShopApplyConfig();
            log.info("【saveFormData】获取表单配置，config: {}, formId: {}", config, config != null ? config.getFormId() : "null");
            
            if (config == null || config.getFormId() == null || config.getFormId() <= 0) {
                log.warn("【saveFormData】表单配置无效，返回 null");
                return null;
            }
            
            // 构建表单记录参数
            DiyFormRecordsParam recordsParam = new DiyFormRecordsParam();
            recordsParam.setFormId(config.getFormId());
            recordsParam.setSiteId(RequestUtils.defaultSiteId());
            recordsParam.setMemberId(RequestUtils.memberId());
            
            // 设置表单数据
            if (formData instanceof JSONArray) {
                log.info("【saveFormData】formData 类型是 JSONArray");
                recordsParam.setValue((JSONArray) formData);
            } else if (formData instanceof String) {
                log.info("【saveFormData】formData 类型是 String");
                recordsParam.setValue(JSONUtil.parseArray((String) formData));
            } else if (formData instanceof Map) {
                log.info("【saveFormData】formData 类型是 Map，转换为 JSONArray");
                // 前端发送的是以数字为键的对象，需要转换为数组
                Map<?, ?> map = (Map<?, ?>) formData;
                List<Object> list = new ArrayList<>();
                int index = 0;
                while (map.containsKey(String.valueOf(index))) {
                    list.add(map.get(String.valueOf(index)));
                    index++;
                }
                recordsParam.setValue(new JSONArray(list));
            } else {
                log.info("【saveFormData】formData 类型是其他，尝试转换");
                recordsParam.setValue(JSONUtil.parseArray(formData));
            }
            
            log.info("【saveFormData】调用 coreDiyFormRecordsService.add() 保存记录");
            // 保存表单记录
            Integer recordId = coreDiyFormRecordsService.add(recordsParam);
            log.info("【saveFormData】保存成功，recordId: {}", recordId);
            return recordId;
        } catch (Exception e) {
            log.error("【saveFormData】保存表单数据异常：{}", e.getMessage(), e);
            // 忽略表单保存异常
            return null;
        }
    }
    
    /**
     * 更新表单数据
     * @param oldRecordId 旧记录ID
     * @param formData 表单数据
     * @return 新记录ID
     */
    private Integer updateFormData(Integer oldRecordId, Object formData) {
        if (formData == null) {
            return null;
        }
        
        try {
            // 获取表单配置
            ShopApplyConfigInfoVo config = shopConfigService.getShopApplyConfig();
            if (config == null || config.getFormId() == null || config.getFormId() <= 0) {
                return null;
            }
            
            // 如果有旧记录，先更新
            if (oldRecordId != null && oldRecordId > 0) {
                try {
                    DiyFormRecordsParam recordsParam = new DiyFormRecordsParam();
                    recordsParam.setRecordId(oldRecordId);
                    recordsParam.setFormId(config.getFormId());
                    recordsParam.setSiteId(RequestUtils.defaultSiteId());
                    recordsParam.setMemberId(RequestUtils.memberId());
                    
                    // 设置表单数据
                    if (formData instanceof JSONArray) {
                        recordsParam.setValue((JSONArray) formData);
                    } else if (formData instanceof String) {
                        recordsParam.setValue(JSONUtil.parseArray((String) formData));
                    } else {
                        // 尝试转换
                        recordsParam.setValue(JSONUtil.parseArray(formData));
                    }
                    
                    coreDiyFormRecordsService.edit(recordsParam);
                    return oldRecordId;
                } catch (Exception e) {
                    // 更新失败，尝试新增
                }
            }
            
            // 新增表单记录
            return saveFormData(formData);
        } catch (Exception e) {
            // 忽略表单更新异常
            return null;
        }
    }
}