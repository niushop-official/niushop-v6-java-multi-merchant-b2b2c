package com.niu.core.service.admin.member.param;

import lombok.Data;
import java.io.Serializable;

@Data
public class MemberImportSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer status;
    private String startTime;
    private String endTime;
}
