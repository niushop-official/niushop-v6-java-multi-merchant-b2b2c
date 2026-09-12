package com.niu.core.service.core.verify;

import com.niu.core.service.core.verify.vo.CoreVerifyCreateCodeVo;

import java.util.List;
import java.util.Map;

/**
 * 核销core服务层
 */
public interface ICoreVerifyService {

    /**
     * 核销(核销api调用)
     * @return true
     */

    Boolean adminVerify(Integer siteId, Integer uid, String code);

    Map<String, Object> adminGetInfoByCode(Integer siteId, String code);

    Map<String, Object> getInfoByCode(String code, Integer integer);

    Boolean verify(Integer memberId, String code);

    List<CoreVerifyCreateCodeVo> create(Integer siteId, Integer memberId, String type, Map<String, Object> data);
}

