package com.niu.core.service.admin.vip_card.card;


import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.vip_card.card.param.AdminBenefitsListParam;
import com.niu.core.service.admin.vip_card.card.param.AdminModifySortParam;
import com.niu.core.service.admin.vip_card.card.param.AdminModifyStatusParam;
import com.niu.core.service.admin.vip_card.card.param.AdminSetConfigParam;
import com.niu.core.service.admin.vip_card.card.vo.AdminBenefitsConfigVo;
import com.niu.core.service.admin.vip_card.card.vo.AdminBenefitsListVo;

import java.util.List;
import java.util.Map;

public interface IAdminVipCardBenefitsService {

    List<AdminBenefitsListVo> getList(AdminBenefitsListParam param);

    AdminBenefitsConfigVo getConfig(String type);

    void setConfig(String type, AdminSetConfigParam param);

    void modifyStatus(AdminModifyStatusParam param);

    void modifySort(AdminModifySortParam param);

    Map<String, Map<String, Object>> getBenefitsDict();

    Map<String, Object> getBenefitsContent(Integer siteId, JSONObject benefits, String scene);

    PageResult<AdminBenefitsListVo> getPage(PageParam pageParam, AdminBenefitsListParam param);
}
