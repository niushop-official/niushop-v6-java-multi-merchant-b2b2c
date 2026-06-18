package com.niu.core.service.core.site_payment.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SitePayment;
import com.niu.core.entity.site.SitePaymentApply;
import com.niu.core.enums.sys.SeparTypeEnum;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.site.SitePaymentApplyMapper;
import com.niu.core.mapper.site.SitePaymentMapper;
import com.niu.core.service.admin.shop.param.SitePaymentAEParam;
import com.niu.core.service.admin.shop.param.SitePaymentSearchParam;
import com.niu.core.service.core.site_payment.ICoreSitePaymentService;
import com.niu.core.service.core.site_payment.vo.SitePaymentListVo;
import com.niu.core.service.core.site_payment.vo.SitePaymentVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoreSitePaymentServiceImpl implements ICoreSitePaymentService {

    @Resource
    private SitePaymentMapper sitePaymentMapper;
    @Resource
    private SitePaymentApplyMapper sitePaymentApplyMapper;
    @Resource
    private SiteMapper siteMapper;

    @Override
    public JSONArray getSpearPaymentType(Integer siteId) {
        return SeparTypeEnum.getTypeBySiteId(siteId);
    }

    @Override
    public JSONArray getSiteSpearPaymentTypeListByType(Integer siteId) {
        JSONArray paymentTypes = getSpearPaymentType(siteId);
        if (!paymentTypes.isEmpty()) {
            for (int i = 0; i < paymentTypes.size(); i++) {
                JSONObject jsonObject = paymentTypes.getJSONObject(i);
                SitePayment sitePayment = sitePaymentMapper.selectOne(new QueryWrapper<SitePayment>().eq("site_id", siteId).eq("type", jsonObject.getStr("key")).last("limit 1"));
                SitePaymentApply sitePaymentApply = sitePaymentApplyMapper.selectOne(new QueryWrapper<SitePaymentApply>().eq("site_id", siteId).eq("type", jsonObject.getStr("key")).last("limit 1"));
//                sitePayment = sitePayment == null ? new SitePayment() : sitePayment;
//                sitePaymentApply = sitePaymentApply == null ? new SitePaymentApply() : sitePaymentApply;
                if (sitePayment == null) paymentTypes.getJSONObject(i).set("site_payment", new ArrayList<>());
                else paymentTypes.getJSONObject(i).set("site_payment", JSONUtil.parseObj(sitePayment));
                if (sitePaymentApply == null)
                    paymentTypes.getJSONObject(i).set("site_payment_apply", new ArrayList<>());
                else paymentTypes.getJSONObject(i).set("site_payment_apply", JSONUtil.parseObj(sitePaymentApply));
            }
        }
        return paymentTypes;
    }

    @Override
    public List<SitePaymentListVo> getList(Integer isSettle) {
        QueryWrapper<SitePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_settle", isSettle);
        queryWrapper.orderByDesc("id");
        return CollectUtils.convert(sitePaymentMapper.selectList(queryWrapper), SitePaymentListVo.class);
    }

    @Override
    public Map<String, Object> getInfo(Integer siteId, String type) {
        if (type == null || type.isEmpty()) {
            type = "huifu";
        }

        QueryWrapper<SitePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId)
                .eq("type", type)
                .last("limit 1");

        SitePayment sitePayment = sitePaymentMapper.selectOne(queryWrapper);

        if (sitePayment == null) {
            return new HashMap<>();
        }

        return BeanUtil.beanToMap(sitePayment);
    }

    @Override
    public Long add(Map<String, Object> data, Integer siteId, String type) {
        // 设置 site_id 和 type
        data.put("siteId", siteId);
        data.put("type", type);

        // 将 Map 转换为实体对象
        SitePayment entity = BeanUtil.mapToBean(data, SitePayment.class, false);

        // 插入数据
        sitePaymentMapper.insert(entity);

        // 返回插入后的 ID
        return entity.getId();
    }

    @Override
    public Boolean edit(Long id, Integer siteId, Map<String, Object> data) {
        // 构建更新条件
        QueryWrapper<SitePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                .eq("site_id", siteId);

        // 将 Map 转换为实体对象
        SitePayment entity = BeanUtil.mapToBean(data, SitePayment.class, false);

        int result = sitePaymentMapper.update(entity, queryWrapper);

        return result > 0;
    }

    @Override
    public PageResult<SitePaymentVo> getPage(PageParam pageParam, SitePaymentSearchParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<SitePayment> wrapper = new MPJQueryWrapper<>();
        wrapper.select("site_id,id,status,infos,type,create_time,extend_no,is_settle,withdrawal_no,settle_desc");
        wrapper.gt("site_id", 0);   // site_id > 0
        if (!param.getType().isBlank()) {
            wrapper.eq("type", param.getType());
        }
        if (!param.getStatus().isBlank()) {
            wrapper.eq("status", param.getStatus());
        }
        if (param.getIsSettle() != null) {
            wrapper.eq("is_settle", param.getIsSettle());
        }
        if (param.getCreateTime() != null && param.getCreateTime().size() == 2) {
            wrapper.between("create_time", param.getCreateTime().get(0), param.getCreateTime().get(1));
        }
        wrapper.orderByDesc("create_time");
        IPage<SitePayment> iPage = sitePaymentMapper.selectPage(new Page<>(page, limit), wrapper);

        List<SitePaymentVo> list = BeanUtil.copyToList(iPage.getRecords(), SitePaymentVo.class);
        for (SitePaymentVo vo : list) {
            Site site = siteMapper.selectById(vo.getSiteId());
            vo.setSite(site);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public SitePaymentVo getDetail(Integer id) {
        SitePayment payment = sitePaymentMapper.selectById(id);
        SitePaymentVo vo = BeanUtil.copyProperties(payment, SitePaymentVo.class);
        vo.setSite(siteMapper.selectById(vo.getSiteId()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addByHandle(SitePaymentAEParam param) {
        boolean isCover = param.getIsCover() != null ? param.getIsCover() : false;

        // 2. 校验站点是否存在
        Site siteInfo = siteMapper.selectById(param.getSiteId());
        if (siteInfo == null) {
            throw new CommonException("店铺不存在");
        }

        // 3. 查询是否已存在相同 site_id 和 type 的记录
        LambdaQueryWrapper<SitePayment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SitePayment::getSiteId, param.getSiteId())
                .eq(SitePayment::getType, param.getType());
        SitePayment existing = sitePaymentMapper.selectOne(wrapper);

        if (existing != null) {
            if (isCover) {
                // 覆盖：删除原有记录
                sitePaymentMapper.deleteById(existing.getId());
            } else {
                // 不覆盖：返回需要确认覆盖的标志
                Map<String, Object> result = new HashMap<>();
                result.put("is_sure_cover", true);
                return result;
            }
        }

        // 4. 构建新记录
        SitePayment sitePayment = new SitePayment();
        sitePayment.setSiteId(param.getSiteId());
        sitePayment.setType(param.getType());
        sitePayment.setIsSettle(2);   // 对应 PHP 中的 is_settle = 2
        sitePayment.setExtendNo(param.getExtendNo());
        sitePayment.setInfos("{}");   // PHP 中空数组转为 JSON 字符串
        sitePayment.setStatus("on");
        sitePayment.setSettleDesc("成功");
        sitePayment.setCreateTime(Math.toIntExact(System.currentTimeMillis() / 1000));

        // 可根据业务需要从 data 中设置其他字段
        // sitePayment.setSomeField(data.get("some_key").toString());

        sitePaymentMapper.insert(sitePayment);

        // 5. 返回空 Map（对应 PHP 的空数组）
        return new HashMap<>();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> editByHandle(SitePaymentAEParam param) {
        SitePayment payment = sitePaymentMapper.selectOne(new LambdaQueryWrapper<SitePayment>()
                .eq(SitePayment::getId, param.getId()));
        if (payment == null) {
            throw new CommonException("当前用户编号已被其它商户绑定");
        }
        payment.setExtendNo(param.getExtendNo() != null ? param.getExtendNo() : payment.getExtendNo());
        sitePaymentMapper.updateById(payment);
        return new HashMap<>();
    }
}
