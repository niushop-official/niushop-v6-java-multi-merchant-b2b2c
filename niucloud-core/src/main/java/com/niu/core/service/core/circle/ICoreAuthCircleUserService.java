package com.niu.core.service.core.circle;

import com.niu.core.service.core.circle.vo.CircleUserAuthVo;

/**
 * 商圈管理员鉴权服务
 */
public interface ICoreAuthCircleUserService {

    /**
     * 检测当前用户是否为商圈管理员
     */
    CircleUserAuthVo checkCircleUserAccount();

    /**
     * 获取当前用户默认商圈ID（首个启用的商圈）
     */
    Integer getCircleId();
}
