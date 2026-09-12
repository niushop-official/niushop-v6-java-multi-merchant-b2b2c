package com.niu.core.service.admin.verify.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.verify.Verify;
import com.niu.core.enums.sys.VerifierTypeEnum;
import com.niu.core.event.verify.GetVerifyInfoByTradeEventDefiner;
import com.niu.core.event.verify.VerifierCheckEventDefiner;
import com.niu.core.mapper.verify.VerifyMapper;
import com.niu.core.service.admin.verify.IVerifyService;
import com.niu.core.service.admin.verify.param.VerifyCodeParam;
import com.niu.core.service.admin.verify.param.VerifySearchParam;
import com.niu.core.service.admin.verify.vo.VerifyCodeVo;
import com.niu.core.service.admin.verify.vo.VerifyInfoVo;
import com.niu.core.service.admin.verify.vo.VerifyListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.verify.ICoreVerifyService;
import com.niu.core.service.core.verify.vo.CoreVerifyCreateCodeVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 核销记录实现
 */
@Service
public class VerifyServiceImpl implements IVerifyService {

    @Resource
    VerifyMapper verifyMapper;

    @Resource
    ICoreVerifyService coreVerifyService;
    /**
     * 核销记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<VerifyListVo>
     */
    @Override
    public PageResult<VerifyListVo> list(PageParam pageParam, VerifySearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<Verify> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getCode())) queryWrapper.eq("code", searchParam.getCode());
        if (ObjectUtil.isNotEmpty(searchParam.getType()) && !"all".equals(searchParam.getType())) queryWrapper.eq("type", searchParam.getType());
        if (ObjectUtil.isNotEmpty(searchParam.getVerifierType()) && !"all".equals(searchParam.getVerifierType()))
            queryWrapper.eq("verifier_type", searchParam.getVerifierType());
        if (ObjectUtil.isNotEmpty(searchParam.getVerifierMemberId()))
            queryWrapper.eq("verifier_member_id", searchParam.getVerifierMemberId());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime()))
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());

        IPage<Verify> iPage = verifyMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<VerifyListVo> list = new LinkedList<>();
        for (Verify item : iPage.getRecords()) {
            VerifyListVo vo = new VerifyListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public VerifyInfoVo detail(String code) {
        QueryWrapper<Verify> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("code", code);

        Verify model = verifyMapper.selectOne(queryWrapper);
        Assert.notNull(model, "数据不存在");

        VerifyInfoVo vo = new VerifyInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    @Override
    public boolean verify(String code) {
     return    coreVerifyService.adminVerify(RequestUtils.siteId(), RequestUtils.uid(), code);
    }

    @Override
    public  Map<String, Object> getInfoByCode(String code) {
        return coreVerifyService.adminGetInfoByCode(RequestUtils.siteId(),code);
    }

    @Override
    public Map<String, String> getVerifierTypeList() {
        return VerifierTypeEnum.getMap();
    }

    @Override
    public List<VerifyCodeVo> getVerifyCode(VerifyCodeParam param) {
        List<VerifyCodeVo> verifyCodeVoList = new LinkedList<>();
        // 触发核销员检查事件
        GetVerifyInfoByTradeEventDefiner.GetVerifyInfoByTradeEvent verifierEvent = new GetVerifyInfoByTradeEventDefiner.GetVerifyInfoByTradeEvent();
        verifierEvent.setTradeId(param.getTradeId());
        verifierEvent.setTradeType(param.getTradeType());
        verifierEvent.setSiteId(RequestUtils.siteId());
        List<GetVerifyInfoByTradeEventDefiner.GetVerifyInfoByTradeResult> verifierResults = EventAndSubscribeOfPublisher.publishAndCallback(verifierEvent);
        Integer memberId = null;
        String type = "";
        Map<String, Object> data = null;
        if (!verifierResults.isEmpty()) {
            for( GetVerifyInfoByTradeEventDefiner.GetVerifyInfoByTradeResult result : verifierResults){
                if (ObjectUtil.isNotEmpty(result)){
                    if (result.getMemberId() != null){
                        memberId = result.getMemberId();
                    }
                    if (ObjectUtil.isNotEmpty(result.getType())){
                        type = result.getType();
                    }
                    if (ObjectUtil.isNotEmpty(result.getData())){
                        data = result.getData();
                    }
                }

            }
        }
        if(ObjectUtil.isNotEmpty(data) && ObjectUtil.isNotEmpty(memberId) && ObjectUtil.isNotEmpty(type)){
            List<CoreVerifyCreateCodeVo> verifyCodeList = coreVerifyService.create(RequestUtils.siteId(), memberId, type, data);
            if (ObjectUtil.isNotEmpty(verifyCodeList)){
                for (CoreVerifyCreateCodeVo verifyCodeItem : verifyCodeList){
                    VerifyCodeVo verifyCodeVo = new VerifyCodeVo();
                    verifyCodeVo.setCode(verifyCodeItem.getCode());
                    verifyCodeVo.setQrcode(QrcodeUtils.qrcode(RequestUtils.siteId(), "h5", verifyCodeItem.getCode(), "", null));
                    verifyCodeVoList.add(verifyCodeVo);
                }
            }
        }else{
            throw new CommonException("核销信息不存在");
        }
        return verifyCodeVoList;
    }


}
