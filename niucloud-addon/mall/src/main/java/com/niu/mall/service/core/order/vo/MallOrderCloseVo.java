package com.niu.mall.service.core.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MallOrderCloseVo {
    private boolean isClose;
    private int closeLength;
}
