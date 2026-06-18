package com.niu.core.service.core.site_payment.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SitePayment;
import com.niu.core.entity.site.SitePaymentApply;
import com.niu.core.enums.site_payment.SitePaymentApplyStatusEnum;
import com.niu.core.event.site_payment.SitePaymentAuditAfterEvent;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.site.SitePaymentApplyMapper;
import com.niu.core.mapper.site.SitePaymentMapper;
import com.niu.core.service.core.site_payment.ICoreSitePaymenApplyService;
import com.niu.core.service.core.site_payment.param.SitePaymentApplyParam;
import com.niu.core.service.core.site_payment.vo.SitePaymentApplyInfoVo;
import com.niu.core.service.core.site_payment.vo.SitePaymentApplyVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CoreSitePaymenApplyServiceImpl implements ICoreSitePaymenApplyService {

    @Resource
    private SitePaymentApplyMapper sitePaymentApplyMapper;

    @Resource
    private SitePaymentMapper sitePaymentMapper;

    @Resource
    private SiteMapper siteMapper;


    @Override
    public PageResult<SitePaymentApplyVo> getPage(PageParam pageParam, SitePaymentApplyParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        // 2. 查询条件
        LambdaQueryWrapper<SitePaymentApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(SitePaymentApply::getSiteId, 0);   // site_id > 0

        // 动态条件：type
        if (!param.getType().isEmpty()) {
            wrapper.eq(SitePaymentApply::getType, param.getType());
        }

        // 动态条件：create_time（假设传入 start_time 和 end_time 字符串）
        if (param.getCreateTime() != null && param.getCreateTime().size() == 2) {
            wrapper.between(SitePaymentApply::getCreateTime, param.getCreateTime().get(0), param.getCreateTime().get(1));
        }

        // 3. 排序
        wrapper.orderByDesc(SitePaymentApply::getId);

        // 4. 执行分页查询（不自动联表）
        IPage<SitePaymentApply> iPage = sitePaymentApplyMapper.selectPage(new Page<>(page, limit), wrapper);
        List<SitePaymentApplyVo> list = BeanUtil.copyToList(iPage.getRecords(), SitePaymentApplyVo.class);
        for (SitePaymentApplyVo vo : list) {
            Site site = siteMapper.selectById(vo.getSiteId());
            vo.setSite(site);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public SitePaymentApplyInfoVo getInfo(Integer siteId, String type) {
        if (type == null || type.isEmpty()) {
            type = "huifu";
        }

        // 查询站点支付申请信息
        LambdaQueryWrapper<SitePaymentApply> applyQueryWrapper = new LambdaQueryWrapper<>();
        applyQueryWrapper.eq(SitePaymentApply::getSiteId, siteId)
                .eq(SitePaymentApply::getType, type)
                .last("limit 1");

        SitePaymentApply sitePaymentApply = sitePaymentApplyMapper.selectOne(applyQueryWrapper);

        if (sitePaymentApply == null) {
            return new SitePaymentApplyInfoVo();
        }

        // 转换为 VO
        SitePaymentApplyInfoVo vo = BeanUtil.copyProperties(sitePaymentApply, SitePaymentApplyInfoVo.class);

        // 如果有 extend_no，查询关联的支付信息
        if (sitePaymentApply.getExtendNo() != null && !sitePaymentApply.getExtendNo().isEmpty()) {
            QueryWrapper<SitePayment> paymentQueryWrapper = new QueryWrapper<>();
            paymentQueryWrapper.eq("extend_no", sitePaymentApply.getExtendNo())
                    .last("limit 1");

            SitePayment sitePayment = sitePaymentMapper.selectOne(paymentQueryWrapper);

            if (sitePayment != null) {
                SitePaymentApplyInfoVo.SitePaymentVo paymentVo = new SitePaymentApplyInfoVo.SitePaymentVo();
                paymentVo.setStatus(sitePayment.getStatus());
                paymentVo.setExtendNo(sitePayment.getExtendNo());
                paymentVo.setIsSettle(sitePayment.getIsSettle());
                paymentVo.setSettleDesc(sitePayment.getSettleDesc());
                vo.setSitePayment(paymentVo);
            }
        }

        return vo;
    }

    @Override
    public SitePaymentApplyVo getDetail(SitePaymentApplyParam param) {
        SitePaymentApply apply = sitePaymentApplyMapper.selectById(param.getId());
        SitePaymentApplyVo vo = BeanUtil.copyProperties(apply, SitePaymentApplyVo.class);
        vo.setSite(siteMapper.selectById(vo.getSiteId()));
        return vo;
    }

    @Override
    public Boolean edit(Long id, Integer siteId, Map<String, Object> data) {
        LambdaUpdateWrapper<SitePaymentApply> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SitePaymentApply::getId, id)
                .eq(SitePaymentApply::getSiteId, siteId);

        SitePaymentApply entity = BeanUtil.mapToBean(data, SitePaymentApply.class, false);
        int result = sitePaymentApplyMapper.update(entity, updateWrapper);

        if (data.containsKey("status") &&
                SitePaymentApplyStatusEnum.WAIT_AUDIT.getStatus().equals(data.get("status"))) {
            toAudit(id, siteId);
        }

        return result > 0;
    }

    @Override
    public Long add(Map<String, Object> data, Integer siteId, String type) {
        // 设置 site_id 和 type
        data.put("siteId", siteId);
        data.put("type", type);

        // 将 Map 转换为实体对象
        SitePaymentApply entity = BeanUtil.mapToBean(data, SitePaymentApply.class, false);

        // 插入数据
        sitePaymentApplyMapper.insert(entity);

        // 返回插入后的 ID
        return entity.getId();
    }

    @Override
    public Boolean agree(Integer siteId, Long id, String extendNo) {
        // 查询申请记录
        LambdaQueryWrapper<SitePaymentApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SitePaymentApply::getId, id)
                .eq(SitePaymentApply::getSiteId, siteId);
        SitePaymentApply sitePayment = sitePaymentApplyMapper.selectOne(queryWrapper);

        if (sitePayment == null) {
            throw new RuntimeException("找不到可用的进件记录");
        }

        // 更新审核通过状态
        sitePayment.setAuditTime((int) (System.currentTimeMillis() / 1000));
        sitePayment.setExtendNo(extendNo != null ? extendNo : "");
        sitePayment.setStatus(SitePaymentApplyStatusEnum.AUDIT_PASS.getStatus());

        int result = sitePaymentApplyMapper.updateById(sitePayment);

        return result > 0;
    }

    /**
     * 提交审核
     *
     * @param id     主键ID
     * @param siteId 站点ID
     * @return 是否成功
     */
    private Boolean toAudit(Long id, Integer siteId) {
        // 更新状态为待审核，并设置申请时间
        LambdaUpdateWrapper<SitePaymentApply> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SitePaymentApply::getId, id)
                .eq(SitePaymentApply::getSiteId, siteId)
                .set(SitePaymentApply::getStatus, SitePaymentApplyStatusEnum.WAIT_AUDIT.getStatus())
                .set(SitePaymentApply::getApplyTime, (int) (System.currentTimeMillis() / 1000));

        int result = sitePaymentApplyMapper.update(null, updateWrapper);
        if (result > 0) {
            LambdaQueryWrapper<SitePaymentApply> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SitePaymentApply::getId, id)
                    .eq(SitePaymentApply::getSiteId, siteId);
            SitePaymentApply sitePaymentApply = sitePaymentApplyMapper.selectOne(queryWrapper);

            if (sitePaymentApply != null) {
                // 构建事件对象
                SitePaymentAuditAfterEvent event = new SitePaymentAuditAfterEvent(sitePaymentApply);
            }
        }

        return result > 0;
    }
}
