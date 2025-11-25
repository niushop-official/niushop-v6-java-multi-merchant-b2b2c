package com.niu.core.service.admin.addon.param;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件参数
*/
@Data
public class AddonSelectParam implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<String> app = new ArrayList<>();
}
