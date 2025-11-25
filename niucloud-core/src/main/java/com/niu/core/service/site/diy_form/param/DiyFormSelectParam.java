package com.niu.core.service.site.diy_form.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DiyFormSelectParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String type;

    private String addon;

    private List<Integer> verifyFormIds;
}
