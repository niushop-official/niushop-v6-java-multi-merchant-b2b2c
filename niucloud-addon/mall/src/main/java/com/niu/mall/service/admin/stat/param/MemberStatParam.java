package com.niu.mall.service.admin.stat.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class MemberStatParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String startTime;
    private String endTime;
    private String keyword;
    private String sortField;
}
