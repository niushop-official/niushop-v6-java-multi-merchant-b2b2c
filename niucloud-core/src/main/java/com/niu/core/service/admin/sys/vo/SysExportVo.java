package com.niu.core.service.admin.sys.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.sys.AgreementEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * SysArea视图
 */
@Data
public class SysExportVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> params;
}
