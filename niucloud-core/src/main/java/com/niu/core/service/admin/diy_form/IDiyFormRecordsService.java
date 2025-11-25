package com.niu.core.service.admin.diy_form;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.admin.diy_form.vo.DiyFormFieldsListVo;
import com.niu.core.service.admin.diy_form.vo.DiyFormRecordsListVo;

import java.util.List;

/**
 * 万能表单填写记录服务接口
 */
public interface IDiyFormRecordsService {

    /**
     * 万能表单填写记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<DiyFormRecordsListVo>
     */
    PageResult<DiyFormRecordsListVo> getPage(PageParam pageParam, DiyFormRecordsSearchParam searchParam);

    List<DiyFormFieldsListVo> getFieldStatList(DiyFormRecordsSearchParam searchParam);

}
