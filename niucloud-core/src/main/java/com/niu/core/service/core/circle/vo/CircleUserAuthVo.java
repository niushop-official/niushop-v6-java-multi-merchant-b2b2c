package com.niu.core.service.core.circle.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CircleUserAuthVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer uid;
    private List<Integer> circleIds;
}
