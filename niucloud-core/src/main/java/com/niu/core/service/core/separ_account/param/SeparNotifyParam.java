package com.niu.core.service.core.separ_account.param;

import com.niu.core.enums.account.SeparStatusEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class SeparNotifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private SeparStatusEnum status;

    private String failReason;
}
