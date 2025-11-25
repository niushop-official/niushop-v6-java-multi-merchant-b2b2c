package com.niu.core.service.site.diy;


import com.niu.core.service.admin.diy.param.SetBottomConfigParam;
import com.niu.core.service.core.diy.vo.BottomConfigVo;

import java.util.List;

/**
 * 自定义配置接口
 */
public interface IDiyConfigService {

    List<BottomConfigVo> getBottomList();

    BottomConfigVo getBottomConfig(String key);

    void setBottomConfig(SetBottomConfigParam param);

}
