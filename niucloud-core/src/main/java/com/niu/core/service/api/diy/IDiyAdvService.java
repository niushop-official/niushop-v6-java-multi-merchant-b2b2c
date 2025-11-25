package com.niu.core.service.api.diy;

import cn.hutool.json.JSONObject;
import com.niu.core.service.api.diy.param.DiyInfoParam;
import com.niu.core.service.api.diy.param.DiyShareParam;
import com.niu.core.service.api.diy.param.DiyTabbarListParam;
import com.niu.core.service.api.diy.param.DiyTabbarParam;
import com.niu.core.service.api.diy.vo.DiyTabbarVo;
import com.niu.core.service.core.diy.vo.BottomConfigVo;

import java.util.List;

public interface IDiyAdvService {

    /**
     * 自定义页面信息
     *
     * @param appKey
     * @return
     */
    Object advInfo(String appKey);

}
