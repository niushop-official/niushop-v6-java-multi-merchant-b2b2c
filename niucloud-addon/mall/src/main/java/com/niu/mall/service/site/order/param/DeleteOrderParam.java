package com.niu.mall.service.site.order.param;

import lombok.Data;

import java.util.List;

@Data
public class DeleteOrderParam {

    private List<Integer> orderIds;
}
