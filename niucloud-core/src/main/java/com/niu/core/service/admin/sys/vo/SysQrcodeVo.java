package com.niu.core.service.admin.sys.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.sys.AgreementEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 推广二维码视图
 */
@Data
public class SysQrcodeVo{

    /**
     * h5 路劲
     */
    private String h5Path;
    /**
     * 微信小程序 路径
     */
    private String weappPath;
}
