package com.niu.core.service.admin.member.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 会员参数
 */
@Data
public class MemberBatchModifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> memberIds;

    private String field;

    @NotNull(message = "value参数缺失")
    private Object value;

}
