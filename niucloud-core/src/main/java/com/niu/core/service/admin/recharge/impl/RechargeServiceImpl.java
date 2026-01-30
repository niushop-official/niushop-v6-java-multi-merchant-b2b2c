package com.niu.core.service.admin.recharge.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.enums.recharge.PackageGiftEnum;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.mapper.recharge.RechargePackageMapper;
import com.niu.core.entity.recharge.RechargePackage;
import com.niu.core.service.admin.recharge.IRechargeService;
import com.niu.core.service.admin.recharge.param.RechargeEditSortParam;
import com.niu.core.service.admin.recharge.param.RechargeModifyStatusParam;
import com.niu.core.service.admin.recharge.param.RechargePageParam;
import com.niu.core.service.admin.recharge.param.RechargeParam;
import com.niu.core.service.admin.recharge.vo.RechargeInitVo;
import com.niu.core.service.admin.recharge.vo.RechargeListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.recharge.order.vo.RechargeGiftContentVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class RechargeServiceImpl implements IRechargeService {

    @Resource
    private RechargePackageMapper rechargePackageMapper;

    @Override
    public PageResult<RechargeListVo> getPage(PageParam pageParam, RechargePageParam param) {
        QueryWrapper<RechargePackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(param.getPackageName())) {
            queryWrapper.like("package_name", param.getPackageName());
        }

        if (ObjectUtil.isNotEmpty(param.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", param.getCreateTime());
        }

        Page<RechargePackage> iPage = rechargePackageMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper);
        if (ObjectUtil.isEmpty(iPage.getRecords())) {
            return PageResult.build(iPage, Collections.emptyList());
        }

        IPage<RechargeListVo> voPage = iPage.convert(recharge -> {
            RechargeListVo vo = new RechargeListVo();
            BeanUtils.copyProperties(recharge, vo);
            vo.setGiftContent(new ArrayList<>());
            JSONObject gift = StringUtils.isNotEmpty(recharge.getGiftJson()) ? JSONUtil.parseObj(recharge.getGiftJson()) : new JSONObject();
            for(Map.Entry<String, Object> entry : gift.entrySet()) {
                CommonEventDefiner.CommonEvent event = new CommonEventDefiner.CommonEvent();
                event.setEventName("RechargeGiftContent");
                event.setData(entry);
                event.setSiteId(RequestUtils.siteId());
                List<CommonEventDefiner.CommonEventResult> giftContent = EventAndSubscribeOfPublisher.publishAndCallback(event);
                for (CommonEventDefiner.CommonEventResult content : giftContent) {
                    if(ObjectUtil.isNotEmpty(content.getResult())) {
                        RechargeGiftContentVo contentVo = new RechargeGiftContentVo();
                        BeanUtils.copyProperties(content.getResult(), contentVo);
                        vo.getGiftContent().add(contentVo);
                    }
                }
            }

            return vo;
        });

        return PageResult.build(voPage);
    }

    @Override
    public void add(RechargeParam param) {
        QueryWrapper<RechargePackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("price", param.getPrice());
        long count = rechargePackageMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("充值金额不能重复");
        }

        int growth = param.getPoint();
        int point = param.getPoint();
        JSONObject giftJson = ObjectUtil.isNotEmpty(param.getGiftJson()) ? JSONUtil.parseObj(param.getGiftJson()) : new JSONObject();

        if (giftJson.containsKey("growth")) {
            Integer value = giftJson.getJSONObject("growth").getInt("value");
            if (ObjectUtil.isNotEmpty(value)) {
                growth = value;
                giftJson.remove("growth");
            }
        }

        if (giftJson.containsKey("point")) {
            Integer value = giftJson.getJSONObject("point").getInt("value");
            if (ObjectUtil.isNotEmpty(point)) {
                point = value;
                giftJson.remove("point");
            }
        }


        RechargePackage recharge = new RechargePackage();
        BeanUtils.copyProperties(param, recharge);
        recharge.setPoint(point);
        recharge.setGrowth(growth);
        recharge.setCreateTime(System.currentTimeMillis() / 1000);
        //recharge.setGiftJson(giftJson.toString());

        rechargePackageMapper.insert(recharge);
    }

    @Override
    public void edit(Integer id, RechargeParam param) {
        RechargePackage recharge = rechargePackageMapper.selectById(id);
        if (ObjectUtil.isEmpty(recharge)) {
            throw new RuntimeException("充值套餐不存在");
        }

        QueryWrapper<RechargePackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("price", param.getPrice()).ne("package_id", id);
        Long count = rechargePackageMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("充值金额不能重复");
        }

        int growth = param.getPoint();
        int point = param.getPoint();
        JSONObject giftJson = ObjectUtil.isNotEmpty(param.getGiftJson()) ? JSONUtil.parseObj(param.getGiftJson()) : new JSONObject();

        if (giftJson.containsKey("growth")) {
            Integer value = giftJson.getJSONObject("growth").getInt("value");
            if (ObjectUtil.isNotEmpty(value)) {
                growth = value;
                giftJson.remove("growth");
            }
        }

        if (giftJson.containsKey("point")) {
            Integer value = giftJson.getJSONObject("point").getInt("value");
            if (ObjectUtil.isNotEmpty(point)) {
                point = value;
                giftJson.remove("point");
            }
        }

        BeanUtils.copyProperties(param, recharge);
        recharge.setGrowth(growth);
        recharge.setPoint(point);
        //recharge.setGiftJson(giftJson.toString());
        recharge.setUpdateTime(System.currentTimeMillis() / 1000);
        rechargePackageMapper.updateById(recharge);
    }

    @Override
    public RechargeInitVo getInit(Integer packageId) {
        packageId = ObjectUtil.isEmpty(packageId) ? 0 : packageId;
        RechargePackage recharge = rechargePackageMapper.selectById(packageId);
        if (recharge == null) {
            return new RechargeInitVo();
        }
        RechargeInitVo vo = new RechargeInitVo();
        BeanUtils.copyProperties(recharge, vo);

        JSONObject giftJson = ObjectUtil.isNotEmpty(recharge.getGiftJson()) ? JSONUtil.parseObj(recharge.getGiftJson()) : new JSONObject();

        if (ObjectUtil.isNotEmpty(vo)) {
            if (ObjectUtil.isNotEmpty(vo.getPoint())) {
                giftJson.set("point", Map.of("value", vo.getPoint()));
            }
            if (ObjectUtil.isNotEmpty(vo.getGrowth())) {
                giftJson.set("growth", Map.of("value", vo.getGrowth()));
            }
        }
        vo.setGiftJson(JSONUtil.toJsonStr(giftJson));
        return vo;
    }

    @Override
    public void del(Integer id) {
        RechargePackage recharge = rechargePackageMapper.selectById(id);
        if (ObjectUtil.isEmpty(recharge)) {
            throw new RuntimeException("充值套餐不存在");
        }
        rechargePackageMapper.deleteById(id);
    }

    @Override
    public void modifyStatus(RechargeModifyStatusParam param) {
        RechargePackage recharge = rechargePackageMapper.selectById(param.getPackageId());
        if (ObjectUtil.isEmpty(recharge)) {
            throw new RuntimeException("充值套餐不存在");
        }

        recharge.setStatus(param.getStatus());
        rechargePackageMapper.updateById(recharge);
    }

    @Override
    public void editSort(RechargeEditSortParam param) {
        RechargePackage recharge = rechargePackageMapper.selectById(param.getRechargeId());
        if (ObjectUtil.isEmpty(recharge)) {
            throw new RuntimeException("充值套餐不存在");
        }
        recharge.setSort(param.getSort());
        rechargePackageMapper.updateById(recharge);
    }

    @Override
    public JSONArray getPackageGiftDict() {
        return PackageGiftEnum.getPackageGiftEnum();
    }

}
