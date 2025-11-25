package com.niu.core.service.admin.sys.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysUpgradeRecordsDelParam {

    /**
     * 删除ID号
     */
    @NotNull(message = "删除ID号不能为空")
    private Object ids;

}
