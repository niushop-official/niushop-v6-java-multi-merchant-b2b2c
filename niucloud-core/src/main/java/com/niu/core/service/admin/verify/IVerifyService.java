package com.niu.core.service.admin.verify;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.verify.param.VerifyCodeParam;
import com.niu.core.service.admin.verify.param.VerifySearchParam;
import com.niu.core.service.admin.verify.vo.VerifyCodeVo;
import com.niu.core.service.admin.verify.vo.VerifyInfoVo;
import com.niu.core.service.admin.verify.vo.VerifyListVo;

import java.util.List;
import java.util.Map;

/**
 * 核销记录服务接口
 */
public interface IVerifyService {


    /**
     * 核销记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<VerifyListVo>
     */
    PageResult<VerifyListVo> list(PageParam pageParam, VerifySearchParam searchParam);

    /**
     * 核销记录详情
     * @return VerifyInfoVo
     */
    VerifyInfoVo detail(String code);

    boolean verify(String code);

    Map<String, Object> getInfoByCode(String code);

    Map<String, String>  getVerifierTypeList();

    /**
     * 获取核销码
     * @param param 参数
     * @return VerifyCodeVo
     */
    List<VerifyCodeVo> getVerifyCode(VerifyCodeParam  param);
}
