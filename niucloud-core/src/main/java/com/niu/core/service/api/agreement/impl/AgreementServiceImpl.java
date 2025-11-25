package com.niu.core.service.api.agreement.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.sys.SysAgreement;
import com.niu.core.enums.sys.AgreementEnum;
import com.niu.core.mapper.sys.SysAgreementMapper;
import com.niu.core.service.api.agreement.IAgreementService;
import com.niu.core.service.api.agreement.param.AgreementInfoParam;
import com.niu.core.service.api.agreement.vo.AgreementInfoVo;
import com.niu.core.service.core.sys.ICoreAgreementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AgreementServiceImpl implements IAgreementService {
    @Resource
    private ICoreAgreementService coreAgreementService;

    @Override
    public AgreementInfoVo info(AgreementInfoParam param) {
        SysAgreement agreement = coreAgreementService.getAgreement(RequestUtils.defaultSiteId(), param.getKey());

        AgreementInfoVo agreementInfoVo = new AgreementInfoVo();
        if (agreement != null) {
            BeanUtils.copyProperties(agreement, agreementInfoVo);
        } else {
            agreementInfoVo.setSiteId(RequestUtils.defaultSiteId());
            agreementInfoVo.setAgreementKey(param.getKey());
            agreementInfoVo.setTitle("");
            agreementInfoVo.setContent("");
        }
        
        return agreementInfoVo;
    }

}
