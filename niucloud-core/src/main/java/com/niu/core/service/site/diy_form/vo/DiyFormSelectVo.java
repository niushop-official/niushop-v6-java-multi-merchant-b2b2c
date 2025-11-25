package com.niu.core.service.site.diy_form.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DiyFormSelectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<DiyFormListVo> list;

    private List<Integer> verifyFormIds;
}
