package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysAreaResolveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    private String area;
}
